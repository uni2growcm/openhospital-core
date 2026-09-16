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

import org.isf.dlvrtype.model.DeliveryType;
import org.isf.patient.model.Patient;
import org.isf.pregnancy.model.PregnancyDelivery;
import org.isf.pregnancy.model.PregnancyNewborn;
import org.isf.stat2.model.StatsDelivery;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class StatsDeliveryIoOperations {

	private static final String YYYY_MM_DD = "yyyy-MM-dd";

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Returns a paginated list of deliveries matching the applied filters,
	 * following the OH-538 pregnancy module schema (OH_PREGNANCYDELIVERY +
	 * OH_PREGNANCYNEWBORN).
	 *
	 * @param periodFrom             start of delivery period, null for no start
	 * @param periodTo               end of delivery period, null for no end
	 * @param motherAgeMin           minimum mother age, null for no lower bound
	 * @param motherAgeMax           maximum mother age, null for no upper bound
	 * @param sex                    newborn sex (M/F), null for no filter
	 * @param weightMin              minimum birth weight (kg), null for no lower bound
	 * @param weightMax              maximum birth weight (kg), null for no upper bound
	 * @param deliveryType           delivery type description, null for no filter
	 * @param deliveryResultType     delivery result type description, null for no filter
	 * @param hivExposed             HIV exposed newborn, null for no filter
	 * @param congenitalMalformation congenital malformation present, null for no filter
	 * @param disease                mother disease description, null for no filter
	 * @param dischargeType          mother discharge type description, null for no filter
	 * @param page                   page number (0-indexed)
	 * @param size                   page size
	 * @return a Page of StatsDelivery matching the filters
	 * @throws OHServiceException if an error occurs during the database operation
	 */
	public Page<StatsDelivery> getDeliveriesStats(
		LocalDateTime periodFrom,
		LocalDateTime periodTo,
		Integer motherAgeMin,
		Integer motherAgeMax,
		String sex,
		Double weightMin,
		Double weightMax,
		String deliveryType,
		String deliveryResultType,
		Boolean hivExposed,
		Boolean congenitalMalformation,
		String disease,
		String dischargeType,
		int page,
		int size
	) throws OHServiceException {

		StringBuilder fromBuilder = new StringBuilder();
		StringBuilder conditions = new StringBuilder();
		List<Object> parameters = new ArrayList<>();

		fromBuilder.append(" FROM oh_patient _mother")
			.append(" INNER JOIN oh_pregnancy _pregnancy ON _mother.PAT_ID = _pregnancy.PREG_PAT_ID")
			.append(" INNER JOIN oh_pregnancydelivery _delivery ON _delivery.PDEL_PREG_ID = _pregnancy.PREG_ID")
			.append(" INNER JOIN oh_pregnancynewborn _newborn ON _newborn.PNB_PDEL_ID = _delivery.PDEL_ID");

		conditions.append("(_mother.PAT_DELETED = 'N' OR _mother.PAT_DELETED IS NULL)");

		if (periodFrom != null) {
			conditions.append(" AND DATE(_delivery.PDEL_DATE_DEL) >= ?");
			parameters.add(TimeTools.formatDateTime(periodFrom, YYYY_MM_DD));
		}
		if (periodTo != null) {
			conditions.append(" AND DATE(_delivery.PDEL_DATE_DEL) <= ?");
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

		if (sex != null && !sex.isEmpty()) {
			conditions.append(" AND _newborn.PNB_SEX = ?");
			parameters.add(sex);
		}

		if (weightMin != null) {
			conditions.append(" AND _newborn.PNB_WEIGHT >= ?");
			parameters.add(weightMin);
		}
		if (weightMax != null) {
			conditions.append(" AND _newborn.PNB_WEIGHT <= ?");
			parameters.add(weightMax);
		}

		if (deliveryType != null && !deliveryType.isEmpty()) {
			fromBuilder.append(" INNER JOIN oh_deliverytype _dlvtype ON _dlvtype.DLT_ID_A = _newborn.PNB_DLT_ID_A");
			conditions.append(" AND _dlvtype.DLT_DESC = ?");
			parameters.add(deliveryType);
		}

		if (deliveryResultType != null && !deliveryResultType.isEmpty()) {
			fromBuilder.append(" INNER JOIN oh_deliveryresulttype _dlvres ON _dlvres.DRT_ID_A = _newborn.PNB_DRT_ID_A");
			conditions.append(" AND _dlvres.DRT_DESC = ?");
			parameters.add(deliveryResultType);
		}

		if (hivExposed != null) {
			conditions.append(hivExposed.booleanValue()
					? " AND _newborn.PNB_HIV_EXPOSED = 1"
					: " AND _newborn.PNB_HIV_EXPOSED = 0");
		}

		if (congenitalMalformation != null) {
			if (congenitalMalformation.booleanValue()) {
				conditions.append(" AND (_newborn.PNB_MALFORMATION IS NOT NULL AND _newborn.PNB_MALFORMATION != '')");
			} else {
				conditions.append(" AND (_newborn.PNB_MALFORMATION IS NULL OR _newborn.PNB_MALFORMATION = '')");
			}
		}

		boolean admissionJoined = false;
		if (disease != null && !disease.isEmpty()) {
			fromBuilder.append(" INNER JOIN oh_admission _patadm ON _patadm.ADM_PAT_ID = _mother.PAT_ID")
				.append(" INNER JOIN oh_disease _disease ON _patadm.ADM_OUT_DIS_ID_A = _disease.DIS_ID_A")
				.append(" OR _patadm.ADM_OUT_DIS_ID_A_2 = _disease.DIS_ID_A")
				.append(" OR _patadm.ADM_OUT_DIS_ID_A_3 = _disease.DIS_ID_A");
			conditions.append(" AND _disease.DIS_DESC = ?");
			parameters.add(disease);
			admissionJoined = true;
		}

		if (dischargeType != null && !dischargeType.isEmpty()) {
			if (!admissionJoined) {
				fromBuilder.append(" INNER JOIN oh_admission _patadm ON _patadm.ADM_PAT_ID = _mother.PAT_ID");
				admissionJoined = true;
			}
			fromBuilder.append(" INNER JOIN oh_dischargetype _disctype ON _disctype.DIST_ID_A = _patadm.ADM_DIST_ID_A");
			conditions.append(" AND _disctype.DIST_DESC = ?");
			parameters.add(dischargeType);
		}

		String from = fromBuilder.toString();
		String where = " WHERE " + conditions;

		String dataSql = "SELECT DISTINCT _mother.PAT_ID, _mother.PAT_FNAME, _mother.PAT_SNAME, _mother.PAT_AGE, "
			+ "_newborn.PNB_ID, _newborn.PNB_SEX, _newborn.PNB_WEIGHT, _newborn.PNB_DLT_ID_A, "
			+ "_newborn.PNB_DRT_ID_A, _delivery.PDEL_DATE_DEL, _delivery.PDEL_COUNSELING, "
			+ "_delivery.PDEL_FP_METHOD, _newborn.PNB_COMPLICATIONS, _newborn.PNB_MALFORMATION, "
			+ "_newborn.PNB_HIV_EXPOSED, _newborn.PNB_CHILD_NUMBER"
			+ from + where
			+ " ORDER BY _delivery.PDEL_DATE_DEL DESC";

		Query dataQuery = entityManager.createNativeQuery(dataSql);
		bindParameters(dataQuery, parameters);
		Pageable pageable = PageRequest.of(page, size);
		dataQuery.setFirstResult((int) pageable.getOffset());
		dataQuery.setMaxResults(pageable.getPageSize());

		String countSql = "SELECT COUNT(DISTINCT _newborn.PNB_ID)" + from + where;
		Query countQuery = entityManager.createNativeQuery(countSql);
		bindParameters(countQuery, parameters);

		long total = ((Number) countQuery.getSingleResult()).longValue();

		List<StatsDelivery> deliveries = new ArrayList<>();
		for (Object record : dataQuery.getResultList()) {
			deliveries.add(mapRow((Object[]) record));
		}

		return new PageImpl<>(deliveries, pageable, total);
	}

	private StatsDelivery mapRow(Object[] row) {
		Patient mother = new Patient();
		mother.setCode(((Number) row[0]).intValue());
		mother.setFirstName((String) row[1]);
		mother.setSecondName((String) row[2]);
		mother.setAge(row[3] != null ? ((Number) row[3]).intValue() : 0);

		PregnancyNewborn newborn = new PregnancyNewborn();
		newborn.setDelivery(new PregnancyDelivery());
		newborn.getDelivery().setId(((Number) row[4]).intValue());
		newborn.setSex(row[5] != null ? row[5].toString().charAt(0) : 'F');
		newborn.setWeight(row[6] != null ? ((Number) row[6]).floatValue() : null);
		if (row[7] != null) {
			DeliveryType deliveryType = new DeliveryType();
			deliveryType.setCode(row[7].toString());
			newborn.setDeliveryType(deliveryType);
		}
		newborn.setComplications((String) row[12]);
		newborn.setCongenitalMalformation((String) row[13]);
		newborn.setHivExposed(toBoolean(row[14]));
		newborn.setChildNumber(row[15] != null ? ((Number) row[15]).intValue() : 1);

		PregnancyDelivery delivery = newborn.getDelivery();
		delivery.setDeliveryDate(row[9] != null ? ((java.sql.Timestamp) row[9]).toLocalDateTime() : null);
		delivery.setCounseling((String) row[10]);
		delivery.setFamilyPlanningMethodChosen((String) row[11]);

		return new StatsDelivery(mother, delivery, newborn);
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
			return "1".equals(value) || "true".equalsIgnoreCase((String) value) || "Y".equalsIgnoreCase((String) value);
		}
		return false;
	}

	private void bindParameters(Query query, List<Object> parameters) {
		for (int i = 0; i < parameters.size(); i++) {
			query.setParameter(i + 1, parameters.get(i));
		}
	}
}
