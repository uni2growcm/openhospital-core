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
package org.isf.maternity;

import org.isf.OHCoreTestCase;
import org.isf.maternity.manager.*;
import org.isf.maternity.model.*;
import org.isf.maternity.service.*;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Tests extends OHCoreTestCase {

	private static TestDelivery testDelivery;
	private static TestVisit testVisit;
	private static TestDeliveryType testDeliveryType;
	private static TestVisitType testVisitType;
	private static TestNewBorn testNewBorn;

	private static TestPregnancy testPregnancy;
	private static TestPatient testPatient;

	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	PregnancyDeliveryTypeBrowserManager pregnancyDeliveryTypeBrowserManager;
	@Autowired
	PregnancyVisitTypeBrowserManager visitTypeBrowserManager;
	@Autowired
	PregnancyDeliveryBrowserManager deliveryBrowserManager;
	@Autowired
	PregnancyVisitBrowserManager pregnancyVisitBrowserManager;
	@Autowired
	PregnancyBrowserManager pregnancyBrowserManager;
	@Autowired
	NewBornBrowserManager newBornBrowserManager;

	@Autowired
	PregnancyDeliveryTypeIoOperationRepository deliveryTypeIoOperationRepository;
	@Autowired
	PregnancyVisitTypeIoOperationRepository visitTypeIoOperationRepository;
	@Autowired
	PregnancyDeliveryIoOperationRepository pregnancyDeliveryIoOperationRepository;
	@Autowired
	PregnancyVisitIoOperationRepository visitIoOperationRepository;
	@Autowired
	PregnancyIoOperationRepository pregnancyIoOperationRepository;
	@Autowired
	NewbornIoOperationRepository newbornIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testDelivery = new TestDelivery();
		testDeliveryType = new TestDeliveryType();
		testVisit = new TestVisit();
		testVisitType = new TestVisitType();
		testNewBorn = new TestNewBorn();
		testPregnancy = new TestPregnancy();
		testPatient = new TestPatient();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testPregnancyCRUD() throws Exception {

		// CREATE PATIENT
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.save(patient);

		// CREATE PREGNANCY
		Pregnancy pregnancy = testPregnancy.setup(patient, false);
		pregnancy = pregnancyBrowserManager.newPregnancy(pregnancy);

		assertThat(pregnancy).isNotNull();
		assertThat(pregnancy.getId()).isNotNull();

		// READ BY PATIENT
		List<Pregnancy> list =
			pregnancyBrowserManager.getPregnanciesByPatient(patient.getCode());

		assertThat(list).isNotEmpty();
		assertThat(list).hasSize(1);

		// UPDATE
		Pregnancy updated = list.get(0);
		updated.setRiskLevel(RiskLevel.HIGH);
		updated = pregnancyBrowserManager.updatePregnancy(updated);

		assertThat(updated.getRiskLevel()).isEqualTo(RiskLevel.HIGH);

		// CHECK ACTIVE PREGNANCY
		assertThat(
			pregnancyBrowserManager.hasActivePregnancy(patient.getCode())
		).isTrue();

		// COUNT BY STATUS (assuming default status like "ONGOING")
		long count =
			pregnancyBrowserManager.countPregnanciesByPatientAndStatus(
				patient.getCode(),
				updated.getStatus()
			);

		assertThat(count).isGreaterThan(0);

		// GET LATEST
		Pregnancy latest =
			pregnancyBrowserManager.getLatestPregnancyByPatientAndStatus(
				patient.getCode(),
				updated.getStatus()
			);

		assertThat(latest).isNotNull();
		assertThat(latest.getId()).isEqualTo(updated.getId());

		// CLOSE PREGNANCY
		Pregnancy closed =
			pregnancyBrowserManager.closePregnancy(updated.getId(), PregnancyStatus.COMPLETED);

		assertThat(closed.getStatus()).isEqualTo(PregnancyStatus.COMPLETED);

		// AFTER CLOSE → NO ACTIVE PREGNANCY
		assertThat(
			pregnancyBrowserManager.hasActivePregnancy(patient.getCode())
		).isFalse();

		// DELETE
		pregnancyBrowserManager.deletePregnancy(closed);

		// VERIFY DELETE
		List<Pregnancy> afterDelete =
			pregnancyBrowserManager.getPregnanciesByPatient(patient.getCode());

		assertThat(afterDelete).isEmpty();
	}

	@Test
	void testPregnancyDeliveryBrowserManagerFullFlow() throws Exception {

		// ARRANGE
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.save(patient);

		Pregnancy pregnancy = testPregnancy.setup(patient, false);
		pregnancy = pregnancyBrowserManager.newPregnancy(pregnancy);

		PregnancyDeliveryType type = testDeliveryType.setup();
		type = pregnancyDeliveryTypeBrowserManager.newDeliveryType(type);

		PregnancyDelivery delivery = testDelivery.setup(pregnancy, type, false);

		// CREATE
		PregnancyDelivery created = deliveryBrowserManager.newDelivery(delivery);
		assertThat(created.getId()).isNotNull();

		// GET BY PREGNANCY
		PregnancyDelivery found =
			deliveryBrowserManager.getDeliveryByPregnancy(pregnancy.getId());

		assertThat(found).isNotNull();
		assertThat(found.getId()).isEqualTo(created.getId());

		// HAS DELIVERY (boolean ✔)
		assertThat(deliveryBrowserManager.hasDelivery(pregnancy.getId())).isTrue();

		// VALIDATE DELIVERY EXISTS (returns entity ✔)
		PregnancyDelivery validated =
			deliveryBrowserManager.validateDeliveryExists(pregnancy.getId());

		assertThat(validated).isNotNull();
		assertThat(validated.getId()).isEqualTo(created.getId());

		// UPDATE
		validated.setFatherName("Updated Father");
		PregnancyDelivery updated =
			deliveryBrowserManager.updateDelivery(validated);

		assertThat(updated.getFatherName()).isEqualTo("Updated Father");

		// DELETE
		deliveryBrowserManager.deleteDelivery(updated);

		// AFTER DELETE → should NOT exist
		assertThat(deliveryBrowserManager.hasDelivery(pregnancy.getId())).isFalse();

		PregnancyDelivery afterDelete =
			deliveryBrowserManager.getDeliveryByPregnancy(pregnancy.getId());

		assertThat(afterDelete).isNull();
	}

	@Test
	void testPregnancyVisitBrowserManagerFullFlow() throws Exception {

		// ARRANGE
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.save(patient);

		Pregnancy pregnancy = testPregnancy.setup(patient, false);
		pregnancy = pregnancyBrowserManager.newPregnancy(pregnancy);

		PregnancyVisitType vt = testVisitType.setup();
		vt = visitTypeBrowserManager.newVisitType(vt);

		PregnancyVisit visit = testVisit.setup(pregnancy, vt, false);

		// CREATE
		PregnancyVisit saved = pregnancyVisitBrowserManager.newVisit(visit);
		assertThat(saved.getId()).isNotNull();

		// GET ALL VISITS
		List<PregnancyVisit> visits =
			pregnancyVisitBrowserManager.getVisitsByPregnancy(pregnancy.getId());

		assertThat(visits).hasSize(1);

		// COUNT VISITS
		long count = pregnancyVisitBrowserManager.countVisits(pregnancy.getId());
		assertThat(count).isEqualTo(1);

		// HAS VISITS
		assertThat(pregnancyVisitBrowserManager.hasVisits(pregnancy.getId()))
			.isTrue();

		// GET LAST VISIT
		PregnancyVisit last =
			pregnancyVisitBrowserManager.getLastVisit(pregnancy.getId());

		assertThat(last).isNotNull();
		assertThat(last.getId()).isEqualTo(saved.getId());

		// UPDATE
		last.setMaternalWeight(72.5);
		PregnancyVisit updated =
			pregnancyVisitBrowserManager.updateVisit(last);

		assertThat(updated.getMaternalWeight()).isEqualTo(72.5);

		// DATE RANGE QUERY
		List<PregnancyVisit> range =
			pregnancyVisitBrowserManager.getVisitsByDateRange(
				pregnancy.getId(),
				updated.getVisitDate().minusDays(1),
				updated.getVisitDate().plusDays(1)
			);

		assertThat(range).isNotEmpty();

		// FILTER QUERY (visitType)
		List<PregnancyVisit> filtered =
			pregnancyVisitBrowserManager.getVisitsByFilters(
				pregnancy.getId(),
				updated.getVisitDate().minusDays(1),
				updated.getVisitDate().plusDays(1),
				vt.getCode()
			);

		assertThat(filtered).isNotEmpty();

		// PRENATAL VISITS
		List<PregnancyVisit> prenatal =
			pregnancyVisitBrowserManager.getPrenatalVisits(
				pregnancy.getId(),
				updated.getVisitDate().plusDays(10)
			);

		assertThat(prenatal).isNotEmpty();

		// POSTNATAL VISITS
		List<PregnancyVisit> postnatal =
			pregnancyVisitBrowserManager.getPostnatalVisits(
				pregnancy.getId(),
				updated.getVisitDate().minusDays(10)
			);

		assertThat(postnatal).isNotEmpty();

		// DELETE
		pregnancyVisitBrowserManager.deleteVisit(updated);

		// AFTER DELETE → should be empty
		assertThat(pregnancyVisitBrowserManager.hasVisits(pregnancy.getId()))
			.isFalse();

		assertThat(pregnancyVisitBrowserManager.countVisits(pregnancy.getId()))
			.isEqualTo(0);
	}

	@Test
	void testNewbornBrowserManagerFullFlow() throws Exception {

		// ARRANGE
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.save(patient);

		Pregnancy pregnancy = testPregnancy.setup(patient, false);
		pregnancy = pregnancyBrowserManager.newPregnancy(pregnancy);

		PregnancyDeliveryType type = testDeliveryType.setup();
		type = pregnancyDeliveryTypeBrowserManager.newDeliveryType(type);

		PregnancyDelivery delivery = testDelivery.setup(pregnancy, type, false);
		delivery = deliveryBrowserManager.newDelivery(delivery);

		Patient newbornPatient = testPatient.setup(false);
		newbornPatient.setFirstName("first name");
		newbornPatient.setSecondName("second name");
		newbornPatient.setMotherName(patient.getMotherName());

		Patient savedNewbornPatient = patientIoOperationRepository.save(newbornPatient);

		Newborn newborn = testNewBorn.setup(savedNewbornPatient, delivery, false);

		// CREATE
		Newborn saved = newBornBrowserManager.newNewborn(newborn);
		assertThat(saved.getId()).isNotNull();

		// GET BY DELIVERY
		List<Newborn> list =
			newBornBrowserManager.getNewbornsByDelivery(delivery.getId());

		assertThat(list).hasSize(1);

		assertThat(newborn.getBabyPatient()).isEqualTo(savedNewbornPatient);

		// COUNT
		long count =
			newBornBrowserManager.countNewbornsByDelivery(delivery.getId());

		assertThat(count).isEqualTo(1);

		// UPDATE
		Newborn toUpdate = list.get(0);
		toUpdate.getBabyPatient().setMotherName(patient.getMotherName() + "mum");

		Newborn updated =
			newBornBrowserManager.updateNewborn(toUpdate);

		assertThat(newborn.getBabyPatient().getMotherName()).isEqualTo(patient.getMotherName() + "mum");

		// FIRST BORN (Optional result)
		assertThat(
			newBornBrowserManager.getFirstBorn(delivery.getId())
		).isPresent();

		// FIRST BORN (Optional result)
		assertThat(
			newBornBrowserManager.findByPatientCode(savedNewbornPatient.getCode())
		).isPresent();

		// WEIGHT RANGE QUERY
		List<Newborn> weightRange =
			newBornBrowserManager.getNewbornsByWeightRange(2.0, 5.0);

		assertThat(weightRange).isNotNull();

		// LOW BIRTH WEIGHT CHECK
		boolean lowWeight =
			newBornBrowserManager.hasLowBirthWeightCases(delivery.getId(), 2.5);

		assertThat(lowWeight).isNotNull();

		// VALIDATION: belongs to delivery
		Newborn validated =
			newBornBrowserManager.validateNewbornBelongsToDelivery(
				updated.getId(),
				delivery.getId()
			);

		assertThat(validated).isNotNull();
		assertThat(validated.getId()).isEqualTo(updated.getId());

		// DELETE
		newBornBrowserManager.deleteNewborn(updated);

		// AFTER DELETE
		assertThat(
			newBornBrowserManager.countNewbornsByDelivery(delivery.getId())
		).isEqualTo(0);
	}

	@Test
	void testDeliveryTypeCRUD() throws Exception {

		// CREATE
		PregnancyDeliveryType dt = testDeliveryType.setup();
		dt = pregnancyDeliveryTypeBrowserManager.newDeliveryType(dt);

		assertThat(dt).isNotNull();
		assertThat(dt.getCode()).isNotNull();

		// READ by code
		PregnancyDeliveryType found =
			pregnancyDeliveryTypeBrowserManager.getDeliveryTypeByCode(dt.getCode());

		assertThat(found).isNotNull();
		assertThat(found.getCode()).isEqualTo(dt.getCode());

		// UPDATE
		dt.setDescription("Updated");
		dt = pregnancyDeliveryTypeBrowserManager.updateDeliveryType(dt);

		assertThat(dt.getDescription()).isEqualTo("Updated");

		// EXISTS CHECK
		assertThat(
			pregnancyDeliveryTypeBrowserManager.isCodePresent(dt.getCode())
		).isTrue();

		// READ ALL (optional but good coverage)
		assertThat(
			pregnancyDeliveryTypeBrowserManager.getDeliveryTypes()
		).isNotEmpty();

		// DELETE
		pregnancyDeliveryTypeBrowserManager.deleteDeliveryType(dt);

		// VERIFY DELETION
		assertThat(
			pregnancyDeliveryTypeBrowserManager.isCodePresent(dt.getCode())
		).isFalse();

		assertThat(
			pregnancyDeliveryTypeBrowserManager.getDeliveryTypeByCode(dt.getCode())
		).isNull();
	}

	@Test
	void testVisitTypeCRUD() throws Exception {

		// CREATE
		PregnancyVisitType vt = testVisitType.setup();
		vt = visitTypeBrowserManager.newVisitType(vt);

		assertThat(vt).isNotNull();
		assertThat(vt.getCode()).isNotNull();

		// READ by code
		PregnancyVisitType found =
			visitTypeBrowserManager.getVisitTypeByCode(vt.getCode());

		assertThat(found).isNotNull();
		assertThat(found.getCode()).isEqualTo(vt.getCode());

		// UPDATE
		vt.setDescription("Updated");
		vt = visitTypeBrowserManager.updateVisitType(vt);

		assertThat(vt.getDescription()).isEqualTo("Updated");

		// EXISTS CHECK
		assertThat(
			visitTypeBrowserManager.isCodePresent(vt.getCode())
		).isTrue();

		// GET ALL (extra coverage)
		assertThat(
			visitTypeBrowserManager.getVisitTypes()
		).isNotEmpty();

		// DELETE
		visitTypeBrowserManager.deleteVisitType(vt);

		// VERIFY DELETE
		assertThat(
			visitTypeBrowserManager.isCodePresent(vt.getCode())
		).isFalse();

		assertThat(
			visitTypeBrowserManager.getVisitTypeByCode(vt.getCode())
		).isNull();
	}
}