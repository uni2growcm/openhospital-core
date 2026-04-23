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
import org.isf.maternity.model.PregnancyVisitType;
import org.isf.maternity.service.PregnancyVisitTypeIoOperation;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

@Component
public class PregnancyVisitTypeBrowserManager {

	private final PregnancyVisitTypeIoOperation ioOperations;

	public PregnancyVisitTypeBrowserManager(PregnancyVisitTypeIoOperation ioOperations) {
		this.ioOperations = ioOperations;
	}

	/**
	 * Validate a {@link PregnancyVisitType} before persistence.
	 *
	 * @param visitType entity
	 * @param insert true if create operation, false if update
	 * @throws OHServiceException validation error
	 */
	protected void validateVisitType(PregnancyVisitType visitType, boolean insert)
		throws OHServiceException {

		List<OHExceptionMessage> errors = new ArrayList<>();

		String code = visitType.getCode();
		String description = visitType.getDescription();

		if (code != null) {
			code = code.trim().toUpperCase();
			visitType.setCode(code);
		}

		if (code == null || code.isEmpty()) {
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

		if (insert) {
			if (isCodePresent(code)) {
				throw new OHDataIntegrityViolationException(
					new OHExceptionMessage(
						MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
			}
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	public List<PregnancyVisitType> getVisitTypes() throws OHServiceException {
		return ioOperations.getVisitTypes();
	}

	public PregnancyVisitType getVisitTypeByCode(String code) throws OHServiceException {
		return ioOperations.getByCode(code).orElse(null);
	}

	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.existsByCode(code);
	}

	public PregnancyVisitType newVisitType(PregnancyVisitType visitType) throws OHServiceException {
		validateVisitType(visitType, true);
		return ioOperations.newVisitType(visitType);
	}

	public PregnancyVisitType updateVisitType(PregnancyVisitType visitType) throws OHServiceException {
		validateVisitType(visitType, false);
		return ioOperations.updateVisitType(visitType);
	}

	public void deleteVisitType(PregnancyVisitType visitType) throws OHServiceException {
		ioOperations.deleteVisitType(visitType);
	}
}