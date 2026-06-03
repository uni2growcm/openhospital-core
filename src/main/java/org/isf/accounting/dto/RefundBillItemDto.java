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
package org.isf.accounting.dto;

import org.isf.accounting.model.BillItems;

/**
 * DTO carrying a bill item together with its refund state:
 * how many units were already refunded and how many the user wants to refund now.
 */
public class RefundBillItemDto {

	private BillItems billItem;
	private int alreadyRefundedQty;
	private int refundQty;

	public RefundBillItemDto() {
	}

	public RefundBillItemDto(BillItems billItem, int alreadyRefundedQty) {
		this.billItem = billItem;
		this.alreadyRefundedQty = alreadyRefundedQty;
		this.refundQty = 0;
	}

	public BillItems getBillItem() {
		return billItem;
	}

	public void setBillItem(BillItems billItem) {
		this.billItem = billItem;
	}

	public int getAlreadyRefundedQty() {
		return alreadyRefundedQty;
	}

	public void setAlreadyRefundedQty(int alreadyRefundedQty) {
		this.alreadyRefundedQty = alreadyRefundedQty;
	}

	public int getRefundQty() {
		return refundQty;
	}

	public void setRefundQty(int refundQty) {
		this.refundQty = refundQty;
	}

	public int getRefundableQty() {
		return billItem.getItemQuantity() - alreadyRefundedQty;
	}
}
