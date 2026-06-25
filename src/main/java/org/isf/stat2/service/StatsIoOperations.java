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
		Integer ageFrom, Integer ageTo,
		LocalDateTime periodFrom, LocalDateTime periodTo,
		String exam, String examResult, LocalDateTime examPeriodFrom, LocalDateTime examPeriodTo,
		String vaccine, LocalDateTime vaccinePeriodFrom, LocalDateTime vaccinePeriodTo,
		String disease, String dischargeType,
		boolean parameterHeight, boolean parameterWeight, boolean parameterArtPress,
		boolean parameterCardFreq, boolean parameterTemp, boolean parameterSaturation, boolean parameterRespRate,
		int page, int size
	) throws OHServiceException {
		return repository.findPregnanciesStatsByFilters(
			ageFrom, ageTo, periodFrom, periodTo,
			exam, examResult, examPeriodFrom, examPeriodTo,
			vaccine, vaccinePeriodFrom, vaccinePeriodTo,
			disease, dischargeType,
			parameterHeight, parameterWeight, parameterArtPress,
			parameterCardFreq, parameterTemp, parameterSaturation, parameterRespRate,
			PageRequest.of(page, size)
		);
	}
}