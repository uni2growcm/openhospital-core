package org.isf.country.service;

import org.isf.country.model.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CountryIoOperations {

	@Autowired
	private CountryIoOperationRepository repository;

	public List<Country> getAllCountries() {
		return repository.findAllByOrderByNameAsc();
	}

	public Country getCountryById(Long id) {
		return repository.findById(id).orElse(null);
	}

	public Country getCountryByIsoCode(String isoCode) {
		return repository.findByIsoCode(isoCode).orElse(null);
	}

	public Country saveCountry(Country country) {
		return repository.save(country);
	}

	public void deleteCountry(Long id) {
		repository.deleteById(id);
	}

	public boolean isCodeUnique(String isoCode, Long excludeId) {
		Country existing = getCountryByIsoCode(isoCode);
		return existing == null || existing.getId().equals(excludeId);
	}
}