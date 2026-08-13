/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.patvac.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.isf.patvac.model.PatientVaccine;
import org.isf.utils.time.TimeTools;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PatVacIoOperationRepositoryImpl implements PatVacIoOperationRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;


	@SuppressWarnings("unchecked")
	@Override
	public List<PatientVaccine> findAllByCodesAndDatesAndSexAndAges(
			String vaccineTypeCode,
			String vaccineCode,
			LocalDateTime dateFrom,
			LocalDateTime dateTo,
			char sex,
			int ageFrom,
			int ageTo) {
		return this.entityManager.
				createQuery(getPatientVaccineQuery(vaccineTypeCode, vaccineCode, TimeTools.truncateToSeconds(dateFrom),
				                                   TimeTools.truncateToSeconds(dateTo), sex, ageFrom, ageTo)).getResultList();
	}

	@Override
	public Page<PatientVaccine> findAllByCodesAndDatesAndSexAndAgesPageable(
			String vaccineTypeCode,
			String vaccineCode,
			LocalDateTime dateFrom,
			LocalDateTime dateTo,
			char sex,
			int ageFrom,
			int ageTo,
			Pageable pageable) {
		LocalDateTime truncatedDateFrom = TimeTools.truncateToSeconds(dateFrom);
		LocalDateTime truncatedDateTo = TimeTools.truncateToSeconds(dateTo);

		CriteriaQuery<PatientVaccine> dataQuery = getPatientVaccineQuery(vaccineTypeCode, vaccineCode, truncatedDateFrom, truncatedDateTo, sex, ageFrom,
				ageTo);
		TypedQuery<PatientVaccine> typedDataQuery = entityManager.createQuery(dataQuery);
		typedDataQuery.setFirstResult((int) pageable.getOffset());
		typedDataQuery.setMaxResults(pageable.getPageSize());
		List<PatientVaccine> content = typedDataQuery.getResultList();

		long total = countByCodesAndDatesAndSexAndAges(vaccineTypeCode, vaccineCode, truncatedDateFrom, truncatedDateTo, sex, ageFrom, ageTo);

		return new PageImpl<>(content, pageable, total);
	}

	private long countByCodesAndDatesAndSexAndAges(
			String vaccineTypeCode,
			String vaccineCode,
			LocalDateTime dateFrom,
			LocalDateTime dateTo,
			char sex,
			int ageFrom,
			int ageTo) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<PatientVaccine> pvRoot = query.from(PatientVaccine.class);

		query.select(cb.count(pvRoot));
		query.where(cb.and(buildPredicates(cb, pvRoot, vaccineTypeCode, vaccineCode, dateFrom, dateTo, sex, ageFrom, ageTo).toArray(new Predicate[0])));

		return entityManager.createQuery(query).getSingleResult();
	}

	private CriteriaQuery<PatientVaccine> getPatientVaccineQuery(
			String vaccineTypeCode,
			String vaccineCode,
			LocalDateTime dateFrom,
			LocalDateTime dateTo,
			char sex,
			int ageFrom,
			int ageTo) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PatientVaccine> query = cb.createQuery(PatientVaccine.class);
		Root<PatientVaccine> pvRoot = query.from(PatientVaccine.class);
		List<Predicate> predicates = buildPredicates(cb, pvRoot, vaccineTypeCode, vaccineCode, dateFrom, dateTo, sex, ageFrom, ageTo);

		query.select(pvRoot);
		query.where(cb.and(predicates.toArray(new Predicate[0])));
		query.orderBy(cb.desc(pvRoot.get("vaccineDate")), cb.asc(pvRoot.get("code")));

		return query;
	}

	/**
	 * Builds the shared filter predicates used by both the data query and its matching count query, so
	 * the two can never drift apart.
	 */
	private List<Predicate> buildPredicates(
			CriteriaBuilder cb,
			Root<PatientVaccine> pvRoot,
			String vaccineTypeCode,
			String vaccineCode,
			LocalDateTime dateFrom,
			LocalDateTime dateTo,
			char sex,
			int ageFrom,
			int ageTo) {
		List<Predicate> predicates = new ArrayList<>();

		if (dateFrom != null) {
			predicates.add(
					cb.greaterThanOrEqualTo(pvRoot.<LocalDateTime> get("vaccineDate"), dateFrom)
			);
		}
		if (dateTo != null) {
			predicates.add(
					cb.lessThanOrEqualTo(pvRoot.<LocalDateTime> get("vaccineDate"), dateTo)
			);
		}
		if (vaccineTypeCode != null) {
			predicates.add(
				cb.equal(pvRoot.join("vaccine").get("vaccineType").get("code"), vaccineTypeCode)
			);
		}
		if (vaccineCode != null) {
			predicates.add(
				cb.equal(pvRoot.join("vaccine").get("code"), vaccineCode)
			);
		}
		if (sex != 'A') {
			predicates.add(
				cb.equal(pvRoot.join("patient").get("sex"), sex)
			);
		}
		if (ageFrom != 0 || ageTo != 0) {
			predicates.add(
				cb.between(pvRoot.join("patient").<Integer>get("age"), ageFrom, ageTo)
			);
		}

		return predicates;
	}

}