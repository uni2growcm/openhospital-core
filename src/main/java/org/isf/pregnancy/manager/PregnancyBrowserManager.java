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
package org.isf.pregnancy.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.pregnancy.model.Pregnancy;
import org.isf.pregnancy.service.PregnancyIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PregnancyBrowserManager {

	private final PregnancyIoOperations ioOperations;

	public PregnancyBrowserManager(PregnancyIoOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	/**
	 * Returns every {@link Pregnancy} recorded for the given patient (most recent first is not guaranteed,
	 * callers needing ordering should sort by {@code getLmp()}).
	 */
	public List<Pregnancy> getByPatientId(int patientCode) throws OHServiceException {
		return ioOperations.getByPatientId(patientCode);
	}

	public List<Pregnancy> getActiveByPatientId(int patientCode) throws OHServiceException {
		return ioOperations.getActiveByPatientId(patientCode);
	}

	/**
	 * Returns every currently active (ongoing) pregnancy, used by the CPN browser screen.
	 */
	public List<Pregnancy> getActivePregnancies() throws OHServiceException {
		return ioOperations.getActive();
	}

	/**
	 * Returns a page of {@link Pregnancy} matching the given optional filters, used by the CPN browser
	 * screen's search/filter/pagination controls.
	 */
	public Page<Pregnancy> getFiltered(String search, Integer active, LocalDate dateFrom, LocalDate dateTo, int page, int size)
					throws OHServiceException {
		return ioOperations.getFiltered(search, active, dateFrom, dateTo, page, size);
	}

	public Pregnancy newPregnancy(Pregnancy pregnancy) throws OHServiceException {
		validatePregnancy(pregnancy);
		return ioOperations.saveOrUpdate(pregnancy);
	}

	public Pregnancy updatePregnancy(Pregnancy pregnancy) throws OHServiceException {
		validatePregnancy(pregnancy);
		return ioOperations.saveOrUpdate(pregnancy);
	}

	public void deletePregnancy(Pregnancy pregnancy) throws OHServiceException {
		ioOperations.delete(pregnancy);
	}

	protected void validatePregnancy(Pregnancy pregnancy) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (pregnancy.getPatient() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseselectapatient.msg")));
		}
		if (pregnancy.getLmp() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.cpn.pleaseinsertthelmp.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
