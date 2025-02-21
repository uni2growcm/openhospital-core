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
package org.isf.reductionplan.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
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

import org.isf.medicals.model.Medical;
import org.isf.medtype.model.MedicalType;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_MEDICALSREDUCTION")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MR_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "MR_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MR_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "MR_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MR_LAST_MODIFIED_DATE"))
public class MedicalReduction extends Auditable<String> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MR_ID")
	private int id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "MR_RP_ID")
	private ReductionPlan reductionPlan;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "MR_MDSR_ID")
	private Medical medical;

	@NotNull
	@Column(name = "MR_REDUCTIONRATE")
	private double reductionRate;

	@Version
	@Column(name="MR_LOCK")
	private int lock;

	public MedicalReduction() {
		super();
	}

	public MedicalReduction(int id, ReductionPlan reductionPlan, Medical medical, double reductionRate) {
		this.id = id;
		this.reductionPlan = reductionPlan;
		this.medical = medical;
		this.reductionRate = reductionRate;
	}

	public MedicalReduction(ReductionPlan reductionPlan, Medical medical, double reductionRate) {
		this.reductionPlan = reductionPlan;
		this.medical = medical;
		this.reductionRate = reductionRate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ReductionPlan getReductionPlan() {
		return reductionPlan;
	}

	public void setReductionPlan(ReductionPlan reductionPlan) {
		this.reductionPlan = reductionPlan;
	}

	public Medical getMedical() {
		return medical;
	}

	public void setMedical(Medical medical) {
		this.medical = medical;
	}

	public double getReductionRate() {
		return reductionRate;
	}

	public void setReductionRate(double reductionRate) {
		this.reductionRate = reductionRate;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}
}
