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

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_REDUCTIONPLAN")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "RP_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "RP_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "RP_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "RP_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "RP_LAST_MODIFIED_DATE"))
public class ReductionPlan extends Auditable<String> implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RP_ID")
	private int id;

	@NotNull
	@Column(name = "RP_DESCRIPTION")
	private String description;

	@Column(name = "RP_OPERATIONRATE",  precision = 5, scale = 2)
	private BigDecimal operationRate;

	@Column(name = "RP_MEDICALRATE",  precision = 5, scale = 2)
	private BigDecimal medicalRate;

	@Column(name = "RP_EXAMRATE",  precision = 5, scale = 2)
	private BigDecimal examRate;

	@Column(name = "RP_OTHERRATE",  precision = 5, scale = 2)
	private BigDecimal otherRate;

	@Column(name = "RP_DELETED")
	private boolean deleted = false;

	@Version
	@Column(name = "RP_LOCK")
	private int lock;

	@OneToMany(mappedBy = "reductionPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<ExamReduction> examReductions = new ArrayList<>();

	@OneToMany(mappedBy = "reductionPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<MedicalReduction> medicalReductions = new ArrayList<>();

	@OneToMany(mappedBy = "reductionPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<OperationReduction> operationReductions = new ArrayList<>();

	@OneToMany(mappedBy = "reductionPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<PriceOtherReduction> priceOtherReductions = new ArrayList<>();

	@Transient
	private volatile int hashcode;

	public ReductionPlan() {
		super();
		this.operationRate = BigDecimal.valueOf(0.00);
		this.examRate = BigDecimal.valueOf(0.00);
		this.medicalRate = BigDecimal.valueOf(0.00);
		this.otherRate = BigDecimal.valueOf(0.00);
	}

	public ReductionPlan(
		int id,
		String description,
		BigDecimal operationRate,
		BigDecimal medicalRate,
		BigDecimal examRate,
		BigDecimal otherRate
	) {
		super();
		this.id = id;
		this.description = description;
		this.operationRate = operationRate;
		this.medicalRate = medicalRate;
		this.examRate = examRate;
		this.otherRate = otherRate;
	}

	public ReductionPlan(
		String description,
		BigDecimal operationRate,
		BigDecimal medicalRate,
		BigDecimal examRate,
		BigDecimal otherRate
	) {
		this.description = description;
		this.operationRate = operationRate;
		this.medicalRate = medicalRate;
		this.examRate = examRate;
		this.otherRate = otherRate;
	}

	public ReductionPlan(
		int id,
		String description,
		BigDecimal operationRate,
		BigDecimal medicalRate,
		BigDecimal examRate,
		BigDecimal otherRate,
		List<ExamReduction> examReductionList,
		List<MedicalReduction> medicalReductionList,
		List<OperationReduction> operationReductionList,
		List<PriceOtherReduction> priceOtherReductionList
	) {
		super();
		this.id = id;
		this.description = description;
		this.operationRate = operationRate;
		this.medicalRate = medicalRate;
		this.examRate = examRate;
		this.otherRate = otherRate;
		this.examReductions = examReductionList;
		this.medicalReductions = medicalReductionList;
		this.operationReductions = operationReductionList;
		this.priceOtherReductions = priceOtherReductionList;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getOperationRate() {
		return operationRate;
	}

	public void setOperationRate(BigDecimal operationRate) {
		this.operationRate = operationRate;
	}

	public BigDecimal getMedicalRate() {
		return medicalRate;
	}

	public void setMedicalRate(BigDecimal medicalRate) {
		this.medicalRate = medicalRate;
	}

	public BigDecimal getExamRate() {
		return examRate;
	}

	public void setExamRate(BigDecimal examRate) {
		this.examRate = examRate;
	}

	public BigDecimal getOtherRate() {
		return otherRate;
	}

	public void setOtherRate(BigDecimal otherRate) {
		this.otherRate = otherRate;
	}

	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public List<ExamReduction> getExamReductions() {
		return examReductions;
	}

	public void setExamReductions(List<ExamReduction> examReductions) {
		this.examReductions = examReductions;
	}

	public List<MedicalReduction> getMedicalReductions() {
		return medicalReductions;
	}

	public void setMedicalReductions(List<MedicalReduction> medicalReductions) {
		this.medicalReductions = medicalReductions;
	}

	public List<OperationReduction> getOperationReductions() {
		return operationReductions;
	}

	public void setOperationReductions(List<OperationReduction> operationReductionList) {
		this.operationReductions = operationReductionList;
	}

	public List<PriceOtherReduction> getPriceOtherReductions() {
		return priceOtherReductions;
	}

	public void setPriceOtherReductions(List<PriceOtherReduction> priceOtherReductions) {
		this.priceOtherReductions = priceOtherReductions;
	}

	@Override
	public String toString() {
		return description;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ReductionPlan reductionplan)) {
			return false;
		}
		return (id == reductionplan.getId());
	}
}