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
package org.isf.integrations.labbook.services;

import org.isf.integrations.labbook.annotations.EnableLabBook;
import org.isf.integrations.labbook.config.LabBookBeanNames;
import org.isf.integrations.labbook.mappers.PatientMapper;
import org.isf.integrations.labbook.models.PatientDetResponse;
import org.isf.integrations.labbook.ports.IPatientService;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientCreatedOrUpdatedEvent;
import org.isf.patient.service.PatientIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

/**
 * {@inheritDoc}
 *
 * <p>Listens for {@link PatientCreatedOrUpdatedEvent} via {@link EventListener} and
 * delegates field mapping to {@link PatientMapper} before calling the LabBook API.
 */
@Service
@EnableLabBook
public class PatientSyncService implements IPatientSyncService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PatientSyncService.class);

	private final IPatientService patientService;
	private final PatientMapper patientMapper;
	private final PatientIoOperations patientIoOperations;

	public PatientSyncService(
		@Qualifier(LabBookBeanNames.PATIENT_SERVICE) IPatientService patientService,
		@Qualifier(LabBookBeanNames.PATIENT_MAPPER) PatientMapper patientMapper, PatientIoOperations patientIoOperations) {
		this.patientService = patientService;
		this.patientMapper = patientMapper;
		this.patientIoOperations = patientIoOperations;
	}

	@Override
	@EventListener
	public void onPatientCreatedOrUpdated(PatientCreatedOrUpdatedEvent event) {
		Patient patient = event.patient();
		if (patient == null || patient.getCode() == null) {
			LOGGER.warn("LabBook patient sync skipped: patient or code is null");
			return;
		}
		try {
			Integer labBookID = patient.getLabBookId() != null ? patient.getLabBookId() : (patient.getLabBookId() == null && event.isNew()) ? 0 : null;

			if (labBookID != null) {
				PatientDetResponse result =
					patientService.saveOrUpdatePatient(
						labBookID,
						patientMapper.toDetRequest(patient)
					);

				if (result != null) {
					if (patient.getLabBookId() != null) {
						return;
					}

					patient.setLabBookId(result.id());
					patientIoOperations.updatePatient(patient);
				}
			}

			LOGGER.debug("LabBook patient sync succeeded for patient code={} (isNew={})",
				patient.getCode(), true);
		} catch (RestClientException ex) {
			LOGGER.warn("LabBook patient sync failed for patient code={}: {}",
				patient.getCode(), ex.getMessage());
		} catch (OHServiceException e) {
			throw new RuntimeException(e);
		}
	}
}
