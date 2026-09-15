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
package org.isf.hivchildfollowup.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import org.isf.patient.model.Patient;
import org.isf.pregnancy.model.NewbornFeedingMode;
import org.isf.pregnancy.model.PregnancyNewborn;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------ HivExposedChild - PTME follow-up record for a child born to an
 * HIV-positive mother -----------------------------------------
 * Holds the PMTCT (prévention de la transmission mère-enfant) information at birth (ARV/CTX prophylaxis,
 * feeding mode) together with the list of {@link HivExposedChildVisit} (typically at 6 weeks, 9 months and
 * 18 months of age) that track the child's HIV status until a final outcome is reached.
 */
@Entity
@Table(name = "OH_HIVEXPOSEDCHILD")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "HEC_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "HEC_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "HEC_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "HEC_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "HEC_LAST_MODIFIED_DATE"))
public class HivExposedChild extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HEC_ID")
	private int id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "HEC_MOTHER_PAT_ID")
	private Patient motherPatient;

	/**
	 * The newborn this follow-up originates from, when it was created straight from a delivery marked
	 * {@link PregnancyNewborn#isHivExposed()}. Nullable so a child can also be enrolled manually (e.g. an
	 * older sibling, or a birth that happened outside this facility).
	 */
	@ManyToOne
	@JoinColumn(name = "HEC_PNB_ID")
	private PregnancyNewborn newborn;

	@Column(name = "HEC_CHILD_NAME")
	private String childName;

	@NotNull
	@Column(name = "HEC_DOB")
	private LocalDate dateOfBirth;

	@Column(name = "HEC_MOTHER_HIV_STATUS")
	private String motherHivStatus;

	@Column(name = "HEC_ARV_GIVEN")
	private boolean arvProphylaxisGiven;

	@Column(name = "HEC_ARV_REGIMEN")
	private String arvRegimen;

	@Column(name = "HEC_CTX_GIVEN")
	private boolean ctxProphylaxisGiven;

	@Enumerated(EnumType.STRING)
	@Column(name = "HEC_FEEDING_MODE")
	private NewbornFeedingMode feedingMode;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "HEC_STATUS", length = 20)
	private HivExposedChildStatus finalStatus = HivExposedChildStatus.ON_FOLLOWUP;

	@Column(name = "HEC_NOTE")
	private String note;

	@OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("visitDate ASC")
	private List<HivExposedChildVisit> visits = new ArrayList<>();

	@Transient
	private volatile int hashCode;

	public HivExposedChild() {
		super();
	}

	public HivExposedChild(Patient motherPatient, LocalDate dateOfBirth) {
		this.motherPatient = motherPatient;
		this.dateOfBirth = dateOfBirth;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Patient getMotherPatient() {
		return motherPatient;
	}

	public void setMotherPatient(Patient motherPatient) {
		this.motherPatient = motherPatient;
	}

	public PregnancyNewborn getNewborn() {
		return newborn;
	}

	public void setNewborn(PregnancyNewborn newborn) {
		this.newborn = newborn;
	}

	public String getChildName() {
		return childName;
	}

	public void setChildName(String childName) {
		this.childName = childName;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getMotherHivStatus() {
		return motherHivStatus;
	}

	public void setMotherHivStatus(String motherHivStatus) {
		this.motherHivStatus = motherHivStatus;
	}

	public boolean isArvProphylaxisGiven() {
		return arvProphylaxisGiven;
	}

	public void setArvProphylaxisGiven(boolean arvProphylaxisGiven) {
		this.arvProphylaxisGiven = arvProphylaxisGiven;
	}

	public String getArvRegimen() {
		return arvRegimen;
	}

	public void setArvRegimen(String arvRegimen) {
		this.arvRegimen = arvRegimen;
	}

	public boolean isCtxProphylaxisGiven() {
		return ctxProphylaxisGiven;
	}

	public void setCtxProphylaxisGiven(boolean ctxProphylaxisGiven) {
		this.ctxProphylaxisGiven = ctxProphylaxisGiven;
	}

	public NewbornFeedingMode getFeedingMode() {
		return feedingMode;
	}

	public void setFeedingMode(NewbornFeedingMode feedingMode) {
		this.feedingMode = feedingMode;
	}

	public HivExposedChildStatus getFinalStatus() {
		return finalStatus;
	}

	public void setFinalStatus(HivExposedChildStatus finalStatus) {
		this.finalStatus = finalStatus;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<HivExposedChildVisit> getVisits() {
		return visits;
	}

	public void setVisits(List<HivExposedChildVisit> visits) {
		this.visits = visits;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof HivExposedChild)) {
			return false;
		}
		return getId() == ((HivExposedChild) other).getId();
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
