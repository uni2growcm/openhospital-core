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

import java.math.BigDecimal;

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

import org.isf.operation.model.Operation;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_OPERATIONSREDUCTION")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "OPR_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "OPR_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "OPR_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "OPR_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "OPR_LAST_MODIFIED_DATE"))
public class OperationReduction extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "OPR_ID")
	private int id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "OPR_RP_ID")
	private ReductionPlan reductionPlan;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "OPR_OPE_ID_A")
	private Operation operation;

	@NotNull
	@Column(name = "OPR_REDUCTIONRATE", precision = 5, scale = 2)
	private BigDecimal reductionRate;

	@Version
	@Column(name = "OPR_LOCK")
	private int lock;

	public OperationReduction() {
		super();
	}

	public OperationReduction(int id, ReductionPlan reductionPlan, Operation operation, BigDecimal reductionRate) {
		super();
		this.id = id;
		this.reductionPlan = reductionPlan;
		this.operation = operation;
		this.reductionRate = reductionRate;
	}

	public OperationReduction(ReductionPlan reductionPlan, Operation operation, BigDecimal reductionRate) {
		this.reductionPlan = reductionPlan;
		this.operation = operation;
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

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public BigDecimal getReductionRate() {
		return reductionRate;
	}

	public void setReductionRate(BigDecimal reductionRate) {
		this.reductionRate = reductionRate;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}
}
