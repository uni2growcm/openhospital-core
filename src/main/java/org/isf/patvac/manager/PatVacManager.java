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
package org.isf.patvac.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.isf.generaldata.MessageBundle;
import org.isf.patvac.model.PatientVaccine;
import org.isf.patvac.service.PatVacIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PatVacManager {

	private final PatVacIoOperations ioOperations;

	public PatVacManager(PatVacIoOperations patVacIoOperations) {
		this.ioOperations = patVacIoOperations;
	}

	/**
	 * Returns all {@link PatientVaccine}s for today or one week ago.
	 *
	 * @param minusOneWeek if {@code true} return the last week
	 * @return the list of {@link PatientVaccine}s
	 * @throws OHServiceException
	 */
	public List<PatientVaccine> getPatientVaccine(boolean minusOneWeek) throws OHServiceException {
		return ioOperations.getPatientVaccine(minusOneWeek);
	}

	/**
	 * Returns a page of {@link PatientVaccine}s filtered by vaccine type, vaccine, date range, sex and age.
	 *
	 * @param vaccineTypeCode the vaccine type code
	 * @param vaccineCode the vaccine code
	 * @param dateFrom the start date
	 * @param dateTo the end date
	 * @param sex the patient sex
	 * @param ageFrom the minimum age
	 * @param ageTo the maximum age
	 * @param pageable the pagination information
	 * @return a page of {@link PatientVaccine}s
	 * @throws OHServiceException
	 */
	public Page<PatientVaccine> getPatientVaccinePage(String vaccineTypeCode, String vaccineCode, LocalDateTime dateFrom,LocalDateTime dateTo, char sex, int ageFrom, int ageTo, Pageable pageable) throws OHServiceException {
		return ioOperations.getPatientVaccinePage(vaccineTypeCode, vaccineCode, dateFrom, dateTo, sex, ageFrom, ageTo, pageable);
	}

	/**
	 * Returns all {@link PatientVaccine}s within {@code dateFrom} and {@code dateTo}.
	 *
	 * @param vaccineTypeCode
	 * @param vaccineCode
	 * @param dateFrom
	 * @param dateTo
	 * @param sex
	 * @param ageFrom
	 * @param ageTo
	 * @return the list of {@link PatientVaccine}s
	 * @throws OHServiceException
	 */
	public List<PatientVaccine> getPatientVaccine(String vaccineTypeCode, String vaccineCode, LocalDateTime dateFrom, LocalDateTime dateTo, char sex,
		int ageFrom, int ageTo) throws OHServiceException {
		return ioOperations.getPatientVaccine(vaccineTypeCode, vaccineCode, dateFrom, dateTo, sex, ageFrom, ageTo);
	}

	/**
	 * Returns all {@link PatientVaccine}s associated with the specified patient.
	 *
	 * @param patientCode the unique identifier of the patient
	 * @return the list of {@link PatientVaccine}s for the given patient
	 * @throws OHServiceException if an error occurs while retrieving the patient vaccines
	 */
	public List<PatientVaccine> getPatientVaccineByPatientId(int patientCode) throws OHServiceException {
		return this.ioOperations.findForPatient(patientCode);
	}

	/**
	 * Inserts a {@link PatientVaccine}.
	 *
	 * @param patVac the {@link PatientVaccine} to insert
	 * @return the newly {@link PatientVaccine} object.
	 * @throws OHServiceException
	 */
	public PatientVaccine newPatientVaccine(PatientVaccine patVac) throws OHServiceException {
		validatePatientVaccine(patVac);
		return ioOperations.newPatientVaccine(patVac);
	}

	/**
	 * Updates a {@link PatientVaccine}.
	 *
	 * @param patVac the {@link PatientVaccine} to update
	 * @return the updated {@link PatientVaccine} object.
	 * @throws OHServiceException
	 */
	public PatientVaccine updatePatientVaccine(PatientVaccine patVac) throws OHServiceException {
		validatePatientVaccine(patVac);
		return ioOperations.updatePatientVaccine(patVac);
	}

	/**
	 * Deletes a {@link PatientVaccine}.
	 *
	 * @param patVac the {@link PatientVaccine} to delete
	 * @throws OHServiceException
	 */
	public void deletePatientVaccine(PatientVaccine patVac) throws OHServiceException {
		ioOperations.deletePatientVaccine(patVac);
	}

	/**
	 * Returns the max progressive number within specified year or within current year if {@code 0}.
	 *
	 * @param year
	 * @return {@code int} the progressive number in the year
	 * @throws OHServiceException
	 */
	public int getProgYear(int year) throws OHServiceException {
		return ioOperations.getProgYear(year);
	}

	public Optional<PatientVaccine> getPatientVaccine(int code) throws OHServiceException {
		return ioOperations.getPatientVaccine(code);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any.
	 *
	 * @param patientVaccine
	 * @throws OHDataValidationException
	 */
	protected void validatePatientVaccine(PatientVaccine patientVaccine) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (patientVaccine.getVaccineDate() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.patvac.pleaseinsertvaccinedate.msg")));
		}
		if (patientVaccine.getProgr() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.patvac.pleaseinsertavalidprogressive.msg")));
		}
		if (patientVaccine.getVaccine() == null
			|| patientVaccine.getVaccine().getDescription().equals(MessageBundle.getMessage("angal.patvac.allvaccine"))) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.patvac.pleaseselectavaccine.msg")));
		}
		if (patientVaccine.getPatient() == null
			|| StringUtils.isEmpty(patientVaccine.getPatName())
			|| StringUtils.isEmpty(String.valueOf(patientVaccine.getPatSex()))) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseselectapatient.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Returns a page of {@link PatientVaccine}s filtered by vaccine type, vaccine, date range, sex, age,
	 * patient search and village.
	 *
	 * @param vaccineTypeCode the vaccine type code
	 * @param vaccineCode the vaccine code
	 * @param dateFrom the start date
	 * @param dateTo the end date
	 * @param sex the patient sex
	 * @param ageFrom the minimum age
	 * @param ageTo the maximum age
	 * @param patientSearchText the patient name or code to search
	 * @param villageText the village to filter
	 * @param pageable the pagination information
	 * @return a page of {@link PatientVaccine}s
	 * @throws OHServiceException
	 */
	public Page<PatientVaccine> getPatientVaccinePage(String vaccineTypeCode, String vaccineCode, LocalDateTime dateFrom,
		LocalDateTime dateTo, char sex, int ageFrom, int ageTo, String patientSearchText, String villageText, Pageable pageable) throws OHServiceException {
		return ioOperations.getPatientVaccinePage(vaccineTypeCode, vaccineCode, dateFrom, dateTo, sex, ageFrom, ageTo, patientSearchText, villageText, pageable);
	}
}
