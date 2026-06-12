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
import org.isf.hiv.model.HIVVisit;
import org.isf.hiv.model.HIVVisit.PCRResult;

public class TestHIVVisit {

	private final LocalDateTime visitDate = LocalDateTime.of(2024, 2, 10, 14, 30);
	private final Double weight = 5.5;
	private final Double height = 62.0;
	private final Double headCircumference = 40.5;
	private final Double temperature = 36.8;
	private final String clinicalStatus = "Good";
	private final String milestones = "Sitting with support";
	private final Double viralLoad = 5000.0;
	private final Integer cd4Count = 850;
	private final Integer cd4Percent = 28;
	private final Double hemoglobin = 11.5;
	private final Integer adherence = 95;
	private final String sideEffects = "None";
	private final LocalDate nextAppointmentDate = LocalDate.of(2024, 3, 10);
	private final String notes = "Test visit notes";

	public HIVVisit setup(HIVInfant infant, PCRResult pcrResult, boolean usingSet) {
		HIVVisit visit;

		if (usingSet) {
			visit = new HIVVisit();
			set(visit, infant, pcrResult);
		} else {
			visit = new HIVVisit(infant, visitDate);
			set(visit, infant, pcrResult);
		}

		return visit;
	}

	private void set(HIVVisit visit, HIVInfant infant, PCRResult pcrResult) {
		visit.setHivInfant(infant);
		visit.setVisitDate(visitDate);
		visit.setWeight(weight);
		visit.setHeight(height);
		visit.setHeadCircumference(headCircumference);
		visit.setTemperature(temperature);
		visit.setClinicalStatus(clinicalStatus);
		visit.setMilestones(milestones);
		visit.setPcrResult(pcrResult);
		visit.setViralLoad(viralLoad);
		visit.setCd4Count(cd4Count);
		visit.setCd4Percent(cd4Percent);
		visit.setHemoglobin(hemoglobin);
		visit.setAdherence(adherence);
		visit.setSideEffects(sideEffects);
		visit.setNextAppointmentDate(nextAppointmentDate);
		visit.setNotes(notes);
	}

	public void check(HIVVisit visit) {
		assertThat(visit.getVisitDate()).isCloseTo(visitDate, within(1, ChronoUnit.SECONDS));
		assertThat(visit.getWeight()).isEqualTo(weight);
		assertThat(visit.getHeight()).isEqualTo(height);
		assertThat(visit.getHeadCircumference()).isEqualTo(headCircumference);
		assertThat(visit.getTemperature()).isEqualTo(temperature);
		assertThat(visit.getClinicalStatus()).isEqualTo(clinicalStatus);
		assertThat(visit.getMilestones()).isEqualTo(milestones);
		assertThat(visit.getViralLoad()).isEqualTo(viralLoad);
		assertThat(visit.getCd4Count()).isEqualTo(cd4Count);
		assertThat(visit.getCd4Percent()).isEqualTo(cd4Percent);
		assertThat(visit.getHemoglobin()).isEqualTo(hemoglobin);
		assertThat(visit.getAdherence()).isEqualTo(adherence);
		assertThat(visit.getSideEffects()).isEqualTo(sideEffects);
		assertThat(visit.getNextAppointmentDate()).isEqualTo(nextAppointmentDate);
		assertThat(visit.getNotes()).isEqualTo(notes);
	}
}