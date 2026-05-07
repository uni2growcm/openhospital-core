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