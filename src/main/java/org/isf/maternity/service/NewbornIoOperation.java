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
package org.isf.maternity.service;

import java.util.List;

import org.isf.maternity.model.Newborn;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service/IO Operations for Newborn model
 */
@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class NewbornIoOperation {
	private final NewbornIoOperationRepository repository;

	public NewbornIoOperation(NewbornIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Get all newborns for a delivery.
	 *
	 * @param deliveryId delivery ID
	 * @return list of newborns
	 * @throws OHServiceException if an error occurs during retrieval
	 */
	public List<Newborn> getNewbornsByDelivery(Integer deliveryId) throws OHServiceException {
		return repository.findByDeliveryId(deliveryId);
	}

	/**
	 * Add a new newborn record.
	 *
	 * @param newborn newborn entity
	 * @return saved newborn
	 * @throws OHServiceException if an error occurs during save
	 */
	public Newborn newNewborn(Newborn newborn) throws OHServiceException {
		return repository.save(newborn);
	}

	/**
	 * Update a newborn record.
	 *
	 * @param newborn newborn entity
	 * @return updated newborn
	 * @throws OHServiceException if an error occurs during update
	 */
	public Newborn updateNewborn(Newborn newborn) throws OHServiceException {
		return repository.save(newborn);
	}

	/**
	 * Delete a newborn record.
	 *
	 * @param newborn newborn entity
	 * @throws OHServiceException if an error occurs during deletion
	 */
	public void deleteNewborn(Newborn newborn) throws OHServiceException {
		repository.delete(newborn);
	}

	/**
	 * Count newborns for a delivery.
	 *
	 * @param deliveryId delivery ID
	 * @return number of newborns
	 * @throws OHServiceException if an error occurs
	 */
	public long countByDelivery(Integer deliveryId) throws OHServiceException {
		return repository.countByDeliveryId(deliveryId);
	}
}
