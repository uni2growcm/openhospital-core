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
package org.isf.accounting.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.lab.manager.LabManager;
import org.isf.menu.model.User;
import org.isf.operation.manager.OperationRowBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.priceslist.model.ItemGroup;
import org.isf.priceslist.model.Price;
import org.isf.priceslist.model.PriceList;
import org.isf.therapy.manager.TherapyManager;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

/**
 * Persistence class for Accounting module.
 */
@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class AccountingIoOperations {

	private AccountingBillIoOperationRepository billRepository;
	private AccountingBillPaymentIoOperationRepository billPaymentRepository;
	private AccountingBillItemsIoOperationRepository billItemsRepository;

	private TherapyManager therapyManager;
	private LabManager labManager;
	private OperationRowBrowserManager operationRowBrowserManager;

	public AccountingIoOperations(
		AccountingBillIoOperationRepository accountingBillIoOperationRepository,
		AccountingBillPaymentIoOperationRepository accountingBillPaymentIoOperationRepository,
		AccountingBillItemsIoOperationRepository accountingBillItemsIoOperationRepository,
		TherapyManager therapyManager,
		LabManager labManager,
		OperationRowBrowserManager operationRowBrowserManager) {
		this.billRepository = accountingBillIoOperationRepository;
		this.billPaymentRepository = accountingBillPaymentIoOperationRepository;
		this.billItemsRepository = accountingBillItemsIoOperationRepository;
		this.therapyManager = therapyManager;
		this.labManager = labManager;
		this.operationRowBrowserManager = operationRowBrowserManager;
	}

	/**
	 * Returns all the pending {@link Bill}s for the specified patient.
	 *
	 * @param patID the patient id.
	 * @return the list of pending bills.
	 * @throws OHServiceException if an error occurs retrieving the pending bills.
	 */
	public List<Bill> getPendingBills(int patID) throws OHServiceException {
		if (patID != 0) {
			return billRepository.findByStatusAndBillPatientCodeOrderByDateDesc("O", patID);
		}
		return billRepository.findByStatusOrderByDateDesc("O");
	}

	/**
	 * Get all the {@link Bill}s.
	 *
	 * @return a list of bills.
	 * @throws OHServiceException if an error occurs retrieving the bills.
	 */
	public List<Bill> getBills() throws OHServiceException {
		return billRepository.findAllByOrderByDateDesc();
	}

	/**
	 * Get the {@link Bill} with specified billID.
	 *
	 * @param billID
	 * @return the {@link Bill}.
	 * @throws OHServiceException if an error occurs retrieving the bill.
	 */
	public Bill getBill(int billID) throws OHServiceException {
		return billRepository.findById(billID).orElse(null);
	}

	/**
	 * Returns all user ids from {@link BillPayments}.
	 *
	 * @return a list of user id.
	 * @throws OHServiceException if an error occurs retrieving the users list.
	 */
	public List<String> getUsers() throws OHServiceException {
		Set<String> accountingUsers = new TreeSet<>(String::compareTo);
		accountingUsers.addAll(billRepository.findUserDistinctByOrderByUserAsc());
		accountingUsers.addAll(billPaymentRepository.findUserDistinctByOrderByUserAsc());
		return new ArrayList<>(accountingUsers);
	}

	/**
	 * Returns the {@link BillItems} associated to the specified {@link Bill} id or all the stored {@link BillItems} if no id is provided.
	 *
	 * @param billID the bill id or {@code 0}.
	 * @return a list of {@link BillItems} associated to the bill id or all the stored bill items.
	 * @throws OHServiceException if an error occurs retrieving the bill items.
	 */
	public List<BillItems> getItems(int billID) throws OHServiceException {
		if (billID != 0) {
			return billItemsRepository.findByBill_idOrderByIdAsc(billID);
		}
		return billItemsRepository.findAllByOrderByIdAsc();
	}

	/**
	 * Retrieves all the {@link BillPayments} for the specified date range.
	 *
	 * @param dateFrom low endpoint, inclusive, for the date range.
	 * @param dateTo high endpoint, inclusive, for the date range.
	 * @return a list of {@link BillPayments} for the specified date range.
	 * @throws OHServiceException if an error occurs retrieving the bill payments.
	 */
	public List<BillPayments> getPayments(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return billPaymentRepository.findByDateBetweenOrderByIdAscDateAsc(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo));
	}

	/**
	 * Retrieves all the {@link BillPayments} for the specified {@link Bill} id, or all the stored {@link BillPayments} if no id is indicated.
	 *
	 * @param billID the bill id or {@code 0}.
	 * @return the list of bill payments.
	 * @throws OHServiceException if an error occurs retrieving the bill payments.
	 */
	public List<BillPayments> getPayments(int billID) throws OHServiceException {
		if (billID != 0) {
			return billPaymentRepository.findAllWherBillIdByOrderByBillAndDate(billID);
		}
		return billPaymentRepository.findAllByOrderByBillAndDate();
	}

	/**
	 * Stores a new {@link Bill}.
	 *
	 * @param newBill the bill to store.
	 * @return the persisted Bill object
	 * @throws OHServiceException if an error occurs storing the bill.
	 */
	public Bill newBill(Bill newBill) throws OHServiceException {
		return billRepository.save(newBill);
	}

	/**
	 * Stores a list of {@link BillItems} associated to a {@link Bill}.
	 *
	 * @param bill the bill.
	 * @param billItems the bill items to store.
	 * @throws OHServiceException if an error occurs during the store operation.
	 */
	public void newBillItems(Bill bill, List<BillItems> billItems) throws OHServiceException {
		billItemsRepository.deleteWhereId(bill.getId());
		for (BillItems item : billItems) {
			item.setBill(bill);
			item.setId(0);
			if (item.getItemDate() == null) {
				item.setItemDate(LocalDateTime.now());
			}
			billItemsRepository.save(item);
		}
	}

	/**
	 * Stores a list of {@link BillPayments} associated to a {@link Bill}.
	 *
	 * @param bill the bill.
	 * @param payItems the bill payments.
	 * @throws OHServiceException if an error occurs during the store procedure.
	 */
	public void newBillPayments(Bill bill, List<BillPayments> payItems) throws OHServiceException {
		billPaymentRepository.deleteWhereId(bill.getId());
		for (BillPayments payment : payItems) {
			payment.setBill(bill);
			payment.setId(0);
			billPaymentRepository.save(payment);
		}
	}

	/**
	 * Updates the specified {@link Bill}.
	 *
	 * @param updateBill the bill to update.
	 * @return the updated Bill object
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public Bill updateBill(Bill updateBill) throws OHServiceException {
		return billRepository.save(updateBill);
	}

	/**
	 * Deletes the specified {@link Bill}. If the argument is NULL then an error is thrown. If the Bill is not found it is silently ignored.
	 *
	 * @param deleteBill the bill to delete.
	 * @throws OHServiceException if an error occurs deleting the bill.
	 */
	public void deleteBill(Bill deleteBill) throws OHServiceException {
		billRepository.deleteById(deleteBill.getId());
	}

	/**
	 * Retrieves all the {@link Bill}s for the specified date range.
	 *
	 * @param dateFrom the low date range endpoint, inclusive.
	 * @param dateTo the high date range endpoint, inclusive.
	 * @return a list of retrieved {@link Bill}s.
	 * @throws OHServiceException if an error occurs retrieving the bill list.
	 */
	public List<Bill> getBillsBetweenDates(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return billRepository.findByDateBetween(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo));
	}

	/**
	 * Gets all the {@link Bill}s associated to the passed {@link BillPayments}.
	 *
	 * @param payments the {@link BillPayments} associated to the bill to retrieve.
	 * @return a list of {@link Bill} associated to the passed {@link BillPayments}.
	 * @throws OHServiceException if an error occurs retrieving the bill list.
	 */
	public List<Bill> getBills(List<BillPayments> payments) throws OHServiceException {
		Set<Bill> bills = new TreeSet<>((o1, o2) -> o1.getId() == o2.getId() ? 0 : -1);
		for (BillPayments bp : payments) {
			bills.add(bp.getBill());
		}
		return new ArrayList<>(bills);
	}

	/**
	 * Retrieves all the {@link BillPayments} associated to the passed {@link Bill} list.
	 *
	 * @param bills the bill list.
	 * @return a list of {@link BillPayments} associated to the passed bill list.
	 * @throws OHServiceException if an error occurs retrieving the payments.
	 */
	public List<BillPayments> getPayments(List<Bill> bills) throws OHServiceException {
		return billPaymentRepository.findAllByBillIn(bills);
	}

	/**
	 * Retrieves all billPayments for a given patient in the period dateFrom -> dateTo
	 *
	 * @param dateFrom
	 * @param dateTo
	 * @param patient
	 * @return
	 * @throws OHServiceException
	 */
	public List<BillPayments> getPaymentsBetweenDatesWherePatient(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient)
		throws OHServiceException {
		return billPaymentRepository.findByDateAndPatient(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo), patient.getCode());
	}

	/**
	 * Retrieves all the bills for a given patient in the period dateFrom -> dateTo
	 *
	 * @param dateFrom
	 * @param dateTo
	 * @param patient
	 * @return the bill list
	 * @throws OHServiceException
	 */
	public List<Bill> getBillsBetweenDatesWherePatient(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient) throws OHServiceException {
		return billRepository.findByDateAndPatient(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo), patient.getCode());
	}

	/**
	 *
	 * @param patID
	 * @return
	 * @throws OHServiceException
	 */
	public List<Bill> getPendingBillsAffiliate(int patID) throws OHServiceException {
		return billRepository.findAllPendindBillsByBillPatient(patID);
	}

	/**
	 *
	 * @param patID
	 * @return
	 * @throws OHServiceException
	 */
	public List<Bill> getAllPatientsBills(int patID) throws OHServiceException {
		return billRepository.findByBillPatientCode(patID);
	}

	/**
	 * Return distinct BillItems
	 *
	 * @return BillItems list
	 * @throws OHServiceException
	 */
	public List<BillItems> getDistictsBillItems() throws OHServiceException {
		return billItemsRepository.findAllGroupByDescription();
	}

	/**
	 * Return the bill list which date between dateFrom and dateTo and containing given billItem
	 *
	 * @param dateFrom
	 * @param dateTo
	 * @param billItem
	 * @return the bill list
	 * @throws OHServiceException
	 */
	public List<Bill> getBillsBetweenDatesWhereBillItem(LocalDateTime dateFrom, LocalDateTime dateTo, BillItems billItem) throws OHServiceException {
		if (billItem == null) {
			return billRepository.findByDateBetween(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo));
		}
		return billRepository.findAllWhereDatesAndBillItem(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo),
			billItem.getItemDescription());
	}

	/**
	 * Count active {@link Bill}s
	 *
	 * @return the number of recorded {@link Bill}s
	 * @throws OHServiceException
	 */
	public long countAllActiveBills() {
		return this.billRepository.countAllActiveBills();
	}

	/**
	 * Get paginated bills with filters returning {@link Page} (pour le nouveau GUI).
	 *
	 * @param status    the bill status filter
	 * @param dateFrom  the start date filter
	 * @param dateTo    the end date filter
	 * @param patient   the patient filter
	 * @param guarantor the guarantor filter
	 * @param pageable the pagination parameters
	 * @return a {@link Page} of {@link Bill}s matching the filters
	 * @throws OHServiceException if an error occurs retrieving the bills
	 */
	public Page<Bill> getBillsWithFilters(
		String status, LocalDateTime dateFrom, LocalDateTime dateTo,
		Patient patient, User guarantor, Pageable pageable
	) throws OHServiceException {
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;

		return billRepository.findBillsWithFilters(status, from, to, patient, guarantor, pageable);
	}

	/**
	 * Get paginated bills with filters returning {@link List} (pour l'ancien GUI).
	 *
	 * @param status    the bill status filter
	 * @param dateFrom  the start date filter
	 * @param dateTo    the end date filter
	 * @param patient   the patient filter
	 * @param guarantor the guarantor filter
	 * @param limit     the maximum number of results to return
	 * @param offset    the starting index
	 * @return a {@link List} of {@link Bill}s matching the filters
	 * @throws OHServiceException if an error occurs retrieving the bills
	 */
	public List<Bill> getBillsListWithFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo,
											  Patient patient, User guarantor, int limit, int offset) throws OHServiceException {
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;
		Pageable pageable = PageRequest.of(offset / limit, limit);
		Page<Bill> billPage = billRepository.findBillsWithFilters(status, from, to, patient, guarantor, pageable);
		return billPage.getContent();
	}

	/**
	 * Count bills matching the given filters.
	 *
	 * @param status    the bill status filter
	 * @param dateFrom  the start date filter
	 * @param dateTo    the end date filter
	 * @param patient   the patient filter
	 * @param guarantor the guarantor filter
	 * @return the total number of {@link Bill}s matching the filters
	 * @throws OHServiceException if an error occurs counting the bills
	 */
	public long countBillsWithFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo,
									  Patient patient, User guarantor) throws OHServiceException {
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;
		return billRepository.countBillsWithFilters(status, from, to, patient, guarantor);
	}

	/** Check if a patient has pending therapies that haven't been billed yet.
	 *
	 * @param patientCode the patient's code
	 * @return true if the patient has pending therapies, false otherwise
	 * @throws OHServiceException
	 */
	public boolean hasTherapyPrescription(Integer patientCode) throws OHServiceException {
		return therapyManager.hasTherapiesRowsNotYetBought(patientCode);
	}

	/**
	 * Check if a patient has pending exams that haven't been billed yet.
	 *
	 * @param patientCode the patient's code
	 * @return true if the patient has pending exams, false otherwise
	 * @throws OHServiceException
	 */
	public boolean hasExamPrescription(Integer patientCode) throws OHServiceException {
		return labManager.hasLabWithoutBill(String.valueOf(patientCode));
	}

	/**
	 * Check if a patient has pending operations that haven't been billed yet.
	 *
	 * @param patientCode the patient's code
	 * @return true if the patient has pending operations, false otherwise
	 * @throws OHServiceException
	 */
	public boolean hasOperationPrescription(Integer patientCode) throws OHServiceException {
		return operationRowBrowserManager.hasOperationWithoutBill(String.valueOf(patientCode));
	}

	/**
	 * Check if a patient has any pending prescription (therapy, exam, or operation)
	 * that hasn't been billed yet.
	 *
	 * @param patientCode the patient's code
	 * @return true if the patient has any pending prescription, false otherwise
	 * @throws OHServiceException
	 */
	public boolean hasPrescription(Integer patientCode) throws OHServiceException {
		return hasTherapyPrescription(patientCode)
			|| hasExamPrescription(patientCode)
			|| hasOperationPrescription(patientCode);
	}

	/**
	 * Get the bills list of invoices filtered by date, patient and guarantor
	 *
	 * @param dateFrom start date
	 * @param dateTo end date
	 * @param guarantor the user acting as the guarantor for the bills.
	 * @return The {@link List} of invoices
	 * @throws OHServiceException when failed to execute the query.
	 */
	public List<Bill> getBillsByDatesPatientAndGuarantor(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient, User guarantor)
		throws OHServiceException {
		if (patient == null) {
			throw new IllegalArgumentException("Patient cannot be null");
		}

		return billRepository.findByDateBetweenAndBillPatientCodeAndGuarantorUserName(
			TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo),
			patient.getCode(), guarantor.getUserName());
	}

	/**
	 * Get the bills list filter by date and guarantor
	 *
	 * @param dateFrom start date
	 * @param dateTo end date
	 * @param guarantor the user acting as the guarantor for the bills.
	 * @return The {@link List} of invoices
	 * @throws OHServiceException when failed to execute the query.
	 */
	public List<Bill> getBillsByDatesAndGuarantor(LocalDateTime dateFrom, LocalDateTime dateTo, User guarantor) throws OHServiceException {
		return billRepository.findByDateBetweenAndGuarantorUserName(
			TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo),
			guarantor.getUserName());
	}

	/**
	 * Get the bills payments filtered by date, patient and guarantor
	 *
	 * @param dateFrom start date
	 * @param dateTo end date
	 * @param guarantor the user acting as the guarantor for the bills.
	 * @return The {@link List} of{@link BillPayments} matching the filters, or an empty list if no match
	 * @throws OHServiceException when failed to execute the query.
	 */
	public List<BillPayments> getPaymentsByDatesPatientAndGuarantor(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient, User guarantor) throws OHServiceException {
		return billPaymentRepository.findByDateBetweenAndBillBillPatientCodeAndBillGuarantorUserNameOrderByBillAscDateAsc(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo), patient.getCode(), guarantor.getUserName());
	}

	/**
	 * Get the bills payments filtered by date and guarantor
	 *
	 * @param dateFrom start date
	 * @param dateTo end date
	 * @param guarantor the user acting as the guarantor for the bills.
	 * @return The {@link List} of{@link BillPayments} matching the filters, or an empty list if no match
	 * @throws OHServiceException when failed to execute the query.
	 */
	public List<BillPayments> getPaymentsByDatesAndGuarantor(LocalDateTime dateFrom, LocalDateTime dateTo, User guarantor) throws OHServiceException {
		return billPaymentRepository.findByDateBetweenAndBillGuarantorUserNameOrderByBillAscDateAsc(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo), guarantor.getUserName());
	}

	/**
	 * Get the bills payments filtered by guarantor
	 *
	 * @param guarantor the user acting as the guarantor for the bills.
	 * @return The {@link List} of{@link BillPayments} matching the filters, or an empty list if no match
	 * @throws OHServiceException when failed to execute the query.
	 */
	public List<Bill> getBillsByGuarantor(List<BillPayments> payments, User guarantor) throws OHServiceException {
		Set<Bill> bills = new TreeSet<>((o1, o2) -> o1.getId() == o2.getId() ? 0 : -1);
		for (BillPayments bp : payments) {
			Bill bill = bp.getBill();

			if (bill.getGuarantor().equals(guarantor)) {
				bills.add(bill);
			}
		}
		return new ArrayList<>(bills);
	}

	/**
	 * Retrieves all items of a bill (including those from refund bills)
	 * @param bill the bill
	 * @return complete list of items with quantities inverted for refunds
	 * @throws OHServiceException
	 */
	public List<BillItems> getAllBillItems(Bill bill) throws OHServiceException {
		if (bill == null || bill.getId() == 0) {
			return new ArrayList<>();
		}

		List<BillItems> allItems = new ArrayList<>();

		List<BillItems> mainItems = billItemsRepository.findByBillIdOrderByItemDateAsc(bill.getId());
		allItems.addAll(mainItems);

		List<BillItems> refundItems = billItemsRepository.findByBillParentIdOrderByItemDateAsc(bill.getId());

		for (BillItems refundItem : refundItems) {
			refundItem.setItemQuantity(-refundItem.getItemQuantity());
			allItems.add(refundItem);
		}

		allItems.sort((a, b) -> {
			if (a.getItemDate() == null || b.getItemDate() == null) return 0;
			return a.getItemDate().compareTo(b.getItemDate());
		});

		return allItems;
	}

	/**
	 * Retrieves all payments of a bill (including those from refund bills)
	 * @param bill the bill
	 * @return complete list of payments
	 * @throws OHServiceException
	 */
	public List<BillPayments> getAllBillPayments(Bill bill) throws OHServiceException {
		if (bill == null || bill.getId() == 0) {
			return new ArrayList<>();
		}

		List<BillPayments> allPayments = new ArrayList<>();

		List<BillPayments> mainPayments = billPaymentRepository.findByBillIdOrderByDateAsc(bill.getId());
		allPayments.addAll(mainPayments);

		List<BillPayments> refundPayments = billPaymentRepository.findByBillParentIdOrderByDateAsc(bill.getId());
		allPayments.addAll(refundPayments);

		allPayments.sort((a, b) -> {
			if (a.getDate() == null || b.getDate() == null) return 0;
			return a.getDate().compareTo(b.getDate());
		});

		return allPayments;
	}

	/**
	 * Gets the price of an item directly from the database.
	 *
	 * @param itemId the item code
	 * @param group the item group (MED, EXA, OPE, OTH)
	 * @param patient the patient
	 * @return the Price with reductions applied, or null if not found
	 * @throws OHServiceException
	 */
	public Price getPrice(String itemId, ItemGroup group, Patient patient) throws OHServiceException {
		// Get the first available price list as default
		List<PriceList> lists = billRepository.findDistinctPriceLists();
		if (lists == null || lists.isEmpty()) {
			return null;
		}

		Integer listId = lists.get(0).getId();

		// Get the base price
		Double basePrice = billRepository.findPriceByListIdAndGroupAndItem(listId, group.getCode(), itemId);
		if (basePrice == null) {
			return null;
		}

		double finalPrice = basePrice;

		// TODO: add reduction plans logic here later
		// For now, return the base price without reductions

		// Create and return Price object
		Price price = new Price();
		price.setPrice(finalPrice);
		price.setItem(itemId);
		price.setGroup(group.getCode());

		return price;
	}

	/**
	 * Gets the gross price (without reductions) from the database.
	 *
	 * @param itemId the item code
	 * @param group the item group
	 * @param patient the patient
	 * @return the Price with gross price, or null if not found
	 * @throws OHServiceException
	 */
	public Price getPriceFromListWithoutReduction(String itemId, ItemGroup group, Patient patient) throws OHServiceException {
		// Get the first available price list as default
		List<PriceList> lists = billRepository.findDistinctPriceLists();
		if (lists == null || lists.isEmpty()) {
			return null;
		}

		Integer listId = lists.get(0).getId();

		// Get the base price without reductions
		Double basePrice = billRepository.findPriceByListIdAndGroupAndItem(listId, group.getCode(), itemId);
		if (basePrice == null) {
			return null;
		}

		Price price = new Price();
		price.setPrice(basePrice);
		price.setItem(itemId);
		price.setGroup(group.getCode());

		return price;
	}

	/**
	 * Checks if a specific prescription item is already in a closed (paid) bill for this patient.
	 *
	 * @param patientCode    the patient's code
	 * @param prescriptionId the prescription ID (therapyID, lab.code, op.id)
	 * @param itemGroup      the item group ("MED", "EXA", "OPE")
	 * @return true if already billed and paid
	 */
	public boolean isPrescriptionAlreadyBilledAndPaid(
		Integer patientCode,
		Integer prescriptionId,
		String itemGroup) throws OHServiceException {
		if (prescriptionId == null) {
			return false;
		}
		return billItemsRepository.existsByPatientAndPrescriptionInClosedBill(
			patientCode, prescriptionId, itemGroup);
	}
}
