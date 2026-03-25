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
package org.isf.integrations.labbook.services;

import org.isf.integrations.labbook.config.LabBookBeanNames;
import org.isf.integrations.labbook.models.CreatePatientRequest;
import org.isf.integrations.labbook.models.GetPatientRequest;
import org.isf.integrations.labbook.ports.ICreatePatientService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Service
@ConditionalOnProperty(name = "labbook.enabled", havingValue = "true")
public class PatientService implements IPatientService{

	private final ICreatePatientService iCreatePatientService;

	public PatientService(@Qualifier(LabBookBeanNames.CREATE_PATIENT_CLIENT) ICreatePatientService iCreatePatientService) {
		this.iCreatePatientService = iCreatePatientService;
	}

	public void createPatient(CreatePatientRequest patient) throws RestClientResponseException {
		GetPatientRequest getPatientRequest = new GetPatientRequest();
		getPatientRequest.setTerm(patient.getPat_code());

		Object object = iCreatePatientService.getPatientsByCode(getPatientRequest);

		if (!(object instanceof List<?>)) {
			iCreatePatientService.createLabbookPatientRequest(0, patient);
		} else {
			CreatePatientRequest labbookPatientFound = (CreatePatientRequest) ((List<?>) object).get(0);
			labbookPatientFound.setPat_age(patient.getPat_age());
			labbookPatientFound.setPat_name(patient.getPat_name());
			labbookPatientFound.setPat_firstname(patient.getPat_firstname());
			labbookPatientFound.setPat_birth(patient.getPat_birth());
			labbookPatientFound.setPat_age(patient.getPat_age());
			labbookPatientFound.setPat_sex(patient.getPat_sex());
			labbookPatientFound.setPat_address(patient.getPat_address());
			labbookPatientFound.setPat_city(patient.getPat_city());
			labbookPatientFound.setPat_phone1(patient.getPat_phone1());
			labbookPatientFound.setPat_blood_group(patient.getPat_blood_group());
			labbookPatientFound.setPat_blood_rhesus(patient.getPat_blood_rhesus());
			iCreatePatientService.createLabbookPatientRequest(labbookPatientFound.getId_data(), patient);
		}
	}
}