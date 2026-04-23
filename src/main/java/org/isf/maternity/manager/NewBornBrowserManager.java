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
package org.isf.maternity.manager;

import org.isf.maternity.model.Newborn;
import org.isf.maternity.service.NewbornIoOperation;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

import java.util.List;

/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License.
 */

@Component
public class NewBornBrowserManager {

	private final NewbornIoOperation ioOperations;

	public NewBornBrowserManager(NewbornIoOperation ioOperations) {
		this.ioOperations = ioOperations;
	}

	/**
	 * Create a new newborn record.
	 *
	 * @param newborn newborn entity
	 * @return saved newborn
	 * @throws OHServiceException if creation fails
	 */
	public Newborn newNewborn(Newborn newborn) throws OHServiceException {
		return ioOperations.newNewborn(newborn);
	}

	/**
	 * Update an existing newborn record.
	 *
	 * @param newborn newborn entity
	 * @return updated newborn
	 * @throws OHServiceException if update fails
	 */
	public Newborn updateNewborn(Newborn newborn) throws OHServiceException {
		return ioOperations.updateNewborn(newborn);
	}

	/**
	 * Delete a newborn record.
	 *
	 * @param newborn newborn entity
	 * @throws OHServiceException if deletion fails
	 */
	public void deleteNewborn(Newborn newborn) throws OHServiceException {
		ioOperations.deleteNewborn(newborn);
	}

	/**
	 * Get all newborns linked to a delivery.
	 *
	 * @param deliveryId delivery identifier
	 * @return list of newborns
	 * @throws OHServiceException if retrieval fails
	 */
	public List<Newborn> getNewbornsByDelivery(Integer deliveryId)
		throws OHServiceException {
		return ioOperations.getNewbornsByDelivery(deliveryId);
	}

	/**
	 * Count newborns for a delivery.
	 *
	 * @param deliveryId delivery identifier
	 * @return number of newborns
	 * @throws OHServiceException if query fails
	 */
	public long countNewbornsByDelivery(Integer deliveryId)
		throws OHServiceException {
		return ioOperations.countNewbornsByDelivery(deliveryId);
	}

	/**
	 * Get firstborn newborn (earliest birth time) in a delivery.
	 *
	 * @param deliveryId delivery identifier
	 * @return first newborn if present
	 * @throws OHServiceException if retrieval fails
	 */
	public java.util.Optional<Newborn> getFirstBorn(Integer deliveryId)
		throws OHServiceException {
		return ioOperations.getFirstBorn(deliveryId);
	}

	/**
	 * Get newborns filtered by birth weight range.
	 *
	 * @param min minimum weight
	 * @param max maximum weight
	 * @return list of newborns in range
	 * @throws OHServiceException if query fails
	 */
	public List<Newborn> getNewbornsByWeightRange(Double min, Double max)
		throws OHServiceException {
		return ioOperations.getNewbornsByWeightRange(min, max);
	}

	/**
	 * Check if delivery has low birth weight cases.
	 *
	 * @param deliveryId delivery identifier
	 * @param weight threshold weight
	 * @return true if at least one newborn is below threshold
	 * @throws OHServiceException if check fails
	 */
	public boolean hasLowBirthWeightCases(Integer deliveryId, Double weight)
		throws OHServiceException {
		return ioOperations.hasLowBirthWeightCases(deliveryId, weight);
	}

	/**
	 * Validate that a newborn belongs to a delivery.
	 *
	 * @param newbornId newborn identifier
	 * @param deliveryId delivery identifier
	 * @return validated newborn
	 * @throws OHServiceException if validation fails
	 */
	public Newborn validateNewbornBelongsToDelivery(Integer newbornId, Integer deliveryId)
		throws OHServiceException {
		return ioOperations.validateNewbornBelongsToDelivery(newbornId, deliveryId);
	}
}