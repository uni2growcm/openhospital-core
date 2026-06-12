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
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.isf.hiv.model.HIVInfant;
import org.isf.hiv.model.HIVInfant.HIVInfantStatus;
import org.isf.hiv.model.HIVInfant.FeedingType;
import org.isf.patient.model.Patient;

public class TestHIVInfant {

	private final LocalDateTime registrationDate = LocalDateTime.of(2024, 1, 15, 10, 30);
	private final Double birthWeight = 3.2;
	private final Integer gestationalAge = 38;
	private final FeedingType feedingType = FeedingType.MATERNAL;
	private final LocalDate followUpStartDate = LocalDate.of(2024, 1, 15);
	private final String notes = "Test infant notes";

	public HIVInfant setup(Patient patient, Patient mother, HIVInfantStatus status, boolean usingSet) {
		HIVInfant infant;

		if (usingSet) {
			infant = new HIVInfant();
			set(infant, patient, mother, status);
		} else {
			infant = new HIVInfant(patient, registrationDate, status, followUpStartDate);
			set(infant, patient, mother, status);
		}

		return infant;
	}

	private void set(HIVInfant infant, Patient patient, Patient mother, HIVInfantStatus status) {
		infant.setPatient(patient);
		infant.setMother(mother);
		infant.setRegistrationDate(registrationDate);
		infant.setStatus(status);
		infant.setFollowUpStartDate(followUpStartDate);
		infant.setBirthWeight(birthWeight);
		infant.setGestationalAge(gestationalAge);
		infant.setFeedingType(feedingType);
		infant.setNotes(notes);
	}

	public void check(HIVInfant infant) {
		assertThat(infant.getRegistrationDate()).isCloseTo(registrationDate, within(1, ChronoUnit.SECONDS));
		assertThat(infant.getBirthWeight()).isEqualTo(birthWeight);
		assertThat(infant.getGestationalAge()).isEqualTo(gestationalAge);
		assertThat(infant.getFeedingType()).isEqualTo(feedingType);
		assertThat(infant.getFollowUpStartDate()).isEqualTo(followUpStartDate);
		assertThat(infant.getNotes()).isEqualTo(notes);
	}
}