package org.isf.country.service;

import org.isf.country.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CountryIoOperationRepository extends JpaRepository<Country, Integer> {

//	Optional<Country> findByIsoCodeAndActiveTrue(String isoCode);
//
//	List<Country> findAllByActiveTrueOrderByNameAsc();
//
//	Optional<Country> findByIdAndActiveTrue(int id);

	Optional<Country> findByIsoCodeAndActive(String isoCode, int active);
	List<Country> findAllByActiveOrderByNameAsc(int active);
	Optional<Country> findByIdAndActive(int id, int active);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Country c SET c.active = 0 WHERE c.id = :id")
	void softDelete(@Param("id") int id);

	@Query("""
    SELECT c FROM Country c
    WHERE c.active = 1 AND (
        LOWER(c.name)    LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(c.isoCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        c.phoneCode      LIKE CONCAT('%', :keyword, '%')
    )
    ORDER BY c.name ASC
""")
	List<Country> searchCountries(@Param("keyword") String keyword);
}