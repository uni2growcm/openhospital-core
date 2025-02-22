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

package org.isf.mortuary.manager;

import java.util.List;

import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.service.DeathReasonIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class DeathReasonManager {

	private final DeathReasonIoOperations deathReasonIoOperations;

	public DeathReasonManager(DeathReasonIoOperations deathReasonIoOperations) {
		this.deathReasonIoOperations = deathReasonIoOperations;
	}

	/**
	 * Get all the {@link DeathReason}s.
	 * @return all the {@link DeathReason}s.
	 * @throws OHServiceException
	 */
	public List<DeathReason> getAll() throws OHServiceException {
		return deathReasonIoOperations.getAll();
	}

	/**
	 * Get a specific {@link DeathReason} by id.
	 * @param id DeathReason specific id.
	 * @return {@link DeathReason}.
	 */
	public DeathReason getById(int id)  {
		return deathReasonIoOperations.getById(id);
	}

	/**
	 * Store the specified {@link DeathReason}.
	 * @param deathReason specific DeathReason to store.
	 * @return {@link DeathReason}.
	 * @throws OHServiceException
	 */
	public DeathReason add(DeathReason deathReason) throws OHServiceException {
		return deathReasonIoOperations.add(deathReason);
	}

	/**
	 * Deletes a {@link DeathReason} in the DB.
	 * @param deathReason - the item to delete
	 * return true if deletion works and false otherwise
	 * @throws OHServiceException
	 */
	public boolean delete(DeathReason deathReason) throws OHServiceException {
		return deathReasonIoOperations.delete(deathReason);
	}

	/**
	 * Updates the specified {@link DeathReason}.
	 * @param deathReason - the {@link DeathReason} to update.
	 * @return deathReason that has been updated.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public DeathReason update(DeathReason deathReason) throws OHServiceException {
		return deathReasonIoOperations.update(deathReason);
	}

	/**
	 * Checks if the code exist.
	 * @param code - the {@link DeathReason} code
	 * @return {@code true} if the code is present in the database, {@code false} otherwise
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return deathReasonIoOperations.isCodePresent(code);
	}

	/**
	 * Returns the page of {@link DeathReason} based on code
	 *
	 * @param code - the code, must not be {@literal null}
	 * @param description - the description, must not be {@literal null}
	 * @param page current page.
	 * @param size the size of the page.
	 * @return the page of {@link DeathReason}
	 * @throws OHServiceException if {@code code} is {@literal null}
	 */
	public Page<DeathReason> getByCodeOrDescriptionPageable(String code, String description ,int page, int size) throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
		return deathReasonIoOperations.getByCodeOrDescriptionPageable(code, description,pageable);
	}
}