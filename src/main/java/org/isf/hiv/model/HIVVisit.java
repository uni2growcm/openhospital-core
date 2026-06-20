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

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import org.isf.generaldata.MessageBundle;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_HIV_VISIT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "HVV_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "HVV_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "HVV_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "HVV_LAST_MODIFIED_DATE"))
@AttributeOverride(name = "active", column = @Column(name = "HVV_ACTIVE"))
public class HIVVisit extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HVV_ID")
	private Integer id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "HVV_HVI_ID", nullable = false)
	private HIVInfant hivInfant;

	@NotNull
	@Column(name = "HVV_VISIT_DATE", nullable = false)
	private LocalDateTime visitDate;

	@Column(name = "HVV_WEIGHT")
	private Double weight;

	@Column(name = "HVV_HEIGHT")
	private Double height;

	@Column(name = "HVV_HEAD_CIRCUMFERENCE")
	private Double headCircumference;

	@Column(name = "HVV_TEMPERATURE")
	private Double temperature;

	@Column(name = "HVV_CLINICAL_STATUS", columnDefinition = "TEXT")
	private String clinicalStatus;

	@Column(name = "HVV_MILESTONES")
	private String milestones;

	@Enumerated(EnumType.STRING)
	@Column(name = "HVV_PCR_RESULT")
	private PCRResult pcrResult;

	@Column(name = "HVV_VIRAL_LOAD")
	private Double viralLoad;

	@Column(name = "HVV_CD4_COUNT")
	private Integer cd4Count;

	@Column(name = "HVV_CD4_PERCENT")
	private Integer cd4Percent;

	@Column(name = "HVV_HEMOGLOBIN")
	private Double hemoglobin;

	@Column(name = "HVV_ADHERENCE")
	private Integer adherence;

	@Column(name = "HVV_SIDE_EFFECTS", columnDefinition = "TEXT")
	private String sideEffects;

	@Column(name = "HVV_NEXT_APPOINTMENT_DATE")
	private LocalDate nextAppointmentDate;

	@Column(name = "HVV_NOTES", columnDefinition = "TEXT")
	private String notes;

	@Version
	@Column(name = "HVV_LOCK")
	private Integer lock;

	public enum PCRResult {
		POSITIVE("angal.hiv.pcr.positive"),
		NEGATIVE("angal.hiv.pcr.negative"),
		PENDING("angal.hiv.pcr.pending");

		private final String key;

		PCRResult(String key) {
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

	public HIVVisit() {
		super();
	}

	public HIVVisit(HIVInfant hivInfant, LocalDateTime visitDate) {
		this.hivInfant = hivInfant;
		this.visitDate = visitDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public HIVInfant getHivInfant() {
		return hivInfant;
	}

	public void setHivInfant(HIVInfant hivInfant) {
		this.hivInfant = hivInfant;
	}

	public LocalDateTime getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(LocalDateTime visitDate) {
		this.visitDate = visitDate;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Double getHeadCircumference() {
		return headCircumference;
	}

	public void setHeadCircumference(Double headCircumference) {
		this.headCircumference = headCircumference;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public String getClinicalStatus() {
		return clinicalStatus;
	}

	public void setClinicalStatus(String clinicalStatus) {
		this.clinicalStatus = clinicalStatus;
	}

	public String getMilestones() {
		return milestones;
	}

	public void setMilestones(String milestones) {
		this.milestones = milestones;
	}

	public PCRResult getPcrResult() {
		return pcrResult;
	}

	public void setPcrResult(PCRResult pcrResult) {
		this.pcrResult = pcrResult;
	}

	public Double getViralLoad() {
		return viralLoad;
	}

	public void setViralLoad(Double viralLoad) {
		this.viralLoad = viralLoad;
	}

	public Integer getCd4Count() {
		return cd4Count;
	}

	public void setCd4Count(Integer cd4Count) {
		this.cd4Count = cd4Count;
	}

	public Integer getCd4Percent() {
		return cd4Percent;
	}

	public void setCd4Percent(Integer cd4Percent) {
		this.cd4Percent = cd4Percent;
	}

	public Double getHemoglobin() {
		return hemoglobin;
	}

	public void setHemoglobin(Double hemoglobin) {
		this.hemoglobin = hemoglobin;
	}

	public Integer getAdherence() {
		return adherence;
	}

	public void setAdherence(Integer adherence) {
		this.adherence = adherence;
	}

	public String getSideEffects() {
		return sideEffects;
	}

	public void setSideEffects(String sideEffects) {
		this.sideEffects = sideEffects;
	}

	public LocalDate getNextAppointmentDate() {
		return nextAppointmentDate;
	}

	public void setNextAppointmentDate(LocalDate nextAppointmentDate) {
		this.nextAppointmentDate = nextAppointmentDate;
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

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof HIVVisit)) return false;
		HIVVisit other = (HIVVisit) obj;
		return id != null && id.equals(other.id);
	}

	@Override
	public String toString() {
		return "HIVVisit{" +
			"id=" + id +
			", visitDate=" + visitDate +
			'}';
	}
}