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
package org.isf.stat2.model;

import java.time.LocalDateTime;

import org.isf.dlvrtype.model.DeliveryType;
import org.isf.patient.model.Patient;
import org.isf.pregnancy.model.PregnancyDelivery;
import org.isf.pregnancy.model.PregnancyNewborn;

/**
 * Delivery statistics row: a mother, her delivery record and the newborn
 * that came out of it, following the OH-538 pregnancy module schema
 * ({@link PregnancyDelivery} + {@link PregnancyNewborn}).
 */
public class StatsDelivery {

	private Patient mother;
	private PregnancyDelivery delivery;
	private PregnancyNewborn newborn;

	public StatsDelivery() {
		super();
	}

	public StatsDelivery(Patient mother, PregnancyDelivery delivery, PregnancyNewborn newborn) {
		this.mother = mother;
		this.delivery = delivery;
		this.newborn = newborn;
	}

	public Patient getMother() {
		return mother;
	}

	public void setMother(Patient mother) {
		this.mother = mother;
	}

	public PregnancyDelivery getDelivery() {
		return delivery;
	}

	public void setDelivery(PregnancyDelivery delivery) {
		this.delivery = delivery;
	}

	public PregnancyNewborn getNewborn() {
		return newborn;
	}

	public void setNewborn(PregnancyNewborn newborn) {
		this.newborn = newborn;
	}

	public String getMotherName() {
		return mother != null ? mother.getName() : "";
	}

	public Integer getMotherAge() {
		return mother != null ? mother.getAge() : 0;
	}

	public String getNewbornSex() {
		if (newborn != null) {
			char sex = newborn.getSex();
			return sex == 'M' ? "Masculin" : sex == 'F' ? "Féminin" : "";
		}
		return "";
	}

	public Float getNewbornWeight() {
		return newborn != null ? newborn.getWeight() : 0f;
	}

	public String getNewbornDeliveryType() {
		return newborn != null && newborn.getDeliveryType() != null
				? newborn.getDeliveryType().getDescription() : "";
	}

	public String getNewbornDeliveryResult() {
		return newborn != null && newborn.getDeliveryResultType() != null
				? newborn.getDeliveryResultType().getDescription() : "";
	}

	public LocalDateTime getDeliveryDate() {
		return delivery != null ? delivery.getDeliveryDate() : null;
	}

	public String getCounseling() {
		return delivery != null ? delivery.getCounseling() : "";
	}

	public String getFamilyPlanningMethodChosen() {
		return delivery != null ? delivery.getFamilyPlanningMethodChosen() : "";
	}

	public String getComplications() {
		return newborn != null ? newborn.getComplications() : "";
	}

	public String getCongenitalMalformation() {
		return newborn != null ? newborn.getCongenitalMalformation() : "";
	}

	public boolean isHivExposed() {
		return newborn != null && newborn.isHivExposed();
	}
}
