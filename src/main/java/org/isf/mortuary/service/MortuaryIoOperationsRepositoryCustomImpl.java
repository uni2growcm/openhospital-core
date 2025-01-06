package org.isf.mortuary.service;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.isf.medicalstockward.model.MovementWard;
import org.isf.mortuary.model.Mortuary;


public class MortuaryIoOperationsRepositoryCustomImpl implements MortuaryIoOperationsRepositoryCustom{

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Mortuary> findAllWithData() {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Mortuary> query = builder.createQuery(Mortuary.class);
		Root<MovementWard> root = query.from(MovementWard.class);
		return entityManager.createQuery(query).getResultList();
	}
}
