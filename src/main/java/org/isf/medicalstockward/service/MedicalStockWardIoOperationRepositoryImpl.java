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
import org.isf.medicalstockward.model.MovementWard;
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

		query.where(predicates.toArray(new Predicate[] {})).orderBy(orderList);
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
}
