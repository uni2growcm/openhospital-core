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

import java.util.List;

import org.isf.maternity.model.VisitType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class VisitTypeIoOperation {

	private final VisitTypeIoOperationRepository repository;

	public VisitTypeIoOperation(VisitTypeIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Get all {@link VisitType}s ordered by description.
	 *
	 * @return a list of all the stored {@link VisitType}s.
	 * @throws OHServiceException if an error occurs retrieving the {@link VisitType}s.
	 */
	public List<VisitType> getVisitTypes() throws OHServiceException {
		return repository.findAllByOrderByDescriptionAsc();
	}

	/**
	 * Get {@link VisitType} by Code.
	 *
	 * @param code the code of the {@link VisitType}
	 * @return a list of all the stored {@link VisitType}s.
	 * @throws OHServiceException if an error occurs retrieving the {@link VisitType}s.
	 */
	public VisitType getVisitTypeByCode(String code) throws OHServiceException {
		return repository.findByCode(code);
	}

	/**
	 * Add a new {@link VisitType}.
	 *
	 * @param visitType the {@link VisitType} to add
	 * @return the new {@link VisitType}.
	 * @throws OHServiceException if an error occurs when adding the new {@link VisitType}.
	 */
	public VisitType newVisitType(VisitType visitType) throws OHServiceException {
		return repository.save(visitType);
	}

	/**
	 * Update a new {@link VisitType}.
	 *
	 * @param visitType the {@link VisitType} to update
	 * @return the updated {@link VisitType}.
	 * @throws OHServiceException if an error occurs when updating the {@link VisitType}.
	 */
	public VisitType updateVisitType(VisitType visitType) throws OHServiceException {
		return repository.save(visitType);
	}

	/**
	 * Delete a new {@link VisitType}.
	 *
	 * @param visitType the {@link VisitType} to delete.
	 * @throws OHServiceException if an error occurs when deleting the {@link VisitType}.
	 */
	public void deleteVisitType(VisitType visitType) throws OHServiceException {
		repository.delete(visitType);
	}

	/**
	 * Check if code exists
	 *
	 * @param code the code to check.
	 * @return the true id the code exist and false if it doesn't.
	 * @throws OHServiceException if an error occurs when checking.
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsByCode(code);
	}
}