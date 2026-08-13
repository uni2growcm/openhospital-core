/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.patient.manager;

import java.util.List;

import org.isf.patient.model.Country;
import org.isf.patient.service.CountryIoOperationRepository;
import org.springframework.stereotype.Component;

/**
 * Manager for the {@link Country} lookup list used by {@code PatientInsertExtended}'s country field.
 */
@Component
public class CountryBrowserManager {

	private final CountryIoOperationRepository repository;

	public CountryBrowserManager(CountryIoOperationRepository repository) {
		this.repository = repository;
	}

	public List<Country> getCountries() {
		return repository.findAll();
	}

	public Country newCountry(Country country) {
		return repository.save(country);
	}
}
