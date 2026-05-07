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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CountryBrowserManager {

	@Autowired
	private CountryIoOperations ioOperations;

	public List<Country> getCountries() {
		return ioOperations.getAllCountries();
	}

	public Country getCountry(int id) {
		return ioOperations.getCountryById(id)
			.orElseThrow(() -> new EntityNotFoundException("Country not found with id: " + id));
	}

	public Country saveCountry(Country country) {
		return ioOperations.saveCountry(country);
	}

	public void deleteCountry(int id) {
		ioOperations.deleteCountry(id);
	}

	public List<Country> searchCountries(String keyword) {
		return ioOperations.searchCountries(keyword);
	}

	public boolean isCodeUnique(String isoCode, Integer excludeId) {
		return ioOperations.isCodeUnique(isoCode, excludeId);
	}
}