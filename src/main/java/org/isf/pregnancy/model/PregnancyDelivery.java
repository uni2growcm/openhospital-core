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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.isf.admission.model.Admission;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------ PregnancyDelivery - one delivery event -----------------------------------------
 * Holds the mother-level information of a childbirth (father identification, lochies, counseling given,
 * family planning method chosen at discharge) together with the list of {@link PregnancyNewborn} (1 to 4).
 * <p>
 * Linked to a {@link Pregnancy} (recorded from the CPN module, whether or not the mother is hospitalized)
 * and/or to an {@link Admission} (recorded from a ward admission, e.g. a maternity stay) - at least one of
 * the two must be set, enforced by {@code PregnancyDeliveryBrowserManager#validate}, not by a database
 * constraint, since either one alone is a legitimate way to record a delivery.
 */
@Entity
@Table(name = "OH_PREGNANCYDELIVERY")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PDEL_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PDEL_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PDEL_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PDEL_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PDEL_LAST_MODIFIED_DATE"))
public class PregnancyDelivery extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PDEL_ID")
	private int id;

	@ManyToOne
	@JoinColumn(name = "PDEL_ADM_ID")
	private Admission admission;

	@ManyToOne
	@JoinColumn(name = "PDEL_PREG_ID")
	private Pregnancy pregnancy;

	@Column(name = "PDEL_DATE_DEL")
	private LocalDateTime deliveryDate;

	@Column(name = "PDEL_FATHER_NAME")
	private String fatherName;

	@Column(name = "PDEL_FATHER_OCCUPATION")
	private String fatherOccupation;

	@Column(name = "PDEL_FATHER_RESIDENCE")
	private String fatherResidence;

	@Column(name = "PDEL_FATHER_BIRTH_PLACE")
	private String fatherBirthPlace;

	@Column(name = "PDEL_FATHER_AGE")
	private Integer fatherAge;

	@Column(name = "PDEL_FATHER_CNI")
	private Long fatherCni;

	/**
	 * Lochies / saignement (registre d'accouchement, colonne 36).
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "PDEL_LOCHIES")
	private Lochies lochies;

	/**
	 * Counseling donné à la sortie, liste de thèmes séparés par ';' (registre d'accouchement, colonne 44).
	 */
	@Column(name = "PDEL_COUNSELING")
	private String counseling;

	/**
	 * Méthode de planning familial choisie à la sortie (registre d'accouchement, colonne 45).
	 */
	@Column(name = "PDEL_FP_METHOD")
	private String familyPlanningMethodChosen;

	@OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderBy("childNumber ASC")
	private List<PregnancyNewborn> newborns = new ArrayList<>();

	@Transient
	private volatile int hashCode;

	public PregnancyDelivery() {
		super();
	}

	public PregnancyDelivery(Admission admission) {
		this.admission = admission;
	}

	public PregnancyDelivery(Pregnancy pregnancy) {
		this.pregnancy = pregnancy;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Admission getAdmission() {
		return admission;
	}

	public void setAdmission(Admission admission) {
		this.admission = admission;
	}

	public Pregnancy getPregnancy() {
		return pregnancy;
	}

	public void setPregnancy(Pregnancy pregnancy) {
		this.pregnancy = pregnancy;
	}

	public LocalDateTime getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(LocalDateTime deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getFatherOccupation() {
		return fatherOccupation;
	}

	public void setFatherOccupation(String fatherOccupation) {
		this.fatherOccupation = fatherOccupation;
	}

	public String getFatherResidence() {
		return fatherResidence;
	}

	public void setFatherResidence(String fatherResidence) {
		this.fatherResidence = fatherResidence;
	}

	public String getFatherBirthPlace() {
		return fatherBirthPlace;
	}

	public void setFatherBirthPlace(String fatherBirthPlace) {
		this.fatherBirthPlace = fatherBirthPlace;
	}

	public Integer getFatherAge() {
		return fatherAge;
	}

	public void setFatherAge(Integer fatherAge) {
		this.fatherAge = fatherAge;
	}

	public Long getFatherCni() {
		return fatherCni;
	}

	public void setFatherCni(Long fatherCni) {
		this.fatherCni = fatherCni;
	}

	public Lochies getLochies() {
		return lochies;
	}

	public void setLochies(Lochies lochies) {
		this.lochies = lochies;
	}

	public String getCounseling() {
		return counseling;
	}

	public void setCounseling(String counseling) {
		this.counseling = counseling;
	}

	public String getFamilyPlanningMethodChosen() {
		return familyPlanningMethodChosen;
	}

	public void setFamilyPlanningMethodChosen(String familyPlanningMethodChosen) {
		this.familyPlanningMethodChosen = familyPlanningMethodChosen;
	}

	public List<PregnancyNewborn> getNewborns() {
		return newborns;
	}

	public void setNewborns(List<PregnancyNewborn> newborns) {
		this.newborns = newborns;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof PregnancyDelivery)) {
			return false;
		}
		return getId() == ((PregnancyDelivery) other).getId();
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
