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

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "OH_BILLITEMGROUPITEM")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "BLIGI_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "BLIGI_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "BLIGI_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "BLIGI_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "BLIGI_LAST_MODIFIED_DATE"))
public class BillItemGroupItem extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BLIGI_ID")
	private int id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "BLIGI_BLIG_ID")
	private BillItemGroup billItemGroup;

	@NotNull
	@Column(name = "BLIGI_IS_PRICE")
	private boolean isPrice = false;

	@Column(name = "BLIGI_PRICE_ID")
	private String priceId;

	@Column(name = "BLIGI_DESC")
	private String description;

	@NotNull
	@Column(name = "BLIGI_AMOUNT")
	private Double amount = 0.0;

	@NotNull
	@Column(name = "BLIGI_QUANTITY")
	private Integer quantity = 1;

	@Version
	@Column(name = "BLIGI_LOCK")
	private Integer lock = 0;

	@Transient
	private volatile int hashCode;

	/**
	 * Default constructor
	 */
	public BillItemGroupItem() {
		super();
	}

	/**
	 * Constructor with main parameters
	 * 
	 * @param billItemGroup the parent bill item group
	 * @param isPrice whether this is a price item
	 * @param priceId the price id
	 * @param description the description
	 * @param amount the amount
	 * @param quantity the quantity
	 */
	public BillItemGroupItem(BillItemGroup billItemGroup, boolean isPrice, String priceId, 
			String description, Double amount, Integer quantity) {
		super();
		this.billItemGroup = billItemGroup;
		this.isPrice = isPrice;
		this.priceId = priceId;
		this.description = description;
		this.amount = amount;
		this.quantity = quantity;
	}

	/**
	 * Full constructor
	 * 
	 * @param id the id
	 * @param billItemGroup the parent bill item group
	 * @param isPrice whether this is a price item
	 * @param priceId the price id
	 * @param description the description
	 * @param amount the amount
	 * @param quantity the quantity
	 * @param itemId the item id
	 * @param itemGroup the item group
	 */
	public BillItemGroupItem(int id, BillItemGroup billItemGroup, boolean isPrice, String priceId,
			String description, Double amount, Integer quantity, String itemId, String itemGroup) {
		super();
		this.id = id;
		this.billItemGroup = billItemGroup;
		this.isPrice = isPrice;
		this.priceId = priceId;
		this.description = description;
		this.amount = amount;
		this.quantity = quantity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BillItemGroup getBillItemGroup() {
		return billItemGroup;
	}

	public void setBillItemGroup(BillItemGroup billItemGroup) {
		this.billItemGroup = billItemGroup;
	}

	public boolean isPrice() {
		return isPrice;
	}

	public void setPrice(boolean isPrice) {
		this.isPrice = isPrice;
	}

	public String getPriceId() {
		return priceId;
	}

	public void setPriceId(String priceId) {
		this.priceId = priceId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getLock() {
		return lock;
	}

	public void setLock(Integer lock) {
		this.lock = lock;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BillItemGroupItem other) {
			return this.id == other.id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			this.hashCode = 31 + this.id;
		}
		return this.hashCode;
	}

	@Override
	public String toString() {
		return "BillItemGroupItem [id=" + id + ", description=" + description + ", amount=" + amount + ", quantity=" + quantity + "]";
	}
}