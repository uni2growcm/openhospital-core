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
package org.isf.prescriber.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.prescriber.model.Prescriber;
import org.isf.prescriber.service.PrescriberIoOperation;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

@Component
public class PrescriberBrowserManager {

	private final PrescriberIoOperation ioOperations;

	public PrescriberBrowserManager(PrescriberIoOperation prescriberIoOperation) {
		this.ioOperations = prescriberIoOperation;
	}

	/**
	 * Return all the {@link Prescriber}s in a list.
	 *
	 * @return the list of all Prescribers (could be empty)
	 * @throws OHServiceException
	 */
	public List<Prescriber> getPrescribers() throws OHServiceException {
		return ioOperations.getPrescriber();
	}

	/**
	 * Persist a new {@link Prescriber}.
	 *
	 * @param prescriber
	 * @return the persisted new Prescriber object.
	 * @throws OHServiceException
	 */
	public Prescriber newPrescriber(Prescriber prescriber) throws OHServiceException {
		validatePrescriber(prescriber, true);
		return ioOperations.newPrescriber(prescriber);
	}

	/**
	 * Update an existing {@link Prescriber}.
	 *
	 * @param prescriber
	 * @return the persisted updated Prescriber object.
	 * @throws OHServiceException
	 */
	public Prescriber updatePrescriber(Prescriber prescriber) throws OHServiceException {
		validatePrescriber(prescriber, false);
		return ioOperations.updatePrescriber(prescriber);
	}

	/**
	 * Check if a {@link Prescriber} already exists.
	 *
	 * @param code
	 * @return true - if the Prescriber already exists
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Delete a {@link Prescriber}.
	 *
	 * @param prescriber
	 * @throws OHServiceException
	 */
	public void deletePrescriber(Prescriber prescriber) throws OHServiceException {
		ioOperations.deletePrescriber(prescriber);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param prescriber
	 * @param insert {@code true} or updated {@code false}
	 * @throws OHServiceException
	 */
	protected void validatePrescriber(Prescriber prescriber, boolean insert) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		String key = prescriber.getCode();
		if (key.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		}
		if (key.length() > 10) {
			errors.add(new OHExceptionMessage(MessageBundle.formatMessage("angal.common.thecodeistoolongmaxchars.fmt.msg", 10)));
		}
		if (insert && isCodePresent(key)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}
		if (prescriber.getDescription().isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
