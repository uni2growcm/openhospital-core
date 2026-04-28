/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.typology.service;

import org.isf.typology.model.Family;
import org.isf.typology.model.Typology;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class TypologyIoOperation {
	private final TypologyIoOperationRepository repository;

	public TypologyIoOperation(TypologyIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Get all {@link Typology} ordered by description.
	 *
	 * @return list of Typologies sorted alphabetically in pages
	 * @throws OHServiceException if retrieval fails
	 */
	public Page<Typology> getTypologies(Pageable pageable) throws OHServiceException {
		return repository.findAllByOrderByDescriptionAsc(pageable);
	}

	/**
	 * Get all {@link Typology} for a given family ordered by description.
	 *
	 * @param family the family of the {@link Typology}s to be fetched
	 * @return list of Typologies sorted alphabetically
	 * @throws OHServiceException if retrieval fails
	 */
	public List<Typology> getTypologies(Family family) throws OHServiceException {
		return repository.findAllByFamilyOrderByDescriptionAsc(family);
	}

	/**
	 * Get a Typology by code (case-insensitive).
	 *
	 * @param code Typology code
	 * @return optional Typology
	 * @throws OHServiceException if retrieval fails
	 */
	public Optional<Typology> getByCode(String code) throws OHServiceException {
		return repository.findByCodeIgnoreCase(code);
	}

	/**
	 * Check if a Typology exists by code.
	 *
	 * @param code Typology code
	 * @return true if exists
	 * @throws OHServiceException if check fails
	 */
	public boolean existsByCode(String code) throws OHServiceException {
		return repository.existsByCodeIgnoreCase(code);
	}

	/**
	 * Create a new {@link Typology}.
	 *
	 * @param typology entity to create
	 * @return saved entity
	 * @throws OHServiceException if creation fails
	 */
	public Typology newTypology(Typology typology) throws OHServiceException {
		return repository.save(typology);
	}

	/**
	 * Update an existing {@link Typology}.
	 *
	 * @param typology entity to update
	 * @return updated entity
	 * @throws OHServiceException if update fails
	 */
	public Typology updateTypology(Typology typology) throws OHServiceException {
		return repository.save(typology);
	}

	/**
	 * Delete a {@link Typology}.
	 *
	 * @param typology entity to delete
	 * @throws OHServiceException if deletion fails
	 */
	public void deleteTypology(Typology typology) throws OHServiceException {
		repository.delete(typology);
	}
}
