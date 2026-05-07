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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CountryIoOperations {

	@Autowired
	private CountryIoOperationRepository repository;

	public List<Country> getAllCountries() {
		return repository.findAllByActiveOrderByNameAsc(1);
	}

	public Optional<Country> getCountryById(int id) {
		return repository.findByIdAndActive(id, 1);
	}

	public Optional<Country> getCountryByIsoCode(String isoCode) {
		return repository.findByIsoCodeAndActive(isoCode, 1);
	}

	public Country saveCountry(Country country) {
		return repository.save(country);
	}

	public void deleteCountry(int id) {
		repository.softDelete(id);
	}

	public boolean isCodeUnique(String isoCode, Integer excludeId) {
		Optional<Country> existing = repository.findByIsoCodeAndActive(isoCode, 1);
		if (excludeId == null) {
			return existing.isEmpty();
		}
		return existing.isEmpty() || existing.get().getId() == excludeId;
	}

	public List<Country> searchCountries(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return getAllCountries();
		}
		return repository.searchCountries(keyword.trim());
	}
}