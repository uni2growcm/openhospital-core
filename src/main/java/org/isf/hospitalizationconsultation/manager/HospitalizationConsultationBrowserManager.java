/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.hospitalizationconsultation.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.encounter.model.Encounter;
import org.isf.hospitalizationconsultation.model.HospitalizationConsultation;
import org.isf.hospitalizationconsultation.service.HospitalizationConsultationIoOperations;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.pagination.PagedResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class HospitalizationConsultationBrowserManager {

	private final HospitalizationConsultationIoOperations ioOperations;

	public HospitalizationConsultationBrowserManager(HospitalizationConsultationIoOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param consultation
	 * @param insert {@code true} for insert, {@code false} for update
	 * @throws OHDataValidationException
	 */
	protected void validateConsultation(HospitalizationConsultation consultation, boolean insert) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		// Check Encounter
		Encounter encounter = consultation.getEncounter();
		if (encounter == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospitalizationconsultation.encountercannotbeempty.msg")));
		}

		// Check Date/Time
		LocalDateTime dateTime = consultation.getDateTime();
		if (dateTime == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospitalizationconsultation.datetimecannotbeempty.msg")));
		}

		// Set user ID if not provided
		if (consultation.getCreatedBy() == null && insert) {
			consultation.setCreatedBy(UserBrowsingManager.getCurrentUser());
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Returns all HospitalizationConsultations.
	 *
	 * @return the list of HospitalizationConsultations.
	 * @throws OHServiceException
	 */
	public List<HospitalizationConsultation> getHospitalizationConsultations() throws OHServiceException {
		return ioOperations.getHospitalizationConsultations();
	}

	/**
	 * Returns all HospitalizationConsultations for a specific Encounter.
	 *
	 * @param encounter the Encounter
	 * @return the list of HospitalizationConsultations for the Encounter.
	 * @throws OHServiceException
	 */
	public List<HospitalizationConsultation> getHospitalizationConsultationsByEncounter(Encounter encounter) throws OHServiceException {
		return ioOperations.getHospitalizationConsultationsByEncounter(encounter);
	}

	/**
	 * Returns all HospitalizationConsultations within a date range.
	 *
	 * @param dateFrom start date
	 * @param dateTo end date
	 * @return the list of HospitalizationConsultations within the date range.
	 * @throws OHServiceException
	 */
	public List<HospitalizationConsultation> getHospitalizationConsultationsByDateRange(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return ioOperations.getHospitalizationConsultationsByDateRange(dateFrom, dateTo);
	}

	/**
	 * Returns the HospitalizationConsultation with the specified id.
	 *
	 * @param id the consultation id
	 * @return the HospitalizationConsultation with the specified id, {@code null} otherwise.
	 * @throws OHServiceException
	 */
	public HospitalizationConsultation getHospitalizationConsultation(int id) throws OHServiceException {
		return ioOperations.getHospitalizationConsultation(id);
	}

	/**
	 * Inserts a new HospitalizationConsultation.
	 *
	 * @param consultation the consultation to insert
	 * @return the inserted consultation
	 * @throws OHServiceException
	 */
	public HospitalizationConsultation newHospitalizationConsultation(HospitalizationConsultation consultation) throws OHServiceException {
		validateConsultation(consultation, true);
		return ioOperations.newHospitalizationConsultation(consultation);
	}

	/**
	 * Updates the specified HospitalizationConsultation.
	 *
	 * @param consultation the consultation to update
	 * @return the updated consultation
	 * @throws OHServiceException
	 */
	public HospitalizationConsultation updateHospitalizationConsultation(HospitalizationConsultation consultation) throws OHServiceException {
		validateConsultation(consultation, false);
		return ioOperations.updateHospitalizationConsultation(consultation);
	}

	/**
	 * Deletes a HospitalizationConsultation.
	 *
	 * @param consultation the consultation to delete
	 * @throws OHServiceException
	 */
	public void deleteHospitalizationConsultation(HospitalizationConsultation consultation) throws OHServiceException {
		ioOperations.deleteHospitalizationConsultation(consultation);
	}

	/**
	 * Returns a paginated list of HospitalizationConsultations.
	 *
	 * @param page the page number
	 * @param size the page size
	 * @return the paginated list of HospitalizationConsultations
	 * @throws OHServiceException
	 */
	public PagedResponse<HospitalizationConsultation> getHospitalizationConsultationsPageable(int page, int size) throws OHServiceException {
		return ioOperations.getHospitalizationConsultationsPageable(PageRequest.of(page, size));
	}

	/**
	 * Returns a paginated list of HospitalizationConsultations for a specific Encounter.
	 *
	 * @param encounter the Encounter
	 * @param page the page number
	 * @param size the page size
	 * @return the paginated list of HospitalizationConsultations for the Encounter
	 * @throws OHServiceException
	 */
	public PagedResponse<HospitalizationConsultation> getHospitalizationConsultationsByEncounterPageable(Encounter encounter, int page, int size) throws OHServiceException {
		return ioOperations.getHospitalizationConsultationsByEncounterPageable(encounter, PageRequest.of(page, size));
	}

	/**
	 * Returns a paginated list of HospitalizationConsultations within a date range.
	 *
	 * @param dateFrom start date
	 * @param dateTo end date
	 * @param page the page number
	 * @param size the page size
	 * @return the paginated list of HospitalizationConsultations within the date range
	 * @throws OHServiceException
	 */
	public PagedResponse<HospitalizationConsultation> getHospitalizationConsultationsByDateRangePageable(LocalDateTime dateFrom, LocalDateTime dateTo, int page, int size) throws OHServiceException {
		return ioOperations.getHospitalizationConsultationsByDateRangePageable(dateFrom, dateTo, PageRequest.of(page, size));
	}
}
