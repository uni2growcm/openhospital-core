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

import org.isf.pricesothers.model.PricesOthers;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_PRICESOTHERSREDUCTION")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "OTHR_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "OTHR_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "OTHR_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "OTHR_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "OTHR_LAST_MODIFIED_DATE"))
public class PriceOtherReduction extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "OTHR_ID")
	private int id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "OTHR_RP_ID")
	private ReductionPlan reductionPlan;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "OTHR_OTH_ID")
	private PricesOthers pricesOthers;

	@NotNull
	@Column(name = "OTHR_REDUCTIONRATE")
	private double reductionRate;

	@Version
	@Column(name = "OTHR_LOCK")
	private int lock;

	public PriceOtherReduction() {
		super();
	}

	public PriceOtherReduction(int id, ReductionPlan reductionPlan, PricesOthers pricesOthers, double reductionRate) {
		super();
		this.id = id;
		this.reductionPlan = reductionPlan;
		this.pricesOthers = pricesOthers;
		this.reductionRate = reductionRate;
	}

	public PriceOtherReduction(ReductionPlan reductionPlan, PricesOthers pricesOthers, double reductionRate) {
		this.reductionPlan = reductionPlan;
		this.pricesOthers = pricesOthers;
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

	public PricesOthers getPricesOthers() {
		return pricesOthers;
	}

	public void setPricesOthers(PricesOthers pricesOthers) {
		this.pricesOthers = pricesOthers;
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
