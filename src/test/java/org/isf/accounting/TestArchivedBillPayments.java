/*
 * Open Hospital (www.open-hospital.org)
 * Copyright  2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import org.isf.accounting.model.ArchivedBillPayments;
import org.isf.utils.exception.OHException;

public class TestArchivedBillPayments {

	private static LocalDateTime date = LocalDateTime.of(4, 3, 2, 0, 0, 0);
	private static double amount = 10.10;
	private static String user = "TestUser";

	public ArchivedBillPayments setup(boolean usingSet) throws OHException {
		ArchivedBillPayments archivedBillPayment;

		if (usingSet) {
			archivedBillPayment = new ArchivedBillPayments();
			setParameters(archivedBillPayment);
		} else {
			archivedBillPayment = new ArchivedBillPayments();
			setParameters(archivedBillPayment);
		}
		return archivedBillPayment;
	}

	public void setParameters(ArchivedBillPayments archivedBillPayment) {
		archivedBillPayment.setDate(date);
		archivedBillPayment.setAmount(amount);
		archivedBillPayment.setUser(user);
	}

	public void check(ArchivedBillPayments archivedBillPayment) {
		assertThat(archivedBillPayment.getDate()).isCloseTo(date, within(1, ChronoUnit.SECONDS));
		assertThat(archivedBillPayment.getAmount()).isCloseTo(amount, offset(0.1));
		assertThat(archivedBillPayment.getUser()).isEqualTo(user);
	}
}
