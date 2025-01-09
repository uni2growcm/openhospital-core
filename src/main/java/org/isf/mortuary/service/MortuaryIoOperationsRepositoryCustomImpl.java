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

package org.isf.mortuary.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.model.Mortuary;
import org.isf.patient.model.Patient;
import org.springframework.data.domain.Pageable;

public class MortuaryIoOperationsRepositoryCustomImpl implements MortuaryIoOperationsRepositoryCustom {

	private final String PROVENANCE = "provenance";
	private final String PATIENT = "patient";
	private final String DEATHREASON = "deathReason";
	private final String NAME = "name";
	private final String DESCRIPTION = "description";
	private final String DATETO = "releaseDate";
	private final String DATEFROM = "enteredDate";

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Retrieves all the {@link Mortuary}s with the specified criteria.<br>
	 * <br>
	 * @param patientName the patient name.
	 * @param provenance the provenance.
	 * @param dateFrom the lower bound for the mortuary date range.
	 * @param dateTo the upper bound for the mortuary date range.
	 * @param deathReason the reason of death.
	 * @param inputOrOutput the value that determines the date to be set in the interval.
	 * @return the retrieved mortuaries.
	 */
	@Override
	public List<Mortuary> findAllWhereData(
		String patientName,
		String provenance,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		String deathReason,
		String inputOrOutput
	) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Mortuary> query = builder.createQuery(Mortuary.class);
		Root<Mortuary> root = query.from(Mortuary.class);

		List<Predicate> predicates = new ArrayList<>();
		Join<Mortuary, Patient> patientJoin = root.join(PATIENT, JoinType.LEFT);
		Join<Mortuary, DeathReason> deathReasonJoin = root.join(DEATHREASON, JoinType.LEFT);

		if (patientName != null) {
			predicates.add(builder.like(patientJoin.get(NAME), "%" + patientName + "%"));
		}
		if (provenance != null) {
			predicates.add(builder.equal(root.<String> get(PROVENANCE), provenance));
		}
		if (deathReason != null) {
			predicates.add(builder.equal(deathReasonJoin.<String> get(DESCRIPTION), deathReason));
		}
		if (dateFrom != null && dateTo != null) {
			if (inputOrOutput.equals("I")) {
				predicates.add(builder.between(root.get(DATEFROM), dateFrom, dateTo));
			} else {
				predicates.add(builder.between(root.get(DATETO), dateFrom, dateTo));
			}
		}
		query.where(predicates.toArray(new Predicate[] {}));

		return entityManager.createQuery(query).getResultList();
	}

	/**
	 * Retrieves a page of {@link Mortuary}s with the specified criteria.<br>
	 * <br>
	 * @param patientName the patient name.
	 * @param provenance the provenance.
	 * @param dateFrom the lower bound for the mortuary date range.
	 * @param dateTo the upper bound for the mortuary date range.
	 * @param deathReason the reason of death.
	 * @param inputOrOutput the value that determines the date to be set in the interval.
	 * @param pageable for pagination/.
	 * @return the retrieved mortuaries.
	 */
	@Override
	public List<Mortuary> findAllWhereDataPageable(
		String patientName,
		String provenance,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		String deathReason,
		String inputOrOutput,
		Pageable pageable
	) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Mortuary> query = builder.createQuery(Mortuary.class);
		Root<Mortuary> root = query.from(Mortuary.class);

		List<Predicate> predicates = new ArrayList<>();
		Join<Mortuary, Patient> patientJoin = root.join(PATIENT, JoinType.LEFT);
		Join<Mortuary, DeathReason> deathReasonJoin = root.join(DEATHREASON, JoinType.LEFT);

		if (patientName != null) {
			predicates.add(builder.like(patientJoin.get(NAME), "%" + patientName + "%"));
		}
		if (provenance != null) {
			predicates.add(builder.equal(root.<String> get(PROVENANCE), provenance));
		}
		if (deathReason != null) {
			predicates.add(builder.equal(deathReasonJoin.<String> get(DESCRIPTION), deathReason));
		}
		if (dateFrom != null && dateTo != null) {
			if (inputOrOutput.equals("I")) {
				predicates.add(builder.between(root.get(DATEFROM), dateFrom, dateTo));
			} else {
				predicates.add(builder.between(root.get(DATETO), dateFrom, dateTo));
			}
		}

		query.select(root).where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Mortuary> typedQuery = entityManager.createQuery(query);

		int firstResult = pageable.getPageNumber() * pageable.getPageSize();
		typedQuery.setFirstResult(firstResult);
		typedQuery.setMaxResults(pageable.getPageSize());

		return typedQuery.getResultList();
	}

	/**
	 * Count all the {@link Mortuary}s with the specified criteria.<br>
	 * <br>
	 * @param patientName the patient name.
	 * @param provenance the provenance.
	 * @param dateFrom the lower bound for the mortuary date range.
	 * @param dateTo the upper bound for the mortuary date range.
	 * @param deathReason the reason of death.
	 * @param inputOrOutput the value that determines the date to be set in the interval.
	 * @return the number of mortuary.
	 */
	@Override
	public long getCountTotalMortuaries(
		String patientName,
		String provenance,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		String deathReason,
		String inputOrOutput
	) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<Mortuary> root = countQuery.from(Mortuary.class);

		List<Predicate> predicates = new ArrayList<>();
		Join<Mortuary, Patient> patientJoin = root.join(PATIENT, JoinType.LEFT);
		Join<Mortuary, DeathReason> deathReasonJoin = root.join(DEATHREASON, JoinType.LEFT);

		if (patientName != null) {
			predicates.add(builder.like(patientJoin.get(NAME), "%" + patientName + "%"));
		}
		if (provenance != null) {
			predicates.add(builder.equal(root.<String> get(PROVENANCE), provenance));
		}
		if (deathReason != null) {
			predicates.add(builder.equal(deathReasonJoin.<String> get(DESCRIPTION), deathReason));
		}
		if (dateFrom != null && dateTo != null) {
			if (inputOrOutput.equals("I")) {
				predicates.add(builder.between(root.get(DATEFROM), dateFrom, dateTo));
			} else {
				predicates.add(builder.between(root.get(DATETO), dateFrom, dateTo));
			}
		}

		countQuery.select(builder.count(root)).where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(countQuery).getSingleResult();
	}
}
