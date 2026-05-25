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

import java.time.LocalDateTime;
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

	@Modifying
	@Query(value = "delete from BillItems b where b.bill.id = :billId")
	void deleteWhereId(@Param("billId") Integer billId);

	@Query("SELECT bi FROM BillItems bi WHERE bi.bill.id = :billId ORDER BY bi.itemDate ASC")
	List<BillItems> findByBillIdOrderByItemDateAsc(@Param("billId") int billId);

	@Query("SELECT bi FROM BillItems bi WHERE bi.bill.parentId = :parentId ORDER BY bi.itemDate ASC")
	List<BillItems> findByBillParentIdOrderByItemDateAsc(@Param("parentId") Integer parentId);

	@Query("SELECT COUNT(bi) > 0 FROM BillItems bi " +
		"WHERE bi.bill.billPatient.code = :patientCode " +
		"AND bi.prescriptionId = :prescriptionId " +
		"AND bi.itemGroup = :itemGroup " +
		"AND bi.bill.status = 'C'")
	boolean existsByPatientAndPrescriptionInClosedBill(
		@Param("patientCode") Integer patientCode,
		@Param("prescriptionId") Integer prescriptionId,
		@Param("itemGroup") String itemGroup
	);

	@Query("SELECT COALESCE(SUM(b.amount), 0) FROM Bill b WHERE " +
		"(:status IS NULL OR b.status = :status) AND " +
		"(:dateFrom IS NULL OR b.date >= :dateFrom) AND " +
		"(:dateTo IS NULL OR b.date < :dateTo) AND " +
		"(:patient IS NULL OR b.billPatient = :patient) AND " +
		"(:guarantor IS NULL OR b.guarantor = :guarantor)")
	double sumAmountByFilters(
		@Param("status") String status,
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("patient") org.isf.patient.model.Patient patient,
		@Param("guarantor") org.isf.menu.model.User guarantor);

	@Query("SELECT COALESCE(SUM(b.balance), 0) FROM Bill b WHERE " +
		"(:status IS NULL OR b.status = :status) AND " +
		"(:dateFrom IS NULL OR b.date >= :dateFrom) AND " +
		"(:dateTo IS NULL OR b.date < :dateTo) AND " +
		"(:patient IS NULL OR b.billPatient = :patient) AND " +
		"(:guarantor IS NULL OR b.guarantor = :guarantor)")
	double sumBalanceByFilters(
		@Param("status") String status,
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("patient") org.isf.patient.model.Patient patient,
		@Param("guarantor") org.isf.menu.model.User guarantor);
}