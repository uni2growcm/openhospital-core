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
import org.isf.patient.model.Patient;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.model.ItemGroup;
import org.isf.priceslist.model.Price;
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
			lastPay = billPayments.get(billPayments.size() - 1).getDate(); // most recent payment
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

	/**
	 * Retrieves all the {@link BillItems} associated to the passed {@link Bill} id.
	 *
	 * @param billID the bill id.
	 * @return a list of {@link BillItems} or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<BillItems> getItems(int billID) throws OHServiceException {
		if (billID == 0) {
			return new ArrayList<>();
		}
		return ioOperations.getItems(billID);
	}

	/**
	 * Retrieves all the bills of a given patient between dateFrom and datTo
	 *
	 * @param dateFrom
	 * @param dateTo
	 * @param patient
	 * @return the bills list
	 * @throws OHServiceException
	 */
	public List<Bill> getBills(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient) throws OHServiceException {
		return ioOperations.getBillsBetweenDatesWherePatient(dateFrom, dateTo, patient);
	}

	/**
	 * Retrieves all the billPayments for a given patient between dateFrom and dateTo
	 *
	 * @param dateFrom
	 * @param dateTo
	 * @param patient
	 * @return the list of payments
	 * @throws OHServiceException
	 */
	public List<BillPayments> getPayments(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient) throws OHServiceException {
		return ioOperations.getPaymentsBetweenDatesWherePatient(dateFrom, dateTo, patient);
	}

	/**
	 * Gets all the {@link BillPayments} for the specified {@link Bill}.
	 *
	 * @param billID the bill id.
	 * @return a list of {@link BillPayments}
	 * @throws OHServiceException
	 */
	public List<BillPayments> getPayments(int billID) throws OHServiceException {
		return ioOperations.getPayments(billID);
	}

	/**
	 * Stores a new {@link Bill} along with all its {@link BillItems} and {@link BillPayments}
	 *
	 * @param bill the bill to store.
	 * @param billItems the list of bill's items
	 * @param billPayments the list of bill's payments
	 * @returns the persisted Bill object
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public Bill newBill(
		Bill bill,
		List<BillItems> billItems,
		List<BillPayments> billPayments) throws OHServiceException {

		validateBill(bill, billPayments);
		Bill newBill = newBill(bill);
		int billId = newBill.getId();

		if (billItems != null && !billItems.isEmpty()) {

			ioOperations.newBillItems(newBill, billItems);

			if (GeneralData.STOCKMVTONBILLSAVE) {
				updateMedicalStock(billItems, billId, false);
			}
			markPrescriptionsAsBilled(billItems, newBill);
		}

		if (billPayments != null && !billPayments.isEmpty()) {
			newBillPayments(newBill.getId(), billPayments);
		}

		return newBill;
	}

	/**
	 * Stores a new {@link Bill}.
	 *
	 * @param newBill the bill to store.
	 * @return the persisted Bill object
	 * @throws OHServiceException
	 */
	private Bill newBill(Bill newBill) throws OHServiceException {
		return ioOperations.newBill(newBill);
	}

	/**
	 * Stores a list of {@link BillItems} associated to a {@link Bill}.
	 *
	 * @param billID the bill id.
	 * @param billItems the bill items to store.
	 * @throws OHServiceException
	 */
	private void newBillItems(int billID, List<BillItems> billItems) throws OHServiceException {
		ioOperations.newBillItems(ioOperations.getBill(billID), billItems);
	}

	/**
	 * Stores a list of {@link BillPayments} associated to a {@link Bill}.
	 *
	 * @param billID the bill id.
	 * @param payItems the bill payments.
	 * @throws OHServiceException
	 */
	private void newBillPayments(int billID, List<BillPayments> payItems) throws OHServiceException {
		ioOperations.newBillPayments(ioOperations.getBill(billID), payItems);
	}

	/**
	 * Updates the specified {@link Bill} along with all its {@link BillItems} and {@link BillPayments}
	 *
	 * @param updateBill the bill to update.
	 * @param billItems the list of bill's items
	 * @param billPayments the list of bill's payments
	 * @return the updated Bill object
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public Bill updateBill(
		Bill updateBill,
		List<BillItems> billItems,
		List<BillPayments> billPayments
	) throws OHServiceException {

		validateBill(updateBill, billPayments);

		if (GeneralData.STOCKMVTONBILLSAVE) {

			List<BillItems> newItems = getNewItems(updateBill.getId(), billItems);
			List<BillItems> deletedItems = getDeletedItems(updateBill.getId(), billItems);

			updateMedicalStock(deletedItems, updateBill.getId(), true);
			updateMedicalStock(newItems, updateBill.getId(), false);
		}

		Bill updatedBill = updateBill(updateBill);
		newBillItems(updateBill.getId(), billItems);
		markPrescriptionsAsBilled(billItems, updatedBill);
		newBillPayments(updateBill.getId(), billPayments);
		return updatedBill;
	}

	/**
	 * Updates the specified {@link Bill}.
	 *
	 * @param updateBill the bill to update.
	 * @return the updated Bill object
	 * @throws OHServiceException
	 */
	private Bill updateBill(Bill updateBill) throws OHServiceException {
		return ioOperations.updateBill(updateBill);
	}

	/**
	 * Returns all the pending {@link Bill}s for the specified patient.
	 *
	 * @param patID the patient id.
	 * @return the list of pending bills or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<Bill> getPendingBills(int patID) throws OHServiceException {
		return ioOperations.getPendingBills(patID);
	}

	/**
	 * Get the {@link Bill} with specified billID
	 *
	 * @param billID
	 * @return the {@link Bill} or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public Bill getBill(int billID) throws OHServiceException {
		return ioOperations.getBill(billID);
	}

	/**
	 * Returns all user ids related to a {@link BillPayments}.
	 *
	 * @return a list of user id or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<String> getUsers() throws OHServiceException {
		return ioOperations.getUsers();
	}

	/**
	 * Deletes the specified {@link Bill}. If the argument is NULL then an error is thrown. If the Bill is not found it is silently ignored.
	 *
	 * @param deleteBill the bill to delete.
	 * @throws OHServiceException
	 */
	public void deleteBill(Bill deleteBill) throws OHServiceException {
		ioOperations.deleteBill(deleteBill);
	}

	/**
	 * Retrieves all the {@link Bill}s for the specified date range.
	 *
	 * @param dateFrom the low date range endpoint, inclusive.
	 * @param dateTo the high date range endpoint, inclusive.
	 * @return a list of retrieved {@link Bill}s or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<Bill> getBills(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return ioOperations.getBillsBetweenDates(dateFrom, dateTo);
	}

	/**
	 * Gets all the {@link Bill}s associated to the passed {@link BillPayments}.
	 *
	 * @param billPayments the {@link BillPayments} associated to the bill to retrieve.
	 * @return a list of {@link Bill} associated to the passed {@link BillPayments} or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<Bill> getBills(List<BillPayments> billPayments) throws OHServiceException {
		if (billPayments.isEmpty()) {
			return new ArrayList<>();
		}
		return ioOperations.getBills(billPayments);
	}

	/**
	 * Retrieves all the {@link BillPayments} for the specified date range.
	 *
	 * @param dateFrom low endpoint, inclusive, for the date range.
	 * @param dateTo high endpoint, inclusive, for the date range.
	 * @return a list of {@link BillPayments} for the specified date range or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<BillPayments> getPayments(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return ioOperations.getPayments(dateFrom, dateTo);
	}

	/**
	 * Retrieves all the {@link BillPayments} associated to the passed {@link Bill} list.
	 *
	 * @param billArray the bill array list of {@link Bill}s.
	 * @return a list of {@link BillPayments} associated to the passed bill list or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<BillPayments> getPayments(List<Bill> billArray) throws OHServiceException {
		return ioOperations.getPayments(billArray);
	}

	/**
	 * Retrieves all the {@link Bill}s associated to the specified {@link Patient}.
	 *
	 * @param patID the Patient's ID
	 * @return the list of {@link Bill}s
	 * @throws OHServiceException
	 */
	public List<Bill> getPendingBillsAffiliate(int patID) throws OHServiceException {
		return ioOperations.getPendingBillsAffiliate(patID);
	}

	/**
	 * Returns all the distinct stored {@link BillItems}.
	 *
	 * @return a list of distinct {@link BillItems} or null if an error occurs.
	 * @throws OHServiceException
	 */
	public List<BillItems> getDistinctItems() throws OHServiceException {
		return ioOperations.getDistictsBillItems();
	}

	/**
	 * Get the bills list with a given billItem
	 *
	 * @param dateFrom
	 * @param dateTo
	 * @param billItem
	 * @return
	 * @throws OHServiceException
	 */
	public List<Bill> getBills(LocalDateTime dateFrom, LocalDateTime dateTo, BillItems billItem) throws OHServiceException {
		return ioOperations.getBillsBetweenDatesWhereBillItem(dateFrom, dateTo, billItem);
	}

	private void updateMedicalStock(List<BillItems> medicalItems, int billID, boolean isCharge) throws OHServiceException {
		if (medicalItems == null || medicalItems.isEmpty()) return;

		PatientBrowserManager patientManager = Context.getApplicationContext().getBean(PatientBrowserManager.class);
		Bill bill = getBill(billID);
		Ward ward = bill.getWard();
		if (ward == null) return;

		Patient patient = patientManager.getPatientById(bill.getBillPatient().getCode());
		List<Price> prices = patient.getPriceList() != null ?
			priceListManager.getByListId(patient.getPriceList().getId()) :
			priceListManager.getPrices();

		List<OHExceptionMessage> validationErrors = new ArrayList<>();

		for (BillItems item : medicalItems) {
			Price price = prices.stream()
				.filter(p -> p != null && "MED".equals(p.getGroup())
					&& item.getItemDescription().equals(p.getDesc()))
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
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.stocknotavailableforitem")
				+ " : " + billItem.getItemDescription()));
			throw new OHDataValidationException(errors);  // Stop execution immediately
		}

		MedicalWard medicalWard = medWards.stream()
			.filter(med -> med.getId().getMedical().getDescription()
				.equals(billItem.getItemDescription()))
			.findFirst()
			.orElse(null);

		if (!isCharge && medicalWard == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.stocknotavailableforitem")
				+ " : " + billItem.getItemDescription()));
			throw new OHDataValidationException(errors);  // Stop execution immediately
		}

		if (!isCharge && medicalWard.getQty() < qty) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.qtynotinstock")
				+ " : " + billItem.getItemDescription()));
			throw new OHDataValidationException(errors);
		}

		// Now safe to proceed — medicalWard is guaranteed non-null
		MovementWard mvt = new MovementWard();
		mvt.setWard(ward);
		mvt.setPatient(patient);
		mvt.setDate(TimeTools.getServerDateTime());
		mvt.setPatient(true);
		mvt.setQuantity(qty);
		mvt.setDescription(patient.getName());
		if (isCharge) {
			Medical medical =  medicalBrowsingManager.getMedicals(billItem.getItemDescription())
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

	/**
	 * Get the bills payments filtered by date patient  and guarantor
	 *
	 * @param dateFrom Start date
	 * @param dateTo End date
	 * @param patient Target patient
	 * @param guarantor The user acting as the guarantor for the bills.
	 * @return {@link  List} of {@link Bill}s matching the filter,
	 * or empty list if no match found
	 * @throws OHServiceException when the calls to internal methods fail.
	 */
	public List<BillPayments> getPaymentsByDatePatientAndGuarantor(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient, User guarantor) throws OHServiceException {
		if (dateFrom == null || dateTo == null) {
			throw new IllegalArgumentException("Date cannot be null");
		}
		return patient == null ? ioOperations.getPaymentsByDatesAndGuarantor(dateFrom, dateTo, guarantor) : ioOperations.getPaymentsByDatesPatientAndGuarantor(dateFrom, dateTo, patient, guarantor);
	}

	/**
	 * Get the bills payments filtered by guarantor
	 *
	 * @param guarantor the user acting as the guarantor for the bills.
	 * @return The {@link List} of{@link BillPayments} matching the filters, or an empty list if no match
	 * @throws OHServiceException when failed to execute the query.
	 */
	public List<Bill> getBillsByGuarantor(List<BillPayments> billPayments, User guarantor) throws OHServiceException {
		return billPayments.isEmpty() ? new ArrayList<>() : ioOperations.getBillsByGuarantor(billPayments, guarantor);
	}

	/**
	 * Retrieves all items of a bill (including refunds)
	 * @param bill the bill
	 * @return complete list of items
	 * @throws OHServiceException
	 */
	public List<BillItems> getAllBillItems(Bill bill) throws OHServiceException {
		return ioOperations.getAllBillItems(bill);
	}

	/**
	 * Retrieves all payments of a bill (including refunds)
	 * @param bill the bill
	 * @return complete list of payments
	 * @throws OHServiceException
	 */
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

	/**
	 * Gets the price of an item with patient reductions applied.
	 * This method queries the database directly like the legacy version.
	 *
	 * @param itemId the item code (medical code, exam code, operation code)
	 * @param group the item group (MED, EXA, OPE, OTH)
	 * @param patient the patient (for reduction plan)
	 * @return the Price with reductions applied, or null if not found
	 * @throws OHServiceException
	 */
	public Price getPrice(String itemId, ItemGroup group, Patient patient) throws OHServiceException {
		return ioOperations.getPrice(itemId, group, patient);
	}

	/**
	 * Gets the gross price (without reductions) of an item.
	 *
	 * @param itemId the item code
	 * @param group the item group
	 * @param patient the patient
	 * @return the Price with gross price, or null if not found
	 * @throws OHServiceException
	 */
	public Price getPriceFromListWithoutReduction(String itemId, ItemGroup group, Patient patient) throws OHServiceException {
		return ioOperations.getPriceFromListWithoutReduction(itemId, group, patient);
	}

	/**
	 * Vérifie si une prescription spécifique est déjà dans une facture payée.
	 *
	 * @param patientCode    le code du patient
	 * @param prescriptionId l'identifiant de la prescription
	 * @param itemGroup      le groupe de l'item ("MED", "EXA", "OPE")
	 * @return true si déjà facturée et payée
	 * @throws OHServiceException
	 */
	public boolean isPrescriptionAlreadyBilledAndPaid(
		Integer patientCode,
		Integer prescriptionId,
		String itemGroup) throws OHServiceException {
		return ioOperations.isPrescriptionAlreadyBilledAndPaid(
			patientCode, prescriptionId, itemGroup);
	}

	/**
	 * Marks prescriptions as billed by updating the corresponding tables.
	 *
	 * @param billItems the list of bill items containing prescription information
	 * @param bill the Bill object to associate
	 * @throws OHServiceException if an error occurs during the update
	 */
	private void markPrescriptionsAsBilled(List<BillItems> billItems, Bill bill) throws OHServiceException {
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
}
