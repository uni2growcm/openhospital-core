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
package org.isf.maternity.model;

import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Newborn Model - Records for each newborn child from a delivery
 *
 * @author Hema
 * @version 1.15
 */
@Entity
@Table(name = "OH_NEWBORN")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "NBN_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "NBN_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "NBN_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "NBN_LAST_MODIFIED_DATE"))
@AttributeOverride(name = "active", column = @Column(name = "NBN_ACTIVE"))
public class Newborn extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "NBN_ID")
	private Integer id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "NBN_DLV_ID", nullable = false)
	private PregnancyDelivery delivery;

	@NotNull
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "NBN_PAT_ID", nullable = false)
	private Patient babyPatient;

	@Column(name = "NBN_BIRTH_DATE")
	private LocalDateTime birthDate;

	@Column(name = "NBN_NEONATAL_STATUS")
	@Enumerated(EnumType.STRING)
	private NeonatalStatus neonatalStatus;

	@Column(name = "NBN_BIRTH_ORDER")
	private String birthOrder;

	@Column(name = "NBN_COMPLICATION")
	private String complication;

	@NotNull
	@Column(name = "NBN_BIRTH_WEIGHT", nullable = false)
	private Double birthWeight;

	@Column(name = "NBN_BIRTH_LENGTH")
	private Double birthLength;

	@Column(name = "NBN_HEAD_CIRCUMFERENCE")
	private Double headCircumference;

	@Column(name = "NBN_APGAR_SCORE_1MIN")
	private Integer apgarScore1Min;

	@Column(name = "NBN_APGAR_SCORE_5MIN")
	private Integer apgarScore5Min;

	@Column(name = "NBN_RESUSCITATION_REQUIRED")
	private Boolean resuscitationRequired;

	@Column(name = "NBN_CRY_TIME")
	@Enumerated(EnumType.STRING)
	private CryTime cryTime;

	@Column(name = "NBN_CONGENITAL_ANOMALIES", columnDefinition = "LONGTEXT")
	private String congenitalAnomalies;

	@Column(name = "NBN_HIV_STATUS", nullable = false)
	@Enumerated(EnumType.STRING)
	private HivStatus hivStatus;

	@Version
	@Column(name = "NBN_LOCK")
	private Integer lock;

	@Transient
	private volatile int hashCode;

	public Newborn () {}

	public Newborn(
		Patient babyPatient,
		PregnancyDelivery delivery,
		LocalDateTime birthDate,
		HivStatus hivStatus
	) {
		this.babyPatient =  babyPatient;
		this.delivery = delivery;
		this.birthDate = birthDate;
		this.hivStatus = hivStatus;
	}

	public Newborn(
		Patient babyPatient,
		PregnancyDelivery delivery,
		NeonatalStatus neonatalStatus,
		LocalDateTime birthDate,
		String birthOrder,
		Double birthWeight,
		Double birthLength,
		Double headCircumference,
		Integer apgarScore1Min,
		Integer apgarScore5Min,
		Boolean resuscitationRequired,
		CryTime cryTime,
		String congenitalAnomalies,
		HivStatus hivStatus
	) {
		this.babyPatient = babyPatient;
		this.delivery = delivery;
		this.neonatalStatus = neonatalStatus;
		this.birthDate = birthDate;
		this.birthOrder = birthOrder;
		this.birthWeight = birthWeight;
		this.birthLength = birthLength;
		this.headCircumference = headCircumference;
		this.apgarScore1Min = apgarScore1Min;
		this.apgarScore5Min = apgarScore5Min;
		this.resuscitationRequired = resuscitationRequired;
		this.cryTime = cryTime;
		this.congenitalAnomalies = congenitalAnomalies;
		this.hivStatus = hivStatus;
	}

	public Newborn(
		Patient babyPatient,
		PregnancyDelivery delivery,
		NeonatalStatus neonatalStatus,
		LocalDateTime birthDate,
		String birthOrder,
		Double birthWeight,
		Double birthLength,
		Double headCircumference,
		Integer apgarScore1Min,
		Integer apgarScore5Min,
		Boolean resuscitationRequired,
		CryTime cryTime,
		String congenitalAnomalies,
		HivStatus hivStatus,
		String complication
	) {
		this.babyPatient = babyPatient;
		this.delivery = delivery;
		this.neonatalStatus = neonatalStatus;
		this.birthDate = birthDate;
		this.birthOrder = birthOrder;
		this.birthWeight = birthWeight;
		this.birthLength = birthLength;
		this.headCircumference = headCircumference;
		this.apgarScore1Min = apgarScore1Min;
		this.apgarScore5Min = apgarScore5Min;
		this.resuscitationRequired = resuscitationRequired;
		this.cryTime = cryTime;
		this.congenitalAnomalies = congenitalAnomalies;
		this.hivStatus = hivStatus;
		this.complication = complication;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public PregnancyDelivery getDelivery() {
		return delivery;
	}

	public void setDelivery(PregnancyDelivery delivery) {
		this.delivery = delivery;
	}

	public NeonatalStatus getNeonatalStatus() {
		return neonatalStatus;
	}

	public void setNeonatalStatus(NeonatalStatus neonatalStatus) {
		this.neonatalStatus = neonatalStatus;
	}

	public String getBirthOrder() {
		return birthOrder;
	}

	public void setBirthOrder(String birthOrder) {
		this.birthOrder = birthOrder;
	}

	public Patient getBabyPatient() {
		return babyPatient;
	}

	public void setBabyPatient(Patient babyPatient) {
		this.babyPatient = babyPatient;
	}

	public Double getBirthWeight() {
		return birthWeight;
	}

	public void setBirthWeight(Double birthWeight) {
		this.birthWeight = birthWeight;
	}

	public LocalDateTime getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDateTime birthDate) {
		this.birthDate = birthDate;
	}

	public Double getBirthLength() {
		return birthLength;
	}

	public void setBirthLength(Double birthLength) {
		this.birthLength = birthLength;
	}

	public Double getHeadCircumference() {
		return headCircumference;
	}

	public void setHeadCircumference(Double headCircumference) {
		this.headCircumference = headCircumference;
	}

	public Integer getApgarScore1Min() {
		return apgarScore1Min;
	}

	public void setApgarScore1Min(Integer apgarScore1Min) {
		this.apgarScore1Min = apgarScore1Min;
	}

	public Integer getApgarScore5Min() {
		return apgarScore5Min;
	}

	public void setApgarScore5Min(Integer apgarScore5Min) {
		this.apgarScore5Min = apgarScore5Min;
	}

	public Boolean getResuscitationRequired() {
		return resuscitationRequired;
	}

	public void setResuscitationRequired(Boolean resuscitationRequired) {
		this.resuscitationRequired = resuscitationRequired;
	}

	public CryTime getCryTime() {
		return cryTime;
	}

	public void setCryTime(CryTime cryTime) {
		this.cryTime = cryTime;
	}

	public String getCongenitalAnomalies() {
		return congenitalAnomalies;
	}

	public void setCongenitalAnomalies(String congenitalAnomalies) {
		this.congenitalAnomalies = congenitalAnomalies;
	}

	public HivStatus getHivStatus() {
		return hivStatus;
	}

	public void setHivStatus(HivStatus hivStatus) {
		this.hivStatus = hivStatus;
	}

	public String getComplication() {
		return complication;
	}

	public void setComplication(String complication) {
		this.complication = complication;
	}

	public Integer getLock() {
		return lock;
	}

	public void setLock(Integer lock) {
		this.lock = lock;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Newborn other)) return false;
		return id != null && id.equals(other.id);
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			final int m = 23;
			int c = 133;
			c = m * c + id.hashCode();
			this.hashCode = c;
		}
		return this.hashCode;
	}

	@Override
	public String toString() {
		return "Newborn{" +
			"ID=" + id +
			", name=" + (babyPatient.getName()) +
			", gender=" + (babyPatient.getSex()) +
			", HIV status ='" + (hivStatus) + '\'' +
			'}';
	}
}