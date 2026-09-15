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

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------ PregnancyExamResult - the value entered for a
 * {@link PregnancyExamParameter} during a {@link PregnancyVisit} -----------------------------------------
 */
@Entity
@Table(name = "OH_PREGNANCYEXAMRESULT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PEXRES_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PEXRES_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PEXRES_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PEXRES_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PEXRES_LAST_MODIFIED_DATE"))
public class PregnancyExamResult extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PEXRES_ID")
	private int id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "PEXRES_PVIS_ID")
	private PregnancyVisit pregnancyVisit;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "PEXRES_PREGEX_CODE")
	private PregnancyExamParameter examParameter;

	@Column(name = "PEXRES_OUTCOME")
	private String outcome;

	@Transient
	private volatile int hashCode;

	public PregnancyExamResult() {
		super();
	}

	public PregnancyExamResult(PregnancyVisit pregnancyVisit, PregnancyExamParameter examParameter, String outcome) {
		this.pregnancyVisit = pregnancyVisit;
		this.examParameter = examParameter;
		this.outcome = outcome;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PregnancyVisit getPregnancyVisit() {
		return pregnancyVisit;
	}

	public void setPregnancyVisit(PregnancyVisit pregnancyVisit) {
		this.pregnancyVisit = pregnancyVisit;
	}

	public PregnancyExamParameter getExamParameter() {
		return examParameter;
	}

	public void setExamParameter(PregnancyExamParameter examParameter) {
		this.examParameter = examParameter;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof PregnancyExamResult)) {
			return false;
		}
		return getId() == ((PregnancyExamResult) other).getId();
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
