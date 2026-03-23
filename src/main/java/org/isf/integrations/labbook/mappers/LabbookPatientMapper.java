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
package org.isf.integrations.labbook.mappers;

import org.isf.integrations.labbook.models.CreatePatientRequest;
import org.isf.patient.model.Patient;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class LabbookPatientMapper {
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

	public CreatePatientRequest toCreatePatientRequest(Patient ohPatient) {
		if (ohPatient == null) {
			return null;
		}

		CreatePatientRequest labbookPatient = new CreatePatientRequest();

		labbookPatient.setId_user(1);
		labbookPatient.setPat_code(ohPatient.getCode().toString());
		labbookPatient.setPat_name(ohPatient.getSecondName());
		labbookPatient.setPat_firstname(ohPatient.getFirstName());

		if (ohPatient.getBirthDate() != null) {
			labbookPatient.setPat_birth(ohPatient.getBirthDate().format(DATE_FORMATTER));
		}

		labbookPatient.setPat_age(ohPatient.getAge());
		labbookPatient.setPat_sex(mapSexToLabbook(ohPatient.getSex()));
		labbookPatient.setPat_address(ohPatient.getAddress());
		labbookPatient.setPat_city(ohPatient.getCity());
		labbookPatient.setPat_phone1(ohPatient.getTelephone());

		mapBloodType(ohPatient.getBloodType(), labbookPatient);

		return labbookPatient;
	}

	private Integer mapSexToLabbook(char ohSex) {
		return switch (ohSex) {
			case 'M' -> 1;
			case 'F' -> 2;
			default  -> 3;
		};
	}

	private void mapBloodType(String ohBloodType, CreatePatientRequest req) {
		if (ohBloodType == null || ohBloodType.trim().isEmpty()) {
			return;
		}

		String cleaned = ohBloodType.trim().toUpperCase();
		Integer patRhesus = cleaned.contains("+") ? 232 : (cleaned.contains("-") ? 233 : 0);

		if (cleaned.startsWith("AB")) {
			req.setPat_blood_group(903);
		} else if (cleaned.startsWith("B")) {
			req.setPat_blood_group(902);
		} else if (cleaned.startsWith("A")) {
			req.setPat_blood_group(901);
		} else if (cleaned.startsWith("O")) {
			req.setPat_blood_group(904);
		} else {
			req.setPat_blood_group(0);
		}
		req.setPat_blood_rhesus(patRhesus);
	}
}
