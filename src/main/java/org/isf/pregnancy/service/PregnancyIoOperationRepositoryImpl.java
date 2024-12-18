package org.isf.pregnancy.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.isf.admission.model.Admission;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Transactional
public class PregnancyIoOperationRepositoryImpl implements PregnancyIoOperationRepositoryCustom {

	private static final String NAME = "name";
	private static final String PATIENT = "patient";
	private static final String SEX = "sex";

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Admission> findPregnancyPatientBySexAndName(String name, String sex) {
		return getPregnancyPatientBySexAndName(name, sex);
	}

	@Override
	public List<Admission> findPregnancyPatientBySexAndName(String name, String sex, Pageable pageable) {
		return getPregnancyPatientBySexAndName(name, sex, pageable);
	}

	@Override
	public List<Admission> findPregnancyPatientBySex(String sex) {
		return getPregnancyPatientBySex(sex);
	}

	private List<Admission> getPregnancyPatientBySex(String sex) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Admission> query = builder.createQuery(Admission.class);
		Root<Admission> root = query.from(Admission.class);

		Join<Object, Object> patientJoin = root.join(PATIENT);

		List<Predicate> predicates = new ArrayList<>();

		if (sex != null) {
			predicates.add(builder.equal(patientJoin.get(SEX), sex));
		}
		predicates.add(builder.equal(root.<Admission> get("admitted"), 1));
		predicates.add(builder.equal(root.<Admission> get("deleted"), 'N'));

		List<Order> orderList = new ArrayList<>();
		orderList.add(builder.asc(patientJoin.get(SEX)));

		query.select(root)
			.where(predicates.toArray(new Predicate[0]))
			.orderBy(orderList);

		return entityManager.createQuery(query).getResultList();
	}

	private List<Admission> getPregnancyPatientBySexAndName(String name, String sex) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Admission> query = builder.createQuery(Admission.class);
		Root<Admission> root = query.from(Admission.class);

		Join<Object, Object> patientJoin = root.join(PATIENT);

		List<Predicate> predicates = new ArrayList<>();

		if (sex != null) {
			predicates.add(builder.equal(patientJoin.get(SEX), sex));
		}
		predicates.add(builder.equal(root.<Admission> get("admitted"), 1));
		predicates.add(builder.equal(root.<Admission> get("deleted"), 'N'));
		if (name != null) {
			predicates.add(builder.like(patientJoin.get(NAME), "%" + name + "%"));
		}

		List<Order> orderList = new ArrayList<>();
		orderList.add(builder.asc(patientJoin.get(NAME)));

		query.select(root)
			.where(predicates.toArray(new Predicate[0]))
			.orderBy(orderList);

		return entityManager.createQuery(query).getResultList();
	}

	private List<Admission> getPregnancyPatientBySexAndName(String name, String sex, Pageable pageable) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Admission> query = builder.createQuery(Admission.class);
		Root<Admission> root = query.from(Admission.class);

		Join<Object, Object> patientJoin = root.join(PATIENT);

		List<Predicate> predicates = new ArrayList<>();

		if (sex != null) {
			predicates.add(builder.equal(patientJoin.get(SEX), sex));
		}
		predicates.add(builder.equal(root.<Admission> get("admitted"), 1));
		predicates.add(builder.equal(root.<Admission> get("deleted"), 'N'));
		if (name != null) {
			predicates.add(builder.like(patientJoin.get(NAME), "%" + name + "%"));
		}


		List<Order> orderList = new ArrayList<>();
		orderList.add(builder.asc(patientJoin.get(NAME)));

		query.select(root)
			.where(predicates.toArray(new Predicate[0]))
			.orderBy(orderList);

		TypedQuery<Admission> typedQuery = entityManager.createQuery(query);

		int firstResult = pageable.getPageNumber() * pageable.getPageSize();
		typedQuery.setFirstResult(firstResult);
		typedQuery.setMaxResults(pageable.getPageSize());

		return typedQuery.getResultList();
	}

	@Override
	public Long getCountTotalPregnancyPatient(String name, String sex) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<Admission> root = countQuery.from(Admission.class);

		Join<Object, Object> patientJoin = root.join(PATIENT);

		List<Predicate> predicates = new ArrayList<>();

		if (sex != null) {
			predicates.add(builder.equal(patientJoin.get(SEX), sex));
		}
		predicates.add(builder.equal(root.<Admission> get("admitted"), 1));
		predicates.add(builder.equal(root.<Admission> get("deleted"), 'N'));
		if (name != null) {
			predicates.add(builder.like(patientJoin.get(NAME), "%" + name + "%"));
		}

		List<Order> orderList = new ArrayList<>();
		orderList.add(builder.asc(patientJoin.get(NAME)));

		countQuery.select(builder.count(root)).where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(countQuery).getSingleResult();
	}
}
