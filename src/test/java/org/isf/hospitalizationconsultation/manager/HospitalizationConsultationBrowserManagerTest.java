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
package org.isf.hospitalizationconsultation.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.encounter.model.Encounter;
import org.isf.hospitalizationconsultation.model.HospitalizationConsultation;
import org.isf.hospitalizationconsultation.service.HospitalizationConsultationIoOperations;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.pagination.PagedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;

class HospitalizationConsultationBrowserManagerTest extends OHCoreTestCase {

	@Mock
	private HospitalizationConsultationIoOperations ioOperations;
	
	@Mock
	private UserBrowsingManager userBrowsingManager;

	private HospitalizationConsultationBrowserManager manager;
	private HospitalizationConsultation testConsultation;
	private Encounter testEncounter;
	private LocalDateTime testDateTime;

	@BeforeEach
	void setUp() {
		manager = new HospitalizationConsultationBrowserManager(ioOperations);
		
		// Setup test data
		testEncounter = new Encounter();
		testEncounter.setId(1);
		
		testDateTime = LocalDateTime.of(2025, 1, 15, 10, 30);
		
		testConsultation = new HospitalizationConsultation();
		testConsultation.setId(1);
		testConsultation.setEncounter(testEncounter);
		testConsultation.setDateTime(testDateTime);
		testConsultation.setTeams("Cardiology");
		testConsultation.setParentComplaints("Chest pain");
		testConsultation.setPhysicalExamination("Normal exam");
		testConsultation.setDiagnosis("Hypertension");
		testConsultation.setManagementPlan("Medication");
	}

	@Test
	@DisplayName("Test get all hospitalization consultations")
	void testGetHospitalizationConsultations() throws OHServiceException {
		List<HospitalizationConsultation> expectedList = new ArrayList<>();
		expectedList.add(testConsultation);
		
		when(ioOperations.getHospitalizationConsultations()).thenReturn(expectedList);
		
		List<HospitalizationConsultation> result = manager.getHospitalizationConsultations();
		
		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0)).isEqualTo(testConsultation);
		verify(ioOperations).getHospitalizationConsultations();
	}

	@Test
	@DisplayName("Test get hospitalization consultations by Encounter")
	void testGetHospitalizationConsultationsByEncounter() throws OHServiceException {
		List<HospitalizationConsultation> expectedList = new ArrayList<>();
		expectedList.add(testConsultation);
		
		when(ioOperations.getHospitalizationConsultationsByEncounter(testEncounter)).thenReturn(expectedList);
		
		List<HospitalizationConsultation> result = manager.getHospitalizationConsultationsByEncounter(testEncounter);
		
		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0)).isEqualTo(testConsultation);
		verify(ioOperations).getHospitalizationConsultationsByEncounter(testEncounter);
	}

	@Test
	@DisplayName("Test get hospitalization consultations by date range")
	void testGetHospitalizationConsultationsByDateRange() throws OHServiceException {
		LocalDateTime dateFrom = LocalDateTime.of(2025, 1, 1, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(2025, 1, 31, 23, 59);
		
		List<HospitalizationConsultation> expectedList = new ArrayList<>();
		expectedList.add(testConsultation);
		
		when(ioOperations.getHospitalizationConsultationsByDateRange(dateFrom, dateTo)).thenReturn(expectedList);
		
		List<HospitalizationConsultation> result = manager.getHospitalizationConsultationsByDateRange(dateFrom, dateTo);
		
		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0)).isEqualTo(testConsultation);
		verify(ioOperations).getHospitalizationConsultationsByDateRange(dateFrom, dateTo);
	}

	@Test
	@DisplayName("Test get hospitalization consultation by ID")
	void testGetHospitalizationConsultation() throws OHServiceException {
		when(ioOperations.getHospitalizationConsultation(1)).thenReturn(testConsultation);
		when(ioOperations.getHospitalizationConsultation(999)).thenReturn(null);
		
		HospitalizationConsultation result = manager.getHospitalizationConsultation(1);
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(testConsultation);
		
		result = manager.getHospitalizationConsultation(999);
		assertThat(result).isNull();
		
		verify(ioOperations).getHospitalizationConsultation(1);
		verify(ioOperations).getHospitalizationConsultation(999);
	}

	@Test
	@DisplayName("Test new hospitalization consultation - success")
	void testNewHospitalizationConsultationSuccess() throws OHServiceException {
		when(ioOperations.newHospitalizationConsultation(any(HospitalizationConsultation.class))).thenReturn(testConsultation);
		
		HospitalizationConsultation result = manager.newHospitalizationConsultation(testConsultation);
		
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(testConsultation);
		verify(ioOperations).newHospitalizationConsultation(testConsultation);
	}

	@Test
	@DisplayName("Test new hospitalization consultation - validation error - null Encounter")
	void testNewHospitalizationConsultationValidationErrorNullEncounter() {
		testConsultation.setEncounter(null);
		
		assertThatThrownBy(() -> manager.newHospitalizationConsultation(testConsultation))
			.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	@DisplayName("Test new hospitalization consultation - validation error - null date")
	void testNewHospitalizationConsultationValidationErrorNullDate() {
		testConsultation.setDateTime(null);
		
		assertThatThrownBy(() -> manager.newHospitalizationConsultation(testConsultation))
			.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	@DisplayName("Test update hospitalization consultation - success")
	void testUpdateHospitalizationConsultationSuccess() throws OHServiceException {
		when(ioOperations.updateHospitalizationConsultation(any(HospitalizationConsultation.class))).thenReturn(testConsultation);
		
		HospitalizationConsultation result = manager.updateHospitalizationConsultation(testConsultation);
		
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(testConsultation);
		verify(ioOperations).updateHospitalizationConsultation(testConsultation);
	}

	@Test
	@DisplayName("Test update hospitalization consultation - validation error")
	void testUpdateHospitalizationConsultationValidationError() {
		testConsultation.setEncounter(null);
		
		assertThatThrownBy(() -> manager.updateHospitalizationConsultation(testConsultation))
			.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	@DisplayName("Test delete hospitalization consultation")
	void testDeleteHospitalizationConsultation() throws OHServiceException {
		manager.deleteHospitalizationConsultation(testConsultation);
		
		verify(ioOperations).deleteHospitalizationConsultation(testConsultation);
	}

	@Test
	@DisplayName("Test get hospitalization consultations pageable")
	void testGetHospitalizationConsultationsPageable() throws OHServiceException {
		PageRequest pageRequest = PageRequest.of(0, 10);
		PagedResponse<HospitalizationConsultation> expectedResponse = new PagedResponse<>();
		expectedResponse.setData(List.of(testConsultation));
		
		when(ioOperations.getHospitalizationConsultationsPageable(pageRequest)).thenReturn(expectedResponse);
		
		PagedResponse<HospitalizationConsultation> result = manager.getHospitalizationConsultationsPageable(0, 10);
		
		assertThat(result).isNotNull();
		assertThat(result.getData()).hasSize(1);
		assertThat(result.getData().get(0)).isEqualTo(testConsultation);
		verify(ioOperations).getHospitalizationConsultationsPageable(pageRequest);
	}

	@Test
	@DisplayName("Test get hospitalization consultations by Encounter pageable")
	void testGetHospitalizationConsultationsByEncounterPageable() throws OHServiceException {
		PageRequest pageRequest = PageRequest.of(0, 10);
		PagedResponse<HospitalizationConsultation> expectedResponse = new PagedResponse<>();
		expectedResponse.setData(List.of(testConsultation));
		
		when(ioOperations.getHospitalizationConsultationsByEncounterPageable(testEncounter, pageRequest)).thenReturn(expectedResponse);
		
		PagedResponse<HospitalizationConsultation> result = manager.getHospitalizationConsultationsByEncounterPageable(testEncounter, 0, 10);
		
		assertThat(result).isNotNull();
		assertThat(result.getData()).hasSize(1);
		assertThat(result.getData().get(0)).isEqualTo(testConsultation);
		verify(ioOperations).getHospitalizationConsultationsByEncounterPageable(testEncounter, pageRequest);
	}

	@Test
	@DisplayName("Test get hospitalization consultations by date range pageable")
	void testGetHospitalizationConsultationsByDateRangePageable() throws OHServiceException {
		LocalDateTime dateFrom = LocalDateTime.of(2025, 1, 1, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(2025, 1, 31, 23, 59);
		PageRequest pageRequest = PageRequest.of(0, 10);
		PagedResponse<HospitalizationConsultation> expectedResponse = new PagedResponse<>();
		expectedResponse.setData(List.of(testConsultation));
		
		when(ioOperations.getHospitalizationConsultationsByDateRangePageable(dateFrom, dateTo, pageRequest)).thenReturn(expectedResponse);
		
		PagedResponse<HospitalizationConsultation> result = manager.getHospitalizationConsultationsByDateRangePageable(dateFrom, dateTo, 0, 10);
		
		assertThat(result).isNotNull();
		assertThat(result.getData()).hasSize(1);
		assertThat(result.getData().get(0)).isEqualTo(testConsultation);
		verify(ioOperations).getHospitalizationConsultationsByDateRangePageable(dateFrom, dateTo, pageRequest);
	}
}
