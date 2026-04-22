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
import org.isf.maternity.model.DeliveryType;
import org.isf.maternity.service.DeliveryTypeIoOperation;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

@Component
public class DeliveryTypeBrowserManager {

	private final DeliveryTypeIoOperation ioOperations;

	public DeliveryTypeBrowserManager(DeliveryTypeIoOperation ioOperations) {
		this.ioOperations = ioOperations;
	}

	/**
	 * Validate DeliveryType before insert/update.
	 *
	 * @param deliveryType the {@link DeliveryType}
	 * @param insert true if we are adding a new {@link DeliveryType}, false if we update
	 * @throws OHServiceException when fail to validate
	 */
	protected void validateDeliveryType(DeliveryType deliveryType, boolean insert) throws OHServiceException {

		String code = deliveryType.getCode();
		String description = deliveryType.getDescription();

		List<OHExceptionMessage> errors = new ArrayList<>();

		if (code == null || code.isEmpty()) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		}

		if (code != null && code.length() > 20) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.common.thecodeistoolong.msg")));
		}

		if (description == null || description.isEmpty()) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}

		if (insert && isCodePresent(code)) {
			throw new OHDataIntegrityViolationException(
				new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Get all delivery types.
	 *
	 * @return list of {@link DeliveryType}
	 * @throws OHServiceException when an error occurs
	 */
	public List<DeliveryType> getDeliveryTypes() throws OHServiceException {
		return ioOperations.getDeliveryTypes();
	}

	/**
	 * Get delivery type by code.
	 *
	 * @param code the code
	 * @return {@link DeliveryType}
	 * @throws OHServiceException when an error occurs
	 */
	public DeliveryType getDeliveryTypeByCode(String code) throws OHServiceException {
		return ioOperations.getDeliveryTypeByCode(code);
	}

	/**
	 * Create new delivery type.
	 *
	 * @param deliveryType the {@link DeliveryType}
	 * @return saved entity
	 * @throws OHServiceException when an error occurs
	 */
	public DeliveryType newDeliveryType(DeliveryType deliveryType) throws OHServiceException {
		validateDeliveryType(deliveryType, true);
		return ioOperations.newDeliveryType(deliveryType);
	}

	/**
	 * Update delivery type.
	 *
	 * @param deliveryType the {@link DeliveryType}
	 * @return updated entity
	 * @throws OHServiceException when an error occurs
	 */
	public DeliveryType updateDeliveryType(DeliveryType deliveryType) throws OHServiceException {
		validateDeliveryType(deliveryType, false);
		return ioOperations.updateDeliveryType(deliveryType);
	}

	/**
	 * Delete delivery type.
	 *
	 * @param deliveryType the {@link DeliveryType}
	 * @throws OHServiceException when an error occurs
	 */
	public void deleteDeliveryType(DeliveryType deliveryType) throws OHServiceException {
		ioOperations.deleteDeliveryType(deliveryType);
	}

	/**
	 * Check if code exists.
	 *
	 * @param code the code
	 * @return true if exists
	 * @throws OHServiceException when an error occurs
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}
}