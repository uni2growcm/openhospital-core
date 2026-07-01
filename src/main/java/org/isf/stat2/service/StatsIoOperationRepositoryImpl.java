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

		if (riskLevel != null && !riskLevel.isEmpty() && !"Tous".equals(riskLevel)) {
			conditions.append(" AND _pregnancy.PRG_RISK_LEVEL = ?");
			parameters.add(riskLevel);
		}

		if (status != null && !status.isEmpty() && !"Tous".equals(status)) {
			conditions.append(" AND _pregnancy.PRG_STATUS = ?");
			parameters.add(status);
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

		if (gestationalAgeMin != null || gestationalAgeMax != null) {
			if (gestationalAgeMin != null) {
				conditions.append(" AND DATEDIFF(NOW(), _pregnancy.PRG_LMP) >= ?");
				parameters.add(gestationalAgeMin * 7);
			}
			if (gestationalAgeMax != null) {
				conditions.append(" AND DATEDIFF(NOW(), _pregnancy.PRG_LMP) <= ?");
				parameters.add(gestationalAgeMax * 7);
			}
		}

		if (visitCountMin != null || visitCountMax != null) {
			fromBuilder.append(" INNER JOIN (SELECT PRGV_PRG_ID, COUNT(*) AS visit_count"
				+ " FROM oh_pregnancyvisit GROUP BY PRGV_PRG_ID) _visitcount"
				+ " ON _visitcount.PRGV_PRG_ID = _pregnancy.PRG_ID");
			if (visitCountMin != null) {
				conditions.append(" AND _visitcount.visit_count >= ?");
				parameters.add(visitCountMin);
			}
			if (visitCountMax != null) {
				conditions.append(" AND _visitcount.visit_count <= ?");
				parameters.add(visitCountMax);
			}
		}

		if (visitType != null && !visitType.isEmpty() && !"Tous".equals(visitType)) {
			conditions.append(" AND _pregvisit.PRGV_TYPE_ID = ?");
			parameters.add(visitType);
		}

		if (maternalWeightRange != null && !maternalWeightRange.isEmpty() && !"Tous".equals(maternalWeightRange)) {
			switch (maternalWeightRange) {
				case "< 50 kg":
					conditions.append(" AND _pregvisit.PRGV_MATERNAL_WEIGHT < 50");
					break;
				case "50-70 kg":
					conditions.append(" AND _pregvisit.PRGV_MATERNAL_WEIGHT BETWEEN 50 AND 70");
					break;
				case "70-90 kg":
					conditions.append(" AND _pregvisit.PRGV_MATERNAL_WEIGHT BETWEEN 70 AND 90");
					break;
				case "90+ kg":
					conditions.append(" AND _pregvisit.PRGV_MATERNAL_WEIGHT > 90");
					break;
			}
		}

		if (urineProtein != null && !urineProtein.isEmpty() && !"Tous".equals(urineProtein)) {
			conditions.append(" AND _pregvisit.PRGV_URINE_PROTEIN = ?");
			parameters.add(urineProtein);
		}

		if (edema != null && !edema.isEmpty() && !"Tous".equals(edema)) {
			if ("Pas d'œdème".equals(edema)) {
				conditions.append(" AND _pregvisit.PRGV_EDEMA_PRESENCE = ?");
				parameters.add("Pas d'œdème");
			} else {
				conditions.append(" AND _pregvisit.PRGV_EDEMA_PRESENCE = ?");
				parameters.add(edema);
			}
		}

		if (fetalPresentation != null && !fetalPresentation.isEmpty() && !"Tous".equals(fetalPresentation)) {
			conditions.append(" AND _pregvisit.PRGV_FETAL_PRESENTATION = ?");
			parameters.add(fetalPresentation);
		}

		if (systolicBpRange != null && !systolicBpRange.isEmpty() && !"Tous".equals(systolicBpRange)) {
			switch (systolicBpRange) {
				case "< 120":
					conditions.append(" AND _pregvisit.PRGV_SYSTOLIC_BP < 120");
					break;
				case "120-139":
					conditions.append(" AND _pregvisit.PRGV_SYSTOLIC_BP BETWEEN 120 AND 139");
					break;
				case "140+":
					conditions.append(" AND _pregvisit.PRGV_SYSTOLIC_BP >= 140");
					break;
			}
		}

		if (diastolicBpRange != null && !diastolicBpRange.isEmpty() && !"Tous".equals(diastolicBpRange)) {
			switch (diastolicBpRange) {
				case "< 80":
					conditions.append(" AND _pregvisit.PRGV_DIASTOLIC_BP < 80");
					break;
				case "80-89":
					conditions.append(" AND _pregvisit.PRGV_DIASTOLIC_BP BETWEEN 80 AND 89");
					break;
				case "90+":
					conditions.append(" AND _pregvisit.PRGV_DIASTOLIC_BP >= 90");
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
			conditions.append(" AND _patexam.PEX_HEIGHT > 0");
		}
		if (parameterWeight) {
			conditions.append(" AND _patexam.PEX_WEIGHT > 0");
		}
		if (parameterArtPress) {
			conditions.append(" AND _patexam.PEX_AP_MIN > 0");
		}
		if (parameterCardFreq) {
			conditions.append(" AND _patexam.PEX_HR > 0");
		}
		if (parameterTemp) {
			conditions.append(" AND _patexam.PEX_TEMP > 0");
		}
		if (parameterSaturation) {
			conditions.append(" AND _patexam.PEX_SAT > 0");
		}
		if (parameterRespRate) {
			conditions.append(" AND _patexam.PEX_RR > 0");
		}

		String from = fromBuilder.toString();
		String where = " WHERE " + conditions;

		String dataSql = "SELECT DISTINCT _patient.PAT_ID, _patient.PAT_FNAME, _patient.PAT_SNAME, _patient.PAT_AGE"
			+ from + where;

		Query dataQuery = entityManager.createNativeQuery(dataSql);
		bindParameters(dataQuery, parameters);
		dataQuery.setFirstResult((int) pageable.getOffset());
		dataQuery.setMaxResults(pageable.getPageSize());

		String countSql = "SELECT COUNT(DISTINCT _patient.PAT_ID)" + from + where;
		Query countQuery = entityManager.createNativeQuery(countSql);
		bindParameters(countQuery, parameters);

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
}