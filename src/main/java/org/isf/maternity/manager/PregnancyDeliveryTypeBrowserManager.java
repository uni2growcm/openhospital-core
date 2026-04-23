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

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.maternity.model.PregnancyDeliveryType;
import org.isf.maternity.service.PregnancyDeliveryTypeIoOperation;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

@Component
public class PregnancyDeliveryTypeBrowserManager {

	private final PregnancyDeliveryTypeIoOperation service;

	public PregnancyDeliveryTypeBrowserManager(PregnancyDeliveryTypeIoOperation service) {
		this.service = service;
	}

	/**
	 * Validate a {@link PregnancyDeliveryType} before insert or update.
	 * Ensures:
	 * - Code is required and within size limits
	 * - Description is required
	 * - Code uniqueness is enforced on insert
	 *
	 * @param type delivery type to validate
	 * @param insert true if creating new entity, false if updating
	 * @throws OHServiceException if validation fails
	 */
	protected void validateDeliveryType(PregnancyDeliveryType type, boolean insert)
		throws OHServiceException {

		List<OHExceptionMessage> errors = new ArrayList<>();

		String code = type.getCode();
		String description = type.getDescription();

		if (code == null || code.trim().isEmpty()) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		}

		if (code != null && code.length() > 20) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.common.thecodeistoolong.msg")));
		}

		if (description == null || description.trim().isEmpty()) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}

		if (insert && isCodePresent(code)) {
			throw new OHDataIntegrityViolationException(
				new OHExceptionMessage(
					MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Get all {@link PregnancyDeliveryType}.
	 *
	 * @return list of delivery types ordered by description
	 * @throws OHServiceException if retrieval fails
	 */
	public List<PregnancyDeliveryType> getDeliveryTypes() throws OHServiceException {
		return service.getDeliveryTypes();
	}

	/**
	 * Get a delivery type by its code.
	 *
	 * @param code delivery type code
	 * @return delivery type if found
	 * @throws OHServiceException if retrieval fails
	 */
	public PregnancyDeliveryType getDeliveryTypeByCode(String code) throws OHServiceException {
		return service.getByCode(code).orElse(null);
	}

	/**
	 * Check if a delivery type code exists.
	 *
	 * @param code delivery type code
	 * @return true if exists
	 * @throws OHServiceException if check fails
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return service.existsByCode(code);
	}

	/**
	 * Create a new {@link PregnancyDeliveryType}.
	 *
	 * @param type delivery type
	 * @return created entity
	 * @throws OHServiceException if creation fails
	 */
	public PregnancyDeliveryType newDeliveryType(PregnancyDeliveryType type) throws OHServiceException {
		validateDeliveryType(type, true);
		return service.newDeliveryType(type);
	}

	/**
	 * Update an existing {@link PregnancyDeliveryType}.
	 *
	 * @param type delivery type
	 * @return updated entity
	 * @throws OHServiceException if update fails
	 */
	public PregnancyDeliveryType updateDeliveryType(PregnancyDeliveryType type) throws OHServiceException {
		validateDeliveryType(type, false);
		return service.updateDeliveryType(type);
	}

	/**
	 * Delete a {@link PregnancyDeliveryType}.
	 *
	 * @param type delivery type
	 * @throws OHServiceException if deletion fails
	 */
	public void deleteDeliveryType(PregnancyDeliveryType type) throws OHServiceException {
		service.deleteDeliveryType(type);
	}
}