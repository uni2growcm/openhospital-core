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
package org.isf.partner.service;

import org.isf.partner.model.Partner;
import org.isf.typology.model.Typology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PartnerIoOperationRepository extends JpaRepository<Partner, Integer> {

	@Query("SELECT p FROM Partner p WHERE p.active = 1")
	List<Partner> findByActiveTrue();

	@Query("SELECT p FROM Partner p WHERE p.active = 1 ORDER BY p.name ASC")
	List<Partner> findByActiveTrueOrderByNameAsc();

	@Query("SELECT p FROM Partner p WHERE p.type = :type AND p.active = 1")
	List<Partner> findByTypeAndActiveTrue(@Param("type") Typology type);

	@Query("SELECT p FROM Partner p WHERE p.type.code = :typeCode AND p.active = 1")
	List<Partner> findByType_CodeAndActiveTrue(@Param("typeCode") String typeCode);

	@Query("SELECT p FROM Partner p WHERE p.active = 1 AND " +
		"(CONCAT('', p.code) LIKE CONCAT('%', :keyword, '%') OR " +
		" LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		" LOWER(p.contactPerson) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		" LOWER(p.type.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	List<Partner> searchActive(@Param("keyword") String keyword);

	@Modifying
	@Transactional
	@Query("UPDATE Partner p SET p.active = 0 WHERE p.id = :id")
	void softDelete(int id);

}