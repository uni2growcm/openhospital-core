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

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.mortuary.model.BodyCompartment;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
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
		validate(bodyCompartment);
		return bodyCompartmentRepository.save(bodyCompartment);
	}

	/**
	 * Deletes a {@link BodyCompartment} in the DB.
	 *
	 * @param bodyCompartment - the item to delete
	 * return true if deletion works and false otherwise
	 * @throws OHServiceException
	 */
	public boolean delete(BodyCompartment bodyCompartment) throws OHServiceException {
		BodyCompartment bodyCompartmentFound = bodyCompartmentRepository.findByLabelAndDeleted(bodyCompartment.getLabel(), false);
		if (bodyCompartmentFound == null) {
			throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuary.bodycompartment.thisbodycompartmentdontexist.msg")));
		}
		bodyCompartmentFound.setDeleted(true);
		BodyCompartment deleted = bodyCompartmentRepository.save(bodyCompartmentFound);
		return deleted.isDeleted();
	}

	/**
	 * Updates the specified {@link BodyCompartment}.
	 *
	 * @param bodyCompartment - the {@link BodyCompartment} to update.
	 * @return bodyCompartment that has been updated.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public BodyCompartment update(BodyCompartment bodyCompartment) throws OHServiceException {
		BodyCompartment bodyCompartmentFound = bodyCompartmentRepository.findById(bodyCompartment.getId()).orElse(null);
		if (bodyCompartmentFound == null) {
			throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuary.bodycompartment.thisbodycompartmentdontexist.msg")));
		}
		bodyCompartmentFound.setDescription(bodyCompartment.getDescription());
		return bodyCompartmentRepository.save(bodyCompartment);
	}

	/**
	 * Returns the page of {@link BodyCompartment} based on label
	 *
	 * @param label - the label, must not be {@literal null}
	 * @param description - the description, must not be {@literal null}
	 * @return the page of {@link BodyCompartment}
	 * @throws OHServiceException if {@label label} is {@literal null}
	 */
	public Page<BodyCompartment> getByLabelOrDescriptionPageable(String label, String description,Pageable pageable) throws OHServiceException {
		if (label != null && description != null) {
			return bodyCompartmentRepository.findByLabelContainsAndDeletedOrDescriptionContainsAndDeleted(label, false,description,false, pageable);
		}
		throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuarystays.labelmostnotbenull.msg")));
	}

	/**
	 * Checks if the label exist.
	 *
	 * @param label - the {@link BodyCompartment} label
	 * @return {@label true} if the label is already in use and deleted is false, {@label false} otherwise
	 */
	public boolean isLabelPresent(String label) {
		boolean existed = false;
		BodyCompartment bodyCompartment = bodyCompartmentRepository.findByLabelAndDeleted(label, false);
		if (bodyCompartment != null) {
			existed = true;
		}
		return existed;
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any.
	 * @param bodyCompartment the {@link BodyCompartment} object to validate.
	 * @throws OHServiceException
	 */
	protected void validate(BodyCompartment bodyCompartment) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (bodyCompartment == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.commom.anullentrycannotberegistered.msg")));
		} else {
			if (bodyCompartment.getLabel() == null) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
			} else {
				if (bodyCompartment.getLabel().trim().isEmpty()) {
					errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
				}
				if (isLabelPresent(bodyCompartment.getLabel())) {
					throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
				}
			}
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}