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
package org.isf.accounting.service;

import java.util.List;

import org.isf.accounting.model.BillItemGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BillItemGroupIoOperationRepository extends JpaRepository<BillItemGroup, Integer> {

	List<BillItemGroup> findAllByOrderByCreatedDateDesc();

	BillItemGroup findByTitle(String title);

	@Query("SELECT COUNT(b) > 0 FROM BillItemGroup b WHERE b.title = :title")
	boolean existsByTitle(@Param("title") String title);

	@Query("SELECT COUNT(b) > 0 FROM BillItemGroup b WHERE b.title = :title AND b.id <> :id")
	boolean existsByTitleAndIdNot(@Param("title") String title, @Param("id") int id);

	@Query("SELECT b FROM BillItemGroup b WHERE b.active = 1 ORDER BY b.createdDate DESC")
	List<BillItemGroup> findAllActive();

	@Query("SELECT COUNT(b) FROM BillItemGroup b WHERE b.active = 1")
	long countAllActive();
}