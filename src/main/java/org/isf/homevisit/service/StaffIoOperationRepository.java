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
package org.isf.homevisit.service;

import org.isf.homevisit.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffIoOperationRepository extends JpaRepository<Staff, Integer> {

	@Query("SELECT s FROM Staff s WHERE s.active = 1 ORDER BY s.lastName, s.firstName")
	List<Staff> findAllActive();

	@Query("SELECT s FROM Staff s WHERE s.active = 1 AND " +
		"(CONCAT('', s.code) LIKE CONCAT('%', :keyword, '%') OR " +
		" LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		" LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		" LOWER(s.profession) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		" LOWER(s.position) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		" LOWER(s.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	List<Staff> searchActive(@Param("keyword") String keyword);

	@Modifying
	@Query("UPDATE Staff s SET s.active = 0 WHERE s.code = :code")
	void softDelete(@Param("code") Integer code);
}