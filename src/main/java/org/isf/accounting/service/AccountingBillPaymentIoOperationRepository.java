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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillPayments;
import org.isf.menu.model.User;
import org.isf.orthanc.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountingBillPaymentIoOperationRepository extends JpaRepository<BillPayments, Integer> {

	@Query(value = "select distinct bp.user FROM BillPayments bp ORDER BY bp.user asc")
	List<String> findUserDistinctByOrderByUserAsc();

	@Query(value = "SELECT BP FROM BillPayments BP where BP.date >= :start and BP.date < :end ORDER BY BP.id")
	List<BillPayments> findByDateBetweenOrderByIdAscDateAsc(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	List<BillPayments> findAllByBillIn(Collection<Bill> bills);

	@Query(value = "SELECT BP FROM BillPayments BP ORDER BY BP.bill, BP.date ASC")
	List<BillPayments> findAllByOrderByBillAndDate();

	@Query(value = "SELECT BP FROM BillPayments BP WHERE BP.bill.id = :billId ORDER BY BP.bill, BP.date ASC")
	List<BillPayments> findAllWherBillIdByOrderByBillAndDate(@Param("billId") Integer billId);

	@Modifying
	@Query(value = "DELETE FROM BillPayments BP where BP.bill.id = :billId")
	void deleteWhereId(@Param("billId") Integer billId);

	@Query(value = "SELECT BP FROM BillPayments BP WHERE " +
			"BP.bill.billPatient.code = :patientCode and " +
			"DATE(BP.date) between DATE(:dateFrom) and DATE(:dateTo) " +
			"ORDER BY BP.bill, BP.date ASC")
	List<BillPayments> findByDateAndPatient(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo,
			@Param("patientCode") Integer patientCode);

	List<BillPayments> findByDateBetweenAndBillBillPatientCodeAndBillGuarantorUserNameOrderByBillAscDateAsc(LocalDateTime dateFrom, LocalDateTime dateTo, Integer patientCode, String guarantor);

	List<BillPayments> findByDateBetweenAndBillGuarantorUserNameOrderByBillAscDateAsc(LocalDateTime dateFrom, LocalDateTime dateTo, String guarantor);

	@Query("SELECT bp FROM BillPayments bp WHERE bp.bill.id = :billId ORDER BY bp.date ASC")
	List<BillPayments> findByBillIdOrderByDateAsc(@Param("billId") Integer billId);

	@Query("SELECT bp FROM BillPayments bp WHERE bp.bill.parentId = :parentId ORDER BY bp.date ASC")
	List<BillPayments> findByBillParentIdOrderByDateAsc(@Param("parentId") Integer parentId);

	@Query("SELECT COALESCE(SUM(bp.amount), 0) FROM BillPayments bp WHERE " +
		"bp.bill.status != 'D' AND " +
		"(:dateFrom IS NULL OR bp.date >= :dateFrom) AND " +
		"(:dateTo IS NULL OR bp.date < :dateTo) AND " +
		"(:patient IS NULL OR bp.bill.billPatient = :patient) AND " +
		"(:guarantor IS NULL OR bp.bill.guarantor = :guarantor)")
	double sumPaymentsByFilters(
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		org.isf.patient.model.Patient patient,
		org.isf.menu.model.User guarantor);

	@Query("SELECT COALESCE(SUM(bp.amount), 0) FROM BillPayments bp WHERE " +
		"bp.bill.status != 'D' AND " +
		"bp.user = :username AND " +
		"(:dateFrom IS NULL OR bp.date >= :dateFrom) AND " +
		"(:dateTo IS NULL OR bp.date < :dateTo) AND " +
		"(:patient IS NULL OR bp.bill.billPatient = :patient) AND " +
		"(:guarantor IS NULL OR bp.bill.guarantor = :guarantor)")
	double sumPaymentsByUserAndFilters(
		String username,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		org.isf.patient.model.Patient patient,
		org.isf.menu.model.User guarantor);

	@Query("SELECT bp FROM BillPayments bp WHERE bp.date >= :dateFrom AND bp.date < :dateTo ORDER BY bp.date")
	List<BillPayments> findPaymentsForSage(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo);

	@Query("SELECT bp FROM BillPayments bp WHERE bp.date >= :dateFrom AND bp.date < :dateTo ORDER BY bp.date")
	Stream<BillPayments> streamPaymentsForSage(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo);
}