package org.isf.mortuary.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.model.Mortuary;
import org.isf.patient.model.Patient;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;

public class MortuaryIoOperationsRepositoryCustomImpl implements MortuaryIoOperationsRepositoryCustom{

	@PersistenceContext
	private EntityManager entityManager;

	private final String PROVENANCE = "provenance";
	private final String PATIENT = "patient";
	private final String DEATHREASON = "deathReason";
	private final String NAME = "name";
	private final String DESCRIPTION = "description";
	private final String DATETO = "releaseDate";
	private final String DATEFROM = "enteredDate";

	@Override
	public List<Mortuary> findAllWhereData(String patientName, String provenance, LocalDateTime dateFrom, LocalDateTime dateTo,String deathReason) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Mortuary> query = builder.createQuery(Mortuary.class);
		Root<Mortuary> root = query.from(Mortuary.class);

		List<Predicate> predicates = new ArrayList<>();
		Join<Mortuary, Patient> patientJoin = root.join(PATIENT, JoinType.LEFT);
		Join<Mortuary, DeathReason> deathReasonJoin = root.join(DEATHREASON, JoinType.LEFT);

		if(patientName != null) {
			predicates.add(builder.like(patientJoin.<String>get(NAME), "%" + patientName + "%"));
		}
		if(provenance != null) {
			predicates.add(builder.equal(root.<String>get(PROVENANCE), provenance));
		}
		if(deathReason != null){
			predicates.add(builder.equal(deathReasonJoin.<String>get(DESCRIPTION), deathReason));
		}
		if (dateFrom != null && dateTo != null) {
			predicates.add(
				builder.between(root.get(DATEFROM), dateFrom, dateTo)
			);
		}
		if (dateFrom != null && dateTo != null) {
			predicates.add(
				builder.between(root.get(DATETO), dateFrom, dateTo)
			);
		}

		query.where(predicates.toArray(new Predicate[] {}));

		return entityManager.createQuery(query).getResultList();
	}
}
