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
package org.isf.patient.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.isf.patient.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PatientIoOperationRepositoryImpl implements PatientIoOperationRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<Patient> findByFieldsContainingWordsFromLiteral(String literal) {
		return this.entityManager.
				createQuery(buildSearchQuery(literal)).
				getResultList();
	}

	@Override
	public Page<Patient> findByFieldsContainingWordsFromLiteral(String literal, Pageable pageable) {
		return findByFieldsContainingWordsFromLiteral(literal, pageable, null);
	}

	@Override
	public Page<Patient> findByFieldsContainingWordsFromLiteral(String literal, Pageable pageable, Long knownTotalElements) {
		String[] words = getWordsToSearchForInPatientsRepository(literal);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaQuery<Patient> query = cb.createQuery(Patient.class);
		Root<Patient> patientRoot = query.from(Patient.class);
		query.select(patientRoot)
				.where(buildSearchPredicate(words, cb, patientRoot))
				.orderBy(cb.desc(patientRoot.get("code")));

		List<Patient> content = entityManager.createQuery(query)
				.setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize())
				.getResultList();

		long total;
		if (knownTotalElements != null) {
			total = knownTotalElements;
		} else {
			CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
			Root<Patient> countRoot = countQuery.from(Patient.class);
			countQuery.select(cb.count(countRoot)).where(buildSearchPredicate(words, cb, countRoot));
			total = entityManager.createQuery(countQuery).getSingleResult();
		}

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public Page<Patient> findAllNotDeletedOrderByName(char deletedStatus, Pageable pageable, Long knownTotalElements) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaQuery<Patient> query = cb.createQuery(Patient.class);
		Root<Patient> patientRoot = query.from(Patient.class);
		query.select(patientRoot)
				.where(buildNotDeletedPredicate(deletedStatus, cb, patientRoot))
				.orderBy(cb.asc(patientRoot.get("name")));

		List<Patient> content = entityManager.createQuery(query)
				.setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize())
				.getResultList();

		long total;
		if (knownTotalElements != null) {
			total = knownTotalElements;
		} else {
			CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
			Root<Patient> countRoot = countQuery.from(Patient.class);
			countQuery.select(cb.count(countRoot)).where(buildNotDeletedPredicate(deletedStatus, cb, countRoot));
			total = entityManager.createQuery(countQuery).getSingleResult();
		}

		return new PageImpl<>(content, pageable, total);
	}

	private Predicate buildNotDeletedPredicate(char deletedStatus, CriteriaBuilder cb, Root<Patient> patientRoot) {
		return cb.or(
				cb.equal(patientRoot.get("deleted"), deletedStatus),
				cb.isNull(patientRoot.get("deleted"))
		);
	}

	private CriteriaQuery<Patient> buildSearchQuery(String regex) {
		String[] words = getWordsToSearchForInPatientsRepository(regex);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Patient> query = cb.createQuery(Patient.class);
		Root<Patient> patientRoot = query.from(Patient.class);
		query.select(patientRoot)
				.where(buildSearchPredicate(words, cb, patientRoot))
				.orderBy(cb.desc(patientRoot.get("code")));
		return query;
	}

	private String[] getWordsToSearchForInPatientsRepository(String regex) {
		String[] words = new String[0];

		if (regex != null && !regex.isEmpty()) {
			String string = regex.trim().toLowerCase();
			words = string.split(" ");
		}

		return words;
	}

	private Predicate buildSearchPredicate(String[] words, CriteriaBuilder cb, Root<Patient> patientRoot) {
		List<Predicate> where = new ArrayList<>();

		for (String word : words) {
			where.add(wordExistsInOneOfPatientFields(word, cb, patientRoot));
		}

		where.add(cb.or(
				cb.equal(patientRoot.get("deleted"), 'N'),
				cb.isNull(patientRoot.get("deleted"))
		));

		return cb.and(where.toArray(new Predicate[0]));
	}

	private Predicate wordExistsInOneOfPatientFields(String word, CriteriaBuilder cb, Root<Patient> root) {
		return cb.or(
				cb.like(cb.lower(root.get("code").as(String.class)), like(word)),
				cb.like(cb.lower(root.get("firstName").as(String.class)), like(word)),
				cb.like(cb.lower(root.get("secondName").as(String.class)), like(word)),
				cb.like(cb.lower(root.get("city").as(String.class)), like(word)),
				cb.like(cb.lower(root.get("address").as(String.class)), like(word)),
				cb.like(cb.lower(root.get("telephone").as(String.class)), like(word)),
				cb.like(cb.lower(root.get("note").as(String.class)), like(word)),
				cb.like(cb.lower(root.get("taxCode").as(String.class)), like(word))
		);
	}

	private String like(String word) {
		return '%' + word + '%';
	}

	public List<Patient> getPatientsByParams(Map<String, Object> params) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Patient> query = cb.createQuery(Patient.class);
		Root<Patient> patient = query.from(Patient.class);

		// Only not deleted patient
		Predicate deletedN = cb.equal(patient.get("deleted"), 'N');
		Predicate deletedNull = cb.isNull(patient.get("deleted"));
		Predicate notDeleted = cb.or(deletedN, deletedNull);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(notDeleted);
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			Path<String> keyPath = patient.get(entry.getKey());

			if (entry.getKey().equals("birthDate")) {
				LocalDateTime birthDateFrom = (LocalDateTime) entry.getValue();
				LocalDateTime birthDateTo = birthDateFrom.plusDays(1);
				predicates.add(cb.between(keyPath.as(LocalDateTime.class), birthDateFrom, birthDateTo));
			} else {
				if (entry.getValue() instanceof String) {
					predicates.add(cb.like(cb.lower(keyPath), like(((String) entry.getValue()).toLowerCase())));
				}
			}
		}
		query.select(patient).where(cb.and(predicates.toArray(new Predicate[0])));

		return entityManager.createQuery(query).getResultList();
	}

}
