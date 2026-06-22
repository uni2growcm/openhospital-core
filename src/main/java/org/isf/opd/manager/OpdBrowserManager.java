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
package org.isf.opd.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.model.Disease;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.opd.model.DiagnosisEntry;
import org.isf.opd.model.Opd;
import org.isf.opd.service.OpdIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.pagination.PagedResponse;
import org.isf.ward.model.Ward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * @author Vero
 */
@Component
public class OpdBrowserManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(OpdBrowserManager.class);

	private final OpdIoOperations ioOperations;

	private final DiseaseBrowserManager diseaseBrowserManager;

	public OpdBrowserManager(OpdIoOperations opdIoOperations, DiseaseBrowserManager diseaseBrowserManager) {
		this.ioOperations = opdIoOperations;
		this.diseaseBrowserManager = diseaseBrowserManager;
	}

	protected void setPatientConsistency(Opd opd) {
		if (GeneralData.OPDEXTENDED && opd.getPatient() != null) {
			/*
			 * Age and Sex has not to be updated for reporting purposes
			 */
			opd.setAge(opd.getPatient().getAge());
			opd.setSex(opd.getPatient().getSex());
		}
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param opd
	 * @param insert {@code true} or updated {@code false}
	 * @throws OHDataValidationException
	 */
	public void validateOpd(Opd opd, boolean insert) throws OHDataValidationException {

		List<DiagnosisEntry> diagnoses = opd.getDiagnoses();
		Ward ward = opd.getWard();
		if (opd.getUserID() == null) {
			opd.setUserID(UserBrowsingManager.getCurrentUser());
		}
		List<OHExceptionMessage> errors = new ArrayList<>();
		// Check Visit Date
		if (opd.getDate() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.opd.pleaseinsertattendancedate.msg")));
		}
		// Check Patient
		if (GeneralData.OPDEXTENDED && opd.getPatient() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseselectapatient.msg")));
		}
		// Check Ward
		if (ward == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseselectaward.msg")));
		} else {
			if (!ward.isOpd()) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.opd.specifiedwardisnotenabledforopdservice.msg")));
			}
		}
		// Check Sex and Age
		if (opd.getAge() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.opd.pleaseinsertthepatientsage.msg")));
		}
		if (opd.getSex() == ' ') {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.opd.pleaseselectpatientssex.msg")));
		}
		boolean hasActiveDiagnosis = opd.getDisease() != null;
		if (!hasActiveDiagnosis && diagnoses != null) {
			for (DiagnosisEntry entry : diagnoses) {
				if (entry.isActive() && entry.getDisease() != null) {
					hasActiveDiagnosis = true;
					break;
				}
			}
		}
		if (!hasActiveDiagnosis) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.opd.pleaseselectadisease.msg")));
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Return all Opds of today or since one week ago
	 *
	 * @param oneWeek if {@code true} return the last week, only today otherwise.
	 * @return the list of Opds. It could be {@code null}.
	 * @throws OHServiceException
	 */
	public List<Opd> getOpd(boolean oneWeek) throws OHServiceException {
		return ioOperations.getOpdList(oneWeek);
	}

	/**
	 * Return all Opds within specified dates and parameters
	 *
	 * @param ward
	 * @param diseaseTypeCode
	 * @param diseaseCode
	 * @param dateFrom
	 * @param dateTo
	 * @param ageFrom
	 * @param ageTo
	 * @param sex
	 * @param newPatient
	 * @param user
	 * @return the list of Opds. It could be {@code null}.
	 * @throws OHServiceException
	 */
	public List<Opd> getOpd(Ward ward, String diseaseTypeCode, String diseaseCode, LocalDate dateFrom, LocalDate dateTo, int ageFrom, int ageTo, char sex,
		char newPatient, String user) throws OHServiceException {
		return ioOperations.getOpdList(ward, diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient, user);
	}

	/**
	 * Return all Opds within specified dates and parameters
	 *
	 * @param ward the ward to consider
	 * @param diseaseTypeCode the code of the disease type
	 * @param diseaseCode the code of the disease
	 * @param dateFrom the starting date
	 * @param dateTo the ending date
	 * @param ageFrom the starting age
	 * @param ageTo the ending age
	 * @param sex the sex to consider
	 * @param newPatient if patient is new
	 * @param user the user to consider
	 * @param page the page to be fetched
	 * @param size the number of OPD to be fetched per page
	 * @return the list of Opds. It could be {@code null}.
	 * @throws OHServiceException when fails to fetch paginated OPDs
	 */
	public List<Opd> getOpd(Ward ward, String diseaseTypeCode, String diseaseCode, LocalDate dateFrom, LocalDate dateTo, int ageFrom, int ageTo, char sex, char newPatient, String user, int page, int size) throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size);
		Page<Opd> opdPage = ioOperations.getOpdList(ward, diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient, user, pageable);
		return opdPage.getContent();
	}

	/**
	 * Returns all {@link Opd}s associated to specified patient ID
	 *
	 * @param patientcode the patient ID
	 * @return the list of {@link Opd}s associated to specified patient ID. the whole list of {@link Opd}s if {@code 0} is passed.
	 * @throws OHServiceException
	 */
	public List<Opd> getOpdList(int patientcode) throws OHServiceException {
		return ioOperations.getOpdList(patientcode);
	}

	/**
	 * Returns all {@link Opd}s associated to specified patient ID
	 *
	 * @param patientcode - the patient ID
	 * @param page the page to be fetched
	 * @param size the number of OPDs to be fetched per page
	 * @return the list of {@link Opd}s associated to specified patient ID.
	 * the whole list of {@link Opd}s if {@code 0} is passed.
	 * @throws OHServiceException when fails to fetched paginated OPDs
	 */
	public List<Opd> getOpdList(int patientcode, int page, int size) throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size);
		return ioOperations.getOpdList(patientcode, pageable);
	}

	/**
	 * Insert a new item in the db
	 *
	 * @param opd an {@link Opd}
	 * @return {@code true} if the item has been inserted
	 * @throws OHServiceException
	 */
	public Opd newOpd(Opd opd) throws OHServiceException {
		setPatientConsistency(opd);
		validateOpd(opd, true);
		Opd savedOpd = ioOperations.newOpd(opd);
		return savedOpd;
	}

	/**
	 * Updates the specified {@link Opd} object.
	 *
	 * @param opd the {@link Opd} object to update.
	 * @return the updated {@link Opd}
	 * @throws OHServiceException
	 */
	public Opd updateOpd(Opd opd) throws OHServiceException {
		validateOpd(opd, false);
		Opd updatedOpd = ioOperations.updateOpd(opd);
		return updatedOpd;
	}

	/**
	 * Delete an {@link Opd} from the db
	 *
	 * @param opd the {@link Opd} to delete
	 * @throws OHServiceException
	 */
	public void deleteOpd(Opd opd) throws OHServiceException {
		ioOperations.deleteDiagnoses(opd.getCode());
		ioOperations.deleteOpd(opd);
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

	/**
	 * Return the last {@link Opd} in time associated with specified patient ID.
	 *
	 * @param patientcode the patient ID
	 * @return last Opd associated with specified patient ID or {@code null}
	 * @throws OHServiceException
	 */
	public Opd getLastOpd(int patientcode) throws OHServiceException {
		return ioOperations.getLastOpd(patientcode);
	}

	/**
	 * Check if the given {@code opdNum} does already exist for the give {@code year}
	 *
	 * @param opdNum the OPD progressive in year
	 * @param year the year
	 * @return {@code true} if the given number exists in year, {@code false} otherwise
	 */
	public boolean isExistOpdNum(int opdNum, int year) throws OHServiceException {
		return ioOperations.isExistOpdNum(opdNum, year);
	}

	/**
	 * Get an OPD by its code
	 *
	 * @param code the OPD code
	 * @return an OPD or {@code null}
	 */
	public Optional<Opd> getOpdById(int code) {
		return ioOperations.getOpdById(code);
	}

	/**
	 * Get a list of OPD with specified Progressive in Year number
	 *
	 * @param code the OPD code
	 * @return a list of OPD or an empty list
	 */
	public List<Opd> getOpdByProgYear(int code) {
		return ioOperations.getOpdByProgYear(code);
	}

	/**
	 * Get a list of OPD with specified Progressive in Year number
	 *
	 * @param code - the OPD code
	 * @param page the page to be fetched
	 * @param size the number of OPDs per page
	 * @return a list of OPD or an empty list
	 */
	public List<Opd> getOpdByProgYear(int code, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return ioOperations.getOpdByProgYear(code, pageable);
	}

	/**
	 * Returns {@link List} of {@link Opd}s associated to specified patient ID with page info.
	 *
	 * @param ward the ward of opd
	 * @param diseaseTypeCode the disease type
	 * @param diseaseCode the Code of disease
	 * @param dateFrom
	 * @param dateTo
	 * @param ageFrom
	 * @param ageTo
	 * @param sex
	 * @param newPatient
	 * @param page
	 * @param size
	 * @return the list of {@link Opd}s associated to specified patient ID. the whole list of {@link Opd}s if {@code 0} is passed.
	 * @throws OHServiceException
	 */
	public PagedResponse<Opd> getOpdPageable(Ward ward, String diseaseTypeCode, String diseaseCode, LocalDate dateFrom, LocalDate dateTo, int ageFrom,
		int ageTo, char sex, char newPatient, int page, int size) throws OHServiceException {
		return ioOperations.getOpdListPageable(ward, diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient, null, page, size);
	}

	/**
	 * Returns the total number of {@link Opd}s within specified dates and parameters
	 *
	 * @param ward - the ward of opd
	 * @param diseaseTypeCode - the disease type
	 * @param diseaseCode - the Code of disease
	 * @param dateFrom the stating date
	 * @param dateTo the ending date
	 * @param ageFrom the starting age
	 * @param ageTo the ending age
	 * @param sex the sex to consider
	 * @param newPatient if patient is new
	 * @param user the user to consider
	 * @return the total number of {@link Opd}s
	 * @throws OHServiceException when fails to count fetched OPDs
	 */
	public long countTotalOpds(Ward ward, String diseaseTypeCode, String diseaseCode, LocalDate dateFrom, LocalDate dateTo, int ageFrom, int ageTo, char sex, char newPatient, String user) throws OHServiceException {
		if (ward == null && diseaseTypeCode == null && diseaseCode == null && dateFrom == null && dateTo == null &&
			ageFrom == 0 && ageTo == 0 && sex == '\0' && newPatient == '\0' && user == null) {
				return ioOperations.getOpdList(false).size();
		}

		return ioOperations.countTotalOpds(ward, diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient, user);
	}

	/**
	 * Returns the total number of {@link Opd}s with specified Progressive in Year number
	 *
	 * @param code - the OPD code
	 * @return the total number of {@link Opd}s
	 * @throws OHServiceException when fails to count fetched OPDs
	 */
	public long countByProgYear(int code) {
		return ioOperations.countByProgYear(code);
	}

	/**
	 * Returns the total number of {@link Opd}s associated to specified patient ID
	 *
	 * @param patientcode the patient ID
	 * @return the total number of {@link Opd}s
	 * @throws OHServiceException when fails to count fetched OPDs
	 */
	public long countByPatientId(int patientcode) throws OHServiceException {
		return ioOperations.countByPatientId(patientcode);
	}

	/**
	 * Retrieves a page of {@link Opd}s within specified dates and parameters.
	 *
	 * @param ward - the ward
	 * @param diseaseTypeCode the diesease type
	 * @param diseaseCode the code of the diesease
	 * @param dateTo the begininng date
	 * @param dateFrom the ending date
	 * @param ageFrom the starting age
	 * @param ageTo the ending age
	 * @param sex the patients gender to consider
	 * @param newPatient if list should contain only new patients
	 * @param page the page
	 * @param size the size of the page
	 * @return a list of OPD or an empty list
	 * @throws  OHServiceException when fails to fetch
	 */
	public Page<Opd> getOpds(
		Ward ward, String diseaseTypeCode, String diseaseCode, LocalDate dateFrom, LocalDate dateTo, int ageFrom, int ageTo,
		char sex, char newPatient, int page, int size) throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size);
		return ioOperations.getOpdList(ward, diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient, null, pageable);
	}

	/**
	 * Retrieves a page of {@link Opd}s within specified dates and parameters.
	 *
	 * @param patientCode the patient's code
	 * @param page the page
	 * @param size the size of the page
	 * @return a list of OPD or an empty list
	 * @throws  OHServiceException when fails to fetch
	 */
	public Page<Opd> getOpdListByPatientId(int patientCode, int page, int size)
		throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size);
		return ioOperations.getOpdListByPatientId(patientCode, pageable);
	}

	/**
	 * Retrieves a page of {@link Opd}s within specified dates and parameters.
	 *
	 * @param progYear
	 * @param page the page
	 * @param size the size of the page
	 * @return a list of OPD or an empty list
	 * @throws  OHServiceException when fails to fetch
	 */
	public Page<Opd> getOpdListByProgYear(int progYear, int page, int size)
		throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size);
		return ioOperations.getOpdListByProgYear(progYear, pageable);
	}

	/**
	 * Retrieves all active diagnoses for an OPD
	 *
	 * @param opdId the OPD ID
	 * @return list of active diagnoses
	 * @throws OHServiceException
	 */
	public List<DiagnosisEntry> getDiagnosesByOpdId(int opdId) throws OHServiceException {
		return ioOperations.getDiagnosesList(opdId);
	}

	/**
	 * Retrieves all diagnoses (active and inactive) for an OPD
	 *
	 * @param opdId the OPD ID
	 * @return list of all diagnoses
	 * @throws OHServiceException
	 */
	public List<DiagnosisEntry> getAllDiagnosesByOpdId(int opdId) throws OHServiceException {
		return ioOperations.getAllDiagnosesList(opdId);
	}

	/**
	 * Creates a new diagnosis
	 *
	 * @param diagnosis the diagnosis to create
	 * @return created diagnosis
	 * @throws OHServiceException
	 */
	public DiagnosisEntry newDiagnosis(DiagnosisEntry diagnosis) throws OHServiceException {
		return ioOperations.newDiagnosis(diagnosis);
	}

	/**
	 * Updates all diagnoses for an OPD (replaces existing ones)
	 *
	 * @param opdId the OPD ID
	 * @param diagnoses list of diagnoses
	 * @return list of updated diagnoses
	 * @throws OHServiceException
	 */
	public List<DiagnosisEntry> updateDiagnoses(int opdId, List<DiagnosisEntry> diagnoses) throws OHServiceException {
		return ioOperations.updateDiagnoses(opdId, diagnoses);
	}

	/**
	 * Checks if an OPD has any active diagnoses
	 *
	 * @param opdId the OPD ID
	 * @return {@code true} if has active diagnoses
	 * @throws OHServiceException
	 */
	public boolean hasDiagnoses(int opdId) throws OHServiceException {
		return ioOperations.hasDiagnoses(opdId);
	}

	/**
	 * Deletes all diagnoses for an OPD
	 *
	 * @param opdId the OPD ID
	 * @throws OHServiceException
	 */
	public void deleteDiagnoses(int opdId) throws OHServiceException {
		ioOperations.deleteDiagnoses(opdId);
	}
}
