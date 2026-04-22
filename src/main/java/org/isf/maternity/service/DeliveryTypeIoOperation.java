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

import org.isf.maternity.model.DeliveryType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class DeliveryTypeIoOperation {

	private final DeliveryTypeIoOperationRepository repository;

	public DeliveryTypeIoOperation(DeliveryTypeIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Get all {@link DeliveryType}s ordered by description.
	 *
	 * @return a list of all the stored {@link DeliveryType}s.
	 * @throws OHServiceException if an error occurs retrieving the {@link DeliveryType}s.
	 */
	public List<DeliveryType> getDeliveryTypes() throws OHServiceException {
		return repository.findAllByOrderByDescriptionAsc();
	}

	/**
	 * Get {@link DeliveryType} by Code.
	 *
	 * @param code the code of the {@link DeliveryType}
	 * @return the {@link DeliveryType}
	 * @throws OHServiceException if an error occurs retrieving the {@link DeliveryType}.
	 */
	public DeliveryType getDeliveryTypeByCode(String code) throws OHServiceException {
		return repository.findByCode(code);
	}

	/**
	 * Add a new {@link DeliveryType}.
	 *
	 * @param deliveryType the {@link DeliveryType} to add
	 * @return the new {@link DeliveryType}.
	 * @throws OHServiceException if an error occurs when adding the new {@link DeliveryType}.
	 */
	public DeliveryType newDeliveryType(DeliveryType deliveryType) throws OHServiceException {
		return repository.save(deliveryType);
	}

	/**
	 * Update a {@link DeliveryType}.
	 *
	 * @param deliveryType the {@link DeliveryType} to update
	 * @return the updated {@link DeliveryType}.
	 * @throws OHServiceException if an error occurs when updating the {@link DeliveryType}.
	 */
	public DeliveryType updateDeliveryType(DeliveryType deliveryType) throws OHServiceException {
		return repository.save(deliveryType);
	}

	/**
	 * Delete a {@link DeliveryType}.
	 *
	 * @param deliveryType the {@link DeliveryType} to delete.
	 * @throws OHServiceException if an error occurs when deleting the {@link DeliveryType}.
	 */
	public void deleteDeliveryType(DeliveryType deliveryType) throws OHServiceException {
		repository.delete(deliveryType);
	}

	/**
	 * Check if code exists.
	 *
	 * @param code the code to check.
	 * @return true if the code exists, false otherwise.
	 * @throws OHServiceException if an error occurs when checking.
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsByCode(code);
	}
}