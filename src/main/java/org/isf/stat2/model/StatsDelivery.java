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

import org.isf.patient.model.Patient;
import org.isf.maternity.model.PregnancyDelivery;
import org.isf.maternity.model.Newborn;

public class StatsDelivery {

	private Patient mother;
	private PregnancyDelivery delivery;
	private Newborn newborn;

	public StatsDelivery() {
		super();
	}

	public StatsDelivery(Patient mother, PregnancyDelivery delivery, Newborn newborn) {
		this.mother = mother;
		this.delivery = delivery;
		this.newborn = newborn;
	}

	public StatsDelivery(Patient patient, PregnancyDelivery delivery) {
		this.patient = patient;
		this.delivery = delivery;
	}

	public Patient getMother() {
		return mother;
	}

	public void setMother(Patient mother) {
		this.mother = mother;

	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public PregnancyDelivery getDelivery() {
		return delivery;
	}

	public void setDelivery(PregnancyDelivery delivery) {
		this.delivery = delivery;
	}

	public Newborn getNewborn() {
		return newborn;
	}

	public void setNewborn(Newborn newborn) {
		this.newborn = newborn;
	}

	public String getMotherName() {
		return mother != null ? mother.getName() : "";
	}

	public Integer getMotherAge() {
		return mother != null ? mother.getAge() : 0;
	}

	public String getNewbornSex() {
		if (newborn != null && newborn.getBabyPatient() != null) {
			char sex = newborn.getBabyPatient().getSex();
			return sex == 'M' ? "Masculin" : sex == 'F' ? "Féminin" : "";
		}
		return "";
	}

	public Double getNewbornWeight() {
		return newborn != null ? newborn.getBirthWeight() : 0.0;
	}

	public String getNeonatalStatus() {
		return newborn != null && newborn.getNeonatalStatus() != null ?
			newborn.getNeonatalStatus().toString() : "";
	}

	public Integer getApgar1() {
		return newborn != null ? newborn.getApgarScore1Min() : 0;
	}

	public Integer getApgar5() {
		return newborn != null ? newborn.getApgarScore5Min() : 0;
	}

	public String getDeliveryMode() {
		return delivery != null && delivery.getDeliveryMode() != null ?
			delivery.getDeliveryMode().toString() : "";
	}

	public LocalDateTime getDeliveryDate() {
		return delivery != null ? delivery.getDeliveryDate() : null;
	}

	public String getDeliveryType() {
		return delivery != null && delivery.getDeliveryType() != null ?
			delivery.getDeliveryType().getDescription() : "";
	}

	public String getPerinealIntegrity() {
		return delivery != null && delivery.getPerinealIntegrity() != null ?
			delivery.getPerinealIntegrity().toString() : "";
	}

	public Boolean getPlacentaComplete() {
		return delivery != null ? delivery.isPlacentaComplete() : null;
	}

	public Integer getBloodLoss() {
		return delivery != null ? delivery.getEstimatedBloodLoss() : 0;
	}

	public Boolean getResuscitationRequired() {
		return newborn != null ? newborn.getResuscitationRequired() : null;
	}

	public String getCryTime() {
		return newborn != null && newborn.getCryTime() != null ?
			newborn.getCryTime().toString() : "";
	}

	public String getHivStatus() {
		return newborn != null && newborn.getHivStatus() != null ?
			newborn.getHivStatus().toString() : "";
	}

	public String getCongenitalAnomalies() {
		return newborn != null ? newborn.getCongenitalAnomalies() : "";
	}

}


// import org.isf.patient.model.Patient;
// import org.isf.maternity.model.PregnancyDelivery;
// import org.isf.maternity.model.Newborn;

// public class StatsDelivery {

// 	private Patient patient;
// 	private PregnancyDelivery delivery;
// 	private Newborn newborn;

// 	public StatsDelivery() {
// 		super();
// 	}

// 	public StatsDelivery(Patient mother, PregnancyDelivery delivery, Newborn newborn) {
// 		this.mother = mother;
// 		this.delivery = delivery;
// 		this.newborn = newborn;
// 	}

// 	public StatsDelivery(Patient patient, PregnancyDelivery delivery) {
// 		this.patient = patient;
// 		this.delivery = delivery;
// 	}

// 	public Patient getPatient() {
// 		return patient;
// 	}

// 	public void setPatient(Patient patient) {
// 		this.patient = patient;
// 	}

// 	public PregnancyDelivery getDelivery() {
// 		return delivery;
// 	}

// 	public void setDelivery(PregnancyDelivery delivery) {
// 		this.delivery = delivery;
// 	}

// 	public Newborn getNewborn() {
// 		return newborn;
// 	}

// 	public void setNewborn(Newborn newborn) {
// 		this.newborn = newborn;
// 	}

// 	public String getMotherName() {
// 		return mother != null ? mother.getName() : "";
// 	}

// 	public Integer getMotherAge() {
// 		return mother != null ? mother.getAge() : 0;
// 	}

// 	public String getNewbornSex() {
// 		if (newborn != null && newborn.getBabyPatient() != null) {
// 			char sex = newborn.getBabyPatient().getSex();
// 			return sex == 'M' ? "Masculin" : sex == 'F' ? "Féminin" : "";
// 		}
// 		return "";
// 	}

// 	public Double getNewbornWeight() {
// 		return newborn != null ? newborn.getBirthWeight() : 0.0;
// 	}

// 	public String getNeonatalStatus() {
// 		return newborn != null && newborn.getNeonatalStatus() != null ?
// 			newborn.getNeonatalStatus().toString() : "";
// 	}

// 	public Integer getApgar1() {
// 		return newborn != null ? newborn.getApgarScore1Min() : 0;
// 	}

// 	public Integer getApgar5() {
// 		return newborn != null ? newborn.getApgarScore5Min() : 0;
// 	}

// 	public String getDeliveryMode() {
// 		return delivery != null && delivery.getDeliveryMode() != null ?
// 			delivery.getDeliveryMode().toString() : "";
// 	}

// 	public LocalDateTime getDeliveryDate() {
// 		return delivery != null ? delivery.getDeliveryDate() : null;
// 	}

// 	public String getDeliveryType() {
// 		return delivery != null && delivery.getDeliveryType() != null ?
// 			delivery.getDeliveryType().getDescription() : "";
// 	}

// 	public String getPerinealIntegrity() {
// 		return delivery != null && delivery.getPerinealIntegrity() != null ?
// 			delivery.getPerinealIntegrity().toString() : "";
// 	}

// 	public Boolean getPlacentaComplete() {
// 		return delivery != null ? delivery.isPlacentaComplete() : null;
// 	}

// 	public Integer getBloodLoss() {
// 		return delivery != null ? delivery.getEstimatedBloodLoss() : 0;
// 	}

// 	public Boolean getResuscitationRequired() {
// 		return newborn != null ? newborn.getResuscitationRequired() : null;
// 	}

// 	public String getCryTime() {
// 		return newborn != null && newborn.getCryTime() != null ?
// 			newborn.getCryTime().toString() : "";
// 	}

// 	public String getHivStatus() {
// 		return newborn != null && newborn.getHivStatus() != null ?
// 			newborn.getHivStatus().toString() : "";
// 	}

// 	public String getCongenitalAnomalies() {
// 		return newborn != null ? newborn.getCongenitalAnomalies() : "";
// 	}

// 		public Patient getMother() {
// 		return mother;
// 	}

// 	public void setMother(Patient mother) {
// 		this.mother = mother;

// 	}
// }