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
package org.isf.country.service;

import org.isf.country.model.Country;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CountryIoOperations {

	private CountryIoOperationRepository repository;

	public CountryIoOperations(CountryIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Returns all active {@link Country}s ordered by name ascending.
	 *
	 * @return the list of active {@link Country}s.
	 * @throws OHServiceException when fails to fetch the countries.
	 */
	public List<Country> getAllCountries() throws OHServiceException {
		return repository.findAllByActiveOrderByNameAsc(1);
	}

	/**
	 * Returns the active {@link Country} with the given name, if it exists.
	 *
	 * @param name the name of the country to retrieve.
	 * @return an {@link Optional} containing the {@link Country} if found and active, or empty otherwise.
	 * @throws OHServiceException when fails to fetch the country.
	 */
	public Optional<Country> getCountryByName(String name) throws OHServiceException {
		return repository.findByNameAndActive(name, 1);
	}

	/**
	 * Updates an existing {@link Country}.
	 *
	 * @param country the country with updated fields.
	 * @return the updated {@link Country}.
	 * @throws OHServiceException when fails to update the country.
	 */
	public Country updateCountry(Country country) throws OHServiceException {
		return repository.save(country);
	}

	/**
	 * Returns the active {@link Country} with the given id, if it exists.
	 *
	 * @param id the id of the country to retrieve.
	 * @return an {@link Optional} containing the {@link Country} if found and active, or empty otherwise.
	 * @throws OHServiceException when fails to fetch the country.
	 */
	public Optional<Country> getCountryById(int id) throws OHServiceException {
		return repository.findByIdAndActive(id, 1);
	}

	/**
	 * Returns the active {@link Country} with the given ISO code, if it exists.
	 *
	 * @param isoCode the ISO code (2 characters) of the country to retrieve.
	 * @return an {@link Optional} containing the {@link Country} if found and active, or empty otherwise.
	 * @throws OHServiceException when fails to fetch the country.
	 */
	public Optional<Country> getCountryByIsoCode(String isoCode) throws OHServiceException {
		return repository.findByIsoCodeAndActive(isoCode, 1);
	}

	/**
	 * Saves the given {@link Country}. Creates it if new, updates it if already existing.
	 * Audit fields (createdBy, createdDate, lastModifiedBy, lastModifiedDate) are populated
	 * automatically via {@code Auditable} and Spring Data JPA auditing.
	 *
	 * @param country the country to save.
	 * @return the saved {@link Country}.
	 * @throws OHServiceException when fails to save the country.
	 */
	public Country saveCountry(Country country) throws OHServiceException {
		return repository.save(country);
	}

	/**
	 * Soft-deletes the {@link Country} with the given id by setting its {@code active} flag to {@code 0}.
	 * The record remains in the database and will no longer appear in active queries.
	 *
	 * @param id the id of the country to soft-delete.
	 * @throws OHServiceException when fails to soft-delete the country.
	 */
	public void deleteCountry(int id) throws OHServiceException {
		repository.softDelete(id);
	}

	/**
	 * Checks whether the given ISO code is unique among active {@link Country}s.
	 * When {@code excludeId} is provided, the country with that id is excluded
	 * from the uniqueness check (useful during an update to avoid self-conflict).
	 *
	 * @param isoCode   the ISO code to check.
	 * @param excludeId the id of the country to exclude, or {@code null} for a creation check.
	 * @return {@code true} if the ISO code is available, {@code false} if already taken by another country.
	 * @throws OHServiceException when fails to execute the uniqueness check.
	 */
	public boolean isCodeUnique(String isoCode, Integer excludeId) throws OHServiceException {
		Optional<Country> existing = repository.findByIsoCodeAndActive(isoCode, 1);
		if (excludeId == null) {
			return existing.isEmpty();
		}
		return existing.isEmpty() || existing.get().getId() == excludeId;
	}

	/**
	 * Searches for active {@link Country}s whose name, ISO code or phone code
	 * contains the given keyword (case-insensitive).
	 * If the keyword is {@code null} or blank, returns all active countries.
	 *
	 * @param keyword the search keyword; may be {@code null} or empty.
	 * @return the list of matching active {@link Country}s.
	 * @throws OHServiceException when fails to execute the search.
	 */
	public List<Country> searchCountries(String keyword) throws OHServiceException {
		if (keyword == null || keyword.trim().isEmpty()) {
			return getAllCountries();
		}
		return repository.searchCountries(keyword.trim());
	}
}