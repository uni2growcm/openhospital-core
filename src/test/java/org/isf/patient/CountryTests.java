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
package org.isf.patient;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.patient.manager.CountryBrowserManager;
import org.isf.patient.model.Country;
import org.isf.patient.service.CountryIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CountryTests extends OHCoreTestCase {

	private static TestCountry testCountry;

	@Autowired
	CountryIoOperationRepository countryIoOperationRepository;
	@Autowired
	CountryBrowserManager countryBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testCountry = new TestCountry();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testCountryGets() throws Exception {
		Country country = testCountry.setup(false);
		countryIoOperationRepository.saveAndFlush(country);

		Country found = countryIoOperationRepository.findById(country.getId()).orElse(null);
		assertThat(found).isNotNull();
		testCountry.check(found);
	}

	@Test
	void testCountrySets() throws Exception {
		Country country = testCountry.setup(true);
		countryIoOperationRepository.saveAndFlush(country);

		Country found = countryIoOperationRepository.findById(country.getId()).orElse(null);
		assertThat(found).isNotNull();
		testCountry.check(found);
	}

	@Test
	void testNewCountryPersists() throws Exception {
		Country country = testCountry.setup(false);

		Country saved = countryBrowserManager.newCountry(country);

		assertThat(saved.getId()).isNotZero();
		Country found = countryIoOperationRepository.findById(saved.getId()).orElse(null);
		assertThat(found).isNotNull();
		testCountry.check(found);
	}

	@Test
	void testGetCountriesReturnsAllPersisted() throws Exception {
		Country first = testCountry.setup(false);
		first.setCode("C1");
		countryIoOperationRepository.saveAndFlush(first);

		Country second = testCountry.setup(false);
		second.setCode("C2");
		countryIoOperationRepository.saveAndFlush(second);

		List<Country> countries = countryBrowserManager.getCountries();

		assertThat(countries).hasSize(2);
	}
}
