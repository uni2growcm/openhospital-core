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

import org.isf.generaldata.MessageBundle;
import org.isf.mortuary.model.BodyCompartment;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BodyCompartmentIoOperations {

	private static BodyCompartmentRepository bodyCompartmentRepository;

	@Autowired
	public BodyCompartmentIoOperations(BodyCompartmentRepository bodyCompartmentRepository) {
		BodyCompartmentIoOperations.bodyCompartmentRepository = bodyCompartmentRepository;
	}

	/**
	 * Get all the {@link BodyCompartment}s.
	 * @return all the {@link BodyCompartment}s.
	 * @throws OHServiceException
	 */
	public List<BodyCompartment> getAll() throws OHServiceException {
		return bodyCompartmentRepository.findByDeleted(false);
	}

	/**
	 * Get a specific {@link BodyCompartment} by id.
	 * @param id BodyCompartment specific id.
	 * @return {@link BodyCompartment}.
	 * @throws OHServiceException
	 */
	public BodyCompartment getById(int id) throws OHServiceException {
		return bodyCompartmentRepository.findByIDAndDeleted(id, false);
	}

	/**
	 * Store the specified {@link BodyCompartment}.
	 * @param bodyCompartment specific BodyCompartment to store.
	 * @return {@link BodyCompartment}.
	 * @throws OHServiceException
	 */
	public BodyCompartment add(BodyCompartment bodyCompartment) throws OHServiceException {
		return bodyCompartmentRepository.save(bodyCompartment);
	}

	/**
	 * Deletes a {@link BodyCompartment} in the DB.
	 *
	 * @param bodyCompartment - the item to delete
	 * @throws OHServiceException
	 */
	public BodyCompartment delete(BodyCompartment bodyCompartment) throws OHServiceException {
		BodyCompartment bodyCompartmentFound = bodyCompartmentRepository.findByCodeAndDeleted(bodyCompartment.getCode(), false);
		bodyCompartmentFound.setDeleted(true);
		return bodyCompartmentRepository.save(bodyCompartmentFound);
	}

	/**
	 * Updates the specified {@link BodyCompartment}.
	 *
	 * @param bodyCompartment - the {@link BodyCompartment} to update.
	 * @return bodyCompartment that has been updated.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public BodyCompartment update(BodyCompartment bodyCompartment) throws OHServiceException {
		return bodyCompartmentRepository.save(bodyCompartment);
	}

	/**
	 * Returns the {@link BodyCompartment} based on code
	 *
	 * @param code - the code, must not be {@literal null}
	 * @return the {@link BodyCompartment} or {@literal null} if none found
	 * @throws OHServiceException if {@code code} is {@literal null}
	 */
	public BodyCompartment getByCode(String code) throws OHServiceException {
		if (code != null) {
			return bodyCompartmentRepository.findByCodeAndDeleted(code, false);
		}
		throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuarystays.codemostnotbenull.msg")));
	}
}
