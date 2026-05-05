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
package org.isf.hospitalizationconsultation.model;

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
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

import org.isf.encounter.model.Encounter;
import org.isf.utils.db.Auditable;
import org.isf.utils.time.TimeTools;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="oh_hospitalizationconsultation")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "HPC_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "HPC_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "HPC_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "HPC_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "HPC_LAST_MODIFIED_DATE"))
public class HospitalizationConsultation extends Auditable<String> implements Comparable<HospitalizationConsultation> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HPC_ID")
	private int id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "HPC_ENC_ID")
	private Encounter encounter;

	@Column(name = "HPC_TEAMS")
	private String teams;

	@NotNull
	@Column(name = "HPC_DATE_TIME")
	private LocalDateTime dateTime;

	@Column(name = "HPC_PARENT_COMPLAINTS", columnDefinition = "TEXT")
	private String parentComplaints;

	@Column(name = "HPC_PHYSICAL_EXAMINATION", columnDefinition = "TEXT")
	private String physicalExamination;

	@Column(name = "HPC_DIAGNOSIS", columnDefinition = "TEXT")
	private String diagnosis;

	@Column(name = "HPC_MANAGEMENT_PLAN", columnDefinition = "TEXT")
	private String managementPlan;

	@Version
	@Column(name = "HPC_LOCK")
	private int lock;

	@Transient
	private volatile int hashCode;

	public HospitalizationConsultation() {
		super();
	}

	public HospitalizationConsultation(int id, Encounter encounter, String teams, LocalDateTime dateTime, 
			String parentComplaints, String physicalExamination, String diagnosis, String managementPlan) {
		super();
		this.id = id;
		this.encounter = encounter;
		this.teams = teams;
		this.dateTime = TimeTools.truncateToSeconds(dateTime);
		this.parentComplaints = parentComplaints;
		this.physicalExamination = physicalExamination;
		this.diagnosis = diagnosis;
		this.managementPlan = managementPlan;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Encounter getEncounter() {
		return encounter;
	}

	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}

	public String getTeams() {
		return teams;
	}

	public void setTeams(String teams) {
		this.teams = teams;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = TimeTools.truncateToSeconds(dateTime);
	}

	public String getParentComplaints() {
		return parentComplaints;
	}

	public void setParentComplaints(String parentComplaints) {
		this.parentComplaints = parentComplaints;
	}

	public String getPhysicalExamination() {
		return physicalExamination;
	}

	public void setPhysicalExamination(String physicalExamination) {
		this.physicalExamination = physicalExamination;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getManagementPlan() {
		return managementPlan;
	}

	public void setManagementPlan(String managementPlan) {
		this.managementPlan = managementPlan;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	@Override
	public int compareTo(HospitalizationConsultation other) {
		return this.id - other.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof HospitalizationConsultation other)) {
			return false;
		}

		return (this.getId() == other.getId());
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
