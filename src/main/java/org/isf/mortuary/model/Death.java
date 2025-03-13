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

package org.isf.mortuary.model;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.isf.ward.model.Ward;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_DEATH")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "DTH_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "DTH_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "DTH_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "DTH_LAST_MODIFIED_DATE"))
@AttributeOverride(name = "active", column = @Column(name = "DTH_ACTIVE"))
public class Death extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DTH_ID")
	private int id;

	@Column(name = "DTH_PLACE")
	private String place;

	@OneToOne
	@JoinColumn(name = "DTH_PAT_ID")
	private Patient patient;

	@ManyToOne
	@JoinColumn(name = "DTH_WRD_ID_A")
	private Ward ward;

	@ManyToOne
	@JoinColumn(name = "DTH_DTHR_ID")
	private DeathReason deathReason;

	@Column(name = "DTH_DATE")
	private LocalDateTime date;

	@Column(name = "DTH_ADMISSION_DATE")
	private LocalDateTime admissionDate;

	@Column(name = "DTH_DISCHARGE_DATE")
	private LocalDateTime dischargeDate;

	@Column(name = "DTH_ESTIMATED_DISCHARGE_DATE")
	private LocalDateTime estimatedDischargeDate;

	@Column(name = "DTH_DECLARING_NAME")
	private String declaringName;

	@Column(name = "DTH_DECLARING_PHONENUMBER")
	private String declaringPhone;

	@Column(name = "DTH_DECLARING_NID")
	private String declaringNid;

	@Column(name = "DTH_FAMILY_NAME")
	private String familyName;

	@Column(name = "DTH_FAMILY_PHONENUMBER")
	private String familyPhone;

	@Column(name = "DTH_FAMILY_NID")
	private String familyNid;

	@ManyToOne
	@JoinColumn(name = "DTH_BC_ID", nullable = false)
	private BodyCompartment bodyCompartment;

	@Column(name = "DTH_DELETED")
	private boolean deleted;

	public Death() {
		super();
	}

	public Death(String place, Patient patient, Ward ward, LocalDateTime date, LocalDateTime admissionDate,
		LocalDateTime dischargeDate, LocalDateTime estimatedDischargeDate, DeathReason deathReason, String declaringName, String declaringPhone, String declaringNid,
		String familyName, String familyPhone, String familyNid, BodyCompartment lockerNumber, boolean deleted
	) {
		this.place = place;
		this.patient = patient;
		this.ward = ward;
		this.date = date;
		this.admissionDate = admissionDate;
		this.dischargeDate = dischargeDate;
		this.estimatedDischargeDate = estimatedDischargeDate;
		this.deathReason = deathReason;
		this.declaringName = declaringName;
		this.declaringPhone = declaringPhone;
		this.declaringNid = declaringNid;
		this.familyName = familyName;
		this.familyPhone = familyPhone;
		this.familyNid = familyNid;
		this.bodyCompartment = lockerNumber;
		this.deleted = deleted;
	}

	public Death(int id, String place, Patient patient, Ward ward, LocalDateTime date, LocalDateTime admissionDate,
		LocalDateTime dischargeDate, LocalDateTime estimatedDischargeDate, DeathReason deathReason, String declaringName, String declaringPhone, String declaringNid,
		String familyName, String familyPhone, String familyNid, BodyCompartment lockerNumber, boolean deleted
	) {
		this(place, patient, ward, date, admissionDate,
			dischargeDate, estimatedDischargeDate, deathReason, declaringName, declaringPhone, declaringNid,
			familyName, familyPhone, familyNid, lockerNumber, deleted
		);

		this.setId(id);
	}

	public BodyCompartment getBodyCompartment() {
		return bodyCompartment;
	}
	public void setBodyCompartment(BodyCompartment bodyCompartment) {
		this.bodyCompartment = bodyCompartment;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public Ward getWard() {
		return ward;
	}
	public void setWard(Ward ward) {
		this.ward = ward;
	}
	public DeathReason getDeathReason() {
		return deathReason;
	}
	public void setDeathReason(DeathReason deathReason) {
		this.deathReason = deathReason;
	}
	public String getDeclaringName() {
		return declaringName;
	}
	public void setDeclaringName(String declaringName) {
		this.declaringName = declaringName;
	}
	public String getDeclaringPhone() {
		return declaringPhone;
	}
	public void setDeclaringPhone(String declaringPhone) {
		this.declaringPhone = declaringPhone;
	}
	public String getDeclaringNid() {
		return declaringNid;
	}
	public void setDeclaringNid(String declaringNid) {
		this.declaringNid = declaringNid;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public LocalDateTime getAdmissionDate() {
		return admissionDate;
	}
	public void setAdmissionDate(LocalDateTime admissionDate) {
		this.admissionDate = admissionDate;
	}
	public LocalDateTime getDischargeDate() {
		return dischargeDate;
	}
	public void setDischargeDate(LocalDateTime dischargeDate) {
		this.dischargeDate = dischargeDate;
	}
	public LocalDateTime getEstimatedDischargeDate() {
		return estimatedDischargeDate;
	}
	public void setEstimatedDischargeDate(LocalDateTime dateSortieProvisoire) {
		this.estimatedDischargeDate = dateSortieProvisoire;
	}
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String getFamilyPhone() {
		return familyPhone;
	}
	public void setFamilyPhone(String familyPhone) {
		this.familyPhone = familyPhone;
	}
	public String getFamilyNid() {
		return familyNid;
	}
	public void setFamilyNid(String familyNid) {
		this.familyNid = familyNid;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	@Override
	public String toString() {
		return "Mortuary{" +
			"id=" + id +
			", place='" + place + '\'' +
			", patient=" + patient +
			", provenance='" + ward + '\'' +
			", deathDate=" + date +
			", enteredDate=" + admissionDate +
			", releaseDate=" + dischargeDate +
			", provisionalReleaseDate=" + estimatedDischargeDate +
			", cause=" + deathReason +
			", declaringName='" + declaringName + '\'' +
			", declaringPhone='" + declaringPhone + '\'' +
			", declaringNest='" + declaringNid + '\'' +
			", familyName='" + familyName + '\'' +
			", familyPhone='" + familyPhone + '\'' +
			", familyNest='" + familyNid + '\'' +
			", locker='" + bodyCompartment + '\'' +
			'}';
	}
}