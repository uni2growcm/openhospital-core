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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.isf.patient.model.Patient;
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
 * Pregnancy Model - Master record tracking the entire journey from conception to conclusion
 *
 * @author Hema
 * @version 1.15
 */
@Entity
@Table(name = "OH_PREGNANCY")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PRG_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PRG_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PRG_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PRG_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PRG_LAST_MODIFIED_DATE"))
public class Pregnancy extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PRG_ID")
	private Integer ID;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "PRG_PAT_ID")
	private Patient patient;

	@Nullable
	@Column(name = "PRG_LMP")
	private LocalDateTime lmp; // Last Menstrual Period Date

	@Nullable
	@Column(name = "PRG_EDD_LMP")
	private LocalDateTime eddLmp; // Calculated Delivery Date

	@Nullable
	@Column(name = "PRG_EDD_SCAN")
	private LocalDateTime eddScan; // Delivery Date adjusted by Ultrasound

	@Nullable
	@Column(name = "PRG_GRAVIDITY")
	private Integer gravidity; // Total number of times pregnant

	@Nullable
	@Column(name = "PRG_PARITY")
	private Integer parity; // Number of viable pregnancies

	@Nullable
	@Column(name = "PRG_MISSCARRIAGES")
	private Integer miscarriages; // Count of abortions/miscarriages

	@Nullable
	@Column(name = "PRG_BLOOD_GROUP")
	private String bloodGroup; // Blood group and Rh factor

	@Nullable
	@Column(name = "PRG_RISK_LEVEL")
	private String riskLevel; // Low, Medium, High

	@NotNull
	@Column(name = "PRG_STATUS")
	private String status; // Ongoing, Completed, Terminated

	@Version
	@Column(name = "PRG_LOCK")
	private Integer lock;

	@Transient
	private volatile int hashCode;

	public Pregnancy() {
	}

	public Pregnancy(Patient patient, String status) {
		this.patient = patient;
		this.status = status;
		this.riskLevel = "Low";
		this.miscarriages = 0;
	}

	/**
	 * Calculate current gestational age in weeks and days
	 * 
	 * @return String representing gestational age (e.g., "24+3")
	 */
	@Transient
	public String getCurrentGestationalAge() {
		if (lmp == null) {
			return null;
		}
		long daysDifference = ChronoUnit.DAYS.between(lmp, LocalDate.now());
		long weeks = daysDifference / 7;
		long days = daysDifference % 7;
		return weeks + "+" + days;
	}

	public Integer getID() {
		return ID;
	}

	public void setID(Integer ID) {
		this.ID = ID;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public LocalDateTime getLmp() {
		return lmp;
	}

	public void setLmp(LocalDateTime lmp) {
		this.lmp = lmp;
	}

	public LocalDateTime getEddLmp() {
		return eddLmp;
	}

	public void setEddLmp(LocalDateTime eddLmp) {
		this.eddLmp = eddLmp;
	}

	public LocalDateTime getEddScan() {
		return eddScan;
	}

	public void setEddScan(LocalDateTime eddScan) {
		this.eddScan = eddScan;
	}

	public Integer getGravidity() {
		return gravidity;
	}

	public void setGravidity(Integer gravidity) {
		this.gravidity = gravidity;
	}

	public Integer getParity() {
		return parity;
	}

	public void setParity(Integer parity) {
		this.parity = parity;
	}

	public Integer getMiscarriages() {
		return miscarriages;
	}

	public void setMiscarriages(Integer miscarriages) {
		this.miscarriages = miscarriages;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
		if (!(obj instanceof Pregnancy other)) {
			return false;
		}
		return Objects.equals(ID, other.ID);
	}

	@Override
	public String toString() {
		return "Pregnancy{" +
				"ID=" + ID +
				", patient=" + patient +
				", status='" + status + '\'' +
				", riskLevel='" + riskLevel + '\'' +
				'}';
	}
}
