/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.patvac.service;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.patvac.model.PatientVaccine;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatVacIoOperationRepositoryCustom {

	List<PatientVaccine> findAllByCodesAndDatesAndSexAndAges(String vaccineTypeCode, String vaccineCode, LocalDateTime dateFrom, LocalDateTime dateTo, char sex,
			int ageFrom, int ageTo);

	/**
	 * Returns a page of {@link PatientVaccine}s filtered by vaccine type, vaccine, date range, sex and age.
	 * This method uses Criteria API for dynamic query building with pagination support.
	 *
	 * @param vaccineTypeCode the vaccine type code (can be {@code null})
	 * @param vaccineCode the vaccine code (can be {@code null})
	 * @param dateFrom the start date (can be {@code null})
	 * @param dateTo the end date (can be {@code null})
	 * @param sex the patient sex ('M', 'F' or 'A' for all)
	 * @param ageFrom the minimum age (0 for no minimum)
	 * @param ageTo the maximum age (0 for no maximum)
	 * @param pageable the pagination information
	 * @return a page of {@link PatientVaccine}s
	 * @throws OHServiceException
	 */
	Page<PatientVaccine> findAllByCodesAndDatesAndSexAndAgesWithPagination(
		String vaccineTypeCode,
		String vaccineCode,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		char sex,
		int ageFrom,
		int ageTo,
		Pageable pageable) throws OHServiceException;

	/**
	 * Returns a page of {@link PatientVaccine}s filtered by vaccine type, vaccine, date range, sex, age,
	 * patient search and village.
	 * This method uses Criteria API for dynamic query building with pagination support.
	 *
	 * @param vaccineTypeCode the vaccine type code (can be {@code null})
	 * @param vaccineCode the vaccine code (can be {@code null})
	 * @param dateFrom the start date (can be {@code null})
	 * @param dateTo the end date (can be {@code null})
	 * @param sex the patient sex ('M', 'F' or 'A' for all)
	 * @param ageFrom the minimum age (0 for no minimum)
	 * @param ageTo the maximum age (0 for no maximum)
	 * @param patientSearchText the patient name or code to search (can be empty)
	 * @param villageText the village to filter (can be empty)
	 * @param pageable the pagination information
	 * @return a page of {@link PatientVaccine}s
	 * @throws OHServiceException
	 */
	Page<PatientVaccine> findAllByCodesAndDatesAndSexAndAgesWithPagination(
		String vaccineTypeCode,
		String vaccineCode,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		char sex,
		int ageFrom,
		int ageTo,
		String patientSearchText,
		String villageText,
		Pageable pageable) throws OHServiceException;
}
