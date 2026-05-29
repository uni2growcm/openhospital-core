/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.accounting.manager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.isf.accounting.model.*;
import org.isf.therapy.manager.TherapyManager;
import org.isf.lab.manager.LabManager;
import org.isf.operation.manager.OperationRowBrowserManager;
import org.isf.accounting.service.AccountingIoOperations;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.menu.manager.Context;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.menu.model.User;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.menu.manager.Context;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.model.ItemGroup;
import org.isf.priceslist.model.Price;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.io.IOException;

@Component
public class BillBrowserManager {

	private final AccountingIoOperations ioOperations;
	private final MovWardBrowserManager mvtManager;
	private final PriceListManager priceListManager;
	private final MedicalBrowsingManager medicalBrowsingManager;
	private final MovStockInsertingManager movStockInsertingManager;
	private final TherapyManager therapyManager;
	private final LabManager labManager;
	private final OperationRowBrowserManager operationRowManager;

	public BillBrowserManager(
		AccountingIoOperations ioOperations, MovWardBrowserManager mvtManager,
		PriceListManager priceListManager, MedicalBrowsingManager medicalBrowsingManager,
	    MovStockInsertingManager movStockInsertingManager,  TherapyManager therapyManager,
	    LabManager labManager, OperationRowBrowserManager operationRowManager
	) {
		this.ioOperations = ioOperations;
		this.mvtManager = mvtManager;
		this.priceListManager = priceListManager;
		this.medicalBrowsingManager = medicalBrowsingManager;
		this.movStockInsertingManager = movStockInsertingManager;
		this.therapyManager = therapyManager;
		this.labManager = labManager;
		this.operationRowManager = operationRowManager;
	}

	/**
	 * Returns a list of items that were removed or reduced in quantity.
	 * For reduced items, the quantity will be the difference.
	 */
	private List<BillItems> getDeletedItems(int billID, List<BillItems> updatedItems) throws OHServiceException {
		List<BillItems> oldItems = this.ioOperations.getItems(billID);
		if (oldItems == null || oldItems.isEmpty()) return new ArrayList<>();

		if (updatedItems == null) updatedItems = new ArrayList<>();

		Map<Integer, BillItems> newItemsMap = updatedItems.stream()
			.filter(item -> item.getId() > 0)
			.collect(Collectors.toMap(BillItems::getId, item -> item));

		List<BillItems> removedOrReduced = new ArrayList<>();

		for (BillItems oldItem : oldItems) {
			BillItems newItem = newItemsMap.get(oldItem.getId());
			if (newItem == null) {
				removedOrReduced.add(oldItem);
			} else if (oldItem.getItemQuantity() > newItem.getItemQuantity()) {
				int diff = oldItem.getItemQuantity() - newItem.getItemQuantity();
				BillItems reducedItem = new BillItems(oldItem.getId(), oldItem.getBill(), oldItem.isPrice(),
					oldItem.getPriceID(), oldItem.getItemDescription(), oldItem.getItemAmount(), diff);
				reducedItem.setItemId(oldItem.getItemId());
				reducedItem.setItemDisplayCode(oldItem.getItemDisplayCode());
				removedOrReduced.add(reducedItem);
			}
		}

		return removedOrReduced;
	}

	/**
	 * Returns a list of items that are newly added or have increased quantity.
	 */
	private List<BillItems> getNewItems(int billID, List<BillItems> updatedItems) throws OHServiceException {
		List<BillItems> oldItems = this.ioOperations.getItems(billID);
		if (updatedItems == null || updatedItems.isEmpty()) return new ArrayList<>();

		Map<Integer, BillItems> oldItemsMap = oldItems != null ? oldItems.stream()
			.filter(item -> item.getId() > 0)
			.collect(Collectors.toMap(BillItems::getId, item -> item)) : new HashMap<>();

		List<BillItems> addedOrIncreased = new ArrayList<>();

		for (BillItems updatedItem : updatedItems) {
			if (updatedItem.getId() == 0) {
				addedOrIncreased.add(updatedItem);
			} else {
				BillItems oldItem = oldItemsMap.get(updatedItem.getId());
				if (oldItem != null && updatedItem.getItemQuantity() > oldItem.getItemQuantity()) {
					int diff = updatedItem.getItemQuantity() - oldItem.getItemQuantity();
					BillItems increasedItem = new BillItems(updatedItem.getId(), updatedItem.getBill(), updatedItem.isPrice(),
						updatedItem.getPriceID(), updatedItem.getItemDescription(), updatedItem.getItemAmount(), diff);
					increasedItem.setItemId(updatedItem.getItemId());
					increasedItem.setItemDisplayCode(updatedItem.getItemDisplayCode());
					addedOrIncreased.add(increasedItem);
				}
			}
		}

		return addedOrIncreased;
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param bill
	 * @param billPayments
	 * @throws OHDataValidationException
	 */
	protected void validateBill(Bill bill, List<BillPayments> billPayments) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		LocalDateTime today = TimeTools.getNow();
		LocalDateTime upDate;
		LocalDateTime firstPay = today;
		LocalDateTime lastPay = today;
		LocalDateTime billDate = bill.getDate();

		if (!billPayments.isEmpty()) {
			firstPay = billPayments.get(0).getDate();
			lastPay = billPayments.get(billPayments.size() - 1).getDate();
			upDate = lastPay;
		} else {
			upDate = billDate;
		}
		bill.setUpdate(upDate);

		if (billDate.isAfter(today)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.billsinthefuturearenotallowed.msg")));
		}
		if (lastPay.isAfter(today)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.payementsinthefuturearenotallowed.msg")));
		}
		if (billDate.isAfter(firstPay)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.billdateaisfterthefirstpayment.msg")));
		}
		if (bill.getPatName().isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.pleaseinsertanameforthepatient.msg")));
		}
		if (bill.getStatus().equals("C") && bill.getBalance() != 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.abillwithanoutstandingbalancecannotbeclosed.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	public List<BillItems> getItems(int billID) throws OHServiceException {
		if (billID == 0) {
			return new ArrayList<>();
		}
		return ioOperations.getItems(billID);
	}

	public List<Bill> getBills(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient) throws OHServiceException {
		return ioOperations.getBillsBetweenDatesWherePatient(dateFrom, dateTo, patient);
	}

	public List<BillPayments> getPayments(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient) throws OHServiceException {
		return ioOperations.getPaymentsBetweenDatesWherePatient(dateFrom, dateTo, patient);
	}

	public List<BillPayments> getPayments(int billID) throws OHServiceException {
		return ioOperations.getPayments(billID);
	}

	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public Bill newBill(
		Bill bill,
		List<BillItems> billItems,
		List<BillPayments> billPayments) throws OHServiceException {

		validateBill(bill, billPayments);
		Bill newBill = ioOperations.newBill(bill);
		int billId = newBill.getId();

		if (billItems != null && !billItems.isEmpty()) {

			ioOperations.newBillItems(newBill, billItems);

			if (GeneralData.STOCKMVTONBILLSAVE) {
				updateMedicalStock(billItems, billId, false);
			}
			markPrescriptionsAsBilled(billItems, newBill);
			if (GeneralData.STOCKMVTONBILLSAVE) {
				updateMedicalStock(billItems, newBill.getId(), false);
			}
		}

		if (billPayments != null && !billPayments.isEmpty()) {
			ioOperations.newBillPayments(newBill, billPayments);
		}

		return newBill;
	}

	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public Bill updateBill(Bill updateBill, List<BillItems> billItems, List<BillPayments> billPayments) throws OHServiceException {
		validateBill(updateBill, billPayments);

		if (GeneralData.STOCKMVTONBILLSAVE) {
			List<BillItems> newItems = getNewItems(updateBill.getId(), billItems);
			List<BillItems> deletedItems = getDeletedItems(updateBill.getId(), billItems);
			updateMedicalStock(deletedItems, updateBill.getId(), true);
			updateMedicalStock(newItems, updateBill.getId(), false);
		}

		Bill updatedBill = ioOperations.updateBill(updateBill);
		ioOperations.newBillItems(updateBill, billItems);
		markPrescriptionsAsBilled(billItems, updatedBill);
		return updatedBill;
	}

	public List<Bill> getPendingBills(int patID) throws OHServiceException {
		return ioOperations.getPendingBills(patID);
	}

	public Bill getBill(int billID) throws OHServiceException {
		return ioOperations.getBill(billID);
	}

	public List<String> getUsers() throws OHServiceException {
		return ioOperations.getUsers();
	}

	public void deleteBill(Bill deleteBill) throws OHServiceException {
		ioOperations.deleteBill(deleteBill);
	}

	public List<Bill> getBills(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return ioOperations.getBillsBetweenDates(dateFrom, dateTo);
	}

	public List<Bill> getBills(List<BillPayments> billPayments) throws OHServiceException {
		if (billPayments.isEmpty()) {
			return new ArrayList<>();
		}
		return ioOperations.getBills(billPayments);
	}

	public List<BillPayments> getPayments(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return ioOperations.getPayments(dateFrom, dateTo);
	}

	public List<BillPayments> getPayments(List<Bill> billArray) throws OHServiceException {
		return ioOperations.getPayments(billArray);
	}

	public List<Bill> getPendingBillsAffiliate(int patID) throws OHServiceException {
		return ioOperations.getPendingBillsAffiliate(patID);
	}

	public List<BillItems> getDistinctItems() throws OHServiceException {
		return ioOperations.getDistictsBillItems();
	}

	public List<Bill> getBills(LocalDateTime dateFrom, LocalDateTime dateTo, BillItems billItem) throws OHServiceException {
		return ioOperations.getBillsBetweenDatesWhereBillItem(dateFrom, dateTo, billItem);
	}

	/**
	 * Get paginated bills with filters returning Page (nouveau GUI)
	 *
	 * @param status the bill status to filter (O for open, C for closed, null for all)
	 * @param dateFrom the start date to filter (inclusive, null for no lower bound)
	 * @param dateTo the end date to filter (exclusive, null for no upper bound
	 * @param patient the patient to filter (null for all)
	 * @param guarantor the user acting as guarantor to filter (null for all)
	 * @param page the page number to retrieve (0-based)
	 * @param size the number of items per page
	 * @return a Page of Bill matching the filters
	 * @throws OHServiceException when the calls to internal methods fail.	
	 */
	public Page<Bill> getBillsWithFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient, User guarantor, int page, int size) throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size);
		return ioOperations.getBillsWithFilters(status, dateFrom, dateTo, patient, guarantor, pageable);
	}

		/**
	 * Get paginated bills with filters returning Page (nouveau GUI)
	 *
	 * @param status the bill status to filter (O for open, C for closed, null for all)
	 * @param dateFrom the start date to filter (inclusive, null for no lower bound)
	 * @param dateTo the end date to filter (exclusive, null for no upper bound
	 * @param patient the patient to filter (null for all)
	 * @param guarantor the user acting as guarantor to filter (null for all)
	 * @param limit the number of items per page
	 * @param offset the page number to retrieve
	 * @return a Page of Bill matching the filters
	 * @throws OHServiceException when the calls to internal methods fail.	
	 */
	public List<Bill> getBillsListWithFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient, User guarantor, int limit, int offset) throws OHServiceException {
		return ioOperations.getBillsListWithFilters(status, dateFrom, dateTo, patient, guarantor, limit, offset);
	}

	/**
	 * Count bills with filters
	 * 
	 * @param status the bill status to filter (O for open, C for closed, null for all)
	 * @param dateFrom the start date to filter (inclusive, null for no lower bound	)
	 * @param dateTo the end date to filter (exclusive, null for no upper bound)
	 * @param patient the patient to filter (null for all)
	 * @param guarantor the user acting as guarantor to filter (null for all)
	 * @return the number of {@link Bill}s matching the filters
	 * @throws OHServiceException when the calls to internal methods fail.
	 */
	public long countBillsWithFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient, User guarantor) throws OHServiceException {
		return ioOperations.countBillsWithFilters(status, dateFrom, dateTo, patient, guarantor);
	}

	 /** Get the bills filtered by date, patient and guarantor
	 *
	 * @param dateFrom Start date
	 * @param dateTo End date
	 * @param patient Target patient
	 * @param guarantor The user acting as the guarantor for the bills.
	 * @return {@link  List} of {@link Bill}s matching the filter,
	 * or empty list if no match found
	 * @throws OHServiceException when the calls to internal methods fail.
	 */
	public List<Bill> getBillsByDatePatientAndGuarantor(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient, User guarantor) throws OHServiceException {
		return patient == null ? ioOperations.getBillsByDatesAndGuarantor(dateFrom, dateTo, guarantor) : ioOperations.getBillsByDatesPatientAndGuarantor(dateFrom, dateTo, patient, guarantor);
	}

	public List<BillPayments> getPaymentsByDatePatientAndGuarantor(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient, User guarantor) throws OHServiceException {
		if (dateFrom == null || dateTo == null) {
			throw new IllegalArgumentException("Date cannot be null");
		}
		return patient == null ? ioOperations.getPaymentsByDatesAndGuarantor(dateFrom, dateTo, guarantor) : ioOperations.getPaymentsByDatesPatientAndGuarantor(dateFrom, dateTo, patient, guarantor);
	}

	public List<Bill> getBillsByGuarantor(List<BillPayments> billPayments, User guarantor) throws OHServiceException {
		return billPayments.isEmpty() ? new ArrayList<>() : ioOperations.getBillsByGuarantor(billPayments, guarantor);
	}

	private void updateMedicalStock(List<BillItems> medicalItems, int billID, boolean isCharge) throws OHServiceException {
		if (medicalItems == null || medicalItems.isEmpty()) return;

		PatientBrowserManager patientManager = Context.getApplicationContext().getBean(PatientBrowserManager.class);
		Bill bill = getBill(billID);
		Ward ward = bill.getWard();
		if (ward == null) return;

		Patient patient = patientManager.getPatientById(bill.getBillPatient().getCode());
		List<Price> prices = priceListManager.getPrices();

		List<OHExceptionMessage> validationErrors = new ArrayList<>();

		for (BillItems item : medicalItems) {
			Price price = prices.stream()
				.filter(p -> p != null && "MED".equals(p.getGroup()) && item.getItemDescription().equals(p.getDesc()))
				.findFirst()
				.orElse(null);

			if (price != null) {
				try {
					addStockMvt(ward, patient, item, isCharge);
				} catch (OHDataValidationException e) {
					validationErrors.addAll(e.getMessages());
				}
			}
		}

		if (!validationErrors.isEmpty()) {
			throw new OHDataValidationException(validationErrors);
		}
	}

	private void addStockMvt(Ward ward, Patient patient, BillItems billItem, boolean isCharge) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		double qty = billItem.getItemQuantity();

		if (isCharge) {
			qty = -qty;
		}

		List<MedicalWard> medWards = mvtManager.getMedicalsWard(ward.getCode(), true);

		if (!isCharge && (medWards == null || medWards.isEmpty())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.stocknotavailableforitem") + " : " + billItem.getItemDescription()));
			throw new OHDataValidationException(errors);
		}

		MedicalWard medicalWard = medWards.stream()
			.filter(med -> med.getId().getMedical().getDescription().equals(billItem.getItemDescription()))
			.findFirst()
			.orElse(null);

		if (!isCharge && medicalWard == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.stocknotavailableforitem") + " : " + billItem.getItemDescription()));
			throw new OHDataValidationException(errors);
		}

		if (!isCharge && medicalWard.getQty() < qty) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.qtynotinstock") + " : " + billItem.getItemDescription()));
			throw new OHDataValidationException(errors);
		}

		MovementWard mvt = new MovementWard();
		mvt.setWard(ward);
		mvt.setPatient(patient);
		mvt.setDate(TimeTools.getServerDateTime());
		mvt.setPatient(true);
		mvt.setQuantity(qty);
		mvt.setDescription(patient.getName());

		if (isCharge) {
			Medical medical = medicalBrowsingManager.getMedicals(billItem.getItemDescription())
				.stream()
				.filter(med -> Objects.equals(med.getDescription(), billItem.getItemDescription()))
				.findFirst()
				.orElse(null);
			Lot lot = movStockInsertingManager.getLotByMedical(medical, false).stream().findFirst().orElse(null);
			mvt.setMedical(medical);
			mvt.setlot(lot);
		} else {
			mvt.setMedical(medicalWard.getId().getMedical());
			mvt.setlot(medicalWard.getLot());
		}
		mvt.setUnits("pieces");

		mvtManager.newMovementWard(mvt);
	}

	public List<BillItems> getAllBillItems(Bill bill) throws OHServiceException {
		return ioOperations.getAllBillItems(bill);
	}

	public List<BillPayments> getAllBillPayments(Bill bill) throws OHServiceException {
		return ioOperations.getAllBillPayments(bill);
	}

	/**
	 * Add a new billItemGroup with validation
	 *
	 * @param billItemGroup the BillItemGroup to add
	 * @return the added billItemGroup
	 * @throws OHServiceException when fails to add
	 */
	public BillItemGroup addBillItemGroup(BillItemGroup billItemGroup) throws OHServiceException {
		validateBillItemGroup(billItemGroup);
		return ioOperations.addBillItemGroup(billItemGroup);
	}

	/**
	 * Update a billItemGroup with validation
	 *
	 * @param billItemGroup the BillItemGroup to update
	 * @return the updated billItemGroup
	 * @throws OHServiceException when fails to update
	 */
	public BillItemGroup updateBillItemGroup(BillItemGroup billItemGroup) throws OHServiceException {
		validateBillItemGroup(billItemGroup);
		return ioOperations.updateBillItemGroup(billItemGroup);
	}

	/**
	 * Delete a billItemGroup
	 *
	 * @param groupId the id of the BillItemGroup to delete
	 * @throws OHServiceException when fails to delete
	 */
	public void deleteBillItemGroup(int groupId) throws OHServiceException {
		ioOperations.deleteBillItemGroup(groupId);
	}

	/**
	 * Get a single BillItemGroup by ID
	 *
	 * @param id the ID of the bill item group
	 * @return the matching BillItemGroup, or null if not found
	 * @throws OHServiceException if a database error occurs
	 */
	public BillItemGroup getBillItemGroupById(int id) throws OHServiceException {
		return ioOperations.getBillItemGroupById(id);
	}

	/**
	 * Get all billItemGroups
	 *
	 * @return the list of all billItemGroup
	 * @throws OHServiceException when fails to fetch list
	 */
	public List<BillItemGroup> getAllBillItemGroups() throws OHServiceException {
		return ioOperations.getAllBillItemGroups();
	}

	/**
	 * Get all active billItemGroups
	 *
	 * @return the list of all active billItemGroup
	 * @throws OHServiceException when fails to fetch list
	 */
	public List<BillItemGroup> getAllActiveBillItemGroups() throws OHServiceException {
		return ioOperations.getAllActiveBillItemGroups();
	}

	/**
	 * Add billItemGroupItems to a billItemGroup
	 *
	 * @param groupId the id of the billItemGroup
	 * @param items the BillItemGroupItems to add
	 * @throws OHServiceException when fails to add items
	 */
	public void addBillItemGroupItems(int groupId, List<BillItemGroupItem> items) throws OHServiceException {
		if (items != null) {
			for (BillItemGroupItem item : items) {
				validateBillItemGroupItem(item);
			}
		}
		ioOperations.addBillItemGroupItems(groupId, items);
	}

	/**
	 * Update a billItemGroupItem
	 *
	 * @param item the BillItemGroupItem to update
	 * @return the updated billItemGroupItem
	 * @throws OHServiceException when fails to update
	 */
	public BillItemGroupItem updateBillItemGroupItem(BillItemGroupItem item) throws OHServiceException {
		validateBillItemGroupItem(item);
		return ioOperations.updateBillItemGroupItem(item);
	}

	/**
	 * Delete billItemGroupItems from a billItemGroup
	 *
	 * @param groupId the id of the BillItemGroup
	 * @throws OHServiceException when fails to delete items
	 */
	public void deleteBillItemGroupItems(int groupId) throws OHServiceException {
		ioOperations.deleteBillItemGroupItems(groupId);
	}

	/**
	 * Delete a single billItemGroupItem
	 *
	 * @param itemId the id of the BillItemGroupItem to delete
	 * @throws OHServiceException when fails to delete
	 */
	public void deleteBillItemGroupItem(int itemId) throws OHServiceException {
		ioOperations.deleteBillItemGroupItem(itemId);
	}

	/**
	 * Get billItemGroupItems for a specific group
	 *
	 * @param groupId the id of the BillItemGroup
	 * @return the list of items
	 * @throws OHServiceException when fails to fetch list
	 */
	public List<BillItemGroupItem> getItemsByGroupId(int groupId) throws OHServiceException {
		return ioOperations.getItemsByGroupId(groupId);
	}

	/**
	 * Get all billItemGroupItems
	 *
	 * @return the list of all items
	 * @throws OHServiceException when fails to fetch list
	 */
	public List<BillItemGroupItem> getAllBillItemGroupItems() throws OHServiceException {
		return ioOperations.getAllBillItemGroupItems();
	}

	/**
	 * Get a single BillItemGroupItem by ID
	 *
	 * @param id the ID of the bill item group item
	 * @return the matching BillItemGroupItem, or null if not found
	 * @throws OHServiceException if a database error occurs
	 */
	public BillItemGroupItem getBillItemGroupItemById(int id) throws OHServiceException {
		return ioOperations.getBillItemGroupItemById(id);
	}

	/**
	 * Count all billItemGroups
	 *
	 * @return the count of all billItemGroups
	 * @throws OHServiceException when fails
	 */
	public long countAllBillItemGroups() throws OHServiceException {
		return ioOperations.countAllBillItemGroups();
	}

	/**
	 * Count active billItemGroups
	 *
	 * @return the count of active billItemGroups
	 * @throws OHServiceException when fails
	 */
	public long countAllActiveBillItemGroups() throws OHServiceException {
		return ioOperations.countAllActiveBillItemGroups();
	}

	/**
	 * Count items for a specific billItemGroup
	 *
	 * @param groupId the billItemGroup id
	 * @return the count of items
	 * @throws OHServiceException when fails
	 */
	public long countItemsByGroupId(int groupId) throws OHServiceException {
		return ioOperations.countItemsByGroupId(groupId);
	}

	/**
	 * Validate billItemGroup
	 *
	 * @param billItemGroup the billItemGroup to validate
	 * @throws OHDataValidationException when validation fails
	 */
	protected void validateBillItemGroup(BillItemGroup billItemGroup) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (billItemGroup == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error"),
				MessageBundle.getMessage("angal.common.pleasefillallfields"), OHSeverityLevel.ERROR));
		} else {
			if (billItemGroup.getTitle() == null || billItemGroup.getTitle().isEmpty()) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error"),
					MessageBundle.getMessage("angal.billitemgroup.title.required"), OHSeverityLevel.ERROR));
			}
			if (billItemGroup.getTotal() == null || billItemGroup.getTotal() < 0) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error"),
					MessageBundle.getMessage("angal.billitemgroup.total.invalid"), OHSeverityLevel.ERROR));
			}
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Validate billItemGroupItem
	 *
	 * @param item the item to validate
	 * @throws OHDataValidationException when validation fails
	 */
	protected void validateBillItemGroupItem(BillItemGroupItem item) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (item == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error"),
				MessageBundle.getMessage("angal.common.pleasefillallfields"), OHSeverityLevel.ERROR));
		} else {
			if (item.getAmount() == null || item.getAmount() < 0) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error"),
					MessageBundle.getMessage("angal.billitemgroup.amount.invalid"), OHSeverityLevel.ERROR));
			}
			if (item.getQuantity() == null || item.getQuantity() < 1) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error"),
					MessageBundle.getMessage("angal.billitemgroup.quantity.invalid"), OHSeverityLevel.ERROR));
			}
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Check if a patient has pending prescriptions (therapies, exams, operations)
	 * that haven't been billed yet.
	 *
	 * @param patientCode the patient's code
	 * @return true if the patient has pending prescriptions, false otherwise
	 * @throws OHServiceException
	 */
	public boolean hasPrescription(Integer patientCode) throws OHServiceException {
		return ioOperations.hasPrescription(patientCode);
	}

	public Price getPrice(String itemId, ItemGroup group, Patient patient) throws OHServiceException {
		return ioOperations.getPrice(itemId, group, patient);
	}

	public Price getPriceFromListWithoutReduction(String itemId, ItemGroup group, Patient patient) throws OHServiceException {
		return ioOperations.getPriceFromListWithoutReduction(itemId, group, patient);
	}

	public boolean isPrescriptionAlreadyBilledAndPaid(Integer patientCode, Integer prescriptionId, String itemGroup) throws OHServiceException {
		return ioOperations.isPrescriptionAlreadyBilledAndPaid(patientCode, prescriptionId, itemGroup);
	}

	private void markPrescriptionsAsBilled(List<BillItems> billItems, Bill bill) throws OHServiceException {
		if (billItems == null) return;

		for (BillItems item : billItems) {
			if (item.getPrescriptionId() == null || item.getPrescriptionId() == 0) {
				continue;
			}
			if (ItemGroup.MEDICAL.getCode().equals(item.getItemGroup())) {
				therapyManager.updateBougthQuantity(item.getPrescriptionId(), item.getItemQuantity());
			} else if (ItemGroup.EXAM.getCode().equals(item.getItemGroup())) {
				labManager.updateBillForLaboratory(item.getPrescriptionId(), bill);
			} else if (ItemGroup.OPERATION.getCode().equals(item.getItemGroup())) {
				operationRowManager.updateBillForOperationRow(item.getPrescriptionId(), bill);
			}
		}
	}

	/**
	 * Sum of bill amounts filtered by status, date range, patient and guarantor
	 *
	 * @param status the bill status to filter
	 * @param dateFrom the start date to filter
	 * @param dateTo the end date to filter
	 * @param patient the patient to filter
	 * @param guarantor the user acting as guarantor to filter
	 * @return the sum of amounts matching the filters
	 * @throws OHServiceException
	 */
	public double sumAmountByFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                 Patient patient, User guarantor) throws OHServiceException {
		return ioOperations.sumAmountByFilters(status, dateFrom, dateTo, patient, guarantor);
	}

	/**
	 * Sum of bill balances filtered by status, date range, patient and guarantor
	 *
	 * @param status the bill status to filter
	 * @param dateFrom the start date to filter
	 * @param dateTo the end date to filter
	 * @param patient the patient to filter
	 * @param guarantor the user acting as guarantor to filter
	 * @return the sum of balances matching the filters
	 * @throws OHServiceException
	 */
	public double sumBalanceByFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                  Patient patient, User guarantor) throws OHServiceException {
		return ioOperations.sumBalanceByFilters(status, dateFrom, dateTo, patient, guarantor);
	}

	/**
	 * Sum of payments filtered by date range, patient and guarantor
	 *
	 * @param dateFrom the start date to filter
	 * @param dateTo the end date to filter
	 * @param patient the patient to filter
	 * @param guarantor the user acting as guarantor to filter
	 * @return the sum of payments matching the filters
	 * @throws OHServiceException
	 */
	public double sumPaymentsByFilters(LocalDateTime dateFrom, LocalDateTime dateTo,
	                                   Patient patient, User guarantor) throws OHServiceException {
		return ioOperations.sumPaymentsByFilters(dateFrom, dateTo, patient, guarantor);
	}

	/**
	 * Sum of payments filtered by user, date range, patient and guarantor
	 *
	 * @param username the user who created the payment
	 * @param dateFrom the start date to filter
	 * @param dateTo the end date to filter
	 * @param patient the patient to filter
	 * @param guarantor the user acting as guarantor to filter
	 * @return the sum of payments matching the filters
	 * @throws OHServiceException
	 */
	public double sumPaymentsByUserAndFilters(String username, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                          Patient patient, User guarantor) throws OHServiceException {
		return ioOperations.sumPaymentsByUserAndFilters(username, dateFrom, dateTo, patient, guarantor);
	}

	/**
<<<<<<< HEAD
	 * Update a BillItemGroup with a new list of items.
	 * Handles lazy initialization by working directly via repositories.
	 *
	 * @param group    the BillItemGroup to update
	 * @param newItems the new list of items
	 * @return the updated BillItemGroup
	 * @throws OHServiceException when fails to update
	 */
	public BillItemGroup updateBillItemGroupWithItems(BillItemGroup group, List<BillItemGroupItem> newItems) throws OHServiceException {
		return ioOperations.updateBillItemGroupWithItems(group, newItems);
=======
	 * Export payments to Sage using streaming (no memory overload)
	 *
	 * @param dateFrom start date (inclusive)
	 * @param dateTo end date (exclusive)
	 * @throws IOException if an I/O error occurs
	 */
	public List<BillPayments> getPaymentsForSage(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return ioOperations.getPaymentsForSage(dateFrom, dateTo);
	}

	/**
	 * Export payments to Sage using streaming (no memory overload)
	 *
	 * @param dateFrom start date (inclusive)
	 * @param dateTo end date (exclusive)
	 * @throws IOException if an I/O error occurs
	 */
	public List<Bill> getBillsForSage(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return ioOperations.getBillsForSage(dateFrom, dateTo);
	}

	/**
	 * Export payments to Sage using streaming (no memory overload)
	 *
	 * @param file the output file
	 * @param dateFrom start date (inclusive)
	 * @param dateTo end date (exclusive)
	 * @throws IOException if an I/O error occurs
	 */
	public boolean exportSagePayments(File file, LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException, IOException {
		return ioOperations.exportSagePayments(file, dateFrom, dateTo);
	}

	/**
	 * Export payments to Sage using streaming (no memory overload)
	 *
	 * @param file the output file
	 * @param dateFrom start date (inclusive)
	 * @param dateTo end date (exclusive)
	 * @throws IOException if an I/O error occurs
	 */
	public boolean exportSageBills(File file, LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException, IOException {
		return ioOperations.exportSageBills(file, dateFrom, dateTo);
	}

	/**
	 * Export payments to Sage using streaming (no memory overload)
	 *
	 * @param file the output file
	 * @param dateFrom start date (inclusive)
	 * @param dateTo end date (exclusive)
	 * @throws IOException if an I/O error occurs
	 */
	public void exportSagePaymentsStreaming(File file, LocalDateTime dateFrom, LocalDateTime dateTo) throws IOException, OHServiceException {
		ioOperations.exportSagePaymentsStreaming(file, dateFrom, dateTo);
>>>>>>> feature/OH-397-foumban-release
	}
}