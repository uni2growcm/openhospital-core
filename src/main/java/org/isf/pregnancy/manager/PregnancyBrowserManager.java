package org.isf.pregnancy.manager;

import java.util.List;

import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.pregnancy.service.PregnancyIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PregnancyBrowserManager {

	private final AdmissionBrowserManager admissionBrowserManager;
	private final PregnancyIoOperations pregnancyIoOperations;
	
	public PregnancyBrowserManager(AdmissionBrowserManager admissionBrowserManager, PregnancyIoOperations pregnancyIoOperations) {
		this.admissionBrowserManager = admissionBrowserManager;
		this.pregnancyIoOperations = pregnancyIoOperations;
	}
	
	public List<Admission> getAdmissionByName(String name) throws OHServiceException {
		return admissionBrowserManager.getAdmissionsBySex('F', name);
	}

	public List<Admission> getPregnancyPatientBySexAndName(String name, String sex) {
		return pregnancyIoOperations.getPregnancyPatientBySexAndName(name, sex);
	}

	public Long CountTotalPregnancyPatient(String name, String sex) {
		return pregnancyIoOperations.getCountTotalPregnancyPatient(name, sex);
	}

	public List<Admission> getPregnancyPatientBySexAndName(String name, String sex, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return pregnancyIoOperations.getPregnancyPatientBySexAndName(name, sex, pageable);
	}

	public List<Admission> getPregnancyPatientBySex(String sex) {
		return pregnancyIoOperations.getPregnancyPatientBySex(sex);
	}
}
