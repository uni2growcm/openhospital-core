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

import org.isf.accounting.model.ArchivedBillItems;
import org.isf.utils.exception.OHException;

public class TestArchivedBillItems {

	private static boolean isPrice;
	private static String priceID = "TestPId";
	private static String itemDescription = "TestItemDescription";
	private static double itemAmount = 10.10;
	private static Double itemAmountBrut = 9.00;
	private static int itemQuantity = 20;
	private static LocalDateTime itemDate = LocalDateTime.of(10, 9, 8, 0, 0, 0);
	private static String itemId = "TestItemId";
	private static String itemGroup = "GP1";
	private static Integer prescriptionId = 1;

	public ArchivedBillItems setup(boolean usingSet) throws OHException {
		ArchivedBillItems archivedBillItem;

		if (usingSet) {
			archivedBillItem = new ArchivedBillItems();
			setParameters(archivedBillItem);
		} else {
			archivedBillItem = new ArchivedBillItems();
			setParameters(archivedBillItem);
		}
		return archivedBillItem;
	}

	public void setParameters(ArchivedBillItems archivedBillItem) {
		archivedBillItem.setPrice(isPrice);
		archivedBillItem.setPriceID(priceID);
		archivedBillItem.setItemDescription(itemDescription);
		archivedBillItem.setItemAmount(itemAmount);
		archivedBillItem.setItemAmountBrut(itemAmountBrut);
		archivedBillItem.setItemQuantity(itemQuantity);
		archivedBillItem.setItemDate(itemDate);
		archivedBillItem.setItemId(itemId);
		archivedBillItem.setItemGroup(itemGroup);
		archivedBillItem.setPrescriptionId(prescriptionId);
	}

	public void check(ArchivedBillItems archivedBillItem) {
		assertThat(archivedBillItem.isPrice()).isEqualTo(isPrice);
		assertThat(archivedBillItem.getPriceID()).isEqualTo(priceID);
		assertThat(archivedBillItem.getItemDescription()).isEqualTo(itemDescription);
		assertThat(archivedBillItem.getItemAmount()).isCloseTo(itemAmount, offset(0.1));
		assertThat(archivedBillItem.getItemAmountBrut()).isCloseTo(itemAmountBrut, offset(0.1));
		assertThat(archivedBillItem.getItemQuantity()).isEqualTo(itemQuantity);
		assertThat(archivedBillItem.getItemDate()).isCloseTo(itemDate, within(1, ChronoUnit.SECONDS));
		assertThat(archivedBillItem.getItemId()).isEqualTo(itemId);
		assertThat(archivedBillItem.getItemGroup()).isEqualTo(itemGroup);
		assertThat(archivedBillItem.getPrescriptionId()).isEqualTo(prescriptionId);
	}
}
