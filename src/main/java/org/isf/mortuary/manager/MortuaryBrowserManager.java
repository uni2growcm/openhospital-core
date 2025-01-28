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

package org.isf.mortuary.manager;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.mortuary.model.Death;
import org.isf.mortuary.service.MortuaryIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class MortuaryBrowserManager {

	private final MortuaryIoOperations mortuaryIoOperations;

	public MortuaryBrowserManager(MortuaryIoOperations mortuaryIoOperations) {
		this.mortuaryIoOperations = mortuaryIoOperations;
	}

	/**
	 * Get all the {@link Death}s.
	 * @return all the {@link Death}s.
	 * @throws OHServiceException
	 */
	public List<Death> getAll() throws OHServiceException {
		return mortuaryIoOperations.getAll();
	}

	/**
	 * Store the specified {@link Death}.
	 * @param death the death  to store.
	 * @return {@link Death} if the {@link Death} has been stored, null otherwise.
	 * @throws OHServiceException if an error occurs during the store operation.
	 */
	public Death add(Death death) throws OHServiceException {
		return mortuaryIoOperations.add(death);
	}

	/**
	 * Updates an existing {@link Death}
	 * @param death - the {@link Death} to update
	 * @return {@link Death} has been updated
	 * @throws OHServiceException
	 */
	public Death update(Death death) throws OHServiceException {
		return mortuaryIoOperations.update(death);
	}

	/**
	 * Delete {@link Death}
	 * @param death - the {@link Death} to delete
	 * @throws OHServiceException
	 */
	public void delete(Death death) throws OHServiceException {
		mortuaryIoOperations.delete(death);
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
	 * @param page current page.
	 * @param size the size of the page.
	 * @return the retrieved a mortuaries page.
	 * @throws OHServiceException
	 */
	public Page<Death> getMortuariesPageable(
		String patientName,
		String wardCode,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		String deathReasonCode,
		boolean isEnter,
		int page,
		int size
	) throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size);
		return mortuaryIoOperations.getMortuariesPageable(
			patientName,
			wardCode,
			dateFrom,
			dateTo,
			deathReasonCode,
			isEnter,
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
	 * @param page current page.
	 * @param size the size of the page.
	 * @return the retrieved a mortuaries page.
	 * @throws OHServiceException
	 */
	public Page<Death> getByPatientNameAndDates(
		String patientName,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		boolean isEnter,
		int page,
		int size
	) throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size);
		return mortuaryIoOperations.findAllByPatientNameAndDateToDateFromPageable(
			patientName,
			dateFrom,
			dateTo,
			isEnter,
			pageable
		);
	}
}
