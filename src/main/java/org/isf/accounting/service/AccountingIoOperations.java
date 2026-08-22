/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.patient.model.Patient;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	public AccountingIoOperations(AccountingBillIoOperationRepository accountingBillIoOperationRepository,
		AccountingBillPaymentIoOperationRepository accountingBillPaymentIoOperationRepository,
		AccountingBillItemsIoOperationRepository accountingBillItemsIoOperationRepository) {
		this.billRepository = accountingBillIoOperationRepository;
		this.billPaymentRepository = accountingBillPaymentIoOperationRepository;
		this.billItemsRepository = accountingBillItemsIoOperationRepository;
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
	 * Checks whether the specified prescription source record is already linked to a
	 * {@link BillItems} on a closed bill for the given patient.
	 *
	 * @param patientCode the patient's code.
	 * @param prescriptionId the prescription source record's id.
	 * @param itemGroup the prescription source's item group ("MED"/"EXA"/"OPE").
	 * @return {@code true} if already billed on a closed bill.
	 * @throws OHServiceException
	 */
	public boolean existsBilledOnClosedBill(int patientCode, int prescriptionId, String itemGroup) throws OHServiceException {
		return billItemsRepository.existsBilledOnClosedBill(patientCode, prescriptionId, itemGroup);
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
		billItemsRepository.deleteWhereId(deleteBill.getId());
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
	 * Fetches one page of {@link Bill}s in the given date range, optionally narrowed by status, patient
	 * and cashier username (each predicate is skipped when its parameter is {@code null}).
	 *
	 * @param dateFrom the low date range endpoint, inclusive.
	 * @param dateTo the high date range endpoint, inclusive.
	 * @param status the bill status to filter by, or {@code null} for any status.
	 * @param patientCode the patient to filter by, or {@code null} for any patient.
	 * @param username the cashier username to filter by, or {@code null} for any cashier.
	 * @param pageable the requested page and size.
	 * @return the requested {@link Page} of {@link Bill}s.
	 * @throws OHServiceException if an error occurs retrieving the bills.
	 */
	public Page<Bill> getBillsPageable(LocalDateTime dateFrom, LocalDateTime dateTo, String status, Integer patientCode,
			String username, Pageable pageable) throws OHServiceException {
		return billRepository.findPageable(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo),
			status, patientCode, username, pageable);
	}

	/**
	 * Sums the outstanding balance of every non-deleted {@link Bill} in the given date range, optionally
	 * narrowed by patient - independent of status/page, this backs the totals footer which reflects the
	 * whole filtered date range regardless of which page or tab is currently displayed.
	 *
	 * @param dateFrom the low date range endpoint, inclusive.
	 * @param dateTo the high date range endpoint, inclusive.
	 * @param patientCode the patient to filter by, or {@code null} for any patient.
	 * @return the summed balance.
	 * @throws OHServiceException if an error occurs computing the sum.
	 */
	public BigDecimal getBalanceTotal(LocalDateTime dateFrom, LocalDateTime dateTo, Integer patientCode) throws OHServiceException {
		return billRepository.sumBalance(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo), patientCode);
	}

	/**
	 * Sums payment amounts in the given date range, optionally narrowed by patient and/or cashier
	 * username, excluding payments on deleted bills.
	 *
	 * @param dateFrom the low date range endpoint, inclusive.
	 * @param dateTo the high date range endpoint, inclusive.
	 * @param patientCode the patient to filter by, or {@code null} for any patient.
	 * @param username the cashier username to filter by, or {@code null} for all cashiers.
	 * @return the summed payment amount.
	 * @throws OHServiceException if an error occurs computing the sum.
	 */
	public BigDecimal getPaymentsTotal(LocalDateTime dateFrom, LocalDateTime dateTo, Integer patientCode, String username) throws OHServiceException {
		return billPaymentRepository.sumPayments(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo), patientCode, username);
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

}
