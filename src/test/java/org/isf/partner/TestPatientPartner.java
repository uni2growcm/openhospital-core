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
package org.isf.partner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.isf.partner.model.PatientPartner;
import org.isf.partner.model.Partner;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

public class TestPatientPartner {

	private LocalDate startDate = LocalDate.of(2024, 1, 1);
	private LocalDate endDate = null;

	public PatientPartner setup(Patient patient, Partner partner, boolean usingSet) throws OHException {
		PatientPartner patientPartner;
		if (usingSet) {
			patientPartner = new PatientPartner();
			setParameters(patientPartner, patient, partner);
		} else {
			patientPartner = new PatientPartner(patient, partner, startDate);
			patientPartner.setEndDate(endDate);
		}
		return patientPartner;
	}

	public void setParameters(PatientPartner patientPartner, Patient patient, Partner partner) {
		patientPartner.setPatient(patient);
		patientPartner.setPartner(partner);
		patientPartner.setStartDate(startDate);
		patientPartner.setEndDate(endDate);
		patientPartner.setActive(1);
	}

	public void check(PatientPartner patientPartner) {
		assertThat(patientPartner.getStartDate()).isEqualTo(startDate);
		assertThat(patientPartner.getEndDate()).isEqualTo(endDate);
		assertThat(patientPartner.getActive()).isEqualTo(1);
	}
}