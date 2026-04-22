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
package org.isf.maternity.model;

import java.time.LocalDateTime;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.drew.lang.annotations.Nullable;

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

/**
 * PregnancyVisit Model - ANC (Antenatal Care) visit records
 * Captures longitudinal data for each visit during pregnancy
 *
 * @author Hema
 * @version 1.15
 */
@Entity
@Table(name = "OH_PREGNANCYVISIT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PRGV_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PRGV_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PRGV_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PRGV_LAST_MODIFIED_DATE"))
public class Visit extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PRGV_ID")
	private Integer ID;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "PRGV_PRG_ID")
	private Pregnancy pregnancy;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "PRGV_TYPE_ID")
	private VisitType visitType;

	@NotNull
	@Column(name = "PRGV_DATE")
	private LocalDateTime visitDate;

	@Nullable
	@Column(name = "PRGV_GESTATIONAL_WEEKS")
	private Integer gestationalWeeks;

	@Nullable
	@Column(name = "PRGV_GESTATIONAL_DAYS")
	private Integer gestationalDays;

	@Nullable
	@Column(name = "PRGV_MATERNAL_WEIGHT")
	private Double maternalWeight; // kg

	@Nullable
	@Column(name = "PRGV_SYSTOLIC_BP")
	private Integer systolicBP;

	@Nullable
	@Column(name = "PRGV_DIASTOLIC_BP")
	private Integer diastolicBP;

	@Nullable
	@Column(name = "PRGV_TEMPERATURE")
	private Double temperature;

	@Nullable
	@Column(name = "PRGV_FUNDAL_HEIGHT")
	private Double fundalHeight; // cm

	@Nullable
	@Column(name = "PRGV_ABDOMINAL_CIRCUMFERENCE")
	private Double abdominalCircumference; // cm

	@Nullable
	@Column(name = "PRGV_FETAL_HEART_RATE")
	private Integer fetalHeartRate; // BPM

	@Nullable
	@Column(name = "PRGV_FETAL_PRESENTATION")
	private String fetalPresentation;

	@Nullable
	@Column(name = "PRGV_CEPHALIC_ENGAGEMENT")
	private String cephalicEngagement;

	@Nullable
	@Column(name = "PRGV_EDEMA_PRESENCE")
	private String edemaPresence;

	@Nullable
	@Column(name = "PRGV_CONJUNCTIVA_STATUS")
	private String conjunctivaStatus;

	@Nullable
	@Column(name = "PRGV_LOWER_LIMBS")
	private String lowerLimbs;

	@Nullable
	@Column(name = "PRGV_BREASTS")
	private String breasts;

	@Nullable
	@Column(name = "PRGV_VAGINAL_EXAM")
	private String vaginalExam;

	@Nullable
	@Column(name = "PRGV_URINE_PROTEIN")
	private String urineProtein;

	@Nullable
	@Column(name = "PRGV_URINE_GLUCOSE")
	private String urineGlucose;

	@Nullable
	@Column(name = "PRGV_NEXT_APPOINTMENT_DATE")
	private java.time.LocalDate nextAppointmentDate;

	@Nullable
	@Column(name = "PRGV_CLINICAL_NOTES", columnDefinition = "LONGTEXT")
	private String clinicalNotes;

	@Version
	@Column(name = "PRGV_LOCK")
	private Integer lock;

	@Transient
	private volatile int hashCode;

	public Visit() {
	}

	public Visit(Pregnancy pregnancy, VisitType visitType, LocalDateTime visitDate) {
		this.pregnancy = pregnancy;
		this.visitDate = visitDate;
		this.visitType = visitType;
	}

	public Integer getID() {
		return ID;
	}

	public void setID(Integer ID) {
		this.ID = ID;
	}

	public Pregnancy getPregnancy() {
		return pregnancy;
	}

	public void setPregnancy(Pregnancy pregnancy) {
		this.pregnancy = pregnancy;
	}

	public LocalDateTime getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(LocalDateTime visitDate) {
		this.visitDate = visitDate;
	}

	public Integer getGestationalWeeks() {
		return gestationalWeeks;
	}

	public void setGestationalWeeks(Integer gestationalWeeks) {
		this.gestationalWeeks = gestationalWeeks;
	}

	public Integer getGestationalDays() {
		return gestationalDays;
	}

	public void setGestationalDays(Integer gestationalDays) {
		this.gestationalDays = gestationalDays;
	}

	public Double getMaternalWeight() {
		return maternalWeight;
	}

	public void setMaternalWeight(Double maternalWeight) {
		this.maternalWeight = maternalWeight;
	}

	public Integer getSystolicBP() {
		return systolicBP;
	}

	public void setSystolicBP(Integer systolicBP) {
		this.systolicBP = systolicBP;
	}

	public Integer getDiastolicBP() {
		return diastolicBP;
	}

	public void setDiastolicBP(Integer diastolicBP) {
		this.diastolicBP = diastolicBP;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Double getFundalHeight() {
		return fundalHeight;
	}

	public void setFundalHeight(Double fundalHeight) {
		this.fundalHeight = fundalHeight;
	}

	public Double getAbdominalCircumference() {
		return abdominalCircumference;
	}

	public void setAbdominalCircumference(Double abdominalCircumference) {
		this.abdominalCircumference = abdominalCircumference;
	}

	public Integer getFetalHeartRate() {
		return fetalHeartRate;
	}

	public void setFetalHeartRate(Integer fetalHeartRate) {
		this.fetalHeartRate = fetalHeartRate;
	}

	public String getFetalPresentation() {
		return fetalPresentation;
	}

	public void setFetalPresentation(String fetalPresentation) {
		this.fetalPresentation = fetalPresentation;
	}

	public String getCephalicEngagement() {
		return cephalicEngagement;
	}

	public void setCephalicEngagement(String cephalicEngagement) {
		this.cephalicEngagement = cephalicEngagement;
	}

	public String getEdemaPresence() {
		return edemaPresence;
	}

	public void setEdemaPresence(String edemaPresence) {
		this.edemaPresence = edemaPresence;
	}

	public String getConjunctivaStatus() {
		return conjunctivaStatus;
	}

	public void setConjunctivaStatus(String conjunctivaStatus) {
		this.conjunctivaStatus = conjunctivaStatus;
	}

	public String getLowerLimbs() {
		return lowerLimbs;
	}

	public void setLowerLimbs(String lowerLimbs) {
		this.lowerLimbs = lowerLimbs;
	}

	public String getBreasts() {
		return breasts;
	}

	public void setBreasts(String breasts) {
		this.breasts = breasts;
	}

	public String getVaginalExam() {
		return vaginalExam;
	}

	public void setVaginalExam(String vaginalExam) {
		this.vaginalExam = vaginalExam;
	}

	public String getUrineProtein() {
		return urineProtein;
	}

	public void setUrineProtein(String urineProtein) {
		this.urineProtein = urineProtein;
	}

	public String getUrineGlucose() {
		return urineGlucose;
	}

	public void setUrineGlucose(String urineGlucose) {
		this.urineGlucose = urineGlucose;
	}

	public java.time.LocalDate getNextAppointmentDate() {
		return nextAppointmentDate;
	}

	public void setNextAppointmentDate(java.time.LocalDate nextAppointmentDate) {
		this.nextAppointmentDate = nextAppointmentDate;
	}

	public String getClinicalNotes() {
		return clinicalNotes;
	}

	public void setClinicalNotes(String clinicalNotes) {
		this.clinicalNotes = clinicalNotes;
	}

	public VisitType getVisitType() {
		return visitType;
	}

	public void setVisitType(VisitType visitType) {
		this.visitType = visitType;
	}

	public Integer getLock() {
		return lock;
	}

	public void setLock(Integer lock) {
		this.lock = lock;
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			final int prime = 31;
			int hash = 7;
			hash = prime * hash + ID;
			this.hashCode = hash;
		}
		return this.hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Visit)) {
			return false;
		}
		Visit other = (Visit) obj;
		return ID == other.ID;
	}

	@Override
	public String toString() {
		return "PregnancyVisit{" +
				"ID=" + ID +
				", pregnancy=" + pregnancy.getID() +
				", visitDate=" + visitDate +
				", gestationalWeeks=" + gestationalWeeks +
				'}';
	}
}
