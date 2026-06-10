/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.command.model;

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
import jakarta.validation.constraints.NotNull;

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.isf.supplier.model.Supplier;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_COMMAND_ROW")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "CMR_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "CMR_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "CMR_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "CMR_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "CMR_LAST_MODIFIED_DATE"))
public class CommandRow extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CMR_ID")
	private Integer id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "CMR_CMD_ID")
	private Command command;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "CMR_MDSR_ID")
	private Medical medical;

	@NotNull
	@Column(name = "CMR_MDSR_CODE")
	private String medicalCode;

	@NotNull
	@Column(name = "CMR_MDSR_DESC")
	private String medicalDescription;

	@ManyToOne
	@JoinColumn(name = "CMR_LT_ID_A")
	private Lot lot;

	@ManyToOne
	@JoinColumn(name = "CMR_SUP_ID")
	private Supplier supplier;

	@NotNull
	@Column(name = "CMR_QTY_IN_STORE")
	private double qtyInStore;

	@NotNull
	@Column(name = "CMR_CRITICAL_LEVEL")
	private double criticalLevel;

	@Column(name = "CMR_STILL_QTY")
	private Double stillQty;

	@Column(name = "CMR_ORDER_QTY")
	private Double orderQty;

	@NotNull
	@Column(name = "CMR_USER_ADDED_QTY")
	private double userAddedQty;

	public CommandRow() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public Medical getMedical() {
		return medical;
	}

	public void setMedical(Medical medical) {
		this.medical = medical;
	}

	public String getMedicalCode() {
		return medicalCode;
	}

	public void setMedicalCode(String medicalCode) {
		this.medicalCode = medicalCode;
	}

	public String getMedicalDescription() {
		return medicalDescription;
	}

	public void setMedicalDescription(String medicalDescription) {
		this.medicalDescription = medicalDescription;
	}

	public Lot getLot() {
		return lot;
	}

	public void setLot(Lot lot) {
		this.lot = lot;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public double getQtyInStore() {
		return qtyInStore;
	}

	public void setQtyInStore(double qtyInStore) {
		this.qtyInStore = qtyInStore;
	}

	public double getCriticalLevel() {
		return criticalLevel;
	}

	public void setCriticalLevel(double criticalLevel) {
		this.criticalLevel = criticalLevel;
	}

	public Double getStillQty() {
		return stillQty;
	}

	public void setStillQty(Double stillQty) {
		this.stillQty = stillQty;
	}

	public Double getOrderQty() {
		return orderQty;
	}

	public void setOrderQty(Double orderQty) {
		this.orderQty = orderQty;
	}

	public double getUserAddedQty() {
		return userAddedQty;
	}

	public void setUserAddedQty(double userAddedQty) {
		this.userAddedQty = userAddedQty;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CommandRow commandRow)) {
			return false;
		}
		return this.id.equals(commandRow.id);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString() {
		return medicalDescription;
	}
}
