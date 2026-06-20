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
package org.isf.hiv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.hiv.manager.HIVInfantManager;
import org.isf.hiv.manager.HIVVisitManager;
import org.isf.hiv.model.HIVInfant;
import org.isf.hiv.model.HIVInfant.HIVInfantStatus;
import org.isf.hiv.model.HIVInfant.FeedingType;
import org.isf.hiv.model.HIVVisit;
import org.isf.hiv.model.HIVVisit.PCRResult;
import org.isf.hiv.service.HIVInfantIoOperationRepository;
import org.isf.hiv.service.HIVInfantIoOperations;
import org.isf.hiv.service.HIVVisitIoOperationRepository;
import org.isf.hiv.service.HIVVisitIoOperations;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class HIVTests extends OHCoreTestCase {

	private static TestHIVInfant testHIVInfant;
	private static TestHIVVisit testHIVVisit;
	private static TestPatient testPatient;

	@Autowired
	HIVInfantIoOperations hivInfantIoOperations;
	@Autowired
	HIVInfantIoOperationRepository hivInfantIoOperationRepository;
	@Autowired
	HIVVisitIoOperations hivVisitIoOperations;
	@Autowired
	HIVVisitIoOperationRepository hivVisitIoOperationRepository;
	@Autowired
	HIVInfantManager hivInfantManager;
	@Autowired
	HIVVisitManager hivVisitManager;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	@BeforeAll
	static void setUpClass() {
		testHIVInfant = new TestHIVInfant();
		testHIVVisit = new TestHIVVisit();
		testPatient = new TestPatient();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testHIVInfantCRUD() throws Exception {
		Patient patient = testPatient.setup(false);
		patient = patientIoOperationRepository.save(patient);

		HIVInfant infant = testHIVInfant.setup(patient, null, HIVInfantStatus.ACTIVE, false);
		infant = hivInfantManager.newInfant(infant);

		assertThat(infant).isNotNull();
		assertThat(infant.getId()).isNotNull();

		List<HIVInfant> list = hivInfantManager.getInfantsByPatientCode(patient.getCode());
		assertThat(list).isNotEmpty();
		assertThat(list).hasSize(1);

		HIVInfant updated = list.get(0);
		updated.setFeedingType(FeedingType.ARTIFICIAL);
		updated = hivInfantManager.updateInfant(updated);
		assertThat(updated.getFeedingType()).isEqualTo(FeedingType.ARTIFICIAL);

		HIVInfant found = hivInfantManager.getInfantById(updated.getId());
		assertThat(found).isNotNull();
		assertThat(found.getId()).isEqualTo(updated.getId());

		hivInfantManager.deleteInfant(updated);

		List<HIVInfant> afterDelete = hivInfantManager.getInfantsByPatientCode(patient.getCode());
		assertThat(afterDelete).isEmpty();
	}

	@Test
	void testHIVInfantPagination() throws Exception {
		Patient patient = testPatient.setup(false);
		patient = patientIoOperationRepository.save(patient);

		for (int i = 0; i < 5; i++) {
			HIVInfant infant = testHIVInfant.setup(patient, null, HIVInfantStatus.ACTIVE, false);
			hivInfantIoOperationRepository.save(infant);
		}

		Pageable pageable = PageRequest.of(0, 2);
		Page<HIVInfant> page = hivInfantManager.getAllInfants(pageable);

		assertThat(page.getTotalElements()).isEqualTo(5);
		assertThat(page.getContent()).hasSize(2);
	}

	@Test
	void testHIVInfantFilters() throws Exception {
		Patient patient = testPatient.setup(false);
		patient = patientIoOperationRepository.save(patient);

		HIVInfant infant = testHIVInfant.setup(patient, null, HIVInfantStatus.ACTIVE, false);
		infant.setFeedingType(FeedingType.MATERNAL);
		infant = hivInfantIoOperationRepository.save(infant);

		Pageable pageable = PageRequest.of(0, 10);

		LocalDate dateFrom = LocalDate.of(2000, 1, 1);
		LocalDate dateTo = LocalDate.of(2100, 12, 31);

		Page<HIVInfant> page = hivInfantManager.getInfantsByFilters(
			patient.getCode(), HIVInfantStatus.ACTIVE, FeedingType.MATERNAL,
			dateFrom, dateTo,
			dateFrom, dateTo,
			pageable);

		assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(1);
	}

	@Test
	void testHIVInfantValidationPatientNull() throws Exception {
		HIVInfant infant = testHIVInfant.setup(null, null, HIVInfantStatus.ACTIVE, false);
		infant.setPatient(null);

		assertThatThrownBy(() -> hivInfantManager.newInfant(infant))
			.isInstanceOf(OHDataIntegrityViolationException.class);
	}

	@Test
	void testHIVInfantValidationRegistrationDateNull() throws Exception {
		Patient patient = testPatient.setup(false);
		patient = patientIoOperationRepository.save(patient);

		HIVInfant infant = testHIVInfant.setup(patient, null, HIVInfantStatus.ACTIVE, false);
		infant.setRegistrationDate(null);

		assertThatThrownBy(() -> hivInfantManager.newInfant(infant))
			.isInstanceOf(OHDataIntegrityViolationException.class);
	}

	@Test
	void testHIVVisitCRUD() throws Exception {
		Patient patient = testPatient.setup(false);
		patient = patientIoOperationRepository.save(patient);

		HIVInfant infant = testHIVInfant.setup(patient, null, HIVInfantStatus.ACTIVE, false);
		infant = hivInfantManager.newInfant(infant);

		HIVVisit visit = testHIVVisit.setup(infant, PCRResult.NEGATIVE, false);
		visit = hivVisitManager.newVisit(visit);

		assertThat(visit).isNotNull();
		assertThat(visit.getId()).isNotNull();

		List<HIVVisit> list = hivVisitManager.getVisitsByInfantId(infant.getId());
		assertThat(list).isNotEmpty();
		assertThat(list).hasSize(1);

		HIVVisit updated = list.get(0);
		updated.setWeight(7.5);
		updated = hivVisitManager.updateVisit(updated);
		assertThat(updated.getWeight()).isEqualTo(7.5);

		HIVVisit found = hivVisitManager.getVisitById(updated.getId());
		assertThat(found).isNotNull();
		assertThat(found.getId()).isEqualTo(updated.getId());

		hivVisitManager.deleteVisit(updated);

		List<HIVVisit> afterDelete = hivVisitManager.getVisitsByInfantId(infant.getId());
		assertThat(afterDelete).isEmpty();
	}

	@Test
	void testHIVVisitPagination() throws Exception {
		Patient patient = testPatient.setup(false);
		patient = patientIoOperationRepository.save(patient);

		HIVInfant infant = testHIVInfant.setup(patient, null, HIVInfantStatus.ACTIVE, false);
		infant = hivInfantManager.newInfant(infant);

		for (int i = 0; i < 5; i++) {
			HIVVisit visit = testHIVVisit.setup(infant, PCRResult.NEGATIVE, false);
			hivVisitIoOperationRepository.save(visit);
		}

		Pageable pageable = PageRequest.of(0, 2);
		Page<HIVVisit> page = hivVisitManager.getVisitsByInfantId(infant.getId(), pageable);

		assertThat(page.getTotalElements()).isEqualTo(5);
		assertThat(page.getContent()).hasSize(2);
	}

	@Test
	void testHIVVisitDateRange() throws Exception {
		Patient patient = testPatient.setup(false);
		patient = patientIoOperationRepository.save(patient);

		HIVInfant infant = testHIVInfant.setup(patient, null, HIVInfantStatus.ACTIVE, false);
		infant = hivInfantManager.newInfant(infant);

		LocalDateTime now = LocalDateTime.now();

		HIVVisit visit1 = testHIVVisit.setup(infant, PCRResult.NEGATIVE, false);
		visit1.setVisitDate(now.minusDays(5));
		hivVisitIoOperationRepository.save(visit1);

		HIVVisit visit2 = testHIVVisit.setup(infant, PCRResult.NEGATIVE, false);
		visit2.setVisitDate(now.plusDays(5));
		hivVisitIoOperationRepository.save(visit2);

		List<HIVVisit> visits = hivVisitManager.getVisitsByInfantIdAndDateRange(
			infant.getId(),
			now.minusDays(10),
			now.plusDays(1));

		assertThat(visits).hasSize(1);
		assertThat(visits.get(0).getVisitDate()).isBefore(now);
	}

	@Test
	void testHIVVisitValidationDateNull() throws Exception {
		Patient patient = testPatient.setup(false);
		patient = patientIoOperationRepository.save(patient);

		HIVInfant infant = testHIVInfant.setup(patient, null, HIVInfantStatus.ACTIVE, false);
		infant = hivInfantManager.newInfant(infant);

		HIVVisit visit = testHIVVisit.setup(infant, PCRResult.NEGATIVE, false);
		visit.setVisitDate(null);

		assertThatThrownBy(() -> hivVisitManager.newVisit(visit))
			.isInstanceOf(OHDataIntegrityViolationException.class);
	}
}