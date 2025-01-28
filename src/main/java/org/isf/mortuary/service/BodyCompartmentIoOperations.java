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

import org.isf.generaldata.MessageBundle;
import org.isf.mortuary.model.BodyCompartment;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BodyCompartmentIoOperations {

	private static BodyCompartmentRepository bodyCompartmentRepository;

	@Autowired
	public BodyCompartmentIoOperations(BodyCompartmentRepository bodyCompartmentRepository) {
		BodyCompartmentIoOperations.bodyCompartmentRepository = bodyCompartmentRepository;
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
		BodyCompartment bodyCompartmentFound = bodyCompartmentRepository.findByLabelAndDeleted(bodyCompartment.getLabel(), false);
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
	 * Returns the {@link BodyCompartment} based on label
	 *
	 * @param label - the label, must not be {@literal null}
	 * @return the {@link BodyCompartment} or {@literal null} if none found
	 * @throws OHServiceException if {@label label} is {@literal null}
	 */
	public BodyCompartment getByCode(String label) throws OHServiceException {
		if (label != null) {
			return bodyCompartmentRepository.findByLabelAndDeleted(label, false);
		}
		throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuarystays.labelmostnotbenull.msg")));
	}

	/**
	 * Returns the page of {@link BodyCompartment} based on label
	 *
	 * @param label - the label, must not be {@literal null}
	 * @return the page of {@link BodyCompartment}
	 * @throws OHServiceException if {@label label} is {@literal null}
	 */
	public Page<BodyCompartment> getByCodePageable(String label, Pageable pageable) throws OHServiceException {
		if (label != null) {
			return bodyCompartmentRepository.findByLabelContainsAndDeleted(label, false, pageable);
		}
		throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuarystays.labelmostnotbenull.msg")));
	}

	/**
	 * Checks if the label is already in use.
	 *
	 * @param label - the {@link BodyCompartment} label
	 * @return {@label true} if the label is already in use and deleted is false, {@label false} otherwise
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String label) throws OHServiceException {
		boolean existed = false;
		BodyCompartment bodyCompartment = getByCode(label);
		if (bodyCompartment != null) {
			existed = true;
		}
		return existed;
	}
}
