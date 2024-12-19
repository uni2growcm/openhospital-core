package org.isf.pregnancy.manager;

import java.util.List;

import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.pregnancy.service.PregnancyIoOperations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PregnancyBrowserManager {

	private final PregnancyIoOperations pregnancyIoOperations;
	
	public PregnancyBrowserManager(AdmissionBrowserManager admissionBrowserManager, PregnancyIoOperations pregnancyIoOperations) {
		this.pregnancyIoOperations = pregnancyIoOperations;
	}

	/**
	 * Get patients filtered by sex.
	 *
	 * @param sex The sex to consider.
	 * @return {@link List} of {@link Admission}s matching the given sex, or empty list of no match.
	 */
	public List<Admission> getPregnancyPatientBySex(Character sex) {
		return pregnancyIoOperations.getPregnancyPatientBySex(sex);
	}
	
	/**
	 * Get patients filtered by name and sex.
	 *
     * @param name The name to consider.
	 * @param sex The sex to consider.
	 * @return {@link List} of {@link Admission}s matching the given name and sex, or empty list of no match.
	 */
	public List<Admission> getPregnancyPatientBySexAndName(String name, Character sex) {
		return pregnancyIoOperations.getPregnancyPatientBySexAndName(name, sex);
	}
	
	/**
	 * count pregnancyPatient filtered by name and sex.
	 *
	 * @param name The name to consider.
	 * @param sex The sex to consider.
	 * @return {@link Long} of {@link Admission}s matching the given name and sex, or empty list of no match.
	 */
	public Long CountTotalPregnancyPatient(String name, Character sex) {
		return pregnancyIoOperations.getCountTotalPregnancyPatient(name, sex);
	}

	/**
	 * Get patients filtered by name and sex.
	 *
	 * @param name The name to consider.
	 * @param sex The sex to consider.
	 * @param page The page to consider.
	 * @param size The size to consider.
	 * @return {@link List} of {@link Admission}s matching the given name, sex, page and size, or empty list of no match.
	 */
	public List<Admission> getPregnancyPatientBySexAndName(String name, Character sex, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return pregnancyIoOperations.getPregnancyPatientBySexAndName(name, sex, pageable);
	}
}
