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
package org.isf.pregnancy.model;

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
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------ Pregnancy - model for the CPN (Consultation Prénatale) module
 * -----------------------------------------
 * Represents one pregnancy of a {@link Patient}: the last menstrual period (DDR), the automatically computed
 * expected delivery date (DPA) and the gravidity/parity history captured at CPN registration.
 */
@Entity
@Table(name = "OH_PREGNANCY")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PREG_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PREG_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PREG_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PREG_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PREG_LAST_MODIFIED_DATE"))
public class Pregnancy extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PREG_ID")
	private int id;

	@Column(name = "PREG_NR")
	private int pregnancyNr = 1;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "PREG_PAT_ID")
	private Patient patient;

	@NotNull
	@Column(name = "PREG_LMP")
	private LocalDate lmp;

	@Column(name = "PREG_CALC_DELIVERY")
	private LocalDate scheduledDelivery;

	/**
	 * Geste : nombre total de grossesses (remplace l'ancien libellé "nombre d'accouchements").
	 */
	@Column(name = "PREG_NB_GROSS")
	private int nPregnancies;

	@Column(name = "PREG_NB_ACCT")
	private int nTermDeliveries;

	@Column(name = "PREG_NB_ACCP")
	private int nPretermDeliveries;

	@Column(name = "PREG_NB_AVORT")
	private int nAbortions;

	@Column(name = "PREG_NB_ENF_VIV")
	private int nLiveChildren;

	@Column(name = "PREG_NB_ENF_MRT_NEE")
	private int nStillbirths;

	@Column(name = "PREG_NB_ENF_DEC")
	private int nDeceasedChildren;

	@Column(name = "PREG_NB_ENF_DES")
	private int nTotalDesiredChildren;

	@Column(name = "PREG_AGE_LAST")
	private Integer ageOfLastChild;

	@Column(name = "PREG_ALLAIT")
	private boolean breastfeeding;

	@Transient
	private volatile int hashCode;

	public Pregnancy() {
		super();
	}

	public Pregnancy(Patient patient, LocalDate lmp) {
		this.patient = patient;
		setLmp(lmp);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPregnancyNr() {
		return pregnancyNr;
	}

	public void setPregnancyNr(int pregnancyNr) {
		this.pregnancyNr = pregnancyNr;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public LocalDate getLmp() {
		return lmp;
	}

	/**
	 * Sets the last menstrual period (DDR) and automatically recomputes the expected delivery date
	 * (DPA) as {@code lmp + 9 months}.
	 *
	 * @param lmp the date of the last menstrual period
	 */
	public void setLmp(LocalDate lmp) {
		this.lmp = lmp;
		this.scheduledDelivery = lmp == null ? null : lmp.plusMonths(9);
	}

	public LocalDate getScheduledDelivery() {
		return scheduledDelivery;
	}

	/**
	 * Should normally not be called directly: the scheduled delivery date is derived from
	 * {@link #setLmp(LocalDate)}. Exposed only for persistence frameworks / manual overrides.
	 */
	public void setScheduledDelivery(LocalDate scheduledDelivery) {
		this.scheduledDelivery = scheduledDelivery;
	}

	public int getnPregnancies() {
		return nPregnancies;
	}

	public void setnPregnancies(int nPregnancies) {
		this.nPregnancies = nPregnancies;
	}

	public int getnTermDeliveries() {
		return nTermDeliveries;
	}

	public void setnTermDeliveries(int nTermDeliveries) {
		this.nTermDeliveries = nTermDeliveries;
	}

	public int getnPretermDeliveries() {
		return nPretermDeliveries;
	}

	public void setnPretermDeliveries(int nPretermDeliveries) {
		this.nPretermDeliveries = nPretermDeliveries;
	}

	public int getnAbortions() {
		return nAbortions;
	}

	public void setnAbortions(int nAbortions) {
		this.nAbortions = nAbortions;
	}

	public int getnLiveChildren() {
		return nLiveChildren;
	}

	public void setnLiveChildren(int nLiveChildren) {
		this.nLiveChildren = nLiveChildren;
	}

	public int getnStillbirths() {
		return nStillbirths;
	}

	public void setnStillbirths(int nStillbirths) {
		this.nStillbirths = nStillbirths;
	}

	public int getnDeceasedChildren() {
		return nDeceasedChildren;
	}

	public void setnDeceasedChildren(int nDeceasedChildren) {
		this.nDeceasedChildren = nDeceasedChildren;
	}

	public int getnTotalDesiredChildren() {
		return nTotalDesiredChildren;
	}

	public void setnTotalDesiredChildren(int nTotalDesiredChildren) {
		this.nTotalDesiredChildren = nTotalDesiredChildren;
	}

	public Integer getAgeOfLastChild() {
		return ageOfLastChild;
	}

	public void setAgeOfLastChild(Integer ageOfLastChild) {
		this.ageOfLastChild = ageOfLastChild;
	}

	public boolean isBreastfeeding() {
		return breastfeeding;
	}

	public void setBreastfeeding(boolean breastfeeding) {
		this.breastfeeding = breastfeeding;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Pregnancy)) {
			return false;
		}
		return getId() == ((Pregnancy) other).getId();
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			final int m = 23;
			int c = 133;
			c = m * c + id;
			this.hashCode = c;
		}
		return this.hashCode;
	}
}
