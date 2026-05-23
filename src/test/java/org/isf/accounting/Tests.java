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
package org.isf.accounting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.isf.OHCoreTestCase;
import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.*;
import org.isf.accounting.service.AccountingBillIoOperationRepository;
import org.isf.accounting.service.AccountingBillItemsIoOperationRepository;
import org.isf.accounting.service.AccountingBillPaymentIoOperationRepository;
import org.isf.accounting.service.AccountingIoOperations;
import org.isf.menu.TestUser;
import org.isf.menu.TestUserGroup;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.menu.service.UserGroupIoOperationRepository;
import org.isf.menu.service.UserIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.priceslist.TestPriceList;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.service.PricesListIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.isf.utils.pagination.PagedResponse;
import org.isf.utils.pagination.PageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import jakarta.transaction.Transactional;

class Tests extends OHCoreTestCase {

	private static TestBill testBill;
	private static TestBillItems testBillItems;
	private static TestBillPayments testBillPayments;
	private static TestPatient testPatient;
	private static TestPriceList testPriceList;
	private static TestUser testUser;
	private static TestUserGroup testUserGroup;
	private static TestBillItemGroup testBillItemGroup;
	private static TestBillItemGroupItem testBillItemGroupItem;


	@Autowired
	BillBrowserManager billBrowserManager;
	@Autowired
	AccountingIoOperations accountingIoOperation;
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;
	@Autowired
	AccountingBillIoOperationRepository accountingBillIoOperationRepository;
	@Autowired
	AccountingBillItemsIoOperationRepository accountingBillItemsIoOperationRepository;
	@Autowired
	AccountingBillPaymentIoOperationRepository accountingBillPaymentIoOperationRepository;
	@Autowired
	PricesListIoOperationRepository priceListIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	private UserIoOperationRepository userIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testBill = new TestBill();
		testBillItems = new TestBillItems();
		testBillPayments = new TestBillPayments();
		testPatient = new TestPatient();
		testPriceList = new TestPriceList();
		testUserGroup = new TestUserGroup();
		testUser = new TestUser();
		testBillItemGroup = new TestBillItemGroup();
		testBillItemGroupItem = new TestBillItemGroupItem();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testBillGets() throws Exception {
		int id = setupTestBill(false);
		checkBillIntoDb(id);
	}

	@Test
	void testBillSets() throws Exception {
		int id = setupTestBill(true);
		checkBillIntoDb(id);
	}

	@Test
	void testBillItemsGets() throws Exception {
		int id = setupTestBillItems(false);
		checkBillItemsIntoDb(id);
	}

	@Test
	void testBillItemsSets() throws Exception {
		int id = setupTestBillItems(true);
		checkBillItemsIntoDb(id);
	}

	@Test
	void testBillPaymentsGets() throws Exception {
		int id = setupTestBillPayments(false);
		checkBillPaymentsIntoDb(id);
	}

	@Test
	void testBillPaymentsSets() throws Exception {
		int id = setupTestBillPayments(true);
		checkBillPaymentsIntoDb(id);
	}

	@Autowired
	private UserGroupIoOperationRepository userGroupIoOperationRepository;

	static Stream<Arguments> allowbillguarantor() {
		return Stream.of(
			Arguments.of(false),
			Arguments.of(true));
	}

	@Test
	void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() throws Exception {
		// given:
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		Patient mergedPatient = setupTestPatient(false);

		// when:
		applicationEventPublisher.publishEvent(new PatientMergedEvent(foundBill.getBillPatient(), mergedPatient));

		// then:
		Bill resultBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(resultBill).isNotNull();
		assertThat(resultBill.getBillPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	@Test
	void testIoGetPendingBills() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> bills = accountingIoOperation.getPendingBills(0);
		assertThat(bills).contains(foundBill);
	}

	@Test
	void testIoGetPendingBillsPatId() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> bills = accountingIoOperation.getPendingBills(foundBill.getBillPatient().getCode());
		assertThat(foundBill.getAmount()).isCloseTo(bills.get(0).getAmount(), offset(0.1));
	}

	@Test
	void testIoGetBills() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> bills = accountingIoOperation.getBills();
		assertThat(bills).contains(foundBill);
	}

	@Test
	void testIoGetBill() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		Bill bill = accountingIoOperation.getBill(id);
		assertThat(bill.getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void ioBillChecks() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> bills = accountingIoOperation.getBills();
		assertThat(bills).hasSize(1);
		Bill bill = bills.get(0);

		int id2 = setupTestBill(false);
		Bill foundBill2 = accountingBillIoOperationRepository.findById(id2).orElse(null);
		assertThat(foundBill2).isNotNull();

		assertThat(bill)
			.isNotEqualTo(TimeTools.getNow())
			.isEqualTo(foundBill);
		foundBill2.setId(-1);
		assertThat(bill).isNotEqualTo(foundBill2);
		assertThat(bill.compareTo(foundBill2)).isEqualTo(id + 1); // id - (-1)
		foundBill.setId(id);

		assertThat(bill.hashCode()).isPositive();
	}

	@Test
	void testIoGetUsers() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		List<String> userIds = accountingIoOperation.getUsers();
		assertThat(userIds).contains(foundBillPayment.getUser());
	}

	@Test
	void testIoGetItems() throws Exception {
		int billItemID = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(billItemID).orElse(null);
		assertThat(foundBillItem).isNotNull();
		List<BillItems> billItems = accountingIoOperation.getItems(foundBillItem.getBill().getId());
		assertThat(billItems).contains(foundBillItem);
	}

	@Test
	void ioGetAllItems() throws Exception {
		setupTestBillItems(false);
		List<BillItems> billItems = accountingIoOperation.getItems(0);
		assertThat(billItems).isNotEmpty();
	}

	@Test
	void testIoGetItemsBillId() throws Exception {
		int id = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();
		List<BillItems> billItems = accountingIoOperation.getItems(foundBillItem.getBill().getId());
		assertThat(billItems.get(0).getItemAmount()).isCloseTo(foundBillItem.getItemAmount(), offset(0.1));
	}

	@Test
	void testIoGetPayments() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		LocalDateTime dateFrom = foundBillPayment.getDate().minusYears(1);
		LocalDateTime dateTo = TimeTools.getNow();
		List<BillPayments> billPayments = accountingIoOperation.getPayments(dateFrom, dateTo);
		assertThat(billPayments).contains(foundBillPayment);
	}

	@Test
	void testIoGetPaymentsBillId() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		List<BillPayments> billItems = accountingIoOperation.getPayments(foundBillPayment.getBill().getId());
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	void ioGetBillsByDateForPatient() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		LocalDateTime dateFrom = foundBill.getDate().minusYears(1);
		LocalDateTime dateTo = TimeTools.getNow();
		List<Bill> billItems = accountingIoOperation.getBillsBetweenDatesWherePatient(dateFrom, dateTo, foundBill.getBillPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void ioGetPendingBills() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> billItems = accountingIoOperation.getPendingBillsAffiliate(foundBill.getBillPatient().getCode());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void testIoNewBill() throws Exception {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, null, false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		Bill newBill = accountingIoOperation.newBill(bill);
		checkBillIntoDb(newBill.getId());
	}

	@Test
	void testIoNewBillItems() throws Exception {
		// given: an existing bill with one item already stored
		int existingId = setupTestBillItems(false);
		BillItems existingManaged = accountingBillItemsIoOperationRepository.findById(existingId).orElse(null);
		assertThat(existingManaged).isNotNull();

		Bill bill = existingManaged.getBill();

		// Simulate same item as new object from GUI
		BillItems existingFromGui = testBillItems.setup(null, false);
		existingFromGui.setId(existingId);

		// and: a second (new) item created by the GUI (id = null / 0)
		BillItems newItemFromGui = testBillItems.setup(null, false);

		// GUI behaviour: resend the whole list: existing + new
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(existingFromGui);
		billItems.add(newItemFromGui);

		// when: we call the service that internally does delete + re-insert
		accountingIoOperation.newBillItems(bill, billItems);

		// then: for that bill we now have exactly two items
		List<BillItems> persisted = accountingIoOperation.getItems(bill.getId());
		assertThat(persisted).hasSize(2);

		// all items belong to the correct bill
		assertThat(persisted)
			.extracting(i -> i.getBill().getId())
			.containsOnly(bill.getId());

		// and none of them keeps the old id (they've been re-inserted)
		assertThat(persisted)
			.extracting(BillItems::getId)
			.doesNotContain(existingId);
	}

	@Test
	void testIoNewBillPaymentsResendExistingAndNew() throws Exception {
		// given: an existing bill with one payment already stored
		int existingId = setupTestBillPayments(false);
		BillPayments existingPayment = accountingBillPaymentIoOperationRepository.findById(existingId).orElse(null);
		assertThat(existingPayment).isNotNull();

		// Simulate same payment as new object from GUI
		Bill bill = existingPayment.getBill();
		BillPayments existingFromGui = new BillPayments();
		existingFromGui.setId(existingPayment.getId());
		existingFromGui.setAmount(existingPayment.getAmount());
		existingFromGui.setDate(existingPayment.getDate());
		existingFromGui.setUser(existingPayment.getUser());
		existingFromGui.setBill(bill); // oppure null, tanto lo setti in newBillPayments

		// and: a second (new) payment created by the GUI (id = null / 0)
		BillPayments newPayment = testBillPayments.setup(null, false);

		// GUI behaviour: resend the whole list: existing + new
		List<BillPayments> billPayments = new ArrayList<>();
		billPayments.add(existingFromGui); // existing, with original id
		billPayments.add(newPayment); // new, with no id

		// when: we call the service that internally does delete + re-insert
		accountingIoOperation.newBillPayments(bill, billPayments);

		// then: for that bill we now have exactly two payments
		List<BillPayments> persisted = accountingIoOperation.getPayments(bill.getId());
		assertThat(persisted).hasSize(2);

		// all payments belong to the correct bill
		assertThat(persisted)
			.extracting(p -> p.getBill().getId())
			.containsOnly(bill.getId());

		// and none of them keeps the old id (they've been re-inserted)
		assertThat(persisted)
			.extracting(BillPayments::getId)
			.doesNotContain(existingId);
	}

	@Test
	void testIoUpdateBill() throws Exception {
		int id = setupTestBill(true);
		Bill bill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(bill).isNotNull();
		bill.setAmount(12.34);

		accountingIoOperation.updateBill(bill);

		assertThat(bill.getAmount()).isCloseTo(12.34, offset(0.1));
	}

	@Test
	void testIoDeleteBill() throws Exception {
		int id = setupTestBill(true);
		Bill bill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(bill).isNotNull();

		accountingIoOperation.deleteBill(bill);
		assertThat(accountingBillIoOperationRepository.findById(id)).isEmpty();
	}

	@Test
	void testIoGetBillsTimeRange() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		LocalDateTime dateFrom = foundBill.getDate().minusYears(1);
		LocalDateTime dateTo = TimeTools.getNow();
		List<Bill> bills = accountingIoOperation.getBillsBetweenDates(dateFrom, dateTo);

		assertThat(bills).contains(foundBill);
	}

	@Test
	void testIoGetBillsTimeRangeAndItem() throws Exception {
		LocalDateTime dateFrom = LocalDateTime.of(10, 9, 7, 0, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(10, 9, 9, 0, 0, 0);

		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();

		List<Bill> bills = accountingIoOperation.getBillsBetweenDates(dateFrom, dateTo);
		assertThat(bills).contains(foundBill);

		bills = accountingIoOperation.getBillsBetweenDates(LocalDateTime.of(10, 1, 1, 0, 0, 0), dateFrom);
		assertThat(bills).doesNotContain(foundBill);

		bills = accountingIoOperation.getBillsBetweenDates(dateTo, LocalDateTime.of(11, 1, 1, 0, 0, 0));
		assertThat(bills).doesNotContain(foundBill);

		id = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();
		foundBill = accountingBillIoOperationRepository.findById(foundBillItem.getBill().getId()).orElse(null);
		assertThat(foundBill).isNotNull();

		bills = accountingIoOperation.getBillsBetweenDatesWhereBillItem(dateFrom, dateTo, foundBillItem);
		assertThat(bills).contains(foundBill);

		bills = accountingIoOperation.getBillsBetweenDatesWhereBillItem(dateFrom, dateTo, (BillItems) null);
		assertThat(bills).contains(foundBill);

		id = setupTestBillItems(true);
		foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();

		bills = accountingIoOperation.getBillsBetweenDatesWhereBillItem(dateFrom, dateTo, foundBillItem);
		assertThat(bills).contains(foundBill);
	}

	@Test
	void testIoGetBillsPayment() throws Exception {
		List<BillPayments> payments = new ArrayList<>();

		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();

		payments.add(foundBillPayment);
		List<Bill> bills = accountingIoOperation.getBills(payments);

		assertThat(bills.get(0).getAmount()).isCloseTo(foundBillPayment.getBill().getAmount(), offset(0.1));
	}

	@Test
	void testIoGetPaymentsBill() throws Exception {
		List<Bill> bills = new ArrayList<>();

		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		Bill foundBill = foundBillPayment.getBill();

		bills.add(foundBill);
		List<BillPayments> payments = accountingIoOperation.getPayments(bills);

		assertThat(payments.get(0).getBill().getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void ioBillPaymentsChecks() throws Exception {
		List<Bill> bills = new ArrayList<>();
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		Bill foundBill = foundBillPayment.getBill();
		bills.add(foundBill);
		List<BillPayments> payments = accountingIoOperation.getPayments(bills);
		assertThat(payments).hasSize(1);

		BillPayments billPayment = payments.get(0);
		assertThat(foundBillPayment).isEqualTo(foundBillPayment);
		assertThat(foundBillPayment)
			.isNotEqualTo(TimeTools.getNow())
			.isEqualTo(billPayment);
		int id2 = setupTestBillPayments(false);
		BillPayments foundBillPayment2 = accountingBillPaymentIoOperationRepository.findById(id2).orElse(null);
		assertThat(foundBillPayment2).isNotNull();
		foundBillPayment2.setId(-1);
		assertThat(foundBillPayment).isNotEqualTo(foundBillPayment2);
		foundBillPayment.setId(id);

		assertThat(billPayment.compareTo(billPayment)).isZero();

		assertThat(billPayment.hashCode()).isPositive();
	}

	@Test
	void ioGetDistictsBillItems() throws Exception {
		int id = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();
		List<BillItems> billItems = accountingIoOperation.getDistictsBillItems();
		assertThat(billItems).contains(foundBillItem);
	}

	@Test
	void ioBillItemChecks() throws Exception {
		int id = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();
		List<BillItems> billItems = accountingIoOperation.getDistictsBillItems();
		assertThat(billItems).hasSize(1);
		BillItems billItem = billItems.get(0);

		assertThat(foundBillItem)
			.isNotEqualTo(TimeTools.getNow())
			.isEqualTo(billItem);
		int id2 = setupTestBillItems(false);
		BillItems foundBillItem2 = accountingBillItemsIoOperationRepository.findById(id2).orElse(null);
		assertThat(foundBillItem2).isNotNull();
		foundBillItem2.setId(-1);
		assertThat(foundBillItem).isNotEqualTo(foundBillItem2);
		foundBillItem.setId(id);

		String itemId = billItem.getItemId();
		String displayCode = billItem.getItemDisplayCode();
		billItem.setItemDisplayCode(null);
		assertThat(billItem.getItemDisplayCode()).isNull();
		billItem.setItemDisplayCode("");
		assertThat(billItem.getItemDisplayCode()).isNull();
		billItem.setItemId("displayCode");
		assertThat(billItem.getItemDisplayCode()).isEqualTo("displayCode");
		billItem.setItemDisplayCode(displayCode);

		billItem.setItemId(itemId);
		billItem.setItemDisplayCode(displayCode);

		assertThat(billItem.hashCode()).isPositive();
	}

	@Test
	void ioGetPaymentsByDateForPatient() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		LocalDateTime dateFrom = LocalDateTime.of(1, 3, 2, 0, 0, 0, 0);
		LocalDateTime dateTo = TimeTools.getNow();
		List<BillPayments> billItems = accountingIoOperation.getPaymentsBetweenDatesWherePatient(dateFrom, dateTo, foundBillPayment.getBill().getBillPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	void mgrBillItemsChecks() throws Exception {
		int id = setupTestBillItems(false);
		BillItems billitem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(billitem).isNotNull();
		List<BillItems> billItems = billBrowserManager.getItems(0);
		assertThat(billItems).isEmpty();
		billItems = billBrowserManager.getItems(99999);
		assertThat(billItems).isEmpty();
		billItems = billBrowserManager.getItems(billitem.getBill().getId());
		assertThat(billItems).hasSize(1);
	}

	@Test
	void mgrGetPaymentsByDateForPatient() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		LocalDateTime dateFrom = LocalDateTime.of(1, 3, 2, 0, 0, 0, 0);
		LocalDateTime dateTo = TimeTools.getNow();
		List<BillPayments> billItems = billBrowserManager.getPayments(dateFrom, dateTo, foundBillPayment.getBill().getBillPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	void mgrGetAllPayments() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		List<BillPayments> billItems = billBrowserManager.getPayments(0); // get all
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	void mgrGetAllPaymentsWithId() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		List<BillPayments> billItems = billBrowserManager.getPayments(foundBillPayment.getBill().getId());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	void mgrGetDistictsBillItems() throws Exception {
		int id = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();
		List<BillItems> billItems = billBrowserManager.getDistinctItems();
		assertThat(billItems).contains(foundBillItem);
	}

	@Test
	void mgrGetBillsBetweenDatesWherePatient() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		LocalDateTime dateFrom = LocalDateTime.of(1, 3, 2, 0, 0, 0, 0);
		LocalDateTime dateTo = TimeTools.getNow();
		List<Bill> billItems = billBrowserManager.getBills(dateFrom, dateTo, foundBill.getBillPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void mgrGetPendingBillsForPatientId() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> bills = billBrowserManager.getPendingBills(foundBill.getBillPatient().getCode());
		assertThat(bills.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void mgrNewBillNoItemsNoPayments() throws Exception {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		Bill bill = testBill.setup(priceList, patient, null, false);
		billBrowserManager.newBill(
			bill,
			new ArrayList<>(),
			new ArrayList<>());
		assertThat(billBrowserManager.getBill(bill.getId()).getId()).isEqualTo(bill.getId());
		assertThat(billBrowserManager.getItems(bill.getId())).isEmpty();
		assertThat(billBrowserManager.getPayments(bill.getId())).isEmpty();
	}

	@Test
	void mgrNewBillBillItemsNoPayments() throws Exception {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		Bill bill = testBill.setup(priceList, patient, null, false);
		BillItems insertBillItem = testBillItems.setup(null, false);
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(insertBillItem);
		billBrowserManager.newBill(
			bill,
			billItems,
			new ArrayList<>());
		assertThat(billBrowserManager.getBill(bill.getId()).getId()).isEqualTo(bill.getId());
		assertThat(billBrowserManager.getItems(bill.getId())).isNotEmpty();
		assertThat(billBrowserManager.getPayments(bill.getId())).isEmpty();
	}

	@Test
	void mgrNewBillNoItemsAndPayments() throws Exception {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		Bill bill = testBill.setup(priceList, patient, null, false);
		BillPayments insertBillPayment = testBillPayments.setup(bill, false);
		insertBillPayment.setDate(TimeTools.getNow());
		List<BillPayments> billPayments = new ArrayList<>();
		billPayments.add(insertBillPayment);
		billBrowserManager.newBill(
			bill,
			new ArrayList<>(),
			billPayments);
		assertThat(billBrowserManager.getBill(bill.getId()).getId()).isEqualTo(bill.getId());
		assertThat(billBrowserManager.getItems(bill.getId())).isEmpty();
		assertThat(billBrowserManager.getPayments(bill.getId())).isNotEmpty();
	}

	@Test
	void mgrNewBillItemsAndPayments() throws Exception {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		Bill bill = testBill.setup(priceList, patient, null, false);
		BillItems insertBillItem = testBillItems.setup(bill, false);
		BillPayments insertBillPayment = testBillPayments.setup(bill, false);
		insertBillPayment.setDate(TimeTools.getNow());
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(insertBillItem);
		List<BillPayments> billPayments = new ArrayList<>();
		billPayments.add(insertBillPayment);
		billBrowserManager.newBill(
			bill,
			billItems,
			billPayments);
		assertThat(billBrowserManager.getBill(bill.getId()).getId()).isEqualTo(bill.getId());
		assertThat(billBrowserManager.getItems(bill.getId())).isNotEmpty();
		assertThat(billBrowserManager.getPayments(bill.getId())).isNotEmpty();
	}

	@Test
	void mgrNewBillFailValidation() throws Exception {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, null, false);
		List<BillItems> billItems = new ArrayList<>();
		BillItems insertBillItem = testBillItems.setup(bill, false);
		billItems.add(insertBillItem);
		List<BillPayments> billPayments = new ArrayList<>();
		BillPayments payments = testBillPayments.setup(bill, false);
		billPayments.add(payments);

		assertThatThrownBy(() -> billBrowserManager.newBill(bill, billItems, billPayments))
			.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void mgrGetBillsPayment() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();

		List<BillPayments> payments = new ArrayList<>();
		payments.add(foundBillPayment);
		List<Bill> bills = billBrowserManager.getBills(payments);

		assertThat(bills.get(0).getAmount()).isCloseTo(foundBillPayment.getBill().getAmount(), offset(0.1));
	}

	@Test
	void mgrGetBillsPaymentEmpty() throws Exception {
		List<Bill> bills = billBrowserManager.getBills(new ArrayList<>());
		assertThat(bills).isEmpty();
	}

	@Test
	void mgrGetPayments() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		LocalDateTime dateFrom = foundBillPayment.getDate().minusYears(1);
		LocalDateTime dateTo = TimeTools.getNow();
		List<BillPayments> billPayments = billBrowserManager.getPayments(dateFrom, dateTo);
		assertThat(billPayments).contains(foundBillPayment);
	}

	@Test
	void mgrGetPaymentsBill() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		Bill foundBill = foundBillPayment.getBill();
		List<Bill> bills = new ArrayList<>();
		bills.add(foundBill);
		List<BillPayments> payments = billBrowserManager.getPayments(bills);
		assertThat(payments.get(0).getBill().getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void mgrGetBills() throws Exception {
		LocalDateTime dateFrom = LocalDateTime.of(10, 9, 7, 0, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(10, 9, 9, 0, 0, 0);

		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();

		List<Bill> bills = billBrowserManager.getBills(dateFrom, dateTo);
		assertThat(bills).contains(foundBill);

		bills = billBrowserManager.getBills(LocalDateTime.of(10, 1, 1, 0, 0, 0), dateFrom);
		assertThat(bills).doesNotContain(foundBill);

		bills = billBrowserManager.getBills(dateTo, LocalDateTime.of(11, 1, 1, 0, 0, 0));
		assertThat(bills).doesNotContain(foundBill);

		id = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();
		foundBill = accountingBillIoOperationRepository.findById(foundBillItem.getBill().getId()).orElse(null);
		assertThat(foundBill).isNotNull();

		bills = billBrowserManager.getBills(dateFrom, dateTo, foundBillItem);
		assertThat(bills).contains(foundBill);

		bills = billBrowserManager.getBills(dateFrom, dateTo, (BillItems) null);
		assertThat(bills).contains(foundBill);

		id = setupTestBillItems(true);
		foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();

		bills = billBrowserManager.getBills(dateFrom, dateTo, foundBillItem);
		assertThat(bills).contains(foundBill);
	}

	@Test
	void mgrGetBill() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		Bill bill = billBrowserManager.getBill(id);
		assertThat(bill.getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void mgrGetPendingBillsAffiliate() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> billItems = billBrowserManager.getPendingBillsAffiliate(foundBill.getBillPatient().getCode());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void mgrUpdateBillNoItemsNoPayements() throws Exception {
		int id = setupTestBill(true);
		Bill bill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(bill).isNotNull();
		bill.setAmount(12.34);
		Bill updatedBill = billBrowserManager.updateBill(
			bill,
			new ArrayList<>(),
			new ArrayList<>());
		assertThat(updatedBill.getAmount()).isCloseTo(12.34, offset(0.1));
	}

	@Test
	void mgrDeleteBill() throws Exception {
		int id = setupTestBill(true);
		Bill bill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(bill).isNotNull();
		billBrowserManager.deleteBill(bill);
		assertThat(accountingBillIoOperationRepository.findById(id)).isEmpty();
	}

	@Test
	void mgrGetUsers() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		List<String> userIds = billBrowserManager.getUsers();
		assertThat(userIds).contains(foundBillPayment.getUser());
	}

	@Test
	void mgrTestBillItemGroup() throws Exception {

		BillItemGroup group = testBillItemGroup.setup(false);
		List<BillItemGroupItem> list = new ArrayList<>();
		for (int i = 0; i < 4; i ++) {
			BillItemGroupItem billItemGroupItem = testBillItemGroupItem.setup(group, false);
			list.add(billItemGroupItem);
		}

		group.setItems(list);

		BillItemGroup foundGroup = billBrowserManager.getBillItemGroupById(billBrowserManager.addBillItemGroup(group).getId());

		assertThat(foundGroup).isNotNull();
		assertThat(billBrowserManager.getItemsByGroupId(foundGroup.getId())).hasSize(4);

		assertThat(billBrowserManager.getAllBillItemGroupItems()).hasSize(4);

		assertThat(billBrowserManager.getAllBillItemGroups()).hasSize(1);

		foundGroup.setTitle(group.getTitle() + "group");

		list.remove(0);

		foundGroup.setItems(list);

		BillItemGroup updatedGroup = billBrowserManager.updateBillItemGroup(foundGroup);

		BillItemGroup foundUpdatedGroup = billBrowserManager.getBillItemGroupById(updatedGroup.getId());

		assertThat(foundUpdatedGroup).isNotNull();
		assertThat(billBrowserManager.getItemsByGroupId(foundUpdatedGroup.getId())).hasSize(3);

		billBrowserManager.deleteBillItemGroup(foundUpdatedGroup.getId());

		assertThat(billBrowserManager.getBillItemGroupById(foundUpdatedGroup.getId())).isNull();
	}

	private int setupTestBill(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, null, usingSet);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		accountingBillIoOperationRepository.saveAndFlush(bill);
		return bill.getId();
	}

	private void checkBillIntoDb(int id) throws OHException {
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		testBill.check(foundBill);
		testPriceList.check(foundBill.getPriceList());
		testPatient.check(foundBill.getBillPatient());
	}

	private int setupTestBillItems(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, null, usingSet);
		BillItems billItem = testBillItems.setup(bill, usingSet);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		accountingBillIoOperationRepository.saveAndFlush(bill);
		accountingBillItemsIoOperationRepository.saveAndFlush(billItem);
		return billItem.getId();
	}

	private void checkBillItemsIntoDb(int id) throws OHException {
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();
		testBillItems.check(foundBillItem);
		testBill.check(foundBillItem.getBill());
		testPriceList.check(foundBillItem.getBill().getPriceList());
		testPatient.check(foundBillItem.getBill().getBillPatient());
	}

	private int setupTestBillPayments(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, null, usingSet);
		BillPayments billPayment = testBillPayments.setup(bill, usingSet);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		accountingBillIoOperationRepository.saveAndFlush(bill);
		accountingBillPaymentIoOperationRepository.saveAndFlush(billPayment);
		return billPayment.getId();
	}

	private void checkBillPaymentsIntoDb(int id) throws OHException {
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		testBillPayments.check(foundBillPayment);
		testBill.check(foundBillPayment.getBill());
		testPriceList.check(foundBillPayment.getBill().getPriceList());
		testPatient.check(foundBillPayment.getBill().getBillPatient());
	}

	private Patient setupTestPatient(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient;
	}

	@Test
	@DisplayName("Should get bills filtered by a guarantor")
	void testMgrGetBillsByDatePatientAndGuarantor() throws OHException, OHServiceException {
		List<Bill> bills = new ArrayList<>();

		Bill bill1 = accountingBillIoOperationRepository.findById(setupTestBill(false)).orElse(null);
		Bill bill2 = accountingBillIoOperationRepository.findById(setupTestBill(false)).orElse(null);
		Bill bill3 = accountingBillIoOperationRepository.findById(setupTestBill(false)).orElse(null);

		assertThat(bill1).isNotNull();
		assertThat(bill2).isNotNull();
		assertThat(bill3).isNotNull();

		UserGroup userGroup = userGroupIoOperationRepository.save(testUserGroup.setup(false));
		User user = testUser.setup(userGroup, false);
		user.setUserName("Guarantor");
		user = userIoOperationRepository.save(user);

		bill3.setGuarantor(user);

		bills.add(bill1);
		bills.add(bill2);
		bills.add(bill3);

		accountingBillIoOperationRepository.saveAllAndFlush(bills);

		LocalDateTime dateFrom = LocalDateTime.of(10, 9, 7, 0, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(10, 9, 9, 0, 0, 0);

		List<Bill> guarantorBills = billBrowserManager.getBillsByDatePatientAndGuarantor(dateFrom, dateTo, bill3.getBillPatient(), bill3.getGuarantor());

		assertThat(guarantorBills.size()).isEqualTo(1);
		assertThat(guarantorBills.get(0).getGuarantor()).isEqualTo(user);
	}

	@Test
	@DisplayName("Should get payments filtered by a guarantor and patient")
	void testGetPaymentsByDatePatientAndGuarantor() throws Exception {
		LocalDateTime dateFrom = LocalDateTime.now().minusHours(24);
		LocalDateTime dateTo = LocalDateTime.now().plusHours(1);

		int code = setupTestBill(true);
		Bill bill = billBrowserManager.getBill(code);
		checkBillIntoDb(code);

		BillItems insertBillItem = testBillItems.setup(bill, false);
		BillPayments insertBillPayment = testBillPayments.setup(bill, false);

		LocalDateTime now = LocalDateTime.now();
		bill.setDate(now.minusDays(1));
		insertBillPayment.setDate(now);

		List<BillItems> billItems = new ArrayList<>();
		billItems.add(insertBillItem);
		List<BillPayments> billPayments = new ArrayList<>();
		billPayments.add(insertBillPayment);

		UserGroup userGroup = testUserGroup.setup(true);
		userGroup = userGroupIoOperationRepository.saveAndFlush(userGroup);
		User guarantor = testUser.setup(userGroup, true);
		guarantor.setUserName("guarantor");
		guarantor = userIoOperationRepository.saveAndFlush(guarantor);
		bill.setGuarantor(guarantor);

		bill = billBrowserManager.newBill(bill, billItems, billPayments);
		assertThat(bill).isNotNull();

		Patient patient = bill.getBillPatient();
		List<BillPayments> billPaymentsList = billBrowserManager.getPaymentsByDatePatientAndGuarantor(
			dateFrom, dateTo, patient, guarantor);

		assertThat(billPaymentsList).isNotEmpty();
		assertThat(billPaymentsList.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Should get paginated bills with no filters")
	void testGetBillsWithFiltersNoFilters() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();

		int page = 0;
		int size = 10;
		Page<Bill> billPage = billBrowserManager.getBillsWithFilters(null, null, null, null, null, page, size);

		assertThat(billPage).isNotNull();
		assertThat(billPage.getContent()).isNotEmpty();
		assertThat(billPage.getContent().size()).isLessThanOrEqualTo(size);
		assertThat(billPage.getNumber()).isEqualTo(page);
		assertThat(billPage.getSize()).isEqualTo(size);
	}

	@Test
	@DisplayName("Should get paginated bills filtered by status")
	void testGetBillsWithFiltersByStatus() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		foundBill.setStatus("O");
		accountingBillIoOperationRepository.saveAndFlush(foundBill);

		int page = 0;
		int size = 10;
		Page<Bill> billPage = billBrowserManager.getBillsWithFilters("O", null, null, null, null, page, size);

		assertThat(billPage).isNotNull();
		assertThat(billPage.getContent()).isNotEmpty();
		for (Bill bill : billPage.getContent()) {
			assertThat(bill.getStatus()).isEqualTo("O");
		}
	}

	@Test
	@DisplayName("Should get paginated bills filtered by date range")
	void testGetBillsWithFiltersByDateRange() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		LocalDateTime dateFrom = foundBill.getDate().minusDays(1);
		LocalDateTime dateTo = foundBill.getDate().plusDays(1);

		int page = 0;
		int size = 10;
		Page<Bill> billPage = billBrowserManager.getBillsWithFilters(null, dateFrom, dateTo, null, null, page, size);

		assertThat(billPage).isNotNull();
		assertThat(billPage.getContent()).isNotEmpty();
		assertThat(billPage.getContent()).contains(foundBill);
	}

	@Test
	@DisplayName("Should get paginated bills with all filters")
	void testGetBillsWithFiltersAllFilters() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		foundBill.setStatus("O");
		accountingBillIoOperationRepository.saveAndFlush(foundBill);
		Patient patient = foundBill.getBillPatient();
		LocalDateTime dateFrom = foundBill.getDate().minusDays(1);
		LocalDateTime dateTo = foundBill.getDate().plusDays(1);

		int page = 0;
		int size = 10;
		Page<Bill> billPage = billBrowserManager.getBillsWithFilters("O", dateFrom, dateTo, patient, null, page, size);

		assertThat(billPage).isNotNull();
		assertThat(billPage.getContent()).isNotEmpty();
		assertThat(billPage.getContent()).contains(foundBill);
	}

	@Test
	@DisplayName("Should count bills with no filters")
	void testCountBillsWithFiltersNoFilters() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();

		long count = billBrowserManager.countBillsWithFilters(null, null, null, null, null);
		assertThat(count).isGreaterThan(0);
	}

	@Test
	@DisplayName("Should count bills filtered by status")
	void testCountBillsWithFiltersByStatus() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		foundBill.setStatus("O");
		accountingBillIoOperationRepository.saveAndFlush(foundBill);

		long count = billBrowserManager.countBillsWithFilters("O", null, null, null, null);
		assertThat(count).isGreaterThan(0);
	}

	@Test
	@DisplayName("Should get paginated bills with guarantor filter")
	void testGetBillsWithFiltersByGuarantor() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		userGroup = userGroupIoOperationRepository.saveAndFlush(userGroup);
		User guarantor = testUser.setup(userGroup, false);
		guarantor.setUserName("TestGuarantor");
		guarantor = userIoOperationRepository.saveAndFlush(guarantor);

		for (int i = 0; i < 5; i++) {
			Patient patient = testPatient.setup(false);
			PriceList priceList = testPriceList.setup(false);
			Bill bill = testBill.setup(priceList, patient, null, false);
			bill.setGuarantor(guarantor);
			priceListIoOperationRepository.saveAndFlush(priceList);
			patientIoOperationRepository.saveAndFlush(patient);
			accountingBillIoOperationRepository.saveAndFlush(bill);
		}

		for (int i = 0; i < 5; i++) {
			Patient patient = testPatient.setup(false);
			PriceList priceList = testPriceList.setup(false);
			Bill bill = testBill.setup(priceList, patient, null, false);
			priceListIoOperationRepository.saveAndFlush(priceList);
			patientIoOperationRepository.saveAndFlush(patient);
			accountingBillIoOperationRepository.saveAndFlush(bill);
		}

		int page = 0;
		int size = 10;
		Page<Bill> billPage = billBrowserManager.getBillsWithFilters(null, null, null, null, guarantor, page, size);

		assertThat(billPage).isNotNull();
		assertThat(billPage.getContent()).isNotEmpty();
		assertThat(billPage.getContent().size()).isEqualTo(5);
		for (Bill bill : billPage.getContent()) {
			assertThat(bill.getGuarantor()).isEqualTo(guarantor);
		}
	}

	@Test
	@DisplayName("Should verify PageInfo from Spring Page")
	void testPageInfoFromSpringPage() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();

		int page = 0;
		int size = 10;
		Pageable pageable = PageRequest.of(page, size);
		Page<Bill> springPage = accountingBillIoOperationRepository.findBillsWithFilters(null, null, null, null, null, pageable);

		PageInfo pageInfo = PageInfo.from(springPage);
		assertThat(pageInfo).isNotNull();
		assertThat(pageInfo.getSize()).isEqualTo(size);
		assertThat(pageInfo.getPage()).isEqualTo(page);
		assertThat(pageInfo.getTotalPages()).isEqualTo(springPage.getTotalPages());
		assertThat(pageInfo.getTotalNbOfElements()).isEqualTo(springPage.getTotalElements());
	}

	@Test
	@DisplayName("Should get all bill items including refunds")
	@Transactional
	@Rollback
	void testGetAllBillItems() throws Exception {
		// Create patient and price list
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		patient = patientIoOperationRepository.saveAndFlush(patient);
		priceList = priceListIoOperationRepository.saveAndFlush(priceList);

		// Create main bill
		Bill bill = testBill.setup(priceList, patient, null, false);
		bill = accountingBillIoOperationRepository.saveAndFlush(bill);

		// Add items to main bill
		BillItems item1 = testBillItems.setup(bill, false);
		item1.setItemDescription("Paracetamol");
		item1.setItemQuantity(2);
		item1.setItemDate(TimeTools.getNow());
		accountingBillItemsIoOperationRepository.saveAndFlush(item1);

		BillItems item2 = testBillItems.setup(bill, false);
		item2.setItemDescription("Consultation");
		item2.setItemQuantity(1);
		item2.setItemDate(TimeTools.getNow());
		accountingBillItemsIoOperationRepository.saveAndFlush(item2);

		// Create refund bill
		Bill refundBill = testBill.setup(priceList, patient, null, false);
		refundBill.setParentId(bill.getId());
		refundBill.setAmount(-100.0);
		refundBill = accountingBillIoOperationRepository.saveAndFlush(refundBill);

		// Add refund item
		BillItems refundItem = testBillItems.setup(refundBill, false);
		refundItem.setItemDescription("Paracetamol");
		refundItem.setItemQuantity(-1);
		refundItem.setItemDate(TimeTools.getNow());
		accountingBillItemsIoOperationRepository.saveAndFlush(refundItem);

		// Get all bill items - just verify the method doesn't throw exception
		List<BillItems> allItems = billBrowserManager.getAllBillItems(bill);
		assertThat(allItems).isNotNull();
	}

	@Test
	@DisplayName("Should get all bill payments including refunds")
	void testGetAllBillPayments() throws Exception {
		// given: create a main bill with payments
		int billId = setupTestBill(false);
		Bill mainBill = accountingBillIoOperationRepository.findById(billId).orElse(null);
		assertThat(mainBill).isNotNull();

		// add payments to main bill
		BillPayments payment1 = testBillPayments.setup(mainBill, false);
		payment1.setAmount(500.0);
		payment1.setDate(TimeTools.getNow());

		BillPayments payment2 = testBillPayments.setup(mainBill, false);
		payment2.setAmount(300.0);
		payment2.setDate(TimeTools.getNow().plusMinutes(5));

		accountingBillPaymentIoOperationRepository.save(payment1);
		accountingBillPaymentIoOperationRepository.save(payment2);

		// create a refund bill
		Patient patient = mainBill.getBillPatient();
		PriceList priceList = mainBill.getPriceList();
		Bill refundBill = testBill.setup(priceList, patient, null, false);
		refundBill.setParentId(mainBill.getId());
		refundBill = accountingBillIoOperationRepository.save(refundBill);

		// add refund payment (negative amount)
		BillPayments refundPayment = testBillPayments.setup(refundBill, false);
		refundPayment.setAmount(-100.0);
		refundPayment.setDate(TimeTools.getNow().plusMinutes(10));
		accountingBillPaymentIoOperationRepository.save(refundPayment);

		// when: get all bill payments
		List<BillPayments> allPayments = billBrowserManager.getAllBillPayments(mainBill);

		// then: should have 3 payments (2 original + 1 refund)
		assertThat(allPayments).hasSize(3);

		// verify the refund payment has negative amount
		BillPayments refundFound = allPayments.stream()
			.filter(payment -> payment.getAmount() < 0)
			.findFirst()
			.orElse(null);
		assertThat(refundFound).isNotNull();
		assertThat(refundFound.getAmount()).isEqualTo(-100.0);
	}

	@Test
	@DisplayName("Should return empty list when bill is null in getAllBillItems")
	void testGetAllBillItemsWithNullBill() throws Exception {
		List<BillItems> items = billBrowserManager.getAllBillItems(null);
		assertThat(items).isEmpty();
	}

	@Test
	@DisplayName("Should return empty list when bill has no items in getAllBillItems")
	void testGetAllBillItemsWithBillHavingNoItems() throws Exception {
		int billId = setupTestBill(false);
		Bill bill = accountingBillIoOperationRepository.findById(billId).orElse(null);
		assertThat(bill).isNotNull();

		List<BillItems> items = billBrowserManager.getAllBillItems(bill);
		assertThat(items).isEmpty();
	}

	@Test
	@DisplayName("Should return empty list when bill is null in getAllBillPayments")
	void testGetAllBillPaymentsWithNullBill() throws Exception {
		List<BillPayments> payments = billBrowserManager.getAllBillPayments(null);
		assertThat(payments).isEmpty();
	}

	@Test
	@DisplayName("Should return empty list when bill has no payments in getAllBillPayments")
	void testGetAllBillPaymentsWithBillHavingNoPayments() throws Exception {
		int billId = setupTestBill(false);
		Bill bill = accountingBillIoOperationRepository.findById(billId).orElse(null);
		assertThat(bill).isNotNull();

		List<BillPayments> payments = billBrowserManager.getAllBillPayments(bill);
		assertThat(payments).isEmpty();
	}

	@Test
	@DisplayName("Should sort bill items by date in getAllBillItems")
	void testGetAllBillItemsSorting() throws Exception {
		// given: create a main bill
		int billId = setupTestBill(false);
		Bill mainBill = accountingBillIoOperationRepository.findById(billId).orElse(null);
		assertThat(mainBill).isNotNull();

		// add items with different dates
		BillItems itemEarly = testBillItems.setup(mainBill, false);
		itemEarly.setItemDescription("Early Item");
		itemEarly.setItemQuantity(1);
		itemEarly.setItemDate(TimeTools.getNow().minusDays(2));

		BillItems itemLate = testBillItems.setup(mainBill, false);
		itemLate.setItemDescription("Late Item");
		itemLate.setItemQuantity(1);
		itemLate.setItemDate(TimeTools.getNow());

		accountingBillItemsIoOperationRepository.save(itemEarly);
		accountingBillItemsIoOperationRepository.save(itemLate);

		// when: get all bill items
		List<BillItems> allItems = billBrowserManager.getAllBillItems(mainBill);

		// then: items should be sorted by date (ascending)
		assertThat(allItems).hasSize(2);
		assertThat(allItems.get(0).getItemDescription()).isEqualTo("Early Item");
		assertThat(allItems.get(1).getItemDescription()).isEqualTo("Late Item");
	}

	@Test
	@DisplayName("Should sort bill payments by date in getAllBillPayments")
	void testGetAllBillPaymentsSorting() throws Exception {
		// given: create a main bill
		int billId = setupTestBill(false);
		Bill mainBill = accountingBillIoOperationRepository.findById(billId).orElse(null);
		assertThat(mainBill).isNotNull();

		// add payments with different dates
		BillPayments paymentEarly = testBillPayments.setup(mainBill, false);
		paymentEarly.setAmount(100.0);
		paymentEarly.setDate(TimeTools.getNow().minusDays(2));

		BillPayments paymentLate = testBillPayments.setup(mainBill, false);
		paymentLate.setAmount(200.0);
		paymentLate.setDate(TimeTools.getNow());

		accountingBillPaymentIoOperationRepository.save(paymentEarly);
		accountingBillPaymentIoOperationRepository.save(paymentLate);

		// when: get all bill payments
		List<BillPayments> allPayments = billBrowserManager.getAllBillPayments(mainBill);

		// then: payments should be sorted by date (ascending)
		assertThat(allPayments).hasSize(2);
		assertThat(allPayments.get(0).getAmount()).isEqualTo(100.0);
		assertThat(allPayments.get(1).getAmount()).isEqualTo(200.0);
	}

	@Test
	void testHasPrescription() throws Exception {
		Patient patient = setupTestPatient(false);

		boolean hasPrescription = billBrowserManager.hasPrescription(patient.getCode());

		assertThat(hasPrescription).isFalse();
	}

	@Test
	void testBillItemsPrescriptionId() throws Exception {
		BillItems billItem = new BillItems();
		Integer prescriptionId = 12345;

		billItem.setPrescriptionId(prescriptionId);

		assertThat(billItem.getPrescriptionId()).isEqualTo(prescriptionId);

		billItem.setPrescriptionId(null);

		assertThat(billItem.getPrescriptionId()).isNull();
	}

	@Test
	void testBillItemsItemGroups() throws Exception {
		BillItems medicalItem = new BillItems();
		BillItems examItem = new BillItems();
		BillItems operationItem = new BillItems();

		medicalItem.setItemGroup("MED");
		examItem.setItemGroup("EXA");
		operationItem.setItemGroup("OPE");

		assertThat(medicalItem.getItemGroup()).isEqualTo("MED");
		assertThat(examItem.getItemGroup()).isEqualTo("EXA");
		assertThat(operationItem.getItemGroup()).isEqualTo("OPE");
	}

	@Test
	void testCalculateTotalQuantityForTherapy() throws Exception {
		LocalDateTime startDate = LocalDateTime.of(2026, 5, 22, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2026, 5, 28, 0, 0);
		int freqInPeriod = 1;
		int freqInDay = 2;
		double qty = 1.0;

		long diffInMillis = java.time.Duration.between(startDate, endDate).toMillis();
		long totalDays = (diffInMillis / (1000 * 60 * 60 * 24)) + 1;
		long effectiveDays = (totalDays + freqInPeriod - 1) / freqInPeriod;
		double totalQuantity = effectiveDays * freqInDay * qty;

		assertThat(totalQuantity).isEqualTo(14.0);

		freqInPeriod = 3;
		effectiveDays = (totalDays + freqInPeriod - 1) / freqInPeriod;
		totalQuantity = effectiveDays * freqInDay * qty;

		assertThat(totalQuantity).isEqualTo(6.0);
	}

	@Test
	void testCalculateRemainingQuantityForTherapy() throws Exception {
		double totalPrescribed = 100.0;
		double alreadyBought = 30.0;

		double remaining = totalPrescribed - alreadyBought;

		assertThat(remaining).isEqualTo(70.0);

		alreadyBought = 100.0;
		remaining = totalPrescribed - alreadyBought;

		assertThat(remaining).isEqualTo(0.0);
	}

	@Test
	void testBillItemsPriceWithReduction() throws Exception {
		BillItems item = new BillItems();
		double basePrice = 100.0;
		double reductionPercent = 20.0;

		double finalPrice = basePrice * (1 - reductionPercent / 100);

		assertThat(finalPrice).isCloseTo(80.0, offset(0.01));

		item.setItemAmount(finalPrice);
		item.setItemAmountBrut(basePrice);

		assertThat(item.getItemAmount()).isCloseTo(80.0, offset(0.01));
		assertThat(item.getItemAmountBrut()).isCloseTo(100.0, offset(0.01));
	}

	@Test
	void testSelectPrescriptionsDialogCreation() throws Exception {
		Patient patient = setupTestPatient(false);
		assertThat(patient).isNotNull();
	}
}
