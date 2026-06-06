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
package org.isf.partner.manager;

import org.isf.partner.model.PatientPartner;
import org.isf.partner.service.PatientPartnerIoOperations;
import org.isf.patient.model.Patient;
import org.isf.partner.model.Partner;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class PatientPartnerBrowserManager {

	private final PatientPartnerIoOperations ioOperations;

	public PatientPartnerBrowserManager(PatientPartnerIoOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	/**
	 * Method that returns all active {@link PatientPartner} associations
	 * for the specified {@link Patient}.
	 *
	 * @param patient the patient whose active partnerships should be retrieved
	 * @return the list of active {@link PatientPartner}s
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public List<PatientPartner> getPatientPartners(Patient patient) throws OHServiceException {
		return ioOperations.getActiveByPatient(patient);
	}

	/**
	 * Method that associates a {@link Patient} with a {@link Partner}.
	 * <p>
	 * The association is validated before being created. If an active
	 * association between the same patient and partner already exists,
	 * a validation exception is thrown.
	 *
	 * @param patient the patient to associate
	 * @param partner the partner to associate
	 * @param startDate the start date of the association
	 * @param endDate the end date of the association, may be {@code null}
	 * @return the saved {@link PatientPartner}
	 * @throws OHServiceException if an error occurs while saving the association
	 */
	public PatientPartner associatePatientToPartner(Patient patient, Partner partner, LocalDate startDate, LocalDate endDate)
		throws OHServiceException {
		validateAssociation(patient, partner);

		List<PatientPartner> existing = ioOperations.getActiveByPatient(patient);
		for (PatientPartner pp : existing) {
			if (pp.getPartner().getId() == partner.getId()) {
				throw new OHDataValidationException(List.of(
					new OHExceptionMessage(MessageBundle.getMessage("angal.patientpartner.already.active.msg"))
				));
			}
		}

		PatientPartner patientPartner = new PatientPartner(patient, partner, startDate);
		patientPartner.setEndDate(endDate);
		patientPartner.setActive(1);

		return ioOperations.save(patientPartner);
	}

	/**
	 * Method that terminates an existing {@link PatientPartner} association.
	 * <p>
	 * If the end date is {@code null}, the current date is used.
	 *
	 * @param id the identifier of the association to terminate
	 * @param endDate the end date of the association
	 * @throws OHServiceException if an error occurs while updating the association
	 */
	public void endAssociation(int id, LocalDate endDate) throws OHServiceException {
		if (endDate == null) {
			endDate = LocalDate.now();
		}
		ioOperations.endPartnership(id, endDate);
	}

	/**
	 * Method that validates the data required to create a
	 * {@link PatientPartner} association.
	 *
	 * @param patient the patient to validate
	 * @param partner the partner to validate
	 * @throws OHDataValidationException if one or more validation errors occur
	 */
	private void validateAssociation(Patient patient, Partner partner) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (patient == null) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.patientpartner.validation.patient.required.msg")));
		}
		if (partner == null) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.patientpartner.validation.partner.required.msg")));
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}