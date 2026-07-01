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
package org.isf.stat2.service;

import java.time.LocalDateTime;

import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StatsIoOperationRepositoryCustom {

	Page<Patient> findPregnanciesStatsByFilters(
		Integer ageFrom,
		Integer ageTo,
		LocalDateTime periodFrom,
		LocalDateTime periodTo,
		String exam,
		String examResult,
		LocalDateTime examPeriodFrom,
		LocalDateTime examPeriodTo,
		String vaccine,
		LocalDateTime vaccinePeriodFrom,
		LocalDateTime vaccinePeriodTo,
		String disease,
		String dischargeType,
		boolean parameterHeight,
		boolean parameterWeight,
		boolean parameterArtPress,
		boolean parameterCardFreq,
		boolean parameterTemp,
		boolean parameterSaturation,
		boolean parameterRespRate,
		String riskLevel,
		String status,
		Integer gravidityMin,
		Integer gravidityMax,
		Integer parityMin,
		Integer parityMax,
		Integer miscarriageMin,
		Integer miscarriageMax,
		Integer gestationalAgeMin,
		Integer gestationalAgeMax,
		String visitType,
		Integer visitCountMin,
		Integer visitCountMax,
		String maternalWeightRange,
		String urineProtein,
		String edema,
		String fetalPresentation,
		String systolicBpRange,
		String diastolicBpRange,
		Pageable pageable
	) throws OHServiceException;
}