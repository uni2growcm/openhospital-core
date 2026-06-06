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
package org.isf.partner.service;

import org.isf.partner.model.PatientPartner;
import org.isf.patient.model.Patient;
import org.isf.partner.model.Partner;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(rollbackFor = OHServiceException.class)
public class PatientPartnerIoOperations {

	private final PatientPartnerIoOperationRepository repository;

	public PatientPartnerIoOperations(PatientPartnerIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Returns all active {@link PatientPartner} associations for the specified
	 * {@link Patient}.
	 *
	 * @param patient the patient whose partnerships should be retrieved
	 * @return the list of associated {@link PatientPartner}s
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public List<PatientPartner> getByPatient(Patient patient) throws OHServiceException {
		return repository.findByPatientAndActive(patient, 1);
	}

	/**
	 * Returns all currently valid {@link PatientPartner} associations for the
	 * specified {@link Patient}.
	 * <p>
	 * A partnership is considered active when its validity period includes the
	 * current date.
	 *
	 * @param patient the patient whose active partnerships should be retrieved
	 * @return the list of active {@link PatientPartner}s
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public List<PatientPartner> getActiveByPatient(Patient patient) throws OHServiceException {
		return repository.findActiveByPatient(patient, LocalDate.now());
	}

	/**
	 * Returns all active {@link PatientPartner} associations for the specified
	 * {@link Partner}.
	 *
	 * @param partner the partner whose patient associations should be retrieved
	 * @return the list of associated {@link PatientPartner}s
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public List<PatientPartner> getByPartner(Partner partner) throws OHServiceException {
		return repository.findByPartnerAndActive(partner, 1);
	}

	/**
	 * Saves or updates a {@link PatientPartner} association.
	 *
	 * @param patientPartner the association to save or update
	 * @return the persisted {@link PatientPartner}
	 * @throws OHServiceException if an error occurs while saving the association
	 */
	public PatientPartner save(PatientPartner patientPartner) throws OHServiceException {
		return repository.save(patientPartner);
	}

	/**
	 * Terminates an existing partnership by setting its end date.
	 *
	 * @param id the identifier of the partnership to terminate
	 * @param endDate the date on which the partnership ends
	 * @throws OHServiceException if an error occurs while updating the partnership
	 */
	@Transactional
	public void endPartnership(int id, LocalDate endDate) throws OHServiceException {
		repository.endPartnership(id, endDate);
	}
}
