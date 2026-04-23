/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere
 */

package org.isf.maternity.service;

import java.util.List;
import java.util.Optional;

import org.isf.maternity.model.PregnancyVisitType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PregnancyVisitTypeIoOperation {

	private final PregnancyVisitTypeIoOperationRepository repository;

	public PregnancyVisitTypeIoOperation(PregnancyVisitTypeIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Get all {@link PregnancyVisitType} ordered by description.
	 *
	 * @return list of visit types sorted alphabetically
	 * @throws OHServiceException if retrieval fails
	 */
	public List<PregnancyVisitType> getVisitTypes() throws OHServiceException {
		return repository.findAllByOrderByDescriptionAsc();
	}

	/**
	 * Get a visit type by code (case-insensitive).
	 *
	 * @param code visit type code
	 * @return optional visit type
	 * @throws OHServiceException if retrieval fails
	 */
	public Optional<PregnancyVisitType> getByCode(String code) throws OHServiceException {
		return repository.findByCodeIgnoreCase(code);
	}

	/**
	 * Check if a visit type exists by code.
	 *
	 * @param code visit type code
	 * @return true if exists
	 * @throws OHServiceException if check fails
	 */
	public boolean existsByCode(String code) throws OHServiceException {
		return repository.existsByCodeIgnoreCase(code);
	}

	/**
	 * Create a new {@link PregnancyVisitType}.
	 *
	 * @param visitType entity to create
	 * @return saved entity
	 * @throws OHServiceException if creation fails
	 */
	public PregnancyVisitType newVisitType(PregnancyVisitType visitType) throws OHServiceException {
		return repository.save(visitType);
	}

	/**
	 * Update an existing {@link PregnancyVisitType}.
	 *
	 * @param visitType entity to update
	 * @return updated entity
	 * @throws OHServiceException if update fails
	 */
	public PregnancyVisitType updateVisitType(PregnancyVisitType visitType) throws OHServiceException {
		return repository.save(visitType);
	}

	/**
	 * Delete a {@link PregnancyVisitType}.
	 *
	 * @param visitType entity to delete
	 * @throws OHServiceException if deletion fails
	 */
	public void deleteVisitType(PregnancyVisitType visitType) throws OHServiceException {
		repository.delete(visitType);
	}
}