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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItemGroup;
import org.isf.accounting.model.BillItemGroupItem;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.accounting.model.ItemPayments;
import org.isf.generaldata.MessageBundle;
import org.isf.generaldata.SageConfig;
import org.isf.lab.manager.LabManager;
import org.isf.menu.model.User;
import org.isf.operation.manager.OperationRowBrowserManager;
import org.isf.partner.model.Partner;
import org.isf.patient.model.Patient;
import org.isf.priceslist.model.ItemGroup;
import org.isf.priceslist.model.Price;
import org.isf.priceslist.model.PriceList;
import org.isf.therapy.manager.TherapyManager;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
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
	private BillItemGroupIoOperationRepository billItemGroupRepository;
	private BillItemGroupItemIoOperationRepository billItemGroupItemRepository;
	private AccountingItemPaymentIoOperationRepository itemPaymentRepository;

	private TherapyManager therapyManager;
	private LabManager labManager;
	private OperationRowBrowserManager operationRowBrowserManager;

	public AccountingIoOperations(
		AccountingBillIoOperationRepository accountingBillIoOperationRepository,
		AccountingBillPaymentIoOperationRepository accountingBillPaymentIoOperationRepository,
		AccountingBillItemsIoOperationRepository accountingBillItemsIoOperationRepository,
		BillItemGroupIoOperationRepository billItemGroupRepository,
		BillItemGroupItemIoOperationRepository billItemGroupItemRepository,
		AccountingItemPaymentIoOperationRepository accountingItemPaymentIoOperationRepository,
		TherapyManager therapyManager,
		LabManager labManager,
		OperationRowBrowserManager operationRowBrowserManager
	) {
		this.billRepository = accountingBillIoOperationRepository;
		this.billPaymentRepository = accountingBillPaymentIoOperationRepository;
		this.billItemsRepository = accountingBillItemsIoOperationRepository;
		this.billItemGroupRepository = billItemGroupRepository;
		this.billItemGroupItemRepository = billItemGroupItemRepository;
		this.itemPaymentRepository = accountingItemPaymentIoOperationRepository;
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
	 * Returns all refund bills linked to the given bill (i.e. bills whose parentId equals billId).
	 */
	public List<Bill> getRefundBills(int billId) throws OHServiceException {
		return billRepository.findByParentId(billId);
	}

	/**
	 * Returns all items stored under refund bills linked to the given bill.
	 * Each item's quantity represents how many units were refunded in that particular refund bill.
	 */
	public List<BillItems> getRefundedItems(int billId) throws OHServiceException {
		return billItemsRepository.findByBillParentIdOrderByItemDateAsc(billId);
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
	 * Add a new billItemGroup, ensuring the title is unique.
	 * If billItemGroup contains items, they will be automatically persisted due to cascade.
	 *
	 * @param billItemGroup the BillItemGroup to add
	 * @return the added billItemGroup with generated id
	 * @throws OHServiceException when fails to add or when title already exists
	 */
	public BillItemGroup addBillItemGroup(BillItemGroup billItemGroup) throws OHServiceException {
		if (billItemGroupRepository.existsByTitle(billItemGroup.getTitle())) {
			throw new OHDataValidationException(
				new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.billgroupduplicatetitle")));
		}
		// Ensure each item has reference to the group
		if (billItemGroup.getItems() != null) {
			for (BillItemGroupItem item : billItemGroup.getItems()) {
				if (item.getBillItemGroup() == null) {
					item.setBillItemGroup(billItemGroup);
				}
			}
		}
		return billItemGroupRepository.save(billItemGroup);
	}

	/**
	 * Update a billItemGroup with its items.
	 * This method syncs the items list with the database - removing items not in the new list
	 * and adding/updating items that are present.
	 *
	 * @param billItemGroup the BillItemGroup to update (may include updated items list)
	 * @return the updated billItemGroup
	 * @throws OHServiceException when fails to update billItemGroup
	 */
	public BillItemGroup updateBillItemGroup(BillItemGroup billItemGroup) throws OHServiceException {
		if (billItemGroupRepository.existsByTitleAndIdNot(billItemGroup.getTitle(), billItemGroup.getId())) {
			throw new OHDataValidationException(
				new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.billitemgroupduplicatetitle")));
		}
		
		// Sync items: get current items from DB and compare with provided items
		List<BillItemGroupItem> currentItems = billItemGroupItemRepository.findByBillItemGroup_idOrderByIdAsc(billItemGroup.getId());
		List<BillItemGroupItem> newItems = billItemGroup.getItems();
		
		if (newItems != null) {
			// Remove items that are not in the new list
			for (BillItemGroupItem currentItem : currentItems) {
				boolean found = newItems.stream().anyMatch(item -> item.getId() == currentItem.getId());
				if (!found) {
					billItemGroupItemRepository.delete(currentItem);
				}
			}
			
			// Ensure each new item references the group
			for (BillItemGroupItem item : newItems) {
				if (item.getBillItemGroup() == null) {
					item.setBillItemGroup(billItemGroup);
				}
			}
		}
		
		return billItemGroupRepository.save(billItemGroup);
	}

	/**
	 * Update a BillItemGroup with a new list of items.
	 * Handles lazy initialization by working directly via repositories.
	 *
	 * @param group    the BillItemGroup to update
	 * @param newItems the new list of items
	 * @return the updated BillItemGroup
	 * @throws OHServiceException when fails to update
	 */
	@Transactional
	public BillItemGroup updateBillItemGroupWithItems(BillItemGroup group, List<BillItemGroupItem> newItems) throws OHServiceException {
		if (billItemGroupRepository.existsByTitleAndIdNot(group.getTitle(), group.getId())) {
			throw new OHDataValidationException(
				new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.billitemgroupduplicatetitle")));
		}

		billItemGroupItemRepository.deleteByGroupId(group.getId());
		billItemGroupItemRepository.flush();

		BillItemGroup managed = billItemGroupRepository.findById(group.getId())
			.orElseThrow(() -> new OHDataValidationException(
				new OHExceptionMessage("Group not found")));

		managed.setTitle(group.getTitle());
		managed.setDescription(group.getDescription());
		managed.setTotal(group.getTotal());

		for (BillItemGroupItem item : newItems) {
			item.setId(0);
			managed.addItem(item);
		}

		return billItemGroupRepository.save(managed);
	}

	/**
	 * Delete a billItemGroup and all its associated items (cascade delete)
	 *
	 * @param groupId the id of the BillItemGroup to delete
	 * @throws OHServiceException when fails to delete billItemGroup
	 */
	public void deleteBillItemGroup(int groupId) throws OHServiceException {
		// Items will be automatically deleted due to CascadeType.ALL and orphanRemoval=true
		billItemGroupRepository.deleteById(groupId);
	}

	/**
	 * Get a single BillItemGroup by ID
	 *
	 * @param id the ID of the bill item group
	 * @return the matching BillItemGroup, or null if not found
	 * @throws OHServiceException if a database error occurs
	 */
	public BillItemGroup getBillItemGroupById(int id) throws OHServiceException {
		return billItemGroupRepository.findById(id).orElse(null);
	}

	/**
	 * Get all billItemGroup
	 *
	 * @return the list of all billItemGroup stored in db
	 * @throws OHServiceException when fails to fetch list
	 */
	public List<BillItemGroup> getAllBillItemGroups() throws OHServiceException {
		return billItemGroupRepository.findAllByOrderByCreatedDateDesc();
	}

	/**
	 * Get all active billItemGroups
	 *
	 * @return the list of all active billItemGroup stored in db
	 * @throws OHServiceException when fails to fetch list
	 */
	public List<BillItemGroup> getAllActiveBillItemGroups() throws OHServiceException {
		return billItemGroupRepository.findAllActive();
	}

	/**
	 * Check if new billItemGroup has duplicate to prevent creation
	 *
	 * @param title the title of the billItemGroup to add
	 * @return true if duplicate is found
	 * @throws OHServiceException when fails to check if duplicate exist
	 */
	public boolean existsBillItemGroupWithTitle(String title) throws OHServiceException {
		return billItemGroupRepository.existsByTitle(title);
	}

	/**
	 * Add billItemGroupItems to a billItemGroup
	 *
	 * @param groupId the id of the billItemGroup
	 * @param items the BillItemGroupItems to add
	 * @throws OHServiceException when fails to add billItemGroupItems to billItemGroup
	 */
	public void addBillItemGroupItems(int groupId, List<BillItemGroupItem> items) throws OHServiceException {
		if (items != null && !items.isEmpty()) {
			for (BillItemGroupItem item : items) {
				billItemGroupItemRepository.save(item);
			}
		}
	}

	/**
	 * Delete all billItemGroupItems associated to a billItemGroup
	 *
	 * @param groupId the id of the BillItemGroup whose items are to be deleted
	 * @throws OHServiceException when fails to delete billItemGroupItems
	 */
	public void deleteBillItemGroupItems(int groupId) throws OHServiceException {
		billItemGroupItemRepository.deleteByGroupId(groupId);
	}

	/**
	 * Get a billItemGroup items
	 *
	 * @param groupId the id of the BillItemGroup whose items are to be retrieved
	 * @return the list of all billItemGroupItems for a given billItemGroup stored in db
	 * @throws OHServiceException when fails to fetch list
	 */
	public List<BillItemGroupItem> getItemsByGroupId(int groupId) throws OHServiceException {
		return billItemGroupItemRepository.findByBillItemGroup_idOrderByIdAsc(groupId);
	}

	/**
	 * Get all billItemGroupItems
	 *
	 * @return the list of all billItemGroupItems stored in db
	 * @throws OHServiceException when fails to fetch list
	 */
	public List<BillItemGroupItem> getAllBillItemGroupItems() throws OHServiceException {
		return billItemGroupItemRepository.findAllByOrderByIdAsc();
	}

	/**
	 * Get a single BillItemGroupItem by ID
	 *
	 * @param id the ID of the bill item group item
	 * @return the matching BillItemGroupItem, or null if not found
	 * @throws OHServiceException if a database error occurs
	 */
	public BillItemGroupItem getBillItemGroupItemById(int id) throws OHServiceException {
		return billItemGroupItemRepository.findById(id).orElse(null);
	}

	/**
	 * Update a billItemGroupItem
	 *
	 * @param item the BillItemGroupItem to update
	 * @return the updated billItemGroupItem
	 * @throws OHServiceException when fails to update billItemGroupItem
	 */
	public BillItemGroupItem updateBillItemGroupItem(BillItemGroupItem item) throws OHServiceException {
		return billItemGroupItemRepository.save(item);
	}

	/**
	 * Delete a billItemGroupItem
	 *
	 * @param itemId the id of the BillItemGroupItem to delete
	 * @throws OHServiceException when fails to delete billItemGroupItem
	 */
	public void deleteBillItemGroupItem(int itemId) throws OHServiceException {
		billItemGroupItemRepository.deleteById(itemId);
	}

	/**
	 * Retrieves all item payments for a given bill.
	 *
	 * @param billId the bill ID
	 * @return the list of item payments
	 * @throws OHServiceException
	 */
	public List<ItemPayments> getItemPayments(int billId) throws OHServiceException {
		if (billId != 0) {
			return itemPaymentRepository.findByBillIdOrderByIdAsc(billId);
		}
		return itemPaymentRepository.findAllByOrderByIdAsc();
	}

	/**
	 * Creates new item payments for a bill, replacing any existing ones.
	 *
	 * @param bill the bill
	 * @param itemPayments the list of item payments to save
	 * @throws OHServiceException
	 */
	public void newItemPayments(Bill bill, List<ItemPayments> itemPayments) throws OHServiceException {
		itemPaymentRepository.deleteWhereBillId(bill.getId());
		for (ItemPayments itemPayment : itemPayments) {
			itemPayment.setBill(bill);
			itemPayment.setId(0);
			if (itemPayment.getDate() == null) {
				itemPayment.setDate(LocalDateTime.now());
			}
			itemPaymentRepository.save(itemPayment);
		}
	}

	/**
	 * Retrieves all item payments for a given bill and item.
	 *
	 * @param itemId the item ID
	 * @param billId the bill ID
	 * @return the list of item payments
	 * @throws OHServiceException
	 */
	public List<ItemPayments> getItemPaymentsByItemId(String itemId, int billId) throws OHServiceException {
		return itemPaymentRepository.findByItemIdAndBillIdOrderByIdAsc(itemId, billId);
	}

	/**
	 * Deletes all item payments for a given bill.
	 *
	 * @param billId the bill ID
	 * @throws OHServiceException
	 */
	public void deleteItemPaymentsByBillId(int billId) throws OHServiceException {
		itemPaymentRepository.deleteWhereBillId(billId);
	}

	/**
	 * Count all billItemGroups
	 *
	 * @return the count of all billItemGroups
	 * @throws OHServiceException when fails
	 */
	public long countAllBillItemGroups() throws OHServiceException {
		return billItemGroupRepository.count();
	}

	/**
	 * Count active billItemGroups
	 *
	 * @return the count of active billItemGroups
	 * @throws OHServiceException when fails
	 */
	public long countAllActiveBillItemGroups() throws OHServiceException {
		return billItemGroupRepository.countAllActive();
	}

	/**
	 * Count items for a specific billItemGroup
	 *
	 * @param groupId the billItemGroup id
	 * @return the count of items
	 * @throws OHServiceException when fails
	 */
	public long countItemsByGroupId(int groupId) throws OHServiceException {
		return billItemGroupItemRepository.countByGroupId(groupId);
	}

	/** Gets the price of an item directly from the database.
	 *
	 * @param itemId the item code
	 * @param group the item group (MED, EXA, OPE, OTH)
	 * @param patient the patient
	 * @return the Price with reductions applied, or null if not found
	 * @throws OHServiceException
	 */
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
		return billItemsRepository.existsByPatientAndPrescriptionInClosedBill(
		patientCode, prescriptionId, itemGroup);
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
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;
		return billRepository.sumAmountByFilters(status, from, to, patient, guarantor);
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
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;
		return billRepository.sumBalanceByFilters(status, from, to, patient, guarantor);
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
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;
		return billPaymentRepository.sumPaymentsByFilters(from, to, patient, guarantor);
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
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;
		return billPaymentRepository.sumPaymentsByUserAndFilters(username, from, to, patient, guarantor);
	}

	/**
	 * Retrieves all payments within the specified date range for Sage export.
	 *
	 * @param dateFrom the start date (inclusive)
	 * @param dateTo the end date (exclusive)
	 * @return a list of {@link BillPayments} within the specified date range
	 * @throws OHServiceException if an error occurs during database access
	 */
	public List<BillPayments> getPaymentsForSage(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return billPaymentRepository.findPaymentsForSage(
			TimeTools.getBeginningOfDay(dateFrom),
			TimeTools.getBeginningOfNextDay(dateTo)
		);
	}

	/**
	 * Retrieves all bills within the specified date range for Sage export.
	 *
	 * @param dateFrom the start date (inclusive)
	 * @param dateTo the end date (exclusive)
	 * @return a list of {@link Bill} within the specified date range
	 * @throws OHServiceException if an error occurs during database access
	 */
	public List<Bill> getBillsForSage(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return billRepository.findBillsForSage(
			TimeTools.getBeginningOfDay(dateFrom),
			TimeTools.getBeginningOfNextDay(dateTo)
		);
	}

	/**
	 * Exports payments to a text file in Sage-compatible format.
	 *
	 * @param file the destination file to write the export data
	 * @param dateFrom the start date (inclusive)
	 * @param dateTo the end date (exclusive)
	 * @return {@code true} if the export completed successfully
	 * @throws OHServiceException if an error occurs during database access
	 * @throws IOException if an I/O error occurs while writing to the file
	 */
	public boolean exportSagePayments(File file, LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException, IOException {
		List<BillPayments> payments = getPaymentsForSage(dateFrom, dateTo);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			for (BillPayments payment : payments) {
				writer.write(formatSagePaymentLine(payment, true));
				writer.newLine();
				writer.write(formatSagePaymentLine(payment, false));
				writer.newLine();
			}
		}
		return true;
	}

	/**
	 * Exports bills to a text file in Sage-compatible format.
	 *
	 * @param file the destination file to write the export data
	 * @param dateFrom the start date (inclusive)
	 * @param dateTo the end date (exclusive)
	 * @return {@code true} if the export completed successfully
	 * @throws OHServiceException if an error occurs during database access
	 * @throws IOException if an I/O error occurs while writing to the file
	 */
	public boolean exportSageBills(File file, LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException, IOException {
		return exportSagePayments(file, dateFrom, dateTo);
	}

	/**
	 * Formats a single payment into a Sage-compatible line.
	 *
	 * @param payment the payment to format
	 * @param isDebit {@code true} for debit line (cash account), {@code false} for credit line (customer account)
	 * @return a formatted string ready for Sage import
	 */
	private String formatSagePaymentLine(BillPayments payment, boolean isDebit) {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("ddMMyy");
		String journalCode = "CASH3";
		String reference = fmt.format(payment.getDate()) + "C" + payment.getBill().getId();
		String patientName = getPatientName(payment.getBill());
		String libelle = journalCode + "-" + reference + "-" + patientName;
		String amount = String.format("%.2f", payment.getAmount()).replace('.', ',');

		if (isDebit) {
			return String.format("%-5s %-13s %-10s %-40s %10s %10s",
				journalCode, reference, SageConfig.CASH_ACCOUNT, libelle, amount, "0,00");
		} else {
			return String.format("%-5s %-13s %-10s %-40s %10s %10s",
				journalCode, reference, SageConfig.CUSTOMER_GENERAL_ACCOUNT, libelle, "0,00", amount);
		}
	}

	/**
	 * Extracts and sanitizes the patient name from a bill for Sage export.
	 *
	 * @param bill the bill containing the patient information
	 * @return the sanitized patient name in uppercase, or "PATIENT_INCONNU" if no patient is associated
	 */
	private String getPatientName(Bill bill) {
		if (bill.getBillPatient() != null && bill.getBillPatient().getName() != null) {
			String name = bill.getBillPatient().getName().toUpperCase();
			name = name.replace(' ', '_');
			name = name.replace("'", "");
			name = name.replace("-", "_");
			name = name.replace(".", "");
			name = name.replace(",", "");
			return name;
		}
		return "PATIENT_INCONNU";
	}

	/**
	 * Formats a payment for testing purposes (debit line only).
	 *
	 * @param payment the payment to format
	 * @return the formatted debit line string
	 */
	public String formatSagePaymentForTest(BillPayments payment) {
		return formatSagePaymentLine(payment, true);
	}

	/**
	 * Formats a bill for testing purposes in Sage-compatible format.
	 *
	 * @param bill the bill to format
	 * @return a formatted test string with semicolon separators
	 */
	public String formatSageBillForTest(Bill bill) {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("ddMMyy");
		String journalCode = "CASH3";
		String reference = fmt.format(bill.getDate()) + "C" + bill.getId();
		String patientName = getPatientName(bill);
		String libelle = journalCode + "-" + reference + "-" + patientName;
		String amount = String.format("%.2f", bill.getAmount()).replace('.', ',');

		return journalCode + ";" + reference + ";" + SageConfig.CUSTOMER_GENERAL_ACCOUNT + ";" +
			libelle + ";0,00;" + amount;
	}

	/**
	 * Exports payments to a text file using streaming to minimize memory usage.
	 *
	 * @param file the destination file to write the export data
	 * @param dateFrom the start date (inclusive)
	 * @param dateTo the end date (exclusive)
	 * @throws IOException if an I/O error occurs while writing to the file
	 * @see #exportSagePayments(File, LocalDateTime, LocalDateTime) for non-streaming version
	 */
	@Transactional(readOnly = true)
	public void exportSagePaymentsStreaming(File file, LocalDateTime dateFrom, LocalDateTime dateTo) throws IOException {
		LocalDateTime from = TimeTools.getBeginningOfDay(dateFrom);
		LocalDateTime to = TimeTools.getBeginningOfNextDay(dateTo);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		     Stream<BillPayments> stream = billPaymentRepository.streamPaymentsForSage(from, to)) {

			stream.forEach(payment -> {
				try {
					writer.write(formatSagePaymentLine(payment, true));
					writer.newLine();
					writer.write(formatSagePaymentLine(payment, false));
					writer.newLine();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}

	/**
	 * Retrieves a paginated list of bills filtered by status, date range,
	 * patient, guarantor, and partner.
	 *
	 * @param status the status of the bills to filter by
	 * @param dateFrom the start date of the billing period (inclusive)
	 * @param dateTo the end date of the billing period (inclusive)
	 * @param patient the patient associated with the bills
	 * @param guarantor the guarantor associated with the bills
	 * @param partner the partner associated with the bills
	 * @param pageable pagination information
	 * @return a page of filtered {@link Bill}s
	 * @throws OHServiceException if an error occurs while retrieving the bills
	 */
	public Page<Bill> getBillsWithFilters(
		String status,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		Patient patient,
		User guarantor,
		Partner partner,
		Pageable pageable
	) throws OHServiceException {
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;
		return billRepository.findBillsWithFilters(status, from, to, patient, guarantor, partner, pageable);
	}

	/**
	 * Counts the number of bills matching the given filters.
	 *
	 * @param status the status of the bills to filter by
	 * @param dateFrom the start date of the billing period (inclusive)
	 * @param dateTo the end date of the billing period (inclusive)
	 * @param patient the patient associated with the bills
	 * @param guarantor the guarantor associated with the bills
	 * @param partner the partner associated with the bills
	 * @return the total number of matching {@link Bill}s
	 * @throws OHServiceException if an error occurs while counting the bills
	 */
	public long countBillsWithFilters(
		String status,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		Patient patient,
		User guarantor,
		Partner partner
	) throws OHServiceException {
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;
		return billRepository.countBillsWithFilters(status, from, to, patient, guarantor, partner);
	}

	/**
	 * Retrieves all bills within the specified date range for a given patient and partner.
	 *
	 * @param dateFrom the start date of the billing period (inclusive)
	 * @param dateTo the end date of the billing period (inclusive)
	 * @param patient the patient associated with the bills
	 * @param partner the partner associated with the bills
	 * @return the list of matching {@link Bill}s
	 * @throws OHServiceException if an error occurs while retrieving the bills
	 */
	public List<Bill> getBillsByDatePatientAndPartner(
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		Patient patient,
		Partner partner
	) throws OHServiceException {
		return billRepository.findByDateBetweenAndPatientAndPartner(
			TimeTools.getBeginningOfDay(dateFrom),
			TimeTools.getBeginningOfNextDay(dateTo),
			patient,
			partner
		);
	}

	/**
	 * Retrieves all bills within the specified date range for a given partner.
	 *
	 * @param dateFrom the start date of the billing period (inclusive)
	 * @param dateTo the end date of the billing period (inclusive)
	 * @param partner the partner associated with the bills
	 * @return the list of matching {@link Bill}s
	 * @throws OHServiceException if an error occurs while retrieving the bills
	 */
	public List<Bill> getBillsByDateAndPartner(
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		Partner partner
	) throws OHServiceException {
		return billRepository.findByDateBetweenAndPartner(
			TimeTools.getBeginningOfDay(dateFrom),
			TimeTools.getBeginningOfNextDay(dateTo),
			partner
		);
	}
}