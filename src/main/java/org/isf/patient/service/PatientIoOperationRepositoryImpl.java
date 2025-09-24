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
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.isf.patient.model.Patient;
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

	private CriteriaQuery<Patient> buildSearchQuery(String regex) {
		String[] words = getWordsToSearchForInPatientsRepository(regex);
		return createQuerySearchingForPatientContainingGivenWordsInHisProperties(words);
	}

	private String[] getWordsToSearchForInPatientsRepository(String regex) {
		String[] words = new String[0];

		if (regex != null && !regex.isEmpty()) {
			String string = regex.trim().toLowerCase();
			words = string.split(" ");
		}

		return words;
	}

	private CriteriaQuery<Patient> createQuerySearchingForPatientContainingGivenWordsInHisProperties(String[] words) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Patient> query = cb.createQuery(Patient.class);
		Root<Patient> patientRoot = query.from(Patient.class);
		query.select(patientRoot);
		List<Predicate> where = new ArrayList<>();

		for (String word : words) {
			where.add(wordExistsInOneOfPatientFields(word, cb, patientRoot));
		}

		where.add(cb.or(
				cb.equal(patientRoot.get("deleted"), 'N'),
				cb.isNull(patientRoot.get("deleted"))
		));

		query.where(cb.and(where.toArray(new Predicate[0])));
		query.orderBy(cb.desc(patientRoot.get("code")));

		return query;
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
			String key = entry.getKey();
			Object value = entry.getValue();

			if (key.equals("birthDate")) {
				LocalDateTime birthDateFrom = (LocalDateTime) value;
				LocalDateTime birthDateTo = birthDateFrom.plusDays(1);
				predicates.add(cb.between(patient.get(key).as(LocalDateTime.class), birthDateFrom, birthDateTo));
			} else if (key.equals("age")) {
				if (value instanceof String) {
					try {
						Integer ageValue = Integer.parseInt((String) value);
						predicates.add(cb.equal(patient.get("age"), ageValue));
					} catch (NumberFormatException e) {
						predicates.add(cb.disjunction());
					}
				} else if (value instanceof Integer) {
					predicates.add(cb.equal(patient.get("age"), value));
				}
			} else {
				if (value instanceof String) {
					String stringValue = ((String) value).toLowerCase();

					if ("code".equals(key) || "age".equals(key)) {
						try {
							if ("code".equals(key)) {
								Integer intValue = Integer.parseInt((String) value);
								predicates.add(cb.equal(patient.get(key), intValue));
							} else {
								predicates.add(cb.like(patient.get(key).as(String.class), like(stringValue)));
							}
						} catch (NumberFormatException e) {
							predicates.add(cb.disjunction());
						}
					} else {
						predicates.add(cb.like(cb.lower(patient.get(key)), like(stringValue)));
					}
				} else {
					predicates.add(cb.equal(patient.get(key), value));
				}
			}
		}
		query.select(patient).where(cb.and(predicates.toArray(new Predicate[0])));

		return entityManager.createQuery(query).getResultList();
	}

}
