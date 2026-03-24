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

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import org.isf.integrations.labbook.mappers.LabbookPatientMapper;
import org.isf.patient.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class LabbookPatientListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(LabbookPatientListener.class);

	private static ApplicationEventPublisher publisher;
	private final LabbookPatientMapper mapper;

	public LabbookPatientListener(LabbookPatientMapper mapper, ApplicationEventPublisher publisher) {
		this.mapper = mapper;
		LabbookPatientListener.publisher = publisher;
	}

	@PostPersist
	@PostUpdate
	public void afterPersist(Patient patient) {
		try {
			if (patient == null || patient.getCode() == null) return;
			publisher.publishEvent(
				new PatientCreateEvent(mapper.toCreatePatientRequest(patient))
			);
		} catch (Exception e) {
			LOGGER.error("Event publishing error", e);
		}
	}
}
