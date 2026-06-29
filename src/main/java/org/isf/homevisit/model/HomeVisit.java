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
package org.isf.homevisit.model;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Version;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static java.time.Duration.between;

@Entity
@Table(name = "OH_HOME_VISIT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "HV_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "HV_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "HV_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "HV_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "HV_LAST_MODIFIED_DATE"))
public class HomeVisit extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HV_ID")
	private Integer id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "HV_PAT_ID", nullable = false)
	private Patient patient;

	@ManyToOne
	@JoinColumn(name = "HV_STAFF_ID")
	private Staff staff;

	@NotNull
	@Column(name = "HV_VISIT_START_DATE", nullable = false)
	private LocalDateTime visitStartDate;

	@Column(name = "HV_VISIT_END_DATE")
	private LocalDateTime visitEndDate;

	@NotNull
	@Column(name = "HV_STATUS", nullable = false)
	@Enumerated(EnumType.STRING)
	private HomeVisitStatus status = HomeVisitStatus.PLANNED;

	@Column(name = "HV_PURPOSE", length = 255)
	private String purpose;

	@Column(name = "HV_CLINICAL_NOTES", columnDefinition = "TEXT")
	private String clinicalNotes;

	@Column(name = "HV_OBSERVATIONS", columnDefinition = "TEXT")
	private String observations;

	@Column(name = "HV_ADDRESS", length = 255)
	private String address;

	@Column(name = "HV_CONTACT_PHONE", length = 50)
	private String contactPhone;

	@Column(name = "HV_NEXT_VISIT_DATE")
	private LocalDateTime nextVisitDate;

	@Column(name = "HV_CANCELLATION_REASON", length = 500)
	private String cancellationReason;

	@Version
	@Column(name = "HV_LOCK")
	private Integer lock;

	@Transient
	private volatile int hashCode;

	public HomeVisit() {
		super();
	}

	public HomeVisit(Patient patient, LocalDateTime visitStartDate) {
		this.patient = patient;
		this.visitStartDate = visitStartDate;
		this.status = HomeVisitStatus.PLANNED;
	}

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public Patient getPatient() { return patient; }
	public void setPatient(Patient patient) { this.patient = patient; }

	public Staff getStaff() { return staff; }
	public void setStaff(Staff staff) { this.staff = staff; }

	public LocalDateTime getVisitStartDate() { return visitStartDate; }
	public void setVisitStartDate(LocalDateTime visitStartDate) { this.visitStartDate = visitStartDate; }

	public LocalDateTime getVisitEndDate() { return visitEndDate; }
	public void setVisitEndDate(LocalDateTime visitEndDate) { this.visitEndDate = visitEndDate; }

	public HomeVisitStatus getStatus() { return status; }
	public void setStatus(HomeVisitStatus status) { this.status = status; }

	public String getPurpose() { return purpose; }
	public void setPurpose(String purpose) { this.purpose = purpose; }

	public String getClinicalNotes() { return clinicalNotes; }
	public void setClinicalNotes(String clinicalNotes) { this.clinicalNotes = clinicalNotes; }

	public String getObservations() { return observations; }
	public void setObservations(String observations) { this.observations = observations; }

	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }

	public String getContactPhone() { return contactPhone; }
	public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

	public LocalDateTime getNextVisitDate() { return nextVisitDate; }
	public void setNextVisitDate(LocalDateTime nextVisitDate) { this.nextVisitDate = nextVisitDate; }

	public Integer getLock() { return lock; }
	public void setLock(Integer lock) { this.lock = lock; }

	public Long getDurationMinutes() {
		if (visitStartDate != null && visitEndDate != null) {
			return between(visitStartDate, visitEndDate).toMinutes();
		}
		return null;
	}

	public String getCancellationReason() { return cancellationReason; }
	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof HomeVisit other)) return false;
		return id != 0 && id == other.id;
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

	@Override
	public String toString() {
		return "HomeVisit{patient=" + (patient != null ? patient.getName() : "null") +
			", date=" + visitStartDate +
			", status=" + status + "}";
	}
}