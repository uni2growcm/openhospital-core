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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	public List<Bill> getPendingBills(int patID) throws OHServiceException {
		if (patID != 0) {
			return billRepository.findByStatusAndBillPatientCodeOrderByDateDesc("O", patID);
		}
		return billRepository.findByStatusOrderByDateDesc("O");
	}

	public List<Bill> getBills() throws OHServiceException {
		return billRepository.findAllByOrderByDateDesc();
	}

	public Bill getBill(int billID) throws OHServiceException {
		return billRepository.findById(billID).orElse(null);
	}

	public List<String> getUsers() throws OHServiceException {
		Set<String> accountingUsers = new TreeSet<>(String::compareTo);
		accountingUsers.addAll(billRepository.findUserDistinctByOrderByUserAsc());
		accountingUsers.addAll(billPaymentRepository.findUserDistinctByOrderByUserAsc());
		return new ArrayList<>(accountingUsers);
	}

	public List<BillItems> getItems(int billID) throws OHServiceException {
		if (billID != 0) {
			return billItemsRepository.findByBill_idOrderByIdAsc(billID);
		}
		return billItemsRepository.findAllByOrderByIdAsc();
	}

	public List<BillPayments> getPayments(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return billPaymentRepository.findByDateBetweenOrderByIdAscDateAsc(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo));
	}

	public List<BillPayments> getPayments(int billID) throws OHServiceException {
		if (billID != 0) {
			return billPaymentRepository.findAllWherBillIdByOrderByBillAndDate(billID);
		}
		return billPaymentRepository.findAllByOrderByBillAndDate();
	}

	public Bill newBill(Bill newBill) throws OHServiceException {
		return billRepository.save(newBill);
	}

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

	public void newBillPayments(Bill bill, List<BillPayments> payItems) throws OHServiceException {
		billPaymentRepository.deleteWhereId(bill.getId());
		for (BillPayments payment : payItems) {
			payment.setBill(bill);
			payment.setId(0);
			billPaymentRepository.save(payment);
		}
	}

	public Bill updateBill(Bill updateBill) throws OHServiceException {
		return billRepository.save(updateBill);
	}

	public void deleteBill(Bill deleteBill) throws OHServiceException {
		billRepository.deleteById(deleteBill.getId());
	}

	public List<Bill> getBillsBetweenDates(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return billRepository.findByDateBetween(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo));
	}

	public List<Bill> getBills(List<BillPayments> payments) throws OHServiceException {
		Set<Bill> bills = new TreeSet<>((o1, o2) -> o1.getId() == o2.getId() ? 0 : -1);
		for (BillPayments bp : payments) {
			bills.add(bp.getBill());
		}
		return new ArrayList<>(bills);
	}

	public List<BillPayments> getPayments(List<Bill> bills) throws OHServiceException {
		return billPaymentRepository.findAllByBillIn(bills);
	}

	public List<BillPayments> getPaymentsBetweenDatesWherePatient(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient)
		throws OHServiceException {
		return billPaymentRepository.findByDateAndPatient(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo), patient.getCode());
	}

	public List<Bill> getBillsBetweenDatesWherePatient(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient) throws OHServiceException {
		return billRepository.findByDateAndPatient(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo), patient.getCode());
	}

	public List<Bill> getPendingBillsAffiliate(int patID) throws OHServiceException {
		return billRepository.findAllPendindBillsByBillPatient(patID);
	}

	public List<Bill> getAllPatientsBills(int patID) throws OHServiceException {
		return billRepository.findByBillPatientCode(patID);
	}

	public List<BillItems> getDistictsBillItems() throws OHServiceException {
		return billItemsRepository.findAllGroupByDescription();
	}

	public List<Bill> getBillsBetweenDatesWhereBillItem(LocalDateTime dateFrom, LocalDateTime dateTo, BillItems billItem) throws OHServiceException {
		if (billItem == null) {
			return billRepository.findByDateBetween(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo));
		}
		return billRepository.findAllWhereDatesAndBillItem(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo),
			billItem.getItemDescription());
	}

	public long countAllActiveBills() {
		return this.billRepository.countAllActiveBills();
	}

	public Page<Bill> getBillsWithFilters(
		String status, LocalDateTime dateFrom, LocalDateTime dateTo,
		Patient patient, User guarantor, Pageable pageable
	) throws OHServiceException {
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;
		return billRepository.findBillsWithFilters(status, from, to, patient, guarantor, pageable);
	}

	public List<Bill> getBillsListWithFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                          Patient patient, User guarantor, int limit, int offset) throws OHServiceException {
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;
		Pageable pageable = PageRequest.of(offset / limit, limit);
		Page<Bill> billPage = billRepository.findBillsWithFilters(status, from, to, patient, guarantor, pageable);
		return billPage.getContent();
	}

	public long countBillsWithFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                  Patient patient, User guarantor) throws OHServiceException {
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;
		return billRepository.countBillsWithFilters(status, from, to, patient, guarantor);
	}

	public boolean hasTherapyPrescription(Integer patientCode) throws OHServiceException {
		return therapyManager.hasTherapiesRowsNotYetBought(patientCode);
	}

	public boolean hasExamPrescription(Integer patientCode) throws OHServiceException {
		return labManager.hasLabWithoutBill(String.valueOf(patientCode));
	}

	public boolean hasOperationPrescription(Integer patientCode) throws OHServiceException {
		return operationRowBrowserManager.hasOperationWithoutBill(String.valueOf(patientCode));
	}

	public boolean hasPrescription(Integer patientCode) throws OHServiceException {
		return hasTherapyPrescription(patientCode)
			|| hasExamPrescription(patientCode)
			|| hasOperationPrescription(patientCode);
	}

	public List<Bill> getBillsByDatesPatientAndGuarantor(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient, User guarantor)
		throws OHServiceException {
		if (patient == null) {
			throw new IllegalArgumentException("Patient cannot be null");
		}
		return billRepository.findByDateBetweenAndBillPatientCodeAndGuarantorUserName(
			TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo),
			patient.getCode(), guarantor.getUserName());
	}

	public List<Bill> getBillsByDatesAndGuarantor(LocalDateTime dateFrom, LocalDateTime dateTo, User guarantor) throws OHServiceException {
		return billRepository.findByDateBetweenAndGuarantorUserName(
			TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo),
			guarantor.getUserName());
	}

	public List<BillPayments> getPaymentsByDatesPatientAndGuarantor(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient, User guarantor) throws OHServiceException {
		return billPaymentRepository.findByDateBetweenAndBillBillPatientCodeAndBillGuarantorUserNameOrderByBillAscDateAsc(
			TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo),
			patient.getCode(), guarantor.getUserName());
	}

	public List<BillPayments> getPaymentsByDatesAndGuarantor(LocalDateTime dateFrom, LocalDateTime dateTo, User guarantor) throws OHServiceException {
		return billPaymentRepository.findByDateBetweenAndBillGuarantorUserNameOrderByBillAscDateAsc(
			TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo),
			guarantor.getUserName());
	}

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

	public Price getPrice(String itemId, ItemGroup group, Patient patient) throws OHServiceException {
		List<PriceList> lists = billRepository.findDistinctPriceLists();
		if (lists == null || lists.isEmpty()) {
			return null;
		}
		Integer listId = lists.get(0).getId();
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

	public Price getPriceFromListWithoutReduction(String itemId, ItemGroup group, Patient patient) throws OHServiceException {
		List<PriceList> lists = billRepository.findDistinctPriceLists();
		if (lists == null || lists.isEmpty()) {
			return null;
		}
		Integer listId = lists.get(0).getId();
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

	public boolean isPrescriptionAlreadyBilledAndPaid(Integer patientCode, Integer prescriptionId, String itemGroup) throws OHServiceException {
		if (prescriptionId == null) {
			return false;
		}
		return billItemsRepository.existsByPatientAndPrescriptionInClosedBill(patientCode, prescriptionId, itemGroup);
	}
}