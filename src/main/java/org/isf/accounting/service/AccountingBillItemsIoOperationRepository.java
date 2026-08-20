/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import org.isf.accounting.model.BillItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountingBillItemsIoOperationRepository extends JpaRepository<BillItems, Integer> {

	List<BillItems> findByBill_idOrderByIdAsc(int billId);

	List<BillItems> findAllByOrderByIdAsc();

	@Query("select b from BillItems b group by b.itemDescription")
	List<BillItems> findAllGroupByDescription();

	// clearAutomatically: this bulk delete bypasses the persistence context, so any already-loaded
	// BillItems for this bill (e.g. still managed from an earlier read in the same transaction) would
	// otherwise remain as stale managed entities - if a parent Bill is then removed in the same flush,
	// Hibernate's cascade check on that stale entity's `bill` association throws
	// "references an unsaved transient instance" (a known bulk-delete + first-level-cache pitfall).
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = "delete from BillItems b where b.bill.id = :billId")
	void deleteWhereId(@Param("billId") Integer billId);

	@Query("select count(b) > 0 from BillItems b where b.bill.billPatient.code = :patientCode "
		+ "and b.prescriptionId = :prescriptionId and b.itemGroup = :itemGroup and b.bill.status = 'C'")
	boolean existsBilledOnClosedBill(@Param("patientCode") int patientCode, @Param("prescriptionId") int prescriptionId,
		@Param("itemGroup") String itemGroup);

}