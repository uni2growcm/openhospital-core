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
 */
package org.isf.maternity.service;

import java.util.List;

import org.isf.maternity.model.Visit;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class VisitIoOperation {

	private final VisitIoOperationRepository repository;

	public VisitIoOperation(VisitIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Get all {@link Visit}s for a given pregnancy ordered by visit date.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @return a list of all visits linked to the pregnancy
	 * @throws OHServiceException if an error occurs retrieving visits
	 */
	public List<Visit> getVisitsByPregnancy(Integer pregnancyId) throws OHServiceException {
		return repository.findByPregnancyIdOrderByVisitDateAsc(pregnancyId);
	}

	/**
	 * Get prenatal {@link Visit}s for a pregnancy.
	 * Prenatal visits are those performed BEFORE delivery.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @return a list of prenatal visits
	 * @throws OHServiceException if an error occurs retrieving visits
	 */
	public List<Visit> getPrenatalVisits(Integer pregnancyId) throws OHServiceException {
		return repository.findPrenatalVisits(pregnancyId);
	}

	/**
	 * Get postnatal {@link Visit}s for a pregnancy.
	 * Postnatal visits are those performed AFTER delivery.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @return a list of postnatal visits
	 * @throws OHServiceException if an error occurs retrieving visits
	 */
	public List<Visit> getPostnatalVisits(Integer pregnancyId) throws OHServiceException {
		return repository.findPostnatalVisits(pregnancyId);
	}

	/**
	 * Add a new {@link Visit}.
	 *
	 * @param visit the visit to add
	 * @return the created visit
	 * @throws OHServiceException if an error occurs while saving
	 */
	public Visit newVisit(Visit visit) throws OHServiceException {
		return repository.save(visit);
	}

	/**
	 * Update an existing {@link Visit}.
	 *
	 * @param visit the visit to update
	 * @return the updated visit
	 * @throws OHServiceException if an error occurs while updating
	 */
	public Visit updateVisit(Visit visit) throws OHServiceException {
		return repository.save(visit);
	}

	/**
	 * Delete a {@link Visit}.
	 *
	 * @param visit the visit to delete
	 * @throws OHServiceException if an error occurs while deleting
	 */
	public void deleteVisit(Visit visit) throws OHServiceException {
		repository.delete(visit);
	}

	/**
	 * Check if visits exist for a given pregnancy.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @return true if visits exist, false otherwise
	 * @throws OHServiceException if an error occurs during check
	 */
	public boolean hasVisits(Integer pregnancyId) throws OHServiceException {
		return repository.existsByPregnancyId(pregnancyId);
	}
}