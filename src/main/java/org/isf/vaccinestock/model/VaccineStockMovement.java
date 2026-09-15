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

import org.isf.patvac.model.PatientVaccine;
import org.isf.utils.db.Auditable;
import org.isf.utils.time.TimeTools;
import org.isf.vaccine.model.Vaccine;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_VACCINESTOCKMOVEMENT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "VSM_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "VSM_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "VSM_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "VSM_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "VSM_LAST_MODIFIED_DATE"))
public class VaccineStockMovement extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "VSM_ID")
	private int code;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "VSM_VAC_ID_A")
	private Vaccine vaccine;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "VSM_VSL_ID_A")
	private VaccineLot lot;

	/** Signed quantity: positive for a charge (entry), negative for a discharge (exit). */
	@NotNull
	@Column(name = "VSM_QTY")
	private int quantity;

	/** One of {@link VaccineStockMovementReason}, stored as its name(). */
	@NotNull
	@Column(name = "VSM_REASON")
	private String reason;

	@NotNull
	@Column(name = "VSM_DATE") // SQL type: datetime
	private LocalDateTime date;

	/**
	 * The administration that triggered this movement, only set for
	 * {@link VaccineStockMovementReason#ADMINISTRATION}/{@code ADMINISTRATION_CANCELLED}
	 * movements. Nullable: {@code ON DELETE SET NULL}, since deleting the
	 * {@link PatientVaccine} must not remove the movement's audit trail.
	 */
	@ManyToOne
	@JoinColumn(name = "VSM_PAV_ID")
	private PatientVaccine patientVaccine;

	@Column(name = "VSM_NOTE")
	private String note;

	@Transient
	private volatile int hashCode;

	public VaccineStockMovement() {
	}

	public VaccineStockMovement(Vaccine aVaccine, VaccineLot aLot, int aQuantity, VaccineStockMovementReason aReason, LocalDateTime aDate) {
		vaccine = aVaccine;
		lot = aLot;
		quantity = aQuantity;
		reason = aReason.name();
		date = TimeTools.truncateToSeconds(aDate);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Vaccine getVaccine() {
		return vaccine;
	}

	public void setVaccine(Vaccine vaccine) {
		this.vaccine = vaccine;
	}

	public VaccineLot getLot() {
		return lot;
	}

	public void setLot(VaccineLot lot) {
		this.lot = lot;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(VaccineStockMovementReason reason) {
		this.reason = reason.name();
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = TimeTools.truncateToSeconds(date);
	}

	public PatientVaccine getPatientVaccine() {
		return patientVaccine;
	}

	public void setPatientVaccine(PatientVaccine patientVaccine) {
		this.patientVaccine = patientVaccine;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof VaccineStockMovement other)) {
			return false;
		}
		return code == other.code;
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			final int m = 23;
			int c = 133;

			c = m * c + code;

			this.hashCode = c;
		}

		return this.hashCode;
	}
}
