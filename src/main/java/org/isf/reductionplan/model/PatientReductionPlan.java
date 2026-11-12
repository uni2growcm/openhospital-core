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
package org.isf.reductionplan.model;

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
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Assignment of a {@link ReductionPlan} to a {@link Patient}.
 * <p>
 * An assignment removed from a patient is only deactivated (never physically deleted) so that
 * the maximum number of patients of a plan is a cumulative quota.
 */
@Entity
@Table(name = "OH_PATIENT_REDUCTIONPLANS")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PRP_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PRP_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PRP_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PRP_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PRP_LAST_MODIFIED_DATE"))
public class PatientReductionPlan extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PRP_ID")
	private int id;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "PRP_PAT_ID", nullable = false)
	private Patient patient;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "PRP_RP_ID", nullable = false)
	private ReductionPlan reductionPlan;

	@Version
	@Column(name = "PRP_LOCK")
	private int lock;

	public PatientReductionPlan() {
		super();
	}

	public PatientReductionPlan(Patient patient, ReductionPlan reductionPlan) {
		super();
		this.patient = patient;
		this.reductionPlan = reductionPlan;
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

	public ReductionPlan getReductionPlan() {
		return reductionPlan;
	}

	public void setReductionPlan(ReductionPlan reductionPlan) {
		this.reductionPlan = reductionPlan;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public boolean isActive() {
		return getActive() == 1;
	}
}
