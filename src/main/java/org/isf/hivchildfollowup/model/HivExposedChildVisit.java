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
package org.isf.hivchildfollowup.model;

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

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------ HivExposedChildVisit - one PTME follow-up visit of a
 * {@link HivExposedChild} -----------------------------------------
 * Follow-up visits typically happen at 6 weeks, 9 months and 18 months of age, each with a weight, an HIV
 * test (PCR or serology) and its result.
 */
@Entity
@Table(name = "OH_HIVEXPOSEDCHILDVISIT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "HECV_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "HECV_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "HECV_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "HECV_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "HECV_LAST_MODIFIED_DATE"))
public class HivExposedChildVisit extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HECV_ID")
	private int id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "HECV_HEC_ID")
	private HivExposedChild child;

	@NotNull
	@Column(name = "HECV_DATE")
	private LocalDate visitDate;

	@Column(name = "HECV_AGE_MONTHS")
	private Integer ageAtVisitMonths;

	@Column(name = "HECV_WEIGHT")
	private Float weight;

	@Enumerated(EnumType.STRING)
	@Column(name = "HECV_TEST_TYPE", length = 20)
	private HivTestType testType;

	@Enumerated(EnumType.STRING)
	@Column(name = "HECV_TEST_RESULT", length = 20)
	private HivTestResult testResult;

	@Column(name = "HECV_NEXT_APPOINTMENT")
	private LocalDate nextAppointmentDate;

	@Column(name = "HECV_NOTE")
	private String note;

	@Transient
	private volatile int hashCode;

	public HivExposedChildVisit() {
		super();
	}

	public HivExposedChildVisit(HivExposedChild child, LocalDate visitDate) {
		this.child = child;
		this.visitDate = visitDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public HivExposedChild getChild() {
		return child;
	}

	public void setChild(HivExposedChild child) {
		this.child = child;
	}

	public LocalDate getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(LocalDate visitDate) {
		this.visitDate = visitDate;
	}

	public Integer getAgeAtVisitMonths() {
		return ageAtVisitMonths;
	}

	public void setAgeAtVisitMonths(Integer ageAtVisitMonths) {
		this.ageAtVisitMonths = ageAtVisitMonths;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public HivTestType getTestType() {
		return testType;
	}

	public void setTestType(HivTestType testType) {
		this.testType = testType;
	}

	public HivTestResult getTestResult() {
		return testResult;
	}

	public void setTestResult(HivTestResult testResult) {
		this.testResult = testResult;
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
		if (!(other instanceof HivExposedChildVisit)) {
			return false;
		}
		return getId() == ((HivExposedChildVisit) other).getId();
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
