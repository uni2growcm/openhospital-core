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

import org.isf.accounting.model.BillItemGroup;
import org.isf.accounting.model.BillItemGroupItem;
import org.isf.utils.exception.OHException;

public class TestBillItemGroupItem {

	private static boolean isPrice;
	private static final String priceID = "TestPId";
	private static final String itemDescription = "TestItemDescription";
	private static final double itemAmount = 10.10;
	private static final int itemQuantity = 20;

	public BillItemGroupItem setup(BillItemGroup billItemGroup, boolean usingSet) throws OHException {
		BillItemGroupItem billItemGroupItem;

		if (usingSet) {
			billItemGroupItem = new BillItemGroupItem();
			setParameters(billItemGroupItem, billItemGroup);
		} else {
			// Create BillItem with all parameters 
			billItemGroupItem = new BillItemGroupItem(billItemGroup, isPrice, priceID, itemDescription, itemAmount, itemQuantity);
		}

		return billItemGroupItem;
	}

	public void setParameters(BillItemGroupItem billItemGroupItem, BillItemGroup billItemGroup) {
		billItemGroupItem.setBillItemGroup(billItemGroup);
		billItemGroupItem.setAmount(itemAmount);
		billItemGroupItem.setDescription(itemDescription);
		billItemGroupItem.setQuantity(itemQuantity);
		billItemGroupItem.setPrice(isPrice);
		billItemGroupItem.setPriceId(priceID);
	}

	public void check(BillItemGroupItem billItem) {
		assertThat(billItem.getAmount()).isCloseTo(itemAmount, offset(0.1));
		assertThat(billItem.getDescription()).isEqualTo(itemDescription);
		assertThat(billItem.getQuantity()).isEqualTo(itemQuantity);
		assertThat(billItem.isPrice()).isEqualTo(isPrice);
		assertThat(billItem.getPriceId()).isEqualTo(priceID);
	}
}
