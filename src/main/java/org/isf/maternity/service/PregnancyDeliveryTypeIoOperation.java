/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere
 */

package org.isf.maternity.service;

import java.util.List;
import java.util.Optional;

import org.isf.generaldata.MessageBundle;
import org.isf.maternity.model.PregnancyDeliveryType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PregnancyDeliveryTypeIoOperation {

	private final PregnancyDeliveryTypeIoOperationRepository repository;

	public PregnancyDeliveryTypeIoOperation(PregnancyDeliveryTypeIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Get all {@link PregnancyDeliveryType} ordered by description.
	 * Used to populate dropdowns or reference lists in UI.
	 *
	 * @return list of delivery types sorted alphabetically
	 * @throws OHServiceException if retrieval fails
	 */
	public List<PregnancyDeliveryType> getDeliveryTypes() throws OHServiceException {
		return repository.findAllByOrderByDescriptionAsc();
	}

	/**
	 * Get a delivery type by its unique code (case-insensitive).
	 *
	 * @param code the delivery type code
	 * @return optional delivery type if found
	 * @throws OHServiceException if lookup fails
	 */
	public Optional<PregnancyDeliveryType> getByCode(String code) throws OHServiceException {
		return repository.findByCodeIgnoreCase(code);
	}

	/**
	 * Check if a delivery type exists by code.
	 *
	 * @param code the delivery type code
	 * @return true if exists, false otherwise
	 * @throws OHServiceException if check fails
	 */
	public boolean existsByCode(String code) throws OHServiceException {
		return repository.existsByCodeIgnoreCase(code);
	}

	/**
	 * Create a new {@link PregnancyDeliveryType}.
	 *
	 * @param type the delivery type to create
	 * @return saved delivery type
	 * @throws OHServiceException if creation fails
	 */
	public PregnancyDeliveryType newDeliveryType(PregnancyDeliveryType type) throws OHServiceException {
		validateCodeUniqueness(type.getCode());
		return repository.save(type);
	}

	/**
	 * Update an existing {@link PregnancyDeliveryType}.
	 *
	 * @param type the delivery type to update
	 * @return updated entity
	 * @throws OHServiceException if update fails
	 */
	public PregnancyDeliveryType updateDeliveryType(PregnancyDeliveryType type) throws OHServiceException {
		return repository.save(type);
	}

	/**
	 * Delete a {@link PregnancyDeliveryType}.
	 *
	 * @param type the entity to delete
	 * @throws OHServiceException if deletion fails
	 */
	public void deleteDeliveryType(PregnancyDeliveryType type) throws OHServiceException {
		repository.delete(type);
	}

	/**
	 * Ensure delivery type code is unique before creation.
	 *
	 * @param code delivery type code
	 * @throws OHServiceException if code already exists
	 */
	private void validateCodeUniqueness(String code) throws OHServiceException {
		if (code != null && repository.existsByCodeIgnoreCase(code)) {
			throw new OHDataIntegrityViolationException(
				new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}
	}
}