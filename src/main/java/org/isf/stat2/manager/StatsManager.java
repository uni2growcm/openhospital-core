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
package org.isf.stat2.manager;

import org.isf.patient.model.Patient;
import org.isf.stat2.model.DiseaseStat;
import org.isf.stat2.model.ExamStat;
import org.isf.stat2.model.OperationStat;
import org.isf.stat2.model.StatsDelivery;
import org.isf.stat2.model.VaccineStat;
import org.isf.stat2.service.StatsIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StatsManager {

	private final StatsIoOperations statsIoOperations;

	public StatsManager(StatsIoOperations statsIoOperations) {
		this.statsIoOperations = statsIoOperations;
	}

	/**
	 * Retrieves a paginated list of patients matching the specified filters.
	 *
	 * @param startIndex            Starting position for pagination
	 * @param limit                 Number of items per page
	 * @param ageFrom               Minimum age
	 * @param ageTo                 Maximum age
	 * @param periodFrom            Admission start date
	 * @param periodTo              Admission end date
	 * @param sex                   Patient sex
	 * @param ward                  Ward description
	 * @param exam                  Exam description
	 * @param examResult            Exam result
	 * @param examPeriodFrom        Exam start date
	 * @param examPeriodTo          Exam end date
	 * @param vaccine               Vaccine description
	 * @param vaccinePeriodFrom     Vaccination start date
	 * @param vaccinePeriodTo       Vaccination end date
	 * @param operation             Operation description
	 * @param operationResult       Operation result
	 * @param operationPeriodFrom   Operation start date
	 * @param operationPeriodTo     Operation end date
	 * @param disease               Disease description
	 * @param dischargeType         Discharge type description
	 * @return                      List of patients
	 * @throws OHServiceException if an error occurs during the database operation
	 */
	public List<Patient> getPatientsStats(
		int startIndex, int limit, int ageFrom, int ageTo, String periodFrom, String periodTo,
		String sex, String ward, String exam, String examResult, String examPeriodFrom, String examPeriodTo,
		String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo, String operation, String operationResult,
		String operationPeriodFrom, String operationPeriodTo, String disease, String dischargeType) throws OHServiceException {

		Page<Patient> page = statsIoOperations.getPatientsStats(startIndex, limit, ageFrom, ageTo, periodFrom, periodTo,
			sex, ward, exam, examResult, examPeriodFrom, examPeriodTo, vaccine, vaccinePeriodFrom, vaccinePeriodTo, operation, operationResult,
			operationPeriodFrom, operationPeriodTo, disease, dischargeType );
		return page.getContent();
	}

	/**
	 * Counts the total number of patients matching the applied filters.
	 *
	 * @param ageFrom               Minimum patient age
	 * @param ageTo                 Maximum patient age
	 * @param periodFrom            Admission start date
	 * @param periodTo              Admission end date
	 * @param sex                   Patient sex
	 * @param ward                  Ward name
	 * @param exam                  Exam name
	 * @param examResult            Exam result
	 * @param examPeriodFrom        Exam date start
	 * @param examPeriodTo          Exam date end
	 * @param vaccine               Vaccine name
	 * @param vaccinePeriodFrom     Vaccine date start
	 * @param vaccinePeriodTo       Vaccine date end
	 * @param operation             Operation name
	 * @param operationResult       Operation result
	 * @param operationPeriodFrom   Operation date start
	 * @param operationPeriodTo     Operation date end
	 * @param disease               Disease name
	 * @param dischargeType         Discharge type name
	 * @return                      Total number of patients matching the filters
	 * @throws OHServiceException   If a database error occurs
	 */
	public int getPatientsStatsCount(
		int ageFrom, int ageTo, String periodFrom, String periodTo, String sex, String ward,
		String exam, String examResult, String examPeriodFrom, String examPeriodTo, String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo,
		String operation, String operationResult, String operationPeriodFrom, String operationPeriodTo,
		String disease, String dischargeType) throws OHServiceException {

		long count = statsIoOperations.getPatientsStatsCount(ageFrom, ageTo, periodFrom, periodTo, sex, ward,
			exam, examResult, examPeriodFrom, examPeriodTo, vaccine, vaccinePeriodFrom, vaccinePeriodTo,
			operation, operationResult, operationPeriodFrom, operationPeriodTo, disease, dischargeType );
		return (int) count;
	}

	/**
	 * Retrieves vaccine statistics grouped by vaccine, showing the number of male and female patients who received each vaccine.
	 *
	 * @param startIndex            Starting position for pagination
	 * @param limit                 Number of records per page
	 * @param ageFrom               Minimum patient age
	 * @param ageTo                 Maximum patient age
	 * @param periodFrom            Admission start date
	 * @param periodTo              Admission end date
	 * @param ward                  Ward name
	 * @param exam                  Exam name
	 * @param examResult            Exam result value
	 * @param examPeriodFrom        Exam date start
	 * @param examPeriodTo          Exam date end
	 * @param vaccinePeriodFrom     Vaccine administration date start
	 * @param vaccinePeriodTo       Vaccine administration date end
	 * @param operation             Operation name
	 * @param operationResult       Operation result
	 * @param operationPeriodFrom   Operation date start
	 * @param operationPeriodTo     Operation date end
	 * @param disease               Disease name
	 * @param dischargeType         Discharge type name
	 * @return                      List of VaccineStat objects
	 * @throws OHServiceException   If a database error occurs
	 */
	public List<VaccineStat> getVaccinesStats(
		int startIndex, int limit, int ageFrom, int ageTo, String periodFrom, String periodTo,
		String ward, String exam, String examResult, String examPeriodFrom, String examPeriodTo,
		String vaccinePeriodFrom, String vaccinePeriodTo, String operation, String operationResult,
		String operationPeriodFrom, String operationPeriodTo, String disease, String dischargeType) throws OHServiceException {

		Page<VaccineStat> page = statsIoOperations.getVaccinesStats(startIndex, limit, ageFrom, ageTo,
			periodFrom, periodTo, ward, exam, examResult, examPeriodFrom, examPeriodTo, vaccinePeriodFrom, vaccinePeriodTo,
			operation, operationResult, operationPeriodFrom, operationPeriodTo, disease, dischargeType );
		return page.getContent();
	}

	/**
	 * Counts the total number of vaccines that have statistics matching the applied filters.
	 *
	 * @param ageFrom               Minimum patient age
	 * @param ageTo                 Maximum patient age
	 * @param periodFrom            Admission start date
	 * @param periodTo              Admission end date
	 * @param ward                  Ward name
	 * @param exam                  Exam name
	 * @param examResult            Exam result value
	 * @param examPeriodFrom        Exam date start
	 * @param examPeriodTo          Exam date end
	 * @param vaccinePeriodFrom     Vaccine administration date start
	 * @param vaccinePeriodTo       Vaccine administration date end
	 * @param operation             Operation name
	 * @param operationResult       Operation result
	 * @param operationPeriodFrom   Operation date start
	 * @param operationPeriodTo     Operation date end
	 * @param disease               Disease name
	 * @param dischargeType         Discharge type name
	 * @return                      Total number of distinct vaccines matching the filters
	 * @throws OHServiceException   If a database error occurs
	 */
	public int getVaccinesStatsCount(
		int ageFrom, int ageTo, String periodFrom, String periodTo, String ward, String exam, String examResult,
		String examPeriodFrom, String examPeriodTo, String vaccinePeriodFrom, String vaccinePeriodTo,
		String operation, String operationResult, String operationPeriodFrom, String operationPeriodTo,
		String disease, String dischargeType) throws OHServiceException {

		long count = statsIoOperations.getVaccinesStatsCount(ageFrom, ageTo, periodFrom, periodTo, ward,
			exam, examResult, examPeriodFrom, examPeriodTo, vaccinePeriodFrom, vaccinePeriodTo,
			operation, operationResult, operationPeriodFrom, operationPeriodTo, disease, dischargeType);
		return (int) count;
	}

	/**
	 * Retrieves examination statistics grouped by exam type, showing the number of
	 * male and female patients who underwent each examination.
	 *
	 * @param startIndex            Starting position for pagination
	 * @param limit                 Number of records per page
	 * @param ageFrom               Minimum patient age
	 * @param ageTo                 Maximum patient age
	 * @param periodFrom            Admission start date
	 * @param periodTo              Admission end date
	 * @param ward                  Ward name
	 * @param examResult            Exam result value
	 * @param examPeriodFrom        Exam date start
	 * @param examPeriodTo          Exam date end
	 * @param vaccine               Vaccine name
	 * @param vaccinePeriodFrom     Vaccine administration date start
	 * @param vaccinePeriodTo       Vaccine administration date end
	 * @param operation             Operation name
	 * @param operationResult       Operation result
	 * @param operationPeriodFrom   Operation date start
	 * @param operationPeriodTo     Operation date end
	 * @param disease               Disease name
	 * @param dischargeType         Discharge type name
	 * @return                      List of ExamStat objects
	 * @throws OHServiceException   If a database error occurs
	 */
	public List<ExamStat> getExamsStats(
		int startIndex, int limit, int ageFrom, int ageTo, String periodFrom, String periodTo, String ward,
		String examResult, String examPeriodFrom, String examPeriodTo, String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo,
		String operation, String operationResult, String operationPeriodFrom, String operationPeriodTo, String disease, String dischargeType) throws OHServiceException {

		Page<ExamStat> page = statsIoOperations.getExamsStats(startIndex, limit, ageFrom, ageTo, periodFrom, periodTo,
			ward, examResult, examPeriodFrom, examPeriodTo, vaccine, vaccinePeriodFrom, vaccinePeriodTo,
			operation, operationResult, operationPeriodFrom, operationPeriodTo, disease, dischargeType);
		return page.getContent();
	}

	/**
	 * Counts the total number of exams matching the applied filters.
	 *
	 * @param ageFrom               Minimum patient age
	 * @param ageTo                 Maximum patient age
	 * @param periodFrom            Admission start date
	 * @param periodTo              Admission end date
	 * @param ward                  Ward name
	 * @param examResult            Exam result value
	 * @param examPeriodFrom        Exam date start
	 * @param examPeriodTo          Exam date end
	 * @param vaccine               Vaccine name
	 * @param vaccinePeriodFrom     Vaccine administration date start
	 * @param vaccinePeriodTo       Vaccine administration date end
	 * @param operation             Operation name
	 * @param operationResult       Operation result
	 * @param operationPeriodFrom   Operation date start
	 * @param operationPeriodTo     Operation date end
	 * @param disease               Disease name
	 * @param dischargeType         Discharge type name
	 * @return                      Total number of exams matching the filters
	 * @throws OHServiceException   If a database error occurs
	 */
	public int getExamsStatsCount(
		int ageFrom, int ageTo, String periodFrom, String periodTo, String ward, String examResult,
		String examPeriodFrom, String examPeriodTo, String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo,
		String operation, String operationResult, String operationPeriodFrom, String operationPeriodTo, String disease, String dischargeType) throws OHServiceException {

		long count = statsIoOperations.getExamsStatsCount(ageFrom, ageTo, periodFrom, periodTo, ward,
			examResult, examPeriodFrom, examPeriodTo, vaccine, vaccinePeriodFrom, vaccinePeriodTo,
			operation, operationResult, operationPeriodFrom, operationPeriodTo, disease, dischargeType );
		return (int) count;
	}

	/**
	 * Retrieves disease statistics grouped by disease, showing the number of
	 * male and female patients diagnosed with each disease.
	 *
	 * @param startIndex            Starting position for pagination
	 * @param limit                 Number of records per page
	 * @param ageFrom               Minimum patient age
	 * @param ageTo                 Maximum patient age
	 * @param periodFrom            Admission start date
	 * @param periodTo              Admission end date
	 * @param ward                  Ward name
	 * @param exam                  Exam name
	 * @param examResult            Exam result value
	 * @param examPeriodFrom        Exam date start
	 * @param examPeriodTo          Exam date end
	 * @param vaccine               Vaccine name
	 * @param vaccinePeriodFrom     Vaccine administration date start
	 * @param vaccinePeriodTo       Vaccine administration date end
	 * @param operation             Operation name
	 * @param operationResult       Operation result
	 * @param operationPeriodFrom   Operation date start
	 * @param operationPeriodTo     Operation date end
	 * @param dischargeType         Discharge type name
	 * @return                      List of DiseaseStat objects
	 * @throws OHServiceException   If a database error occurs
	 */
	public List<DiseaseStat> getDiseasesStats(
		int startIndex, int limit, int ageFrom, int ageTo, String periodFrom, String periodTo,
		String ward, String exam, String examResult, String examPeriodFrom, String examPeriodTo,
		String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo, String operation, String operationResult,
		String operationPeriodFrom, String operationPeriodTo, String dischargeType) throws OHServiceException {

		Page<DiseaseStat> page = statsIoOperations.getDiseasesStats(startIndex, limit, ageFrom, ageTo,
			periodFrom, periodTo, ward, exam, examResult, examPeriodFrom, examPeriodTo, vaccine, vaccinePeriodFrom, vaccinePeriodTo,
			operation, operationResult, operationPeriodFrom, operationPeriodTo, dischargeType);
		return page.getContent();
	}

	/**
	 * Counts the total number of diseases matching the applied filters.
	 *
	 * @param ageFrom               Minimum patient age
	 * @param ageTo                 Maximum patient age
	 * @param periodFrom            Admission start date
	 * @param periodTo              Admission end date
	 * @param ward                  Ward name
	 * @param exam                  Exam name
	 * @param examResult            Exam result value
	 * @param examPeriodFrom        Exam date start
	 * @param examPeriodTo          Exam date end
	 * @param vaccine               Vaccine name
	 * @param vaccinePeriodFrom     Vaccine administration date start
	 * @param vaccinePeriodTo       Vaccine administration date end
	 * @param operation             Operation name
	 * @param operationResult       Operation result
	 * @param operationPeriodFrom   Operation date start
	 * @param operationPeriodTo     Operation date end
	 * @param dischargeType         Discharge type name
	 * @return                      Total number of diseases matching the filters
	 * @throws OHServiceException   If a database error occurs
	 */
	public int getDiseasesStatsCount(
		int ageFrom, int ageTo, String periodFrom, String periodTo, String ward, String exam, String examResult,
		String examPeriodFrom, String examPeriodTo, String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo,
		String operation, String operationResult, String operationPeriodFrom, String operationPeriodTo,
		String dischargeType) throws OHServiceException {

		long count = statsIoOperations.getDiseasesStatsCount(ageFrom, ageTo, periodFrom, periodTo, ward,
			exam, examResult, examPeriodFrom, examPeriodTo, vaccine, vaccinePeriodFrom, vaccinePeriodTo,
			operation, operationResult, operationPeriodFrom, operationPeriodTo, dischargeType);
		return (int) count;
	}

	/**
	 * Retrieves operation statistics grouped by operation type, showing the number of male and female patients who underwent each operation.
	 *
	 * @param startIndex            Starting position for pagination
	 * @param limit                 Number of records per page
	 * @param ageFrom               Minimum patient age
	 * @param ageTo                 Maximum patient age
	 * @param periodFrom            Admission start date
	 * @param periodTo              Admission end date
	 * @param ward                  Ward name
	 * @param exam                  Exam name
	 * @param examResult            Exam result value
	 * @param examPeriodFrom        Exam date start
	 * @param examPeriodTo          Exam date end
	 * @param vaccine               Vaccine name
	 * @param vaccinePeriodFrom     Vaccine administration date start
	 * @param vaccinePeriodTo       Vaccine administration date end
	 * @param operationResult       Operation result
	 * @param operationPeriodFrom   Operation date start
	 * @param operationPeriodTo     Operation date end
	 * @param disease               Disease name
	 * @param dischargeType         Discharge type name
	 * @return                      List of OperationStat objects
	 * @throws OHServiceException   If a database error occurs
	 */
	public List<OperationStat> getOperationsStats(
		int startIndex, int limit, int ageFrom, int ageTo, String periodFrom, String periodTo,
		String ward, String exam, String examResult, String examPeriodFrom, String examPeriodTo,
		String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo, String operationResult,
		String operationPeriodFrom, String operationPeriodTo, String disease, String dischargeType) throws OHServiceException {

		Page<OperationStat> page = statsIoOperations.getOperationsStats(startIndex, limit, ageFrom, ageTo,
			periodFrom, periodTo, ward, exam, examResult, examPeriodFrom, examPeriodTo,
			vaccine, vaccinePeriodFrom, vaccinePeriodTo, operationResult, operationPeriodFrom, operationPeriodTo, disease, dischargeType);
		return page.getContent();
	}

	/**
	 * Counts the total number of operations matching the applied filters.
	 *
	 * @param ageFrom               Minimum patient age
	 * @param ageTo                 Maximum patient age
	 * @param periodFrom            Admission start date
	 * @param periodTo              Admission end date
	 * @param ward                  Ward name
	 * @param exam                  Exam name
	 * @param examResult            Exam result value
	 * @param examPeriodFrom        Exam date start
	 * @param examPeriodTo          Exam date end
	 * @param vaccine               Vaccine name
	 * @param vaccinePeriodFrom     Vaccine administration date start
	 * @param vaccinePeriodTo       Vaccine administration date end
	 * @param operationResult       Operation result
	 * @param operationPeriodFrom   Operation date start
	 * @param operationPeriodTo     Operation date end
	 * @param disease               Disease name
	 * @param dischargeType         Discharge type name
	 * @return                      Total number of operations matching the filters
	 * @throws OHServiceException   If a database error occurs
	 */
	public int getOperationsStatsCount(
		int ageFrom, int ageTo, String periodFrom, String periodTo, String ward, String exam, String examResult,
		String examPeriodFrom, String examPeriodTo, String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo,
		String operationResult, String operationPeriodFrom, String operationPeriodTo, String disease, String dischargeType) throws OHServiceException {

		long count = statsIoOperations.getOperationsStatsCount(ageFrom, ageTo, periodFrom, periodTo, ward,
			exam, examResult, examPeriodFrom, examPeriodTo, vaccine, vaccinePeriodFrom, vaccinePeriodTo, operationResult,
			operationPeriodFrom, operationPeriodTo, disease, dischargeType );
		return (int) count;
	}

	/**
	 * Retrieves a paginated list of deliveries matching the applied filters.
	 *
	 * @param startIndex            Starting position for pagination
	 * @param limit                 Number of records per page
	 * @param weightFrom            Minimum baby weight in grams
	 * @param weightTo              Maximum baby weight in grams
	 * @param periodFrom            Delivery start date
	 * @param periodTo              Delivery end date
	 * @param sex                   Baby sex
	 * @param deliveryType          Delivery type name
	 * @param deliveryResultType    Delivery result type name
	 * @param disease               Disease name of the mother
	 * @param dischargeType         Discharge type name of the mother
	 * @return                      List of StatsDelivery objects
	 * @throws OHServiceException   If a database error occurs
	 */
	public List<StatsDelivery> getDeliveriesStats(int startIndex, int limit, int weightFrom, int weightTo,
		String periodFrom, String periodTo, String sex, String deliveryType, String deliveryResultType,
		String disease, String dischargeType) throws OHServiceException {

		Page<StatsDelivery> page = statsIoOperations.getDeliveriesStats(startIndex, limit, weightFrom, weightTo,
			periodFrom, periodTo, sex, deliveryType, deliveryResultType, disease, dischargeType );
		return page.getContent();
	}

	/**
	 * Counts the total number of deliveries matching the applied filters.
	 *
	 * @param weightFrom            Minimum baby weight in grams
	 * @param weightTo              Maximum baby weight in grams
	 * @param periodFrom            Delivery start date
	 * @param periodTo              Delivery end date
	 * @param sex                   Baby sex
	 * @param deliveryType          Delivery type name
	 * @param deliveryResultType    Delivery result type name
	 * @param disease               Disease name of the mother
	 * @param dischargeType         Discharge type name of the mother
	 * @return                      Total number of deliveries matching the filters
	 * @throws OHServiceException   If a database error occurs
	 */
	public int getDeliveriesStatsCount(
		int weightFrom, int weightTo, String periodFrom, String periodTo, String sex,
		String deliveryType, String deliveryResultType, String disease, String dischargeType) throws OHServiceException {

		long count = statsIoOperations.getDeliveriesStatsCount(weightFrom, weightTo, periodFrom, periodTo,
			sex, deliveryType, deliveryResultType, disease, dischargeType );
		return (int) count;
	}

	/**
	 * Retrieves a paginated list of pregnant patients matching the applied filters.
	 *
	 * @param startIndex            Starting position for pagination
	 * @param limit                 Number of records per page
	 * @param ageFrom               Minimum patient age
	 * @param ageTo                 Maximum patient age
	 * @param periodFrom            Pregnancy visit start date
	 * @param periodTo              Pregnancy visit end date
	 * @param exam                  Exam name
	 * @param examResult            Exam result value
	 * @param examPeriodFrom        Exam date start
	 * @param examPeriodTo          Exam date end
	 * @param vaccine               Vaccine name
	 * @param vaccinePeriodFrom     Vaccine administration date start
	 * @param vaccinePeriodTo       Vaccine administration date end
	 * @param disease               Disease name
	 * @param dischargeType         Discharge type name
	 * @param parameterHeight       Filter by height parameter presence
	 * @param parameterWeight       Filter by weight parameter presence
	 * @param parameterArtPress     Filter by arterial pressure parameter presence
	 * @param parameterCardFreq     Filter by cardiac frequency parameter presence
	 * @param parameterTemp         Filter by temperature parameter presence
	 * @param parameterSaturation   Filter by saturation parameter presence
	 * @param parameterRespRate     Filter by respiratory rate parameter presence
	 * @return                      List of Patient objects
	 * @throws OHServiceException   If a database error occurs
	 */
	public List<Patient> getPregnanciesStats(
		int startIndex, int limit, int ageFrom, int ageTo, String periodFrom, String periodTo,
		String exam, String examResult, String examPeriodFrom, String examPeriodTo, String vaccine,
		String vaccinePeriodFrom, String vaccinePeriodTo, String disease, String dischargeType,
		Boolean parameterHeight, Boolean parameterWeight, Boolean parameterArtPress, Boolean parameterCardFreq,
		Boolean parameterTemp, Boolean parameterSaturation, Boolean parameterRespRate) throws OHServiceException {

		Page<Patient> page = statsIoOperations.getPregnanciesStats(startIndex, limit, ageFrom, ageTo, periodFrom, periodTo,
			exam, examResult, examPeriodFrom, examPeriodTo, vaccine, vaccinePeriodFrom, vaccinePeriodTo,
			disease, dischargeType, parameterHeight, parameterWeight, parameterArtPress, parameterCardFreq,
			parameterTemp, parameterSaturation, parameterRespRate );
		return page.getContent();
	}

	/**
	 * Counts the total number of pregnant patients matching the applied filters.
	 *
	 * @param ageFrom               Minimum patient age
	 * @param ageTo                 Maximum patient age
	 * @param periodFrom            Pregnancy visit start date
	 * @param periodTo              Pregnancy visit end date
	 * @param exam                  Exam name
	 * @param examResult            Exam result value
	 * @param examPeriodFrom        Exam date start
	 * @param examPeriodTo          Exam date end
	 * @param vaccine               Vaccine name
	 * @param vaccinePeriodFrom     Vaccine administration date start
	 * @param vaccinePeriodTo       Vaccine administration date end
	 * @param disease               Disease name
	 * @param dischargeType         Discharge type name
	 * @param parameterHeight       Filter by height parameter presence
	 * @param parameterWeight       Filter by weight parameter presence
	 * @param parameterArtPress     Filter by arterial pressure parameter presence
	 * @param parameterCardFreq     Filter by cardiac frequency parameter presence
	 * @param parameterTemp         Filter by temperature parameter presence
	 * @param parameterSaturation   Filter by saturation parameter presence
	 * @param parameterRespRate     Filter by respiratory rate parameter presence
	 * @return                      Total number of pregnant patients matching the filters
	 * @throws OHServiceException   If a database error occurs
	 */
	public int getPregnanciesStatsCount(
		int ageFrom, int ageTo, String periodFrom, String periodTo, String exam, String examResult,
		String examPeriodFrom, String examPeriodTo, String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo,
		String disease, String dischargeType, Boolean parameterHeight, Boolean parameterWeight,
		Boolean parameterArtPress, Boolean parameterCardFreq, Boolean parameterTemp, Boolean parameterSaturation,
		Boolean parameterRespRate) throws OHServiceException {

		long count = statsIoOperations.getPregnanciesStatsCount(
			ageFrom, ageTo, periodFrom, periodTo, exam, examResult, examPeriodFrom, examPeriodTo,
			vaccine, vaccinePeriodFrom, vaccinePeriodTo, disease, dischargeType, parameterHeight, parameterWeight,
			parameterArtPress, parameterCardFreq, parameterTemp, parameterSaturation, parameterRespRate );
		return (int) count;
	}
}