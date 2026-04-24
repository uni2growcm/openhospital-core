/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere
 */

package org.isf.maternity.service;

import java.util.List;
import java.util.Optional;

import org.isf.generaldata.MessageBundle;
import org.isf.maternity.model.Newborn;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class NewbornIoOperation {

	private final NewbornIoOperationRepository repository;

	public NewbornIoOperation(NewbornIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Create a new {@link Newborn} record.
	 *
	 * @param newborn the newborn entity to persist
	 * @return the saved newborn entity
	 * @throws OHServiceException if an error occurs during saving
	 */
	public Newborn newNewborn(Newborn newborn) throws OHServiceException {
		return repository.save(newborn);
	}

	/**
	 * Update an existing {@link Newborn} record.
	 *
	 * @param newborn the newborn entity to update
	 * @return the updated newborn entity
	 * @throws OHServiceException if an error occurs during updating
	 */
	public Newborn updateNewborn(Newborn newborn) throws OHServiceException {
		return repository.save(newborn);
	}

	/**
	 * Delete a {@link Newborn} record.
	 *
	 * @param newborn the newborn entity to delete
	 * @throws OHServiceException if an error occurs during deletion
	 */
	public void deleteNewborn(Newborn newborn) throws OHServiceException {
		repository.delete(newborn);
	}

	/**
	 * Get all newborns linked to a delivery.
	 *
	 * @param deliveryId the delivery identifier
	 * @return list of newborns for the given delivery
	 * @throws OHServiceException if an error occurs during retrieval
	 */
	public List<Newborn> getNewbornsByDelivery(Integer deliveryId) throws OHServiceException {
		return repository.findByDeliveryId(deliveryId);
	}

	/**
	 * Count newborns for a specific delivery.
	 *
	 * @param deliveryId the delivery identifier
	 * @return number of newborns linked to the delivery
	 * @throws OHServiceException if an error occurs during counting
	 */
	public long countNewbornsByDelivery(Integer deliveryId) throws OHServiceException {
		return repository.countByDeliveryId(deliveryId);
	}

	/**
	 * Get the firstborn child in a delivery (by birth time).
	 *
	 * @param deliveryId the delivery identifier
	 * @return optional containing the earliest newborn if exists
	 * @throws OHServiceException if an error occurs during retrieval
	 */
	public Optional<Newborn> getFirstBorn(Integer deliveryId) throws OHServiceException {
		return repository.findTopByDeliveryIdOrderByBirthDateAsc(deliveryId);
	}

	/**
	 * Get the Newborn that corresponds to a patient's record.
	 *
	 * @param patientCode the newborn patient identifier
	 * @return optional containing the newborn if exists
	 * @throws OHServiceException if an error occurs during retrieval
	 */
	public Optional<Newborn> findByPatientCode(Integer patientCode) throws OHServiceException {
		return repository.findByBabyPatient_Code(patientCode);
	}

	/**
	 * Get newborns filtered by birth weight range.
	 * Useful for detecting low birth weight or macrosomia cases.
	 *
	 * @param min minimum weight (kg or grams depending on system standard)
	 * @param max maximum weight
	 * @return list of newborns within weight range
	 * @throws OHServiceException if an error occurs during retrieval
	 */
	public List<Newborn> getNewbornsByWeightRange(Double min, Double max) throws OHServiceException {
		return repository.findByBirthWeightBetween(min, max);
	}

	/**
	 * Check if a delivery contains newborns with birth weight below a threshold.
	 * Useful for medical alerts (low birth weight risk detection).
	 *
	 * @param deliveryId the delivery identifier
	 * @param weight threshold weight
	 * @return true if at least one newborn is below threshold
	 * @throws OHServiceException if an error occurs during check
	 */
	public boolean hasLowBirthWeightCases(Integer deliveryId, Double weight) throws OHServiceException {
		return repository.existsByDeliveryIdAndBirthWeightLessThan(deliveryId, weight);
	}

	/**
	 * Validate that a newborn belongs to a specific delivery before update/delete.
	 *
	 * @param newbornId newborn identifier
	 * @param deliveryId delivery identifier
	 * @return validated newborn entity
	 * @throws OHServiceException if newborn does not exist or mismatch occurs
	 */
	public Newborn validateNewbornBelongsToDelivery(Integer newbornId, Integer deliveryId)
		throws OHServiceException {

		Newborn newborn = repository.findById(newbornId)
			.orElseThrow(() ->
				new OHServiceException(
					new OHExceptionMessage(
						MessageBundle.getMessage("angal.maternity.newBornnotfound.msg") + ": " + newbornId
					)
				)
			);

		if (!newborn.getDelivery().getId().equals(deliveryId)) {
			throw new OHServiceException(
				new OHExceptionMessage(
					MessageBundle.getMessage("angal.maternity.newborndonotbelongtodelivery.msg") + ": " + deliveryId
				)
			);
		}

		return newborn;
	}
}