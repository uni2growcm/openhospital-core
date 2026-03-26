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
package org.isf.integrations.labbook.ports;

import org.isf.integrations.labbook.models.LabBookPatient;
import org.isf.integrations.labbook.models.PatientDetRequest;
import org.isf.integrations.labbook.models.PatientDetResponse;
import org.isf.integrations.labbook.models.PatientListRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

/**
 * Declarative HTTP client for the LabBook patient API.
 *
 * <p>Covers the three patient endpoints scoped to this integration:
 * <ul>
 *   <li>{@code GET  /services/patient/det/{idPat}}     — retrieve a single patient</li>
 *   <li>{@code POST /services/patient/det/{idPat}}     — insert or update a patient</li>
 *   <li>{@code POST /services/patient/list}            — list / filter patients</li>
 * </ul>
 *
 * <p>The proxy is registered in {@code LabBookConfig} via {@code HttpServiceProxyFactory}
 * backed by the {@code labbookRestClient} bean, which transparently injects an OAuth2
 * Bearer token on every request.
 *
 * @author Steve Tsala
 */
@HttpExchange("/services/patient")
public interface IPatientService {

	/**
	 * Retrieves the details of a single patient from LabBook.
	 *
	 * @param id the LabBook patient identifier
	 * @return the patient record, or throws {@code RestClientException} on error
	 */
	@GetExchange("/det/{id}")
	LabBookPatient getPatient(@PathVariable Integer id);

	/**
	 * Inserts or updates a patient in LabBook.
	 *
	 * <p>When {@code id} corresponds to a new patient (i.e. no LabBook record exists yet),
	 * the API inserts it; otherwise it updates the existing record.
	 *
	 * @param id      the Open Hospital patient code used as LabBook identifier
	 * @param request the patient data payload
	 * @return the persisted patient record returned by LabBook
	 */
	@PostExchange(value = "/det/{id}")
	PatientDetResponse saveOrUpdatePatient(
		@PathVariable Integer id,
		@RequestBody PatientDetRequest request
	);

	/**
	 * Lists patients from LabBook, with optional filtering by code or lab code.
	 *
	 * @param request filter parameters ({@code code} and/or {@code code_lab} may be {@code null})
	 * @return the matching patient records
	 */
	@PostExchange("/list")
	List<LabBookPatient> listPatients(@RequestBody PatientListRequest request);
}
