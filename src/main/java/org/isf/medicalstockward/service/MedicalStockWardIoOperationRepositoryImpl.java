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
import java.util.ArrayList;
import java.util.Comparator;
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

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Integer> query = builder.createQuery(Integer.class);
		Root<MovementWard> root = query.from(MovementWard.class);
		query.select(root.<Integer>get(CODE));

		List<Predicate> predicates = buildFilteredWardMovementPredicates(
			builder, root, wardId, dateFrom, dateTo,
			medicalTypeCode, medicalCode, sex, ageFrom, ageTo, weightFrom, weightTo);

		List<Order> orderList = new ArrayList<>();
		orderList.add(builder.asc(root.get(DATE)));
		query.where(predicates.toArray(new Predicate[]{})).orderBy(orderList);

		List<Integer> ids = entityManager.createQuery(query)
			.setFirstResult((int) pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();

		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<MovementWard> countRoot = countQuery.from(MovementWard.class);
		countQuery.select(builder.count(countRoot));

		List<Predicate> countPredicates = buildFilteredWardMovementPredicates(
			builder, countRoot, wardId, dateFrom, dateTo,
			medicalTypeCode, medicalCode, sex, ageFrom, ageTo, weightFrom, weightTo);
		countQuery.where(countPredicates.toArray(new Predicate[]{}));

		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(ids, pageable, total);
	}

	@Override
	public Page<Movement> findIncomingMovements(String wardId,
	                                            LocalDateTime dateFrom, LocalDateTime dateTo,
	                                            Pageable pageable) {

		String countSql =
			"SELECT COUNT(*) FROM (" +
				"  SELECT 1 FROM OH_MEDICALDSRSTOCKMOV " +
				"  WHERE MMVN_WRD_ID_A = :wardId " +
				"  AND MMVN_DATE BETWEEN :dateFrom AND :dateTo " +
				"  UNION ALL " +
				"  SELECT 1 FROM OH_MEDICALDSRSTOCKMOVWARD " +
				"  WHERE MMVN_WRD_ID_A_TO = :wardId " +
				"  AND MMVN_DATE BETWEEN :dateFrom AND :dateTo " +
				") AS combined";

		Long total = ((Number) entityManager.createNativeQuery(countSql)
			.setParameter("wardId", wardId)
			.setParameter("dateFrom", dateFrom)
			.setParameter("dateTo", dateTo)
			.getSingleResult()).longValue();

		if (total == 0) {
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		}

		String idsSql =
			"SELECT id, type_source FROM (" +
				"  SELECT MMV_ID as id, 'central' as type_source, MMV_DATE as mov_date " +
				"  FROM OH_MEDICALDSRSTOCKMOV " +
				"  WHERE MMV_WRD_ID_A = :wardId " +
				"  AND MMV_DATE BETWEEN :dateFrom AND :dateTo " +
				"  UNION ALL " +
				"  SELECT MMVN_ID as id, 'ward' as type_source, MMVN_DATE as mov_date " +
				"  FROM OH_MEDICALDSRSTOCKMOVWARD " +
				"  WHERE MMVN_WRD_ID_A_TO = :wardId " +
				"  AND MMVN_DATE BETWEEN :dateFrom AND :dateTo " +
				") AS combined " +
				"ORDER BY mov_date ASC " +
				"LIMIT :limit OFFSET :offset";

		List<Object[]> rows = entityManager.createNativeQuery(idsSql)
			.setParameter("wardId", wardId)
			.setParameter("dateFrom", dateFrom)
			.setParameter("dateTo", dateTo)
			.setParameter("limit", pageable.getPageSize())
			.setParameter("offset", pageable.getOffset())
			.getResultList();

		List<Integer> centralIds = new ArrayList<>();
		List<Integer> wardIds = new ArrayList<>();

		for (Object[] row : rows) {
			Integer id = (Integer) row[0];
			String source = (String) row[1];
			if ("central".equals(source)) {
				centralIds.add(id);
			} else {
				wardIds.add(id);
			}
		}

		List<Movement> result = new ArrayList<>();

		if (!centralIds.isEmpty()) {
			List<Movement> centralMovements = movementRepository.findAllByIdsWithFetch(centralIds);
			result.addAll(centralMovements);
		}

		if (!wardIds.isEmpty()) {
			List<MovementWard> wardMovements = movementWardRepository.findAllByIds(wardIds);
			for (MovementWard mw : wardMovements) {
				result.add(convertMovementWardToMovement(mw));
			}
		}

		result.sort(Comparator.comparing(Movement::getDate));

		return new PageImpl<>(result, pageable, total);
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
}
