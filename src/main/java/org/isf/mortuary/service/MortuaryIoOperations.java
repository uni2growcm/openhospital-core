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
import java.util.List;

import org.isf.mortuary.model.Death;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class MortuaryIoOperations {

	private final MortuaryRepository mortuaryRepository;

	public MortuaryIoOperations(MortuaryRepository mortuaryRepository) {
		this.mortuaryRepository = mortuaryRepository;
	}

	/**
	 * Get all the {@link Death}s.
	 * @return a list of deaths.
	 * @throws OHServiceException if an error occurs retrieving the deaths.
	 */
	public List<Death> getAll() throws OHServiceException {
		return mortuaryRepository.findAll();
	}

	/**
	 * Store the specified {@link Death}.
	 * @param death the death  to store.
	 * @return {@link Death} if the {@link Death} has been stored, null otherwise.
	 * @throws OHServiceException if an error occurs during the store operation.
	 */
	public Death save(Death death) throws OHServiceException {
		return mortuaryRepository.save(death);
	}

	/**
	 * Updates an existing {@link Death}
	 * @param death - the {@link Death} to update
	 * @return {@link Death} has been updated
	 * @throws OHServiceException
	 */
	public Death update(Death death) throws OHServiceException {
		return mortuaryRepository.save(death);
	}

	/**
	 * Delete {@link Death}
	 * @param death - the {@link Death} to delete
	 * @throws OHServiceException
	 */
	public void delete(Death death) throws OHServiceException {
		mortuaryRepository.delete(death);
	}

	/**
	 * Find {@link Death} by the specific id.
	 * @return {@link Death}.
	 * @throws OHServiceException
	 */
	public Death findById(int id) throws OHServiceException {
		return mortuaryRepository.findById(id).orElse(null);
	}

	/**
	 * Retrieves a page of {@link Death}s.<br>
	 * <br>
	 * @param patientName the patient name.
	 * @param wardCode the code of provenance ward.
	 * @param dateFrom the lower bound for the mortuary date range.
	 * @param dateTo the upper bound for the mortuary date range.
	 * @param deathReasonCode the reason of death.
	 * @param isEnter to specify if it's admission date or discharge date
	 * @param pageable for pagination.
	 * @return the retrieved a mortuaries page.
	 * @throws OHServiceException
	 */
	public Page<Death> getMortuariesPages(
		String patientName,
		String wardCode,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		String deathReasonCode,
		boolean isEnter,
		Pageable pageable
	) throws OHServiceException {
		if (isEnter) {
			return mortuaryRepository.findAllByPatientNameContainsAndWardCodeContainsAndAdmissionDateBetweenAndDeathReasonCodeContains(
				patientName,
				wardCode,
				dateFrom,
				dateTo,
				deathReasonCode,
				pageable
			);
		}
		return mortuaryRepository.findAllByPatientNameContainsAndWardCodeContainsAndDischargeDateBetweenAndDeathReasonCodeContains(
			patientName,
			wardCode,
			dateFrom,
			dateTo,
			deathReasonCode,
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
			return mortuaryRepository.findAllByPatientNameContainsAndAdmissionDateBetween(patientName, dateFrom, dateTo, pageable);
		}
		return mortuaryRepository.findAllByPatientNameContainsAndDischargeDateBetween(patientName, dateFrom, dateTo, pageable);
	}
}
