package org.isf.stat2.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.patient.model.Patient;
import org.isf.stat2.service.StatsIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class StatsBrowserManager {

	private final StatsIoOperations ioOperations;

	public StatsBrowserManager(StatsIoOperations statsIoOperations) {
		this.ioOperations = statsIoOperations;
	}

	/**
	 * Validates the filters and returns the paginated pregnancy statistics.
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

		validateFilters(ageFrom, ageTo, periodFrom, periodTo, examPeriodFrom, examPeriodTo,
			vaccinePeriodFrom, vaccinePeriodTo);

		return ioOperations.getPregnanciesStats(
			ageFrom, ageTo, periodFrom, periodTo,
			exam, examResult, examPeriodFrom, examPeriodTo,
			vaccine, vaccinePeriodFrom, vaccinePeriodTo,
			disease, dischargeType,
			parameterHeight, parameterWeight, parameterArtPress,
			parameterCardFreq, parameterTemp, parameterSaturation, parameterRespRate,
			page, size
		);
	}

	private void validateFilters(
		Integer ageFrom, Integer ageTo,
		LocalDateTime periodFrom, LocalDateTime periodTo,
		LocalDateTime examPeriodFrom, LocalDateTime examPeriodTo,
		LocalDateTime vaccinePeriodFrom, LocalDateTime vaccinePeriodTo
	) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (ageFrom != null && ageTo != null && ageFrom > ageTo) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.stat.error.pleaseinsertvalidagerange")));
		}
		if (periodFrom != null && periodTo != null && periodTo.isBefore(periodFrom)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.stat.error.pleaseinsertvalidadmissionperiod")));
		}
		if (examPeriodFrom != null && examPeriodTo != null && examPeriodTo.isBefore(examPeriodFrom)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.stat.error.pleaseinsertvalidexamperiod")));
		}
		if (vaccinePeriodFrom != null && vaccinePeriodTo != null && vaccinePeriodTo.isBefore(vaccinePeriodFrom)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.stat.error.pleaseinsertvalidvaccineperiod")));
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}