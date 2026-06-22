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

import org.isf.accounting.model.ArchivedBill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchivedBillRepository extends JpaRepository<ArchivedBill, Integer> {

	List<ArchivedBill> findAllByOrderByDateDesc();

	List<ArchivedBill> findByBillPatientIdOrderByDateDesc(Integer patientId);

	@Query("SELECT b FROM ArchivedBill b WHERE b.date >= :dateFrom AND b.date < :dateTo ORDER BY b.date DESC")
	List<ArchivedBill> findByDateBetween(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo);

	@Query("SELECT b FROM ArchivedBill b WHERE b.billPatientId = :patientId AND b.date >= :dateFrom AND b.date < :dateTo ORDER BY b.date DESC")
	List<ArchivedBill> findByDateAndPatient(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo,
	                                        @Param("patientId") Integer patientId);

	@Query("SELECT b FROM ArchivedBill b WHERE b.status = :status ORDER BY b.date DESC")
	List<ArchivedBill> findByStatusOrderByDateDesc(@Param("status") String status);

	@Query("SELECT b FROM ArchivedBill b WHERE b.status = :status AND b.billPatientId = :patientId ORDER BY b.date DESC")
	List<ArchivedBill> findByStatusAndBillPatientIdOrderByDateDesc(@Param("status") String status, @Param("patientId") Integer patientId);

	@Query("SELECT b FROM ArchivedBill b WHERE "
		+ "(:status IS NULL OR b.status = :status) AND "
		+ "(:dateFrom IS NULL OR b.date >= :dateFrom) AND "
		+ "(:dateTo IS NULL OR b.date < :dateTo) AND "
		+ "(:patientId IS NULL OR b.billPatientId = :patientId) AND "
		+ "(:guarantorId IS NULL OR b.guarantorId = :guarantorId) "
		+ "ORDER BY b.date DESC")
	List<ArchivedBill> findArchivedBillsWithFilters(
		@Param("status") String status,
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("patientId") Integer patientId,
		@Param("guarantorId") String guarantorId);

	@Query(value = "SELECT b FROM ArchivedBill b WHERE "
		+ "(:status IS NULL OR b.status = :status) AND "
		+ "(:dateFrom IS NULL OR b.date >= :dateFrom) AND "
		+ "(:dateTo IS NULL OR b.date < :dateTo) AND "
		+ "(:patientId IS NULL OR b.billPatientId = :patientId) AND "
		+ "(:guarantorId IS NULL OR b.guarantorId = :guarantorId) "
		+ "ORDER BY b.date DESC",
		countQuery = "SELECT COUNT(b) FROM ArchivedBill b WHERE "
		+ "(:status IS NULL OR b.status = :status) AND "
		+ "(:dateFrom IS NULL OR b.date >= :dateFrom) AND "
		+ "(:dateTo IS NULL OR b.date < :dateTo) AND "
		+ "(:patientId IS NULL OR b.billPatientId = :patientId) AND "
		+ "(:guarantorId IS NULL OR b.guarantorId = :guarantorId)")
	Page<ArchivedBill> findArchivedBillsWithFilters(
		@Param("status") String status,
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("patientId") Integer patientId,
		@Param("guarantorId") String guarantorId,
		Pageable pageable);

	@Query("SELECT COUNT(b) FROM ArchivedBill b WHERE "
		+ "(:status IS NULL OR b.status = :status) AND "
		+ "(:dateFrom IS NULL OR b.date >= :dateFrom) AND "
		+ "(:dateTo IS NULL OR b.date < :dateTo) AND "
		+ "(:patientId IS NULL OR b.billPatientId = :patientId) AND "
		+ "(:guarantorId IS NULL OR b.guarantorId = :guarantorId)")
	long countArchivedBillsWithFilters(
		@Param("status") String status,
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("patientId") Integer patientId,
		@Param("guarantorId") String guarantorId);

	@Query("SELECT b FROM ArchivedBill b WHERE b.date >= :dateFrom AND b.date < :dateTo AND b.billPatientId = :patientId AND b.guarantorId = :guarantorId ORDER BY b.date DESC")
	List<ArchivedBill> findByDateBetweenAndBillPatientIdAndGuarantorId(
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("patientId") Integer patientId,
		@Param("guarantorId") String guarantorId);

	@Query("SELECT b FROM ArchivedBill b WHERE b.date >= :dateFrom AND b.date < :dateTo AND b.guarantorId = :guarantorId ORDER BY b.date DESC")
	List<ArchivedBill> findByDateBetweenAndGuarantorId(
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("guarantorId") String guarantorId);

	@Query("SELECT COALESCE(SUM(b.amount), 0) FROM ArchivedBill b WHERE "
		+ "(:status IS NULL OR b.status = :status) AND "
		+ "(:dateFrom IS NULL OR b.date >= :dateFrom) AND "
		+ "(:dateTo IS NULL OR b.date < :dateTo) AND "
		+ "(:patientId IS NULL OR b.billPatientId = :patientId) AND "
		+ "(:guarantorId IS NULL OR b.guarantorId = :guarantorId)")
	double sumAmountByFilters(
		@Param("status") String status,
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("patientId") Integer patientId,
		@Param("guarantorId") String guarantorId);

	@Query("SELECT COALESCE(SUM(b.balance), 0) FROM ArchivedBill b WHERE "
		+ "(:status IS NULL OR b.status = :status) AND "
		+ "(:dateFrom IS NULL OR b.date >= :dateFrom) AND "
		+ "(:dateTo IS NULL OR b.date < :dateTo) AND "
		+ "(:patientId IS NULL OR b.billPatientId = :patientId) AND "
		+ "(:guarantorId IS NULL OR b.guarantorId = :guarantorId)")
	double sumBalanceByFilters(
		@Param("status") String status,
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("patientId") Integer patientId,
		@Param("guarantorId") String guarantorId);

	@Query("SELECT b FROM ArchivedBill b WHERE b.date >= :dateFrom AND b.date < :dateTo ORDER BY b.date")
	List<ArchivedBill> findArchivedBillsForSage(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo);

	@Query("SELECT COUNT(b) FROM ArchivedBill b WHERE b.active = 1")
	long countAllActiveArchivedBills();

	@Query("SELECT DISTINCT b.user FROM ArchivedBill b ORDER BY b.user ASC")
	List<String> findUserDistinctByOrderByUserAsc();
}
