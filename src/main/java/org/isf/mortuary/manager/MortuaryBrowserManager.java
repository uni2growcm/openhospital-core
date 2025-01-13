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
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
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
	public Death add(Death Mortuary) throws OHException {
		return mortuaryIoOperations.add(Mortuary);
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
	 * Updates an existing {@link Death}.
	 * @return {@link Death}.
	 * @throws OHServiceException
	 */
	public Death update(Death mortuary) throws OHServiceException {
		return mortuaryIoOperations.update(mortuary);
	}

	/**
	 * Delete the specified {@link Death}.
	 * @throws OHServiceException
	 */
	public void delete(Death mortuary) throws OHServiceException {
		mortuaryIoOperations.delete(mortuary);
	}

	/**
	 * Retrieves all the {@link Death}s.<br>
	 * <br>
	 * @param patientName the patient name.
	 * @param wardDescription the provenance ward.
	 * @param dateFrom the lower bound for the mortuary date range.
	 * @param dateTo the upper bound for the mortuary date range.
	 * @param deathReasonDescription the reason of death.
	 * @return the retrieved mortuaries.
	 * @throws OHServiceException
	 */
	public List<Death> getMortuariesWhereData(
		String patientName,
		String wardDescription,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		String deathReasonDescription
	) throws OHServiceException {
		return mortuaryIoOperations.getMortuariesWhereData(
			patientName,
			wardDescription,
			TimeTools.truncateToSeconds(dateFrom),
			TimeTools.truncateToSeconds(dateTo),
			deathReasonDescription
		);
	}

	/**
	 * Retrieves a page of {@link Death}s.<br>
	 * <br>
	 * @param patientName the patient name.
	 * @param provenance the provenance.
	 * @param dateFrom the lower bound for the mortuary date range.
	 * @param dateTo the upper bound for the mortuary date range.
	 * @param deathReason the reason of death.
	 * @param page current page.
	 * @param size the size of the page.
	 * @return the retrieved a mortuaries page.
	 * @throws OHServiceException
	 */
	public Page<Death> getMortuariesWhereDataPageable(
		String patientName,
		String provenance,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		String deathReason,
		int page,
		int size
	) throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size);
		return mortuaryIoOperations.getMortuariesWhereDataPageable(
			patientName,
			provenance,
			dateFrom,
			dateTo,
			deathReason,
			pageable
		);
	}

	/**
	 * Retrieves a page of {@link Death}s.<br>
	 * <br>
	 * @param patientName the patient name.
	 * @param dateFrom the lower bound for the mortuary date range.
	 * @param dateTo the upper bound for the mortuary date range.
	 * @param page current page.
	 * @param size the size of the page.
	 * @return the retrieved a mortuaries page.
	 * @throws OHServiceException
	 */
	public Page<Death> searchPatientByName(
		String patientName,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		int page,
		int size
	) throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size);
		return mortuaryIoOperations.findAllByPatientNameAndDateToDateFromPageable(
			patientName,
			dateFrom,
			dateTo,
			pageable
		);
	}
}
