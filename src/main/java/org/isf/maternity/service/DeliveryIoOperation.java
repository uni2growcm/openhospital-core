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

import org.isf.maternity.model.Delivery;
import org.isf.utils.exception.OHServiceException;

public class DeliveryIoOperation {

	private final DeliveryIoOperationRepository repository;

	public DeliveryIoOperation(DeliveryIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Retrieve delivery by pregnancy ID.
	 *
	 * @param pregnancyId the pregnancy ID
	 * @return the Delivery associated with the pregnancy
	 * @throws OHServiceException if an error occurs during retrieval
	 */
	public Delivery getDeliveryByPregnancy(Integer pregnancyId) throws OHServiceException {
		return repository.findByPregnancyId(pregnancyId);
	}

	/**
	 * Save a new Delivery.
	 *
	 * @param delivery the Delivery to save
	 * @return the saved Delivery
	 * @throws OHServiceException if an error occurs during saving
	 */
	public Delivery newDelivery(Delivery delivery) throws OHServiceException {
		return repository.save(delivery);
	}

	/**
	 * Update an existing Delivery.
	 *
	 * @param delivery the Delivery to update
	 * @return the updated Delivery
	 * @throws OHServiceException if an error occurs during update
	 */
	public Delivery updateDelivery(Delivery delivery) throws OHServiceException {
		return repository.save(delivery);
	}

	/**
	 * Delete a Delivery.
	 *
	 * @param delivery the Delivery to delete
	 * @throws OHServiceException if an error occurs during deletion
	 */
	public void deleteDelivery(Delivery delivery) throws OHServiceException {
		repository.delete(delivery);
	}

	/**
	 * Check if a delivery exists for a pregnancy.
	 *
	 * @param pregnancyId pregnancy ID
	 * @return true if exists
	 * @throws OHServiceException if an error occurs
	 */
	public boolean isDeliveryPresent(Integer pregnancyId) throws OHServiceException {
		return repository.existsByPregnancyId(pregnancyId);
	}
}