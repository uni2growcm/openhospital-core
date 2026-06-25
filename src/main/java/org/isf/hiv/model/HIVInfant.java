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

package org.isf.hiv.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import org.isf.generaldata.MessageBundle;
import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "OH_HIV_INFANT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "HVI_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "HVI_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "HVI_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "HVI_LAST_MODIFIED_DATE"))
@AttributeOverride(name = "active", column = @Column(name = "HVI_ACTIVE"))
public class HIVInfant extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HVI_ID")
	private Integer id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "HVI_PAT_ID", nullable = false)
	private Patient patient;

	@ManyToOne
	@JoinColumn(name = "HVI_MOTHER_PAT_ID", nullable = true)
	private Patient mother;

	@NotNull
	@Column(name = "HVI_REG_DATE", nullable = false)
	private LocalDateTime registrationDate;

	@Column(name = "HVI_BIRTH_WEIGHT")
	private Double birthWeight;

	@Column(name = "HVI_GESTATIONAL_AGE")
	private Integer gestationalAge;

	@Enumerated(EnumType.STRING)
	@Column(name = "HVI_FEEDING_TYPE")
	private FeedingType feedingType;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "HVI_STATUS", nullable = false)
	private HIVInfantStatus status;

	@NotNull
	@Column(name = "HVI_FOLLOWUP_START", nullable = false)
	private LocalDate followUpStartDate;

	@Column(name = "HVI_FOLLOWUP_END")
	private LocalDate followUpEndDate;

	@Column(name = "HVI_NOTES", columnDefinition = "TEXT")
	private String notes;

	@Version
	@Column(name = "HVI_LOCK")
	private Integer lock;

	@JsonIgnore
	@OneToMany(mappedBy = "hivInfant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<HIVVisit> visits = new ArrayList<>();

	public enum HIVInfantStatus {
		ACTIVE("angal.hiv.status.active"),
		LOST("angal.hiv.status.lost"),
		DECEASED("angal.hiv.status.deceased"),
		TRANSFERRED("angal.hiv.status.transferred");

		private final String key;

		HIVInfantStatus(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}

		public String getDescription() {
			return MessageBundle.getMessage(key);
		}

		@Override
		public String toString() {
			return getDescription();
		}
	}

	public enum FeedingType {
		MATERNAL("angal.hiv.feeding.maternal"),
		MIXED("angal.hiv.feeding.mixed"),
		ARTIFICIAL("angal.hiv.feeding.artificial");

		private final String key;

		FeedingType(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}

		public String getDescription() {
			return MessageBundle.getMessage(key);
		}

		@Override
		public String toString() {
			return getDescription();
		}
	}

	public HIVInfant() {
		super();
	}

	public HIVInfant(Patient patient, LocalDateTime registrationDate, HIVInfantStatus status, LocalDate followUpStartDate) {
		this.patient = patient;
		this.registrationDate = registrationDate;
		this.status = status;
		this.followUpStartDate = followUpStartDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Patient getMother() {
		return mother;
	}

	public void setMother(Patient mother) {
		this.mother = mother;
	}

	public LocalDateTime getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDateTime registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Double getBirthWeight() {
		return birthWeight;
	}

	public void setBirthWeight(Double birthWeight) {
		this.birthWeight = birthWeight;
	}

	public Integer getGestationalAge() {
		return gestationalAge;
	}

	public void setGestationalAge(Integer gestationalAge) {
		this.gestationalAge = gestationalAge;
	}

	public FeedingType getFeedingType() {
		return feedingType;
	}

	public void setFeedingType(FeedingType feedingType) {
		this.feedingType = feedingType;
	}

	public HIVInfantStatus getStatus() {
		return status;
	}

	public void setStatus(HIVInfantStatus status) {
		this.status = status;
	}

	public LocalDate getFollowUpStartDate() {
		return followUpStartDate;
	}

	public void setFollowUpStartDate(LocalDate followUpStartDate) {
		this.followUpStartDate = followUpStartDate;
	}

	public LocalDate getFollowUpEndDate() {
		return followUpEndDate;
	}

	public void setFollowUpEndDate(LocalDate followUpEndDate) {
		this.followUpEndDate = followUpEndDate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Integer getLock() {
		return lock;
	}

	public void setLock(Integer lock) {
		this.lock = lock;
	}

	public List<HIVVisit> getVisits() {
		return visits;
	}

	public void setVisits(List<HIVVisit> visits) {
		this.visits = visits;
	}
	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof HIVInfant)) return false;
		HIVInfant other = (HIVInfant) obj;
		return id != null && id.equals(other.id);
	}

	@Override
	public String toString() {
		return "HIVInfant{" +
			"id=" + id +
			", patient=" + (patient != null ? patient.getCode() : null) +
			", status=" + status +
			'}';
	}
}