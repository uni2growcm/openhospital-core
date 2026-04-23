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

import java.time.LocalDateTime;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.maternity.model.PregnancyVisit;
import org.isf.maternity.service.PregnancyVisitIoOperation;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

@Component
public class PregnancyVisitBrowserManager {

	private final PregnancyVisitIoOperation ioOperation;

	public PregnancyVisitBrowserManager(PregnancyVisitIoOperation ioOperation) {
		this.ioOperation = ioOperation;
	}

	/**
	 * Get all visits for a pregnancy ordered by visit date.
	 *
	 * @param pregnancyId pregnancy identifier
	 * @return list of visits
	 * @throws OHServiceException if retrieval fails
	 */
	public List<PregnancyVisit> getVisitsByPregnancy(Integer pregnancyId) throws OHServiceException {
		return ioOperation.getVisitsByPregnancy(pregnancyId);
	}

	/**
	 * Get last visit of a pregnancy.
	 *
	 * @param pregnancyId pregnancy identifier
	 * @return latest visit if exists
	 * @throws OHServiceException if retrieval fails
	 */
	public PregnancyVisit getLastVisit(Integer pregnancyId) throws OHServiceException {
		return ioOperation.getLastVisit(pregnancyId)
			.orElse(null);
	}

	/**
	 * Count all visits of a pregnancy.
	 *
	 * @param pregnancyId pregnancy identifier
	 * @return number of visits
	 * @throws OHServiceException if retrieval fails
	 */
	public long countVisits(Integer pregnancyId) throws OHServiceException {
		return ioOperation.countVisits(pregnancyId);
	}

	/**
	 * Check if pregnancy has visits.
	 *
	 * @param pregnancyId pregnancy identifier
	 * @return true if visits exist
	 * @throws OHServiceException if check fails
	 */
	public boolean hasVisits(Integer pregnancyId) throws OHServiceException {
		return ioOperation.hasVisits(pregnancyId);
	}

	/**
	 * Get visits in date range.
	 *
	 * @param pregnancyId pregnancy identifier
	 * @param from start date
	 * @param to end date
	 * @return list of visits
	 * @throws OHServiceException if retrieval fails
	 */
	public List<PregnancyVisit> getVisitsByDateRange(
		Integer pregnancyId,
		LocalDateTime from,
		LocalDateTime to
	) throws OHServiceException {

		return ioOperation.getVisitsByDateRange(pregnancyId, from, to);
	}

	/**
	 * Get visits using filters (date range + visit type).
	 *
	 * @param pregnancyId pregnancy identifier
	 * @param fromDate start date
	 * @param toDate end date
	 * @param visitTypeId visit type id
	 * @return filtered visits
	 * @throws OHServiceException if retrieval fails
	 */
	public List<PregnancyVisit> getVisitsByFilters(
		Integer pregnancyId,
		LocalDateTime fromDate,
		LocalDateTime toDate,
		Integer visitTypeId
	) throws OHServiceException {

		return ioOperation.getVisitsByFilters(
			pregnancyId,
			fromDate,
			toDate,
			visitTypeId
		);
	}

	/**
	 * Get prenatal visits (before delivery).
	 *
	 * @param pregnancyId pregnancy identifier
	 * @param deliveryDate delivery date reference
	 * @return prenatal visits
	 * @throws OHServiceException if retrieval fails
	 */
	public List<PregnancyVisit> getPrenatalVisits(
		Integer pregnancyId,
		LocalDateTime deliveryDate
	) throws OHServiceException {

		return ioOperation.getPrenatalVisits(pregnancyId, deliveryDate);
	}

	/**
	 * Get postnatal visits (after delivery).
	 *
	 * @param pregnancyId pregnancy identifier
	 * @param deliveryDate delivery date reference
	 * @return postnatal visits
	 * @throws OHServiceException if retrieval fails
	 */
	public List<PregnancyVisit> getPostnatalVisits(
		Integer pregnancyId,
		LocalDateTime deliveryDate
	) throws OHServiceException {

		return ioOperation.getPostnatalVisits(pregnancyId, deliveryDate);
	}

	/**
	 * Create a new pregnancy visit.
	 *
	 * @param visit visit entity
	 * @return saved visit
	 * @throws OHServiceException if validation fails
	 */
	public PregnancyVisit newVisit(PregnancyVisit visit) throws OHServiceException {
		validateVisit(visit);
		return ioOperation.newVisit(visit);
	}

	/**
	 * Update a pregnancy visit.
	 *
	 * @param visit visit entity
	 * @return updated visit
	 * @throws OHServiceException if validation fails
	 */
	public PregnancyVisit updateVisit(PregnancyVisit visit) throws OHServiceException {
		validateVisit(visit);
		return ioOperation.updateVisit(visit);
	}

	/**
	 * Delete a pregnancy visit.
	 *
	 * @param visit visit entity
	 * @throws OHServiceException if deletion fails
	 */
	public void deleteVisit(PregnancyVisit visit) throws OHServiceException {
		ioOperation.deleteVisit(visit);
	}

	/**
	 * Validate visit before persistence.
	 *
	 * @param visit visit entity
	 * @throws OHServiceException if invalid
	 */
	private void validateVisit(PregnancyVisit visit) throws OHServiceException {

		if (visit == null) {
			throw new OHServiceException(
				new OHExceptionMessage(
					MessageBundle.getMessage("angal.maternity.visitcannotbenull.msg")
				)
			);
		}

		if (visit.getPregnancy() == null || visit.getPregnancy().getId() == null) {
			throw new OHServiceException(
				new OHExceptionMessage(
					MessageBundle.getMessage("angal.maternity.visitmustbelinkedtopregnancy.msg")
				)
			);
		}

		if (visit.getVisitDate() == null) {
			throw new OHServiceException(
				new OHExceptionMessage(
					MessageBundle.getMessage("angal.maternity.visitdaterequired.msg")
				)
			);
		}

		if (visit.getVisitDate().isAfter(LocalDateTime.now())) {
			throw new OHServiceException(
				new OHExceptionMessage(
					MessageBundle.getMessage("angal.maternity.visitdatecannotbeinfuture.msg")
				)
			);
		}
	}
}