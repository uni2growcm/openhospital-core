package org.isf.pregnancy.service;

import org.isf.admission.model.Admission;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PregnancyIoOperationRepositoryCustom {
	List<Admission> findPregnancyPatientBySexAndName(String name, Character sex);

	List<Admission> findPregnancyPatientBySexAndName(String name, Character sex, Pageable pageable);

	List<Admission> findPregnancyPatientBySex(Character sex);

	Long getCountTotalPregnancyPatient(String name, Character sex);
}
