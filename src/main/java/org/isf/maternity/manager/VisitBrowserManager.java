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
package org.isf.maternity.manager;

import java.util.List;

import org.isf.maternity.model.Visit;
import org.isf.maternity.service.VisitIoOperation;
import org.isf.utils.exception.OHServiceException;
import org.isf.vaccine.service.VaccineIoOperations;
import org.springframework.stereotype.Component;

/**
 * Manager for Pregnancy Visit operations.
 *
 * <p>
 * This class acts as a bridge between the GUI layer and the service (IoOperation)
 * layer. It contains business-level methods used by the presentation layer.
 * </p>
 *
 * Responsibilities:
 * <ul>
 *     <li>Retrieve pregnancy visits (ANC / PNC)</li>
 *     <li>Handle visit persistence operations</li>
 *     <li>Provide clean API for GUI/controllers</li>
 * </ul>
 */
@Component
public class VisitBrowserManager {

	private final VisitIoOperation ioOperation;

	public VisitBrowserManager(VisitIoOperation ioOperation) {
		this.ioOperation = ioOperation;
	}

	/**
	 * Get all visits for a pregnancy (complete timeline).
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @return list of visits ordered by date
	 * @throws OHServiceException if an error occurs
	 */
	public List<Visit> getVisitsByPregnancy(Integer pregnancyId) throws OHServiceException {
		return ioOperation.getVisitsByPregnancy(pregnancyId);
	}

	/**
	 * Get prenatal (ANC) visits for a pregnancy.
	 *
	 * Prenatal visits are all visits performed BEFORE delivery.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @return list of prenatal visits
	 * @throws OHServiceException if an error occurs
	 */
	public List<Visit> getPrenatalVisits(Integer pregnancyId) throws OHServiceException {
		return ioOperation.getPrenatalVisits(pregnancyId);
	}

	/**
	 * Get postnatal (PNC) visits for a pregnancy.
	 *
	 * Postnatal visits are all visits performed AFTER delivery.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @return list of postnatal visits
	 * @throws OHServiceException if an error occurs
	 */
	public List<Visit> getPostnatalVisits(Integer pregnancyId) throws OHServiceException {
		return ioOperation.getPostnatalVisits(pregnancyId);
	}

	/**
	 * Create a new pregnancy visit.
	 *
	 * @param visit the visit to create
	 * @return the saved visit
	 * @throws OHServiceException if an error occurs
	 */
	public Visit newVisit(Visit visit) throws OHServiceException {
		return ioOperation.newVisit(visit);
	}

	/**
	 * Update an existing pregnancy visit.
	 *
	 * @param visit the visit to update
	 * @return the updated visit
	 * @throws OHServiceException if an error occurs
	 */
	public Visit updateVisit(Visit visit) throws OHServiceException {
		return ioOperation.updateVisit(visit);
	}

	/**
	 * Delete a pregnancy visit.
	 *
	 * @param visit the visit to delete
	 * @throws OHServiceException if an error occurs
	 */
	public void deleteVisit(Visit visit) throws OHServiceException {
		ioOperation.deleteVisit(visit);
	}

	/**
	 * Check if a pregnancy has any visits recorded.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @return true if visits exist, false otherwise
	 * @throws OHServiceException if an error occurs
	 */
	public boolean hasVisits(Integer pregnancyId) throws OHServiceException {
		return ioOperation.hasVisits(pregnancyId);
	}
}