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
import org.isf.utils.exception.OHServiceException;
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
	/**
	 * Returns a page of {@link PatientVaccine}s filtered by vaccine type, vaccine, date range, sex and age.
	 * This method uses Criteria API for dynamic query building with pagination support.
	 *
	 * @param vaccineTypeCode the vaccine type code (can be {@code null})
	 * @param vaccineCode the vaccine code (can be {@code null})
	 * @param dateFrom the start date (can be {@code null})
	 * @param dateTo the end date (can be {@code null})
	 * @param sex the patient sex ('M', 'F' or 'A' for all)
	 * @param ageFrom the minimum age (0 for no minimum)
	 * @param ageTo the maximum age (0 for no maximum)
	 * @param pageable the pagination information
	 * @return a page of {@link PatientVaccine}s
	 * @throws OHServiceException
	 */
	@Override
	public Page<PatientVaccine> findAllByCodesAndDatesAndSexAndAgesWithPagination(
		String vaccineTypeCode,
		String vaccineCode,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		char sex,
		int ageFrom,
		int ageTo,
		Pageable pageable) throws OHServiceException {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PatientVaccine> query = cb.createQuery(PatientVaccine.class);
		Root<PatientVaccine> pvRoot = query.from(PatientVaccine.class);
		List<Predicate> predicates = buildPredicates(cb, pvRoot, vaccineTypeCode, vaccineCode, TimeTools.truncateToSeconds(dateFrom), TimeTools.truncateToSeconds(dateTo), sex, ageFrom, ageTo);

		query.select(pvRoot).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.desc(pvRoot.get("vaccineDate")), cb.asc(pvRoot.get("code")));

		TypedQuery<PatientVaccine> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());
		List<PatientVaccine> content = typedQuery.getResultList();
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<PatientVaccine> countRoot = countQuery.from(PatientVaccine.class);
		List<Predicate> countPredicates = buildPredicates(cb, countRoot, vaccineTypeCode, vaccineCode,
			TimeTools.truncateToSeconds(dateFrom), TimeTools.truncateToSeconds(dateTo), sex, ageFrom, ageTo);
		countQuery.select(cb.count(countRoot))
			.where(cb.and(countPredicates.toArray(new Predicate[0])));

		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(content, pageable, total);
	}

	/**
	 * Builds the list of predicates for the query based on the filter criteria.
	 *
	 * @param vaccineTypeCode the vaccine type code (can be {@code null})
	 * @param vaccineCode the vaccine code (can be {@code null})
	 * @param dateFrom the start date (can be {@code null})
	 * @param dateTo the end date (can be {@code null})
	 * @param sex the patient sex ('M', 'F' or 'A' for all)
	 * @param ageFrom the minimum age (0 for no minimum)
	 * @param ageTo the maximum age (0 for no maximum)
	 * @return the list of predicates
	 */
	private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<?> root,
	                                        String vaccineTypeCode, String vaccineCode, LocalDateTime dateFrom,
	                                        LocalDateTime dateTo, char sex, int ageFrom, int ageTo) {

		List<Predicate> predicates = new ArrayList<>();

		if (dateFrom != null) {
			predicates.add(cb.greaterThanOrEqualTo(root.<LocalDateTime>get("vaccineDate"), dateFrom));
		}
		if (dateTo != null) {
			predicates.add(cb.lessThanOrEqualTo(root.<LocalDateTime>get("vaccineDate"), dateTo));
		}
		if (vaccineTypeCode != null) {
			predicates.add(cb.equal(root.join("vaccine").get("vaccineType").get("code"), vaccineTypeCode));
		}
		if (vaccineCode != null) {
			predicates.add(cb.equal(root.join("vaccine").get("code"), vaccineCode));
		}
		if (sex != 'A') {
			predicates.add(cb.equal(root.join("patient").get("sex"), sex));
		}
		if (ageFrom != 0 || ageTo != 0) {
			predicates.add(cb.between(root.join("patient").<Integer>get("age"), ageFrom, ageTo));
		}

		return predicates;
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
		List<Predicate> predicates = new ArrayList<>();

		query.select(pvRoot);
		if (dateFrom != null) {
			predicates.add(
					cb.greaterThanOrEqualTo(pvRoot.<LocalDateTime> get("vaccineDate"), TimeTools.truncateToSeconds(dateFrom))
			);
		}
		if (dateTo != null) {
			predicates.add(
					cb.lessThanOrEqualTo(pvRoot.<LocalDateTime> get("vaccineDate"), TimeTools.truncateToSeconds(dateTo))
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
		query.where(cb.and(predicates.toArray(new Predicate[0])));
		query.orderBy(cb.desc(pvRoot.get("vaccineDate")), cb.asc(pvRoot.get("code")));

		return query;
	}

}