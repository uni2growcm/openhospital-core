package org.isf.country.manager;

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
		return countryIoOperations.getCountryById(id);
	}

	public Country saveCountry(Country country) {
		return countryIoOperations.saveCountry(country);
	}

	public void deleteCountry(Long id) {
		countryIoOperations.deleteCountry(id);
	}
}