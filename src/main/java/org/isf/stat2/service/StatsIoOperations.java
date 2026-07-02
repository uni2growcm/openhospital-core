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
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class StatsIoOperations {

	private final StatsIoOperationRepositoryCustom repository;

	public StatsIoOperations(StatsIoOperationRepositoryCustom statsIoOperationRepositoryCustom) {
		this.repository = statsIoOperationRepositoryCustom;
	}

	/**
	 * Returns a paginated list of {@link Patient}s matching the pregnancy statistics filters.
	 */
	public Page<Patient> getPregnanciesStats(
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
		Double heightMin, Double heightMax,
		Double weightMin, Double weightMax,
		Double systolicMin, Double systolicMax,
		Double diastolicMin, Double diastolicMax,
		Double cardiacMin, Double cardiacMax,
		Double tempMin, Double tempMax,
		Double satMin, Double satMax,
		Double respMin, Double respMax,
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
		int page,
		int size
	) throws OHServiceException {

		return repository.findPregnanciesStatsByFilters(
			ageFrom, ageTo, periodFrom, periodTo,
			exam, examResult, examPeriodFrom, examPeriodTo,
			vaccine, vaccinePeriodFrom, vaccinePeriodTo,
			disease, dischargeType,
			parameterHeight, parameterWeight, parameterArtPress,
			parameterCardFreq, parameterTemp, parameterSaturation, parameterRespRate,
			heightMin, heightMax, weightMin, weightMax, systolicMin,systolicMax, diastolicMin,
			diastolicMax, cardiacMin, cardiacMax, tempMin, tempMax, satMin, satMax, respMin, respMax,
			riskLevel, status,
			gravidityMin, gravidityMax,
			parityMin, parityMax,
			miscarriageMin, miscarriageMax,
			gestationalAgeMin, gestationalAgeMax,
			visitType, visitCountMin, visitCountMax,
			maternalWeightRange, urineProtein, edema,
			fetalPresentation, systolicBpRange, diastolicBpRange,
			PageRequest.of(page, size)
		);
	}
}