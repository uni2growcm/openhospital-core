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

import org.isf.maternity.model.DeliveryMode;
import org.isf.maternity.model.PerinealIntegrity;
import org.isf.maternity.model.Newborn;
import org.isf.maternity.model.CryTime;
import org.isf.maternity.model.NeonatalStatus;
import org.isf.maternity.model.HivStatus;
import org.isf.maternity.model.PregnancyDelivery;
import org.isf.patient.model.Patient;
import org.isf.stat2.model.StatsDelivery;
import org.isf.typology.model.Typology;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class StatsDeliveryIoOperationRepositoryImpl implements StatsDeliveryIoOperationRepositoryCustom {

	private static final String YYYY_MM_DD = "yyyy-MM-dd";

	@PersistenceContext
	private EntityManager entityManager;

	private DeliveryMode parseDeliveryMode(Object value) {
		if (value == null) {
			return null;
		}
		String str = value.toString().trim();
		switch (str) {
			case "1":
				return DeliveryMode.SVD;
			case "2":
				return DeliveryMode.VACUUM;
			case "3":
				return DeliveryMode.FORCEPS;
			case "4":
				return DeliveryMode.C_SECTION_ELECTIVE;
			case "5":
				return DeliveryMode.C_SECTION_EMERGENCY;
			default:
				try {
					return DeliveryMode.valueOf(str);
				} catch (IllegalArgumentException e) {
					return null;
				}
		}
	}

	private boolean toBoolean(Object value) {
		if (value == null) {
			return false;
		}
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		if (value instanceof Number) {
			return ((Number) value).intValue() == 1;
		}
		if (value instanceof String) {
			String str = (String) value;
			return "1".equals(str) || "true".equalsIgnoreCase(str) || "Y".equalsIgnoreCase(str);
		}
		return false;
	}

	private PerinealIntegrity parsePerinealIntegrity(Object value) {
		if (value == null) {
			return null;
		}
		String str = value.toString().trim();
		switch (str) {
			case "0":
				return PerinealIntegrity.INTACT;
			case "1":
				return PerinealIntegrity.FIRST_DEGREE;
			case "2":
				return PerinealIntegrity.SECOND_DEGREE;
			case "3":
				return PerinealIntegrity.THIRD_DEGREE;
			case "4":
				return PerinealIntegrity.FOURTH_DEGREE;
			default:
				try {
					return PerinealIntegrity.valueOf(str);
				} catch (IllegalArgumentException e) {
					return null;
				}
		}
	}

	@Override
	public Page<StatsDelivery> findDeliveriesStatsByFilters(
		LocalDateTime periodFrom,
		LocalDateTime periodTo,
		Integer motherAgeMin,
		Integer motherAgeMax,
		String sex,
		Double weightMin,
		Double weightMax,
		String deliveryType,
		String deliveryResultType,
		String deliveryMode,
		String laborDuration,
		String romRange,
		String perinealIntegrity,
		Boolean placentaComplete,
		String bloodLossRange,
		String newbornSex,
		String birthWeightRange,
		String neonatalStatus,
		String apgar1Range,
		String apgar5Range,
		Boolean resuscitationRequired,
		String cryTime,
		String hivStatus,
		Boolean congenitalAnomalies,
		String disease,
		String dischargeType,
		Pageable pageable
	) throws OHServiceException {

		StringBuilder fromBuilder = new StringBuilder();
		StringBuilder conditions = new StringBuilder();
		List<Object> parameters = new ArrayList<>();

		fromBuilder.append(" FROM oh_patient _mother")
			.append(" INNER JOIN oh_pregnancy _pregnancy ON _mother.PAT_ID = _pregnancy.PRG_PAT_ID")
			.append(" INNER JOIN oh_pregnancydelivery _delivery ON _delivery.PRGDLV_PRG_ID = _pregnancy.PRG_ID")
			.append(" INNER JOIN oh_newborn _newborn ON _newborn.NBN_DLV_ID = _delivery.PRGDLV_ID");

		conditions.append("(_mother.PAT_DELETED = 'N' OR _mother.PAT_DELETED IS NULL)");

		if (periodFrom != null) {
			conditions.append(" AND DATE(_delivery.PRGDLV_DATE) >= ?");
			parameters.add(TimeTools.formatDateTime(periodFrom, YYYY_MM_DD));
		}
		if (periodTo != null) {
			conditions.append(" AND DATE(_delivery.PRGDLV_DATE) <= ?");
			parameters.add(TimeTools.formatDateTime(periodTo, YYYY_MM_DD));
		}

		if (motherAgeMin != null) {
			conditions.append(" AND _mother.PAT_AGE >= ?");
			parameters.add(motherAgeMin);
		}
		if (motherAgeMax != null) {
			conditions.append(" AND _mother.PAT_AGE <= ?");
			parameters.add(motherAgeMax);
		}

		if (sex != null && !sex.isEmpty() && !"Tous".equals(sex) && !"All".equals(sex)) {
			conditions.append(" AND _newborn.NBN_PAT_ID IN (SELECT PAT_ID FROM oh_patient WHERE PAT_SEX = ?)");
			parameters.add(sex);
		}

		if (weightMin != null) {
			conditions.append(" AND _newborn.NBN_BIRTH_WEIGHT >= ?");
			parameters.add(weightMin);
		}
		if (weightMax != null) {
			conditions.append(" AND _newborn.NBN_BIRTH_WEIGHT <= ?");
			parameters.add(weightMax);
		}

		if (deliveryType != null && !deliveryType.isEmpty() && !"Tous".equals(deliveryType) && !"All".equals(deliveryType)) {
			fromBuilder.append(" INNER JOIN oh_typologies _deliverytype ON _deliverytype.TYPO_CODE = _delivery.PRGDLV_TYPE_ID");
			conditions.append(" AND _deliverytype.TYPO_DESCRIPTION = ?");
			parameters.add(deliveryType);
		}

		if (deliveryResultType != null && !deliveryResultType.isEmpty() && !"Tous".equals(deliveryResultType) && !"All".equals(deliveryResultType)) {
			conditions.append(" AND _newborn.NBN_NEONATAL_STATUS = ?");
			parameters.add(deliveryResultType);
		}

		if (deliveryMode != null && !deliveryMode.isEmpty() && !"Tous".equals(deliveryMode) && !"All".equals(deliveryMode)) {
			conditions.append(" AND _delivery.PRGDLV_DELIVERYMODE = ?");
			parameters.add(deliveryMode);
		}

		if (laborDuration != null && !laborDuration.isEmpty() && !"Tous".equals(laborDuration) && !"All".equals(laborDuration)) {
			switch (laborDuration) {
				case "< 6h":
					conditions.append(" AND TIMESTAMPDIFF(HOUR, _delivery.PRGDLV_LABOR_ONSET_DATETIME, _delivery.PRGDLV_DATE) < 6");
					break;
				case "6-12h":
					conditions.append(" AND TIMESTAMPDIFF(HOUR, _delivery.PRGDLV_LABOR_ONSET_DATETIME, _delivery.PRGDLV_DATE) BETWEEN 6 AND 12");
					break;
				case "12-24h":
					conditions.append(" AND TIMESTAMPDIFF(HOUR, _delivery.PRGDLV_LABOR_ONSET_DATETIME, _delivery.PRGDLV_DATE) BETWEEN 12 AND 24");
					break;
				case "> 24h":
					conditions.append(" AND TIMESTAMPDIFF(HOUR, _delivery.PRGDLV_LABOR_ONSET_DATETIME, _delivery.PRGDLV_DATE) > 24");
					break;
			}
		}

		if (romRange != null && !romRange.isEmpty() && !"Tous".equals(romRange) && !"All".equals(romRange)) {
			if ("< 18h".equals(romRange)) {
				conditions.append(" AND TIMESTAMPDIFF(HOUR, _delivery.PRGDLV_ROM_DATETIME, _delivery.PRGDLV_DATE) < 18");
			} else if (">= 18h".equals(romRange)) {
				conditions.append(" AND TIMESTAMPDIFF(HOUR, _delivery.PRGDLV_ROM_DATETIME, _delivery.PRGDLV_DATE) >= 18");
			}
		}

		if (perinealIntegrity != null && !perinealIntegrity.isEmpty() && !"Tous".equals(perinealIntegrity) && !"All".equals(perinealIntegrity)) {
			conditions.append(" AND _delivery.PRGDLV_PERINEAL_INTEGRITY = ?");
			parameters.add(perinealIntegrity);
		}

		if (placentaComplete != null) {
			conditions.append(" AND _delivery.PRGDLV_PLACENTA_COMPLETE = ?");
			parameters.add(placentaComplete ? 1 : 0);
		}

		if (bloodLossRange != null && !bloodLossRange.isEmpty() && !"Tous".equals(bloodLossRange) && !"All".equals(bloodLossRange)) {
			switch (bloodLossRange) {
				case "< 500 ml":
					conditions.append(" AND _delivery.PRGDLV_ESTIMATED_BLD_LOSS < 500");
					break;
				case "500-1000 ml":
					conditions.append(" AND _delivery.PRGDLV_ESTIMATED_BLD_LOSS BETWEEN 500 AND 1000");
					break;
				case "> 1000 ml":
					conditions.append(" AND _delivery.PRGDLV_ESTIMATED_BLD_LOSS > 1000");
					break;
			}
		}

		if (birthWeightRange != null && !birthWeightRange.isEmpty() && !"Tous".equals(birthWeightRange) && !"All".equals(birthWeightRange)) {
			switch (birthWeightRange) {
				case "< 1.5 kg":
					conditions.append(" AND _newborn.NBN_BIRTH_WEIGHT < 1.5");
					break;
				case "1.5-2.49 kg":
					conditions.append(" AND _newborn.NBN_BIRTH_WEIGHT BETWEEN 1.5 AND 2.49");
					break;
				case "2.5-4 kg":
					conditions.append(" AND _newborn.NBN_BIRTH_WEIGHT BETWEEN 2.5 AND 4");
					break;
				case "> 4 kg":
					conditions.append(" AND _newborn.NBN_BIRTH_WEIGHT > 4");
					break;
			}
		}

		if (neonatalStatus != null && !neonatalStatus.isEmpty() && !"Tous".equals(neonatalStatus) && !"All".equals(neonatalStatus)) {
			conditions.append(" AND _newborn.NBN_NEONATAL_STATUS = ?");
			parameters.add(neonatalStatus);
		}

		if (apgar1Range != null && !apgar1Range.isEmpty() && !"Tous".equals(apgar1Range) && !"All".equals(apgar1Range)) {
			switch (apgar1Range) {
				case "0-3":
					conditions.append(" AND _newborn.NBN_APGAR_SCORE_1MIN BETWEEN 0 AND 3");
					break;
				case "4-6":
					conditions.append(" AND _newborn.NBN_APGAR_SCORE_1MIN BETWEEN 4 AND 6");
					break;
				case "7-10":
					conditions.append(" AND _newborn.NBN_APGAR_SCORE_1MIN BETWEEN 7 AND 10");
					break;
			}
		}

		if (apgar5Range != null && !apgar5Range.isEmpty() && !"Tous".equals(apgar5Range) && !"All".equals(apgar5Range)) {
			switch (apgar5Range) {
				case "0-3":
					conditions.append(" AND _newborn.NBN_APGAR_SCORE_5MIN BETWEEN 0 AND 3");
					break;
				case "4-6":
					conditions.append(" AND _newborn.NBN_APGAR_SCORE_5MIN BETWEEN 4 AND 6");
					break;
				case "7-10":
					conditions.append(" AND _newborn.NBN_APGAR_SCORE_5MIN BETWEEN 7 AND 10");
					break;
			}
		}

		if (resuscitationRequired != null) {
			conditions.append(" AND _newborn.NBN_RESUSCITATION_REQUIRED = ?");
			parameters.add(resuscitationRequired ? 1 : 0);
		}

		if (cryTime != null && !cryTime.isEmpty() && !"Tous".equals(cryTime) && !"All".equals(cryTime)) {
			conditions.append(" AND _newborn.NBN_CRY_TIME = ?");
			parameters.add(cryTime);
		}

		if (hivStatus != null && !hivStatus.isEmpty() && !"Tous".equals(hivStatus) && !"All".equals(hivStatus)) {
			conditions.append(" AND _newborn.NBN_HIV_STATUS = ?");
			parameters.add(hivStatus);
		}

		if (congenitalAnomalies != null) {
			if (congenitalAnomalies) {
				conditions.append(" AND (_newborn.NBN_CONGENITAL_ANOMALIES IS NOT NULL AND _newborn.NBN_CONGENITAL_ANOMALIES != '')");
			} else {
				conditions.append(" AND (_newborn.NBN_CONGENITAL_ANOMALIES IS NULL OR _newborn.NBN_CONGENITAL_ANOMALIES = '')");
			}
		}

		if (disease != null && !disease.isEmpty() && !"Tous".equals(disease) && !"All".equals(disease)) {
			fromBuilder.append(" INNER JOIN oh_admission _patadm ON _mother.PAT_ID = _patadm.ADM_PAT_ID")
				.append(" INNER JOIN oh_disease _disease ON _patadm.ADM_OUT_DIS_ID_A = _disease.DIS_ID_A")
				.append(" OR _patadm.ADM_OUT_DIS_ID_A_2 = _disease.DIS_ID_A")
				.append(" OR _patadm.ADM_OUT_DIS_ID_A_3 = _disease.DIS_ID_A");
			conditions.append(" AND _disease.DIS_DESC = ?");
			parameters.add(disease);
		}

		if (dischargeType != null && !dischargeType.isEmpty() && !"Tous".equals(dischargeType) && !"All".equals(dischargeType)) {
			if (disease != null && !disease.isEmpty()) {
				fromBuilder.append(" INNER JOIN oh_dischargetype _disctype ON _disctype.DIST_ID_A = _patadm.ADM_DIST_ID_A");
			} else {
				fromBuilder.append(" INNER JOIN oh_admission _patadm ON _mother.PAT_ID = _patadm.ADM_PAT_ID")
					.append(" INNER JOIN oh_dischargetype _disctype ON _disctype.DIST_ID_A = _patadm.ADM_DIST_ID_A");
			}
			conditions.append(" AND _disctype.DIST_DESC = ?");
			parameters.add(dischargeType);
		}

		String from = fromBuilder.toString();
		String where = " WHERE " + conditions;

		String dataSql = "SELECT DISTINCT _mother.PAT_ID, _mother.PAT_FNAME, _mother.PAT_SNAME, _mother.PAT_AGE, "
			+ "_newborn.NBN_PAT_ID, _newborn.NBN_BIRTH_WEIGHT, _newborn.NBN_NEONATAL_STATUS, "
			+ "_newborn.NBN_APGAR_SCORE_1MIN, _newborn.NBN_APGAR_SCORE_5MIN, _delivery.PRGDLV_DELIVERYMODE, "
			+ "_delivery.PRGDLV_DATE, _delivery.PRGDLV_ROM_DATETIME, _delivery.PRGDLV_PERINEAL_INTEGRITY, "
			+ "_delivery.PRGDLV_PLACENTA_COMPLETE, _delivery.PRGDLV_ESTIMATED_BLD_LOSS, _newborn.NBN_CRY_TIME, "
			+ "_newborn.NBN_HIV_STATUS, _newborn.NBN_CONGENITAL_ANOMALIES, _newborn.NBN_RESUSCITATION_REQUIRED, "
			+ "_delivery.PRGDLV_TYPE_ID, "
			+ "(SELECT PAT_SEX FROM oh_patient WHERE PAT_ID = _newborn.NBN_PAT_ID) AS NBN_SEX"
			+ from + where;

		System.out.println("=== DELIVERY STATS QUERY ===");
		System.out.println("SQL: " + dataSql);
		System.out.println("Parameters: " + parameters);

		Query dataQuery = entityManager.createNativeQuery(dataSql);
		bindParameters(dataQuery, parameters);
		dataQuery.setFirstResult((int) pageable.getOffset());
		dataQuery.setMaxResults(pageable.getPageSize());

		String countSql = "SELECT COUNT(DISTINCT _mother.PAT_ID)" + from + where;
		Query countQuery = entityManager.createNativeQuery(countSql);
		bindParameters(countQuery, parameters);

		long total = ((Number) countQuery.getSingleResult()).longValue();

		List<StatsDelivery> deliveries = new ArrayList<>();
		for (Object record : dataQuery.getResultList()) {
			Object[] row = (Object[]) record;

			Patient mother = new Patient();
			mother.setCode(((Number) row[0]).intValue());
			mother.setFirstName((String) row[1]);
			mother.setSecondName((String) row[2]);
			mother.setAge(row[3] != null ? ((Number) row[3]).intValue() : 0);

			Newborn newborn = new Newborn();
			newborn.setId(((Number) row[4]).intValue());
			newborn.setBirthWeight(row[5] != null ? ((Number) row[5]).doubleValue() : 0.0);
			newborn.setNeonatalStatus(row[6] != null ? NeonatalStatus.valueOf((String) row[6]) : null);
			newborn.setApgarScore1Min(row[7] != null ? ((Number) row[7]).intValue() : 0);
			newborn.setApgarScore5Min(row[8] != null ? ((Number) row[8]).intValue() : 0);
			newborn.setResuscitationRequired(toBoolean(row[18]));
			newborn.setCryTime(row[15] != null ? CryTime.valueOf((String) row[15]) : null);
			newborn.setHivStatus(row[16] != null ? HivStatus.valueOf((String) row[16]) : null);
			newborn.setCongenitalAnomalies((String) row[17]);

			Object babySexObj = row[20];
			if (babySexObj != null) {
				String babySex = babySexObj.toString();
				if (!babySex.isEmpty()) {
					Patient babyPatient = new Patient();
					babyPatient.setSex(babySex.charAt(0));
					newborn.setBabyPatient(babyPatient);
				}
			}

			PregnancyDelivery delivery = new PregnancyDelivery();
			delivery.setDeliveryMode(parseDeliveryMode(row[9]));
			delivery.setDeliveryDate(row[10] != null ? ((java.sql.Timestamp) row[10]).toLocalDateTime() : null);
			delivery.setRuptureMembranesDateTime(row[11] != null ? ((java.sql.Timestamp) row[11]).toLocalDateTime() : null);
			delivery.setPerinealIntegrity(parsePerinealIntegrity(row[12]));
			delivery.setPlacentaComplete(toBoolean(row[13]));
			delivery.setEstimatedBloodLoss(row[14] != null ? ((Number) row[14]).intValue() : 0);
			delivery.setDeliveryType(new Typology((String) row[19], null, null));

			StatsDelivery statsDelivery = new StatsDelivery(mother, delivery, newborn);
			deliveries.add(statsDelivery);
		}

		return new PageImpl<>(deliveries, pageable, total);
	}

	private void bindParameters(Query query, List<Object> parameters) {
		for (int i = 0; i < parameters.size(); i++) {
			query.setParameter(i + 1, parameters.get(i));
		}
	}
}