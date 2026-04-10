/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.integrations.labbook.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * DTO representing the raw patient data from LabBook.
 * Retrieved from {@code GET /services/patient/historic/{id}}.
 *
 * <p>Fields with {@code null} values are excluded from the serialized JSON via
 * {@link JsonInclude.Include#NON_NULL}.
 *
 * @author Duval Donfack
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LabbookRawPatient(

	@JsonProperty("id_data") Integer idData,

	@JsonProperty("id_user") Integer idUser,

	@JsonProperty("pat_ano") Integer ano,

	@JsonProperty("pat_code") String code,

	@JsonProperty("pat_code_lab") String codeLab,

	@JsonProperty("pat_name") String name,

	@JsonProperty("pat_firstname") String firstname,

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonProperty("pat_birth") LocalDate birth,

	@JsonProperty("pat_sex") Integer sex,

	@JsonProperty("pat_address") String address,

	@JsonProperty("pat_zipcode") String zipcode,

	@JsonProperty("pat_city") String city,

	@JsonProperty("pat_phone1") String phone1,

	@JsonProperty("pat_phone2") String phone2,

	@JsonProperty("pat_profession") String profession,

	@JsonProperty("pat_maiden") String maiden,

	@JsonProperty("pat_district") String district,

	@JsonProperty("pat_pbox") String pbox,

	@JsonProperty("pat_birth_approx") Integer birthApprox,

	@JsonProperty("pat_age") Integer age,

	@JsonProperty("pat_age_unit") Integer ageUnit,

	@JsonProperty("pat_email") String email,

	@JsonProperty("pat_agreement") String agreement,

	@JsonProperty("pat_midname") String midname,

	@JsonProperty("pat_nationality") Integer nationality,

	@JsonProperty("pat_resident") String resident,


	@JsonProperty("pat_blood_group") Integer bloodGroup,

	@JsonProperty("pat_blood_rhesus") Integer bloodRhesus

) {

}