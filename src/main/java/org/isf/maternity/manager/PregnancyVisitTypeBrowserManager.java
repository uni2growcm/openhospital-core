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
import org.isf.maternity.model.VisitType;
import org.isf.maternity.service.VisitTypeIoOperation;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

@Component
public class PregnancyVisitTypeBrowserManager {

	private final VisitTypeIoOperation ioOperations;

	public PregnancyVisitTypeBrowserManager(VisitTypeIoOperation ioOperations) {
		this.ioOperations = ioOperations;
	}

	/**
	 * Validate {@link VisitType} before insert/update.
	 *
	 * @param visitType the {@link VisitType}
	 * @param insert true if we are adding a new {@link DeliveryType}, false if we update
	 * @throws OHServiceException when fail to validate
	 */
	protected void validateVisitType(VisitType visitType, boolean insert) throws OHServiceException {

		String code = visitType.getCode();
		String description = visitType.getDescription();

		List<OHExceptionMessage> errors = new ArrayList<>();

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
				new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Get all {@link VisitType}s ordered by description.
	 *
	 * @return a list of all the stored {@link VisitType}s.
	 * @throws OHServiceException if an error occurs retrieving the {@link VisitType}s.
	 */
	public List<VisitType> getVisitTypes() throws OHServiceException {
		return ioOperations.getVisitTypes();
	}

	/**
	 * Get {@link VisitType} by Code.
	 *
	 * @param code the code of the {@link VisitType}
	 * @return a list of all the stored {@link VisitType}s.
	 * @throws OHServiceException if an error occurs retrieving the {@link VisitType}s.
	 */
	public VisitType getVisitTypeByCode(String code) throws OHServiceException {
		return ioOperations.getVisitTypeByCode(code);
	}

	/**
	 * Add a new {@link VisitType}.
	 *
	 * @param visitType the {@link VisitType} to add
	 * @return the new {@link VisitType}.
	 * @throws OHServiceException if an error occurs when adding the new {@link VisitType}.
	 */
	public VisitType newVisitType(VisitType visitType) throws OHServiceException {
		validateVisitType(visitType, true);
		return ioOperations.newVisitType(visitType);
	}

	/**
	 * Update a new {@link VisitType}.
	 *
	 * @param visitType the {@link VisitType} to update
	 * @return the updated {@link VisitType}.
	 * @throws OHServiceException if an error occurs when updating the {@link VisitType}.
	 */
	public VisitType updateVisitType(VisitType visitType) throws OHServiceException {
		validateVisitType(visitType, false);
		return ioOperations.updateVisitType(visitType);
	}

	/**
	 * Check if code exists
	 *
	 * @param code the code to check.
	 * @return the true id the code exist and false if it doesn't.
	 * @throws OHServiceException if an error occurs when checking.
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Delete a new {@link VisitType}.
	 *
	 * @param visitType the {@link VisitType} to delete.
	 * @throws OHServiceException if an error occurs when deleting the {@link VisitType}.
	 */
	public void deleteVisitType(VisitType visitType) throws OHServiceException {
		ioOperations.deleteVisitType(visitType);
	}
}