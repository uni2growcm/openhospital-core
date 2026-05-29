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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

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
import org.isf.utils.pagination.PageInfo;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import java.io.File;

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
	private BillBrowserManager billBrowserManager;
	@Autowired
	private AccountingIoOperations accountingIoOperation;
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	@Autowired
	private AccountingBillIoOperationRepository accountingBillIoOperationRepository;
	@Autowired
	private AccountingBillItemsIoOperationRepository accountingBillItemsIoOperationRepository;
	@Autowired
	private AccountingBillPaymentIoOperationRepository accountingBillPaymentIoOperationRepository;
	@Autowired
	private PricesListIoOperationRepository priceListIoOperationRepository;
	@Autowired
	private PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	private UserIoOperationRepository userIoOperationRepository;
	@Autowired
	private UserGroupIoOperationRepository userGroupIoOperationRepository;

	static Stream<Arguments> allowbillguarantor() {
		return Stream.of(Arguments.of(false), Arguments.of(true));
	}

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
		MockitoAnnotations.openMocks(this);
		cleanH2InMemoryDb();
	}

	// ==================== BILL TESTS ====================
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

	// ==================== BILL ITEMS TESTS ====================
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

	// ==================== LISTENER TESTS ====================
	@Test
	void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(foundBill).isNotNull();
		Patient mergedPatient = setupTestPatient(false);

		applicationEventPublisher.publishEvent(new PatientMergedEvent(foundBill.getBillPatient(), mergedPatient));

		Bill resultBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(resultBill).isNotNull();
		Assertions.assertThat(resultBill.getBillPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	// ==================== IO OPERATIONS TESTS ====================
	@Test
	void testIoGetPendingBills() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(foundBill).isNotNull();
		List<Bill> bills = accountingIoOperation.getPendingBills(0);
		Assertions.assertThat(bills).contains(foundBill);
	}

	@Test
	void testIoGetPendingBillsPatId() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(foundBill).isNotNull();
		List<Bill> bills = accountingIoOperation.getPendingBills(foundBill.getBillPatient().getCode());
		Assertions.assertThat(foundBill.getAmount()).isCloseTo(bills.get(0).getAmount(), offset(0.1));
	}

	@Test
	void testIoGetBills() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(foundBill).isNotNull();
		List<Bill> bills = accountingIoOperation.getBills();
		Assertions.assertThat(bills).contains(foundBill);
	}

	@Test
	void testIoGetBill() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(foundBill).isNotNull();
		Bill bill = accountingIoOperation.getBill(id);
		Assertions.assertThat(bill.getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void ioBillChecks() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(foundBill).isNotNull();
		List<Bill> bills = accountingIoOperation.getBills();
		Assertions.assertThat(bills).hasSize(1);
		Bill bill = bills.get(0);

		int id2 = setupTestBill(false);
		Bill foundBill2 = accountingBillIoOperationRepository.findById(id2).orElse(null);
		Assertions.assertThat(foundBill2).isNotNull();

		Assertions.assertThat(bill).isNotEqualTo(TimeTools.getNow()).isEqualTo(foundBill);
		foundBill2.setId(-1);
		Assertions.assertThat(bill).isNotEqualTo(foundBill2);
		Assertions.assertThat(bill.compareTo(foundBill2)).isEqualTo(id + 1);
		foundBill.setId(id);

		Assertions.assertThat(bill.hashCode()).isPositive();
	}

	@Test
	void testIoGetUsers() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(foundBillPayment).isNotNull();
		List<String> userIds = accountingIoOperation.getUsers();
		Assertions.assertThat(userIds).contains(foundBillPayment.getUser());
	}

	@Test
	void testIoGetItems() throws Exception {
		int billItemID = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(billItemID).orElse(null);
		Assertions.assertThat(foundBillItem).isNotNull();
		List<BillItems> billItems = accountingIoOperation.getItems(foundBillItem.getBill().getId());
		Assertions.assertThat(billItems).contains(foundBillItem);
	}

	@Test
	void ioGetAllItems() throws Exception {
		setupTestBillItems(false);
		List<BillItems> billItems = accountingIoOperation.getItems(0);
		Assertions.assertThat(billItems).isNotEmpty();
	}

	@Test
	void testIoGetItemsBillId() throws Exception {
		int id = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(foundBillItem).isNotNull();
		List<BillItems> billItems = accountingIoOperation.getItems(foundBillItem.getBill().getId());
		Assertions.assertThat(billItems.get(0).getItemAmount()).isCloseTo(foundBillItem.getItemAmount(), offset(0.1));
	}

	@Test
	void testIoGetPayments() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(foundBillPayment).isNotNull();
		LocalDateTime dateFrom = foundBillPayment.getDate().minusYears(1);
		LocalDateTime dateTo = TimeTools.getNow();
		List<BillPayments> billPayments = accountingIoOperation.getPayments(dateFrom, dateTo);
		Assertions.assertThat(billPayments).contains(foundBillPayment);
	}

	@Test
	void testIoGetPaymentsBillId() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(foundBillPayment).isNotNull();
		List<BillPayments> billItems = accountingIoOperation.getPayments(foundBillPayment.getBill().getId());
		Assertions.assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	void ioGetBillsByDateForPatient() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(foundBill).isNotNull();
		LocalDateTime dateFrom = foundBill.getDate().minusYears(1);
		LocalDateTime dateTo = TimeTools.getNow();
		List<Bill> billItems = accountingIoOperation.getBillsBetweenDatesWherePatient(dateFrom, dateTo, foundBill.getBillPatient());
		Assertions.assertThat(billItems).isNotEmpty();
		Assertions.assertThat(billItems.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void ioGetPendingBills() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(foundBill).isNotNull();
		List<Bill> billItems = accountingIoOperation.getPendingBillsAffiliate(foundBill.getBillPatient().getCode());
		Assertions.assertThat(billItems).isNotEmpty();
		Assertions.assertThat(billItems.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
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
		int existingId = setupTestBillItems(false);
		BillItems existingManaged = accountingBillItemsIoOperationRepository.findById(existingId).orElse(null);
		Assertions.assertThat(existingManaged).isNotNull();

		Bill bill = existingManaged.getBill();
		BillItems existingFromGui = testBillItems.setup(null, false);
		existingFromGui.setId(existingId);

		BillItems newItemFromGui = testBillItems.setup(null, false);

		List<BillItems> billItems = new ArrayList<>();
		billItems.add(existingFromGui);
		billItems.add(newItemFromGui);

		accountingIoOperation.newBillItems(bill, billItems);

		List<BillItems> persisted = accountingIoOperation.getItems(bill.getId());
		Assertions.assertThat(persisted).hasSize(2);
		Assertions.assertThat(persisted).extracting(i -> i.getBill().getId()).containsOnly(bill.getId());
		Assertions.assertThat(persisted).extracting(BillItems::getId).doesNotContain(existingId);
	}

	@Test
	void testIoNewBillPaymentsResendExistingAndNew() throws Exception {
		int existingId = setupTestBillPayments(false);
		BillPayments existingPayment = accountingBillPaymentIoOperationRepository.findById(existingId).orElse(null);
		Assertions.assertThat(existingPayment).isNotNull();

		Bill bill = existingPayment.getBill();
		BillPayments existingFromGui = new BillPayments();
		existingFromGui.setId(existingPayment.getId());
		existingFromGui.setAmount(existingPayment.getAmount());
		existingFromGui.setDate(existingPayment.getDate());
		existingFromGui.setUser(existingPayment.getUser());
		existingFromGui.setBill(bill);

		BillPayments newPayment = testBillPayments.setup(null, false);

		List<BillPayments> billPayments = new ArrayList<>();
		billPayments.add(existingFromGui);
		billPayments.add(newPayment);

		accountingIoOperation.newBillPayments(bill, billPayments);

		List<BillPayments> persisted = accountingIoOperation.getPayments(bill.getId());
		Assertions.assertThat(persisted).hasSize(2);
		Assertions.assertThat(persisted).extracting(p -> p.getBill().getId()).containsOnly(bill.getId());
		Assertions.assertThat(persisted).extracting(BillPayments::getId).doesNotContain(existingId);
	}

	@Test
	void testIoUpdateBill() throws Exception {
		int id = setupTestBill(true);
		Bill bill = accountingBillIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(bill).isNotNull();
		bill.setAmount(12.34);
		accountingIoOperation.updateBill(bill);
		Assertions.assertThat(bill.getAmount()).isCloseTo(12.34, offset(0.1));
	}

	@Test
	void testIoDeleteBill() throws Exception {
		int id = setupTestBill(true);
		Bill bill = accountingBillIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(bill).isNotNull();
		accountingIoOperation.deleteBill(bill);
		Assertions.assertThat(accountingBillIoOperationRepository.findById(id)).isEmpty();
	}

	@Test
	void testIoGetBillsTimeRange() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(foundBill).isNotNull();
		LocalDateTime dateFrom = foundBill.getDate().minusYears(1);
		LocalDateTime dateTo = TimeTools.getNow();
		List<Bill> bills = accountingIoOperation.getBillsBetweenDates(dateFrom, dateTo);
		Assertions.assertThat(bills).contains(foundBill);
	}

	@Test
	void testIoGetBillsTimeRangeAndItem() throws Exception {
		LocalDateTime dateFrom = LocalDateTime.of(10, 9, 7, 0, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(10, 9, 9, 0, 0, 0);

		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(foundBill).isNotNull();

		List<Bill> bills = accountingIoOperation.getBillsBetweenDates(dateFrom, dateTo);
		Assertions.assertThat(bills).contains(foundBill);

		bills = accountingIoOperation.getBillsBetweenDates(LocalDateTime.of(10, 1, 1, 0, 0, 0), dateFrom);
		Assertions.assertThat(bills).doesNotContain(foundBill);

		bills = accountingIoOperation.getBillsBetweenDates(dateTo, LocalDateTime.of(11, 1, 1, 0, 0, 0));
		Assertions.assertThat(bills).doesNotContain(foundBill);

		id = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(foundBillItem).isNotNull();
		foundBill = accountingBillIoOperationRepository.findById(foundBillItem.getBill().getId()).orElse(null);
		Assertions.assertThat(foundBill).isNotNull();

		bills = accountingIoOperation.getBillsBetweenDatesWhereBillItem(dateFrom, dateTo, foundBillItem);
		Assertions.assertThat(bills).contains(foundBill);

		bills = accountingIoOperation.getBillsBetweenDatesWhereBillItem(dateFrom, dateTo, (BillItems) null);
		Assertions.assertThat(bills).contains(foundBill);
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
		Assertions.assertThat(foundBill).isNotNull();
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
		Assertions.assertThat(foundBillItem).isNotNull();
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
		Assertions.assertThat(foundBillPayment).isNotNull();
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
	void testGetPaymentsForSage() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundPayment).isNotNull();

		LocalDateTime dateFrom = foundPayment.getDate().minusDays(1);
		LocalDateTime dateTo = foundPayment.getDate().plusDays(1);

		List<BillPayments> payments = accountingIoOperation.getPaymentsForSage(dateFrom, dateTo);

		assertThat(payments).isNotEmpty();
		assertThat(payments).contains(foundPayment);
	}

	@Test
	void testGetBillsForSage() throws Exception {

		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();

		LocalDateTime dateFrom = foundBill.getDate().minusDays(1);
		LocalDateTime dateTo = foundBill.getDate().plusDays(1);

		// When
		List<Bill> bills = accountingIoOperation.getBillsForSage(dateFrom, dateTo);

		assertThat(bills).isNotEmpty();
		assertThat(bills).contains(foundBill);
	}

	@Test
	void testExportSagePayments() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(foundPayment).isNotNull();

		LocalDateTime dateFrom = foundPayment.getDate().minusDays(1);
		LocalDateTime dateTo = foundPayment.getDate().plusDays(1);

		File tempFile = File.createTempFile("sage_payments_test", ".txt");

		boolean result = accountingIoOperation.exportSagePayments(tempFile, dateFrom, dateTo);

		Assertions.assertThat(result).isTrue();
		Assertions.assertThat(tempFile.exists()).isTrue();
		Assertions.assertThat(tempFile.length()).isGreaterThan(0);
		tempFile.delete();
	}

	@Test
	void testExportSageBills() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		Assertions.assertThat(foundBill).isNotNull();

		LocalDateTime dateFrom = foundBill.getDate().minusDays(1);
		LocalDateTime dateTo = foundBill.getDate().plusDays(1);

		File tempFile = File.createTempFile("sage_bills_test", ".txt");

		boolean result = accountingIoOperation.exportSageBills(tempFile, dateFrom, dateTo);

		Assertions.assertThat(result).isTrue();
		Assertions.assertThat(tempFile.exists()).isTrue();
		assertThat(tempFile.length()).isGreaterThan(0);
		tempFile.delete();
	}

	@Test
	void testFormatSagePayment() throws Exception {

		int id = setupTestBillPayments(false);
		BillPayments payment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(payment).isNotNull();

		String formatted = accountingIoOperation.formatSagePaymentForTest(payment);

		Assertions.assertThat(formatted).isNotNull();
		Assertions.assertThat(formatted).contains(String.valueOf(payment.getBill().getId()));
		Assertions.assertThat(formatted).contains(String.valueOf(payment.getAmount()).replace('.', ','));  // ← Remplacer . par ,
		Assertions.assertThat(formatted).contains(payment.getUser());
	}

	@Test
	void testFormatSageBill() throws Exception {
		int id = setupTestBill(false);
		Bill bill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(bill).isNotNull();

		String formatted = accountingIoOperation.formatSageBillForTest(bill);

		Assertions.assertThat(formatted).isNotNull();
		Assertions.assertThat(formatted).contains(String.valueOf(bill.getId()));
		Assertions.assertThat(formatted).contains(String.valueOf(bill.getAmount()).replace('.', ','));  // ← Remplacer . par ,
		Assertions.assertThat(formatted).contains(String.valueOf(bill.getBalance()).replace('.', ',')); // ← Remplacer . par ,
		Assertions.assertThat(formatted).contains(bill.getStatus());
	}
}