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
package org.isf.vaccinestock.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.isf.utils.time.TimeTools;
import org.isf.vaccine.model.Vaccine;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_VACCINESTOCKLOT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "VSL_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "VSL_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "VSL_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "VSL_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "VSL_LAST_MODIFIED_DATE"))
public class VaccineLot extends Auditable<String> {

	@Id
	@Column(name = "VSL_ID_A")
	private String code;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "VSL_VAC_ID_A")
	private Vaccine vaccine;

	@NotNull
	@Column(name = "VSL_PREP_DATE") // SQL type: datetime
	private LocalDateTime preparationDate;

	@NotNull
	@Column(name = "VSL_DUE_DATE") // SQL type: datetime
	private LocalDateTime dueDate;

	@Column(name = "VSL_COST")
	private BigDecimal cost;

	/**
	 * Automatic calculated field for the lot's current balance, computed as the
	 * sum of {@link VaccineStockMovement#getQuantity()} for movements tied to this lot.
	 */
	@Transient
	private int quantity;

	@Version
	@Column(name = "VSL_LOCK")
	private int lock;

	@Transient
	private volatile int hashCode;

	public VaccineLot() {
	}

	public VaccineLot(String aCode) {
		code = aCode;
	}

	public VaccineLot(Vaccine aVaccine, String aCode, LocalDateTime aPreparationDate, LocalDateTime aDueDate) {
		vaccine = aVaccine;
		code = aCode;
		preparationDate = TimeTools.truncateToSeconds(aPreparationDate);
		dueDate = TimeTools.truncateToSeconds(aDueDate);
	}

	public VaccineLot(Vaccine aVaccine, String aCode, LocalDateTime aPreparationDate, LocalDateTime aDueDate, BigDecimal aCost) {
		vaccine = aVaccine;
		code = aCode;
		preparationDate = TimeTools.truncateToSeconds(aPreparationDate);
		dueDate = TimeTools.truncateToSeconds(aDueDate);
		cost = aCost;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String aCode) {
		code = aCode;
	}

	public Vaccine getVaccine() {
		return vaccine;
	}

	public void setVaccine(Vaccine aVaccine) {
		vaccine = aVaccine;
	}

	public LocalDateTime getPreparationDate() {
		return preparationDate;
	}

	public void setPreparationDate(LocalDateTime aPreparationDate) {
		preparationDate = TimeTools.truncateToSeconds(aPreparationDate);
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDateTime aDueDate) {
		dueDate = TimeTools.truncateToSeconds(aDueDate);
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof VaccineLot other)) {
			return false;
		}
		return code != null && code.equals(other.code);
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			final int m = 23;
			int c = 133;

			c = m * c + (code == null ? 0 : code.hashCode());

			this.hashCode = c;
		}

		return this.hashCode;
	}

	@Override
	public String toString() {
		return code;
	}
}
