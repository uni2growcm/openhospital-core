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
package org.isf.partner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.EntityManager;

import org.isf.OHCoreTestCase;
import org.isf.partner.manager.PatientPartnerBrowserManager;
import org.isf.partner.model.PatientPartner;
import org.isf.partner.model.Partner;
import org.isf.partner.service.PartnerIoOperationRepository;
import org.isf.partner.service.PatientPartnerIoOperationRepository;
import org.isf.partner.service.PatientPartnerIoOperations;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.typology.model.Family;
import org.isf.typology.model.Typology;
import org.isf.typology.service.TypologyIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PatientPartnerTests extends OHCoreTestCase {

	private static TestPartner testPartner;
	private static TestPatientPartner testPatientPartner;
	private static Typology partnerType;
	private static Partner testPartnerEntity;
	private static Patient testPatient;

	@Autowired
	PatientPartnerIoOperations patientPartnerIoOperations;

	@Autowired
	PatientPartnerIoOperationRepository patientPartnerIoOperationRepository;

	@Autowired
	PatientPartnerBrowserManager patientPartnerBrowserManager;

	@Autowired
	TypologyIoOperationRepository typologyIoOperationRepository;

	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;

	@Autowired
	PartnerIoOperationRepository partnerIoOperationRepository;

	@Autowired
	private EntityManager entityManager;

	@BeforeAll
	static void setUpClass() {
		testPartner = new TestPartner();
		testPatientPartner = new TestPatientPartner();

		partnerType = new Typology();
		partnerType.setCode("PRT_TEST");
		partnerType.setDescription("Test Partner Type");
		partnerType.setFamily(Family.PARTNERTYPE);
		partnerType.setActive(1);
	}

	@BeforeEach
	void setUp() throws OHException {
		cleanH2InMemoryDb();

		typologyIoOperationRepository.saveAndFlush(partnerType);

		testPartnerEntity = testPartner.setup(false, partnerType);
		partnerIoOperationRepository.saveAndFlush(testPartnerEntity);

		testPatient = new Patient();
		testPatient.setFirstName("John");
		testPatient.setSecondName("Dilan");
		testPatient.setBirthDate(LocalDate.of(1990, 1, 1));
		patientIoOperationRepository.saveAndFlush(testPatient);
	}

	// ============================================
	// MODEL TESTS
	// ============================================

	@Test
	void testPatientPartnerGets() throws Exception {
		int id = setupTestPatientPartner(false);
		checkPatientPartnerIntoDb(id);
	}

	@Test
	void testPatientPartnerSets() throws Exception {
		int id = setupTestPatientPartner(true);
		checkPatientPartnerIntoDb(id);
	}

	// ============================================
	// IO (SERVICE) TESTS
	// ============================================

	@Test
	void testIoGetActiveByPatient() throws Exception {
		setupTestPatientPartner(false);

		List<PatientPartner> result = patientPartnerIoOperations.getActiveByPatient(testPatient);

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getPartner().getName()).isEqualTo("Test Partner");
	}

	@Test
	void testIoGetActiveByPatient_shouldNotReturnEnded() throws Exception {
		int id = setupTestPatientPartner(false);

		patientPartnerIoOperations.endPartnership(id, LocalDate.now());
		entityManager.flush();
		entityManager.clear();

		List<PatientPartner> result = patientPartnerIoOperations.getActiveByPatient(testPatient);

		assertThat(result).isEmpty();
	}

	@Test
	void testIoGetByPatient() throws Exception {
		setupTestPatientPartner(false);

		List<PatientPartner> result = patientPartnerIoOperations.getByPatient(testPatient);

		assertThat(result).hasSize(1);
	}

	@Test
	void testIoGetByPartner() throws Exception {
		setupTestPatientPartner(false);

		List<PatientPartner> result = patientPartnerIoOperations.getByPartner(testPartnerEntity);

		assertThat(result).hasSize(1);
	}

	@Test
	void testIoSavePatientPartner() throws Exception {
		PatientPartner patientPartner = testPatientPartner.setup(testPatient, testPartnerEntity, true);

		PatientPartner saved = patientPartnerIoOperations.save(patientPartner);

		assertThat(saved.getId()).isPositive();
		assertThat(saved.getPatient().getCode()).isEqualTo(testPatient.getCode());
		assertThat(saved.getPartner().getId()).isEqualTo(testPartnerEntity.getId());
	}

	@Test
	void testIoEndPartnership() throws Exception {
		int id = setupTestPatientPartner(false);

		List<PatientPartner> active = patientPartnerIoOperations.getActiveByPatient(testPatient);
		assertThat(active).hasSize(1);

		patientPartnerIoOperations.endPartnership(id, LocalDate.now());
		entityManager.flush();
		entityManager.clear();

		active = patientPartnerIoOperations.getActiveByPatient(testPatient);
		assertThat(active).isEmpty();

		java.util.Optional<PatientPartner> ended = patientPartnerIoOperationRepository.findById(id);
		assertThat(ended).isPresent();
		assertThat(ended.get().getActive()).isEqualTo(0);
		assertThat(ended.get().getEndDate()).isNotNull();
	}

	// ============================================
	// MANAGER TESTS
	// ============================================

	@Test
	void testMgrGetPatientPartners() throws Exception {
		setupTestPatientPartner(false);

		List<PatientPartner> result = patientPartnerBrowserManager.getPatientPartners(testPatient);

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getPartner().getName()).isEqualTo("Test Partner");
	}

	@Test
	void testMgrAssociatePatientToPartner() throws Exception {
		LocalDate startDate = LocalDate.now();
		LocalDate endDate = LocalDate.now().plusDays(1);
		PatientPartner result = patientPartnerBrowserManager.associatePatientToPartner(
			testPatient, testPartnerEntity, startDate, endDate);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isPositive();
		assertThat(result.getActive()).isEqualTo(1);
		assertThat(result.getStartDate()).isEqualTo(startDate);
	}

	@Test
	void testMgrAssociatePatientToPartner_shouldNotAllowDuplicateActive() throws Exception {
		patientPartnerBrowserManager.associatePatientToPartner(
			testPatient,
			testPartnerEntity,
			LocalDate.now().minusMonths(1),
			null);

		List<PatientPartner> partners = patientPartnerBrowserManager.getPatientPartners(testPatient);
		assertThat(partners).hasSize(1);

		assertThatThrownBy(() ->
			patientPartnerBrowserManager.associatePatientToPartner(
				testPatient,
				testPartnerEntity,
				LocalDate.now(),
				null)
		).isInstanceOf(OHDataValidationException.class);

		partners = patientPartnerBrowserManager.getPatientPartners(testPatient);
		assertThat(partners).hasSize(1);
	}

	@Test
	void testMgrEndAssociation() throws Exception {
		int id = setupTestPatientPartner(false);

		List<PatientPartner> active = patientPartnerBrowserManager.getPatientPartners(testPatient);
		assertThat(active).hasSize(1);

		patientPartnerBrowserManager.endAssociation(id, LocalDate.now());
		entityManager.flush();
		entityManager.clear();

		active = patientPartnerBrowserManager.getPatientPartners(testPatient);
		assertThat(active).isEmpty();
	}

	// ============================================
	// HELPERS
	// ============================================

	private int setupTestPatientPartner(boolean usingSet) throws OHException {
		PatientPartner patientPartner = testPatientPartner.setup(testPatient, testPartnerEntity, usingSet);
		patientPartnerIoOperationRepository.saveAndFlush(patientPartner);
		return patientPartner.getId();
	}

	private void checkPatientPartnerIntoDb(int id) throws OHException {
		PatientPartner found = patientPartnerIoOperationRepository.findById(id).orElse(null);
		assertThat(found).isNotNull();
		testPatientPartner.check(found);
	}
}