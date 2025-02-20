/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="OH_BILLITEMS")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "BLI_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "BLI_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "BLI_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "BLI_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "BLI_LAST_MODIFIED_DATE"))
public class BillItems extends Auditable<String> {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="BLI_ID")
	private int id;
	
	@ManyToOne
	@JoinColumn(name="BLI_ID_BILL")
	private Bill bill;

	@NotNull
	@Column(name="BLI_IS_PRICE")
	private boolean isPrice;
	
	@Column(name="BLI_ID_PRICE")
	private String priceID;
	
	@Column(name="BLI_ITEM_DESC")
	private String itemDescription;

	@NotNull
	@Column(name="BLI_ITEM_AMOUNT")
	private double itemAmount;

	@NotNull
	@Column(name="BLI_QTY")
	private int itemQuantity;
	
	@Transient
	private volatile int hashCode;
	
	/**
	 * Store  the code of the item that is used for search purpose.
	 * For medical for example it will store the prod_code.
	 * it is necessary for medical and otherPRices items
	 * used in the patient bill edit
	 * The field is not persisted
	 */
	@Transient
	private String itemDisplayCode;
	
	/**
	 * Store the item Id that is involved in this bill item (medId, exaId, opeId...)
	 */
	@Transient
	private String itemId;
	
	
	public BillItems() {
		super();
	}

	public BillItems(int id, Bill bill, boolean isPrice, String priceID,
			String itemDescription, double itemAmount, int itemQuantity) {
		super();
		this.id = id;
		this.bill = bill;
		this.isPrice = isPrice;
		this.priceID = priceID;
		this.itemDescription = itemDescription;
		this.itemAmount = itemAmount;
		this.itemQuantity = itemQuantity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
	}

	public boolean isPrice() {
		return isPrice;
	}

	public void setPrice(boolean isPrice) {
		this.isPrice = isPrice;
	}

	public String getPriceID() {
		return priceID;
	}

	public void setPriceID(String priceID) {
		this.priceID = priceID;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public double getItemAmount() {
		return itemAmount;
	}

	public void setItemAmount(double itemAmount) {
		this.itemAmount = itemAmount;
	}

	public int getItemQuantity() {
		return itemQuantity;
	}

	public void setItemQuantity(int itemQuantity) {
		this.itemQuantity = itemQuantity;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof BillItems billItem)) {
			return false;
		}

		return (id == billItem.getId());
	}

	/**
	 * Get the item ID
	 *
	 * @return
	 */
	public String getItemDisplayCode() {
		if (itemDisplayCode == null || itemDisplayCode.isEmpty()) {
			itemDisplayCode = itemId;
		}
		return itemDisplayCode;
	}

	public void setItemDisplayCode(String itemDisplayCode) {
		this.itemDisplayCode = itemDisplayCode;
	}
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
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
