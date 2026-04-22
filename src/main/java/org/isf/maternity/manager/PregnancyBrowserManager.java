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

/**
 * Manager for Pregnancy Browser operations
 * Provides CRUD operations and business logic for pregnancies
 *
 * @author Developer
 * @version 1.0
 */
@Component
public class PregnancyBrowserManager {
	private final PregnancyIoOperation ioOperation;

	public PregnancyBrowserManager(PregnancyIoOperation ioOperation) {
		this.ioOperation = ioOperation;
	}

	/**
	 * Retrieve all pregnancies associated with a specific patient.
	 *
	 * @param patientId the identifier of the patient
	 * @return list of pregnancies linked to the patient
	 * @throws OHServiceException if an error occurs during retrieval
	 */
	public List<Pregnancy> getPregnanciesByPatient(Integer patientId) throws OHServiceException {
		return ioOperation.getPregnanciesByPatient(patientId);
	}

	/**
	 * Retrieve all active pregnancies (status = Ongoing).
	 *
	 * @return list of active pregnancies
	 * @throws OHServiceException if an error occurs during retrieval
	 */
	public List<Pregnancy> getActivePregnancies() throws OHServiceException {
		return ioOperation.getActivePregnancies();
	}

	/**
	 * Retrieve pregnancies filtered by status.
	 *
	 * @param status pregnancy status (e.g. Ongoing, Completed, Terminated)
	 * @return list of pregnancies matching the status
	 * @throws OHServiceException if an error occurs during retrieval
	 */
	public List<Pregnancy> getPregnanciesByStatus(String status) throws OHServiceException {
		return ioOperation.getPregnanciesByStatus(status);
	}

	/**
	 * Retrieve pregnancies filtered by risk level.
	 *
	 * @param riskLevel risk classification (Low, Medium, High)
	 * @return list of pregnancies matching the risk level
	 * @throws OHServiceException if an error occurs during retrieval
	 */
	public List<Pregnancy> getPregnanciesByRiskLevel(String riskLevel) throws OHServiceException {
		return ioOperation.getPregnanciesByRiskLevel(riskLevel);
	}

	/**
	 * Retrieve pregnancies whose Last Menstrual Period (LMP) falls within a given date range.
	 *
	 * @param dateFrom start date (inclusive)
	 * @param dateTo end date (inclusive)
	 * @return list of pregnancies within the specified range
	 * @throws OHServiceException if an error occurs during retrieval
	 */
	public List<Pregnancy> getPregnanciesByLmpRange(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return ioOperation.getPregnanciesByLmpRange(dateFrom, dateTo);
	}

	/**
	 * Create a new pregnancy record.
	 *
	 * <p>
	 * Additional business rules (risk calculation, validation, default values)
	 * can be applied before persistence.
	 * </p>
	 *
	 * @param pregnancy the pregnancy entity to create
	 * @return the persisted pregnancy entity
	 * @throws OHServiceException if an error occurs during creation
	 */
	public Pregnancy newPregnancy(Pregnancy pregnancy) throws OHServiceException {
		return ioOperation.newPregnancy(pregnancy);
	}

	/**
	 * Update an existing pregnancy record.
	 *
	 * @param pregnancy the pregnancy entity to update
	 * @return the updated pregnancy entity
	 * @throws OHServiceException if an error occurs during update
	 */
	public Pregnancy updatePregnancy(Pregnancy pregnancy) throws OHServiceException {
		return ioOperation.updatePregnancy(pregnancy);
	}

	/**
	 * Delete a pregnancy record.
	 *
	 * @param pregnancy the pregnancy entity to delete
	 * @throws OHServiceException if an error occurs during deletion
	 */
	public void deletePregnancy(Pregnancy pregnancy) throws OHServiceException {
		ioOperation.deletePregnancy(pregnancy);
	}

	/**
	 * Check whether a patient currently has an active pregnancy.
	 *
	 * @param patientId the identifier of the patient
	 * @return true if an active pregnancy exists, false otherwise
	 * @throws OHServiceException if an error occurs during the check
	 */
	public boolean hasActivePregnancy(Integer patientId) throws OHServiceException {
		return ioOperation.hasActivePregnancy(patientId);
	}

	/**
	 * Close a pregnancy by updating its status (e.g. Completed or Terminated).
	 *
	 * @param pregnancyId the identifier of the pregnancy
	 * @param status the new status to assign
	 * @return the updated pregnancy entity
	 * @throws OHServiceException if the pregnancy is not found or update fails
	 */
	public Pregnancy closePregnancy(Integer pregnancyId, String status) throws OHServiceException {
		return ioOperation.closePregnancy(pregnancyId, status);
	}
}
