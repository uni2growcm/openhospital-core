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

import org.isf.maternity.model.Pregnancy;
import org.isf.maternity.model.PregnancyStatus;
import org.isf.maternity.model.RiskLevel;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PregnancyIoOperation {

	private final PregnancyIoOperationRepository repository;

	public PregnancyIoOperation(PregnancyIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Create a new pregnancy record.
	 *
	 * @param pregnancy the pregnancy entity to persist
	 * @return the saved pregnancy entity
	 * @throws OHServiceException if persistence fails
	 */
	public Pregnancy newPregnancy(Pregnancy pregnancy) throws OHServiceException {
		return repository.save(pregnancy);
	}

	/**
	 * Update an existing pregnancy record.
	 *
	 * @param pregnancy the pregnancy entity to update
	 * @return the updated pregnancy entity
	 * @throws OHServiceException if update fails
	 */
	public Pregnancy updatePregnancy(Pregnancy pregnancy) throws OHServiceException {
		return repository.save(pregnancy);
	}

	/**
	 * Delete a pregnancy record.
	 *
	 * @param pregnancy the pregnancy entity to delete
	 * @throws OHServiceException if deletion fails
	 */
	public void deletePregnancy(Pregnancy pregnancy) throws OHServiceException {
		repository.delete(pregnancy);
	}

	/**
	 * Get all pregnancies for a given patient ordered by creation date.
	 *
	 * @param patientCode the patient identifier
	 * @return list of pregnancies for the patient
	 * @throws OHServiceException if retrieval fails
	 */
	public List<Pregnancy> getPregnanciesByPatient(Integer patientCode)
		throws OHServiceException {
		return repository.findByPatient_CodeOrderByDateDesc(patientCode);
	}

	/**
	 * Get pregnancies using advanced search filters.
	 * All parameters are optional and can be null.
	 *
	 * @param patientCode filter by patient code (optional)
	 * @param status filter by pregnancy status (optional)
	 * @param riskLevel filter by risk level (optional)
	 * @param fromDate filter start date (optional)
	 * @param toDate filter end date (optional)
	 * @param lmpDateFrom filter start date (optional)
	 * @param lmpDateTo filter end date (optional)
	 * @param pageable pagination information
	 * @return paginated list of pregnancies
	 * @throws OHServiceException if search fails
	 */
	public Page<Pregnancy> searchPregnancies(
		Integer patientCode,
		PregnancyStatus status,
		RiskLevel riskLevel,
		LocalDateTime fromDate,
		LocalDateTime toDate,
		LocalDateTime lmpDateFrom,
		LocalDateTime lmpDateTo,
		Pageable pageable
	) throws OHServiceException {
		return repository.getPregnancies(
			patientCode,
			status,
			riskLevel,
			fromDate,
			toDate,
			lmpDateFrom,
			lmpDateTo,
			pageable
		);
	}

	/**
	 * Get pregnancies created within a date range.
	 *
	 * @param fromDate start date
	 * @param toDate end date
	 * @param pageable pagination information
	 * @return paginated pregnancies
	 * @throws OHServiceException if retrieval fails
	 */
	public Page<Pregnancy> getPregnanciesByDateRange(
		LocalDateTime fromDate,
		LocalDateTime toDate,
		Pageable pageable
	) throws OHServiceException {
		return repository.findByDateBetween(fromDate, toDate, pageable);
	}

	/**
	 * Check if a patient currently has an active pregnancy.
	 *
	 * @param patientCode the patient identifier
	 * @return true if an active pregnancy exists
	 * @throws OHServiceException if check fails
	 */
	public boolean hasActivePregnancy(Integer patientCode) throws OHServiceException {
		return repository.existsByPatient_CodeAndStatus(patientCode, PregnancyStatus.ONGOING);
	}

	/**
	 * Count pregnancies by patient and status.
	 *
	 * @param patientCode patient identifier
	 * @param status pregnancy status
	 * @return number of pregnancies
	 * @throws OHServiceException if operation fails
	 */
	public long countPregnanciesByPatientAndStatus(Integer patientCode, PregnancyStatus status)
		throws OHServiceException {
		return repository.countByPatient_CodeAndStatus(patientCode, status);
	}

	/**
	 * Get the latest pregnancy for a patient with a given status.
	 *
	 * @param patientCode patient identifier
	 * @param status pregnancy status
	 * @return latest pregnancy (if exists)
	 * @throws OHServiceException if retrieval fails
	 */
	public Pregnancy getLatestPregnancyByPatientAndStatus(Integer patientCode, PregnancyStatus status)
		throws OHServiceException {
		return repository
			.findTopByPatient_CodeAndStatusOrderByDateDesc(patientCode, status)
			.orElse(null);
	}

	/**
	 * Close a pregnancy by updating its status.
	 * Typical statuses:
	 * - Completed
	 * - Terminated
	 *
	 * @param pregnancyId pregnancy identifier
	 * @param status new status to set
	 * @return updated pregnancy
	 * @throws OHServiceException if pregnancy not found or update fails
	 */
	public Pregnancy closePregnancy(Integer pregnancyId, PregnancyStatus status)
		throws OHServiceException {

		Pregnancy pregnancy = repository.findById(pregnancyId)
			.orElseThrow(() ->
				new OHServiceException(
					new OHExceptionMessage("Pregnancy not found: " + pregnancyId)
				)
			);

		pregnancy.setStatus(status);
		return repository.save(pregnancy);
	}
}