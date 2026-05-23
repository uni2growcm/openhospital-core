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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItemGroup;
import org.isf.accounting.model.BillItemGroupItem;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.generaldata.MessageBundle;
import org.isf.patient.model.Patient;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.time.TimeTools;
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
	private BillItemGroupIoOperationRepository billItemGroupRepository;
	private BillItemGroupItemIoOperationRepository billItemGroupItemRepository;

	public AccountingIoOperations(AccountingBillIoOperationRepository accountingBillIoOperationRepository,
		AccountingBillPaymentIoOperationRepository accountingBillPaymentIoOperationRepository,
		AccountingBillItemsIoOperationRepository accountingBillItemsIoOperationRepository,
		BillItemGroupIoOperationRepository billItemGroupRepository,
		BillItemGroupItemIoOperationRepository billItemGroupItemRepository
	) {
		this.billRepository = accountingBillIoOperationRepository;
		this.billPaymentRepository = accountingBillPaymentIoOperationRepository;
		this.billItemsRepository = accountingBillItemsIoOperationRepository;
		this.billItemGroupRepository = billItemGroupRepository;
		this.billItemGroupItemRepository = billItemGroupItemRepository;
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
}
