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

import org.isf.patient.model.Patient;
import org.isf.stat2.model.DiseaseStat;
import org.isf.stat2.model.ExamStat;
import org.isf.stat2.model.OperationStat;
import org.isf.stat2.model.StatsDelivery;
import org.isf.stat2.model.VaccineStat;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(rollbackFor = OHServiceException.class)
public class StatsIoOperations {

	@PersistenceContext
	private EntityManager entityManager;

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
	 * @throws OHServiceException if an error occurs during the database operation
	 */
	public Page<Patient> getPatientsStats(
		int startIndex, int limit, int ageFrom, int ageTo, String periodFrom, String periodTo,
		String sex, String ward, String exam, String examResult, String examPeriodFrom, String examPeriodTo,
		String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo, String operation, String operationResult,
		String operationPeriodFrom, String operationPeriodTo, String disease, String dischargeType) throws OHServiceException {

		StringBuilder sqlSelect = new StringBuilder();
		sqlSelect.append("SELECT DISTINCT p FROM Patient p ");

		StringBuilder sqlWhere = new StringBuilder();
		sqlWhere.append("WHERE (p.deleted = 'N' OR p.deleted IS NULL) ");
		List<Object> parameters = new ArrayList<>();

		if (ageFrom > 0) {
			sqlWhere.append("AND p.age >= ? ");
			parameters.add(ageFrom);
		}
		if (ageTo > 0) {
			sqlWhere.append("AND p.age <= ? ");
			parameters.add(ageTo);
		}

		if (sex != null && !sex.isEmpty()) {
			sqlWhere.append("AND p.sex = ? ");
			parameters.add(sex);
		}

		boolean hasAdmissionFilter = false;
		if ((periodFrom != null && !periodFrom.isEmpty()) ||
			(periodTo != null && !periodTo.isEmpty())) {

			sqlSelect.append("LEFT JOIN Admission a ON a.patient = p ");
			hasAdmissionFilter = true;

			if (periodFrom != null && !periodFrom.isEmpty()) {
				sqlWhere.append("AND a.dischargeDate >= ? ");
				parameters.add(parseDate(periodFrom));
			}
			if (periodTo != null && !periodTo.isEmpty()) {
				sqlWhere.append("AND a.dischargeDate <= ? ");
				parameters.add(parseDate(periodTo));
			}
		}

		if (ward != null && !ward.isEmpty()) {
			if (!hasAdmissionFilter) {
				sqlSelect.append("LEFT JOIN Admission a ON a.patient = p ");
				hasAdmissionFilter = true;
			}
			sqlSelect.append("LEFT JOIN Ward w ON a.ward = w ");
			sqlWhere.append("AND w.description = ? ");
			parameters.add(ward);
		}

		if (exam != null && !exam.isEmpty()) {
			sqlSelect.append("INNER JOIN Laboratory l ON l.patient = p ");
			sqlSelect.append("INNER JOIN Exam e ON l.exam = e ");
			sqlWhere.append("AND e.description = ? ");
			parameters.add(exam);

			if (examResult != null && !examResult.isEmpty()) {
				sqlWhere.append("AND l.result = ? ");
				parameters.add(examResult);
			}
			if (examPeriodFrom != null && !examPeriodFrom.isEmpty()) {
				sqlWhere.append("AND l.labDate >= ? ");
				parameters.add(parseDate(examPeriodFrom));
			}
			if (examPeriodTo != null && !examPeriodTo.isEmpty()) {
				sqlWhere.append("AND l.labDate <= ? ");
				parameters.add(parseDate(examPeriodTo));
			}
		}

		if (vaccine != null && !vaccine.isEmpty()) {
			sqlSelect.append("INNER JOIN PatientVaccine pv ON pv.patient = p ");
			sqlSelect.append("INNER JOIN Vaccine v ON pv.vaccine = v ");
			sqlWhere.append("AND v.description = ? ");
			parameters.add(vaccine);

			if (vaccinePeriodFrom != null && !vaccinePeriodFrom.isEmpty()) {
				sqlWhere.append("AND pv.date >= ? ");
				parameters.add(parseDate(vaccinePeriodFrom));
			}
			if (vaccinePeriodTo != null && !vaccinePeriodTo.isEmpty()) {
				sqlWhere.append("AND pv.date <= ? ");
				parameters.add(parseDate(vaccinePeriodTo));
			}
		}

		if (operation != null && !operation.isEmpty()) {
			if (!hasAdmissionFilter) {
				sqlSelect.append("LEFT JOIN Admission a ON a.patient = p ");
				hasAdmissionFilter = true;
			}
			sqlSelect.append("LEFT JOIN Opd o ON o.patient = p ");
			sqlSelect.append("INNER JOIN OperationRow orow ON (orow.admission = a OR orow.opd = o) ");
			sqlSelect.append("INNER JOIN Operation op ON orow.operation = op ");
			sqlWhere.append("AND op.description = ? ");
			parameters.add(operation);

			if (operationResult != null && !operationResult.isEmpty()) {
				sqlWhere.append("AND orow.result = ? ");
				parameters.add(operationResult);
			}
			if (operationPeriodFrom != null && !operationPeriodFrom.isEmpty()) {
				sqlWhere.append("AND orow.operationDate >= ? ");
				parameters.add(parseDate(operationPeriodFrom));
			}
			if (operationPeriodTo != null && !operationPeriodTo.isEmpty()) {
				sqlWhere.append("AND orow.operationDate <= ? ");
				parameters.add(parseDate(operationPeriodTo));
			}
		}

		if (disease != null && !disease.isEmpty()) {
			if (!hasAdmissionFilter) {
				sqlSelect.append("LEFT JOIN Admission a ON a.patient = p ");
				hasAdmissionFilter = true;
			}
			sqlSelect.append("INNER JOIN Disease d ON (a.diseaseOut1 = d OR a.diseaseOut2 = d OR a.diseaseOut3 = d) ");
			sqlWhere.append("AND d.description = ? ");
			parameters.add(disease);
		}

		if (dischargeType != null && !dischargeType.isEmpty()) {
			if (!hasAdmissionFilter) {
				sqlSelect.append("LEFT JOIN Admission a ON a.patient = p ");
				hasAdmissionFilter = true;
			}
			sqlSelect.append("INNER JOIN DischargeType dt ON a.dischargeType = dt ");
			sqlWhere.append("AND dt.description = ? ");
			parameters.add(dischargeType);
		}

		String sql = sqlSelect.toString() + sqlWhere.toString();

		Pageable pageable = PageRequest.of(startIndex / limit, limit);

		Query query = entityManager.createQuery(sql);
		for (int i = 0; i < parameters.size(); i++) {
			query.setParameter(i + 1, parameters.get(i));
		}
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		@SuppressWarnings("unchecked")
		List<Patient> patients = query.getResultList();

		String countSql = "SELECT COUNT(DISTINCT p) " + sql.substring(sql.indexOf("FROM"));
		Query countQuery = entityManager.createQuery(countSql);
		for (int i = 0; i < parameters.size(); i++) {
			countQuery.setParameter(i + 1, parameters.get(i));
		}
		long total = (long) countQuery.getSingleResult();

		return new PageImpl<>(patients, pageable, total);
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
	public long getPatientsStatsCount(
		int ageFrom, int ageTo, String periodFrom, String periodTo, String sex, String ward,
		String exam, String examResult, String examPeriodFrom, String examPeriodTo,
		String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo, String operation, String operationResult,
		String operationPeriodFrom, String operationPeriodTo, String disease, String dischargeType) throws OHServiceException {

		Page<Patient> page = getPatientsStats(0, 1, ageFrom, ageTo, periodFrom, periodTo,
			sex, ward, exam, examResult, examPeriodFrom, examPeriodTo, vaccine, vaccinePeriodFrom, vaccinePeriodTo,
			operation, operationResult, operationPeriodFrom, operationPeriodTo, disease, dischargeType );
		return page.getTotalElements();
	}

	/**
	 * Retrieves vaccine statistics grouped by vaccine, showing the number of
	 * male and female patients who received each vaccine.
	 *
	 * This method aggregates data and returns a paginated list where each row
	 * represents a vaccine with its corresponding gender counts.
	 *
	 * The statistics are calculated based on patients who have received each vaccine,
	 * filtered by the provided criteria. The results are grouped by vaccine ID
	 * and include counts for males and females separately.
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
	 * @return                      Paginated list of VaccineStat objects
	 * @throws OHServiceException   If a database error occurs
	 */
	public Page<VaccineStat> getVaccinesStats(
		int startIndex, int limit, int ageFrom, int ageTo, String periodFrom, String periodTo,
		String ward, String exam, String examResult, String examPeriodFrom, String examPeriodTo,
		String vaccinePeriodFrom, String vaccinePeriodTo, String operation, String operationResult,
		String operationPeriodFrom, String operationPeriodTo, String disease, String dischargeType) throws OHServiceException {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT v, ");
		sql.append("COUNT(DISTINCT CASE WHEN p.sex = 'M' THEN p.id END) AS men, ");
		sql.append("COUNT(DISTINCT CASE WHEN p.sex = 'F' THEN p.id END) AS women ");
		sql.append("FROM Vaccine v ");
		sql.append("LEFT JOIN PatientVaccine pv ON pv.vaccine = v ");
		sql.append("LEFT JOIN Patient p ON pv.patient = p ");

		StringBuilder sqlWhere = new StringBuilder();
		sqlWhere.append("WHERE (p.deleted = 'N' OR p.deleted IS NULL) ");
		List<Object> parameters = new ArrayList<>();

		if (ageFrom > 0) {
			sqlWhere.append("AND p.age >= ? ");
			parameters.add(ageFrom);
		}
		if (ageTo > 0) {
			sqlWhere.append("AND p.age <= ? ");
			parameters.add(ageTo);
		}

		boolean hasAdmissionFilter = false;
		if ((periodFrom != null && !periodFrom.isEmpty()) ||
			(periodTo != null && !periodTo.isEmpty())) {

			sql.append("LEFT JOIN Admission a ON a.patient = p ");
			hasAdmissionFilter = true;

			if (periodFrom != null && !periodFrom.isEmpty()) {
				sqlWhere.append("AND a.dischargeDate >= ? ");
				parameters.add(parseDate(periodFrom));
			}
			if (periodTo != null && !periodTo.isEmpty()) {
				sqlWhere.append("AND a.dischargeDate <= ? ");
				parameters.add(parseDate(periodTo));
			}
		}

		if (ward != null && !ward.isEmpty()) {
			if (!hasAdmissionFilter) {
				sql.append("LEFT JOIN Admission a ON a.patient = p ");
				hasAdmissionFilter = true;
			}
			sql.append("LEFT JOIN Ward w ON a.ward = w ");
			sqlWhere.append("AND w.description = ? ");
			parameters.add(ward);
		}

		if (exam != null && !exam.isEmpty()) {
			sql.append("INNER JOIN Laboratory l ON l.patient = p ");
			sql.append("INNER JOIN Exam e ON l.exam = e ");
			sqlWhere.append("AND e.description = ? ");
			parameters.add(exam);

			if (examResult != null && !examResult.isEmpty()) {
				sqlWhere.append("AND l.result = ? ");
				parameters.add(examResult);
			}
			if (examPeriodFrom != null && !examPeriodFrom.isEmpty()) {
				sqlWhere.append("AND l.labDate >= ? ");
				parameters.add(parseDate(examPeriodFrom));
			}
			if (examPeriodTo != null && !examPeriodTo.isEmpty()) {
				sqlWhere.append("AND l.labDate <= ? ");
				parameters.add(parseDate(examPeriodTo));
			}
		}

		if (vaccinePeriodFrom != null && !vaccinePeriodFrom.isEmpty()) {
			sqlWhere.append("AND pv.date >= ? ");
			parameters.add(parseDate(vaccinePeriodFrom));
		}
		if (vaccinePeriodTo != null && !vaccinePeriodTo.isEmpty()) {
			sqlWhere.append("AND pv.date <= ? ");
			parameters.add(parseDate(vaccinePeriodTo));
		}

		if (operation != null && !operation.isEmpty()) {
			if (!hasAdmissionFilter) {
				sql.append("LEFT JOIN Admission a ON a.patient = p ");
				hasAdmissionFilter = true;
			}
			sql.append("LEFT JOIN Opd o ON o.patient = p ");
			sql.append("INNER JOIN OperationRow orow ON (orow.admission = a OR orow.opd = o) ");
			sql.append("INNER JOIN Operation op ON orow.operation = op ");
			sqlWhere.append("AND op.description = ? ");
			parameters.add(operation);

			if (operationResult != null && !operationResult.isEmpty()) {
				sqlWhere.append("AND orow.result = ? ");
				parameters.add(operationResult);
			}
			if (operationPeriodFrom != null && !operationPeriodFrom.isEmpty()) {
				sqlWhere.append("AND orow.operationDate >= ? ");
				parameters.add(parseDate(operationPeriodFrom));
			}
			if (operationPeriodTo != null && !operationPeriodTo.isEmpty()) {
				sqlWhere.append("AND orow.operationDate <= ? ");
				parameters.add(parseDate(operationPeriodTo));
			}
		}

		if (disease != null && !disease.isEmpty()) {
			if (!hasAdmissionFilter) {
				sql.append("LEFT JOIN Admission a ON a.patient = p ");
				hasAdmissionFilter = true;
			}
			sql.append("INNER JOIN Disease d ON (a.diseaseOut1 = d OR a.diseaseOut2 = d OR a.diseaseOut3 = d) ");
			sqlWhere.append("AND d.description = ? ");
			parameters.add(disease);
		}

		if (dischargeType != null && !dischargeType.isEmpty()) {
			if (!hasAdmissionFilter) {
				sql.append("LEFT JOIN Admission a ON a.patient = p ");
				hasAdmissionFilter = true;
			}
			sql.append("INNER JOIN DischargeType dt ON a.dischargeType = dt ");
			sqlWhere.append("AND dt.description = ? ");
			parameters.add(dischargeType);
		}

		sql.append(sqlWhere);
		sql.append("GROUP BY v.id ");

		Pageable pageable = PageRequest.of(startIndex / limit, limit);
		Query query = entityManager.createQuery(sql.toString());
		for (int i = 0; i < parameters.size(); i++) {
			query.setParameter(i + 1, parameters.get(i));
		}
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();

		List<VaccineStat> stats = new ArrayList<>();
		for (Object[] row : results) {
			VaccineStat stat = new VaccineStat(
				(org.isf.vaccine.model.Vaccine) row[0],
				((Number) row[1]).intValue(),
				((Number) row[2]).intValue()
			);
			stats.add(stat);
		}

		String countSql = "SELECT COUNT(DISTINCT v.id) " + sql.substring(sql.indexOf("FROM"));
		countSql = countSql.replace("GROUP BY v.id ", "");
		Query countQuery = entityManager.createQuery(countSql);
		for (int i = 0; i < parameters.size(); i++) {
			countQuery.setParameter(i + 1, parameters.get(i));
		}
		long total = (long) countQuery.getSingleResult();

		return new PageImpl<>(stats, pageable, total);
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
	public long getVaccinesStatsCount(
		int ageFrom, int ageTo, String periodFrom, String periodTo, String ward,
		String exam, String examResult, String examPeriodFrom, String examPeriodTo,
		String vaccinePeriodFrom, String vaccinePeriodTo, String operation, String operationResult,
		String operationPeriodFrom, String operationPeriodTo, String disease, String dischargeType) throws OHServiceException {

		Page<VaccineStat> page = getVaccinesStats(0, 1, ageFrom, ageTo, periodFrom, periodTo,
			ward, exam, examResult, examPeriodFrom, examPeriodTo, vaccinePeriodFrom, vaccinePeriodTo,
			operation, operationResult, operationPeriodFrom, operationPeriodTo, disease, dischargeType );
		return page.getTotalElements();
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
	 * @return                      Paginated list of ExamStat objects
	 * @throws OHServiceException   If a database error occurs
	 */
	public Page<ExamStat> getExamsStats(
		int startIndex, int limit, int ageFrom, int ageTo, String periodFrom, String periodTo,
		String ward, String examResult, String examPeriodFrom, String examPeriodTo,
		String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo, String operation, String operationResult,
		String operationPeriodFrom, String operationPeriodTo, String disease, String dischargeType) throws OHServiceException {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT e, ");
		sql.append("COUNT(DISTINCT CASE WHEN p.sex = 'M' THEN p.id END) AS men, ");
		sql.append("COUNT(DISTINCT CASE WHEN p.sex = 'F' THEN p.id END) AS women ");
		sql.append("FROM Exam e ");
		sql.append("LEFT JOIN Laboratory l ON l.exam = e ");
		sql.append("LEFT JOIN Patient p ON l.patient = p ");

		StringBuilder sqlWhere = new StringBuilder();
		sqlWhere.append("WHERE (p.deleted = 'N' OR p.deleted IS NULL) ");
		List<Object> parameters = new ArrayList<>();

		if (ageFrom > 0) {
			sqlWhere.append("AND p.age >= ? ");
			parameters.add(ageFrom);
		}
		if (ageTo > 0) {
			sqlWhere.append("AND p.age <= ? ");
			parameters.add(ageTo);
		}

		boolean hasAdmissionFilter = false;
		if ((periodFrom != null && !periodFrom.isEmpty()) ||
			(periodTo != null && !periodTo.isEmpty())) {

			sql.append("LEFT JOIN Admission a ON a.patient = p ");
			hasAdmissionFilter = true;

			if (periodFrom != null && !periodFrom.isEmpty()) {
				sqlWhere.append("AND a.dischargeDate >= ? ");
				parameters.add(parseDate(periodFrom));
			}
			if (periodTo != null && !periodTo.isEmpty()) {
				sqlWhere.append("AND a.dischargeDate <= ? ");
				parameters.add(parseDate(periodTo));
			}
		}

		if (ward != null && !ward.isEmpty()) {
			if (!hasAdmissionFilter) {
				sql.append("LEFT JOIN Admission a ON a.patient = p ");
				hasAdmissionFilter = true;
			}
			sql.append("LEFT JOIN Ward w ON a.ward = w ");
			sqlWhere.append("AND w.description = ? ");
			parameters.add(ward);
		}

		if (examResult != null && !examResult.isEmpty()) {
			sqlWhere.append("AND l.result = ? ");
			parameters.add(examResult);
		}

		if (examPeriodFrom != null && !examPeriodFrom.isEmpty()) {
			sqlWhere.append("AND l.labDate >= ? ");
			parameters.add(parseDate(examPeriodFrom));
		}
		if (examPeriodTo != null && !examPeriodTo.isEmpty()) {
			sqlWhere.append("AND l.labDate <= ? ");
			parameters.add(parseDate(examPeriodTo));
		}

		if (vaccine != null && !vaccine.isEmpty()) {
			sql.append("INNER JOIN PatientVaccine pv ON pv.patient = p ");
			sql.append("INNER JOIN Vaccine v ON pv.vaccine = v ");
			sqlWhere.append("AND v.description = ? ");
			parameters.add(vaccine);

			if (vaccinePeriodFrom != null && !vaccinePeriodFrom.isEmpty()) {
				sqlWhere.append("AND pv.date >= ? ");
				parameters.add(parseDate(vaccinePeriodFrom));
			}
			if (vaccinePeriodTo != null && !vaccinePeriodTo.isEmpty()) {
				sqlWhere.append("AND pv.date <= ? ");
				parameters.add(parseDate(vaccinePeriodTo));
			}
		}

		if (operation != null && !operation.isEmpty()) {
			if (!hasAdmissionFilter) {
				sql.append("LEFT JOIN Admission a ON a.patient = p ");
				hasAdmissionFilter = true;
			}
			sql.append("LEFT JOIN Opd o ON o.patient = p ");
			sql.append("INNER JOIN OperationRow orow ON (orow.admission = a OR orow.opd = o) ");
			sql.append("INNER JOIN Operation op ON orow.operation = op ");
			sqlWhere.append("AND op.description = ? ");
			parameters.add(operation);

			if (operationResult != null && !operationResult.isEmpty()) {
				sqlWhere.append("AND orow.result = ? ");
				parameters.add(operationResult);
			}
			if (operationPeriodFrom != null && !operationPeriodFrom.isEmpty()) {
				sqlWhere.append("AND orow.operationDate >= ? ");
				parameters.add(parseDate(operationPeriodFrom));
			}
			if (operationPeriodTo != null && !operationPeriodTo.isEmpty()) {
				sqlWhere.append("AND orow.operationDate <= ? ");
				parameters.add(parseDate(operationPeriodTo));
			}
		}

		if (disease != null && !disease.isEmpty()) {
			if (!hasAdmissionFilter) {
				sql.append("LEFT JOIN Admission a ON a.patient = p ");
				hasAdmissionFilter = true;
			}
			sql.append("INNER JOIN Disease d ON (a.diseaseOut1 = d OR a.diseaseOut2 = d OR a.diseaseOut3 = d) ");
			sqlWhere.append("AND d.description = ? ");
			parameters.add(disease);
		}

		if (dischargeType != null && !dischargeType.isEmpty()) {
			if (!hasAdmissionFilter) {
				sql.append("LEFT JOIN Admission a ON a.patient = p ");
				hasAdmissionFilter = true;
			}
			sql.append("INNER JOIN DischargeType dt ON a.dischargeType = dt ");
			sqlWhere.append("AND dt.description = ? ");
			parameters.add(dischargeType);
		}

		sql.append(sqlWhere);
		sql.append("GROUP BY e.id ");

		Pageable pageable = PageRequest.of(startIndex / limit, limit);
		Query query = entityManager.createQuery(sql.toString());
		for (int i = 0; i < parameters.size(); i++) {
			query.setParameter(i + 1, parameters.get(i));
		}
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();

		List<ExamStat> stats = new ArrayList<>();
		for (Object[] row : results) {
			ExamStat stat = new ExamStat(
				(org.isf.exa.model.Exam) row[0],
				((Number) row[1]).intValue(),
				((Number) row[2]).intValue()
			);
			stats.add(stat);
		}

		String countSql = "SELECT COUNT(DISTINCT e.id) " + sql.substring(sql.indexOf("FROM"));
		countSql = countSql.replace("GROUP BY e.id ", "");
		Query countQuery = entityManager.createQuery(countSql);
		for (int i = 0; i < parameters.size(); i++) {
			countQuery.setParameter(i + 1, parameters.get(i));
		}
		long total = (long) countQuery.getSingleResult();

		return new PageImpl<>(stats, pageable, total);
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
	public long getExamsStatsCount(
		int ageFrom, int ageTo, String periodFrom, String periodTo, String ward, String examResult,
		String examPeriodFrom, String examPeriodTo, String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo,
		String operation, String operationResult, String operationPeriodFrom, String operationPeriodTo,
		String disease, String dischargeType) throws OHServiceException {

		Page<ExamStat> page = getExamsStats(0, 1, ageFrom, ageTo, periodFrom, periodTo, ward,
			examResult,examPeriodFrom, examPeriodTo, vaccine, vaccinePeriodFrom, vaccinePeriodTo,
			operation, operationResult, operationPeriodFrom, operationPeriodTo, disease, dischargeType
		);
		return page.getTotalElements();
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
	 * @return                      Paginated list of DiseaseStat objects
	 * @throws OHServiceException   If a database error occurs
	 */
	public Page<DiseaseStat> getDiseasesStats(
		int startIndex, int limit, int ageFrom, int ageTo, String periodFrom, String periodTo, String ward,
		String exam, String examResult, String examPeriodFrom, String examPeriodTo,
		String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo, String operation, String operationResult,
		String operationPeriodFrom, String operationPeriodTo, String dischargeType) throws OHServiceException {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT d, ");
		sql.append("COUNT(DISTINCT CASE WHEN p.sex = 'M' THEN p.id END) AS men, ");
		sql.append("COUNT(DISTINCT CASE WHEN p.sex = 'F' THEN p.id END) AS women ");
		sql.append("FROM Disease d ");
		sql.append("LEFT JOIN Admission a ON (a.diseaseOut1 = d OR a.diseaseOut2 = d OR a.diseaseOut3 = d) ");
		sql.append("LEFT JOIN Patient p ON a.patient = p ");

		StringBuilder sqlWhere = new StringBuilder();
		sqlWhere.append("WHERE (p.deleted = 'N' OR p.deleted IS NULL) ");
		List<Object> parameters = new ArrayList<>();

		if (ageFrom > 0) {
			sqlWhere.append("AND p.age >= ? ");
			parameters.add(ageFrom);
		}
		if (ageTo > 0) {
			sqlWhere.append("AND p.age <= ? ");
			parameters.add(ageTo);
		}

		if (periodFrom != null && !periodFrom.isEmpty()) {
			sqlWhere.append("AND a.dischargeDate >= ? ");
			parameters.add(parseDate(periodFrom));
		}
		if (periodTo != null && !periodTo.isEmpty()) {
			sqlWhere.append("AND a.dischargeDate <= ? ");
			parameters.add(parseDate(periodTo));
		}

		if (ward != null && !ward.isEmpty()) {
			sql.append("LEFT JOIN Ward w ON a.ward = w ");
			sqlWhere.append("AND w.description = ? ");
			parameters.add(ward);
		}

		if (exam != null && !exam.isEmpty()) {
			sql.append("INNER JOIN Laboratory l ON l.patient = p ");
			sql.append("INNER JOIN Exam e ON l.exam = e ");
			sqlWhere.append("AND e.description = ? ");
			parameters.add(exam);

			if (examResult != null && !examResult.isEmpty()) {
				sqlWhere.append("AND l.result = ? ");
				parameters.add(examResult);
			}
			if (examPeriodFrom != null && !examPeriodFrom.isEmpty()) {
				sqlWhere.append("AND l.labDate >= ? ");
				parameters.add(parseDate(examPeriodFrom));
			}
			if (examPeriodTo != null && !examPeriodTo.isEmpty()) {
				sqlWhere.append("AND l.labDate <= ? ");
				parameters.add(parseDate(examPeriodTo));
			}
		}

		if (vaccine != null && !vaccine.isEmpty()) {
			sql.append("INNER JOIN PatientVaccine pv ON pv.patient = p ");
			sql.append("INNER JOIN Vaccine v ON pv.vaccine = v ");
			sqlWhere.append("AND v.description = ? ");
			parameters.add(vaccine);

			if (vaccinePeriodFrom != null && !vaccinePeriodFrom.isEmpty()) {
				sqlWhere.append("AND pv.date >= ? ");
				parameters.add(parseDate(vaccinePeriodFrom));
			}
			if (vaccinePeriodTo != null && !vaccinePeriodTo.isEmpty()) {
				sqlWhere.append("AND pv.date <= ? ");
				parameters.add(parseDate(vaccinePeriodTo));
			}
		}

		if (operation != null && !operation.isEmpty()) {
			sql.append("LEFT JOIN Opd o ON o.patient = p ");
			sql.append("INNER JOIN OperationRow orow ON (orow.admission = a OR orow.opd = o) ");
			sql.append("INNER JOIN Operation op ON orow.operation = op ");
			sqlWhere.append("AND op.description = ? ");
			parameters.add(operation);

			if (operationResult != null && !operationResult.isEmpty()) {
				sqlWhere.append("AND orow.result = ? ");
				parameters.add(operationResult);
			}
			if (operationPeriodFrom != null && !operationPeriodFrom.isEmpty()) {
				sqlWhere.append("AND orow.operationDate >= ? ");
				parameters.add(parseDate(operationPeriodFrom));
			}
			if (operationPeriodTo != null && !operationPeriodTo.isEmpty()) {
				sqlWhere.append("AND orow.operationDate <= ? ");
				parameters.add(parseDate(operationPeriodTo));
			}
		}

		if (dischargeType != null && !dischargeType.isEmpty()) {
			sql.append("INNER JOIN DischargeType dt ON a.dischargeType = dt ");
			sqlWhere.append("AND dt.description = ? ");
			parameters.add(dischargeType);
		}

		sql.append(sqlWhere);
		sql.append("GROUP BY d.id ");

		Pageable pageable = PageRequest.of(startIndex / limit, limit);
		Query query = entityManager.createQuery(sql.toString());
		for (int i = 0; i < parameters.size(); i++) {
			query.setParameter(i + 1, parameters.get(i));
		}
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();

		List<DiseaseStat> stats = new ArrayList<>();
		for (Object[] row : results) {
			DiseaseStat stat = new DiseaseStat(
				(org.isf.disease.model.Disease) row[0],
				((Number) row[1]).intValue(),
				((Number) row[2]).intValue()
			);
			stats.add(stat);
		}

		String countSql = "SELECT COUNT(DISTINCT d.id) " + sql.substring(sql.indexOf("FROM"));
		countSql = countSql.replace("GROUP BY d.id ", "");
		Query countQuery = entityManager.createQuery(countSql);
		for (int i = 0; i < parameters.size(); i++) {
			countQuery.setParameter(i + 1, parameters.get(i));
		}
		long total = (long) countQuery.getSingleResult();

		return new PageImpl<>(stats, pageable, total);
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
	public long getDiseasesStatsCount(
		int ageFrom, int ageTo, String periodFrom, String periodTo, String ward,
		String exam, String examResult, String examPeriodFrom, String examPeriodTo,
		String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo, String operation, String operationResult,
		String operationPeriodFrom, String operationPeriodTo, String dischargeType) throws OHServiceException {

		Page<DiseaseStat> page = getDiseasesStats(0, 1, ageFrom, ageTo, periodFrom, periodTo,
			ward, exam, examResult, examPeriodFrom, examPeriodTo, vaccine, vaccinePeriodFrom, vaccinePeriodTo,
			operation, operationResult, operationPeriodFrom, operationPeriodTo, dischargeType );
		return page.getTotalElements();
	}

	/**
	 * Retrieves operation statistics grouped by operation type, showing the number of
	 * male and female patients who underwent each operation.
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
	 * @return                      Paginated list of OperationStat objects
	 * @throws OHServiceException   If a database error occurs
	 */
	public Page<OperationStat> getOperationsStats(
		int startIndex, int limit, int ageFrom, int ageTo, String periodFrom, String periodTo,
		String ward, String exam, String examResult, String examPeriodFrom, String examPeriodTo,
		String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo, String operationResult,
		String operationPeriodFrom, String operationPeriodTo, String disease, String dischargeType) throws OHServiceException {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT op, ");
		sql.append("COUNT(DISTINCT CASE WHEN p.sex = 'M' THEN p.id END) AS men, ");
		sql.append("COUNT(DISTINCT CASE WHEN p.sex = 'F' THEN p.id END) AS women ");
		sql.append("FROM Operation op ");
		sql.append("LEFT JOIN OperationRow orow ON orow.operation = op ");
		sql.append("LEFT JOIN Admission a ON orow.admission = a ");
		sql.append("LEFT JOIN Opd o ON orow.opd = o ");
		sql.append("LEFT JOIN Patient p ON (a.patient = p OR o.patient = p) ");

		StringBuilder sqlWhere = new StringBuilder();
		sqlWhere.append("WHERE (p.deleted = 'N' OR p.deleted IS NULL) ");
		List<Object> parameters = new ArrayList<>();

		if (ageFrom > 0) {
			sqlWhere.append("AND p.age >= ? ");
			parameters.add(ageFrom);
		}
		if (ageTo > 0) {
			sqlWhere.append("AND p.age <= ? ");
			parameters.add(ageTo);
		}

		if (periodFrom != null && !periodFrom.isEmpty()) {
			sqlWhere.append("AND (a.dischargeDate >= ? OR o.date >= ?) ");
			parameters.add(parseDate(periodFrom));
			parameters.add(parseDate(periodFrom));
		}
		if (periodTo != null && !periodTo.isEmpty()) {
			sqlWhere.append("AND (a.dischargeDate <= ? OR o.date <= ?) ");
			parameters.add(parseDate(periodTo));
			parameters.add(parseDate(periodTo));
		}

		if (ward != null && !ward.isEmpty()) {
			sql.append("LEFT JOIN Ward w ON a.ward = w ");
			sqlWhere.append("AND w.description = ? ");
			parameters.add(ward);
		}

		if (exam != null && !exam.isEmpty()) {
			sql.append("INNER JOIN Laboratory l ON l.patient = p ");
			sql.append("INNER JOIN Exam e ON l.exam = e ");
			sqlWhere.append("AND e.description = ? ");
			parameters.add(exam);

			if (examResult != null && !examResult.isEmpty()) {
				sqlWhere.append("AND l.result = ? ");
				parameters.add(examResult);
			}
			if (examPeriodFrom != null && !examPeriodFrom.isEmpty()) {
				sqlWhere.append("AND l.labDate >= ? ");
				parameters.add(parseDate(examPeriodFrom));
			}
			if (examPeriodTo != null && !examPeriodTo.isEmpty()) {
				sqlWhere.append("AND l.labDate <= ? ");
				parameters.add(parseDate(examPeriodTo));
			}
		}

		if (vaccine != null && !vaccine.isEmpty()) {
			sql.append("INNER JOIN PatientVaccine pv ON pv.patient = p ");
			sql.append("INNER JOIN Vaccine v ON pv.vaccine = v ");
			sqlWhere.append("AND v.description = ? ");
			parameters.add(vaccine);

			if (vaccinePeriodFrom != null && !vaccinePeriodFrom.isEmpty()) {
				sqlWhere.append("AND pv.date >= ? ");
				parameters.add(parseDate(vaccinePeriodFrom));
			}
			if (vaccinePeriodTo != null && !vaccinePeriodTo.isEmpty()) {
				sqlWhere.append("AND pv.date <= ? ");
				parameters.add(parseDate(vaccinePeriodTo));
			}
		}
		if (operationResult != null && !operationResult.isEmpty()) {
			sqlWhere.append("AND orow.result = ? ");
			parameters.add(operationResult);
		}
		if (operationPeriodFrom != null && !operationPeriodFrom.isEmpty()) {
			sqlWhere.append("AND orow.operationDate >= ? ");
			parameters.add(parseDate(operationPeriodFrom));
		}
		if (operationPeriodTo != null && !operationPeriodTo.isEmpty()) {
			sqlWhere.append("AND orow.operationDate <= ? ");
			parameters.add(parseDate(operationPeriodTo));
		}
		if (disease != null && !disease.isEmpty()) {
			sql.append("INNER JOIN Disease d ON (a.diseaseOut1 = d OR a.diseaseOut2 = d OR a.diseaseOut3 = d) ");
			sqlWhere.append("AND d.description = ? ");
			parameters.add(disease);
		}
		if (dischargeType != null && !dischargeType.isEmpty()) {
			sql.append("INNER JOIN DischargeType dt ON a.dischargeType = dt ");
			sqlWhere.append("AND dt.description = ? ");
			parameters.add(dischargeType);
		}

		sql.append(sqlWhere);
		sql.append("GROUP BY op.id ");

		Pageable pageable = PageRequest.of(startIndex / limit, limit);
		Query query = entityManager.createQuery(sql.toString());
		for (int i = 0; i < parameters.size(); i++) {
			query.setParameter(i + 1, parameters.get(i));
		}
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();

		List<OperationStat> stats = new ArrayList<>();
		for (Object[] row : results) {
			OperationStat stat = new OperationStat(
				(org.isf.operation.model.Operation) row[0],
				((Number) row[1]).intValue(),
				((Number) row[2]).intValue()
			);
			stats.add(stat);
		}

		String countSql = "SELECT COUNT(DISTINCT op.id) " + sql.substring(sql.indexOf("FROM"));
		countSql = countSql.replace("GROUP BY op.id ", "");
		Query countQuery = entityManager.createQuery(countSql);
		for (int i = 0; i < parameters.size(); i++) {
			countQuery.setParameter(i + 1, parameters.get(i));
		}
		long total = (long) countQuery.getSingleResult();

		return new PageImpl<>(stats, pageable, total);
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
	public long getOperationsStatsCount(
		int ageFrom, int ageTo, String periodFrom, String periodTo, String ward,
		String exam, String examResult, String examPeriodFrom, String examPeriodTo,
		String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo, String operationResult,
		String operationPeriodFrom, String operationPeriodTo, String disease, String dischargeType) throws OHServiceException {

		Page<OperationStat> page = getOperationsStats(0, 1, ageFrom, ageTo, periodFrom, periodTo,
			ward, exam, examResult, examPeriodFrom, examPeriodTo, vaccine, vaccinePeriodFrom, vaccinePeriodTo,
			operationResult, operationPeriodFrom, operationPeriodTo, disease, dischargeType );
		return page.getTotalElements();
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
	 * @return                      Paginated list of StatsDelivery objects
	 * @throws OHServiceException   If a database error occurs
	 */
	public Page<StatsDelivery> getDeliveriesStats(
		int startIndex, int limit, int weightFrom, int weightTo, String periodFrom, String periodTo,
		String sex, String deliveryType, String deliveryResultType, String disease, String dischargeType) throws OHServiceException {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT p, d ");
		sql.append("FROM Patient p ");
		sql.append("INNER JOIN Admission a ON a.patient = p ");
		sql.append("INNER JOIN PregnancyDelivery d ON d.admission = a ");

		StringBuilder sqlWhere = new StringBuilder();
		sqlWhere.append("WHERE (p.deleted = 'N' OR p.deleted IS NULL) ");
		List<Object> parameters = new ArrayList<>();

		if (weightFrom > 0) {
			sqlWhere.append("AND d.weight >= ? ");
			parameters.add(weightFrom);
		}
		if (weightTo > 0) {
			sqlWhere.append("AND d.weight <= ? ");
			parameters.add(weightTo);
		}
		if (periodFrom != null && !periodFrom.isEmpty()) {
			sqlWhere.append("AND d.date >= ? ");
			parameters.add(parseDate(periodFrom));
		}
		if (periodTo != null && !periodTo.isEmpty()) {
			sqlWhere.append("AND d.date <= ? ");
			parameters.add(parseDate(periodTo));
		}
		if (sex != null && !sex.isEmpty()) {
			sqlWhere.append("AND d.sex = ? ");
			parameters.add(sex);
		}
		if (deliveryType != null && !deliveryType.isEmpty()) {
			sql.append("INNER JOIN DeliveryType dt ON d.deliveryType = dt ");
			sqlWhere.append("AND dt.description = ? ");
			parameters.add(deliveryType);
		}
		if (deliveryResultType != null && !deliveryResultType.isEmpty()) {
			sql.append("INNER JOIN DeliveryResultType drt ON d.deliveryResult = drt ");
			sqlWhere.append("AND drt.description = ? ");
			parameters.add(deliveryResultType);
		}
		if (disease != null && !disease.isEmpty()) {
			sql.append("INNER JOIN Disease dis ON (a.diseaseOut1 = dis OR a.diseaseOut2 = dis OR a.diseaseOut3 = dis) ");
			sqlWhere.append("AND dis.description = ? ");
			parameters.add(disease);
		}
		if (dischargeType != null && !dischargeType.isEmpty()) {
			sql.append("INNER JOIN DischargeType dist ON a.dischargeType = dist ");
			sqlWhere.append("AND dist.description = ? ");
			parameters.add(dischargeType);
		}
		sql.append(sqlWhere);

		Pageable pageable = PageRequest.of(startIndex / limit, limit);
		Query query = entityManager.createQuery(sql.toString());
		for (int i = 0; i < parameters.size(); i++) {
			query.setParameter(i + 1, parameters.get(i));
		}
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();

		List<StatsDelivery> stats = new ArrayList<>();
		for (Object[] row : results) {
			StatsDelivery stat = new StatsDelivery(
				(org.isf.patient.model.Patient) row[0],
				(org.isf.maternity.model.PregnancyDelivery) row[1]
			);
			stats.add(stat);
		}

		String countSql = "SELECT COUNT(d) " + sql.substring(sql.indexOf("FROM"));
		Query countQuery = entityManager.createQuery(countSql);
		for (int i = 0; i < parameters.size(); i++) {
			countQuery.setParameter(i + 1, parameters.get(i));
		}
		long total = (long) countQuery.getSingleResult();

		return new PageImpl<>(stats, pageable, total);
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
	public long getDeliveriesStatsCount(
		int weightFrom, int weightTo, String periodFrom, String periodTo, String sex,
		String deliveryType, String deliveryResultType, String disease, String dischargeType) throws OHServiceException {

		Page<StatsDelivery> page = getDeliveriesStats(0, 1, weightFrom, weightTo,
			periodFrom, periodTo, sex, deliveryType, deliveryResultType, disease, dischargeType );
		return page.getTotalElements();
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
	 * @return                      Paginated list of Patient objects
	 * @throws OHServiceException   If a database error occurs
	 */
	public Page<Patient> getPregnanciesStats(
		int startIndex, int limit, int ageFrom, int ageTo, String periodFrom, String periodTo,
		String exam, String examResult, String examPeriodFrom, String examPeriodTo,
		String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo, String disease, String dischargeType,
		Boolean parameterHeight, Boolean parameterWeight, Boolean parameterArtPress, Boolean parameterCardFreq,
		Boolean parameterTemp, Boolean parameterSaturation, Boolean parameterRespRate) throws OHServiceException {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT p ");
		sql.append("FROM Patient p ");
		sql.append("INNER JOIN Pregnancy pg ON pg.patient = p ");
		sql.append("INNER JOIN PregnancyVisit pv ON pv.pregnancy = pg ");

		StringBuilder sqlWhere = new StringBuilder();
		sqlWhere.append("WHERE (p.deleted = 'N' OR p.deleted IS NULL) ");
		List<Object> parameters = new ArrayList<>();

		if (ageFrom > 0) {
			sqlWhere.append("AND p.age >= ? ");
			parameters.add(ageFrom);
		}
		if (ageTo > 0) {
			sqlWhere.append("AND p.age <= ? ");
			parameters.add(ageTo);
		}

		if (periodFrom != null && !periodFrom.isEmpty()) {
			sqlWhere.append("AND pv.visitDate >= ? ");
			parameters.add(parseDate(periodFrom));
		}
		if (periodTo != null && !periodTo.isEmpty()) {
			sqlWhere.append("AND pv.visitDate <= ? ");
			parameters.add(parseDate(periodTo));
		}

		if (exam != null && !exam.isEmpty()) {
			sql.append("INNER JOIN Laboratory l ON l.patient = p ");
			sql.append("INNER JOIN Exam e ON l.exam = e ");
			sqlWhere.append("AND e.description = ? ");
			parameters.add(exam);

			if (examResult != null && !examResult.isEmpty()) {
				sqlWhere.append("AND l.result = ? ");
				parameters.add(examResult);
			}
			if (examPeriodFrom != null && !examPeriodFrom.isEmpty()) {
				sqlWhere.append("AND l.labDate >= ? ");
				parameters.add(parseDate(examPeriodFrom));
			}
			if (examPeriodTo != null && !examPeriodTo.isEmpty()) {
				sqlWhere.append("AND l.labDate <= ? ");
				parameters.add(parseDate(examPeriodTo));
			}
		}

		if (vaccine != null && !vaccine.isEmpty()) {
			sql.append("INNER JOIN PatientVaccine pv2 ON pv2.patient = p ");
			sql.append("INNER JOIN Vaccine v ON pv2.vaccine = v ");
			sqlWhere.append("AND v.description = ? ");
			parameters.add(vaccine);

			if (vaccinePeriodFrom != null && !vaccinePeriodFrom.isEmpty()) {
				sqlWhere.append("AND pv2.date >= ? ");
				parameters.add(parseDate(vaccinePeriodFrom));
			}
			if (vaccinePeriodTo != null && !vaccinePeriodTo.isEmpty()) {
				sqlWhere.append("AND pv2.date <= ? ");
				parameters.add(parseDate(vaccinePeriodTo));
			}
		}

		if (disease != null && !disease.isEmpty()) {
			sql.append("LEFT JOIN Admission a ON a.patient = p ");
			sql.append("INNER JOIN Disease d ON (a.diseaseOut1 = d OR a.diseaseOut2 = d OR a.diseaseOut3 = d) ");
			sqlWhere.append("AND d.description = ? ");
			parameters.add(disease);
		}

		if (dischargeType != null && !dischargeType.isEmpty()) {
			if (!sql.toString().contains("LEFT JOIN Admission")) {
				sql.append("LEFT JOIN Admission a ON a.patient = p ");
			}
			sql.append("INNER JOIN DischargeType dt ON a.dischargeType = dt ");
			sqlWhere.append("AND dt.description = ? ");
			parameters.add(dischargeType);
		}

		// Paramètres patient (spécifique aux grossesses)
		if (parameterHeight || parameterWeight || parameterArtPress ||
			parameterCardFreq || parameterTemp || parameterSaturation ||
			parameterRespRate) {

			sql.append("INNER JOIN PatientExamination pe ON pe.patient = p ");
		}

		if (parameterHeight) {
			sqlWhere.append("AND pe.height > 0 ");
		}
		if (parameterWeight) {
			sqlWhere.append("AND pe.weight > 0 ");
		}
		if (parameterArtPress) {
			sqlWhere.append("AND pe.paMin > 0 ");
		}
		if (parameterCardFreq) {
			sqlWhere.append("AND pe.fc > 0 ");
		}
		if (parameterTemp) {
			sqlWhere.append("AND pe.temp > 0 ");
		}
		if (parameterSaturation) {
			sqlWhere.append("AND pe.sat > 0 ");
		}
		if (parameterRespRate) {
			sqlWhere.append("AND pe.freq > 0 ");
		}

		sql.append(sqlWhere);

		Pageable pageable = PageRequest.of(startIndex / limit, limit);
		Query query = entityManager.createQuery(sql.toString());
		for (int i = 0; i < parameters.size(); i++) {
			query.setParameter(i + 1, parameters.get(i));
		}
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		@SuppressWarnings("unchecked")
		List<Patient> patients = query.getResultList();

		String countSql = "SELECT COUNT(DISTINCT p) " + sql.substring(sql.indexOf("FROM"));
		Query countQuery = entityManager.createQuery(countSql);
		for (int i = 0; i < parameters.size(); i++) {
			countQuery.setParameter(i + 1, parameters.get(i));
		}
		long total = (long) countQuery.getSingleResult();

		return new PageImpl<>(patients, pageable, total);
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
	public long getPregnanciesStatsCount(
		int ageFrom, int ageTo, String periodFrom, String periodTo, String exam, String examResult,
		String examPeriodFrom, String examPeriodTo, String vaccine, String vaccinePeriodFrom, String vaccinePeriodTo,
		String disease, String dischargeType, Boolean parameterHeight, Boolean parameterWeight,
		Boolean parameterArtPress, Boolean parameterCardFreq, Boolean parameterTemp, Boolean parameterSaturation,
		Boolean parameterRespRate) throws OHServiceException {

		Page<Patient> page = getPregnanciesStats(0, 1, ageFrom, ageTo, periodFrom, periodTo,
			exam, examResult, examPeriodFrom, examPeriodTo, vaccine, vaccinePeriodFrom, vaccinePeriodTo,
			disease, dischargeType, parameterHeight, parameterWeight, parameterArtPress, parameterCardFreq,
			parameterTemp, parameterSaturation, parameterRespRate);
		return page.getTotalElements();
	}

	/**
	 * Converts a string to LocalDateTime
	 */
	private LocalDateTime parseDate(String dateStr) {
		if (dateStr == null || dateStr.isEmpty()) {
			return null;
		}
		try {
			return LocalDateTime.parse(dateStr + "T00:00:00");
		} catch (Exception e) {
			return null;
		}
	}
}