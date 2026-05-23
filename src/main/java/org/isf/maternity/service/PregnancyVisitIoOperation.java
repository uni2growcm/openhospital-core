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
package org.isf.maternity.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.isf.generaldata.MessageBundle;
import org.isf.maternity.model.PregnancyVisit;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PregnancyVisitIoOperation {

	private final PregnancyVisitIoOperationRepository repository;

	public PregnancyVisitIoOperation(PregnancyVisitIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Get all {@link PregnancyVisit}s for a given pregnancy ordered by visit date.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @return a list of all visits linked to the pregnancy
	 * @throws OHServiceException if an error occurs retrieving visits
	 */
	public List<PregnancyVisit> getVisitsByPregnancy(Integer pregnancyId) throws OHServiceException {
		return repository.findByPregnancyIdOrderByVisitDateAsc(pregnancyId);
	}

	/**
	 * Get the last (most recent) {@link PregnancyVisit} for a pregnancy.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @return the latest visit if present
	 * @throws OHServiceException if an error occurs retrieving data
	 */
	public Optional<PregnancyVisit> getLastVisit(Integer pregnancyId) throws OHServiceException {
		return repository.findTopByPregnancyIdOrderByVisitDateDesc(pregnancyId);
	}

	/**
	 * Count all visits for a given pregnancy.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @return number of visits
	 * @throws OHServiceException if an error occurs during counting
	 */
	public long countVisits(Integer pregnancyId) throws OHServiceException {
		return repository.countByPregnancyId(pregnancyId);
	}

	/**
	 * Check if visits exist for a given pregnancy.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @return true if at least one visit exists
	 * @throws OHServiceException if an error occurs during check
	 */
	public boolean hasVisits(Integer pregnancyId) throws OHServiceException {
		return repository.existsByPregnancyId(pregnancyId);
	}

	/**
	 * Get visits for a pregnancy within a date range.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @param from start date (inclusive)
	 * @param to end date (inclusive)
	 * @return list of visits in the specified range
	 * @throws OHServiceException if an error occurs retrieving visits
	 */
	public List<PregnancyVisit> getVisitsByDateRange(
		Integer pregnancyId,
		LocalDateTime from,
		LocalDateTime to
	) throws OHServiceException {
		return repository.findByPregnancyIdAndVisitDateBetween(pregnancyId, from, to);
	}

	/**
	 * Get visits using advanced filters (date range + visit type).
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @param fromDate start date (nullable)
	 * @param toDate end date (nullable)
	 * @param visitTypeCode visit type identifier (nullable)
	 * @return filtered list of visits
	 * @throws OHServiceException if an error occurs retrieving visits
	 */
	public List<PregnancyVisit> getVisitsByFilters(
		Integer pregnancyId,
		LocalDateTime fromDate,
		LocalDateTime toDate,
		String visitTypeCode
	) throws OHServiceException {

		return repository.findVisitsByFilters(
			pregnancyId,
			fromDate,
			toDate,
			visitTypeCode
		);
	}

	/**
	 * Get prenatal {@link PregnancyVisit}s for a pregnancy.
	 * Prenatal visits are those performed before delivery.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @param deliveryDate the delivery date reference
	 * @return list of prenatal visits
	 * @throws OHServiceException if an error occurs retrieving visits
	 */
	public List<PregnancyVisit> getPrenatalVisits(
		Integer pregnancyId,
		LocalDateTime deliveryDate
	) throws OHServiceException {

		return repository.findPrenatalVisits(pregnancyId, deliveryDate);
	}

	/**
	 * Get postnatal {@link PregnancyVisit}s for a pregnancy.
	 * Postnatal visits are those performed after delivery.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @param deliveryDate the delivery date reference
	 * @return list of postnatal visits
	 * @throws OHServiceException if an error occurs retrieving visits
	 */
	public List<PregnancyVisit> getPostnatalVisits(
		Integer pregnancyId,
		LocalDateTime deliveryDate
	) throws OHServiceException {

		return repository.findPostnatalVisits(pregnancyId, deliveryDate);
	}

	/**
	 * Add a new {@link PregnancyVisit}.
	 *
	 * @param visit the visit to add
	 * @return the created visit
	 * @throws OHServiceException if an error occurs while saving
	 */
	public PregnancyVisit newVisit(PregnancyVisit visit) throws OHServiceException {
		return repository.save(visit);
	}

	/**
	 * Update an existing {@link PregnancyVisit}.
	 *
	 * @param visit the visit to update
	 * @return the updated visit
	 * @throws OHServiceException if an error occurs while updating
	 */
	public PregnancyVisit updateVisit(PregnancyVisit visit) throws OHServiceException {
		return repository.save(visit);
	}

	/**
	 * Delete a {@link PregnancyVisit}.
	 *
	 * @param visit the visit to delete
	 * @throws OHServiceException if an error occurs while deleting
	 */
	public void deleteVisit(PregnancyVisit visit) throws OHServiceException {
		repository.delete(visit);
	}

	/**
	 * Get a visit by its identifier.
	 *
	 * @param id visit identifier
	 * @return visit if found
	 * @throws OHServiceException if an error occurs retrieving visit
	 */
	public Optional<PregnancyVisit> getVisitById(Integer id) throws OHServiceException {
		return repository.findById(id);
	}

	/**
	 * Ensure that a visit belongs to a specific pregnancy.
	 *
	 * @param visitId visit identifier
	 * @param pregnancyId pregnancy identifier
	 * @return the validated visit
	 * @throws OHServiceException if visit not found or mismatch occurs
	 */
	public PregnancyVisit validateVisitBelongsToPregnancy(
		Integer visitId,
		Integer pregnancyId
	) throws OHServiceException {

		PregnancyVisit visit = repository.findById(visitId)
			.orElseThrow(() ->
				new OHServiceException(
					new OHExceptionMessage(
						MessageBundle.getMessage("angal.maternity.visitnotfound.msg") + ": " + visitId
					)
				)
			);

		if (!visit.getPregnancy().getId().equals(pregnancyId)) {
			throw new OHServiceException(
				new OHExceptionMessage(
					MessageBundle.getMessage("angal.maternity.visitdonotbelongtopregnancy.msg") + ": " + pregnancyId
				)
			);
		}

		return visit;
	}
}