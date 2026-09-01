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
package org.isf.pregnancy.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import org.isf.patient.model.Patient;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------ PregnancyVisit - model for a CPN visit (prenatal or postnatal)
 * -----------------------------------------
 */
@Entity
@Table(name = "OH_PREGNANCYVISIT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PVIS_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PVIS_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PVIS_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PVIS_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PVIS_LAST_MODIFIED_DATE"))
public class PregnancyVisit extends Auditable<String> {

	public static final int PRENATAL = -1;
	public static final int POSTNATAL = 1;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PVIS_ID")
	private int id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "PVIS_PREG_ID")
	private Pregnancy pregnancy;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "PVIS_PAT_ID")
	private Patient patient;

	@NotNull
	@Column(name = "PVIS_DATE")
	private LocalDateTime visitDate;

	@Column(name = "PVIS_NEXTDATE")
	private LocalDateTime nextVisitDate;

	@Column(name = "PVIS_TYPE")
	private int visitType = PRENATAL;

	@Column(name = "PVIS_NOTE")
	private String note;

	@ManyToMany
	@JoinTable(
			name = "OH_PREGNANCYVISITTREATMENTTYPE",
			joinColumns = @JoinColumn(name = "PVTT_PVIS_ID"),
			inverseJoinColumns = @JoinColumn(name = "PVTT_PTT_ID_A"))
	private List<PregnantTreatmentType> treatmentTypes = new ArrayList<>();

	@Transient
	private volatile int hashCode;

	public PregnancyVisit() {
		super();
	}

	public PregnancyVisit(Pregnancy pregnancy, Patient patient, LocalDateTime visitDate, int visitType) {
		this.pregnancy = pregnancy;
		this.patient = patient;
		this.visitDate = visitDate;
		this.visitType = visitType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Pregnancy getPregnancy() {
		return pregnancy;
	}

	public void setPregnancy(Pregnancy pregnancy) {
		this.pregnancy = pregnancy;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public LocalDateTime getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(LocalDateTime visitDate) {
		this.visitDate = visitDate;
	}

	public LocalDateTime getNextVisitDate() {
		return nextVisitDate;
	}

	public void setNextVisitDate(LocalDateTime nextVisitDate) {
		this.nextVisitDate = nextVisitDate;
	}

	public int getVisitType() {
		return visitType;
	}

	public void setVisitType(int visitType) {
		this.visitType = visitType;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<PregnantTreatmentType> getTreatmentTypes() {
		return treatmentTypes;
	}

	public void setTreatmentTypes(List<PregnantTreatmentType> treatmentTypes) {
		this.treatmentTypes = treatmentTypes;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof PregnancyVisit)) {
			return false;
		}
		return getId() == ((PregnancyVisit) other).getId();
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
