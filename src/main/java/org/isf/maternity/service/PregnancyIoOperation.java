package org.isf.maternity.service;

import org.isf.maternity.model.Pregnancy;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PregnancyIoOperation {
	private final PregnancyIoOperationRepository repository;

	public PregnancyIoOperation(PregnancyIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Get all pregnancies for a given patient.
	 *
	 * @param patientCode patient identifier
	 * @return list of pregnancies
	 * @throws OHServiceException if retrieval fails
	 */
	public List<Pregnancy> getPregnanciesByPatient(Integer patientCode) throws OHServiceException {
		return repository.findByPatientCode(patientCode);
	}

	/**
	 * Get all active pregnancies (ongoing cases).
	 *
	 * @return list of ongoing pregnancies
	 * @throws OHServiceException if retrieval fails
	 */
	public List<Pregnancy> getActivePregnancies() throws OHServiceException {
		return repository.findByStatus("Ongoing");
	}

	/**
	 * Get pregnancies by status.
	 *
	 * @param status pregnancy status (Ongoing, Completed, Terminated)
	 * @return list of pregnancies
	 * @throws OHServiceException if retrieval fails
	 */
	public List<Pregnancy> getPregnanciesByStatus(String status) throws OHServiceException {
		return repository.findByStatus(status);
	}

	/**
	 * Get pregnancies by risk level.
	 *
	 * @param riskLevel Low, Medium, High
	 * @return list of pregnancies
	 * @throws OHServiceException if retrieval fails
	 */
	public List<Pregnancy> getPregnanciesByRiskLevel(String riskLevel) throws OHServiceException {
		return repository.findByRiskLevel(riskLevel);
	}

	/**
	 * Get pregnancies within a LMP date range.
	 *
	 * @param dateFrom start date
	 * @param dateTo end date
	 * @return list of pregnancies
	 * @throws OHServiceException if retrieval fails
	 */
	public List<Pregnancy> getPregnanciesByLmpRange(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return repository.findByLmpBetween(dateFrom, dateTo);
	}

	/**
	 * Create a new pregnancy record.
	 *
	 * @param pregnancy entity to save
	 * @return saved pregnancy
	 * @throws OHServiceException if saving fails
	 */
	public Pregnancy newPregnancy(Pregnancy pregnancy) throws OHServiceException {
		return repository.save(pregnancy);
	}

	/**
	 * Update an existing pregnancy.
	 *
	 * @param pregnancy entity to update
	 * @return updated pregnancy
	 * @throws OHServiceException if update fails
	 */
	public Pregnancy updatePregnancy(Pregnancy pregnancy) throws OHServiceException {
		return repository.save(pregnancy);
	}

	/**
	 * Delete a pregnancy record.
	 *
	 * @param pregnancy entity to delete
	 * @throws OHServiceException if deletion fails
	 */
	public void deletePregnancy(Pregnancy pregnancy) throws OHServiceException {
		repository.delete(pregnancy);
	}

	/**
	 * Check if a patient already has an active pregnancy.
	 *
	 * @param patientId patient identifier
	 * @return true if active pregnancy exists
	 * @throws OHServiceException if check fails
	 */
	public boolean hasActivePregnancy(Integer patientId) throws OHServiceException {
		return repository.existsByPatientIdAndStatus(patientId, "Ongoing");
	}

	/**
	 * Close a pregnancy (mark as Completed or Terminated).
	 *
	 * @param pregnancyId pregnancy identifier
	 * @param status new status
	 * @return updated pregnancy
	 * @throws OHServiceException if update fails or pregnancy not found
	 */
	public Pregnancy closePregnancy(Integer pregnancyId, String status) throws OHServiceException {
		Pregnancy pregnancy = repository.findById(pregnancyId)
			.orElseThrow(() -> new OHServiceException(new OHExceptionMessage("Pregnancy not found: " + pregnancyId)));

		pregnancy.setStatus(status);

		return repository.save(pregnancy);
	}
}
