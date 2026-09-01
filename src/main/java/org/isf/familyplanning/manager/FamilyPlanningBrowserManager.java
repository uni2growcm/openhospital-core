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
package org.isf.familyplanning.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.isf.familyplanning.model.FamilyPlanningRecord;
import org.isf.familyplanning.service.FamilyPlanningIoOperations;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

@Component
public class FamilyPlanningBrowserManager {

	private final FamilyPlanningIoOperations ioOperations;

	public FamilyPlanningBrowserManager(FamilyPlanningIoOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	public List<FamilyPlanningRecord> getByPatientId(int patientCode) throws OHServiceException {
		return ioOperations.getByPatientId(patientCode);
	}

	/**
	 * Returns every family planning record between the two dates (inclusive), used by the printable
	 * register report.
	 */
	public List<FamilyPlanningRecord> getByDateRange(LocalDate dateFrom, LocalDate dateTo) throws OHServiceException {
		return ioOperations.getByDateRange(dateFrom, dateTo);
	}

	public FamilyPlanningRecord newRecord(FamilyPlanningRecord record) throws OHServiceException {
		validateRecord(record);
		return ioOperations.saveOrUpdate(record);
	}

	public FamilyPlanningRecord updateRecord(FamilyPlanningRecord record) throws OHServiceException {
		validateRecord(record);
		return ioOperations.saveOrUpdate(record);
	}

	public void deleteRecord(FamilyPlanningRecord record) throws OHServiceException {
		ioOperations.delete(record);
	}

	protected void validateRecord(FamilyPlanningRecord record) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (record.getPatient() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseselectapatient.msg")));
		}
		if (record.getVisitDate() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.familyplanning.pleaseinsertthevisitdate.msg")));
		}
		if (record.getMethod() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.familyplanning.pleaseselectamethod.msg")));
		}
		if (record.getReason() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.familyplanning.pleaseselectareason.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
