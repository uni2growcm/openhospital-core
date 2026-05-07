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