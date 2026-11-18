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
import org.isf.prescriber.service.PrescriberIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

@Component
public class PrescriberBrowserManager {

	private final PrescriberIoOperations ioOperations;

	public PrescriberBrowserManager(PrescriberIoOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	public List<Prescriber> getPrescribers() throws OHServiceException {
		return ioOperations.getAll();
	}

	public List<String> getPrescriberNames() throws OHServiceException {
		List<String> names = new ArrayList<>();
		for (Prescriber prescriber : ioOperations.getAll()) {
			names.add(prescriber.getName());
		}
		return names;
	}

	public Prescriber savePrescriber(Prescriber prescriber) throws OHServiceException {
		validatePrescriber(prescriber);
		return ioOperations.save(prescriber);
	}

	public void deletePrescriber(int id) throws OHServiceException {
		ioOperations.softDelete(id);
	}

	private void validatePrescriber(Prescriber prescriber) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		String name = prescriber.getName() == null ? "" : prescriber.getName().trim();
		if (name.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.prescriber.name.required.msg")));
			throw new OHDataValidationException(errors);
		}
		prescriber.setName(name);
		long duplicates = prescriber.getId() > 0
				? ioOperations.countByNameAndIdNot(name, prescriber.getId())
				: ioOperations.countByName(name);
		if (duplicates > 0) {
			errors.add(new OHExceptionMessage(
					MessageBundle.formatMessage("angal.prescriber.namealreadyexists.fmt.msg", name)));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
