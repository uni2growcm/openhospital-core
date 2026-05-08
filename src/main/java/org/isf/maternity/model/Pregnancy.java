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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import jakarta.persistence.*;
import org.isf.generaldata.MessageBundle;
import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.annotation.Nullable;

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
	private Integer id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "PRG_PAT_ID")
	private Patient patient;

	@Nullable
	@Column(name = "PRG_DATE")
	private LocalDateTime date;

	@OneToMany(mappedBy = "pregnancy")
	private List<PregnancyVisit> visits;

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

	@Enumerated(EnumType.STRING)
	@Column(name = "PRG_RISK_LEVEL")
	private RiskLevel riskLevel;

	@Enumerated(EnumType.STRING)
	@Column(name = "PRG_STATUS")
	private PregnancyStatus status;

	@Version
	@Column(name = "PRG_LOCK")
	private Integer lock;

	@Transient
	private volatile int hashCode;

	public Pregnancy() {
	}

	public Pregnancy(Patient patient, LocalDateTime date, LocalDateTime lmp) {
		this.patient = patient;
		this.date = date;
		this.lmp = lmp;
		this.status = PregnancyStatus.ONGOING;
		this.riskLevel = RiskLevel.LOW;
		this.miscarriages = 0;
	}

	/**
	 * Calculate current gestational age in weeks and days
	 * 
	 * @return String representing gestational age (e.g., "24+3")
	 */
	@Transient
	public String getCurrentGestationalAge() {
		if (this.lmp == null) {
			return null;
		}

		LocalDate lmpDate = this.lmp.toLocalDate();

		long daysDifference = ChronoUnit.DAYS.between(lmpDate, LocalDate.now());

		long weeks = daysDifference / 7;
		long days = daysDifference % 7;

		return weeks + " " + MessageBundle.getMessage("angal.maternity.gestationalage.weeks") + " " + days +
			" " + MessageBundle.getMessage("angal.maternity.gestationalage.days");
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

	public LocalDateTime getDate() { return this.date; }

	public void setDate(LocalDateTime date) { this.date = date; }

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

	public RiskLevel getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(RiskLevel riskLevel) {
		this.riskLevel = riskLevel;
	}

	public PregnancyStatus getStatus() {
		return status;
	}

	public void setStatus(PregnancyStatus status) {
		this.status = status;
	}

	public Integer getLock() {
		return lock;
	}

	public void setLock(Integer lock) {
		this.lock = lock;
	}

	public List<PregnancyVisit> getVisits() {
		return visits;
	}

	public void setVisits(List<PregnancyVisit> visits) {
		this.visits = visits;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Pregnancy other)) return false;
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
		return "Pregnancy{" +
				"ID=" + id +
				", patient=" + patient +
				", status='" + status + '\'' +
				", riskLevel='" + riskLevel + '\'' +
				'}';
	}
}
