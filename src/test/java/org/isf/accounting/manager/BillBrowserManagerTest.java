/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.pagination.PagedResponse;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link BillBrowserManager#closeBill(Bill)}.
 */
class BillBrowserManagerTest extends OHCoreTestCase {

	@Autowired
	private BillBrowserManager billBrowserManager;

	@Autowired
	private PatientIoOperationRepository patientIoOperationRepository;

	private final TestPatient testPatient = new TestPatient();

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	private Bill buildOpenZeroBalanceBill() {
		LocalDateTime now = TimeTools.getNow();
		Bill bill = new Bill();
		bill.setDate(now);
		bill.setUpdate(now);
		bill.setIsList(false);
		bill.setIsPatient(false);
		bill.setPatName("Test Patient");
		bill.setStatus("O");
		bill.setAmount(50.0);
		bill.setBalance(0.0);
		bill.setUser("test");
		return bill;
	}

	/**
	 * Persists a bill with the given status/balance/user (no items/payments), bypassing
	 * {@link BillBrowserManager#newBill} to avoid its date/patient-name validation, since these
	 * pagination tests only care about the bill row itself.
	 */
	private Bill persistBill(String status, double balance, String user, Patient patient) throws OHServiceException {
		LocalDateTime now = TimeTools.getNow();
		Bill bill = new Bill();
		bill.setDate(now);
		bill.setUpdate(now);
		bill.setIsList(false);
		bill.setIsPatient(patient != null);
		bill.setBillPatient(patient);
		bill.setPatName(patient != null ? patient.getName() : "Test Patient");
		bill.setStatus(status);
		bill.setAmount(balance);
		bill.setBalance(balance);
		bill.setUser(user);
		return billBrowserManager.newBill(bill, List.of(), List.of());
	}

	@Test
	@DisplayName("Close an open bill with a zero balance")
	void testCloseBillSuccess() throws OHServiceException {
		LocalDateTime now = TimeTools.getNow();
		Bill bill = buildOpenZeroBalanceBill();
		List<BillItems> items = List.of(new BillItems(0, null, true, "MED1", "Test Medical", 50.0, 1));
		List<BillPayments> payments = List.of(new BillPayments(0, null, now, 50.0, "test"));

		Bill saved = billBrowserManager.newBill(bill, items, payments);

		Bill closed = billBrowserManager.closeBill(saved);

		assertThat(closed.getStatus()).isEqualTo("C");
		assertThat(billBrowserManager.getItems(closed.getId())).hasSize(1);
		assertThat(billBrowserManager.getItems(closed.getId()).get(0).getItemAmount()).isEqualTo(50.0);
		assertThat(billBrowserManager.getPayments(closed.getId())).hasSize(1);
		assertThat(billBrowserManager.getPayments(closed.getId()).get(0).getAmount()).isEqualTo(50.0);
	}

	@Test
	@DisplayName("Reject closing a bill with an outstanding balance")
	void testCloseBillRejectsNonZeroBalance() {
		Bill bill = buildOpenZeroBalanceBill();
		bill.setBalance(10.0);

		assertThatThrownBy(() -> billBrowserManager.closeBill(bill)).isInstanceOf(OHServiceException.class);
		assertThat(bill.getStatus()).isEqualTo("O");
	}

	@Test
	@DisplayName("Reject closing a bill that is already closed")
	void testCloseBillRejectsAlreadyClosed() {
		Bill bill = buildOpenZeroBalanceBill();
		bill.setStatus("C");

		assertThatThrownBy(() -> billBrowserManager.closeBill(bill)).isInstanceOf(OHServiceException.class);
	}

	@Test
	@DisplayName("getBillsPageable paginates and reports correct page info")
	void testGetBillsPageablePaging() throws OHServiceException {
		persistBill("O", 10.0, "cashierA", null);
		persistBill("O", 20.0, "cashierA", null);
		persistBill("O", 30.0, "cashierA", null);

		LocalDateTime dateFrom = TimeTools.getNow().minusDays(1);
		LocalDateTime dateTo = TimeTools.getNow().plusDays(1);

		PagedResponse<Bill> page0 = billBrowserManager.getBillsPageable(dateFrom, dateTo, null, null, null, 0, 2);
		assertThat(page0.getData()).hasSize(2);
		assertThat(page0.getPageInfo().getTotalNbOfElements()).isEqualTo(3);
		assertThat(page0.getPageInfo().getTotalPages()).isEqualTo(2);
		assertThat(page0.getPageInfo().isHasNextPage()).isTrue();
		assertThat(page0.getPageInfo().isHasPreviousPage()).isFalse();

		PagedResponse<Bill> page1 = billBrowserManager.getBillsPageable(dateFrom, dateTo, null, null, null, 1, 2);
		assertThat(page1.getData()).hasSize(1);
		assertThat(page1.getPageInfo().isHasNextPage()).isFalse();
		assertThat(page1.getPageInfo().isHasPreviousPage()).isTrue();
	}

	@Test
	@DisplayName("getBillsPageable filters by status")
	void testGetBillsPageableStatusFilter() throws OHServiceException {
		persistBill("O", 10.0, "cashierA", null);
		persistBill("O", 0.0, "cashierA", null);
		Bill closed = persistBill("O", 0.0, "cashierA", null);
		closed.setStatus("C");
		billBrowserManager.updateBill(closed, List.of(), List.of());

		LocalDateTime dateFrom = TimeTools.getNow().minusDays(1);
		LocalDateTime dateTo = TimeTools.getNow().plusDays(1);

		PagedResponse<Bill> openOnly = billBrowserManager.getBillsPageable(dateFrom, dateTo, "O", null, null, 0, 10);
		assertThat(openOnly.getData()).hasSize(2).allMatch(b -> b.getStatus().equals("O"));

		PagedResponse<Bill> closedOnly = billBrowserManager.getBillsPageable(dateFrom, dateTo, "C", null, null, 0, 10);
		assertThat(closedOnly.getData()).hasSize(1).allMatch(b -> b.getStatus().equals("C"));
	}

	@Test
	@DisplayName("getBillsPageable filters by patient and username")
	void testGetBillsPageablePatientAndUserFilter() throws Exception {
		Patient patient = patientIoOperationRepository.save(testPatient.setup(false));

		persistBill("O", 10.0, "cashierA", patient);
		persistBill("O", 10.0, "cashierB", null);

		LocalDateTime dateFrom = TimeTools.getNow().minusDays(1);
		LocalDateTime dateTo = TimeTools.getNow().plusDays(1);

		PagedResponse<Bill> byPatient = billBrowserManager.getBillsPageable(dateFrom, dateTo, null, patient.getCode(), null, 0, 10);
		assertThat(byPatient.getData()).hasSize(1);
		assertThat(byPatient.getData().get(0).getBillPatient().getCode()).isEqualTo(patient.getCode());

		PagedResponse<Bill> byUser = billBrowserManager.getBillsPageable(dateFrom, dateTo, null, null, "cashierB", 0, 10);
		assertThat(byUser.getData()).hasSize(1);
		assertThat(byUser.getData().get(0).getUser()).isEqualTo("cashierB");
	}

	@Test
	@DisplayName("Balance and payments totals reflect the whole filtered range, independent of pagination")
	void testTotals() throws OHServiceException {
		LocalDateTime now = TimeTools.getNow();
		Bill bill1 = persistBill("O", 10.0, "cashierA", null);
		Bill bill2 = persistBill("O", 25.0, "cashierB", null);
		billBrowserManager.updateBill(bill1, List.of(), List.of(new BillPayments(0, bill1, now, 5.0, "cashierA")));
		billBrowserManager.updateBill(bill2, List.of(), List.of(new BillPayments(0, bill2, now, 15.0, "cashierB")));

		LocalDateTime dateFrom = now.minusDays(1);
		LocalDateTime dateTo = now.plusDays(1);

		assertThat(billBrowserManager.getBalanceTotal(dateFrom, dateTo, null)).isEqualByComparingTo(BigDecimal.valueOf(35.0));
		assertThat(billBrowserManager.getPaymentsTotal(dateFrom, dateTo, null, null)).isEqualByComparingTo(BigDecimal.valueOf(20.0));
		assertThat(billBrowserManager.getPaymentsTotal(dateFrom, dateTo, null, "cashierA")).isEqualByComparingTo(BigDecimal.valueOf(5.0));
	}
}
