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
package org.isf.patvac.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

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
			Integer ageFrom,
			Integer ageTo) {
		return this.entityManager.
				createQuery(getPatientVaccineQuery(vaccineTypeCode, vaccineCode, TimeTools.truncateToSeconds(dateFrom),
				                                   TimeTools.truncateToSeconds(dateTo), sex, ageFrom, ageTo)).getResultList();
	}
	/**
	 * Returns a page of {@link PatientVaccine}s filtered by vaccine type, vaccine, date range, sex and age.
	 *
	 * @param vaccineTypeCode the vaccine type code
	 * @param vaccineCode the vaccine code
	 * @param dateFrom the start date
	 * @param dateTo the end date
	 * @param sex the patient sex
	 * @param ageFrom the minimum age
	 * @param ageTo the maximum age
	 * @param pageable the pagination information
	 * @return a page of {@link PatientVaccine}s
	 * @throws OHServiceException
	 */
	@Override
	public Page<PatientVaccine> findAllByCodesAndDatesAndSexAndAgesWithPagination(String vaccineTypeCode, String vaccineCode, LocalDateTime dateFrom,
		LocalDateTime dateTo, char sex, Integer ageFrom, Integer ageTo, Pageable pageable) throws OHServiceException {

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
		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));

		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(content, pageable, total);
	}

	private boolean hasAgeFilter(Integer ageFrom, Integer ageTo) {
		if (ageFrom == null && ageTo == null) {
			return false;
		}
		if (ageFrom != null && ageFrom == 0 && ageTo != null && ageTo == 0) {
			return false;
		}
		if (ageFrom != null && ageFrom == 0 && ageTo == null) {
			return false;
		}
		return (ageFrom != null && ageFrom > 0) || ageTo != null;
	}

	private Predicate buildAgePredicate(CriteriaBuilder cb, Path<?> patientPath, Integer ageFrom, Integer ageTo) {
		LocalDate today = LocalDate.now();
		List<Predicate> birthDatePreds = new ArrayList<>();
		List<Predicate> fallbackAgePreds = new ArrayList<>();

		birthDatePreds.add(cb.isNotNull(patientPath.get("birthDate")));
		fallbackAgePreds.add(cb.isNull(patientPath.get("birthDate")));

		if (ageFrom != null && ageFrom > 0) {
			birthDatePreds.add(cb.lessThanOrEqualTo(patientPath.get("birthDate"), today.minusYears(ageFrom)));
			fallbackAgePreds.add(cb.greaterThanOrEqualTo(patientPath.<Integer>get("age"), ageFrom));
		}
		if (ageTo != null && ageTo >= 0 && !(ageTo == 0 && (ageFrom != null && ageFrom > 0))) {
			birthDatePreds.add(cb.greaterThan(patientPath.get("birthDate"), today.minusYears(ageTo + 1)));
			fallbackAgePreds.add(cb.lessThanOrEqualTo(patientPath.<Integer>get("age"), ageTo));
		}

		Predicate birthDatePredicate = cb.and(birthDatePreds.toArray(new Predicate[0]));
		Predicate fallbackAgePredicate = cb.and(fallbackAgePreds.toArray(new Predicate[0]));

		return cb.or(birthDatePredicate, fallbackAgePredicate);
	}

	private From<?, ?> getOrCreatePatientJoin(From<?, ?> root) {
		for (Join<?, ?> join : root.getJoins()) {
			if (join.getAttribute() != null && "patient".equals(join.getAttribute().getName())) {
				return join;
			}
		}
		return root.join("patient");
	}

	/**
	 * Builds the list of predicates for the query based on the filter criteria.
	 *
	 * @param vaccineTypeCode the vaccine type code
	 * @param vaccineCode the vaccine code
	 * @param dateFrom the start date
	 * @param dateTo the end date
	 * @param sex the patient sex
	 * @param ageFrom the minimum age
	 * @param ageTo the maximum age
	 * @return the list of predicates
	 */
	private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<?> root, String vaccineTypeCode, String vaccineCode, LocalDateTime dateFrom,
	                                        LocalDateTime dateTo, char sex, Integer ageFrom, Integer ageTo) {

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
			predicates.add(cb.equal(getOrCreatePatientJoin(root).get("sex"), sex));
		}
		if (hasAgeFilter(ageFrom, ageTo)) {
			predicates.add(buildAgePredicate(cb, getOrCreatePatientJoin(root), ageFrom, ageTo));
		}
		return predicates;
	}

	private CriteriaQuery<PatientVaccine> getPatientVaccineQuery(String vaccineTypeCode, String vaccineCode, LocalDateTime dateFrom, LocalDateTime dateTo,
			char sex, Integer ageFrom, Integer ageTo) {
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
				cb.equal(getOrCreatePatientJoin(pvRoot).get("sex"), sex)
			);
		}
		if (hasAgeFilter(ageFrom, ageTo)) {
			predicates.add(
				buildAgePredicate(cb, getOrCreatePatientJoin(pvRoot), ageFrom, ageTo)
			);
		}
		query.where(cb.and(predicates.toArray(new Predicate[0])));
		query.orderBy(cb.desc(pvRoot.get("vaccineDate")), cb.asc(pvRoot.get("code")));

		return query;
	}

	/**
	 * Retrieves a paginated list of {@link PatientVaccine} records filtered by multiple criteria.
	 *
	 * @param vaccineTypeCode the vaccine type code
	 * @param vaccineCode the vaccine code
	 * @param dateFrom the start date
	 * @param dateTo the end date
	 * @param sex the patient sex
	 * @param ageFrom the minimum age
	 * @param ageTo the maximum age
	 * @param patientSearchText the search patient
	 * @param villageText the search village by text
	 * @return the list of predicates
	 */
	@Override
	public Page<PatientVaccine> findAllByCodesAndDatesAndSexAndAgesWithPagination(String vaccineTypeCode, String vaccineCode, LocalDateTime dateFrom,
		LocalDateTime dateTo, char sex, Integer ageFrom, Integer ageTo, String patientSearchText, String villageText, Pageable pageable) throws OHServiceException {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PatientVaccine> query = cb.createQuery(PatientVaccine.class);
		Root<PatientVaccine> pvRoot = query.from(PatientVaccine.class);
		List<Predicate> predicates = buildPredicatesWithSearch(cb, pvRoot, vaccineTypeCode, vaccineCode,
			TimeTools.truncateToSeconds(dateFrom), TimeTools.truncateToSeconds(dateTo), sex, ageFrom, ageTo,
			patientSearchText, villageText);

		query.select(pvRoot).where(cb.and(predicates.toArray(new Predicate[0])))
			.orderBy(cb.desc(pvRoot.get("vaccineDate")), cb.asc(pvRoot.get("code")));

		TypedQuery<PatientVaccine> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());
		List<PatientVaccine> content = typedQuery.getResultList();

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<PatientVaccine> countRoot = countQuery.from(PatientVaccine.class);
		List<Predicate> countPredicates = buildPredicatesWithSearch(cb, countRoot, vaccineTypeCode, vaccineCode,
			TimeTools.truncateToSeconds(dateFrom), TimeTools.truncateToSeconds(dateTo), sex, ageFrom, ageTo,
			patientSearchText, villageText);
		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));

		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(content, pageable, total);
	}

	/**
	 * Builds the list of predicates for the query including patient search and village filters.
	 *
	 * @param vaccineTypeCode the vaccine type code
	 * @param vaccineCode the vaccine code
	 * @param dateFrom the start date
	 * @param dateTo the end date
	 * @param sex the patient sex
	 * @param ageFrom the minimum age
	 * @param ageTo the maximum age
	 * @param patientSearchText the search patient
	 * @param villageText the search village by text
	 * @return the list of predicates
	 */
	private List<Predicate> buildPredicatesWithSearch(CriteriaBuilder cb, Root<?> root, String vaccineTypeCode, String vaccineCode, LocalDateTime dateFrom,
	                                                  LocalDateTime dateTo, char sex, Integer ageFrom, Integer ageTo, String patientSearchText, String villageText) {
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
			predicates.add(cb.equal(getOrCreatePatientJoin(root).get("sex"), sex));
		}
		if (hasAgeFilter(ageFrom, ageTo)) {
			predicates.add(buildAgePredicate(cb, getOrCreatePatientJoin(root), ageFrom, ageTo));
		}
		if (patientSearchText != null && !patientSearchText.trim().isEmpty()) {
			String searchPattern = "%" + patientSearchText.trim().toLowerCase() + "%";
			From<?, ?> patientPath = getOrCreatePatientJoin(root);
			Predicate codePredicate = cb.like(
				cb.lower(patientPath.get("code").as(String.class)), searchPattern);
			Predicate namePredicate = cb.like(
				cb.lower(cb.concat(cb.concat(patientPath.get("firstName"), " "), patientPath.get("secondName"))), searchPattern);
			predicates.add(cb.or(codePredicate, namePredicate));
		}
		if (villageText != null && !villageText.trim().isEmpty()) {
			predicates.add(cb.like(
				cb.lower(root.get("village").as(String.class)),
				"%" + villageText.trim().toLowerCase() + "%"));
		}
		return predicates;
	}

	@Override
	public List<PatientVaccine> findAllByFilter(String vaccineTypeCode, String vaccineCode, LocalDateTime dateFrom,
			LocalDateTime dateTo, char sex, Integer ageFrom, Integer ageTo, String patientSearchText, String villageText) throws OHServiceException {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PatientVaccine> query = cb.createQuery(PatientVaccine.class);
		Root<PatientVaccine> pvRoot = query.from(PatientVaccine.class);
		List<Predicate> predicates = buildPredicatesWithSearch(cb, pvRoot, vaccineTypeCode, vaccineCode,
			TimeTools.truncateToSeconds(dateFrom), TimeTools.truncateToSeconds(dateTo), sex, ageFrom, ageTo,
			patientSearchText, villageText);

		query.select(pvRoot).where(cb.and(predicates.toArray(new Predicate[0])))
			.orderBy(cb.desc(pvRoot.get("vaccineDate")), cb.asc(pvRoot.get("code")));

		TypedQuery<PatientVaccine> typedQuery = entityManager.createQuery(query);
		return typedQuery.getResultList();
	}

}