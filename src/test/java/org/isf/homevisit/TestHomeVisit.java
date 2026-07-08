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
package org.isf.homevisit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.isf.homevisit.model.HomeVisit;
import org.isf.homevisit.model.HomeVisitStatus;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

public class TestHomeVisit {

	private LocalDateTime visitStartDate = LocalDateTime.of(2026, 12, 15, 10, 0, 0);
	private LocalDateTime visitEndDate = null;
	private HomeVisitStatus status = HomeVisitStatus.PLANNED;
	private String purpose = "Test purpose";
	private String clinicalNotes = "Test clinical notes";
	private String observations = "Test observations";
	private String address = "123 Test Street";
	private String contactPhone = "+237 690001234";
	private LocalDateTime nextVisitDate = LocalDateTime.of(2026, 12, 22, 10, 0, 0);

	public HomeVisit setup(Patient patient, boolean usingSet) throws OHException {
		HomeVisit homeVisit;
		if (usingSet) {
			homeVisit = new HomeVisit();
			setParameters(homeVisit, patient);
		} else {
			homeVisit = new HomeVisit(patient, visitStartDate);
			homeVisit.setStatus(status);
			homeVisit.setPurpose(purpose);
			homeVisit.setClinicalNotes(clinicalNotes);
			homeVisit.setObservations(observations);
			homeVisit.setAddress(address);
			homeVisit.setContactPhone(contactPhone);
			homeVisit.setNextVisitDate(nextVisitDate);
			homeVisit.setActive(1);
		}
		return homeVisit;
	}

	public void setParameters(HomeVisit homeVisit, Patient patient) {
		homeVisit.setPatient(patient);
		homeVisit.setVisitStartDate(visitStartDate);
		homeVisit.setVisitEndDate(visitEndDate);
		homeVisit.setStatus(status);
		homeVisit.setPurpose(purpose);
		homeVisit.setClinicalNotes(clinicalNotes);
		homeVisit.setObservations(observations);
		homeVisit.setAddress(address);
		homeVisit.setContactPhone(contactPhone);
		homeVisit.setNextVisitDate(nextVisitDate);
		homeVisit.setActive(1);
	}

	public void check(HomeVisit homeVisit) {
		assertThat(homeVisit.getVisitStartDate()).isEqualTo(visitStartDate);
		assertThat(homeVisit.getVisitEndDate()).isEqualTo(visitEndDate);
		assertThat(homeVisit.getStatus()).isEqualTo(status);
		assertThat(homeVisit.getPurpose()).isEqualTo(purpose);
		assertThat(homeVisit.getClinicalNotes()).isEqualTo(clinicalNotes);
		assertThat(homeVisit.getObservations()).isEqualTo(observations);
		assertThat(homeVisit.getAddress()).isEqualTo(address);
		assertThat(homeVisit.getContactPhone()).isEqualTo(contactPhone);
		assertThat(homeVisit.getNextVisitDate()).isEqualTo(nextVisitDate);
		assertThat(homeVisit.getActive()).isEqualTo(1);
	}
}