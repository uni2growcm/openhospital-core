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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.isf.accounting.model.ArchivedBill;
import org.isf.utils.exception.OHException;

public class TestArchivedBill {

	private static LocalDateTime date = LocalDateTime.of(10, 9, 8, 0, 0, 0);
	private static LocalDateTime update = LocalDateTime.of(7, 6, 5, 0, 0, 0);
	private static boolean isList;
	private static String listName = "TestListName";
	private static boolean isPatient = true;
	private static String patName = "TestPatName";
	private static String status = "C";
	private static Double amount = 10.10;
	private static Double balance = 20.20;
	private static int lock = 0;
	private static String user = "TestUser";
	private static Integer listId = 1;
	private static Integer billPatientId = 1;

	public ArchivedBill setup(boolean usingSet) throws OHException {
		ArchivedBill archivedBill;

		if (usingSet) {
			archivedBill = new ArchivedBill();
			setParameters(archivedBill);
		} else {
			archivedBill = new ArchivedBill();
			setParameters(archivedBill);
		}
		return archivedBill;
	}

	public void setParameters(ArchivedBill archivedBill) {
		archivedBill.setDate(date);
		archivedBill.setUpdate(update);
		archivedBill.setIsList(isList);
		archivedBill.setListId(listId);
		archivedBill.setListName(listName);
		archivedBill.setIsPatient(isPatient);
		archivedBill.setBillPatientId(billPatientId);
		archivedBill.setPatName(patName);
		archivedBill.setStatus(status);
		archivedBill.setAmount(amount);
		archivedBill.setBalance(balance);
		archivedBill.setLock(lock);
		archivedBill.setUser(user);
	}

	public void check(ArchivedBill archivedBill) {
		assertThat(archivedBill.getDate()).isCloseTo(date, within(1, ChronoUnit.SECONDS));
		assertThat(archivedBill.getUpdate()).isCloseTo(update, within(1, ChronoUnit.SECONDS));
		assertThat(archivedBill.isList()).isEqualTo(isList);
		assertThat(archivedBill.getListName()).isEqualTo(listName);
		assertThat(archivedBill.isPatient()).isEqualTo(isPatient);
		assertThat(archivedBill.getPatName()).isEqualTo(patName);
		assertThat(archivedBill.getStatus()).isEqualTo(status);
		assertThat(archivedBill.getAmount()).isEqualTo(amount);
		assertThat(archivedBill.getBalance()).isEqualTo(balance);
		assertThat(archivedBill.getUser()).isEqualTo(user);
	}
}
