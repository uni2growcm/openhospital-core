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
package org.isf.maternity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.isf.maternity.TestMaternity;
import org.isf.maternity.model.Delivery;
import org.isf.maternity.model.Newborn;
import org.isf.maternity.model.Pregnancy;
import org.isf.maternity.model.Visit;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

/**
 * Unit tests for MaternityIoOperations unified service
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Maternity IO Operations Tests")
class MaternityIoOperationsTest {

	@Autowired
	private MaternityIoOperations maternityIoOperations;

	@MockBean
	private MaternityIoOperationRepository maternityRepository;

	private TestMaternity testMaternity;
	private TestPatient testPatient;
	private Patient patient;
	private Pregnancy pregnancy;
	private Delivery delivery;
	private Newborn newborn;
	private Visit pregnancyVisit;

	@BeforeEach
	void setUp() {
		testMaternity = new TestMaternity();
		testPatient = new TestPatient();
		patient = testPatient.setup(false);
		pregnancy = testMaternity.setupPregnancy(patient, false);
		delivery = testMaternity.setupDelivery(patient, pregnancy, false);
		newborn = testMaternity.setupNewborn(delivery, false);
		pregnancyVisit = testMaternity.setupPregnancyVisit(pregnancy, false);
	}

	// ===================== PREGNANCY TESTS =====================

	@Test
	@DisplayName("Save pregnancy successfully")
	void testSavePregnancy() throws OHServiceException {
		when(maternityRepository.save(any(Pregnancy.class))).thenReturn(pregnancy);

		Pregnancy result = maternityIoOperations.savePregnancy(pregnancy);

		assertThat(result).isNotNull();
		assertThat(result.getPatient()).isEqualTo(patient);
		verify(maternityRepository, times(1)).save(any(Pregnancy.class));
	}

	@Test
	@DisplayName("Find pregnancy by ID")
	void testFindPregnancyById() throws OHServiceException {
		when(maternityRepository.findById(anyInt())).thenReturn(Optional.of(pregnancy));

		Optional<Pregnancy> result = maternityIoOperations.findPregnancyById(1);

		assertThat(result).isPresent();
		assertThat(result.get()).isEqualTo(pregnancy);
	}

	@Test
	@DisplayName("Find pregnancies by patient code")
	void testFindPregnanciesByPatientCode() throws OHServiceException {
		List<Pregnancy> pregnancies = new ArrayList<>();
		pregnancies.add(pregnancy);
		
		when(maternityRepository.findPregnanciesByPatientCode(anyInt())).thenReturn(pregnancies);

		List<Pregnancy> result = maternityIoOperations.findPregnanciesByPatientCode(patient.getCode());

		assertThat(result).isNotEmpty();
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.get(0)).isEqualTo(pregnancy);
	}

	@Test
	@DisplayName("Find pregnancies by date range")
	void testFindPregnanciesByDateRange() throws OHServiceException {
		List<Pregnancy> pregnancies = new ArrayList<>();
		pregnancies.add(pregnancy);
		LocalDate dateFrom = LocalDate.of(2025, 1, 1);
		LocalDate dateTo = LocalDate.of(2025, 12, 31);
		
		when(maternityRepository.findPregnanciesByDateRange(dateFrom, dateTo)).thenReturn(pregnancies);

		List<Pregnancy> result = maternityIoOperations.findPregnanciesByDateRange(dateFrom, dateTo);

		assertThat(result).isNotEmpty();
		assertThat(result.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Find pregnancies by status")
	void testFindPregnanciesByStatus() throws OHServiceException {
		List<Pregnancy> pregnancies = new ArrayList<>();
		pregnancies.add(pregnancy);
		
		when(maternityRepository.findPregnanciesByStatus("Ongoing")).thenReturn(pregnancies);

		List<Pregnancy> result = maternityIoOperations.findPregnanciesByStatus("Ongoing");

		assertThat(result).isNotEmpty();
		assertThat(result.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Find pregnancies by risk level")
	void testFindPregnanciesByRiskLevel() throws OHServiceException {
		List<Pregnancy> pregnancies = new ArrayList<>();
		pregnancies.add(pregnancy);
		
		when(maternityRepository.findPregnanciesByRiskLevel("High")).thenReturn(pregnancies);

		List<Pregnancy> result = maternityIoOperations.findPregnanciesByRiskLevel("High");

		assertThat(result).isNotEmpty();
		assertThat(result.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Find pregnancies by multiple criteria")
	void testFindPregnanciesByMultipleCriteria() throws OHServiceException {
		List<Pregnancy> pregnancies = new ArrayList<>();
		pregnancies.add(pregnancy);
		LocalDate dateFrom = LocalDate.of(2025, 1, 1);
		LocalDate dateTo = LocalDate.of(2025, 12, 31);
		
		when(maternityRepository.findPregnanciesByMultipleCriteria("Ongoing", dateFrom, dateTo, "High", patient.getCode()))
			.thenReturn(pregnancies);

		List<Pregnancy> result = maternityIoOperations.findPregnanciesByMultipleCriteria(
			"Ongoing", dateFrom, dateTo, "High", patient.getCode());

		assertThat(result).isNotEmpty();
		assertThat(result.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Find active pregnancies by patient")
	void testFindActivePregnanciesByPatient() throws OHServiceException {
		List<Pregnancy> pregnancies = new ArrayList<>();
		pregnancies.add(pregnancy);
		
		when(maternityRepository.findActivePregnanciesByPatient(anyInt())).thenReturn(pregnancies);

		List<Pregnancy> result = maternityIoOperations.findActivePregnanciesByPatient(patient.getCode());

		assertThat(result).isNotEmpty();
	}

	@Test
	@DisplayName("Update pregnancy")
	void testUpdatePregnancy() throws OHServiceException {
		when(maternityRepository.save(any(Pregnancy.class))).thenReturn(pregnancy);

		Pregnancy result = maternityIoOperations.updatePregnancy(pregnancy);

		assertThat(result).isNotNull();
		verify(maternityRepository, times(1)).save(any(Pregnancy.class));
	}

	@Test
	@DisplayName("Soft delete pregnancy")
	void testSoftDeletePregnancy() throws OHServiceException {
		when(maternityRepository.findById(anyInt())).thenReturn(Optional.of(pregnancy));
		when(maternityRepository.save(any(Pregnancy.class))).thenReturn(pregnancy);

		maternityIoOperations.softDeletePregnancy(1);

		assertThat(pregnancy.getActive()).isEqualTo(0);
		verify(maternityRepository, times(1)).save(any(Pregnancy.class));
	}

	// ===================== DELIVERY TESTS =====================

	@Test
	@DisplayName("Save delivery successfully")
	void testSaveDelivery() throws OHServiceException {
		when(maternityRepository.save(any(Delivery.class))).thenReturn(delivery);

		Delivery result = maternityIoOperations.saveDelivery(delivery);

		assertThat(result).isNotNull();
		verify(maternityRepository, times(1)).save(any(Delivery.class));
	}

	@Test
	@DisplayName("Find deliveries by pregnancy ID")
	void testFindDeliveriesByPregnancyId() throws OHServiceException {
		List<Delivery> deliveries = new ArrayList<>();
		deliveries.add(delivery);
		
		when(maternityRepository.findDeliveriesByPregnancyId(anyInt())).thenReturn(deliveries);

		List<Delivery> result = maternityIoOperations.findDeliveriesByPregnancyId(pregnancy.getID());

		assertThat(result).isNotEmpty();
		assertThat(result.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Find deliveries by patient code")
	void testFindDeliveriesByPatientCode() throws OHServiceException {
		List<Delivery> deliveries = new ArrayList<>();
		deliveries.add(delivery);
		
		when(maternityRepository.findDeliveriesByPatientCode(anyInt())).thenReturn(deliveries);

		List<Delivery> result = maternityIoOperations.findDeliveriesByPatientCode(patient.getCode());

		assertThat(result).isNotEmpty();
	}

	@Test
	@DisplayName("Find deliveries by date range")
	void testFindDeliveriesByDateRange() throws OHServiceException {
		List<Delivery> deliveries = new ArrayList<>();
		deliveries.add(delivery);
		LocalDate dateFrom = LocalDate.of(2025, 1, 1);
		LocalDate dateTo = LocalDate.of(2025, 12, 31);
		
		when(maternityRepository.findDeliveriesByDateRange(dateFrom, dateTo)).thenReturn(deliveries);

		List<Delivery> result = maternityIoOperations.findDeliveriesByDateRange(dateFrom, dateTo);

		assertThat(result).isNotEmpty();
	}

	@Test
	@DisplayName("Find deliveries by mode")
	void testFindDeliveriesByMode() throws OHServiceException {
		List<Delivery> deliveries = new ArrayList<>();
		deliveries.add(delivery);
		
		when(maternityRepository.findDeliveriesByMode("SVD")).thenReturn(deliveries);

		List<Delivery> result = maternityIoOperations.findDeliveriesByMode("SVD");

		assertThat(result).isNotEmpty();
	}

	@Test
	@DisplayName("Find deliveries by type")
	void testFindDeliveriesByDeliveryType() throws OHServiceException {
		List<Delivery> deliveries = new ArrayList<>();
		deliveries.add(delivery);
		
		when(maternityRepository.findDeliveriesByDeliveryType("VD")).thenReturn(deliveries);

		List<Delivery> result = maternityIoOperations.findDeliveriesByDeliveryType("VD");

		assertThat(result).isNotEmpty();
	}

	@Test
	@DisplayName("Update delivery")
	void testUpdateDelivery() throws OHServiceException {
		when(maternityRepository.save(any(Delivery.class))).thenReturn(delivery);

		Delivery result = maternityIoOperations.updateDelivery(delivery);

		assertThat(result).isNotNull();
		verify(maternityRepository, times(1)).save(any(Delivery.class));
	}

	// ===================== NEWBORN TESTS =====================

	@Test
	@DisplayName("Save newborn successfully")
	void testSaveNewborn() throws OHServiceException {
		when(maternityRepository.save(any(Newborn.class))).thenReturn(newborn);

		Newborn result = maternityIoOperations.saveNewborn(newborn);

		assertThat(result).isNotNull();
		verify(maternityRepository, times(1)).save(any(Newborn.class));
	}

	@Test
	@DisplayName("Find newborns by delivery ID")
	void testFindNewbornsByDeliveryId() throws OHServiceException {
		List<Newborn> newborns = new ArrayList<>();
		newborns.add(newborn);
		
		when(maternityRepository.findNewbornsByDeliveryId(anyInt())).thenReturn(newborns);

		List<Newborn> result = maternityIoOperations.findNewbornsByDeliveryId(delivery.getId());

		assertThat(result).isNotEmpty();
		assertThat(result.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Find newborns by pregnancy ID")
	void testFindNewbornsByPregnancyId() throws OHServiceException {
		List<Newborn> newborns = new ArrayList<>();
		newborns.add(newborn);
		
		when(maternityRepository.findNewbornsByPregnancyId(anyInt())).thenReturn(newborns);

		List<Newborn> result = maternityIoOperations.findNewbornsByPregnancyId(pregnancy.getID());

		assertThat(result).isNotEmpty();
	}

	@Test
	@DisplayName("Find newborns by gender")
	void testFindNewbornsByGender() throws OHServiceException {
		List<Newborn> newborns = new ArrayList<>();
		newborns.add(newborn);
		
		when(maternityRepository.findNewbornsByGender("M")).thenReturn(newborns);

		List<Newborn> result = maternityIoOperations.findNewbornsByGender("M");

		assertThat(result).isNotEmpty();
	}

	@Test
	@DisplayName("Find newborns by weight range")
	void testFindNewbornsByBirthWeightRange() throws OHServiceException {
		List<Newborn> newborns = new ArrayList<>();
		newborns.add(newborn);
		
		when(maternityRepository.findNewbornsByBirthWeightRange(2500, 4000)).thenReturn(newborns);

		List<Newborn> result = maternityIoOperations.findNewbornsByBirthWeightRange(2500, 4000);

		assertThat(result).isNotEmpty();
	}

	@Test
	@DisplayName("Find newborns with low APGAR score")
	void testFindNewbornsByLowApgarScore() throws OHServiceException {
		List<Newborn> newborns = new ArrayList<>();
		newborns.add(newborn);
		
		when(maternityRepository.findNewbornsByLowApgarScore(7)).thenReturn(newborns);

		List<Newborn> result = maternityIoOperations.findNewbornsByLowApgarScore(7);

		assertThat(result).isNotEmpty();
	}

	@Test
	@DisplayName("Update newborn")
	void testUpdateNewborn() throws OHServiceException {
		when(maternityRepository.save(any(Newborn.class))).thenReturn(newborn);

		Newborn result = maternityIoOperations.updateNewborn(newborn);

		assertThat(result).isNotNull();
		verify(maternityRepository, times(1)).save(any(Newborn.class));
	}

	// ===================== PREGNANCY VISIT TESTS =====================

	@Test
	@DisplayName("Save pregnancy visit successfully")
	void testSavePregnancyVisit() throws OHServiceException {
		when(maternityRepository.save(any(Visit.class))).thenReturn(pregnancyVisit);

		Visit result = maternityIoOperations.savePregnancyVisit(pregnancyVisit);

		assertThat(result).isNotNull();
		verify(maternityRepository, times(1)).save(any(Visit.class));
	}

	@Test
	@DisplayName("Find visits by pregnancy ID")
	void testFindVisitsByPregnancyId() throws OHServiceException {
		List<Visit> visits = new ArrayList<>();
		visits.add(pregnancyVisit);
		
		when(maternityRepository.findVisitsByPregnancyId(anyInt())).thenReturn(visits);

		List<Visit> result = maternityIoOperations.findVisitsByPregnancyId(pregnancy.getID());

		assertThat(result).isNotEmpty();
		assertThat(result.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Find prenatal visits")
	void testFindPrenatalVisits() throws OHServiceException {
		List<Visit> visits = new ArrayList<>();
		visits.add(pregnancyVisit);
		
		when(maternityRepository.findPrenatalVisits(anyInt())).thenReturn(visits);

		List<Visit> result = maternityIoOperations.findPrenatalVisits(pregnancy.getID());

		assertThat(result).isNotEmpty();
	}

	@Test
	@DisplayName("Find visits by date range")
	void testFindVisitsByDateRange() throws OHServiceException {
		List<Visit> visits = new ArrayList<>();
		visits.add(pregnancyVisit);
		LocalDate dateFrom = LocalDate.of(2025, 1, 1);
		LocalDate dateTo = LocalDate.of(2025, 12, 31);
		
		when(maternityRepository.findVisitsByDateRange(anyInt(), any(), any())).thenReturn(visits);

		List<Visit> result = maternityIoOperations.findVisitsByDateRange(pregnancy.getID(), dateFrom, dateTo);

		assertThat(result).isNotEmpty();
	}

	@Test
	@DisplayName("Find visits by gestational age range")
	void testFindVisitsByGestationalAgeRange() throws OHServiceException {
		List<Visit> visits = new ArrayList<>();
		visits.add(pregnancyVisit);
		
		when(maternityRepository.findVisitsByGestationalAgeRange(anyInt(), anyInt(), anyInt())).thenReturn(visits);

		List<Visit> result = maternityIoOperations.findVisitsByGestationalAgeRange(pregnancy.getID(), 8, 12);

		assertThat(result).isNotEmpty();
	}

	@Test
	@DisplayName("Update pregnancy visit")
	void testUpdatePregnancyVisit() throws OHServiceException {
		when(maternityRepository.save(any(Visit.class))).thenReturn(pregnancyVisit);

		Visit result = maternityIoOperations.updatePregnancyVisit(pregnancyVisit);

		assertThat(result).isNotNull();
		verify(maternityRepository, times(1)).save(any(Visit.class));
	}

	// ===================== COUNT TESTS =====================

	@Test
	@DisplayName("Count active pregnancies")
	void testCountActivePregnancies() throws OHServiceException {
		when(maternityRepository.countAllActivePregnancies()).thenReturn(1L);

		long result = maternityIoOperations.countActivePregnancies();

		assertThat(result).isEqualTo(1L);
	}

	@Test
	@DisplayName("Count deliveries")
	void testCountDeliveries() throws OHServiceException {
		when(maternityRepository.countAllDeliveries()).thenReturn(1L);

		long result = maternityIoOperations.countDeliveries();

		assertThat(result).isEqualTo(1L);
	}

	@Test
	@DisplayName("Count deliveries by pregnancy")
	void testCountDeliveriesByPregnancy() throws OHServiceException {
		when(maternityRepository.countDeliveriesByPregnancy(anyInt())).thenReturn(1L);

		long result = maternityIoOperations.countDeliveriesByPregnancy(pregnancy.getID());

		assertThat(result).isEqualTo(1L);
	}

	@Test
	@DisplayName("Count newborns")
	void testCountNewborns() throws OHServiceException {
		when(maternityRepository.countAllNewborns()).thenReturn(1L);

		long result = maternityIoOperations.countNewborns();

		assertThat(result).isEqualTo(1L);
	}

	@Test
	@DisplayName("Count newborns by delivery")
	void testCountNewbornsByDelivery() throws OHServiceException {
		when(maternityRepository.countNewbornsByDelivery(anyInt())).thenReturn(1L);

		long result = maternityIoOperations.countNewbornsByDelivery(delivery.getId());

		assertThat(result).isEqualTo(1L);
	}

	@Test
	@DisplayName("Count visits by pregnancy")
	void testCountVisitsByPregnancy() throws OHServiceException {
		when(maternityRepository.countVisitsByPregnancy(anyInt())).thenReturn(1L);

		long result = maternityIoOperations.countVisitsByPregnancy(pregnancy.getID());

		assertThat(result).isEqualTo(1L);
	}
}
