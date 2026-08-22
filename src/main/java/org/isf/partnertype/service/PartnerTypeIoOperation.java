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
package org.isf.partnertype.service;

import java.util.List;

import org.isf.partnertype.model.PartnerType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence class for the partner type module.
 */
@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PartnerTypeIoOperation {

	private final PartnerTypeIoOperationRepository repository;

	public PartnerTypeIoOperation(PartnerTypeIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Retrieves all the non-deleted {@link PartnerType}s, ordered by description.
	 * @return the list of active {@link PartnerType}s.
	 * @throws OHServiceException if an error occurs retrieving the partner types.
	 */
	public List<PartnerType> getPartnerTypes() throws OHServiceException {
		return repository.findByDeletedFalseOrderByDescriptionAsc();
	}

	/**
	 * Stores the specified {@link PartnerType}.
	 * @param partnerType the partner type to store.
	 * @return the newly saved {@link PartnerType}.
	 * @throws OHServiceException if an error occurs storing the partner type.
	 */
	public PartnerType newPartnerType(PartnerType partnerType) throws OHServiceException {
		return repository.save(partnerType);
	}

	/**
	 * Updates the specified {@link PartnerType}.
	 * @param partnerType the partner type to update.
	 * @return the updated {@link PartnerType}.
	 * @throws OHServiceException if an error occurs updating the partner type.
	 */
	public PartnerType updatePartnerType(PartnerType partnerType) throws OHServiceException {
		return repository.save(partnerType);
	}

	/**
	 * Checks if the specified {@link PartnerType} code already exists.
	 * @param code the code to check.
	 * @return {@code true} if the code is already stored, {@code false} otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}
}
