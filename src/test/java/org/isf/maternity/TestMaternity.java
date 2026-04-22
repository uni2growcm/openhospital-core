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
package org.isf.maternity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.isf.maternity.model.*;
import org.isf.patient.model.Patient;

/**
 * Test class builder for Maternity models
 */
public class TestMaternity {

	private LocalDate lmp = LocalDate.of(2025, 1, 15);
	private LocalDate eddLmp = LocalDate.of(2025, 10, 22);
	private LocalDate eddScan = LocalDate.of(2025, 10, 20);
	private Integer gravidity = 1;
	private Integer parity = 0;
	private Integer abortions = 0;
	private String bloodGroup = "O+";
	private String riskLevel = "Low";
	private String status = "Ongoing";

	private LocalDateTime visitDate = LocalDateTime.of(2025, 3, 15, 10, 30, 0);
	private Integer gestationalWeeks = 8;
	private Integer gestationalDays = 3;
	private Double maternalWeight = 65.5;
	private Integer systolicBP = 120;
	private Integer diastolicBP = 80;
	private Double temperature = 37.2;
	private Double fundalHeight = 18.0;
	private Double abdominalCircumference = 75.0;
	private Integer fetalHeartRate = 155;
	private String fetalPresentation = "Cephalic";
	private String clinicalNotes = "Pregnancy progressing well";
	private String visitType = "ANC"; // Antenatal Care

	private String treatmentName = "Iron Supplement";
	private String dosage = "1 tablet twice daily";
	private String route = "Oral";
	private Integer durationDays = 90;
	private String indication = "Prevention of anemia";

	private LocalDateTime laborOnset = LocalDateTime.of(2025, 10, 19, 8, 0, 0);
	private LocalDateTime ruptureMembranes = LocalDateTime.of(2025, 10, 19, 9, 30, 0);
	private LocalDateTime deliveryDateTime = LocalDateTime.of(2025, 10, 19, 12, 15, 0);
	private String modeOfDelivery = "SVD";
	private String deliveryType = "Vaginal"; // SVD, Vacuum, Forceps, C-Section
	private String anesthesiaUsed = "None";
	private String perinealIntegrity = "Intact";
	private Boolean placentaComplete = true;
	private Integer placentaWeight = 500;
	private Integer estimatedBloodLoss = 300;
	private String fatherName = "John Doe";
	private Integer fatherAge = 35;

	private Integer birthWeight = 3200;
	private Double birthLength = 50.0;
	private Double headCircumference = 35.0;
	private Integer apgarScore1Min = 9;
	private Integer apgarScore5Min = 10;
	private String birthOrder = "Singleton";
	private String gender = "M";
	private String cryTime = "Immediate";
	private String newbornName = "Baby Doe";
	private String neonatalStatus = "LIVE";
	private String hivStatus = "N"; // N = Negative, P = Positive, U = Unknown

	public Pregnancy setupPregnancy(Patient patient, boolean usingSet) {
		Pregnancy pregnancy;

		if (usingSet) {
			pregnancy = new Pregnancy();
			pregnancy.setPatient(patient);
			pregnancy.setLmp(lmp);
			pregnancy.setEddLmp(eddLmp);
			pregnancy.setEddScan(eddScan);
			pregnancy.setGravidity(gravidity);
			pregnancy.setParity(parity);
			pregnancy.setMiscarriages(abortions);
			pregnancy.setBloodGroup(bloodGroup);
			pregnancy.setRiskLevel(riskLevel);
			pregnancy.setStatus(status);
		} else {
			pregnancy = new Pregnancy(patient, status);
			pregnancy.setLmp(lmp);
			pregnancy.setEddLmp(eddLmp);
			pregnancy.setEddScan(eddScan);
			pregnancy.setGravidity(gravidity);
			pregnancy.setParity(parity);
			pregnancy.setMiscarriages(abortions);
			pregnancy.setBloodGroup(bloodGroup);
			pregnancy.setRiskLevel(riskLevel);
		}

		return pregnancy;
	}

	public Visit setupPregnancyVisit(Pregnancy pregnancy, boolean usingSet) {
		Visit visit;

		if (usingSet) {
			visit = new Visit();
			visit.setPregnancy(pregnancy);
			visit.setVisitDate(visitDate);
			visit.setGestationalWeeks(gestationalWeeks);
			visit.setGestationalDays(gestationalDays);
			visit.setMaternalWeight(maternalWeight);
			visit.setSystolicBP(systolicBP);
			visit.setDiastolicBP(diastolicBP);
			visit.setTemperature(temperature);
			visit.setFundalHeight(fundalHeight);
			visit.setAbdominalCircumference(abdominalCircumference);
			visit.setFetalHeartRate(fetalHeartRate);
			visit.setFetalPresentation(fetalPresentation);
			visit.setClinicalNotes(clinicalNotes);
			visit.setVisitType(visitType);
		} else {
			visit = new Visit(pregnancy, visitDate);
			visit.setGestationalWeeks(gestationalWeeks);
			visit.setGestationalDays(gestationalDays);
			visit.setMaternalWeight(maternalWeight);
			visit.setSystolicBP(systolicBP);
			visit.setDiastolicBP(diastolicBP);
			visit.setTemperature(temperature);
			visit.setFundalHeight(fundalHeight);
			visit.setAbdominalCircumference(abdominalCircumference);
			visit.setFetalHeartRate(fetalHeartRate);
			visit.setFetalPresentation(fetalPresentation);
			visit.setClinicalNotes(clinicalNotes);
			visit.setVisitType(visitType);
		}

		return visit;
	}

	public DeliveryType setupPregnancyTreatment(Pregnancy pregnancy, Visit visit, boolean usingSet) {
		DeliveryType treatment;

		if (usingSet) {
			treatment = new DeliveryType();
			treatment.setPregnancy(pregnancy);
			treatment.setPregnancyVisit(visit);
			treatment.setTreatmentName(treatmentName);
			treatment.setDosage(dosage);
			treatment.setRoute(route);
			treatment.setDurationDays(durationDays);
			treatment.setIndication(indication);
			treatment.setStartDate(LocalDate.now());
			treatment.setEndDate(LocalDate.now().plusDays(durationDays));
		} else {
			treatment = new DeliveryType(visit, pregnancy, treatmentName);
			treatment.setDosage(dosage);
			treatment.setRoute(route);
			treatment.setDurationDays(durationDays);
			treatment.setIndication(indication);
			treatment.setStartDate(LocalDate.now());
			treatment.setEndDate(LocalDate.now().plusDays(durationDays));
		}

		return treatment;
	}

	public Delivery setupDelivery(Patient patient, Pregnancy pregnancy, boolean usingSet) {
		Delivery delivery;

		if (usingSet) {
			delivery = new Delivery();
			delivery.setPregnancy(pregnancy);
			delivery.setLaborOnsetDateTime(laborOnset);
			delivery.setRuptureMembranesDateTime(ruptureMembranes);
			delivery.setDeliveryDateTime(deliveryDateTime);
			delivery.setModeOfDelivery(modeOfDelivery);
			delivery.setAnesthesiaUsed(anesthesiaUsed);
			delivery.setPerinealIntegrity(perinealIntegrity);
			delivery.setPlacentaComplete(placentaComplete);
			delivery.setPlacentaWeight(placentaWeight);
			delivery.setEstimatedBloodLoss(estimatedBloodLoss);
			delivery.setFatherName(fatherName);
			delivery.setFatherAge(fatherAge);
		} else {
			delivery = new Delivery(pregnancy, deliveryDateTime);
			delivery.setLaborOnsetDateTime(laborOnset);
			delivery.setRuptureMembranesDateTime(ruptureMembranes);
			delivery.setModeOfDelivery(modeOfDelivery);
			delivery.setAnesthesiaUsed(anesthesiaUsed);
			delivery.setPerinealIntegrity(perinealIntegrity);
			delivery.setPlacentaComplete(placentaComplete);
			delivery.setPlacentaWeight(placentaWeight);
			delivery.setEstimatedBloodLoss(estimatedBloodLoss);
			delivery.setFatherName(fatherName);
			delivery.setFatherAge(fatherAge);
		}

		return delivery;
	}

	public Delivery setupDelivery(Pregnancy pregnancy, boolean usingSet) {
		return setupDelivery(null, pregnancy, usingSet);
	}

	public Newborn setupNewborn(Delivery delivery, boolean usingSet) {
		Newborn newborn;

		if (usingSet) {
			newborn = new Newborn();
			newborn.setDelivery(delivery);
			newborn.setName(newbornName);
			newborn.setNeonatalStatus(neonatalStatus);
			newborn.setHivStatus(hivStatus);
			newborn.setBirthOrder(birthOrder);
			newborn.setGender(gender);
			newborn.setBirthWeight(birthWeight);
			newborn.setBirthLength(birthLength);
			newborn.setHeadCircumference(headCircumference);
			newborn.setApgarScore1Min(apgarScore1Min);
			newborn.setApgarScore5Min(apgarScore5Min);
			newborn.setCryTime(cryTime);
		} else {
			newborn = new Newborn(delivery, gender);
			newborn.setName(newbornName);
			newborn.setNeonatalStatus(neonatalStatus);
			newborn.setHivStatus(hivStatus);
			newborn.setBirthOrder(birthOrder);
			newborn.setBirthWeight(birthWeight);
			newborn.setBirthLength(birthLength);
			newborn.setHeadCircumference(headCircumference);
			newborn.setApgarScore1Min(apgarScore1Min);
			newborn.setApgarScore5Min(apgarScore5Min);
			newborn.setCryTime(cryTime);
		}

		return newborn;
	}

	public void checkPregnancy(Pregnancy pregnancy) {
		assert pregnancy.getStatus().equals(status) : "Status mismatch";
		assert pregnancy.getRiskLevel().equals(riskLevel) : "Risk level mismatch";
		assert pregnancy.getLmp().equals(lmp) : "LMP mismatch";
	}

	public void checkPregnancyVisit(Visit visit) {
		assert visit.getGestationalWeeks().equals(gestationalWeeks) : "Gestational weeks mismatch";
		assert visit.getMaternalWeight().equals(maternalWeight) : "Maternal weight mismatch";
		assert visit.getSystolicBP().equals(systolicBP) : "Systolic BP mismatch";
		assert visit.getVisitType().equals(visitType) : "Visit type mismatch";
	}

	public void checkPregnancyTreatment(DeliveryType treatment) {
		assert treatment.getTreatmentName().equals(treatmentName) : "Treatment name mismatch";
		assert treatment.getDosage().equals(dosage) : "Dosage mismatch";
		assert treatment.getRoute().equals(route) : "Route mismatch";
	}

	public void checkDelivery(Delivery delivery) {
		assert delivery.getModeOfDelivery().equals(modeOfDelivery) : "Mode of delivery mismatch";
		assert delivery.getFatherName().equals(fatherName) : "Father name mismatch";
		assert delivery.getEstimatedBloodLoss().equals(estimatedBloodLoss) : "Blood loss mismatch";
	}

	public void checkNewborn(Newborn newborn) {
		assert newborn.getName().equals(newbornName) : "Newborn name mismatch";
		assert newborn.getGender().equals(gender) : "Gender mismatch";
		assert newborn.getBirthWeight().equals(birthWeight) : "Birth weight mismatch";
		assert newborn.getHivStatus().equals(hivStatus) : "HIV status mismatch";
		assert newborn.getApgarScore5Min().equals(apgarScore5Min) : "APGAR 5 min score mismatch";
	}
}
