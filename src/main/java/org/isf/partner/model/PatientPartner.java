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
package org.isf.partner.model;

import jakarta.persistence.*;
import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "OH_PATIENT_PARTNERS")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PP_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PP_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PP_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PP_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PP_LAST_MODIFIED_DATE"))
public class PatientPartner extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PP_ID")
	private int id;

	@ManyToOne
	@JoinColumn(name = "PP_PAT_ID", nullable = false)
	private Patient patient;

	@ManyToOne
	@JoinColumn(name = "PP_PRT_ID", nullable = false)
	private Partner partner;

	@Column(name = "PP_START_DATE")
	private LocalDate startDate;

	@Column(name = "PP_END_DATE")
	private LocalDate endDate;

	@Version
	@Column(name = "PP_LOCK")
	private Integer lock;

	public PatientPartner() {}

	public PatientPartner(Patient patient, Partner partner, LocalDate startDate) {
		this.patient = patient;
		this.partner = partner;
		this.startDate = startDate;
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public Patient getPatient() { return patient; }
	public void setPatient(Patient patient) { this.patient = patient; }

	public Partner getPartner() { return partner; }
	public void setPartner(Partner partner) { this.partner = partner; }

	public LocalDate getStartDate() { return startDate; }
	public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

	public LocalDate getEndDate() { return endDate; }
	public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

	public Integer getLock() { return lock; }
	public void setLock(Integer lock) { this.lock = lock; }

	public boolean isActive() {
		return getActive() == 1 && (endDate == null || endDate.isAfter(LocalDate.now()));
	}

	@Override
	public String toString() {
		return patient + " - " + partner;
	}
}
