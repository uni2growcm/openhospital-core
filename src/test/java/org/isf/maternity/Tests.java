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
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
	void testPregnancyAdvancedMethods() throws Exception {
		Patient patient = testPatient.setup(false);
		Pregnancy pregnancy = testPregnancy.setup(patient, false);
		pregnancy = pregnancyBrowserManager.newPregnancy(pregnancy);

		assertThat(pregnancyBrowserManager.hasActivePregnancy(patient.getCode())).isTrue();

		long count = pregnancyBrowserManager.countPregnanciesByPatientAndStatus(patient.getCode(), "Ongoing");
		assertThat(count).isGreaterThanOrEqualTo(1);

		Pregnancy latest = pregnancyBrowserManager.getLatestPregnancyByPatientAndStatus(patient.getCode(), "Ongoing");
		assertThat(latest).isNotNull();

		// update
		pregnancy.setRiskLevel("High");
		pregnancyBrowserManager.updatePregnancy(pregnancy);

		// search
		assertThat(
			pregnancyBrowserManager.searchPregnancies(
				patient.getCode(), null, null,
				null, null, null
			)
		).isNotNull();

		// close
		pregnancyBrowserManager.closePregnancy(pregnancy.getId(), "Completed");
	}

	// ========================= DELIVERY =========================

	@Test
	void testDeliveryFullLifecycle() throws Exception {

		Pregnancy pregnancy = testPregnancy.setup(testPatient.setup(false), false);
		pregnancy = pregnancyBrowserManager.newPregnancy(pregnancy);

		PregnancyDeliveryType type = pregnancyDeliveryTypeBrowserManager.newDeliveryType(
			testDeliveryType.setup()
		);

		PregnancyDelivery delivery = testDelivery.setup(pregnancy, type, false);

		delivery = deliveryBrowserManager.newDelivery(delivery);
		assertThat(delivery.getId()).isNotNull();

		assertThat(deliveryBrowserManager.hasDelivery(pregnancy.getId())).isTrue();

		PregnancyDelivery found = deliveryBrowserManager.getDeliveryByPregnancy(pregnancy.getId());
		assertThat(found).isNotNull();

		PregnancyDelivery validated = deliveryBrowserManager.validateDeliveryExists(pregnancy.getId());
		assertThat(validated).isNotNull();

		delivery.setFatherName("Updated Father");
		deliveryBrowserManager.updateDelivery(delivery);

		deliveryBrowserManager.deleteDelivery(delivery);
	}

	// ========================= DELIVERY TYPE =========================

	@Test
	void testDeliveryTypeAllMethods() throws Exception {

		PregnancyDeliveryType type = testDeliveryType.setup();

		type = pregnancyDeliveryTypeBrowserManager.newDeliveryType(type);

		assertThat(pregnancyDeliveryTypeBrowserManager.getDeliveryTypes()).isNotEmpty();
		assertThat(pregnancyDeliveryTypeBrowserManager.getDeliveryTypeByCode("VD")).isNotNull();
		assertThat(pregnancyDeliveryTypeBrowserManager.isCodePresent("VD")).isTrue();

		type.setDescription("Updated");
		pregnancyDeliveryTypeBrowserManager.updateDeliveryType(type);

		pregnancyDeliveryTypeBrowserManager.deleteDeliveryType(type);
	}

	@Test
	void testVisitFullFeatureSet() throws Exception {

		Pregnancy pregnancy = pregnancyBrowserManager.newPregnancy(
			testPregnancy.setup(testPatient.setup(false), false)
		);

		PregnancyVisitType vt = visitTypeBrowserManager.newVisitType(testVisitType.setup());

		PregnancyVisit visit = testVisit.setup(pregnancy, vt, false);
		visit = pregnancyVisitBrowserManager.newVisit(visit);

		assertThat(pregnancyVisitBrowserManager.getVisitsByPregnancy(pregnancy.getId())).hasSize(1);
		assertThat(pregnancyVisitBrowserManager.countVisits(pregnancy.getId())).isEqualTo(1);
		assertThat(pregnancyVisitBrowserManager.hasVisits(pregnancy.getId())).isTrue();

		assertThat(pregnancyVisitBrowserManager.getLastVisit(pregnancy.getId())).isNotNull();

		assertThat(
			pregnancyVisitBrowserManager.getVisitsByFilters(
				pregnancy.getId(), null, null, vt.getId()
			)
		).isNotNull();

		assertThat(
			pregnancyVisitBrowserManager.getPrenatalVisits(
				pregnancy.getId(), LocalDateTime.now().plusDays(10)
			)
		).isNotNull();

		assertThat(
			pregnancyVisitBrowserManager.getPostnatalVisits(
				pregnancy.getId(), LocalDateTime.now().minusDays(10)
			)
		).isNotNull();

		visit.setMaternalWeight(75.0);
		pregnancyVisitBrowserManager.updateVisit(visit);

		pregnancyVisitBrowserManager.deleteVisit(visit);
	}

	// ========================= VISIT TYPE =========================

	@Test
	void testVisitTypeAllMethods() throws Exception {

		PregnancyVisitType vt = visitTypeBrowserManager.newVisitType(testVisitType.setup());

		assertThat(visitTypeBrowserManager.getVisitTypes()).isNotEmpty();
		assertThat(visitTypeBrowserManager.getVisitTypeByCode("ANC")).isNotNull();
		assertThat(visitTypeBrowserManager.isCodePresent("ANC")).isTrue();

		vt.setDescription("Updated");
		visitTypeBrowserManager.updateVisitType(vt);

		visitTypeBrowserManager.deleteVisitType(vt);
	}

	// ========================= NEWBORN =========================

	@Test
	void testNewbornAllMethods() throws Exception {

		Pregnancy pregnancy = pregnancyBrowserManager.newPregnancy(
			testPregnancy.setup(testPatient.setup(false), false)
		);

		PregnancyDeliveryType type = pregnancyDeliveryTypeBrowserManager.newDeliveryType(
			testDeliveryType.setup()
		);

		PregnancyDelivery delivery = deliveryBrowserManager.newDelivery(
			testDelivery.setup(pregnancy, type, false)
		);

		Newborn baby = testNewBorn.setup(delivery, false);

		baby = newBornBrowserManager.newNewborn(baby);

		assertThat(newBornBrowserManager.getNewbornsByDelivery(delivery.getId())).hasSize(1);
		assertThat(newBornBrowserManager.countNewbornsByDelivery(delivery.getId())).isEqualTo(1);

		assertThat(newBornBrowserManager.getFirstBorn(delivery.getId())).isPresent();

		assertThat(
			newBornBrowserManager.getNewbornsByWeightRange(2000.0, 4000.0)
		).isNotNull();

		assertThat(
			newBornBrowserManager.hasLowBirthWeightCases(delivery.getId(), 2500.0)
		).isNotNull();

		assertThat(
			newBornBrowserManager.validateNewbornBelongsToDelivery(
				baby.getId(),
				delivery.getId()
			)
		).isNotNull();

		baby.setName("Updated Baby");
		newBornBrowserManager.updateNewborn(baby);

		newBornBrowserManager.deleteNewborn(baby);
	}

	private List<Pregnancy> setupTestPregnancies(int number, boolean samePatient) throws OHException {
		List<Pregnancy> pregnancies = new ArrayList<>();
		Patient patient = testPatient.setup(false);
		if(samePatient) {
			for (int i = 0; i <= number; i++) {
				Pregnancy pregnancy = testPregnancy.setup(patient, false);
				pregnancies.add(pregnancy);
			}
		} else {
			for (int i = 0; i <= number; i++) {
				patient.setCode(i);
				Pregnancy pregnancy = testPregnancy.setup(patient, false);
				pregnancies.add(pregnancy);
			}
		}

		return pregnancies;
	}

	private List<PregnancyVisitType> setupTestVisitTypes(int number) throws OHException {
		List<PregnancyVisitType> visitTypes = new ArrayList<>();

		for (int i = 0; i <= number; i++) {
			PregnancyVisitType visitType = testVisitType.setup();
			visitTypes.add(visitType);
		}

		return visitTypes;
	}

	private List<PregnancyDeliveryType> setupTestDeliveryTypes(int number) throws OHException {
		List<PregnancyDeliveryType> pregnancyDeliveryTypes = new ArrayList<>();

		for (int i = 0; i <= number; i++) {
			PregnancyDeliveryType pregnancyDeliveryType = testDeliveryType.setup();
			pregnancyDeliveryTypes.add(pregnancyDeliveryType);
		}

		return pregnancyDeliveryTypes;
	}

	private List<PregnancyDelivery> setupTestDelivery(int number) throws OHException {
		List<PregnancyDelivery> deliveries = new ArrayList<>();
		List<Pregnancy> pregnancies = setupTestPregnancies(number, false);

		for (int i = 0; i <= number; i++) {
			PregnancyDelivery delivery = testDelivery.setup(pregnancies.get(i), testDeliveryType.setup(), false);
			deliveries.add(delivery);
		}

		return deliveries;
	}

	private List<PregnancyVisit> setupTestVisits(int number, boolean samePregnancy) throws OHException {
		List<Pregnancy> pregnancies = setupTestPregnancies(number, true);
		List<PregnancyVisit> visits = new ArrayList<>();

		if(samePregnancy) {
			for (int i = 0; i <= number; i++) {
				PregnancyVisit visit = testVisit.setup(pregnancies.get(0), testVisitType.setup(), false);
				visits.add(visit);
			}
		} else {
			for (int i = 0; i <= number; i++) {
				PregnancyVisit visit = testVisit.setup(pregnancies.get(i), testVisitType.setup(), false);
				visits.add(visit);
			}
		}

		return visits;
	}

	private List<Newborn> setupTestNewBorn(int number, boolean sameDelivery) throws OHException {
		List<PregnancyDelivery> deliveries = setupTestDelivery(number);
		List<Newborn> newborns = new ArrayList<>();

		if(sameDelivery) {
			for (int i = 0; i <= number; i++) {
				Newborn newborn = testNewBorn.setup(deliveries.get(0), false);
				newborns.add(newborn);
			}
		} else {
			for (int i = 0; i <= number; i++) {
				Newborn newborn = testNewBorn.setup(deliveries.get(i), false);
				newborns.add(newborn);
			}
		}

		return newborns;
	}
}