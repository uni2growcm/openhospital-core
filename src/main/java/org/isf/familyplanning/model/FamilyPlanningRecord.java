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
package org.isf.familyplanning.model;

import java.time.LocalDate;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------ FamilyPlanningRecord - model for the family planning module
 * -----------------------------------------
 * Records one family planning consultation for a {@link Patient}: the contraceptive method
 * chosen/discussed, the reason for the visit, counseling given and the next scheduled appointment.
 */
@Entity
@Table(name = "OH_FAMILYPLANNING")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "FPL_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "FPL_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "FPL_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "FPL_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "FPL_LAST_MODIFIED_DATE"))
public class FamilyPlanningRecord extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FPL_ID")
	private int id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "FPL_PAT_ID")
	private Patient patient;

	@NotNull
	@Column(name = "FPL_DATE")
	private LocalDate visitDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "FPL_METHOD", length = 20)
	private FamilyPlanningMethod method;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "FPL_REASON", length = 20)
	private FamilyPlanningReason reason;

	@Enumerated(EnumType.STRING)
	@Column(name = "FPL_PREVIOUS_METHOD", length = 20)
	private FamilyPlanningMethod previousMethod;

	@Column(name = "FPL_SIDE_EFFECTS")
	private String sideEffects;

	@Column(name = "FPL_COUNSELING")
	private boolean counselingGiven;

	@Column(name = "FPL_PARITY")
	private Integer parity;

	@Column(name = "FPL_NEXT_APPOINTMENT")
	private LocalDate nextAppointmentDate;

	@Column(name = "FPL_NOTE")
	private String note;

	@Transient
	private volatile int hashCode;

	public FamilyPlanningRecord() {
		super();
	}

	public FamilyPlanningRecord(Patient patient, LocalDate visitDate, FamilyPlanningMethod method, FamilyPlanningReason reason) {
		this.patient = patient;
		this.visitDate = visitDate;
		this.method = method;
		this.reason = reason;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public LocalDate getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(LocalDate visitDate) {
		this.visitDate = visitDate;
	}

	public FamilyPlanningMethod getMethod() {
		return method;
	}

	public void setMethod(FamilyPlanningMethod method) {
		this.method = method;
	}

	public FamilyPlanningReason getReason() {
		return reason;
	}

	public void setReason(FamilyPlanningReason reason) {
		this.reason = reason;
	}

	public FamilyPlanningMethod getPreviousMethod() {
		return previousMethod;
	}

	public void setPreviousMethod(FamilyPlanningMethod previousMethod) {
		this.previousMethod = previousMethod;
	}

	public String getSideEffects() {
		return sideEffects;
	}

	public void setSideEffects(String sideEffects) {
		this.sideEffects = sideEffects;
	}

	public boolean isCounselingGiven() {
		return counselingGiven;
	}

	public void setCounselingGiven(boolean counselingGiven) {
		this.counselingGiven = counselingGiven;
	}

	public Integer getParity() {
		return parity;
	}

	public void setParity(Integer parity) {
		this.parity = parity;
	}

	public LocalDate getNextAppointmentDate() {
		return nextAppointmentDate;
	}

	public void setNextAppointmentDate(LocalDate nextAppointmentDate) {
		this.nextAppointmentDate = nextAppointmentDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof FamilyPlanningRecord)) {
			return false;
		}
		return getId() == ((FamilyPlanningRecord) other).getId();
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
