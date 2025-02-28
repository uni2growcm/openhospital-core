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

package org.isf.mortuary.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.mortuary.model.Death;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class DeathIoOperations {

	private final DeathRepository deathRepository;

	public DeathIoOperations(DeathRepository mortuaryRepository) {
		this.deathRepository = mortuaryRepository;
	}

	/**
	 * Get all the {@link Death}s.
	 * @return a list of deaths.
	 * @throws OHServiceException if an error occurs retrieving the deaths.
	 */
	public List<Death> getAll() throws OHServiceException {
		return deathRepository.findAll();
	}

	/**
	 * Store the specified {@link Death}.
	 * @param death the death  to store.
	 * @return {@link Death} if the {@link Death} has been stored, null otherwise.
	 * @throws OHServiceException if an error occurs during the store operation.
	 */
	public Death add(Death death) throws OHServiceException {
		List<OHExceptionMessage> errors = validate(death);
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
		return deathRepository.save(death);
	}

	/**
	 * Updates an existing {@link Death}
	 * @param death - the {@link Death} to update
	 * @return {@link Death} has been updated
	 * @throws OHServiceException
	 */
	public Death update(Death death) throws OHServiceException {
		if (death == null) {
			throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuary.deathnotfound.msg")));
		}
		List<OHExceptionMessage> errors = validate(death);
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
		return deathRepository.save(death);
	}

	/**
	 * Delete {@link Death}
	 * @param death - the {@link Death} to delete
	 * @throws OHServiceException
	 */
	public void delete(Death death) throws OHServiceException {
		Death deathDeleted = findById(death.getId());
		if (deathDeleted == null) {
			throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuary.deathnotfound.msg")));
		}
		deathDeleted.setDeleted(true);
		deathRepository.save(deathDeleted);
	}

	/**
	 * Find {@link Death} by the specific id.
	 * @return {@link Death}.
	 * @throws OHServiceException
	 */
	public Death findById(int id) throws OHServiceException {
		return deathRepository.findByIdAndDeleted(id, false);
	}

	/**
	 * Retrieves a page of {@link Death}s.<br>
	 * <br>
	 * @param patientName the patient name.
	 * @param wardCode the code of provenance ward.
	 * @param dateFrom the lower bound for the mortuary date range.
	 * @param dateTo the upper bound for the mortuary date range.
	 * @param deathReasonTitle the title of death reason.
	 * @param isEnter to specify if it's admission date or discharge date
	 * @param pageable for pagination.
	 * @return the retrieved a mortuaries page.
	 * @throws OHServiceException
	 */
	public Page<Death> getMortuariesPageable(
		String patientName,
		String wardCode,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		String deathReasonTitle,
		boolean isEnter,
		Pageable pageable
	) throws OHServiceException {
		if (isEnter) {
			return deathRepository.findAllByPatientNameContainsAndWardCodeContainsAndAdmissionDateBetweenAndDeathReasonTitleContainsAndDeleted(
				patientName,
				wardCode,
				dateFrom,
				dateTo,
				deathReasonTitle,
				false,
				pageable
			);
		}
		return deathRepository.findAllByPatientNameContainsAndWardCodeContainsAndEstimatedDischargeDateBetweenAndDeathReasonTitleContainsAndDeleted(
			patientName,
			wardCode,
			dateFrom,
			dateTo,
			deathReasonTitle,
			false,
			pageable
		);
	}

	/**
	 * Retrieves a page of {@link Death}s.<br>
	 * <br>
	 * @param patientName the patient name.
	 * @param dateFrom the lower bound for the mortuary date range.
	 * @param dateTo the upper bound for the mortuary date range.
	 * @param isEnter to specify if it's admission date or discharge date
	 * @param pageable for pagination.
	 * @return the retrieved a mortuaries page.
	 * @throws OHServiceException
	 */
	public Page<Death> findAllByPatientNameAndDateToDateFromPageable(
		String patientName,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		boolean isEnter,
		Pageable pageable
	) throws OHServiceException {
		if (isEnter) {
			return deathRepository.findAllByPatientNameContainsAndAdmissionDateBetweenAndDeleted(patientName, dateFrom, dateTo, false, pageable);
		}
		return deathRepository.findAllByPatientNameContainsAndEstimatedDischargeDateBetweenAndDeleted(patientName, dateFrom, dateTo, false, pageable);
	}

	/**
	 * Find {@link Death} by patient code.
	 * @return {@link Death}.
	 * @throws OHServiceException
	 */
	public boolean exists(Death death) throws OHServiceException{
		if (death.getId() > 0){
			return deathRepository.existsByPatientCodeAndDeletedAndIdNot(death.getPatient().getCode(), false, death.getId());
		}
		return deathRepository.existsByPatientCodeAndDeleted(death.getPatient().getCode(), false);
	}

	private List<OHExceptionMessage> validate(Death death) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (exists(death)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuary.thispatientisalreadydeadpleaseselectanotherpatient.msg")));
			return errors;
		}
		if (death.getAdmissionDate().isAfter(death.getEstimatedDischargeDate())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuary.admissiondatemustbenotlaterthandischargedate.msg")));
		}
		if (death.getDate().isAfter(LocalDateTime.now())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuary.deathdatemustnotbelaterthantodaydate.msg")));
		}
		if (death.getAdmissionDate().isBefore(death.getDate())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuary.deathdatemustnotbelaterthanadmissiondate.msg")));
		}
		if (death.getAdmissionDate().isAfter(LocalDateTime.now())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuary.admissiondatemustbenotlaterthantodaydate.msg")));
		}

		return errors;
	}
}