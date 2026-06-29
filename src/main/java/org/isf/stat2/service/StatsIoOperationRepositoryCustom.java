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