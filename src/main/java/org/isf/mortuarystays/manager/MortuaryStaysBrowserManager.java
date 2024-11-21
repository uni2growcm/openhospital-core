/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

package org.isf.mortuarystays.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.mortuarystays.model.MortuaryStays;
import org.isf.mortuarystays.service.MortuaryStaysIoOperations;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.validator.EmailValidator;
import org.springframework.stereotype.Component;

@Component
public class MortuaryStaysBrowserManager {
	private MortuaryStaysIoOperations ioOperations;

	public MortuaryStaysBrowserManager(MortuaryStaysIoOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	/**
	 * Returns all stored {@link MortuaryStays}s.
	 * In case of error a message error is shown and a {@code null} value is returned.
	 *
	 * @return the stored Mortuaries.
	 * @throws OHServiceException
	 */
	public List<MortuaryStays> getMortuariesStays() throws OHServiceException {
		return ioOperations.getMortuaries();
	}

	/**
	 * Updates the specified {@link MortuaryStays}.
	 *
	 * @param mortuary - the {@link MortuaryStays} to update.
	 * @return mortuary that has been updated.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public MortuaryStays update(MortuaryStays mortuary) throws OHServiceException {
		return ioOperations.update(mortuary);
	}

	/**
	 * Stores the specified {@link MortuaryStays}.
	 *
	 * @param mortuary the mortuary to store.
	 * @return mortuary that has been stored.
	 * @throws OHServiceException if an error occurs storing the mortuary.
	 */
	public MortuaryStays newMortuaryStays(MortuaryStays mortuary) throws OHServiceException {
		validateMortuaryStays(mortuary, true);
		return ioOperations.newMortuaryStays(mortuary);
	}

	/**
	 * Deletes a {@link MortuaryStays} in the DB.
	 *
	 * @param mortuary - the item to delete
	 * @throws OHServiceException
	 */
	public void delete(MortuaryStays mortuary) throws OHServiceException {
		ioOperations.delete(mortuary);
	}

	/**
	 * Checks if the code is already in use.
	 *
	 * @param code - the {@link MortuaryStays} code
	 * @return {@code true} if the code is already in use, {@code false} otherwise
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Returns the {@link MortuaryStays} based on vaccine type code.
	 *
	 * @param code - the  {@link MortuaryStays} code.
	 * @return the {@link MortuaryStays} or {@literal null} if none found
	 * @throws OHServiceException
	 */
	public MortuaryStays getByCode(String code) throws OHServiceException {
		if (code == null) {
			throw new OHDataValidationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		}
		return ioOperations.getByCode(code);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any.
	 * @param mortuaryStays the {@link MortuaryStays} object to validate.
	 * @param insert {@code true} or updated {@code false}
	 * @throws OHServiceException
	 */
	protected void validateMortuaryStays(MortuaryStays mortuaryStays, boolean insert) throws OHServiceException {
		String code = mortuaryStays.getCode();
		String desc = mortuaryStays.getDescription();
		String name = mortuaryStays.getName();
		int minD = mortuaryStays.getDaysMin();
		int maxD = mortuaryStays.getDaysMax();
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (code.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		}
		if (code.length() > 11) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeistoolongmax1char.msg")));
		}

		if (name.isEmpty()){
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuarystays.pleaseinsertavalidname.msg")));
		}
		if (ioOperations.isCodePresent(code)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuarystays.codealreadyinuse.msg")));
		}
		if (desc.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}
		if (minD < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuarystays.insertavalidmindnumber.msg")));
		}
		if (maxD < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuatystays.insertavalidmaxdnumber.msg")));
		}
		if(minD >= maxD){
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuarystays.insertcoherencemaxminvalues.msg")));
		}

		if (insert && isCodePresent(mortuaryStays.getCode())) {
			throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
