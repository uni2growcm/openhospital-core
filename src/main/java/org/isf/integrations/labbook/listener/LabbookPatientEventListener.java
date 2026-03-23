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
package org.isf.integrations.labbook.listener;

import org.isf.integrations.labbook.exceptions.LabbookException;
import org.isf.integrations.labbook.services.LabbookAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestClientResponseException;

@Component
public class LabbookPatientEventListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(LabbookPatientEventListener.class);

	private final LabbookAPIService labbookAPIService;

	public LabbookPatientEventListener(LabbookAPIService labbookAPIService) {
		this.labbookAPIService = labbookAPIService;
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onPatientChange(PatientCreateEvent event) {
		try {
			labbookAPIService.createPatient(event.patient());
		} catch (RestClientResponseException ex) {
			throw new LabbookException(ex.getMessage(), ex.getStatusCode());
		}
	}
}
