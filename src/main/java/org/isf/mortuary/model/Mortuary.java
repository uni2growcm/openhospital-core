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

import java.time.LocalDate;

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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_MORTUARY")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MRT_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "MRT_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MRT_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MRT_LAST_MODIFIED_DATE"))
@AttributeOverride(name = "active", column = @Column(name = "MRT_ACTIVE"))
public class Mortuary {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MRT_ID")
	private int id;

	@Column(name = "MRT_PLACE")
	private String place;

	@OneToOne
	@JoinColumn(name = "MRT_PAT_ID", insertable = false, updatable = false)
	private Patient patient;

	@Column(name = "MRT_PAT_ID")
	private int idPatient;

	@Column(name = "MRT_DR_ID")
	private int idCause;

	@Column(name = "MRT_PROVENANCE")
	private String provenance;

	@Column(name = "MRT_DEATH_DATE")
	private LocalDate deathDate;

	@Column(name = "MRT_ENTERED_DATE")
	private LocalDate enteredDate;

	@Column(name = "MRT_RELEASE_DATE")
	private LocalDate releaseDate;

	@Column(name = "MRT_PROVISIONAL_RELEASE_DATE")
	private LocalDate provisionalReleaseDate;

	@ManyToOne
	@JoinColumn(name = "MRT_DR_ID", insertable = false, updatable = false)
	private DeathReason deathReason;

	@Column(name = "MRT_DECLARING_NAME")
	private String declaringName;

	@Column(name = "MRT_DECLARING_PHONE")
	private String declaringPhone;

	@Column(name = "MRT_DECLARING_NEST")
	private String declaringNest;

	@Column(name = "MRT_FAMILY_NAME")
	private String familyName;

	@Column(name = "MRT_FAMILY_PHONE")
	private String familyPhone;

	@Column(name = "MRT_FAMILY_NEST")
	private String familyNest;

	@Column(name = "MRT_LOCKER")
	private String locker;

	public Mortuary() {
		super();
	}

	public Mortuary(int id, String place, Patient patient, int idPatient, int idCause, String provenance, LocalDate deathDate, LocalDate enteredDate,
		LocalDate releaseDate, LocalDate provisionalReleaseDate, DeathReason deathReason, String declaringName, String declaringPhone, String declaringNest,
		String familyName, String familyPhone, String familyNest, String locker) {
		this.id = id;
		this.place = place;
		this.patient = patient;
		this.idPatient = idPatient;
		this.idCause = idCause;
		this.provenance = provenance;
		this.deathDate = deathDate;
		this.enteredDate = enteredDate;
		this.releaseDate = releaseDate;
		this.provisionalReleaseDate = provisionalReleaseDate;
		this.deathReason = deathReason;
		this.declaringName = declaringName;
		this.declaringPhone = declaringPhone;
		this.declaringNest = declaringNest;
		this.familyName = familyName;
		this.familyPhone = familyPhone;
		this.familyNest = familyNest;
		this.locker = locker;
	}

	public int getIdPatient() {
		return idPatient;
	}
	public void setIdPatient(int idPatient) {
		this.idPatient = idPatient;
	}
	public int getIdCause() {
		return idCause;
	}
	public void setIdCause(int idCause) {
		this.idCause = idCause;
	}
	public String getLocker() {
		return locker;
	}
	public void setLocker(String locker) {
		this.locker = locker;
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
	public String getProvenance() {
		return provenance;
	}
	public void setProvenance(String provenance) {
		this.provenance = provenance;
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
	public String getDeclaringNest() {
		return declaringNest;
	}
	public void setDeclaringNest(String declaringNest) {
		this.declaringNest = declaringNest;
	}
	public LocalDate getDeathDate() {
		return deathDate;
	}
	public void setDeathDate(LocalDate deathDate) {
		this.deathDate = deathDate;
	}
	public LocalDate getEnteredDate() {
		return enteredDate;
	}
	public void setEnteredDate(LocalDate enteredDate) {
		this.enteredDate = enteredDate;
	}
	public LocalDate getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}
	public LocalDate getProvisionalReleaseDate() {
		return provisionalReleaseDate;
	}
	public void setProvisionalReleaseDate(LocalDate dateSortieProvisoire) {
		this.provisionalReleaseDate = dateSortieProvisoire;
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
	public String getFamilyNest() {
		return familyNest;
	}
	public void setFamilyNest(String familyNest) {
		this.familyNest = familyNest;
	}

	@Override
	public String toString() {
		return "Mortuary{" +
			"id=" + id +
			", place='" + place + '\'' +
			", patient=" + patient +
			", idPatient=" + idPatient +
			", idCause=" + idCause +
			", provenance='" + provenance + '\'' +
			", deathDate=" + deathDate +
			", enteredDate=" + enteredDate +
			", releaseDate=" + releaseDate +
			", provisionalReleaseDate=" + provisionalReleaseDate +
			", cause=" + deathReason +
			", declaringName='" + declaringName + '\'' +
			", declaringPhone='" + declaringPhone + '\'' +
			", declaringNest='" + declaringNest + '\'' +
			", familyName='" + familyName + '\'' +
			", familyPhone='" + familyPhone + '\'' +
			", familyNest='" + familyNest + '\'' +
			", locker='" + locker + '\'' +
			'}';
	}
}
