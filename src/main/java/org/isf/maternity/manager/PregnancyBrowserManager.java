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
package org.isf.maternity.manager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.isf.generaldata.MessageBundle;
import org.isf.maternity.model.Pregnancy;
import org.isf.maternity.service.PregnancyIoOperation;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.pagination.PageInfo;
import org.isf.utils.pagination.PagedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PregnancyBrowserManager {

	private final PregnancyIoOperation ioOperation;

	public PregnancyBrowserManager(PregnancyIoOperation ioOperation) {
		this.ioOperation = ioOperation;
	}

	/**
	 * Create a new pregnancy record.
	 *
	 * @param pregnancy the pregnancy entity
	 * @return created pregnancy
	 * @throws OHServiceException if creation fails
	 */
	public Pregnancy newPregnancy(Pregnancy pregnancy) throws OHServiceException {
		return ioOperation.newPregnancy(pregnancy);
	}

	/**
	 * Update an existing pregnancy record.
	 *
	 * @param pregnancy the pregnancy entity
	 * @return updated pregnancy
	 * @throws OHServiceException if update fails
	 */
	public Pregnancy updatePregnancy(Pregnancy pregnancy) throws OHServiceException {
		return ioOperation.updatePregnancy(pregnancy);
	}

	/**
	 * Delete a pregnancy record.
	 *
	 * @param pregnancy the pregnancy entity
	 * @throws OHServiceException if deletion fails
	 */
	public void deletePregnancy(Pregnancy pregnancy) throws OHServiceException {
		ioOperation.deletePregnancy(pregnancy);
	}

	// =========================================================
	// READ OPERATIONS
	// =========================================================

	/**
	 * Get pregnancies for a specific patient.
	 *
	 * @param patientCode patient identifier
	 * @return list of pregnancies
	 * @throws OHServiceException if retrieval fails
	 */
	public List<Pregnancy> getPregnanciesByPatient(Integer patientCode)
		throws OHServiceException {
		return ioOperation.getPregnanciesByPatient(patientCode);
	}

	/**
	 * Advanced pregnancy search with filters.
	 *
	 * @param patientCode patient filter (nullable)
	 * @param status pregnancy status (nullable)
	 * @param riskLevel risk level (nullable)
	 * @param fromDate start date (nullable)
	 * @param toDate end date (nullable)
	 * @param pageable pagination info
	 * @return paginated results
	 * @throws OHServiceException if search fails
	 */
	public Page<Pregnancy> searchPregnancies(
		Integer patientCode,
		String status,
		String riskLevel,
		LocalDateTime fromDate,
		LocalDateTime toDate,
		Pageable pageable
	) throws OHServiceException {
		return ioOperation.searchPregnancies(
			patientCode,
			status,
			riskLevel,
			fromDate,
			toDate,
			pageable
		);
	}

	/**
	 * Get pregnancies created in a date range.
	 *
	 * @param fromDate start date
	 * @param toDate end date
	 * @param pageable pagination
	 * @return paginated pregnancies
	 * @throws OHServiceException if retrieval fails
	 */
	public Page<Pregnancy> getPregnanciesByDateRange(
		LocalDateTime fromDate,
		LocalDateTime toDate,
		Pageable pageable
	) throws OHServiceException {
		return ioOperation.getPregnanciesByDateRange(fromDate, toDate, pageable);
	}

	/**
	 * Get latest pregnancy for a patient and status.
	 *
	 * @param patientCode patient identifier
	 * @param status pregnancy status
	 * @return latest pregnancy or null
	 * @throws OHServiceException if retrieval fails
	 */
	public Pregnancy getLatestPregnancyByPatientAndStatus(
		Integer patientCode,
		String status
	) throws OHServiceException {
		return ioOperation.getLatestPregnancyByPatientAndStatus(patientCode, status);
	}

	/**
	 * Count pregnancies by patient and status.
	 *
	 * @param patientCode patient identifier
	 * @param status pregnancy status
	 * @return number of pregnancies
	 * @throws OHServiceException if operation fails
	 */
	public long countPregnanciesByPatientAndStatus(
		Integer patientCode,
		String status
	) throws OHServiceException {
		return ioOperation.countPregnanciesByPatientAndStatus(patientCode, status);
	}

	/**
	 * Check if a patient has an active pregnancy.
	 *
	 * @param patientCode patient identifier
	 * @return true if active pregnancy exists
	 * @throws OHServiceException if check fails
	 */
	public boolean hasActivePregnancy(Integer patientCode)
		throws OHServiceException {
		return ioOperation.hasActivePregnancy(patientCode);
	}

	/**
	 * Close a pregnancy (mark as Completed or Terminated).
	 *
	 * @param pregnancyId pregnancy identifier
	 * @param status new status
	 * @return updated pregnancy
	 * @throws OHServiceException if update fails
	 */
	public Pregnancy closePregnancy(Integer pregnancyId, String status)
		throws OHServiceException {
		return ioOperation.closePregnancy(pregnancyId, status);
	}
}