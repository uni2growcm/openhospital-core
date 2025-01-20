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

package org.isf.mortuary.service;

import java.util.List;

import org.isf.mortuary.model.DeathReason;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeathReasonIoOperations {

	private static DeathReasonRepository deathReasonRepository;

	@Autowired
	public DeathReasonIoOperations(DeathReasonRepository deathReasonRepository) {
		DeathReasonIoOperations.deathReasonRepository = deathReasonRepository;
	}

	/**
	 * Get all the {@link DeathReason}s.
	 * @return all the {@link DeathReason}s.
	 * @throws OHServiceException
	 */
	public List<DeathReason> getAll() throws OHServiceException {
		return deathReasonRepository.findByDeleted(false);
	}

	/**
	 * Get a specific {@link DeathReason} by id.
	 * @param id DeathReason specific id.
	 * @return {@link DeathReason}.
	 * @throws OHServiceException
	 */
	public DeathReason getById(int id) throws OHServiceException {
		return deathReasonRepository.findByIdAndDeleted(id, false);
	}

	/**
	 * Store the specified {@link DeathReason}.
	 * @param deathReason specific DeathReason to store.
	 * @return {@link DeathReason}.
	 * @throws OHServiceException
	 */
	public DeathReason add(DeathReason deathReason) throws OHServiceException {
		return deathReasonRepository.save(deathReason);
	}
}

