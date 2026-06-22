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
package org.isf.accounting.service.archive;

import java.util.List;

import org.isf.accounting.model.ArchivedBillItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchivedBillItemsRepository extends JpaRepository<ArchivedBillItems, Integer> {

	List<ArchivedBillItems> findByBillIdOrderByIdAsc(Integer billId);

	List<ArchivedBillItems> findAllByOrderByIdAsc();

	@Query("SELECT bi FROM ArchivedBillItems bi GROUP BY bi.itemDescription")
	List<ArchivedBillItems> findAllGroupByDescription();

	@Query("SELECT bi FROM ArchivedBillItems bi WHERE bi.billId = :billId ORDER BY bi.itemDate ASC")
	List<ArchivedBillItems> findByBillIdOrderByItemDateAsc(@Param("billId") Integer billId);

	@Query("SELECT bi FROM ArchivedBillItems bi WHERE bi.billId IN (SELECT b.id FROM ArchivedBill b WHERE b.parentId = :parentId) ORDER BY bi.itemDate ASC")
	List<ArchivedBillItems> findByBillParentIdOrderByItemDateAsc(@Param("parentId") Integer parentId);
}
