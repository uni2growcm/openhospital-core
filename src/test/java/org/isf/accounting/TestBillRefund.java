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
package org.isf.accounting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.accounting.dto.RefundBillItemDto;
import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.accounting.service.AccountingBillIoOperationRepository;
import org.isf.accounting.service.AccountingBillItemsIoOperationRepository;
import org.isf.accounting.service.AccountingBillPaymentIoOperationRepository;
import org.isf.accounting.service.AccountingIoOperations;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.priceslist.TestPriceList;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.service.PricesListIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

class TestBillRefund extends OHCoreTestCase {

	private static TestBill testBill;
	private static TestBillItems testBillItems;
	private static TestBillPayments testBillPayments;
	private static TestPatient testPatient;
	private static TestPriceList testPriceList;

	@Autowired private BillBrowserManager billBrowserManager;
	@Autowired private AccountingIoOperations accountingIoOperations;
	@Autowired private AccountingBillIoOperationRepository billRepository;
	@Autowired private AccountingBillItemsIoOperationRepository billItemsRepository;
	@Autowired private AccountingBillPaymentIoOperationRepository billPaymentRepository;
	@Autowired private PatientIoOperationRepository patientRepository;
	@Autowired private PricesListIoOperationRepository priceListRepository;

	@BeforeAll
	static void setUpClass() {
		testBill = new TestBill();
		testBillItems = new TestBillItems();
		testBillPayments = new TestBillPayments();
		testPatient = new TestPatient();
		testPriceList = new TestPriceList();
	}

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		cleanH2InMemoryDb();
	}

	@Test
	@DisplayName("RefundBillItemDto: getRefundableQty = totalQty - alreadyRefundedQty")
	void dtoRefundableQtyIsComputedCorrectly() throws OHException {
		BillItems item = testBillItems.setup(null, false); // itemQuantity = 20
		RefundBillItemDto dto = new RefundBillItemDto(item, 5);

		assertThat(dto.getRefundableQty()).isEqualTo(15);
		assertThat(dto.getRefundQty()).isZero();
		assertThat(dto.getAlreadyRefundedQty()).isEqualTo(5);
	}

	@Test
	@DisplayName("RefundBillItemDto: refundableQty is zero when all units already refunded")
	void dtoRefundableQtyIsZeroWhenFullyRefunded() throws OHException {
		BillItems item = testBillItems.setup(null, false); // itemQuantity = 20
		RefundBillItemDto dto = new RefundBillItemDto(item, 20);

		assertThat(dto.getRefundableQty()).isZero();
	}

	@Test
	@DisplayName("IoOps: getRefundBills returns bills whose parentId matches the given billId")
	void ioGetRefundBillsReturnsLinkedBills() throws OHException, OHServiceException {
		Bill original = persistBill("C");
		Bill refund = persistRefundBill(original, 50.0);

		List<Bill> refundBills = accountingIoOperations.getRefundBills(original.getId());

		assertThat(refundBills).hasSize(1);
		assertThat(refundBills.get(0).getId()).isEqualTo(refund.getId());
		assertThat(refundBills.get(0).getParentId()).isEqualTo(original.getId());
	}

	@Test
	@DisplayName("IoOps: getRefundBills returns empty list when no refund bill exists")
	void ioGetRefundBillsReturnsEmptyWhenNone() throws OHException, OHServiceException {
		Bill original = persistBill("C");

		List<Bill> refundBills = accountingIoOperations.getRefundBills(original.getId());

		assertThat(refundBills).isEmpty();
	}

	@Test
	@DisplayName("IoOps: getRefundedItems returns items stored under refund bills")
	void ioGetRefundedItemsReturnsItemsFromRefundBills() throws OHException, OHServiceException {
		Bill original = persistBill("C");
		Bill refund = persistRefundBill(original, 30.0);
		BillItems refundItem = persistBillItem(refund, "Amoxicillin", 10.0, 3);

		List<BillItems> refundedItems = accountingIoOperations.getRefundedItems(original.getId());

		assertThat(refundedItems).hasSize(1);
		assertThat(refundedItems.get(0).getItemDescription()).isEqualTo("Amoxicillin");
		assertThat(refundedItems.get(0).getItemQuantity()).isEqualTo(3);
	}

	@Test
	@DisplayName("IoOps: getRefundedItems returns empty list when no refund bills exist")
	void ioGetRefundedItemsReturnsEmptyWhenNone() throws OHException, OHServiceException {
		Bill original = persistBill("C");

		List<BillItems> refundedItems = accountingIoOperations.getRefundedItems(original.getId());

		assertThat(refundedItems).isEmpty();
	}

	@Test
	@DisplayName("Manager: getRefundBills delegates correctly and returns refund bills")
	void mgrGetRefundBillsReturnsRefundBills() throws OHException, OHServiceException {
		Bill original = persistBill("C");
		persistRefundBill(original, 20.0);
		persistRefundBill(original, 15.0);

		List<Bill> result = billBrowserManager.getRefundBills(original.getId());

		assertThat(result).hasSize(2);
		assertThat(result).allMatch(b -> original.getId() == b.getParentId());
	}

	@Test
	@DisplayName("Manager: getRefundItems returns empty list for billId 0")
	void mgrGetRefundItemsReturnEmptyForZeroId() throws OHServiceException {
		List<RefundBillItemDto> result = billBrowserManager.getRefundItems(0);

		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("Manager: getRefundItems groups duplicate descriptions into a single row")
	void mgrGetRefundItemsGroupsByDescription() throws OHException, OHServiceException {
		Bill original = persistBill("C");
		// Two entries of the same item on the same bill
		persistBillItem(original, "Paracetamol", 5.0, 2);
		persistBillItem(original, "Paracetamol", 5.0, 3);

		List<RefundBillItemDto> result = billBrowserManager.getRefundItems(original.getId());

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getBillItem().getItemDescription()).isEqualTo("Paracetamol");
		assertThat(result.get(0).getBillItem().getItemQuantity()).isEqualTo(5); // 2 + 3
	}

	@Test
	@DisplayName("Manager: getRefundItems keeps distinct items as separate rows")
	void mgrGetRefundItemsKeepsDistinctItems() throws OHException, OHServiceException {
		Bill original = persistBill("C");
		persistBillItem(original, "Amoxicillin", 8.0, 1);
		persistBillItem(original, "Ibuprofen", 4.0, 2);

		List<RefundBillItemDto> result = billBrowserManager.getRefundItems(original.getId());

		assertThat(result).hasSize(2);
		assertThat(result).extracting(dto -> dto.getBillItem().getItemDescription())
			.containsExactlyInAnyOrder("Amoxicillin", "Ibuprofen");
	}

	@Test
	@DisplayName("Manager: getRefundItems sets alreadyRefundedQty from existing refund bills")
	void mgrGetRefundItemsSetsAlreadyRefundedQty() throws OHException, OHServiceException {
		Bill original = persistBill("C");
		persistBillItem(original, "Aspirin", 2.0, 10);

		// First partial refund: 3 units
		Bill refund1 = persistRefundBill(original, 6.0);
		persistBillItem(refund1, "Aspirin", 2.0, 3);

		List<RefundBillItemDto> result = billBrowserManager.getRefundItems(original.getId());

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getAlreadyRefundedQty()).isEqualTo(3);
		assertThat(result.get(0).getRefundableQty()).isEqualTo(7); // 10 - 3
	}

	@Test
	@DisplayName("Manager: getRefundItems accumulates alreadyRefundedQty across multiple refund bills")
	void mgrGetRefundItemsAccumulatesAcrossMultipleRefunds() throws OHException, OHServiceException {
		Bill original = persistBill("C");
		persistBillItem(original, "Aspirin", 2.0, 10);

		Bill refund1 = persistRefundBill(original, 4.0);
		persistBillItem(refund1, "Aspirin", 2.0, 2);

		Bill refund2 = persistRefundBill(original, 6.0);
		persistBillItem(refund2, "Aspirin", 2.0, 3);

		List<RefundBillItemDto> result = billBrowserManager.getRefundItems(original.getId());

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getAlreadyRefundedQty()).isEqualTo(5); // 2 + 3
		assertThat(result.get(0).getRefundableQty()).isEqualTo(5);      // 10 - 5
	}

	@Test
	@DisplayName("Manager: refundBill persists the refund bill with parentId pointing to original")
	void mgrRefundBillCreatesRefundBillWithParentId() throws OHException, OHServiceException {
		Bill original = persistBill("C");
		persistBillItem(original, "Aspirin", 5.0, 4);

		Bill refundBill = buildRefundBill(original, 10.0);
		List<BillItems> items = buildRefundItemsList(original, "Aspirin", 5.0, 2);
		List<BillPayments> payments = buildNegativePayment(-10.0);

		Bill saved = billBrowserManager.refundBill(original, refundBill, items, payments);

		assertThat(saved.getId()).isPositive();
		assertThat(saved.getParentId()).isEqualTo(original.getId());
		assertThat(saved.getStatus()).isEqualTo("C");
	}

	@Test
	@DisplayName("Manager: refundBill saves items under the new refund bill")
	void mgrRefundBillSavesItemsWithChosenQty() throws OHException, OHServiceException {
		Bill original = persistBill("C");
		persistBillItem(original, "Ibuprofen", 4.0, 6);

		Bill refundBill = buildRefundBill(original, 8.0);
		List<BillItems> items = buildRefundItemsList(original, "Ibuprofen", 4.0, 2);
		List<BillPayments> payments = buildNegativePayment(-8.0);

		Bill saved = billBrowserManager.refundBill(original, refundBill, items, payments);

		List<BillItems> savedItems = accountingIoOperations.getItems(saved.getId());
		assertThat(savedItems).hasSize(1);
		assertThat(savedItems.get(0).getItemDescription()).isEqualTo("Ibuprofen");
		assertThat(savedItems.get(0).getItemQuantity()).isEqualTo(2);
	}

	@Test
	@DisplayName("Manager: refundBill records a negative payment equal to minus the refund amount")
	void mgrRefundBillSavesNegativePayment() throws OHException, OHServiceException {
		Bill original = persistBill("C");
		persistBillItem(original, "Paracetamol", 3.0, 5);

		Bill refundBill = buildRefundBill(original, 9.0);
		List<BillItems> items = buildRefundItemsList(original, "Paracetamol", 3.0, 3);
		List<BillPayments> payments = buildNegativePayment(-9.0);

		Bill saved = billBrowserManager.refundBill(original, refundBill, items, payments);

		List<BillPayments> savedPayments = accountingIoOperations.getPayments(saved.getId());
		assertThat(savedPayments).hasSize(1);
		assertThat(savedPayments.get(0).getAmount()).isCloseTo(-9.0, offset(0.01));
	}

	@Test
	@DisplayName("Manager: refundBill is visible via getRefundBills on the original bill")
	void mgrRefundBillIsRetrievableViaGetRefundBills() throws OHException, OHServiceException {
		Bill original = persistBill("C");
		persistBillItem(original, "Aspirin", 2.0, 10);

		Bill refundBill = buildRefundBill(original, 4.0);
		List<BillItems> items = buildRefundItemsList(original, "Aspirin", 2.0, 2);
		List<BillPayments> payments = buildNegativePayment(-4.0);

		Bill saved = billBrowserManager.refundBill(original, refundBill, items, payments);

		List<Bill> linkedRefunds = billBrowserManager.getRefundBills(original.getId());
		assertThat(linkedRefunds).extracting(Bill::getId).contains(saved.getId());
	}

	@Test
	@DisplayName("Manager: getRefundItems reflects new alreadyRefundedQty after a refundBill call")
	void mgrGetRefundItemsReflectsNewRefundAfterSave() throws OHException, OHServiceException {
		Bill original = persistBill("C");
		persistBillItem(original, "Metformin", 1.5, 8);

		Bill refundBill = buildRefundBill(original, 4.5);
		List<BillItems> items = buildRefundItemsList(original, "Metformin", 1.5, 3);
		List<BillPayments> payments = buildNegativePayment(-4.5);
		billBrowserManager.refundBill(original, refundBill, items, payments);

		List<RefundBillItemDto> result = billBrowserManager.getRefundItems(original.getId());

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getAlreadyRefundedQty()).isEqualTo(3);
		assertThat(result.get(0).getRefundableQty()).isEqualTo(5); // 8 - 3
	}

	private Bill persistBill(String status) throws OHException {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, null, false);
		bill.setStatus(status);
		priceListRepository.saveAndFlush(priceList);
		patientRepository.saveAndFlush(patient);
		billRepository.saveAndFlush(bill);
		return bill;
	}

	private Bill persistRefundBill(Bill original, double amount) {
		Bill refund = new Bill();
		refund.setDate(LocalDateTime.now());
		refund.setUpdate(LocalDateTime.now());
		refund.setIsList(original.isList());
		refund.setPriceList(original.getPriceList());
		refund.setListName(original.getListName());
		refund.setIsPatient(original.isPatient());
		refund.setBillPatient(original.getBillPatient());
		refund.setPatName(original.getPatName());
		refund.setStatus("C");
		refund.setAmount(amount);
		refund.setBalance(0.0);
		refund.setUser("test");
		refund.setParentId(original.getId());
		billRepository.saveAndFlush(refund);
		return refund;
	}

	private BillItems persistBillItem(Bill bill, String description, double unitPrice, int qty) {
		BillItems item = new BillItems(0, bill, false, null, description, unitPrice, qty);
		billItemsRepository.saveAndFlush(item);
		return item;
	}

	private Bill buildRefundBill(Bill original, double amount) {
		Bill refund = new Bill();
		refund.setDate(LocalDateTime.now());
		refund.setUpdate(LocalDateTime.now());
		refund.setIsList(original.isList());
		refund.setPriceList(original.getPriceList());
		refund.setListName(original.getListName());
		refund.setIsPatient(original.isPatient());
		refund.setBillPatient(original.getBillPatient());
		refund.setPatName(original.getPatName());
		refund.setStatus("C");
		refund.setAmount(amount);
		refund.setBalance(0.0);
		refund.setUser("test");
		refund.setParentId(original.getId());
		return refund;
	}

	private List<BillItems> buildRefundItemsList(Bill originalBill, String description, double unitPrice, int qty) {
		List<BillItems> items = new ArrayList<>();
		BillItems item = new BillItems(0, null, false, null, description, unitPrice, qty);
		items.add(item);
		return items;
	}

	private List<BillPayments> buildNegativePayment(double amount) {
		List<BillPayments> payments = new ArrayList<>();
		payments.add(new BillPayments(0, null, LocalDateTime.now(), amount, "test"));
		return payments;
	}
}
