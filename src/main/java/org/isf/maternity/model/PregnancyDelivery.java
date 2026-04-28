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

import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.isf.typology.model.Typology;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.annotation.Nullable;

import jakarta.validation.constraints.NotNull;

/**
 * Delivery Model - Records the labor process and mother's surgical/medical outcome
 *
 * @author Hema
 * @version 1.15
 */
@Entity
@Table(name = "OH_PREGNANCYDELIVERY")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PRGDLV_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PRGDLV_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PRGDLV_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PRGDLV_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PRGDLV_LAST_MODIFIED_DATE"))
public class PregnancyDelivery extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PRGDLV_ID")
	private Integer id;

	@NotNull
	@OneToOne
	@JoinColumn(name = "PRGDLV_PRG_ID")
	private Pregnancy pregnancy;

	@NotNull
	@ManyToOne
	@Enumerated(EnumType.STRING)
	@JoinColumn(name = "PRGDLV_TYPE_ID")
	private Typology deliveryType;

	@Nullable
	@Column(name = "PRGDLV_LABOR_ONSET_DATETIME")
	private LocalDateTime laborOnsetDateTime;

	@Nullable
	@Column(name = "PRGDLV_ROM_DATETIME")
	private LocalDateTime ruptureMembranesDateTime;

	@NotNull
	@Column(name = "PRGDLV_DATE")
	private LocalDateTime deliveryDate;

	@Nullable
	@Column(name = "PRGDLV_DELIVERYMODE")
	private DeliveryMode  deliveryMode; // SVD, Vacuum, Forceps, Emergency C-Section, Elective C-Section

	@Nullable
	@Column(name = "PRGDLV_INDICATION")
	private String indication;

	@Nullable
	@Column(name = "PRGDLV_ANESTHESIA_USED")
	private String anesthesiaUsed;

	@Nullable
	@Column(name = "PRGDLV_PERINEAL_INTEGRITY")
	private PerinealIntegrity perinealIntegrity;

	@Nullable
	@Column(name = "PRGDLV_PLACENTA_COMPLETE")
	private Boolean placentaComplete;

	@Nullable
	@Column(name = "PRGDLV_PLACENTA_WEIGHT")
	private Integer placentaWeight; // grams

	@Nullable
	@Column(name = "PRGDLV_ESTIMATED_BLD_LOSS")
	private Integer estimatedBloodLoss; // ml

	@Nullable
	@Column(name = "PRGDLV_ATTENDING_CLINICIAN_ID")
	private String attendingClinicianId;

	@Nullable
	@Column(name = "PRGDLV_FATHER_NAME")
	private String fatherName;

	@Nullable
	@Column(name = "PRGDLV_FATHER_AGE")
	private Integer fatherAge;

	@Nullable
	@Column(name = "PRGDLV_FATHER_BIRTHPLACE")
	private String fatherBirthplace;

	@Nullable
	@Column(name = "PRGDLV_FATHER_ADDRESS")
	private String fatherAddress;

	@Nullable
	@Column(name = "PRGDLV_FATHER_PROFESSION")
	private String fatherProfession;

	@Version
	@Column(name = "PRGDLV_LOCK")
	private Integer lock;

	@Transient
	private volatile int hashCode;

	public PregnancyDelivery() {
	}

	public PregnancyDelivery(Pregnancy pregnancy, Typology pregnancyDeliveryType, LocalDateTime deliveryDate) {
		this.pregnancy = pregnancy;
		this.deliveryDate = deliveryDate;
		this.deliveryType = pregnancyDeliveryType;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Pregnancy getPregnancy() {
		return pregnancy;
	}

	public void setPregnancy(Pregnancy pregnancy) {
		this.pregnancy = pregnancy;
	}

	public Typology getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(Typology pregnancyDeliveryType) {
		this.deliveryType = pregnancyDeliveryType;
	}

	public LocalDateTime getLaborOnsetDateTime() {
		return laborOnsetDateTime;
	}

	public void setLaborOnsetDateTime(LocalDateTime laborOnsetDateTime) {
		this.laborOnsetDateTime = laborOnsetDateTime;
	}

	public LocalDateTime getRuptureMembranesDateTime() {
		return ruptureMembranesDateTime;
	}

	public void setRuptureMembranesDateTime(LocalDateTime ruptureMembranesDateTime) {
		this.ruptureMembranesDateTime = ruptureMembranesDateTime;
	}

	public LocalDateTime getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(LocalDateTime deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public DeliveryMode getDeliveryMode() {
		return deliveryMode;
	}

	public void setDeliveryMode(DeliveryMode deliveryMode) {
		this.deliveryMode = deliveryMode;
	}

	public String getIndication() {
		return indication;
	}

	public void setIndication(String indication) {
		this.indication = indication;
	}

	public String getAnesthesiaUsed() {
		return anesthesiaUsed;
	}

	public void setAnesthesiaUsed(String anesthesiaUsed) {
		this.anesthesiaUsed = anesthesiaUsed;
	}

	public PerinealIntegrity getPerinealIntegrity() {
		return perinealIntegrity;
	}

	public void setPerinealIntegrity(PerinealIntegrity perinealIntegrity) {
		this.perinealIntegrity = perinealIntegrity;
	}

	public Boolean getPlacentaComplete() {
		return placentaComplete;
	}

	public void setPlacentaComplete(Boolean placentaComplete) {
		this.placentaComplete = placentaComplete;
	}

	public Integer getPlacentaWeight() {
		return placentaWeight;
	}

	public void setPlacentaWeight(Integer placentaWeight) {
		this.placentaWeight = placentaWeight;
	}

	public Integer getEstimatedBloodLoss() {
		return estimatedBloodLoss;
	}

	public void setEstimatedBloodLoss(Integer estimatedBloodLoss) {
		this.estimatedBloodLoss = estimatedBloodLoss;
	}

	public String getAttendingClinicianId() {
		return attendingClinicianId;
	}

	public void setAttendingClinicianId(String attendingClinicianId) {
		this.attendingClinicianId = attendingClinicianId;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public Integer getFatherAge() {
		return fatherAge;
	}

	public void setFatherAge(Integer fatherAge) {
		this.fatherAge = fatherAge;
	}

	public String getFatherBirthplace() {
		return fatherBirthplace;
	}

	public void setFatherBirthplace(String fatherBirthplace) {
		this.fatherBirthplace = fatherBirthplace;
	}

	public String getFatherAddress() {
		return fatherAddress;
	}

	public void setFatherAddress(String fatherAddress) {
		this.fatherAddress = fatherAddress;
	}

	public String getFatherProfession() {
		return fatherProfession;
	}

	public void setFatherProfession(String fatherProfession) {
		this.fatherProfession = fatherProfession;
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
		if (!(o instanceof PregnancyDelivery other)) return false;
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
		return "Delivery{" +
			"ID=" + id +
			", pregnancyId=" + (pregnancy != null ? pregnancy.getId() : null) +
			", modeOfDelivery='" + deliveryMode + '\'' +
			", deliveryDateTime=" + deliveryDate +
			'}';
	}
}
