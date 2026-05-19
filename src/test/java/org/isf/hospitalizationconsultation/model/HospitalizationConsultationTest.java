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
package org.isf.hospitalizationconsultation.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.isf.encounter.model.Encounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HospitalizationConsultationTest {

	private HospitalizationConsultation consultation;
	private Encounter encounter;
	private LocalDateTime testDateTime;

	@BeforeEach
	void setUp() {
		encounter = new Encounter();
		encounter.setId(1);
		
		testDateTime = LocalDateTime.of(2025, 1, 15, 10, 30);
		
		consultation = new HospitalizationConsultation();
		consultation.setId(1);
		consultation.setEncounter(encounter);
		consultation.setConsultationDate(testDateTime);
		consultation.setTeams("Cardiology, Neurology");
		consultation.setParentComplaints("Patient complains of chest pain and headache");
		consultation.setPhysicalExamination("Normal heart sounds, no neurological deficits");
		consultation.setDiagnosis("Hypertension, Tension headache");
		consultation.setInstructions("Start antihypertensive medication, regular follow-up");
	}

	@Test
	@DisplayName("Test constructor with parameters")
	void testConstructorWithParameters() {
		HospitalizationConsultation newConsultation = new HospitalizationConsultation(
			2, encounter, "Emergency Team", testDateTime.plusDays(1),
			"Acute pain", "Abnormal findings", "Acute condition", "Immediate treatment"
		);

		assertThat(newConsultation.getId()).isEqualTo(2);
		assertThat(newConsultation.getEncounter()).isEqualTo(encounter);
		assertThat(newConsultation.getTeams()).isEqualTo("Emergency Team");
		assertThat(newConsultation.getConsultationDate()).isEqualTo(testDateTime.plusDays(1));
		assertThat(newConsultation.getParentComplaints()).isEqualTo("Acute pain");
		assertThat(newConsultation.getPhysicalExamination()).isEqualTo("Abnormal findings");
		assertThat(newConsultation.getDiagnosis()).isEqualTo("Acute condition");
		assertThat(newConsultation.getInstructions()).isEqualTo("Immediate treatment");
	}

	@Test
	@DisplayName("Test getters and setters")
	void testGettersAndSetters() {
		assertThat(consultation.getId()).isEqualTo(1);
		assertThat(consultation.getEncounter()).isEqualTo(encounter);
		assertThat(consultation.getTeams()).isEqualTo("Cardiology, Neurology");
		assertThat(consultation.getConsultationDate()).isEqualTo(testDateTime);
		assertThat(consultation.getParentComplaints()).isEqualTo("Patient complains of chest pain and headache");
		assertThat(consultation.getPhysicalExamination()).isEqualTo("Normal heart sounds, no neurological deficits");
		assertThat(consultation.getDiagnosis()).isEqualTo("Hypertension, Tension headache");
		assertThat(consultation.getInstructions()).isEqualTo("Start antihypertensive medication, regular follow-up");
		assertThat(consultation.getLock()).isEqualTo(0);
	}

	@Test
	@DisplayName("Test compareTo")
	void testCompareTo() {
		HospitalizationConsultation lowerId = new HospitalizationConsultation();
		lowerId.setId(0);
		
		HospitalizationConsultation higherId = new HospitalizationConsultation();
		higherId.setId(2);
		
		assertThat(consultation.compareTo(lowerId)).isPositive();
		assertThat(consultation.compareTo(consultation)).isZero();
		assertThat(consultation.compareTo(higherId)).isNegative();
	}

	@Test
	@DisplayName("Test equals")
	void testEquals() {
		HospitalizationConsultation sameId = new HospitalizationConsultation();
		sameId.setId(1);
		
		HospitalizationConsultation differentId = new HospitalizationConsultation();
		differentId.setId(2);
		
		assertThat(consultation.equals(consultation)).isTrue();
		assertThat(consultation.equals(sameId)).isTrue();
		assertThat(consultation.equals(differentId)).isFalse();
		assertThat(consultation.equals(null)).isFalse();
		assertThat(consultation.equals("string")).isFalse();
	}

	@Test
	@DisplayName("Test hashCode")
	void testHashCode() {
		HospitalizationConsultation sameId = new HospitalizationConsultation();
		sameId.setId(1);
		
		HospitalizationConsultation differentId = new HospitalizationConsultation();
		differentId.setId(2);
		
		assertThat(consultation.hashCode()).isEqualTo(sameId.hashCode());
		assertThat(consultation.hashCode()).isNotEqualTo(differentId.hashCode());
		
		// Test hashCode consistency
		int firstHash = consultation.hashCode();
		int secondHash = consultation.hashCode();
		assertThat(firstHash).isEqualTo(secondHash);
	}

	@Test
	@DisplayName("Test null values handling")
	void testNullValuesHandling() {
		HospitalizationConsultation nullConsultation = new HospitalizationConsultation();
		
		assertThat(nullConsultation.getId()).isEqualTo(0);
		assertThat(nullConsultation.getEncounter()).isNull();
		assertThat(nullConsultation.getTeams()).isNull();
		assertThat(nullConsultation.getConsultationDate()).isNull();
		assertThat(nullConsultation.getParentComplaints()).isNull();
		assertThat(nullConsultation.getPhysicalExamination()).isNull();
		assertThat(nullConsultation.getDiagnosis()).isNull();
		assertThat(nullConsultation.getInstructions()).isNull();
		assertThat(nullConsultation.getLock()).isEqualTo(0);
	}
}
