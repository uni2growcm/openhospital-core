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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.isf.generaldata.MessageBundle;
import org.isf.maternity.model.PregnancyDelivery;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PregnancyDeliveryIoOperation {

	private final PregnancyDeliveryIoOperationRepository repository;

	public PregnancyDeliveryIoOperation(PregnancyDeliveryIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Retrieve a delivery linked to a specific pregnancy.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @return the delivery if it exists
	 * @throws OHServiceException if an error occurs during retrieval
	 */
	public Optional<PregnancyDelivery> getDeliveryByPregnancy(Integer pregnancyId) throws OHServiceException {
		return repository.findByPregnancyId(pregnancyId);
	}

	/**
	 * Check whether a delivery exists for a given pregnancy.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @return true if a delivery exists, false otherwise
	 * @throws OHServiceException if an error occurs during check
	 */
	public boolean hasDelivery(Integer pregnancyId) throws OHServiceException {
		return repository.existsByPregnancyId(pregnancyId);
	}

	/**
	 * Retrieve all deliveries within a given date range.
	 * Useful for statistics, reporting, and maternity analytics.
	 *
	 * @param from start date (inclusive)
	 * @param to end date (inclusive)
	 * @return list of deliveries within the specified period
	 * @throws OHServiceException if an error occurs during retrieval
	 */
	public List<PregnancyDelivery> getDeliveriesByDateRange(
		LocalDateTime from,
		LocalDateTime to
	) throws OHServiceException {
		return repository.findByDeliveryDateBetween(from, to);
	}

	/**
	 * Create a new {@link PregnancyDelivery}.
	 *
	 * @param delivery the delivery to save
	 * @return the created delivery
	 * @throws OHServiceException if an error occurs while saving
	 */
	public PregnancyDelivery newDelivery(PregnancyDelivery delivery) throws OHServiceException {
		return repository.save(delivery);
	}

	/**
	 * Update an existing {@link PregnancyDelivery}.
	 *
	 * @param delivery the delivery to update
	 * @return the updated delivery
	 * @throws OHServiceException if an error occurs while updating
	 */
	public PregnancyDelivery updateDelivery(PregnancyDelivery delivery) throws OHServiceException {
		return repository.save(delivery);
	}

	/**
	 * Delete a {@link PregnancyDelivery}.
	 *
	 * @param delivery the delivery to delete
	 * @throws OHServiceException if an error occurs while deleting
	 */
	public void deleteDelivery(PregnancyDelivery delivery) throws OHServiceException {
		repository.delete(delivery);
	}

	/**
	 * Retrieve a delivery by its identifier.
	 *
	 * @param id delivery identifier
	 * @return delivery if found
	 * @throws OHServiceException if an error occurs during retrieval
	 */
	public Optional<PregnancyDelivery> getDeliveryById(Integer id) throws OHServiceException {
		return repository.findById(id);
	}

	/**
	 * Validate that a pregnancy has a registered delivery.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @return the delivery if valid
	 * @throws OHServiceException if no delivery exists or retrieval fails
	 */
	public PregnancyDelivery validateDeliveryExists(Integer pregnancyId) throws OHServiceException {

		return repository.findByPregnancyId(pregnancyId)
			.orElseThrow(() ->
				new OHServiceException(
					new OHExceptionMessage(
						MessageBundle.getMessage("angal.maternity.nodeliveryfoundforpregnancy.msg") + ": " + pregnancyId
					)
				)
			);
	}
}