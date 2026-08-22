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
package org.isf.partnertype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.partnertype.model.PartnerType;
import org.isf.partnertype.service.PartnerTypeIoOperation;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

/**
 * Manager class for the partner type module.
 */
@Component
public class PartnerTypeBrowserManager {

	private final PartnerTypeIoOperation ioOperations;

	public PartnerTypeBrowserManager(PartnerTypeIoOperation ioOperations) {
		this.ioOperations = ioOperations;
	}

	private void validatePartnerType(PartnerType partnerType, boolean insert) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (partnerType.getCode() == null || partnerType.getCode().isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		}
		if (partnerType.getDescription() == null || partnerType.getDescription().isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
		if (insert && isCodePresent(partnerType.getCode())) {
			throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}
	}

	/**
	 * Retrieves all the active {@link PartnerType}s.
	 * @return the list of active {@link PartnerType}s.
	 * @throws OHServiceException if an error occurs retrieving the partner types.
	 */
	public List<PartnerType> getPartnerTypes() throws OHServiceException {
		return ioOperations.getPartnerTypes();
	}

	/**
	 * Saves the specified {@link PartnerType}.
	 * @param partnerType the partner type to save.
	 * @return the newly saved {@link PartnerType}.
	 * @throws OHServiceException if the partner type is invalid or the code is already used.
	 */
	public PartnerType newPartnerType(PartnerType partnerType) throws OHServiceException {
		validatePartnerType(partnerType, true);
		return ioOperations.newPartnerType(partnerType);
	}

	/**
	 * Updates the specified {@link PartnerType}.
	 * @param partnerType the partner type to update.
	 * @return the updated {@link PartnerType}.
	 * @throws OHServiceException if the partner type is invalid.
	 */
	public PartnerType updatePartnerType(PartnerType partnerType) throws OHServiceException {
		validatePartnerType(partnerType, false);
		return ioOperations.updatePartnerType(partnerType);
	}

	/**
	 * Checks if the specified {@link PartnerType} code is already used.
	 * @param code the code to check.
	 * @return {@code true} if the code is used, {@code false} otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Soft-deletes the specified {@link PartnerType} by marking it deleted rather than removing it,
	 * so partners already using it keep referencing a valid row.
	 * @param partnerType the partner type to delete.
	 * @throws OHServiceException if an error occurs deleting the partner type.
	 */
	public void deletePartnerType(PartnerType partnerType) throws OHServiceException {
		partnerType.setDeleted(true);
		ioOperations.updatePartnerType(partnerType);
	}
}
