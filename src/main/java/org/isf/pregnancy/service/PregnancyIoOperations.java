package org.isf.pregnancy.service;
import java.util.List;
import jakarta.transaction.Transactional;

import org.isf.admission.model.Admission;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@TranslateOHServiceException
public class PregnancyIoOperations {

	private PregnancyIoOperationRepository pregnancyIoOperationRepository;

	public PregnancyIoOperations(PregnancyIoOperationRepository pregnancyIoOperationRepository) throws OHServiceException {
		this.pregnancyIoOperationRepository = pregnancyIoOperationRepository;
	}

	/**
	 * Get patients filtered by sex.
	 *
	 * @param sex The sex to consider.
	 * @return {@link List} of {@link Admission}s matching the given sex, or empty list of no match.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public List<Admission> getPregnancyPatientBySex(Character sex) throws OHServiceException {
		return pregnancyIoOperationRepository.findPregnancyPatientBySex(sex);
	}
	
	/**
	 * Get patients filtered by name and sex.
	 *
	 * @param name The name to consider.
	 * @param sex The sex to consider.
	 * @return {@link List} of {@link Admission}s matching the given name and sex, or empty list of no match.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public List<Admission> getPregnancyPatientBySexAndName(String name, Character sex) throws OHServiceException {
		return pregnancyIoOperationRepository.findPregnancyPatientBySexAndName(name, sex);
	}

	/**
	 * count pregnancyPatient filtered by name and sex.
	 *
	 * @param name The name to consider.
	 * @param sex The sex to consider. 
	 * @return {@link Long} of {@link Admission}s matching the given name and sex, or empty list of no match.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public Long getCountTotalPregnancyPatient(String name, Character sex) throws OHServiceException {
		return pregnancyIoOperationRepository.getCountTotalPregnancyPatient(name, sex);
	}

	/**
	 * Get patients filtered by name and sex.
	 *
	 * @param sex The sex to consider.
	 * @param pageable The pageable to consider.
	 * @return {@link Page} of {@link Admission}s matching the given name, sex and pageable Object, or empty page of no match.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public List<Admission> getPregnancyPatientBySexAndName(String name, Character sex, Pageable pageable) throws OHServiceException{
		return pregnancyIoOperationRepository.findPregnancyPatientBySexAndName(name, sex, pageable);
	}
}
