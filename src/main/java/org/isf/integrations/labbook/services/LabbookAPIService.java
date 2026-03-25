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
import org.isf.integrations.labbook.ports.ILabbookService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Service
@ConditionalOnProperty(name = "labbook.enabled", havingValue = "true")
public class LabbookAPIService {

	private final ILabbookService iLabbookService;

	public LabbookAPIService(@Qualifier(LabBookBeanNames.CREATE_PATIENT_CLIENT) ILabbookService iLabbookService) {
		this.iLabbookService = iLabbookService;
	}

	public void createPatient(CreatePatientRequest patient) throws RestClientResponseException {
			iLabbookService.sendLabbookRequest(0, patient);
	}
}