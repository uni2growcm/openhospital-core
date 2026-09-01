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

import jakarta.persistence.AttributeOverride;
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
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------ PregnancyNewborn - one newborn of a {@link PregnancyDelivery}
 * -----------------------------------------
 * A delivery can have between 1 and 4 newborns (twins/triplets/quadruplets): each is its own row, which is
 * what makes supporting a 4th child a matter of allowing a 4th row rather than adding fixed columns.
 */
@Entity
@Table(name = "OH_PREGNANCYNEWBORN")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PNB_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PNB_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PNB_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PNB_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PNB_LAST_MODIFIED_DATE"))
public class PregnancyNewborn extends Auditable<String> {

	/**
	 * Maximum number of newborns supported for a single delivery (twins, triplets, quadruplets).
	 */
	public static final int MAX_CHILDREN = 4;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PNB_ID")
	private int id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "PNB_PDEL_ID")
	private PregnancyDelivery delivery;

	@Column(name = "PNB_CHILD_NUMBER")
	private int childNumber = 1;

	@Column(name = "PNB_CHILD_NAME")
	private String childName;

	@Column(name = "PNB_SEX")
	private char sex = 'F';

	@ManyToOne
	@JoinColumn(name = "PNB_DLT_ID_A")
	private DeliveryType deliveryType;

	@ManyToOne
	@JoinColumn(name = "PNB_DRT_ID_A")
	private DeliveryResultType deliveryResultType;

	@Column(name = "PNB_WEIGHT")
	private Float weight;

	@Column(name = "PNB_HEIGHT")
	private Float height;

	/**
	 * Périmètre crânien (PC), max 100 cm.
	 */
	@Column(name = "PNB_HEAD_CIRC")
	private Double headCircumference;

	/**
	 * Périmètre thoracique (PT), max 50 cm.
	 */
	@Column(name = "PNB_CHEST_CIRC")
	private Double chestCircumference;

	/**
	 * Périmètre brachial (PB), max 30 cm.
	 */
	@Column(name = "PNB_ARM_CIRC")
	private Double armCircumference;

	/**
	 * Complications du nouveau-né (registre d'accouchement, colonne 31).
	 */
	@Column(name = "PNB_COMPLICATIONS")
	private String complications;

	/**
	 * Malformation congénitale (registre d'accouchement, colonne 32).
	 */
	@Column(name = "PNB_MALFORMATION")
	private String congenitalMalformation;

	@Enumerated(EnumType.STRING)
	@Column(name = "PNB_FEEDING_MODE")
	private NewbornFeedingMode feedingMode;

	/**
	 * True when the mother is HIV positive, so this newborn should be enrolled in the HIV-exposed child
	 * follow-up module (Phase 5).
	 */
	@Column(name = "PNB_HIV_EXPOSED")
	private boolean hivExposed;

	@Transient
	private volatile int hashCode;

	public PregnancyNewborn() {
		super();
	}

	public PregnancyNewborn(PregnancyDelivery delivery, int childNumber) {
		this.delivery = delivery;
		this.childNumber = childNumber;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PregnancyDelivery getDelivery() {
		return delivery;
	}

	public void setDelivery(PregnancyDelivery delivery) {
		this.delivery = delivery;
	}

	public int getChildNumber() {
		return childNumber;
	}

	public void setChildNumber(int childNumber) {
		this.childNumber = childNumber;
	}

	public String getChildName() {
		return childName;
	}

	public void setChildName(String childName) {
		this.childName = childName;
	}

	public char getSex() {
		return sex;
	}

	public void setSex(char sex) {
		this.sex = sex;
	}

	public DeliveryType getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(DeliveryType deliveryType) {
		this.deliveryType = deliveryType;
	}

	public DeliveryResultType getDeliveryResultType() {
		return deliveryResultType;
	}

	public void setDeliveryResultType(DeliveryResultType deliveryResultType) {
		this.deliveryResultType = deliveryResultType;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public Float getHeight() {
		return height;
	}

	public void setHeight(Float height) {
		this.height = height;
	}

	public Double getHeadCircumference() {
		return headCircumference;
	}

	public void setHeadCircumference(Double headCircumference) {
		this.headCircumference = headCircumference;
	}

	public Double getChestCircumference() {
		return chestCircumference;
	}

	public void setChestCircumference(Double chestCircumference) {
		this.chestCircumference = chestCircumference;
	}

	public Double getArmCircumference() {
		return armCircumference;
	}

	public void setArmCircumference(Double armCircumference) {
		this.armCircumference = armCircumference;
	}

	public String getComplications() {
		return complications;
	}

	public void setComplications(String complications) {
		this.complications = complications;
	}

	public String getCongenitalMalformation() {
		return congenitalMalformation;
	}

	public void setCongenitalMalformation(String congenitalMalformation) {
		this.congenitalMalformation = congenitalMalformation;
	}

	public NewbornFeedingMode getFeedingMode() {
		return feedingMode;
	}

	public void setFeedingMode(NewbornFeedingMode feedingMode) {
		this.feedingMode = feedingMode;
	}

	public boolean isHivExposed() {
		return hivExposed;
	}

	public void setHivExposed(boolean hivExposed) {
		this.hivExposed = hivExposed;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof PregnancyNewborn)) {
			return false;
		}
		return getId() == ((PregnancyNewborn) other).getId();
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
