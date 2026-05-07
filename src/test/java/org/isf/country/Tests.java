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
package org.isf.country;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import org.isf.OHCoreTestCase;
import org.isf.country.manager.CountryBrowserManager;
import org.isf.country.model.Country;
import org.isf.country.service.CountryIoOperationRepository;
import org.isf.country.service.CountryIoOperations;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityNotFoundException;

class Tests extends OHCoreTestCase {

	private static TestCountry testCountry;

	@Autowired
	CountryIoOperations countryIoOperations;

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

	// ════════════════════════════════════════════
	//  MODEL
	// ════════════════════════════════════════════

	@Test
	void testCountryGets() throws Exception {
		int id = setupTestCountry(false);
		checkCountryIntoDb(id);
	}

	@Test
	void testCountrySets() throws Exception {
		int id = setupTestCountry(true);
		checkCountryIntoDb(id);
	}

	@Test
	void testCountryEquals() throws Exception {
		Country country1 = testCountry.setup(false);
		countryIoOperationRepository.saveAndFlush(country1);

		Country country2 = new Country();
		country2.setId(country1.getId());
		assertThat(country1).isEqualTo(country2);

		Country country3 = new Country();
		country3.setId(country1.getId() + 1);
		assertThat(country1).isNotEqualTo(country3);

		assertThat(country1).isNotNull();
		assertThat(country1).isNotEqualTo("someString");
	}

	@Test
	void testCountryHashCode() throws Exception {
		Country country = testCountry.setup(true);
		countryIoOperationRepository.saveAndFlush(country);

		int hashCode = country.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + country.getId());
		assertThat(country.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void testCountryToString() throws Exception {
		Country country = testCountry.setup(false);
		assertThat(country).hasToString("Cameroon");
	}

	@Test
	void testCountryCompareTo() throws Exception {
		Country country1 = testCountry.setup(false);
		country1.setId(1);

		Country country2 = testCountry.setup(false);
		country2.setId(2);

		assertThat(country1.compareTo(country2)).isNegative();
		assertThat(country2.compareTo(country1)).isPositive();
		assertThat(country1.compareTo(country1)).isZero();
	}

	// ════════════════════════════════════════════
	//  IO (Service)
	// ════════════════════════════════════════════

	@Test
	void testIoGetAllCountries() throws Exception {
		int id = setupTestCountry(false);
		Country foundCountry = countryIoOperationRepository.findById(id).orElse(null);
		assertThat(foundCountry).isNotNull();

		List<Country> countries = countryIoOperations.getAllCountries();

		assertThat(countries).hasSize(1);
		assertThat(countries.get(0).getName()).isEqualTo(foundCountry.getName());
	}

	@Test
	void testIoGetAllCountries_shouldNotReturnSoftDeletedCountry() throws Exception {
		Country deleted = testCountry.setup(false);
		deleted.setActive(0);
		countryIoOperationRepository.saveAndFlush(deleted);

		List<Country> countries = countryIoOperations.getAllCountries();

		assertThat(countries).isEmpty();
	}

	@Test
	void testIoGetCountryById() throws Exception {
		int id = setupTestCountry(false);

		Optional<Country> result = countryIoOperations.getCountryById(id);

		assertThat(result).isPresent();
		assertThat(result.get().getIsoCode()).isEqualTo("CM");
	}

	@Test
	void testIoGetCountryById_shouldReturnEmptyForSoftDeleted() throws Exception {
		Country deleted = testCountry.setup(false);
		deleted.setActive(0);
		countryIoOperationRepository.saveAndFlush(deleted);

		java.util.Optional<Country> result = countryIoOperations.getCountryById(deleted.getId());

		assertThat(result).isEmpty();
	}

	@Test
	void testIoGetCountryByIsoCode() throws Exception {
		setupTestCountry(false);

		java.util.Optional<Country> result = countryIoOperations.getCountryByIsoCode("CM");

		assertThat(result).isPresent();
		assertThat(result.get().getName()).isEqualTo("Cameroon");
	}

	@Test
	void testIoGetCountryByIsoCode_shouldReturnEmptyForUnknown() throws Exception {
		java.util.Optional<Country> result = countryIoOperations.getCountryByIsoCode("XX");

		assertThat(result).isEmpty();
	}

	@Test
	void testIoSaveNewCountry() throws Exception {
		Country country = testCountry.setup(true);
		Country saved = countryIoOperations.saveCountry(country);
		assertThat(saved.getId()).isPositive();
		assertThat(saved.getName()).isEqualTo("Cameroon");
		assertThat(saved.getActive()).isEqualTo(1);
	}

	@Test
	void testIoUpdateCountry() throws Exception {
		int id = setupTestCountry(false);
		Country foundCountry = countryIoOperationRepository.findById(id).orElse(null);
		assertThat(foundCountry).isNotNull();

		foundCountry.setName("Cameroun");
		Country updated = countryIoOperations.saveCountry(foundCountry);

		assertThat(updated.getName()).isEqualTo("Cameroun");
	}

	@Test
	void testIoSoftDeleteCountry() throws Exception {
		int id = setupTestCountry(false);

		countryIoOperations.deleteCountry(id);

		assertThat(countryIoOperations.getCountryById(id)).isEmpty();

		java.util.Optional<Country> raw = countryIoOperationRepository.findById(id);
		assertThat(raw).isPresent();
		assertThat(raw.get().getActive()).isEqualTo(0);
	}

	@Test
	void testIoIsCodeUnique_trueWhenNoExisting() throws Exception {
		boolean result = countryIoOperations.isCodeUnique("CM", null);

		assertThat(result).isTrue();
	}

	@Test
	void testIoIsCodeUnique_falseWhenTakenByAnother() throws Exception {
		int id = setupTestCountry(false);
		Country foundCountry = countryIoOperationRepository.findById(id).orElse(null);
		assertThat(foundCountry).isNotNull();

		boolean result = countryIoOperations.isCodeUnique("CM", null);

		assertThat(result).isFalse();
	}

	@Test
	void testIoIsCodeUnique_trueWhenBelongsToSameCountry() throws Exception {
		int id = setupTestCountry(false);

		boolean result = countryIoOperations.isCodeUnique("CM", id);

		assertThat(result).isTrue();
	}

	@Test
	void testIoSearchCountries_byName() throws Exception {
		setupTestCountry(false);

		List<Country> result = countryIoOperations.searchCountries("came");

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getName()).isEqualTo("Cameroon");
	}

	@Test
	void testIoSearchCountries_byIsoCode() throws Exception {
		setupTestCountry(false);

		List<Country> result = countryIoOperations.searchCountries("CM");

		assertThat(result).hasSize(1);
	}

	@Test
	void testIoSearchCountries_byPhoneCode() throws Exception {
		setupTestCountry(false);

		List<Country> result = countryIoOperations.searchCountries("+237");

		assertThat(result).hasSize(1);
	}

	@Test
	void testIoSearchCountries_caseInsensitive() throws Exception {
		setupTestCountry(false);

		List<Country> result = countryIoOperations.searchCountries("CAMEROON");

		assertThat(result).hasSize(1);
	}

	@Test
	void testIoSearchCountries_noMatch() throws Exception {
		setupTestCountry(false);

		List<Country> result = countryIoOperations.searchCountries("xyz999");

		assertThat(result).isEmpty();
	}

	@Test
	void testIoSearchCountries_emptyKeywordReturnsAll() throws Exception {
		setupTestCountry(false);

		List<Country> result = countryIoOperations.searchCountries("");

		assertThat(result).hasSize(1);
	}

	@Test
	void testIoSearchCountries_shouldNotReturnSoftDeleted() throws Exception {
		Country deleted = testCountry.setup(false);
		deleted.setActive(0);
		countryIoOperationRepository.saveAndFlush(deleted);

		List<Country> result = countryIoOperations.searchCountries("Cameroon");

		assertThat(result).isEmpty();
	}

	// ════════════════════════════════════════════
	//  MANAGER
	// ════════════════════════════════════════════

	@Test
	void testMgrGetCountries() throws Exception {
		setupTestCountry(false);

		List<Country> result = countryBrowserManager.getCountries();

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getName()).isEqualTo("Cameroon");
	}

	@Test
	void testMgrGetCountry() throws Exception {
		int id = setupTestCountry(false);

		Country result = countryBrowserManager.getCountry(id);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(id);
	}

	@Test
	void testMgrGetCountry_shouldThrowWhenNotFound() throws Exception {
		assertThatThrownBy(() -> countryBrowserManager.getCountry(9999))
			.isInstanceOf(EntityNotFoundException.class)
			.hasMessageContaining("9999");
	}

	@Test
	void testMgrSaveNewCountry() throws Exception {
		Country country = testCountry.setup(true);

		Country saved = countryBrowserManager.saveCountry(country);

		assertThat(saved.getId()).isPositive();
		checkCountryIntoDb(saved.getId());
	}

	@Test
	void testMgrUpdateCountry() throws Exception {
		int id = setupTestCountry(false);
		Country foundCountry = countryIoOperationRepository.findById(id).orElse(null);
		assertThat(foundCountry).isNotNull();

		foundCountry.setName("Updated Name");
		Country updated = countryBrowserManager.saveCountry(foundCountry);

		assertThat(updated.getName()).isEqualTo("Updated Name");
	}

	@Test
	void testMgrDeleteCountry() throws Exception {
		int id = setupTestCountry(false);

		countryBrowserManager.deleteCountry(id);

		assertThatThrownBy(() -> countryBrowserManager.getCountry(id))
			.isInstanceOf(EntityNotFoundException.class);

		java.util.Optional<Country> raw = countryIoOperationRepository.findById(id);
		assertThat(raw).isPresent();
		assertThat(raw.get().getActive()).isEqualTo(0);
	}

	@Test
	void testMgrSearchCountries() throws Exception {
		setupTestCountry(false);

		List<Country> result = countryBrowserManager.searchCountries("came");

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getName()).isEqualTo("Cameroon");
	}

	@Test
	void testMgrIsCodeUnique_true() throws Exception {
		boolean result = countryBrowserManager.isCodeUnique("CM", null);
		assertThat(result).isTrue();
	}

	@Test
	void testMgrIsCodeUnique_false() throws Exception {
		setupTestCountry(false);

		boolean result = countryBrowserManager.isCodeUnique("CM", null);

		assertThat(result).isFalse();
	}

	// ════════════════════════════════════════════
	//  HELPERS
	// ════════════════════════════════════════════

	private int setupTestCountry(boolean usingSet) throws OHException {
		Country country = testCountry.setup(usingSet);
		countryIoOperationRepository.saveAndFlush(country);
		return country.getId();
	}

	private void checkCountryIntoDb(int id) throws OHException {
		Country foundCountry = countryIoOperationRepository.findById(id).orElse(null);
		assertThat(foundCountry).isNotNull();
		testCountry.check(foundCountry);
	}
}