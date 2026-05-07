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

package org.isf.country.service;

import org.isf.country.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CountryIoOperationRepository extends JpaRepository<Country, Integer> {

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