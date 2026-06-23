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

import org.isf.homevisit.model.HomeVisit;
import org.isf.homevisit.model.HomeVisitStatus;
import org.isf.patient.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HomeVisitIoOperationRepository extends JpaRepository<HomeVisit, Integer> {

	@Query("SELECT hv FROM HomeVisit hv WHERE hv.active = 1 ORDER BY hv.visitStartDate DESC")
	Page<HomeVisit> findAllActive(Pageable pageable);

	List<HomeVisit> findByPatientAndActive(Patient patient, int active);

	@Query("SELECT hv FROM HomeVisit hv WHERE hv.active = 1 AND hv.status = :status ORDER BY hv.visitStartDate DESC")
	Page<HomeVisit> findByStatus(@Param("status") HomeVisitStatus status, Pageable pageable);

	@Query("SELECT hv FROM HomeVisit hv WHERE hv.active = 1 AND " +
		"hv.visitStartDate BETWEEN :startDate AND :endDate ORDER BY hv.visitStartDate DESC")
	Page<HomeVisit> findByDateRange(@Param("startDate") LocalDateTime startDate,
	                                @Param("endDate") LocalDateTime endDate,
	                                Pageable pageable);

	@Modifying
	@Query("UPDATE HomeVisit hv SET hv.status = :status WHERE hv.id = :id")
	void updateStatus(@Param("id") int id, @Param("status") HomeVisitStatus status);

	@Modifying
	@Query("UPDATE HomeVisit hv SET hv.active = 0 WHERE hv.id = :id")
	void softDelete(@Param("id") int id);

	@Query("SELECT hv FROM HomeVisit hv " +
		"LEFT JOIN hv.patient p " +
		"LEFT JOIN hv.staff s " +
		"WHERE hv.active = 1 " +
		"AND (:code IS NULL OR p.code = :code) " +
		"AND (:status IS NULL OR hv.status = :status) " +
		"AND (:dateFrom IS NULL OR hv.visitStartDate >= :dateFrom) " +
		"AND (:dateTo IS NULL OR hv.visitStartDate <= :dateTo) " +
		"AND (:sex IS NULL OR p.sex = :sex) " +
		"AND (:ageFrom IS NULL OR (YEAR(CURRENT_DATE) - YEAR(p.birthDate)) >= :ageFrom) " +
		"AND (:ageTo IS NULL OR (YEAR(CURRENT_DATE) - YEAR(p.birthDate)) <= :ageTo) " +
		"AND (:searchText IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
		"ORDER BY hv.visitStartDate DESC")
	Page<HomeVisit> findWithFilters(
		@Param("code") Integer code,
		@Param("status") HomeVisitStatus status,
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("sex") Character sex,
		@Param("ageFrom") Integer ageFrom,
		@Param("ageTo") Integer ageTo,
		@Param("searchText") String searchText,
		Pageable pageable);
}