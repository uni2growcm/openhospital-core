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

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.pregnancy.model.PregnancyVisit;
import org.isf.pregnancy.service.PregnancyVisitIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

@Component
public class PregnancyVisitBrowserManager {

	private final PregnancyVisitIoOperations ioOperations;

	public PregnancyVisitBrowserManager(PregnancyVisitIoOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	public List<PregnancyVisit> getByPregnancyId(int pregnancyId) throws OHServiceException {
		return ioOperations.getByPregnancyId(pregnancyId);
	}

	public List<PregnancyVisit> getByPatientId(int patientCode) throws OHServiceException {
		return ioOperations.getByPatientId(patientCode);
	}

	public PregnancyVisit newVisit(PregnancyVisit visit) throws OHServiceException {
		validateVisit(visit);
		return ioOperations.saveOrUpdate(visit);
	}

	public PregnancyVisit updateVisit(PregnancyVisit visit) throws OHServiceException {
		validateVisit(visit);
		return ioOperations.saveOrUpdate(visit);
	}

	public void deleteVisit(PregnancyVisit visit) throws OHServiceException {
		ioOperations.delete(visit);
	}

	protected void validateVisit(PregnancyVisit visit) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (visit.getPregnancy() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.cpn.pleaseselectapregnancy.msg")));
		}
		if (visit.getPatient() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseselectapatient.msg")));
		}
		if (visit.getVisitDate() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.cpn.pleaseinsertavalidvisitdate.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
