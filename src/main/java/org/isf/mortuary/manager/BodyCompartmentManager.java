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

import org.isf.mortuary.model.BodyCompartment;
import org.isf.mortuary.service.BodyCompartmentIoOperations;
import org.isf.mortuarystays.model.MortuaryStay;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

@Component
public class BodyCompartmentManager {

	private final BodyCompartmentIoOperations bodyCompartmentIoOperations;

	public BodyCompartmentManager(BodyCompartmentIoOperations deathReasonIoOperations) {
		this.bodyCompartmentIoOperations = deathReasonIoOperations;
	}

	/**
	 * Get all the {@link BodyCompartment}s.
	 * @return all the {@link BodyCompartment}s.
	 * @throws OHServiceException
	 */
	public List<BodyCompartment> getAll() throws OHServiceException {
		return bodyCompartmentIoOperations.getAll();
	}

	/**
	 * Get a specific {@link BodyCompartment} by id.
	 * @param id BodyCompartment specific id.
	 * @return {@link BodyCompartment}.
	 * @throws OHServiceException
	 */
	public BodyCompartment getById(int id) throws OHServiceException {
		return bodyCompartmentIoOperations.getById(id);
	}

	/**
	 * Store the specified {@link BodyCompartment}.
	 * @param bodyCompartment specific BodyCompartment to store.
	 * @return {@link BodyCompartment}.
	 * @throws OHServiceException
	 */
	public BodyCompartment add(BodyCompartment bodyCompartment) throws OHServiceException {
		return bodyCompartmentIoOperations.add(bodyCompartment);
	}

	/**
	 * Deletes a {@link BodyCompartment} in the DB.
	 * @param bodyCompartment - the item to delete
	 * @throws OHServiceException
	 */
	public BodyCompartment delete(BodyCompartment bodyCompartment) throws OHServiceException {
		return bodyCompartmentIoOperations.delete(bodyCompartment);
	}

	/**
	 * Updates the specified {@link BodyCompartment}.
	 * @param bodyCompartment - the {@link BodyCompartment} to update.
	 * @return bodyCompartment that has been updated.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public BodyCompartment update(BodyCompartment bodyCompartment) throws OHServiceException {
		BodyCompartment bodyCompartmentFound = bodyCompartmentIoOperations.getByCode(bodyCompartment.getCode());
		bodyCompartmentFound.setDescription(bodyCompartment.getDescription());
		return bodyCompartmentIoOperations.update(bodyCompartmentFound);
	}

	/**
	 * Checks if the code is already in use.
	 * @param code - the {@link BodyCompartment} code
	 * @return {@code true} if the code is already in use, {@code false} otherwise
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return bodyCompartmentIoOperations.isCodePresent(code);
	}

}
