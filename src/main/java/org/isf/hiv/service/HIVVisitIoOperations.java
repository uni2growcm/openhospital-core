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
package org.isf.hiv.service;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.hiv.model.HIVVisit;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for HIV visit database operations.
 * This class handles all CRUD operations and data access for HIV follow-up visits.
 *
 * @author Open Hospital Team
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class HIVVisitIoOperations {

	@Autowired
	private HIVVisitIoOperationRepository repository;

	/**
	 * Saves a new HIV visit record to the database.
	 *
	 * @param visit the HIVVisit object to be saved
	 * @return the saved HIVVisit object with generated ID
	 * @throws OHServiceException if an error occurs during the save operation
	 */
	public HIVVisit saveVisit(HIVVisit visit) throws OHServiceException {
		return repository.save(visit);
	}

	/**
	 * Updates an existing HIV visit record in the database.
	 *
	 * @param visit the HIVVisit object containing updated data
	 * @return the updated HIVVisit object
	 * @throws OHServiceException if an error occurs during the update operation
	 */
	public HIVVisit updateVisit(HIVVisit visit) throws OHServiceException {
		return repository.save(visit);
	}

	/**
	 * Deletes an HIV visit record from the database.
	 *
	 * @param visit the HIVVisit object to be deleted
	 * @throws OHServiceException if an error occurs during the delete operation
	 */
	public void deleteVisit(HIVVisit visit) throws OHServiceException {
		repository.delete(visit);
	}

	/**
	 * Finds an HIV visit record by its unique identifier.
	 *
	 * @param id the visit ID to search for
	 * @return the found HIVVisit object, or null if no visit exists with the given ID
	 * @throws OHServiceException if an error occurs during the search operation
	 */
	public HIVVisit findVisitById(Integer id) throws OHServiceException {
		return repository.findById(id).orElse(null);
	}

	/**
	 * Retrieves all visits for a specific infant, ordered by visit date descending (most recent first).
	 *
	 * @param infantId the unique identifier of the HIV infant
	 * @return a list of HIVVisit objects associated with the specified infant
	 * @throws OHServiceException if an error occurs during the search operation
	 */
	public List<HIVVisit> findVisitsByInfantId(Integer infantId) throws OHServiceException {
		return repository.findByHivInfant_IdOrderByVisitDateDesc(infantId);
	}

	/**
	 * Retrieves a paginated list of visits for a specific infant.
	 * Useful for displaying large datasets in the user interface.
	 *
	 * @param infantId the unique identifier of the HIV infant
	 * @param pageable pagination information (page number, page size, sorting)
	 * @return a Page object containing the requested page of HIVVisit objects
	 * @throws OHServiceException if an error occurs during the search operation
	 */
	public Page<HIVVisit> findVisitsByInfantId(Integer infantId, Pageable pageable) throws OHServiceException {
		return repository.findByHivInfant_Id(infantId, pageable);
	}

	/**
	 * Retrieves visits for a specific infant within a specified date range.
	 * Both start and end dates are optional - if null, no boundary is applied.
	 *
	 * @param infantId the unique identifier of the HIV infant
	 * @param dateFrom the start date for the range (inclusive), can be null for no lower bound
	 * @param dateTo the end date for the range (inclusive), can be null for no upper bound
	 * @return a list of HIVVisit objects within the specified date range
	 * @throws OHServiceException if an error occurs during the search operation
	 */
	public List<HIVVisit> findVisitsByInfantIdAndDateRange(
		Integer infantId,
		LocalDateTime dateFrom,
		LocalDateTime dateTo) throws OHServiceException {
		return repository.findByInfantIdAndDateRange(infantId, dateFrom, dateTo);
	}
}