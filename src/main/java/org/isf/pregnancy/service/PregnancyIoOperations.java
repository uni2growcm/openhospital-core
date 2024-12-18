package org.isf.pregnancy.service;

import jakarta.transaction.Transactional;
import org.isf.admission.model.Admission;
import org.isf.utils.db.TranslateOHServiceException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@TranslateOHServiceException
public class PregnancyIoOperations {

	private PregnancyIoOperationRepository pregnancyIoOperationRepository;

	public PregnancyIoOperations(PregnancyIoOperationRepository pregnancyIoOperationRepository) {
		this.pregnancyIoOperationRepository = pregnancyIoOperationRepository;
	}

	public List<Admission> getPregnancyPatientBySexAndName(String name, String sex) {
		return pregnancyIoOperationRepository.findPregnancyPatientBySexAndName(name, sex);
	}

	public Long getCountTotalPregnancyPatient(String name, String sex) {
		return pregnancyIoOperationRepository.getCountTotalPregnancyPatient(name, sex);
	}

	public List<Admission> getPregnancyPatientBySexAndName(String name, String sex, Pageable pageable) {
		return pregnancyIoOperationRepository.findPregnancyPatientBySexAndName(name, sex, pageable);
	}

	public List<Admission> getPregnancyPatientBySex(String sex) {
		return pregnancyIoOperationRepository.findPregnancyPatientBySex(sex);
	}
}
