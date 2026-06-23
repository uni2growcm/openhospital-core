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
package org.isf.hiv.service;

import java.time.LocalDate;
import java.util.List;

import org.isf.hiv.model.HIVInfant;
import org.isf.hiv.model.HIVInfant.HIVInfantStatus;
import org.isf.hiv.model.HIVInfant.FeedingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HIVInfantIoOperationRepository extends JpaRepository<HIVInfant, Integer> {

	List<HIVInfant> findByPatient_Code(Integer patientCode);

	Page<HIVInfant> findByStatus(HIVInfantStatus status, Pageable pageable);

	@Query("SELECT i FROM HIVInfant i WHERE "
		+ "(:patientCode IS NULL OR i.patient.code = :patientCode) "
		+ "AND (:status IS NULL OR i.status = :status) "
		+ "AND (:feedingType IS NULL OR i.feedingType = :feedingType) "
		+ "AND (:dateFrom IS NULL OR DATE(i.registrationDate) >= :dateFrom) "
		+ "AND (:dateTo IS NULL OR DATE(i.registrationDate) <= :dateTo) "
		+ "AND (:startDateFrom IS NULL OR i.followUpStartDate >= :startDateFrom) "
		+ "AND (:startDateTo IS NULL OR i.followUpStartDate <= :startDateTo)")
	Page<HIVInfant> findByFilters(
		@Param("patientCode") Integer patientCode,
		@Param("status") HIVInfantStatus status,
		@Param("feedingType") FeedingType feedingType,
		@Param("dateFrom") LocalDate dateFrom,
		@Param("dateTo") LocalDate dateTo,
		@Param("startDateFrom") LocalDate startDateFrom,
		@Param("startDateTo") LocalDate startDateTo,
		Pageable pageable);
}