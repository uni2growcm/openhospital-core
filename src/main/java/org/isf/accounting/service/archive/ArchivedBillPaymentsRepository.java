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

import java.time.LocalDateTime;
import java.util.List;

import org.isf.accounting.model.ArchivedBillPayments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchivedBillPaymentsRepository extends JpaRepository<ArchivedBillPayments, Integer> {

	List<ArchivedBillPayments> findByBillIdOrderByIdAsc(Integer billId);

	@Query("SELECT bp FROM ArchivedBillPayments bp WHERE bp.date >= :dateFrom AND bp.date < :dateEnd ORDER BY bp.id ASC")
	List<ArchivedBillPayments> findByDateBetweenOrderByIdAscDateAsc(
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateEnd") LocalDateTime dateEnd);

	@Query("SELECT bp FROM ArchivedBillPayments bp ORDER BY bp.billId, bp.date ASC")
	List<ArchivedBillPayments> findAllByOrderByBillAndDate();

	@Query("SELECT bp FROM ArchivedBillPayments bp WHERE bp.billId = :billId ORDER BY bp.billId, bp.date ASC")
	List<ArchivedBillPayments> findByBillIdOrderByBillAndDate(@Param("billId") Integer billId);

	@Query("SELECT bp FROM ArchivedBillPayments bp WHERE "
		+ "bp.billId IN (SELECT b.id FROM ArchivedBill b WHERE b.billPatientId = :patientId) AND "
		+ "bp.date >= :dateFrom AND bp.date < :dateTo "
		+ "ORDER BY bp.billId, bp.date ASC")
	List<ArchivedBillPayments> findByDateAndPatient(
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("patientId") Integer patientId);

	@Query("SELECT bp FROM ArchivedBillPayments bp WHERE bp.billId = :billId ORDER BY bp.date ASC")
	List<ArchivedBillPayments> findByBillIdOrderByDateAsc(@Param("billId") Integer billId);

	@Query("SELECT bp FROM ArchivedBillPayments bp WHERE "
		+ "bp.billId IN (SELECT b.id FROM ArchivedBill b WHERE b.parentId = :parentId) ORDER BY bp.date ASC")
	List<ArchivedBillPayments> findByBillParentIdOrderByDateAsc(@Param("parentId") Integer parentId);

	@Query("SELECT COALESCE(SUM(bp.amount), 0) FROM ArchivedBillPayments bp WHERE "
		+ "bp.billId IN (SELECT b.id FROM ArchivedBill b WHERE "
		+ "  (:status IS NULL OR b.status != :status) AND "
		+ "  (:dateFrom IS NULL OR b.date >= :dateFrom) AND "
		+ "  (:dateTo IS NULL OR b.date < :dateTo) AND "
		+ "  (:patientId IS NULL OR b.billPatientId = :patientId) AND "
		+ "  (:guarantorId IS NULL OR b.guarantorId = :guarantorId))")
	double sumPaymentsByFilters(
		@Param("status") String status,
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("patientId") Integer patientId,
		@Param("guarantorId") String guarantorId);

	@Query("SELECT COALESCE(SUM(bp.amount), 0) FROM ArchivedBillPayments bp WHERE "
		+ "bp.user = :username AND "
		+ "bp.billId IN (SELECT b.id FROM ArchivedBill b WHERE "
		+ "  (:status IS NULL OR b.status != :status) AND "
		+ "  (:dateFrom IS NULL OR b.date >= :dateFrom) AND "
		+ "  (:dateTo IS NULL OR b.date < :dateTo) AND "
		+ "  (:patientId IS NULL OR b.billPatientId = :patientId) AND "
		+ "  (:guarantorId IS NULL OR b.guarantorId = :guarantorId))")
	double sumPaymentsByUserAndFilters(
		@Param("username") String username,
		@Param("status") String status,
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("patientId") Integer patientId,
		@Param("guarantorId") String guarantorId);

	@Query("SELECT bp FROM ArchivedBillPayments bp WHERE bp.date >= :dateFrom AND bp.date < :dateTo ORDER BY bp.date")
	List<ArchivedBillPayments> findPaymentsForSage(
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo);

	@Query("SELECT DISTINCT bp.user FROM ArchivedBillPayments bp ORDER BY bp.user ASC")
	List<String> findUserDistinctByOrderByUserAsc();
}
