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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * DTO representing a patient record returned by the LabBook API.
 *
 * <p>Maps the {@code Patient} schema defined in the LabBook OpenAPI specification
 * (endpoints {@code GET /services/patient/det/{idPat}} and {@code POST /services/patient/list}).
 *
 * @author Steve Tsala
 */
public record LabBookPatient(
	@JsonProperty("id_data") Integer idData,
	@JsonProperty("id_owner") Integer idOwner,
	String code,
	@JsonProperty("code_lab") String codeLab,
	String lastname,
	String firstname,
	String midname,
	String maidenname,
	LocalDate birth,
	@JsonProperty("birth_approx") Integer birthApprox,
	Integer sex,
	String phone1,
	String phone2,
	String email,
	String address,
	String zipcode,
	String city,
	String suburb,
	String job,
	String pbox,
	String resident,
	Integer lite,
	String agreement
) {

}
