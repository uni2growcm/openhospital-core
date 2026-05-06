package org.isf.country.service;

import org.isf.country.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CountryIoOperationRepository extends JpaRepository<Country, Long> {

	Optional<Country> findByIsoCode(String isoCode);
	List<Country> findAllByOrderByNameAsc();

}
