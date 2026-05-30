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
package org.isf.accounting.model;

import java.time.LocalDateTime;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="OH_ARCHIVED_BILLITEMS")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "BLI_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "BLI_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "BLI_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "BLI_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "BLI_LAST_MODIFIED_DATE"))
public class ArchivedBillItems extends Auditable<String> {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="BLI_ID")
	private int id;

	@Column(name="BLI_ID_BILL")
	private Integer billId;

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

	@Column(name="BLI_ITEM_AMOUNT_BRUT")
	private Double itemAmountBrut;

	@NotNull
	@Column(name="BLI_QTY")
	private int itemQuantity;

	@Column(name="BLI_DATE")
	private LocalDateTime itemDate; // Re-ajouté car présent dans oh_billitems source

	@Column(name = "BLI_ITEM_ID")
	private String itemId;

	@Column(name = "BLI_ITEM_GROUP")
	private String itemGroup;

	@Column(name = "BLI_PRESC_ID")
	private Integer prescriptionId;

	public ArchivedBillItems() {
		super();
	}

	// Getters and Setters
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public Integer getBillId() { return billId; }
	public void setBillId(Integer billId) { this.billId = billId; }
	public boolean isPrice() { return isPrice; }
	public void setPrice(boolean isPrice) { this.isPrice = isPrice; }
	public String getPriceID() { return priceID; }
	public void setPriceID(String priceID) { this.priceID = priceID; }
	public String getItemDescription() { return itemDescription; }
	public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }
	public double getItemAmount() { return itemAmount; }
	public void setItemAmount(double itemAmount) { this.itemAmount = itemAmount; }
	public Double getItemAmountBrut() { return itemAmountBrut; }
	public void setItemAmountBrut(Double itemAmountBrut) { this.itemAmountBrut = itemAmountBrut; }
	public int getItemQuantity() { return itemQuantity; }
	public void setItemQuantity(int itemQuantity) { this.itemQuantity = itemQuantity; }
	public LocalDateTime getItemDate() { return itemDate; }
	public void setItemDate(LocalDateTime itemDate) { this.itemDate = itemDate; }
	public String getItemId() { return itemId; }
	public void setItemId(String itemId) { this.itemId = itemId; }
	public String getItemGroup() { return itemGroup; }
	public void setItemGroup(String itemGroup) { this.itemGroup = itemGroup; }
	public Integer getPrescriptionId() { return prescriptionId; }
	public void setPrescriptionId(Integer prescriptionId) { this.prescriptionId = prescriptionId; }
}