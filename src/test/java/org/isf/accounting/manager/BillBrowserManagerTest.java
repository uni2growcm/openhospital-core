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

import java.time.LocalDateTime;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.utils.exception.OHServiceException;
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
}
