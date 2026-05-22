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

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
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
import org.isf.menu.model.User;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.model.Price;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BillBrowserManager {
	private final AccountingIoOperations ioOperations;
	private final MovWardBrowserManager mvtManager;
	private final PriceListManager priceListManager;
	private final MedicalBrowsingManager medicalBrowsingManager;
	private final MovStockInsertingManager movStockInsertingManager;

	public BillBrowserManager(AccountingIoOperations ioOperations, MovWardBrowserManager mvtManager, PriceListManager priceListManager, MedicalBrowsingManager medicalBrowsingManager, MovStockInsertingManager movStockInsertingManager) {
		this.ioOperations = ioOperations;
		this.mvtManager = mvtManager;
		this.priceListManager = priceListManager;
		this.medicalBrowsingManager = medicalBrowsingManager;
		this.movStockInsertingManager = movStockInsertingManager;
	}

	/**
	 * Returns a list of items that were removed or reduced in quantity.
	 * For reduced items, the quantity will be the difference.
	 */
	private List<BillItems> getDeletedItems(int billID, List<BillItems> updatedItems) throws OHServiceException {
		List<BillItems> oldItems = this.ioOperations.getItems(billID);
		if (oldItems == null || oldItems.isEmpty()) return new ArrayList<>();

		if (updatedItems == null) updatedItems = new ArrayList<>();

		// Map new items by ID
		Map<Integer, BillItems> newItemsMap = updatedItems.stream()
			.filter(item -> item.getId() > 0)
			.collect(Collectors.toMap(BillItems::getId, item -> item));

		List<BillItems> removedOrReduced = new ArrayList<>();

		for (BillItems oldItem : oldItems) {
			BillItems newItem = newItemsMap.get(oldItem.getId());
			if (newItem == null) {
				// Item fully removed
				removedOrReduced.add(oldItem);
			} else if (oldItem.getItemQuantity() > newItem.getItemQuantity()) {
				// Quantity reduced → calculate difference
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
				// New item
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
	public Bill updateBill(Bill updateBill,
						   List<BillItems> billItems,
						   List<BillPayments> billPayments) throws OHServiceException {

		validateBill(updateBill, billPayments);

		if (GeneralData.STOCKMVTONBILLSAVE) {

			List<BillItems> newItems = getNewItems(updateBill.getId(), billItems);
			List<BillItems> deletedItems = getDeletedItems(updateBill.getId(), billItems);

			updateMedicalStock(deletedItems, updateBill.getId(), true);
			updateMedicalStock(newItems, updateBill.getId(), false);
		}

		Bill updatedBill = updateBill(updateBill);
		newBillItems(updateBill.getId(), billItems);
		newBillPayments(updatedBill.getId(), billPayments);

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

	/**
	 * Get the bills filtered by date, patient and guarantor
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
}
