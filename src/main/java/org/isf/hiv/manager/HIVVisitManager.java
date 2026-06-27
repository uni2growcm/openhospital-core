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
package org.isf.hiv.manager;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.hiv.model.HIVVisit;
import org.isf.hiv.service.HIVVisitIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class HIVVisitManager {

	@Autowired
	private HIVVisitIoOperations ioOperations;

	/**
	 * Create a new HIV visit record.
	 *
	 * @param visit the HIV visit to create
	 * @return the saved HIV visit
	 * @throws OHServiceException if an error occurs during the save operation
	 */
	public HIVVisit newVisit(HIVVisit visit) throws OHServiceException {
		return ioOperations.saveVisit(visit);
	}

	/**
	 * Update an existing HIV visit record.
	 *
	 * @param visit the HIV visit to update
	 * @return the updated HIV visit
	 * @throws OHServiceException if an error occurs during the update operation
	 */
	public HIVVisit updateVisit(HIVVisit visit) throws OHServiceException {
		return ioOperations.updateVisit(visit);
	}

	/**
	 * Delete an HIV visit record.
	 *
	 * @param visit the HIV visit to delete
	 * @throws OHServiceException if an error occurs during the delete operation
	 */
	public void deleteVisit(HIVVisit visit) throws OHServiceException {
		ioOperations.deleteVisit(visit);
	}

	/**
	 * Find an HIV visit by its ID.
	 *
	 * @param id the visit ID
	 * @return the HIV visit if found, otherwise null
	 * @throws OHServiceException if an error occurs during the search
	 */
	public HIVVisit getVisitById(Integer id) throws OHServiceException {
		return ioOperations.findVisitById(id);
	}

	/**
	 * Get all visits for a specific infant, ordered by date descending.
	 *
	 * @param infantId the infant ID
	 * @return a list of visits for the given infant
	 * @throws OHServiceException if an error occurs during the search
	 */
	public List<HIVVisit> getVisitsByInfantId(Integer infantId) throws OHServiceException {
		return ioOperations.findVisitsByInfantId(infantId);
	}

	/**
	 * Get paginated list of visits for a specific infant.
	 *
	 * @param infantId the infant ID
	 * @param pageable pagination information (page number, size, sorting)
	 * @return a Page of visits for the given infant
	 * @throws OHServiceException if an error occurs during the search
	 */
	public Page<HIVVisit> getVisitsByInfantId(Integer infantId, Pageable pageable) throws OHServiceException {
		return ioOperations.findVisitsByInfantId(infantId, pageable);
	}

	/**
	 * Get visits for a specific infant within a date range.
	 *
	 * @param infantId the infant ID
	 * @param dateFrom the start date (inclusive, null for no lower bound)
	 * @param dateTo the end date (inclusive, null for no upper bound)
	 * @return a list of visits for the given infant within the date range
	 * @throws OHServiceException if an error occurs during the search
	 */
	public List<HIVVisit> getVisitsByInfantIdAndDateRange(
		Integer infantId,
		LocalDateTime dateFrom,
		LocalDateTime dateTo) throws OHServiceException {
		return ioOperations.findVisitsByInfantIdAndDateRange(infantId, dateFrom, dateTo);
	}
}