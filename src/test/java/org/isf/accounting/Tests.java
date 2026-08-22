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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.isf.OHCoreTestCase;
import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.accounting.service.AccountingBillIoOperationRepository;
import org.isf.accounting.service.AccountingBillItemsIoOperationRepository;
import org.isf.accounting.service.AccountingBillPaymentIoOperationRepository;
import org.isf.accounting.service.AccountingIoOperations;
import org.isf.exa.TestExam;
import org.isf.exa.model.Exam;
import org.isf.exa.service.ExamIoOperationRepository;
import org.isf.exatype.TestExamType;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.generaldata.GeneralData;
import org.isf.lab.TestLaboratory;
import org.isf.lab.model.Laboratory;
import org.isf.lab.service.LabIoOperationRepository;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicalstock.TestLot;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.medicalstockward.service.MedicalStockWardIoOperationRepository;
import org.isf.medicalstockward.service.MovementWardIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.operation.TestOperation;
import org.isf.operation.TestOperationRow;
import org.isf.operation.model.Operation;
import org.isf.operation.model.OperationRow;
import org.isf.operation.service.OperationIoOperationRepository;
import org.isf.operation.service.OperationRowIoOperationRepository;
import org.isf.opetype.TestOperationType;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.priceslist.TestPriceList;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.service.PricesListIoOperationRepository;
import org.isf.therapy.TestTherapy;
import org.isf.therapy.model.TherapyRow;
import org.isf.therapy.service.TherapyIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

class Tests extends OHCoreTestCase {

	private static TestBill testBill;
	private static TestBillItems testBillItems;
	private static TestBillPayments testBillPayments;
	private static TestPatient testPatient;
	private static TestPriceList testPriceList;
	private static TestMedicalType testMedicalType;
	private static TestMedical testMedical;
	private static TestWard testWard;
	private static TestLot testLot;
	private static TestTherapy testTherapy;
	private static TestExamType testExamType;
	private static TestExam testExam;
	private static TestLaboratory testLaboratory;
	private static TestOperationType testOperationType;
	private static TestOperation testOperation;
	private static TestOperationRow testOperationRow;

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
	MovWardBrowserManager movWardBrowserManager;
	@Autowired
	MedicalStockWardIoOperationRepository medicalStockWardIoOperationRepository;
	@Autowired
	MovementWardIoOperationRepository movementWardIoOperationRepository;
	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	@Autowired
	LotIoOperationRepository lotIoOperationRepository;
	@Autowired
	TherapyIoOperationRepository therapyIoOperationRepository;
	@Autowired
	ExamTypeIoOperationRepository examTypeIoOperationRepository;
	@Autowired
	ExamIoOperationRepository examIoOperationRepository;
	@Autowired
	LabIoOperationRepository labIoOperationRepository;
	@Autowired
	OperationTypeIoOperationRepository operationTypeIoOperationRepository;
	@Autowired
	OperationIoOperationRepository operationIoOperationRepository;
	@Autowired
	OperationRowIoOperationRepository operationRowIoOperationRepository;
	@PersistenceContext
	EntityManager entityManager;

	@BeforeAll
	static void setUpClass() {
		testBill = new TestBill();
		testBillItems = new TestBillItems();
		testBillPayments = new TestBillPayments();
		testPatient = new TestPatient();
		testPriceList = new TestPriceList();
		testMedicalType = new TestMedicalType();
		testMedical = new TestMedical();
		testWard = new TestWard();
		testLot = new TestLot();
		testTherapy = new TestTherapy();
		testExamType = new TestExamType();
		testExam = new TestExam();
		testLaboratory = new TestLaboratory();
		testOperationType = new TestOperationType();
		testOperation = new TestOperation();
		testOperationRow = new TestOperationRow();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@AfterEach
	void tearDown() {
		GeneralData.STOCKMVTONBILLSAVE = false;
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
	void mgrNewBillCreatesStockMovementsAcrossLotsFefo() throws Exception {
		GeneralData.STOCKMVTONBILLSAVE = true;

		Ward ward = setupWard();
		Medical medical = setupMedical();
		Lot earlierLot = setupLot(medical, "LOT1", LocalDateTime.of(2025, 1, 1, 0, 0));
		Lot laterLot = setupLot(medical, "LOT2", LocalDateTime.of(2026, 1, 1, 0, 0));
		seedWardStock(ward, medical, earlierLot, 5.0f);
		seedWardStock(ward, medical, laterLot, 10.0f);

		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);

		Bill bill = testBill.setup(priceList, patient, null, false);
		bill.setWard(ward);

		BillItems medicalItem = testBillItems.setup(null, false);
		medicalItem.setPriceID("MED" + medical.getCode());
		medicalItem.setItemQuantity(8);
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(medicalItem);

		Bill savedBill = billBrowserManager.newBill(bill, billItems, new ArrayList<>());

		List<MovementWard> movements = movWardBrowserManager.getMovementWardByBillId(savedBill.getId());
		assertThat(movements).hasSize(2);

		MovementWard earlierLotMovement = findMovementByLotCode(movements, "LOT1");
		MovementWard laterLotMovement = findMovementByLotCode(movements, "LOT2");
		assertThat(earlierLotMovement.getQuantity()).isEqualTo(5.0);
		assertThat(laterLotMovement.getQuantity()).isEqualTo(3.0);

		// the earlier-expiring lot is consumed first and fully (5), the remainder (3) comes from the later lot
		assertThat(outQuantityOf(ward, medical, "LOT1")).isEqualTo(5.0f);
		assertThat(outQuantityOf(ward, medical, "LOT2")).isEqualTo(3.0f);
	}

	@Test
	void mgrNewBillWithStockMvtDisabledCreatesNoMovements() throws Exception {
		GeneralData.STOCKMVTONBILLSAVE = false;

		Ward ward = setupWard();
		Medical medical = setupMedical();
		Lot lot = setupLot(medical, "LOT1", LocalDateTime.of(2025, 1, 1, 0, 0));
		seedWardStock(ward, medical, lot, 10.0f);

		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);

		Bill bill = testBill.setup(priceList, patient, null, false);
		bill.setWard(ward);

		BillItems medicalItem = testBillItems.setup(null, false);
		medicalItem.setPriceID("MED" + medical.getCode());
		medicalItem.setItemQuantity(3);
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(medicalItem);

		Bill savedBill = billBrowserManager.newBill(bill, billItems, new ArrayList<>());

		assertThat(movWardBrowserManager.getMovementWardByBillId(savedBill.getId())).isEmpty();
		assertThat(outQuantityOf(ward, medical, "LOT1")).isEqualTo(0.0f);
	}

	@Test
	void mgrUpdateBillRemovingMedicalItemReversesMovement() throws Exception {
		GeneralData.STOCKMVTONBILLSAVE = true;

		Ward ward = setupWard();
		Medical medical = setupMedical();
		Lot lot = setupLot(medical, "LOT1", LocalDateTime.of(2025, 1, 1, 0, 0));
		seedWardStock(ward, medical, lot, 10.0f);

		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);

		Bill bill = testBill.setup(priceList, patient, null, false);
		bill.setWard(ward);

		BillItems medicalItem = testBillItems.setup(null, false);
		medicalItem.setPriceID("MED" + medical.getCode());
		medicalItem.setItemQuantity(4);
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(medicalItem);

		Bill savedBill = billBrowserManager.newBill(bill, billItems, new ArrayList<>());
		assertThat(movWardBrowserManager.getMovementWardByBillId(savedBill.getId())).hasSize(1);
		assertThat(outQuantityOf(ward, medical, "LOT1")).isEqualTo(4.0f);

		// when: the item is removed on update
		Bill updateBill = accountingBillIoOperationRepository.findById(savedBill.getId()).orElse(null);
		assertThat(updateBill).isNotNull();
		updateBill.setWard(ward);
		billBrowserManager.updateBill(updateBill, new ArrayList<>(), new ArrayList<>());

		// then: the movement is reversed and stock restored
		assertThat(movWardBrowserManager.getMovementWardByBillId(savedBill.getId())).isEmpty();
		assertThat(outQuantityOf(ward, medical, "LOT1")).isEqualTo(0.0f);
	}

	@Test
	void mgrUpdateBillAddingMedicalItemCreatesMovement() throws Exception {
		GeneralData.STOCKMVTONBILLSAVE = true;

		Ward ward = setupWard();
		Medical medical = setupMedical();
		Lot lot = setupLot(medical, "LOT1", LocalDateTime.of(2025, 1, 1, 0, 0));
		seedWardStock(ward, medical, lot, 10.0f);

		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);

		Bill bill = testBill.setup(priceList, patient, null, false);
		bill.setWard(ward);

		Bill savedBill = billBrowserManager.newBill(bill, new ArrayList<>(), new ArrayList<>());
		assertThat(movWardBrowserManager.getMovementWardByBillId(savedBill.getId())).isEmpty();

		Bill updateBill = accountingBillIoOperationRepository.findById(savedBill.getId()).orElse(null);
		assertThat(updateBill).isNotNull();
		updateBill.setWard(ward);

		BillItems medicalItem = testBillItems.setup(null, false);
		medicalItem.setPriceID("MED" + medical.getCode());
		medicalItem.setItemQuantity(4);
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(medicalItem);

		billBrowserManager.updateBill(updateBill, billItems, new ArrayList<>());

		assertThat(movWardBrowserManager.getMovementWardByBillId(savedBill.getId())).hasSize(1);
		assertThat(outQuantityOf(ward, medical, "LOT1")).isEqualTo(4.0f);
	}

	@Test
	void mgrUpdateBillUnchangedMedicalItemLeavesMovementUntouched() throws Exception {
		GeneralData.STOCKMVTONBILLSAVE = true;

		Ward ward = setupWard();
		Medical medical = setupMedical();
		Lot lot = setupLot(medical, "LOT1", LocalDateTime.of(2025, 1, 1, 0, 0));
		seedWardStock(ward, medical, lot, 10.0f);

		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);

		Bill bill = testBill.setup(priceList, patient, null, false);
		bill.setWard(ward);

		BillItems medicalItem = testBillItems.setup(null, false);
		medicalItem.setPriceID("MED" + medical.getCode());
		medicalItem.setItemQuantity(4);
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(medicalItem);

		Bill savedBill = billBrowserManager.newBill(bill, billItems, new ArrayList<>());
		List<MovementWard> originalMovements = movWardBrowserManager.getMovementWardByBillId(savedBill.getId());
		assertThat(originalMovements).hasSize(1);
		int originalMovementCode = originalMovements.get(0).getCode();

		// when: the bill is re-saved with the exact same medical item (same quantity, same ward)
		Bill updateBill = accountingBillIoOperationRepository.findById(savedBill.getId()).orElse(null);
		assertThat(updateBill).isNotNull();
		updateBill.setWard(ward);

		BillItems sameMedicalItem = testBillItems.setup(null, false);
		sameMedicalItem.setPriceID("MED" + medical.getCode());
		sameMedicalItem.setItemQuantity(4);
		List<BillItems> sameBillItems = new ArrayList<>();
		sameBillItems.add(sameMedicalItem);

		billBrowserManager.updateBill(updateBill, sameBillItems, new ArrayList<>());

		// then: the exact same movement survives untouched (not reversed and recreated) and stock is
		// unchanged (would have been double-deducted if the item had been re-charged)
		List<MovementWard> movementsAfterUpdate = movWardBrowserManager.getMovementWardByBillId(savedBill.getId());
		assertThat(movementsAfterUpdate).hasSize(1);
		assertThat(movementsAfterUpdate.get(0).getCode()).isEqualTo(originalMovementCode);
		assertThat(outQuantityOf(ward, medical, "LOT1")).isEqualTo(4.0f);
	}

	@Test
	void mgrUpdateBillWithStockMvtDisabledLeavesExistingMovementsUntouched() throws Exception {
		GeneralData.STOCKMVTONBILLSAVE = true;

		Ward ward = setupWard();
		Medical medical = setupMedical();
		Lot lot = setupLot(medical, "LOT1", LocalDateTime.of(2025, 1, 1, 0, 0));
		seedWardStock(ward, medical, lot, 10.0f);

		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);

		Bill bill = testBill.setup(priceList, patient, null, false);
		bill.setWard(ward);

		BillItems medicalItem = testBillItems.setup(null, false);
		medicalItem.setPriceID("MED" + medical.getCode());
		medicalItem.setItemQuantity(4);
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(medicalItem);

		Bill savedBill = billBrowserManager.newBill(bill, billItems, new ArrayList<>());
		assertThat(movWardBrowserManager.getMovementWardByBillId(savedBill.getId())).hasSize(1);

		// when: STOCKMVTONBILLSAVE is turned off before the next save
		GeneralData.STOCKMVTONBILLSAVE = false;
		Bill updateBill = accountingBillIoOperationRepository.findById(savedBill.getId()).orElse(null);
		assertThat(updateBill).isNotNull();
		updateBill.setWard(ward);
		billBrowserManager.updateBill(updateBill, billItems, new ArrayList<>());

		// then: no error, and since reconciliation didn't run, the existing movement is untouched
		// (this flag only gates whether NEW reconciliation/movements happen, not a cleanup of old ones)
		assertThat(movWardBrowserManager.getMovementWardByBillId(savedBill.getId())).hasSize(1);
	}

	@Test
	void mgrDeleteBillReversesLinkedMovements() throws Exception {
		GeneralData.STOCKMVTONBILLSAVE = true;

		Ward ward = setupWard();
		Medical medical = setupMedical();
		Lot lot = setupLot(medical, "LOT1", LocalDateTime.of(2025, 1, 1, 0, 0));
		seedWardStock(ward, medical, lot, 10.0f);

		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);

		Bill bill = testBill.setup(priceList, patient, null, false);
		bill.setWard(ward);

		BillItems medicalItem = testBillItems.setup(null, false);
		medicalItem.setPriceID("MED" + medical.getCode());
		medicalItem.setItemQuantity(4);
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(medicalItem);

		Bill savedBill = billBrowserManager.newBill(bill, billItems, new ArrayList<>());
		assertThat(movWardBrowserManager.getMovementWardByBillId(savedBill.getId())).hasSize(1);

		billBrowserManager.deleteBill(savedBill);

		// AccountingIoOperations.deleteBill's bulk BillItems delete clears the persistence context
		// (@Modifying(clearAutomatically = true)), so the Bill delete that follows lands on a
		// freshly-reloaded instance and is never flushed by anything afterward. A raw query here
		// forces Hibernate's auto-flush before the checks below re-read this bill/its movements.
		Number remainingBillRows = (Number) entityManager.createNativeQuery("SELECT COUNT(*) FROM OH_BILLS WHERE BLL_ID = :billId")
			.setParameter("billId", savedBill.getId())
			.getSingleResult();
		assertThat(remainingBillRows.intValue()).isZero();

		assertThat(movWardBrowserManager.getMovementWardByBillId(savedBill.getId())).isEmpty();
		assertThat(outQuantityOf(ward, medical, "LOT1")).isEqualTo(0.0f);
		entityManager.clear();
		assertThat(accountingBillIoOperationRepository.findById(savedBill.getId())).isEmpty();
	}

	@Test
	void mgrNewBillInsufficientStockRollsBackFully() throws Exception {
		GeneralData.STOCKMVTONBILLSAVE = true;

		Ward ward = setupWard();
		Medical medical = setupMedical();
		Lot lot = setupLot(medical, "LOT1", LocalDateTime.of(2025, 1, 1, 0, 0));
		seedWardStock(ward, medical, lot, 3.0f);

		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);

		Bill bill = testBill.setup(priceList, patient, null, false);
		bill.setWard(ward);

		BillItems medicalItem = testBillItems.setup(null, false);
		medicalItem.setPriceID("MED" + medical.getCode());
		medicalItem.setItemQuantity(10);
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(medicalItem);

		// the whole save is @Transactional(rollbackFor = OHServiceException.class): the shortage is
		// caught before any movement is created, and Spring rolls back everything else on this
		// exception (bill/items already flushed in this method call) once the transaction completes -
		// not observable via a same-transaction read here (this test class's transaction wraps the
		// whole test method), so this only asserts the failure itself, matching the established
		// pattern for the pre-existing validation-failure test right above.
		assertThatThrownBy(() -> billBrowserManager.newBill(bill, billItems, new ArrayList<>()))
			.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void mgrNewBillMarksTherapyLinkedItemBilled() throws Exception {
		GeneralData.STOCKMVTONBILLSAVE = false;

		Medical medical = setupMedical();
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		TherapyRow therapyRow = setupTherapyRow(patient, medical, 10.0);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);

		Bill bill = testBill.setup(priceList, patient, null, false);

		BillItems medicalItem = testBillItems.setup(null, false);
		medicalItem.setPriceID("MED" + medical.getCode());
		medicalItem.setItemQuantity(6);
		medicalItem.setItemGroup("MED");
		medicalItem.setPrescriptionId(therapyRow.getTherapyID());
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(medicalItem);

		billBrowserManager.newBill(bill, billItems, new ArrayList<>());

		assertThat(therapyIoOperationRepository.findById(therapyRow.getTherapyID()).orElseThrow().getQtyBougth()).isEqualTo(6.0);
	}

	@Test
	void mgrNewBillMarksLaboratoryAndOperationLinkedItemsBilled() throws Exception {
		GeneralData.STOCKMVTONBILLSAVE = false;

		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		Laboratory laboratory = setupLaboratory(patient);
		OperationRow operationRow = setupOperationRow();
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);

		Bill bill = testBill.setup(priceList, patient, null, false);

		BillItems examItem = testBillItems.setup(null, false);
		examItem.setPriceID("EXA" + laboratory.getExam().getCode());
		examItem.setItemQuantity(1);
		examItem.setItemGroup("EXA");
		examItem.setPrescriptionId(laboratory.getCode());

		BillItems operationItem = testBillItems.setup(null, false);
		operationItem.setPriceID("OPE" + operationRow.getOperation().getCode());
		operationItem.setItemQuantity(1);
		operationItem.setItemGroup("OPE");
		operationItem.setPrescriptionId(operationRow.getId());

		List<BillItems> billItems = new ArrayList<>();
		billItems.add(examItem);
		billItems.add(operationItem);

		Bill savedBill = billBrowserManager.newBill(bill, billItems, new ArrayList<>());

		assertThat(labIoOperationRepository.findById(laboratory.getCode()).orElseThrow().getBillId()).isEqualTo(savedBill.getId());
		assertThat(operationRowIoOperationRepository.findById(operationRow.getId()).getBill().getId()).isEqualTo(savedBill.getId());
	}

	@Test
	void mgrUpdateBillRemovingPrescriptionItemReversesTherapyMarking() throws Exception {
		GeneralData.STOCKMVTONBILLSAVE = false;

		Medical medical = setupMedical();
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		TherapyRow therapyRow = setupTherapyRow(patient, medical, 10.0);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);

		Bill bill = testBill.setup(priceList, patient, null, false);

		BillItems medicalItem = testBillItems.setup(null, false);
		medicalItem.setPriceID("MED" + medical.getCode());
		medicalItem.setItemQuantity(6);
		medicalItem.setItemGroup("MED");
		medicalItem.setPrescriptionId(therapyRow.getTherapyID());
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(medicalItem);

		Bill savedBill = billBrowserManager.newBill(bill, billItems, new ArrayList<>());
		assertThat(therapyIoOperationRepository.findById(therapyRow.getTherapyID()).orElseThrow().getQtyBougth()).isEqualTo(6.0);

		Bill updateBill = accountingBillIoOperationRepository.findById(savedBill.getId()).orElse(null);
		assertThat(updateBill).isNotNull();
		billBrowserManager.updateBill(updateBill, new ArrayList<>(), new ArrayList<>());

		assertThat(therapyIoOperationRepository.findById(therapyRow.getTherapyID()).orElseThrow().getQtyBougth()).isEqualTo(0.0);
	}

	@Test
	void mgrUpdateBillUnchangedPrescriptionItemLeavesMarkingUntouched() throws Exception {
		GeneralData.STOCKMVTONBILLSAVE = false;

		Medical medical = setupMedical();
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		TherapyRow therapyRow = setupTherapyRow(patient, medical, 10.0);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);

		Bill bill = testBill.setup(priceList, patient, null, false);

		BillItems medicalItem = testBillItems.setup(null, false);
		medicalItem.setPriceID("MED" + medical.getCode());
		medicalItem.setItemQuantity(6);
		medicalItem.setItemGroup("MED");
		medicalItem.setPrescriptionId(therapyRow.getTherapyID());
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(medicalItem);

		Bill savedBill = billBrowserManager.newBill(bill, billItems, new ArrayList<>());
		assertThat(therapyIoOperationRepository.findById(therapyRow.getTherapyID()).orElseThrow().getQtyBougth()).isEqualTo(6.0);

		// when: the bill is re-saved with the exact same prescription-linked item (same quantity)
		Bill updateBill = accountingBillIoOperationRepository.findById(savedBill.getId()).orElse(null);
		assertThat(updateBill).isNotNull();

		BillItems sameMedicalItem = testBillItems.setup(null, false);
		sameMedicalItem.setPriceID("MED" + medical.getCode());
		sameMedicalItem.setItemQuantity(6);
		sameMedicalItem.setItemGroup("MED");
		sameMedicalItem.setPrescriptionId(therapyRow.getTherapyID());
		List<BillItems> sameBillItems = new ArrayList<>();
		sameBillItems.add(sameMedicalItem);

		billBrowserManager.updateBill(updateBill, sameBillItems, new ArrayList<>());

		// then: qtyBougth is left exactly as it was (not doubled, not reset)
		assertThat(therapyIoOperationRepository.findById(therapyRow.getTherapyID()).orElseThrow().getQtyBougth()).isEqualTo(6.0);
	}

	@Test
	void mgrUpdateBillChangingTherapyItemQuantityAdjustsQtyBougthByDelta() throws Exception {
		GeneralData.STOCKMVTONBILLSAVE = false;

		Medical medical = setupMedical();
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		TherapyRow therapyRow = setupTherapyRow(patient, medical, 10.0);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);

		Bill bill = testBill.setup(priceList, patient, null, false);

		BillItems medicalItem = testBillItems.setup(null, false);
		medicalItem.setPriceID("MED" + medical.getCode());
		medicalItem.setItemQuantity(6);
		medicalItem.setItemGroup("MED");
		medicalItem.setPrescriptionId(therapyRow.getTherapyID());
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(medicalItem);

		Bill savedBill = billBrowserManager.newBill(bill, billItems, new ArrayList<>());
		assertThat(therapyIoOperationRepository.findById(therapyRow.getTherapyID()).orElseThrow().getQtyBougth()).isEqualTo(6.0);

		Bill updateBill = accountingBillIoOperationRepository.findById(savedBill.getId()).orElse(null);
		assertThat(updateBill).isNotNull();

		BillItems increasedMedicalItem = testBillItems.setup(null, false);
		increasedMedicalItem.setPriceID("MED" + medical.getCode());
		increasedMedicalItem.setItemQuantity(9);
		increasedMedicalItem.setItemGroup("MED");
		increasedMedicalItem.setPrescriptionId(therapyRow.getTherapyID());
		List<BillItems> increasedBillItems = new ArrayList<>();
		increasedBillItems.add(increasedMedicalItem);

		billBrowserManager.updateBill(updateBill, increasedBillItems, new ArrayList<>());

		// the delta (+3) is applied directly to qtyBougth (6 -> 9), not reversed to 0 and remarked
		assertThat(therapyIoOperationRepository.findById(therapyRow.getTherapyID()).orElseThrow().getQtyBougth()).isEqualTo(9.0);
	}

	@Test
	void mgrDeleteBillReversesAllPrescriptionMarkings() throws Exception {
		GeneralData.STOCKMVTONBILLSAVE = false;

		Medical medical = setupMedical();
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		TherapyRow therapyRow = setupTherapyRow(patient, medical, 10.0);
		Laboratory laboratory = setupLaboratory(patient);
		OperationRow operationRow = setupOperationRow();
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);

		Bill bill = testBill.setup(priceList, patient, null, false);

		BillItems medicalItem = testBillItems.setup(null, false);
		medicalItem.setPriceID("MED" + medical.getCode());
		medicalItem.setItemQuantity(6);
		medicalItem.setItemGroup("MED");
		medicalItem.setPrescriptionId(therapyRow.getTherapyID());

		BillItems examItem = testBillItems.setup(null, false);
		examItem.setPriceID("EXA" + laboratory.getExam().getCode());
		examItem.setItemQuantity(1);
		examItem.setItemGroup("EXA");
		examItem.setPrescriptionId(laboratory.getCode());

		BillItems operationItem = testBillItems.setup(null, false);
		operationItem.setPriceID("OPE" + operationRow.getOperation().getCode());
		operationItem.setItemQuantity(1);
		operationItem.setItemGroup("OPE");
		operationItem.setPrescriptionId(operationRow.getId());

		List<BillItems> billItems = new ArrayList<>();
		billItems.add(medicalItem);
		billItems.add(examItem);
		billItems.add(operationItem);

		Bill savedBill = billBrowserManager.newBill(bill, billItems, new ArrayList<>());
		assertThat(therapyIoOperationRepository.findById(therapyRow.getTherapyID()).orElseThrow().getQtyBougth()).isEqualTo(6.0);
		assertThat(labIoOperationRepository.findById(laboratory.getCode()).orElseThrow().getBillId()).isEqualTo(savedBill.getId());
		assertThat(operationRowIoOperationRepository.findById(operationRow.getId()).getBill()).isNotNull();

		billBrowserManager.deleteBill(savedBill);

		assertThat(therapyIoOperationRepository.findById(therapyRow.getTherapyID()).orElseThrow().getQtyBougth()).isEqualTo(0.0);
		assertThat(labIoOperationRepository.findById(laboratory.getCode()).orElseThrow().getBillId()).isNull();
		assertThat(operationRowIoOperationRepository.findById(operationRow.getId()).getBill()).isNull();
	}

	@Test
	void mgrIsPrescriptionAlreadyBilledAndPaidTrueOnlyForClosedBill() throws Exception {
		GeneralData.STOCKMVTONBILLSAVE = false;

		Medical medical = setupMedical();
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		TherapyRow therapyRow = setupTherapyRow(patient, medical, 10.0);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);

		Bill closedBill = testBill.setup(priceList, patient, null, false);
		closedBill.setStatus("C");
		closedBill.setBalance(0.0);

		BillItems medicalItem = testBillItems.setup(null, false);
		medicalItem.setPriceID("MED" + medical.getCode());
		medicalItem.setItemQuantity(6);
		medicalItem.setItemGroup("MED");
		medicalItem.setPrescriptionId(therapyRow.getTherapyID());
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(medicalItem);

		billBrowserManager.newBill(closedBill, billItems, new ArrayList<>());

		assertThat(billBrowserManager.isPrescriptionAlreadyBilledAndPaid(patient.getCode(), therapyRow.getTherapyID(), "MED")).isTrue();

		// a different patient never billed this prescription
		Patient otherPatient = testPatient.setup(true);
		patientIoOperationRepository.saveAndFlush(otherPatient);
		assertThat(billBrowserManager.isPrescriptionAlreadyBilledAndPaid(otherPatient.getCode(), therapyRow.getTherapyID(), "MED")).isFalse();

		// an open bill referencing the same prescription doesn't count as "already billed and paid"
		TherapyRow otherTherapyRow = setupTherapyRow(patient, medical, 10.0);
		Bill openBill = testBill.setup(priceList, patient, null, false);

		BillItems openMedicalItem = testBillItems.setup(null, false);
		openMedicalItem.setPriceID("MED" + medical.getCode());
		openMedicalItem.setItemQuantity(4);
		openMedicalItem.setItemGroup("MED");
		openMedicalItem.setPrescriptionId(otherTherapyRow.getTherapyID());
		List<BillItems> openBillItems = new ArrayList<>();
		openBillItems.add(openMedicalItem);

		billBrowserManager.newBill(openBill, openBillItems, new ArrayList<>());

		assertThat(billBrowserManager.isPrescriptionAlreadyBilledAndPaid(patient.getCode(), otherTherapyRow.getTherapyID(), "MED")).isFalse();
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

	private Ward setupWard() throws OHException {
		Ward ward = testWard.setup(false);
		wardIoOperationRepository.saveAndFlush(ward);
		return ward;
	}

	private Medical setupMedical() throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		return medical;
	}

	private Lot setupLot(Medical medical, String code, LocalDateTime dueDate) throws OHException {
		Lot lot = testLot.setup(medical, false);
		lot.setCode(code);
		lot.setDueDate(dueDate);
		lotIoOperationRepository.saveAndFlush(lot);
		return lot;
	}

	private void seedWardStock(Ward ward, Medical medical, Lot lot, float inQuantity) {
		MedicalWard medicalWard = new MedicalWard(ward, medical, inQuantity, 0.0f, lot);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard);
	}

	/**
	 * {@code MedicalStockWardIoOperationRepository.updateOutQuantity(...)}/{@code updateInQuantity(...)}
	 * are native bulk {@code @Modifying} updates with no {@code clearAutomatically}, so within a
	 * single (test) transaction Hibernate's first-level cache keeps returning the pre-update
	 * {@link MedicalWard} instance unless the persistence context is cleared first - forcing a fresh
	 * read straight from the DB, where the native update already landed.
	 */
	private float outQuantityOf(Ward ward, Medical medical, String lotCode) throws Exception {
		entityManager.clear();
		return movWardBrowserManager.getMedicalWardByWardMedicalAndLot(ward.getCode(), medical.getCode(), lotCode).getOut_quantity();
	}

	private MovementWard findMovementByLotCode(List<MovementWard> movements, String lotCode) {
		for (MovementWard movement : movements) {
			if (movement.getLot().getCode().equals(lotCode)) {
				return movement;
			}
		}
		throw new AssertionError("No movement found for lot " + lotCode);
	}

	private TherapyRow setupTherapyRow(Patient patient, Medical medical, Double qty) throws OHException {
		TherapyRow therapyRow = testTherapy.setup(patient, medical, false);
		therapyRow.setQty(qty);
		therapyRow.setQtyBougth(0.0);
		therapyIoOperationRepository.saveAndFlush(therapyRow);
		return therapyRow;
	}

	private Laboratory setupLaboratory(Patient patient) throws OHException {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		labIoOperationRepository.saveAndFlush(laboratory);
		return laboratory;
	}

	private OperationRow setupOperationRow() throws OHException {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		OperationRow operationRow = testOperationRow.setup(operation, true);
		operationRowIoOperationRepository.saveAndFlush(operationRow);
		return operationRow;
	}
}
