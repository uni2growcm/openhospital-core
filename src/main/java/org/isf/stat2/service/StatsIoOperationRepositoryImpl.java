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
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.isf.patient.model.Patient;
import org.isf.stat2.model.PregnancyStatsRange;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class StatsIoOperationRepositoryImpl implements StatsIoOperationRepositoryCustom {

	private static final String YYYY_MM_DD = "yyyy-MM-dd";

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Page<Patient> findPregnanciesStatsByFilters(
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
		Pageable pageable
	) throws OHServiceException {

		StringBuilder fromBuilder = new StringBuilder();
		StringBuilder conditions = new StringBuilder();
		List<Object> parameters = new ArrayList<>();

		fromBuilder.append(" FROM oh_patient _patient")
			.append(" INNER JOIN oh_pregnancy _pregnancy ON _patient.PAT_ID = _pregnancy.PRG_PAT_ID")
			.append(" INNER JOIN oh_pregnancyvisit _pregvisit ON _pregvisit.PRGV_PRG_ID = _pregnancy.PRG_ID");

		conditions.append("(_patient.PAT_DELETED = 'N' OR _patient.PAT_DELETED IS NULL)");

		if (ageFrom != null && ageFrom > 0) {
			conditions.append(" AND _patient.PAT_AGE >= ?");
			parameters.add(ageFrom);
		}

		if (ageTo != null && ageTo > 0) {
			conditions.append(" AND _patient.PAT_AGE <= ?");
			parameters.add(ageTo);
		}

		if (periodFrom != null) {
			conditions.append(" AND DATE(_pregvisit.PRGV_DATE) >= ?");
			parameters.add(TimeTools.formatDateTime(periodFrom, YYYY_MM_DD));
		}

		if (periodTo != null) {
			conditions.append(" AND DATE(_pregvisit.PRGV_DATE) <= ?");
			parameters.add(TimeTools.formatDateTime(periodTo, YYYY_MM_DD));
		}

		if (riskLevel != null && !riskLevel.isEmpty()) {
			conditions.append(" AND _pregnancy.PRG_RISK_LEVEL = ?");
			parameters.add(riskLevel.toUpperCase());
		}

		if (status != null && !status.isEmpty()) {
			conditions.append(" AND _pregnancy.PRG_STATUS = ?");
			parameters.add(status.toUpperCase());
		}

		if (gravidityMin != null) {
			conditions.append(" AND _pregnancy.PRG_GRAVIDITY >= ?");
			parameters.add(gravidityMin);
		}
		if (gravidityMax != null) {
			conditions.append(" AND _pregnancy.PRG_GRAVIDITY <= ?");
			parameters.add(gravidityMax);
		}

		if (parityMin != null) {
			conditions.append(" AND _pregnancy.PRG_PARITY >= ?");
			parameters.add(parityMin);
		}
		if (parityMax != null) {
			conditions.append(" AND _pregnancy.PRG_PARITY <= ?");
			parameters.add(parityMax);
		}

		if (miscarriageMin != null) {
			conditions.append(" AND _pregnancy.PRG_MISSCARRIAGES >= ?");
			parameters.add(miscarriageMin);
		}
		if (miscarriageMax != null) {
			conditions.append(" AND _pregnancy.PRG_MISSCARRIAGES <= ?");
			parameters.add(miscarriageMax);
		}

		if (gestationalAgeMin != null) {
			conditions.append(" AND DATEDIFF(_pregvisit.PRGV_DATE, _pregnancy.PRG_LMP) >= ?");
			parameters.add(gestationalAgeMin * 7);
		}

		if (gestationalAgeMax != null) {
			conditions.append(" AND DATEDIFF(_pregvisit.PRGV_DATE, _pregnancy.PRG_LMP) < ?");
			parameters.add(gestationalAgeMax * 7);
		}

		List<Object> fromParameters = new ArrayList<>();
		if (visitCountMin != null || visitCountMax != null) {
			StringBuilder visitCountSql = new StringBuilder(
				" INNER JOIN (SELECT PRGV_PRG_ID, COUNT(*) AS visit_count FROM oh_pregnancyvisit WHERE 1 = 1");
			if (periodFrom != null) {
				visitCountSql.append(" AND DATE(PRGV_DATE) >= ?");
				fromParameters.add(TimeTools.formatDateTime(periodFrom, YYYY_MM_DD));
			}
			if (periodTo != null) {
				visitCountSql.append(" AND DATE(PRGV_DATE) <= ?");
				fromParameters.add(TimeTools.formatDateTime(periodTo, YYYY_MM_DD));
			}
			visitCountSql.append(" GROUP BY PRGV_PRG_ID) _visitcount ON _visitcount.PRGV_PRG_ID = _pregnancy.PRG_ID");
			fromBuilder.append(visitCountSql);
			if (visitCountMin != null) {
				conditions.append(" AND _visitcount.visit_count >= ?");
				parameters.add(visitCountMin);
			}
			if (visitCountMax != null) {
				conditions.append(" AND _visitcount.visit_count <= ?");
				parameters.add(visitCountMax);
			}
		}

		if (visitType != null && !visitType.isEmpty()) {
			conditions.append(" AND _pregvisit.PRGV_TYPE_ID = ?");
			parameters.add(visitType);
		}

		if (maternalWeightRange != null && !maternalWeightRange.isEmpty()) {
			switch (maternalWeightRange) {
				case PregnancyStatsRange.MATERNAL_WEIGHT_LT50:
					conditions.append(" AND _pregvisit.PRGV_MATERNAL_WEIGHT < 50");
					break;
				case PregnancyStatsRange.MATERNAL_WEIGHT_50_70:
					conditions.append(" AND _pregvisit.PRGV_MATERNAL_WEIGHT >= 50 AND _pregvisit.PRGV_MATERNAL_WEIGHT < 70");
					break;
				case PregnancyStatsRange.MATERNAL_WEIGHT_70_90:
					conditions.append(" AND _pregvisit.PRGV_MATERNAL_WEIGHT >= 70 AND _pregvisit.PRGV_MATERNAL_WEIGHT < 90");
					break;
				case PregnancyStatsRange.MATERNAL_WEIGHT_GE90:
					conditions.append(" AND _pregvisit.PRGV_MATERNAL_WEIGHT >= 90");
					break;
				default:
					break;
			}
		}

		if (urineProtein != null && !urineProtein.isEmpty()) {
			conditions.append(" AND _pregvisit.PRGV_URINE_PROTEIN = ?");
			parameters.add(urineProtein);
		}

		if (edema != null && !edema.isEmpty()) {
			conditions.append(" AND _pregvisit.PRGV_EDEMA_PRESENCE = ?");
			parameters.add(edema);
		}

		if (fetalPresentation != null && !fetalPresentation.isEmpty()) {
			conditions.append(" AND _pregvisit.PRGV_FETAL_PRESENTATION = ?");
			parameters.add(fetalPresentation);
		}

		if (systolicBpRange != null && !systolicBpRange.isEmpty()) {
			switch (systolicBpRange) {
				case PregnancyStatsRange.SYSTOLIC_LT120:
					conditions.append(" AND _pregvisit.PRGV_SYSTOLIC_BP < 120");
					break;
				case PregnancyStatsRange.SYSTOLIC_120_139:
					conditions.append(" AND _pregvisit.PRGV_SYSTOLIC_BP >= 120 AND _pregvisit.PRGV_SYSTOLIC_BP < 140");
					break;
				case PregnancyStatsRange.SYSTOLIC_GE140:
					conditions.append(" AND _pregvisit.PRGV_SYSTOLIC_BP >= 140");
					break;
				default:
					break;
			}
		}

		if (diastolicBpRange != null && !diastolicBpRange.isEmpty()) {
			switch (diastolicBpRange) {
				case PregnancyStatsRange.DIASTOLIC_LT80:
					conditions.append(" AND _pregvisit.PRGV_DIASTOLIC_BP < 80");
					break;
				case PregnancyStatsRange.DIASTOLIC_80_89:
					conditions.append(" AND _pregvisit.PRGV_DIASTOLIC_BP >= 80 AND _pregvisit.PRGV_DIASTOLIC_BP < 90");
					break;
				case PregnancyStatsRange.DIASTOLIC_GE90:
					conditions.append(" AND _pregvisit.PRGV_DIASTOLIC_BP >= 90");
					break;
				default:
					break;
			}
		}

		boolean examJoined = false;
		if (exam != null && !exam.isEmpty()) {
			fromBuilder.append(" INNER JOIN oh_laboratory _labo ON _labo.LAB_PAT_ID = _patient.PAT_ID")
				.append(" INNER JOIN oh_exam _labexam ON _labo.LAB_EXA_ID_A = _labexam.EXA_ID_A");
			conditions.append(" AND _labexam.EXA_DESC = ?");
			parameters.add(exam);
			examJoined = true;

			if (examResult != null && !examResult.isEmpty()) {
				conditions.append(" AND _labo.LAB_RES = ?");
				parameters.add(examResult);
			}
			if (examPeriodFrom != null) {
				conditions.append(" AND DATE(_labo.LAB_DATE) >= ?");
				parameters.add(TimeTools.formatDateTime(examPeriodFrom, YYYY_MM_DD));
			}
			if (examPeriodTo != null) {
				conditions.append(" AND DATE(_labo.LAB_DATE) <= ?");
				parameters.add(TimeTools.formatDateTime(examPeriodTo, YYYY_MM_DD));
			}
		}

		if (vaccine != null && !vaccine.isEmpty()) {
			fromBuilder.append(" INNER JOIN oh_patientvaccine _patvac ON _patvac.PAV_PAT_ID = _patient.PAT_ID")
				.append(" INNER JOIN oh_vaccine _vaccine ON _vaccine.VAC_ID_A = _patvac.PAV_VAC_ID_A");
			conditions.append(" AND _vaccine.VAC_DESC = ?");
			parameters.add(vaccine);

			if (vaccinePeriodFrom != null) {
				conditions.append(" AND DATE(_patvac.PAV_DATE) >= ?");
				parameters.add(TimeTools.formatDateTime(vaccinePeriodFrom, YYYY_MM_DD));
			}
			if (vaccinePeriodTo != null) {
				conditions.append(" AND DATE(_patvac.PAV_DATE) <= ?");
				parameters.add(TimeTools.formatDateTime(vaccinePeriodTo, YYYY_MM_DD));
			}
		}

		boolean admissionJoined = false;
		if (disease != null && !disease.isEmpty()) {
			fromBuilder.append(" INNER JOIN oh_admission _patadm ON _patadm.ADM_PAT_ID = _patient.PAT_ID")
				.append(" INNER JOIN oh_disease _disease ON _patadm.ADM_OUT_DIS_ID_A = _disease.DIS_ID_A")
				.append(" OR _patadm.ADM_OUT_DIS_ID_A_2 = _disease.DIS_ID_A")
				.append(" OR _patadm.ADM_OUT_DIS_ID_A_3 = _disease.DIS_ID_A");
			conditions.append(" AND _disease.DIS_DESC = ?");
			parameters.add(disease);
			admissionJoined = true;
		}

		if (dischargeType != null && !dischargeType.isEmpty()) {
			if (!admissionJoined) {
				fromBuilder.append(" INNER JOIN oh_admission _patadm ON _patadm.ADM_PAT_ID = _patient.PAT_ID");
				admissionJoined = true;
			}
			fromBuilder.append(" INNER JOIN oh_dischargetype _disctype ON _disctype.DIST_ID_A = _patadm.ADM_DIST_ID_A");
			conditions.append(" AND _disctype.DIST_DESC = ?");
			parameters.add(dischargeType);
		}

		boolean anyParameter = parameterHeight || parameterWeight || parameterArtPress || parameterCardFreq
			|| parameterTemp || parameterSaturation || parameterRespRate;
		if (anyParameter) {
			fromBuilder.append(" INNER JOIN oh_patientexamination _patexam ON _patexam.PEX_PAT_ID = _patient.PAT_ID");
		}

		if (parameterHeight) {
			addRangeCondition(conditions, parameters, "_patexam.PEX_HEIGHT", heightMin, heightMax);
		}

		if (parameterWeight) {
			addRangeCondition(conditions, parameters, "_patexam.PEX_WEIGHT", weightMin, weightMax);
		}

		if (parameterCardFreq) {
			addRangeCondition(conditions, parameters, "_patexam.PEX_HR", cardiacMin, cardiacMax);
		}

		if (parameterTemp) {
			addRangeCondition(conditions, parameters, "_patexam.PEX_TEMP", tempMin, tempMax);
		}

		if (parameterSaturation) {
			addRangeCondition(conditions, parameters, "_patexam.PEX_SAT", satMin, satMax);
		}

		if (parameterRespRate) {
			addRangeCondition(conditions, parameters, "_patexam.PEX_RR", respMin, respMax);
		}

		if (parameterArtPress) {
			addRangeCondition(conditions, parameters, "_patexam.PEX_AP_MIN", systolicMin, systolicMax);
			addRangeCondition(conditions, parameters, "_patexam.PEX_AP_MAX", diastolicMin, diastolicMax);
		}

		String from = fromBuilder.toString();
		String where = " WHERE " + conditions;

		// parameters of the FROM clause (sub-queries) come before those of the WHERE clause
		List<Object> allParameters = new ArrayList<>(fromParameters);
		allParameters.addAll(parameters);

		// ORDER BY keeps pagination stable
		String dataSql = "SELECT DISTINCT _patient.PAT_ID, _patient.PAT_FNAME, _patient.PAT_SNAME, _patient.PAT_AGE"
			+ from + where + " ORDER BY _patient.PAT_SNAME, _patient.PAT_FNAME, _patient.PAT_ID";

		Query dataQuery = entityManager.createNativeQuery(dataSql);
		bindParameters(dataQuery, allParameters);
		dataQuery.setFirstResult((int) pageable.getOffset());
		dataQuery.setMaxResults(pageable.getPageSize());

		String countSql = "SELECT COUNT(DISTINCT _patient.PAT_ID)" + from + where;
		Query countQuery = entityManager.createNativeQuery(countSql);
		bindParameters(countQuery, allParameters);

		long total = ((Number) countQuery.getSingleResult()).longValue();

		List<Patient> patients = new ArrayList<>();
		for (Object record : dataQuery.getResultList()) {
			Object[] row = (Object[]) record;
			Patient patient = new Patient();
			patient.setCode(((Number) row[0]).intValue());
			patient.setFirstName((String) row[1]);
			patient.setSecondName((String) row[2]);
			patient.setAge(row[3] != null ? ((Number) row[3]).intValue() : 0);
			patients.add(patient);
		}

		return new PageImpl<>(patients, pageable, total);
	}

	private void bindParameters(Query query, List<Object> parameters) {
		for (int i = 0; i < parameters.size(); i++) {
			query.setParameter(i + 1, parameters.get(i));
		}
	}

	private void addRangeCondition(StringBuilder conditions, List<Object> parameters,
	                               String columnName, Double min, Double max) {
		if (min != null) {
			conditions.append(" AND ").append(columnName).append(" >= ?");
			parameters.add(min);
		}
		if (max != null) {
			conditions.append(" AND ").append(columnName).append(" <= ?");
			parameters.add(max);
		}
	}
}