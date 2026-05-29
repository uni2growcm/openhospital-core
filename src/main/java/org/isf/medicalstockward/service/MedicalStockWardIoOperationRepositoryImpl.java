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
package org.isf.medicalstockward.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.MovementIoOperationRepository;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medtype.model.MedicalType;
import org.isf.orthanc.model.Patient;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MedicalStockWardIoOperationRepositoryImpl implements MedicalStockWardIoOperationRepositoryCustom {

	private static final String WARD = "ward";
	private static final String DATE = "date";
	private static final String CODE = "code";

	private final MovementIoOperationRepository movementRepository;
	private final MovementWardIoOperationRepository movementWardRepository;

	public MedicalStockWardIoOperationRepositoryImpl(
		MovementIoOperationRepository movementRepository,
		MovementWardIoOperationRepository movementWardRepository) {
		this.movementRepository = movementRepository;
		this.movementWardRepository = movementWardRepository;
	}

	@PersistenceContext
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> findAllWardMovement(String wardId, LocalDateTime dateFrom, LocalDateTime dateTo) {

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> query = builder.createQuery(Integer.class);
		Root<MovementWard> root = query.from(MovementWard.class);
		query.select(root.<Integer>get(CODE));
		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotEmpty(wardId)) {
			predicates.add(builder.equal(root.<Ward>get(WARD).<String>get(CODE), wardId));
		}
		if ((dateFrom != null) && (dateTo != null)) {
			predicates.add(builder.between(root.<LocalDateTime>get(DATE), TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo)));
		}

		List<Order> orderList = new ArrayList<>();
		orderList.add(builder.asc(root.get(DATE)));

		query.where(predicates.toArray(new Predicate[]{})).orderBy(orderList);
		return entityManager.createQuery(query).getResultList();
	}

	private List<Predicate> buildFilteredWardMovementPredicates(
		CriteriaBuilder builder,
		Root<MovementWard> root,
		String wardId,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		String medicalTypeCode,
		Integer medicalCode,
		String sex,
		Integer ageFrom,
		Integer ageTo,
		Float weightFrom,
		Float weightTo) {

		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotEmpty(wardId)) {
			predicates.add(builder.equal(
				root.<Ward>get(WARD).<String>get(CODE), wardId));
		}

		if (dateFrom != null && dateTo != null) {
			predicates.add(builder.between(
				root.<LocalDateTime>get(DATE),
				TimeTools.getBeginningOfDay(dateFrom),
				TimeTools.getBeginningOfNextDay(dateTo)));
		}

		if (StringUtils.isNotEmpty(medicalTypeCode)) {
			predicates.add(builder.equal(
				root.<Medical>get("medical").<MedicalType>get("type").<String>get(CODE),
				medicalTypeCode));
		}

		if (medicalCode != null) {
			predicates.add(builder.equal(
				root.<Medical>get("medical").<Integer>get(CODE),
				medicalCode));
		}

		if (StringUtils.isNotEmpty(sex) && !sex.equals("A")) {
			predicates.add(builder.equal(
				root.<Patient>get("patient").<String>get("sex"),
				sex));
		}

		if (ageTo != null && ageTo != 0) {
			predicates.add(builder.greaterThanOrEqualTo(
				root.<Patient>get("patient").<Integer>get("age"),
				ageFrom != null ? ageFrom : 0));
			predicates.add(builder.lessThanOrEqualTo(
				root.<Patient>get("patient").<Integer>get("age"),
				ageTo));
		}

		if (weightTo != null && weightTo != 0) {
			predicates.add(builder.greaterThanOrEqualTo(
				root.<Patient>get("patient").<Float>get("weight"),
				weightFrom != null ? weightFrom : 0f));
			predicates.add(builder.lessThanOrEqualTo(
				root.<Patient>get("patient").<Float>get("weight"),
				weightTo));
		}

		return predicates;
	}

	@Override
	public Page<Integer> findWardMovementsWithFilter(
		String wardId, LocalDateTime dateFrom, LocalDateTime dateTo,
		String medicalTypeCode, Integer medicalCode,
		String sex, Integer ageFrom, Integer ageTo,
		Float weightFrom, Float weightTo,
		Pageable pageable) {

		FilterQuery fq = buildNativeFilter(wardId, dateFrom, dateTo,
			medicalTypeCode, medicalCode, sex, ageFrom, ageTo, weightFrom, weightTo);

		String countSql = "SELECT COUNT(*) FROM OH_MEDICALDSRSTOCKMOVWARD m "
			+ fq.joinClause + fq.whereClause;
		jakarta.persistence.Query countQuery = entityManager.createNativeQuery(countSql);
		fq.applyParams(countQuery);
		Long total = ((Number) countQuery.getSingleResult()).longValue();

		if (total == 0) {
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		}

		String idsSql = "SELECT m.MMVN_ID FROM OH_MEDICALDSRSTOCKMOVWARD m "
			+ fq.joinClause + fq.whereClause
			+ " ORDER BY m.MMVN_DATE DESC, m.MMVN_ID DESC"
			+ " LIMIT " + pageable.getPageSize()
			+ " OFFSET " + pageable.getOffset();

		jakarta.persistence.Query idsQuery = entityManager.createNativeQuery(idsSql);
		fq.applyParams(idsQuery);

		@SuppressWarnings("unchecked")
		List<Number> rawIds = idsQuery.getResultList();
		List<Integer> ids = rawIds.stream()
			.map(Number::intValue)
			.collect(java.util.stream.Collectors.toList());

		return new PageImpl<>(ids, pageable, total);
	}

	private FilterQuery buildNativeFilter(
		String wardId, LocalDateTime dateFrom, LocalDateTime dateTo,
		String medicalTypeCode, Integer medicalCode,
		String sex, Integer ageFrom, Integer ageTo,
		Float weightFrom, Float weightTo) {

		FilterQuery fq = new FilterQuery();

		if (StringUtils.isNotEmpty(wardId)) {
			fq.whereClause.append(" AND m.MMVN_WRD_ID_A = :wardId");
			fq.params.put("wardId", wardId);
		}
		if (dateFrom != null && dateTo != null) {
			fq.whereClause.append(" AND m.MMVN_DATE BETWEEN :dateFrom AND :dateTo");
			fq.params.put("dateFrom", TimeTools.getBeginningOfDay(dateFrom));
			fq.params.put("dateTo", TimeTools.getBeginningOfNextDay(dateTo));
		}
		if (StringUtils.isNotEmpty(medicalTypeCode)) {
			fq.joinClause.append(
				" INNER JOIN OH_MEDICALDSR med ON m.MMVN_MDSR_ID = med.MDSR_ID"
					+ " INNER JOIN OH_MEDICALDSRTYPE mt ON med.MDSR_MDSRT_ID_A = mt.MDSRT_ID_A");
			fq.whereClause.append(" AND mt.MDSRT_ID_A = :medicalTypeCode");
			fq.params.put("medicalTypeCode", medicalTypeCode);
		}
		if (medicalCode != null) {
			fq.whereClause.append(" AND m.MMVN_MDSR_ID = :medicalCode");
			fq.params.put("medicalCode", medicalCode);
		}
		if (StringUtils.isNotEmpty(sex) && !"A".equals(sex)) {
			fq.joinClause.append(" INNER JOIN OH_PATIENT p ON m.MMVN_PAT_ID = p.PAT_ID");
			fq.whereClause.append(" AND p.PAT_SEX = :sex");
			fq.params.put("sex", sex);
		}

		if (ageFrom != null && ageFrom >= 0) {
			fq.whereClause.append(" AND m.MMVN_PAT_AGE >= :ageFrom");
			fq.params.put("ageFrom", ageFrom);
		}
		if (ageTo != null && ageTo >= 0) {
			fq.whereClause.append(" AND m.MMVN_PAT_AGE <= :ageTo");
			fq.params.put("ageTo", ageTo);
		}
		if (weightFrom != null && weightFrom > 0) {
			fq.whereClause.append(" AND m.MMVN_PAT_WEIGHT >= :weightFrom");
			fq.params.put("weightFrom", weightFrom);
		}
		if (weightTo != null && weightTo > 0) {
			fq.whereClause.append(" AND m.MMVN_PAT_WEIGHT <= :weightTo");
			fq.params.put("weightTo", weightTo);
		}

		return fq;
	}

	private static class FilterQuery {
		final StringBuilder joinClause = new StringBuilder();
		final StringBuilder whereClause = new StringBuilder(" WHERE 1=1 ");
		final java.util.Map<String, Object> params = new java.util.LinkedHashMap<>();

		void applyParams(jakarta.persistence.Query query) {
			params.forEach(query::setParameter);
		}
	}

	private Movement convertMovementWardToMovement(MovementWard mw) {
		MovementType typeCharge = new MovementType(
			"fromward",
			mw.getWard().getDescription(),
			"*",
			"*"
		);
		return new Movement(
			mw.getMedical(),
			typeCharge,
			mw.getWardTo(),
			mw.getLot(),
			mw.getDate(),
			mw.getQuantity().intValue(),
			null,
			null
		);
	}

	@Override
	public Page<Movement> findIncomingMovements(String wardId,
	                                            LocalDateTime dateFrom, LocalDateTime dateTo,
	                                            Pageable pageable) {
		String baseCentralWhere =
			" MMV_WRD_ID_A = :wardId AND MMV_DATE BETWEEN :dateFrom AND :dateTo ";
		String baseWardWhere =
			" MMVN_WRD_ID_A_TO = :wardId AND MMVN_DATE BETWEEN :dateFrom AND :dateTo ";

		String countSql =
			"SELECT COUNT(*) FROM ("
				+ "  SELECT 1 FROM OH_MEDICALDSRSTOCKMOV WHERE " + baseCentralWhere
				+ "  UNION ALL "
				+ "  SELECT 1 FROM OH_MEDICALDSRSTOCKMOVWARD WHERE " + baseWardWhere
				+ ") AS combined";

		Long total = ((Number) entityManager.createNativeQuery(countSql)
			.setParameter("wardId", wardId)
			.setParameter("dateFrom", dateFrom)
			.setParameter("dateTo", dateTo)
			.getSingleResult()).longValue();

		if (total == 0) {
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		}

		String idsSql =
			"SELECT id, type_source FROM ("
				+ "  SELECT MMV_ID AS id, 'central' AS type_source, MMV_DATE AS mov_date"
				+ "  FROM OH_MEDICALDSRSTOCKMOV WHERE " + baseCentralWhere
				+ "  UNION ALL "
				+ "  SELECT MMVN_ID AS id, 'ward' AS type_source, MMVN_DATE AS mov_date"
				+ "  FROM OH_MEDICALDSRSTOCKMOVWARD WHERE " + baseWardWhere
				+ ") AS combined"
				+ " ORDER BY mov_date DESC"
				+ " LIMIT " + pageable.getPageSize()
				+ " OFFSET " + pageable.getOffset();

		@SuppressWarnings("unchecked")
		List<Object[]> rows = entityManager.createNativeQuery(idsSql)
			.setParameter("wardId", wardId)
			.setParameter("dateFrom", dateFrom)
			.setParameter("dateTo", dateTo)
			.getResultList();

		List<Integer> centralIds = new ArrayList<>();
		List<Integer> wardIds = new ArrayList<>();
		List<Object[]> orderedRows = new ArrayList<>(rows);

		for (Object[] row : rows) {
			Integer id = ((Number) row[0]).intValue();
			String source = (String) row[1];
			if ("central".equals(source)) centralIds.add(id);
			else wardIds.add(id);
		}

		Map<Integer, Movement> centralMap = new HashMap<>();
		Map<Integer, Movement> wardMap = new HashMap<>();

		if (!centralIds.isEmpty()) {
			movementRepository.findAllByIdsWithFetch(centralIds)
				.forEach(m -> centralMap.put(m.getCode(), m));
		}
		if (!wardIds.isEmpty()) {
			movementWardRepository.findAllByIds(wardIds)
				.forEach(mw -> wardMap.put(mw.getCode(), convertMovementWardToMovement(mw)));
		}

		List<Movement> result = new ArrayList<>(rows.size());
		for (Object[] row : orderedRows) {
			Integer id = ((Number) row[0]).intValue();
			String source = (String) row[1];
			Movement m = "central".equals(source) ? centralMap.get(id) : wardMap.get(id);
			if (m != null) result.add(m);
		}

		return new PageImpl<>(result, pageable, total);
	}
}
