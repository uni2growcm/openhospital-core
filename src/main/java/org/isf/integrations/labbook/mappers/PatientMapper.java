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

import org.isf.integrations.labbook.annotations.EnableLabBook;
import org.isf.integrations.labbook.config.LabBookBeanNames;
import org.isf.integrations.labbook.models.PatientDetRequest;
import org.isf.patient.model.Patient;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Maps Open Hospital domain objects to LabBook API request models.
 *
 * <p>This component is only active when the LabBook integration is enabled
 * ({@code labbook.enabled=true}).
 *
 * <h3>Patient field mapping</h3>
 * <table border="1">
 *   <tr><th>OH field</th><th>LabBook field</th><th>Notes</th></tr>
 *   <tr><td>{@code patient.getCode()}</td><td>{@code pat_code}</td><td>Converted to String</td></tr>
 *   <tr><td>{@code patient.getSecondName()}</td><td>{@code pat_name}</td><td>Last name</td></tr>
 *   <tr><td>{@code patient.getFirstName()}</td><td>{@code pat_firstname}</td><td></td></tr>
 *   <tr><td>{@code patient.getBirthDate()}</td><td>{@code pat_birth}</td><td></td></tr>
 *   <tr><td>{@code patient.getSex()} ({@code 'M'}/{@code 'F'})</td><td>{@code pat_sex}</td><td>1=Male, 2=Female, 3=Unknown</td></tr>
 *   <tr><td>{@code patient.getAddress()}</td><td>{@code pat_address}</td><td></td></tr>
 *   <tr><td>{@code patient.getCity()}</td><td>{@code pat_city}</td><td></td></tr>
 *   <tr><td>{@code patient.getTelephone()}</td><td>{@code pat_phone1}</td><td></td></tr>
 *   <tr><td>{@code patient.getProfession()}</td><td>{@code pat_profession}</td><td></td></tr>
 *   <tr><td>{@code patient.getMotherName()}</td><td>{@code pat_maiden}</td><td></td></tr>
 *   <tr><td>{@code patient.getAge()}</td><td>{@code pat_age}</td><td></td></tr>
 * </table>
 *
 * @author Steve Tsala
 */
@EnableLabBook
@Component(LabBookBeanNames.PATIENT_MAPPER)
public class PatientMapper {

	/**
	 * LabBook sex code: Male
	 */
	private static final int SEX_MALE = 1;
	/**
	 * LabBook sex code: Female
	 */
	private static final int SEX_FEMALE = 2;
	/**
	 * LabBook sex code: Unknown / unspecified
	 */
	private static final int SEX_UNKNOWN = 3;

	public static Map.Entry<Integer, Integer> parseBloodType(String bloodType) {
		if (bloodType == null || bloodType.trim().isEmpty()) {
			return null;
		}

		String input = bloodType.trim().toUpperCase();

		// Extract the Rh factor (+ or -) if present
		int rhesus = 0; // default negative
		if (input.endsWith("+")) {
			rhesus = 1;
			input = input.substring(0, input.length() - 1).trim();
		} else if (input.endsWith("-")) {
			rhesus = 0;
			input = input.substring(0, input.length() - 1).trim();
		}

		// Map blood group to number
		int group;
		switch (input) {
			case "O":
				group = 0;
				break;
			case "A":
				group = 1;
				break;
			case "B":
				group = 2;
				break;
			case "AB":
				group = 3;
				break;
			default:
				throw new IllegalArgumentException("Invalid blood group: " + input
					+ ". Valid groups are: O, A, B, AB");
		}

		return new AbstractMap.SimpleEntry<>(group, rhesus);
	}

	/**
	 * Maps an Open Hospital {@link Patient} to a {@link PatientDetRequest}.
	 *
	 * <p>The {@code id_user} field is intentionally left {@code null} — LabBook derives
	 * the acting user from the OAuth2 Bearer token. Fields without a direct OH equivalent
	 * (e.g. {@code pat_zipcode}, {@code pat_blood_group}) are also left {@code null} and
	 * omitted from the serialised JSON via {@code @JsonInclude(NON_NULL)}.
	 *
	 * @param patient the persisted OH patient; must not be {@code null}
	 * @return a fully populated {@link PatientDetRequest} ready to send to the LabBook API
	 */
	public PatientDetRequest toDetRequest(Patient patient) {
		var bloodType = patient.getBloodType();

		var result = parseBloodType(bloodType);

		var bloodGroup = result == null ? null : result.getKey();
		var bloodRhesus = result == null ? null : result.getValue();

		return new PatientDetRequest(
			/* idUser         */ 1, // If oh users are synced with labbook users, then this could be replaced by the current logged-in user
			/* ano            */ null,
			/* code           */ String.valueOf(patient.getCode()),
			/* codeLab        */ null,
			/* name           */ patient.getSecondName(),
			/* firstname      */ patient.getFirstName(),
			/* birth          */ patient.getBirthDate(),
			/* sex            */ mapSex(patient.getSex()),
			/* address        */ patient.getAddress(),
			/* zipcode        */ null,
			/* city           */ patient.getCity(),
			/* phone1         */ patient.getTelephone(),
			/* phone2         */ null,
			/* profession     */ patient.getProfession(),
			/* maiden         */ patient.getMotherName(),
			/* district       */ null,
			/* pbox           */ null,
			/* birthApprox    */ null,
			/* age            */ patient.getAge(),
			/* ageUnit        */ null,
			/* midname        */ null,
			/* nationality    */ null,
			/* resident       */ null,
			/* bloodGroup     */ bloodGroup,
			/* bloodRhesus    */ bloodRhesus,
			/* email          */ null,
			/* agreement      */ null
		);
	}

	/**
	 * Converts an Open Hospital sex character to the LabBook integer sex code.
	 *
	 * @param sex {@code 'M'} or {@code 'm'} for male, {@code 'F'} or {@code 'f'} for female,
	 *            anything else maps to unknown
	 * @return 1 (male), 2 (female), or 3 (unknown)
	 */
	private int mapSex(char sex) {
		return switch (sex) {
			case 'M', 'm' -> SEX_MALE;
			case 'F', 'f' -> SEX_FEMALE;
			default -> SEX_UNKNOWN;
		};
	}
}
