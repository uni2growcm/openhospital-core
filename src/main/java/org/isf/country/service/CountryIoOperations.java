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
		return repository.findAllByOrderByNameAsc();
	}


	public Optional<Country> getCountryById(Long id) {
		return repository.findById(id);
	}

	public Optional<Country> getCountryByIsoCode(String isoCode) {
		return repository.findByIsoCode(isoCode);
	}

	public Country saveCountry(Country country) {
		return repository.save(country);
	}

	public void deleteCountry(Long id) {
		repository.deleteById(id);
	}

	public boolean isCodeUnique(String isoCode, Long excludeId) {
		Optional<Country> existing = repository.findByIsoCode(isoCode);

		if (excludeId == null) {
			return existing.isEmpty();
		}

		return existing.isEmpty() || existing.get().getId().equals(excludeId);
	}
}