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

	public List<Death> getAll() throws OHException {
		return mortuaryIoOperations.getAll();
	}

	public Death update(Death mortuary) throws OHException {
		return mortuaryIoOperations.update(mortuary);
	}

	public void delete(Death mortuary) throws OHException {
		mortuaryIoOperations.delete(mortuary);
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
		return mortuaryIoOperations.getMortuariesWhereData(
			patientName,
			provenance,
			TimeTools.truncateToSeconds(dateFrom),
			TimeTools.truncateToSeconds(dateTo),
			deathReason,
			inputOrOutput
		);
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
	 * @param page current page.
	 * @param size the size of the page.
	 * @return the retrieved a mortuaries page.
	 */
	public Page<Death> getMortuariesWhereDataPageable(
		String patientName,
		String provenance,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		int deathReason,
		int page,
		int size
	) {
		Pageable pageable = PageRequest.of(page, size);
		System.out.println(mortuaryIoOperations.findByPatientName(patientName).get(0).getPatient().getName());
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
	) throws OHServiceException {
		return mortuaryIoOperations.countTotalMortuaries(
			patientName,
			provenance,
			TimeTools.truncateToSeconds(dateFrom),
			TimeTools.truncateToSeconds(dateTo),
			deathReason,
			inputOrOutput
		);
	}
}
