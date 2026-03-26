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
package org.isf.integrations.labbook.mappers;

import org.isf.OpenHospitalCoreApplication;
import org.isf.integrations.labbook.config.LabBookBeanNames;
import org.isf.integrations.labbook.models.PatientDetRequest;
import org.isf.integrations.labbook.ports.IPatientService;
import org.isf.patient.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link PatientMapper}.
 *
 * <p>Verifies field-level mapping from Open Hospital {@link Patient} objects to
 * {@link PatientDetRequest} LabBook API models, including sex-code translation.
 */
class PatientMapperTest {

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

		@Autowired
		IPatientService patientService;

		@Autowired
		@Qualifier(LabBookBeanNames.PATIENT_MAPPER)
		private PatientMapper patientMapper;

		private Patient patient;

		@BeforeEach
		void setUp() {
			patient = buildPatient(42, "John", "Doe", 'M');
		}

		@Test
		@DisplayName("Should map patient code as pat_code in the request")
		void shouldMapPatientCodeInRequest() {
			PatientDetRequest request = patientMapper.toDetRequest(patient);

			assertThat(request.code()).isEqualTo("42");
		}

		@Test
		@DisplayName("Should map firstName to pat_firstname and secondName to pat_name")
		void shouldMapFirstNameAndSecondName() {
			PatientDetRequest request = patientMapper.toDetRequest(patient);

			assertThat(request.firstname()).isEqualTo("John");
			assertThat(request.name()).isEqualTo("Doe");
		}

		@Test
		@DisplayName("Should map male sex char to LabBook sex code 1")
		void shouldMapMaleSexToCode1() {
			patient.setSex('M');
			PatientDetRequest request = patientMapper.toDetRequest(patient);

			assertThat(request.sex()).isEqualTo(1);
		}

		@Test
		@DisplayName("Should map female sex char to LabBook sex code 2")
		void shouldMapFemaleSexToCode2() {
			patient.setSex('F');
			PatientDetRequest request = patientMapper.toDetRequest(patient);

			assertThat(request.sex()).isEqualTo(2);
		}

		@Test
		@DisplayName("Should map unknown / blank sex to LabBook sex code 3")
		void shouldMapUnknownSexToCode3() {
			patient.setSex(' ');
			PatientDetRequest request = patientMapper.toDetRequest(patient);

			assertThat(request.sex()).isEqualTo(3);
		}

		@Test
		@DisplayName("Should map birth date from patient to request")
		void shouldMapBirthDate() {
			PatientDetRequest request = patientMapper.toDetRequest(patient);

			assertThat(request.birth()).isEqualTo(LocalDate.of(1990, 6, 15));
		}

		@Test
		@DisplayName("Should map bloodType from patient to request")
		void shouldMapBloodType() {
			PatientDetRequest request = patientMapper.toDetRequest(patient);

			assertThat(request.bloodGroup()).isEqualTo(3);
			assertThat(request.bloodRhesus()).isEqualTo(1);
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
		@DisplayName("Should not register PatientMapper bean when disabled")
		void shouldNotRegisterPatientMapperBean() {
			assertThat(context.getBeansOfType(PatientMapper.class)).isEmpty();
		}
	}
}
