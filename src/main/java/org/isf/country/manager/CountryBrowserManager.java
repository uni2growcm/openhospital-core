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
	private CountryIoOperations countryIoOperations;

	public List<Country> getCountries() {
		return countryIoOperations.getAllCountries();
	}

	public Country getCountry(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("id is null");
		}
		return countryIoOperations.getCountryById(id)
			.orElseThrow(() -> new EntityNotFoundException("Country not found with id: " + id));
	}

	public Country saveCountry(Country country) {
		return countryIoOperations.saveCountry(country);
	}

	public void deleteCountry(Long id) {
		countryIoOperations.deleteCountry(id);
	}
}