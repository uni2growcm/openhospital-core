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
package org.isf.integrations.labbook.services;

import org.isf.OpenHospitalCoreApplication;
import org.isf.integrations.labbook.config.LabBookBeanNames;
import org.isf.integrations.labbook.ports.IOauthTokenService;
import org.isf.integrations.labbook.ports.IPatientService;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientCreatedOrUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Integration tests for {@link PatientSyncService}.
 *
 * <p>Uses {@code @MockitoBean} on {@link IPatientService} to avoid real HTTP calls —
 * consistent with the approach used in {@code TokenServiceTest}.
 */
class PatientSyncServiceTest {

	// -------------------------------------------------------------------------
	// Shared patient factory
	// -------------------------------------------------------------------------

	private static Patient buildPatient(int code, String firstName, String secondName, char sex) {
		Patient patient = new Patient();
		patient.setCode(code);
		patient.setFirstName(firstName);
		patient.setSecondName(secondName);
		patient.setSex(sex);
		patient.setBirthDate(LocalDate.of(1990, 6, 15));
		patient.setAddress("123 Main St");
		patient.setCity("Testville");
		patient.setTelephone("555-1234");
		patient.setBloodType("AB+");
		return patient;
	}

	// =========================================================================
	// Tests when LabBook is ENABLED
	// =========================================================================

	@Nested
	@DisplayName("When labbook.enabled=true")
	@SpringBootTest(
		classes = OpenHospitalCoreApplication.class,
		properties = {
			"labbook.enabled=true",
			"labbook.oauth.client-id=test-client",
			"labbook.oauth.client-secret=test-secret"
		}
	)
	class WhenLabBookEnabled {

		@MockitoBean(LabBookBeanNames.OAUTH_TOKEN_SERVICE)
		private IOauthTokenService oauthTokenService;

		@MockitoBean(LabBookBeanNames.PATIENT_SERVICE)
		private IPatientService patientService;

		@Autowired
		private IPatientSyncService patientSyncService;

		private Patient patient;

		@BeforeEach
		void setUp() {
			patient = buildPatient(42, "John", "Doe", 'M');
		}

		@Test
		@DisplayName("Should call saveOrUpdatePatient when a patient creation event is published")
		void shouldCallSaveOrUpdateOnCreation() {
			patientSyncService.onPatientCreatedOrUpdated(new PatientCreatedOrUpdatedEvent(patient, true));

			verify(patientService).saveOrUpdatePatient(eq(42), any());
		}

		@Test
		@DisplayName("Should call saveOrUpdatePatient when a patient update event is published")
		void shouldCallSaveOrUpdateOnUpdate() {
			patientSyncService.onPatientCreatedOrUpdated(new PatientCreatedOrUpdatedEvent(patient, false));

			verify(patientService).saveOrUpdatePatient(eq(42), any());
		}

		@Test
		@DisplayName("Should not throw when LabBook API call fails (swallow RestClientException)")
		void shouldSwallowRestClientExceptionOnApiFailure() {
			doThrow(new ResourceAccessException("LabBook unreachable"))
				.when(patientService).saveOrUpdatePatient(any(), any());

			assertThatCode(() ->
				patientSyncService.onPatientCreatedOrUpdated(new PatientCreatedOrUpdatedEvent(patient, false))
			).doesNotThrowAnyException();
		}

		@Test
		@DisplayName("Should skip sync when patient code is null")
		void shouldSkipSyncWhenPatientCodeIsNull() {
			Patient patientWithoutCode = buildPatient(42, "Jane", "Smith", 'F');
			patientWithoutCode.setCode(null);

			patientSyncService.onPatientCreatedOrUpdated(new PatientCreatedOrUpdatedEvent(patientWithoutCode, true));

			verify(patientService, never()).saveOrUpdatePatient(any(), any());
		}

		@Test
		@DisplayName("Should skip sync when patient itself is null")
		void shouldSkipSyncWhenPatientIsNull() {
			assertThatCode(() ->
				patientSyncService.onPatientCreatedOrUpdated(new PatientCreatedOrUpdatedEvent(null, true))
			).doesNotThrowAnyException();

			verify(patientService, never()).saveOrUpdatePatient(any(), any());
		}
	}

	// =========================================================================
	// Tests when LabBook is DISABLED
	// =========================================================================

	@Nested
	@DisplayName("When labbook.enabled=false")
	@SpringBootTest(
		classes = OpenHospitalCoreApplication.class,
		properties = {"labbook.enabled=false"}
	)
	class WhenLabBookDisabled {

		@Autowired
		private ApplicationContext context;

		@Test
		@DisplayName("Should not register PatientSyncService bean when disabled")
		void shouldNotRegisterPatientSyncServiceBean() {
			boolean hasSyncService = context.getBeansOfType(PatientSyncService.class).isEmpty();
			assertThat(hasSyncService).isTrue();
		}

		@Test
		@DisplayName("Should not register labbookPatientService bean when disabled")
		void shouldNotRegisterPatientServiceBean() {
			assertThat(context.containsBean(LabBookBeanNames.PATIENT_SERVICE)).isFalse();
		}
	}
}
