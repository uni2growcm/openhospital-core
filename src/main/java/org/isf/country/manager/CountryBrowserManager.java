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
package org.isf.country.manager;

import jakarta.persistence.EntityNotFoundException;
import org.isf.country.model.Country;
import org.isf.country.service.CountryIoOperations;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CountryBrowserManager {

	private CountryIoOperations ioOperations;

	public CountryBrowserManager(CountryIoOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	/**
	 * Returns all the list of {@link Country}s.
	 *
	 * @return the list of {@link Country}s.
	 * @throws OHServiceException when fails to fetch the countries.
	 */
	public List<Country> getCountries() throws OHServiceException {
		return ioOperations.getAllCountries();
	}

	/**
	 * Returns the {@link Country} with the given id.
	 *
	 * @param id the id of the country to retrieve.
	 * @return the {@link Country} with the given id.
	 * @throws EntityNotFoundException if no active country is found with the given id.
	 * @throws OHServiceException when fails to fetch the country.
	 */
	public Country getCountry(int id) throws OHServiceException {
		return ioOperations.getCountryById(id)
			.orElseThrow(() -> new EntityNotFoundException(
				MessageBundle.formatMessage("angal.country.notfound.msg", String.valueOf(id))
			));
	}

	/**
	 * Returns the active {@link Country} with the given ISO code, if it exists.
	 *
	 * @param isoCode the ISO code (2 characters) of the country to retrieve.
	 * @return an {@link Optional} containing the {@link Country} if found and active, or empty otherwise.
	 * @throws OHServiceException when fails to fetch the country.
	 */
	public Optional<Country> getCountryByIsoCode(String isoCode) throws OHServiceException {
		return ioOperations.getCountryByIsoCode(isoCode);
	}

	/**
	 * Returns the active {@link Country} with the given name, if it exists.
	 *
	 * @param name the name of the country to retrieve.
	 * @return an {@link Optional} containing the {@link Country} if found and active, or empty otherwise.
	 * @throws OHServiceException when fails to fetch the country.
	 */
	public Optional<Country> getCountryByName(String name) throws OHServiceException {
		return ioOperations.getCountryByName(name);
	}

	/**
	 * Returns the active {@link Country} with the given name, if it exists.
	 *
	 * @param phoneCode the name of the country to retrieve.
	 * @return an {@link Optional} containing the {@link Country} if found and active, or empty otherwise.
	 * @throws OHServiceException when fails to fetch the country.
	 */
	public Optional<Country> getCountryByPhoneCode(String phoneCode) throws OHServiceException {
		return ioOperations.getCountryByPhoneCode(phoneCode);
	}

	/**
	 * Updates an existing {@link Country}.
	 *
	 * @param country the country with updated fields.
	 * @return the updated {@link Country}.
	 * @throws OHServiceException when fails to update the country.
	 */
	public Country updateCountry(Country country) throws OHServiceException {
		return ioOperations.updateCountry(country);
	}

	/**
	 * Saves a {@link Country}. Creates it if new, updates it if already existing.
	 *
	 * @param country the country to save.
	 * @return the saved {@link Country}.
	 * @throws OHServiceException when fails to save the country.
	 */
	public Country saveCountry(Country country) throws OHServiceException {
		return ioOperations.saveCountry(country);
	}

	/**
	 * Soft-deletes the {@link Country} with the given id by setting its active flag to {@code 0}.
	 * The record is not physically removed from the database.
	 *
	 * @param id the id of the country to delete.
	 * @throws OHServiceException when fails to delete the country.
	 */
	public void deleteCountry(int id) throws OHServiceException {
		ioOperations.deleteCountry(id);
	}

	/**
	 * Searches for active {@link Country}s whose name, ISO code or phone code
	 * contains the given keyword (case-insensitive).
	 * Returns all active countries if the keyword is {@code null} or blank.
	 *
	 * @param keyword the search keyword; may be {@code null} or empty.
	 * @return the list of matching {@link Country}s.
	 * @throws OHServiceException when fails to execute the search.
	 */
	public List<Country> searchCountries(String keyword) throws OHServiceException {
		return ioOperations.searchCountries(keyword);
	}

	/**
	 * Checks whether the given ISO code is unique among active {@link Country}s.
	 * When {@code excludeId} is provided, the country with that id is excluded
	 * from the uniqueness check (useful when updating an existing country).
	 *
	 * @param isoCode   the ISO code to check.
	 * @param excludeId the id of the country to exclude from the check, or {@code null} for a creation check.
	 * @return {@code true} if the ISO code is not used by any other active country, {@code false} otherwise.
	 * @throws OHServiceException when fails to execute the uniqueness check.
	 */
	public boolean isCodeUnique(String isoCode, Integer excludeId) throws OHServiceException {
		return ioOperations.isCodeUnique(isoCode, excludeId);
	}
}