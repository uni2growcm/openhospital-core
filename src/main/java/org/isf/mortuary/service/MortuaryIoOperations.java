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
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
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
	 * Store the specified {@link Death}.
	 * @param mortuary the death  to store.
	 * @return {@link Death} if the {@link Death} has been stored, null otherwise.
	 * @throws OHException if an error occurs during the store operation.
	 */
	public Death add(Death mortuary) throws OHException {
		return mortuaryRepository.save(mortuary);
	}

	/**
	 * Get all the {@link Death}s.
	 * @return a list of deaths.
	 * @throws OHException if an error occurs retrieving the deaths.
	 */
	public List<Death> getAll() throws OHException {
		return mortuaryRepository.findAll();
	}

	/**
	 * method that update an existing {@link Death} in the db
	 * @param mortuary - the {@link Death} to update
	 * @return {@link Death} has been updated
	 * @throws OHException
	 */
	public Death update(Death mortuary) throws OHException {
		return mortuaryRepository.save(mortuary);
	}

	/**
	 * method that delete a death
	 * @param mortuary
	 * @throws OHException
	 */
	public void delete(Death mortuary) throws OHException {
		mortuaryRepository.delete(mortuary);
	}

	/**
	 * Retrieves all the {@link Death}s with the specified criteria.<br>
	 * <br>
	 * @param patientName the patient name.
	 * @param provenance the provenance.
	 * @param dateFrom the lower bound for the mortuary date range.
	 * @param dateTo the upper bound for the mortuary date range.
	 * @param deathReason the reason of death.
	 * @param inputOrOutput the value that determines the date to be set in the interval.
	 * @return the retrieved mortuaries.
	 */
	public List<Death> getMortuariesWhereData(
		String patientName,
		String provenance,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		String deathReason,
		String inputOrOutput
	) {
		return mortuaryRepository.findAllWhereData(patientName, provenance, dateFrom, dateTo, deathReason, inputOrOutput);
	}

	/**
	 * Retrieves a page of {@link Death}s with the specified criteria.<br>
	 * <br>
	 * @param patientName the patient name.
	 * @param provenance the provenance.
	 * @param dateFrom the lower bound for the mortuary date range.
	 * @param dateTo the upper bound for the mortuary date range.
	 * @param deathReason the reason of death.
	 * @param inputOrOutput the value that determines the date to be set in the interval.
	 * @param pageable for pagination/.
	 * @return the retrieved a mortuaries page.
	 */
	public Page<Death> getMortuariesWhereDataPageable(
		String patientName,
		String provenance,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		int deathReason,
		Pageable pageable
	) {
		return mortuaryRepository.findAllWhereDatad(
			patientName,
			provenance,
			dateFrom,
			dateTo,
			deathReason,
			pageable
		);
	}

	/**
	 * Count all the {@link Death}s with the specified criteria.<br>
	 * <br>
	 * @param patientName the patient name.
	 * @param provenance the provenance.
	 * @param dateFrom the lower bound for the mortuary date range.
	 * @param dateTo the upper bound for the mortuary date range.
	 * @param deathReason the reason of death.
	 * @param inputOrOutput the value that determines the date to be set in the interval.
	 * @return the number of mortuary.
	 */
	public long countTotalMortuaries(
		String patientName,
		String provenance,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		String deathReason,
		String inputOrOutput
	) {
		if ((dateFrom != null) && (dateTo != null)) {
			dateFrom = dateFrom.withHour(0).withMinute(0);
			dateTo = dateTo.withHour(23).withMinute(59);
		}

		return mortuaryRepository.getCountTotalMortuaries(
			patientName,
			provenance,
			TimeTools.truncateToSeconds(dateFrom),
			TimeTools.truncateToSeconds(dateTo),
			deathReason,
			inputOrOutput
		);
	}

	public List<Death> findByPatientName(String name) {
		return mortuaryRepository.findByPatientName(name);
	}
	public Death save(Death mortuary) {
		return mortuaryRepository.save(mortuary);
	}
	public Death findById(int id) {
		return mortuaryRepository.findMortuaryById(id);
	}
}
