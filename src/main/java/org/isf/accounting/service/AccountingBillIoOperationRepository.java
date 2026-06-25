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
import java.util.List;

import org.isf.accounting.model.Bill;
import org.isf.menu.model.User;
import org.isf.partner.model.Partner;
import org.isf.priceslist.model.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.isf.patient.model.Patient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@Repository
public interface AccountingBillIoOperationRepository extends JpaRepository<Bill, Integer> {

	List<Bill> findByStatusOrderByDateDesc(String status);

	List<Bill> findByStatusAndBillPatientCodeOrderByDateDesc(String status, int patientId);

	List<Bill> findAllByOrderByDateDesc();

	List<Bill> findByBillPatientCode(int patientCode);

	@Query(value = "select b from Bill b where b.date >= :dateFrom and b.date < :dateTo")
	List<Bill> findByDateBetween(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo);

	@Query(value = "select b from Bill b where b.billPatient.id = :patientCode and b.date >= :dateFrom and b.date < :dateTo")
	List<Bill> findByDateAndPatient(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo,
	                                @Param("patientCode") Integer patientCode);

	@Query(value = "select b from Bill b where b.status='O' and b.billPatient.id = :patID")
	List<Bill> findAllPendindBillsByBillPatient(@Param("patID") int patID);

	@Query(value = "select bi.bill from BillItems bi where bi.itemDescription = :desc and bi.bill.date >= :dateFrom and bi.bill.date < :dateTo")
	List<Bill> findAllWhereDatesAndBillItem(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, @Param("desc") String desc);

	@Query(value = "select distinct b.user FROM Bill b ORDER BY b.user asc")
	List<String> findUserDistinctByOrderByUserAsc();

	@Query("select count(b) from Bill b where active=1")
	long countAllActiveBills();

	List<Bill> findByDateBetweenAndGuarantorUserName(LocalDateTime dateFrom, LocalDateTime dateTo, String userName);

	List<Bill> findByDateBetweenAndBillPatientCodeAndGuarantorUserName(LocalDateTime beginningOfDay, LocalDateTime beginningOfNextDay, Integer code, String userName);

	@Query("SELECT DISTINCT p.list FROM Price p")
	List<PriceList> findDistinctPriceLists();

	@Query("SELECT p.price FROM Price p WHERE p.list.id = :listId AND p.group = :group AND p.item = :itemId")
	Double findPriceByListIdAndGroupAndItem(@Param("listId") Integer listId, @Param("group") String group, @Param("itemId") String itemId);

	@Query("SELECT b FROM Bill b WHERE "
		+ "b.parentId IS NULL AND "
		+ "(:status IS NULL OR b.status = :status) AND "
		+ "(:dateFrom IS NULL OR b.date >= :dateFrom) AND "
		+ "(:dateTo IS NULL OR b.date < :dateTo) AND "
		+ "(:patient IS NULL OR b.billPatient = :patient) AND "
		+ "(:guarantor IS NULL OR b.guarantor = :guarantor) "
		+ "ORDER BY b.date DESC")
	Page<Bill> findBillsWithFilters(
		@Param("status") String status,
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("patient") Patient patient,
		@Param("guarantor") User guarantor,
		Pageable pageable);

	@Query("SELECT COUNT(b) FROM Bill b WHERE "
		+ "b.parentId IS NULL AND "
		+ "(:status IS NULL OR b.status = :status) AND "
		+ "(:dateFrom IS NULL OR b.date >= :dateFrom) AND "
		+ "(:dateTo IS NULL OR b.date < :dateTo) AND "
		+ "(:patient IS NULL OR b.billPatient = :patient) AND "
		+ "(:guarantor IS NULL OR b.guarantor = :guarantor)")
	long countBillsWithFilters(
		@Param("status") String status,
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("patient") Patient patient,
		@Param("guarantor") User guarantor);

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
		@Param("patient") Patient patient,
		@Param("guarantor") User guarantor);

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
		@Param("patient") Patient patient,
		@Param("guarantor") User guarantor);

	@Query("SELECT b FROM Bill b WHERE b.date >= :dateFrom AND b.date < :dateTo ORDER BY b.date")
	List<Bill> findBillsForSage(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo);

	@Query("SELECT b FROM Bill b WHERE b.parentId = :parentId ORDER BY b.date DESC")
	List<Bill> findByParentId(@Param("parentId") Integer parentId);

	@Query("SELECT DISTINCT b FROM Bill b JOIN b.billPatient p JOIN p.partners pt " +
		"WHERE (:dateFrom IS NULL OR b.date >= :dateFrom) " +
		"AND (:dateTo IS NULL OR b.date < :dateTo) " +
		"AND (:status IS NULL OR b.status = :status) " +
		"AND (:patient IS NULL OR b.billPatient = :patient) " +
		"AND (:guarantor IS NULL OR b.guarantor = :guarantor) " +
		"AND (:partner IS NULL OR pt = :partner) " +
		"ORDER BY b.date DESC")
	Page<Bill> findBillsWithFilters(
		@Param("status") String status,
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("patient") Patient patient,
		@Param("guarantor") User guarantor,
		@Param("partner") Partner partner,
		Pageable pageable);

	@Query("SELECT COUNT(DISTINCT b) FROM Bill b JOIN b.billPatient p JOIN p.partners pt " +
		"WHERE (:dateFrom IS NULL OR b.date >= :dateFrom) " +
		"AND (:dateTo IS NULL OR b.date < :dateTo) " +
		"AND (:status IS NULL OR b.status = :status) " +
		"AND (:patient IS NULL OR b.billPatient = :patient) " +
		"AND (:guarantor IS NULL OR b.guarantor = :guarantor) " +
		"AND (:partner IS NULL OR pt = :partner)")
	long countBillsWithFilters(
		@Param("status") String status,
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("patient") Patient patient,
		@Param("guarantor") User guarantor,
		@Param("partner") Partner partner);

	@Query("SELECT b FROM Bill b JOIN b.billPatient p JOIN p.partners pt " +
		"WHERE b.date >= :dateFrom AND b.date < :dateTo " +
		"AND b.billPatient = :patient AND pt = :partner")
	List<Bill> findByDateBetweenAndPatientAndPartner(
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("patient") Patient patient,
		@Param("partner") Partner partner);

	@Query("SELECT b FROM Bill b JOIN b.billPatient p JOIN p.partners pt " +
		"WHERE b.date >= :dateFrom AND b.date < :dateTo AND pt = :partner")
	List<Bill> findByDateBetweenAndPartner(
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("partner") Partner partner);
}