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

import java.time.LocalDate;
import java.util.List;

import org.isf.hiv.model.HIVInfant;
import org.isf.hiv.model.HIVInfant.HIVInfantStatus;
import org.isf.hiv.model.HIVInfant.FeedingType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for HIV infant database operations.
 * This class handles all CRUD operations and data access for HIV-exposed infants.
 *
 * @author Open Hospital Team
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class HIVInfantIoOperations {

	@Autowired
	private HIVInfantIoOperationRepository repository;

	/**
	 * Saves a new HIV infant record to the database.
	 *
	 * @param infant the HIVInfant object to be saved
	 * @return the saved HIVInfant object with generated ID
	 * @throws OHServiceException if an error occurs during the save operation
	 */
	public HIVInfant saveInfant(HIVInfant infant) throws OHServiceException {
		return repository.save(infant);
	}

	/**
	 * Updates an existing HIV infant record in the database.
	 *
	 * @param infant the HIVInfant object containing updated data
	 * @return the updated HIVInfant object
	 * @throws OHServiceException if an error occurs during the update operation
	 */
	public HIVInfant updateInfant(HIVInfant infant) throws OHServiceException {
		return repository.save(infant);
	}

	/**
	 * Deletes an HIV infant record from the database.
	 * This operation will cascade delete all associated visits.
	 *
	 * @param infant the HIVInfant object to be deleted
	 * @throws OHServiceException if an error occurs during the delete operation
	 */
	public void deleteInfant(HIVInfant infant) throws OHServiceException {
		repository.delete(infant);
	}

	/**
	 * Finds an HIV infant record by its unique identifier.
	 *
	 * @param id the infant ID to search for
	 * @return the found HIVInfant object, or null if no infant exists with the given ID
	 * @throws OHServiceException if an error occurs during the search operation
	 */
	public HIVInfant findInfantById(Integer id) throws OHServiceException {
		return repository.findById(id).orElse(null);
	}

	/**
	 * Retrieves all HIV infants associated with a specific patient.
	 * A patient may have multiple HIV infant records (e.g., multiple pregnancies).
	 *
	 * @param patientCode the unique identifier of the parent patient
	 * @return a list of HIVInfant objects associated with the specified patient
	 * @throws OHServiceException if an error occurs during the search operation
	 */
	public List<HIVInfant> findInfantsByPatientCode(Integer patientCode) throws OHServiceException {
		return repository.findByPatient_Code(patientCode);
	}

	/**
	 * Retrieves all HIV infant records with pagination support.
	 *
	 * @param pageable pagination information (page number, page size, sorting)
	 * @return a Page object containing the requested page of HIVInfant objects
	 * @throws OHServiceException if an error occurs during the search operation
	 */
	public Page<HIVInfant> findAllInfants(Pageable pageable) throws OHServiceException {
		return repository.findAll(pageable);
	}

	/**
	 * Retrieves paginated HIV infant records filtered by follow-up status.
	 *
	 * @param status the follow-up status (ACTIVE, LOST, DECEASED, TRANSFERRED)
	 * @param pageable pagination information (page number, page size, sorting)
	 * @return a Page object containing the filtered HIVInfant objects
	 * @throws OHServiceException if an error occurs during the search operation
	 */
	public Page<HIVInfant> findInfantsByStatus(HIVInfantStatus status, Pageable pageable) throws OHServiceException {
		return repository.findByStatus(status, pageable);
	}

	/**
	 * Retrieves paginated HIV infant records using multiple search criteria.
	 * All parameters are optional - null values are ignored in the filter.
	 *
	 * @param patientCode filter by patient code (optional, null for all patients)
	 * @param status filter by follow-up status (optional, null for all statuses)
	 * @param feedingType filter by feeding type (optional, null for all types)
	 * @param dateFrom start of registration date range (inclusive, optional)
	 * @param dateTo end of registration date range (inclusive, optional)
	 * @param startDateFrom start of follow-up date range (inclusive, optional)
	 * @param startDateTo end of follow-up date range (inclusive, optional)
	 * @param pageable pagination information (page number, page size, sorting)
	 * @return a Page object containing the filtered HIVInfant objects
	 * @throws OHServiceException if an error occurs during the search operation
	 */
	public Page<HIVInfant> findInfantsByFilters(
		Integer patientCode,
		HIVInfantStatus status,
		FeedingType feedingType,
		LocalDate dateFrom,
		LocalDate dateTo,
		LocalDate startDateFrom,
		LocalDate startDateTo,
		Pageable pageable) throws OHServiceException {
		return repository.findByFilters(
			patientCode, status, feedingType,
			dateFrom, dateTo, startDateFrom, startDateTo, pageable);
	}
}