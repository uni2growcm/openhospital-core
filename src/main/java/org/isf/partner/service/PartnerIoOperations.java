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
package org.isf.partner.service;

import org.isf.partner.model.Partner;
import org.isf.typology.model.Typology;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PartnerIoOperations {

	private final PartnerIoOperationRepository repository;

	public PartnerIoOperations(PartnerIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Method that returns all active {@link Partner}s ordered by name.
	 *
	 * @return the list of active {@link Partner}s
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public List<Partner> getAll() throws OHServiceException {
		return repository.findByActiveTrueOrderByNameAsc();
	}

	/**
	 * Method that gets an active {@link Partner} by its identifier.
	 *
	 * @param id the partner identifier
	 * @return an {@link Optional} containing the matching {@link Partner},
	 *         or an empty {@link Optional} if not found or inactive
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public Optional<Partner> getById(int id) throws OHServiceException {
		return repository.findById(id)
			.filter(Partner::isActive);
	}

	/**
	 * Method that returns all active {@link Partner}s associated with the
	 * specified {@link Typology}.
	 *
	 * @param type the partner type
	 * @return the list of matching active {@link Partner}s
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public List<Partner> getByType(Typology type) throws OHServiceException {
		return repository.findByTypeAndActiveTrue(type);
	}

	/**
	 * Method that returns all active {@link Partner}s associated with the
	 * specified typology code.
	 *
	 * @param typeCode the typology code
	 * @return the list of matching active {@link Partner}s
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public List<Partner> getByTypeCode(String typeCode) throws OHServiceException {
		return repository.findByType_CodeAndActiveTrue(typeCode);
	}

	/**
	 * Method that searches active {@link Partner}s matching the specified keyword.
	 *
	 * @param keyword the search keyword
	 * @return the list of matching active {@link Partner}s
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public List<Partner> search(String keyword) throws OHServiceException {
		return repository.searchActive(keyword);
	}

	/**
	 * Method that saves or updates a {@link Partner}.
	 *
	 * @param partner the {@link Partner} to save
	 * @return the saved {@link Partner}
	 * @throws OHServiceException if an error occurs while saving data
	 */
	public Partner save(Partner partner) throws OHServiceException {
		return repository.save(partner);
	}

	/**
	 * Method that logically deletes a {@link Partner}.
	 * <p>
	 * The partner is marked as inactive and remains stored in the database.
	 *
	 * @param id the identifier of the partner to delete
	 * @throws OHServiceException if an error occurs while updating data
	 */
	@Transactional
	public void softDelete(int id) throws OHServiceException {
		repository.softDelete(id);
	}
}