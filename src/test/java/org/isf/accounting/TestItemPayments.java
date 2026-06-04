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
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.data.Offset.offset;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.ItemPayments;
import org.isf.utils.exception.OHException;

public class TestItemPayments {

	public LocalDateTime paymentDate = LocalDateTime.of(4, 3, 2, 0, 0, 0);
	private static double paymentAmount = 25.50;
	private static String paymentUser = "TestUser";
	private static String itemId = "ITEM01";
	private static String itemDescription = "Test Item Description";
	private static String itemGroup = "OTH";

	public ItemPayments setup(Bill bill, boolean usingSet) throws OHException {
		ItemPayments itemPayment;

		if (usingSet) {
			itemPayment = new ItemPayments();
			setParameters(itemPayment, bill);
		} else {
			itemPayment = new ItemPayments(0, itemId, itemDescription, bill, false,
				paymentAmount, paymentUser, itemGroup, paymentDate);
		}

		return itemPayment;
	}

	public void setParameters(ItemPayments itemPayment, Bill bill) {
		itemPayment.setBill(bill);
		itemPayment.setDate(paymentDate);
		itemPayment.setAmount(paymentAmount);
		itemPayment.setUser(paymentUser);
		itemPayment.setItemId(itemId);
		itemPayment.setItemDescription(itemDescription);
		itemPayment.setItemGroup(itemGroup);
		itemPayment.setRefund(false);
	}

	public void check(ItemPayments itemPayment) {
		assertThat(itemPayment.getAmount()).isCloseTo(paymentAmount, offset(0.1));
		assertThat(itemPayment.getDate()).isCloseTo(paymentDate, within(1, ChronoUnit.SECONDS));
		assertThat(itemPayment.getUser()).isEqualTo(paymentUser);
		assertThat(itemPayment.getItemId()).isEqualTo(itemId);
		assertThat(itemPayment.getItemDescription()).isEqualTo(itemDescription);
		assertThat(itemPayment.getItemGroup()).isEqualTo(itemGroup);
		assertThat(itemPayment.isRefund()).isFalse();
	}
}
