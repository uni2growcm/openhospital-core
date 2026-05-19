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
package org.isf.hospitalizationconsultation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.isf.OHCoreTestCase;
import org.isf.encounter.model.Encounter;
import org.isf.hospitalizationconsultation.model.HospitalizationConsultation;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.pagination.PagedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class HospitalizationConsultationIoOperationsTest extends OHCoreTestCase {

	private HospitalizationConsultationIoOperationRepository repository;

	private HospitalizationConsultationIoOperations service;
	private HospitalizationConsultation testConsultation;
	private Encounter testEncounter;
	private LocalDateTime testDateTime;

	@BeforeEach
	void setUp() {
		repository = mock(HospitalizationConsultationIoOperationRepository.class);
		service = new HospitalizationConsultationIoOperations(repository);

		testEncounter = new Encounter();
		testEncounter.setId(1);
		
		testDateTime = LocalDateTime.of(2025, 1, 15, 10, 30);
		
		testConsultation = new HospitalizationConsultation();
		testConsultation.setId(1);
		testConsultation.setEncounter(testEncounter);
		testConsultation.setConsultationDate(testDateTime);
		testConsultation.setTeams("Cardiology");
		testConsultation.setParentComplaints("Chest pain");
		testConsultation.setPhysicalExamination("Normal exam");
		testConsultation.setDiagnosis("Hypertension");
		testConsultation.setInstructions("Medication");
	}

	@Test
	@DisplayName("Test get all hospitalization consultations")
	void testGetHospitalizationConsultations() throws OHServiceException {
		List<HospitalizationConsultation> expectedList = new ArrayList<>();
		expectedList.add(testConsultation);
		
		when(repository.findAll()).thenReturn(expectedList);
		
		List<HospitalizationConsultation> result = service.getHospitalizationConsultations();
		
		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0)).isEqualTo(testConsultation);
		verify(repository).findAll();
	}

	@Test
	@DisplayName("Test get hospitalization consultations by Encounter")
	void testGetHospitalizationConsultationsByEncounter() throws OHServiceException {
		List<HospitalizationConsultation> expectedList = new ArrayList<>();
		expectedList.add(testConsultation);
		
		when(repository.findByEncounter(testEncounter)).thenReturn(expectedList);
		
		List<HospitalizationConsultation> result = service.getHospitalizationConsultationsByEncounter(testEncounter);
		
		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0)).isEqualTo(testConsultation);
		verify(repository).findByEncounter(testEncounter);
	}

	@Test
	@DisplayName("Test get hospitalization consultations by date range")
	void testGetHospitalizationConsultationsByDateRange() throws OHServiceException {
		LocalDateTime dateFrom = LocalDateTime.of(2025, 1, 1, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(2025, 1, 31, 23, 59);
		
		List<HospitalizationConsultation> expectedList = new ArrayList<>();
		expectedList.add(testConsultation);
		
		when(repository.findByConsultationDateBetween(dateFrom, dateTo)).thenReturn(expectedList);
		
		List<HospitalizationConsultation> result = service.getHospitalizationConsultationsByDateRange(dateFrom, dateTo);
		
		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0)).isEqualTo(testConsultation);
		verify(repository).findByConsultationDateBetween(dateFrom, dateTo);
	}

	@Test
	@DisplayName("Test get hospitalization consultation by ID - found")
	void testGetHospitalizationConsultationFound() throws OHServiceException {
		when(repository.findById(1)).thenReturn(Optional.of(testConsultation));
		
		HospitalizationConsultation result = service.getHospitalizationConsultation(1);
		
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(testConsultation);
		verify(repository).findById(1);
	}

	@Test
	@DisplayName("Test get hospitalization consultation by ID - not found")
	void testGetHospitalizationConsultationNotFound() throws OHServiceException {
		when(repository.findById(999)).thenReturn(Optional.empty());
		
		HospitalizationConsultation result = service.getHospitalizationConsultation(999);
		
		assertThat(result).isNull();
		verify(repository).findById(999);
	}

	@Test
	@DisplayName("Test new hospitalization consultation")
	void testNewHospitalizationConsultation() throws OHServiceException {
		when(repository.save(any(HospitalizationConsultation.class))).thenReturn(testConsultation);
		
		HospitalizationConsultation result = service.newHospitalizationConsultation(testConsultation);
		
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(testConsultation);
		verify(repository).save(testConsultation);
	}

	@Test
	@DisplayName("Test update hospitalization consultation")
	void testUpdateHospitalizationConsultation() throws OHServiceException {
		when(repository.save(any(HospitalizationConsultation.class))).thenReturn(testConsultation);
		
		HospitalizationConsultation result = service.updateHospitalizationConsultation(testConsultation);
		
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(testConsultation);
		verify(repository).save(testConsultation);
	}

	@Test
	@DisplayName("Test delete hospitalization consultation")
	void testDeleteHospitalizationConsultation() throws OHServiceException {
		service.deleteHospitalizationConsultation(testConsultation);
		
		verify(repository).delete(testConsultation);
	}

	@Test
	@DisplayName("Test get hospitalization consultations pageable")
	void testGetHospitalizationConsultationsPageable() throws OHServiceException {
		PageRequest pageRequest = PageRequest.of(0, 10);
		List<HospitalizationConsultation> content = List.of(testConsultation);
		Page<HospitalizationConsultation> page = new PageImpl<>(content, pageRequest, 1);
		
		when(repository.findAll(pageRequest)).thenReturn(page);
		
		PagedResponse<HospitalizationConsultation> result = service.getHospitalizationConsultationsPageable(pageRequest);
		
		assertThat(result).isNotNull();
		assertThat(result.getData()).hasSize(1);
		assertThat(result.getData().get(0)).isEqualTo(testConsultation);
		assertThat(result.getPageInfo()).isNotNull();
		verify(repository).findAll(pageRequest);
	}

	@Test
	@DisplayName("Test get hospitalization consultations by Encounter pageable")
	void testGetHospitalizationConsultationsByEncounterPageable() throws OHServiceException {
		PageRequest pageRequest = PageRequest.of(0, 10);
		List<HospitalizationConsultation> content = List.of(testConsultation);
		Page<HospitalizationConsultation> page = new PageImpl<>(content, pageRequest, 1);
		
		when(repository.findByEncounter(testEncounter, pageRequest)).thenReturn(page);
		
		PagedResponse<HospitalizationConsultation> result = service.getHospitalizationConsultationsByEncounterPageable(testEncounter, pageRequest);
		
		assertThat(result).isNotNull();
		assertThat(result.getData()).hasSize(1);
		assertThat(result.getData().get(0)).isEqualTo(testConsultation);
		assertThat(result.getPageInfo()).isNotNull();
		verify(repository).findByEncounter(testEncounter, pageRequest);
	}

	@Test
	@DisplayName("Test get hospitalization consultations by date range pageable")
	void testGetHospitalizationConsultationsByDateRangePageable() throws OHServiceException {
		LocalDateTime dateFrom = LocalDateTime.of(2025, 1, 1, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(2025, 1, 31, 23, 59);
		PageRequest pageRequest = PageRequest.of(0, 10);
		List<HospitalizationConsultation> content = List.of(testConsultation);
		Page<HospitalizationConsultation> page = new PageImpl<>(content, pageRequest, 1);
		
		when(repository.findByConsultationDateBetween(dateFrom, dateTo, pageRequest)).thenReturn(page);
		
		PagedResponse<HospitalizationConsultation> result = service.getHospitalizationConsultationsByDateRangePageable(dateFrom, dateTo, pageRequest);
		
		assertThat(result).isNotNull();
		assertThat(result.getData()).hasSize(1);
		assertThat(result.getData().get(0)).isEqualTo(testConsultation);
		assertThat(result.getPageInfo()).isNotNull();
		verify(repository).findByConsultationDateBetween(dateFrom, dateTo, pageRequest);
	}

	@Test
	@DisplayName("Test exists by ID - true")
	void testExistsByIdTrue() throws OHServiceException {
		when(repository.existsById(1)).thenReturn(true);
		
		boolean result = service.existsById(1);
		
		assertThat(result).isTrue();
		verify(repository).existsById(1);
	}

	@Test
	@DisplayName("Test exists by ID - false")
	void testExistsByIdFalse() throws OHServiceException {
		when(repository.existsById(999)).thenReturn(false);
		
		boolean result = service.existsById(999);
		
		assertThat(result).isFalse();
		verify(repository).existsById(999);
	}

	@Test
	@DisplayName("Test count all")
	void testCountAll() throws OHServiceException {
		when(repository.count()).thenReturn(5L);
		
		long result = service.countAll();
		
		assertThat(result).isEqualTo(5L);
		verify(repository).count();
	}

	@Test
	@DisplayName("Test count by Encounter")
	void testCountByEncounter() throws OHServiceException {
		when(repository.countByEncounter(testEncounter)).thenReturn(3L);
		
		long result = service.countByEncounter(testEncounter);
		
		assertThat(result).isEqualTo(3L);
		verify(repository).countByEncounter(testEncounter);
	}
}
