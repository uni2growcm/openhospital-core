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

		// ===== BLOC 3 : DELIVERY FILTERS (à venir) =====
		// String deliveryType,
		// String deliveryMode,
		// String laborDuration,
		// String romRange,
		// String perinealIntegrity,
		// Boolean placentaComplete,
		// String bloodLossRange,

		// ===== BLOC 4 : NEWBORN FILTERS (à venir) =====
		// String newbornSex,
		// String birthWeightRange,
		// String neonatalStatus,
		// String apgar1Range,
		// String apgar5Range,
		// Boolean resuscitationRequired,
		// String cryTime,
		// String hivStatus,
		// Boolean congenitalAnomalies,
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
			riskLevel, status,
			gravidityMin, gravidityMax,
			parityMin, parityMax,
			miscarriageMin, miscarriageMax,
			gestationalAgeMin, gestationalAgeMax,
			visitType, visitCountMin, visitCountMax,
			maternalWeightRange, urineProtein, edema,
			fetalPresentation, systolicBpRange, diastolicBpRange,

			// BLOC 3 : Delivery Filters (à venir)
			// deliveryType, deliveryMode, laborDuration, romRange,
			// perinealIntegrity, placentaComplete, bloodLossRange,

			// BLOC 4 : Newborn Filters (à venir)
			// newbornSex, birthWeightRange, neonatalStatus,
			// apgar1Range, apgar5Range, resuscitationRequired,
			// cryTime, hivStatus, congenitalAnomalies,

			PageRequest.of(page, size)
		);
	}
}