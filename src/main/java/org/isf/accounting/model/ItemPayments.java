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
package org.isf.accounting.model;

import java.time.LocalDateTime;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.isf.utils.time.TimeTools;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_ITEMPAYMENTS")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "ITP_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "ITP_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "ITP_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "ITP_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "ITP_LAST_MODIFIED_DATE"))
public class ItemPayments extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ITP_ID")
	private int id;

	@Column(name = "ITP_ITEM_ID")
	private String itemId;

	@NotNull
	@Column(name = "ITP_ITEM_DESC")
	private String itemDescription;

	@ManyToOne
	@JoinColumn(name = "ITP_BLL_ID")
	private Bill bill;

	@NotNull
	@Column(name = "IS_REFUND")
	private boolean isRefund;

	@NotNull
	@Column(name = "ITP_AMOUNT")
	private double amount;

	@NotNull
	@Column(name = "ITP_USR_ID_A")
	private String user;

	@Column(name = "ITP_ITEM_GROUP")
	private String itemGroup;

	@NotNull
	@Column(name = "ITP_DATE")
	private LocalDateTime date;

	@Transient
	private volatile int hashCode;

	public ItemPayments() {
		super();
	}

	public ItemPayments(int id, String itemId, String itemDescription, Bill bill, boolean isRefund,
			double amount, String user, String itemGroup, LocalDateTime date) {
		this.id = id;
		this.itemId = itemId;
		this.itemDescription = itemDescription;
		this.bill = bill;
		this.isRefund = isRefund;
		this.amount = amount;
		this.user = user;
		this.itemGroup = itemGroup;
		this.date = TimeTools.truncateToSeconds(date);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
	}

	public boolean isRefund() {
		return isRefund;
	}

	public void setRefund(boolean isRefund) {
		this.isRefund = isRefund;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getItemGroup() {
		return itemGroup;
	}

	public void setItemGroup(String itemGroup) {
		this.itemGroup = itemGroup;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = TimeTools.truncateToSeconds(date);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ItemPayments itemPayment)) {
			return false;
		}

		return id == itemPayment.getId();
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			final int m = 23;
			int c = 133;

			c = m * c + id;

			this.hashCode = c;
		}

		return this.hashCode;
	}
}
