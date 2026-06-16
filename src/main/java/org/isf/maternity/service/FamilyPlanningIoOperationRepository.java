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
package org.isf.maternity.service;

import org.isf.maternity.model.FPMethod;
import org.isf.maternity.model.FPStatus;
import org.isf.maternity.model.FamilyPlanning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyPlanningIoOperationRepository extends JpaRepository<FamilyPlanning, Integer> {

    List<FamilyPlanning> findByPatient_CodeOrderByStartDateDesc(Integer patientCode);

    Optional<FamilyPlanning> findTopByPatient_CodeAndStatusOrderByStartDateDesc(Integer patientCode, FPStatus status);

    boolean existsByPatient_CodeAndStatus(Integer patientCode, FPStatus status);

    long countByPatient_CodeAndStatus(Integer patientCode, FPStatus status);

    List<FamilyPlanning> findByPatient_CodeAndStatus(Integer patientCode, FPStatus status);

    Page<FamilyPlanning> findByStartDateBetween(LocalDate from, LocalDate to, Pageable pageable);

    @Query("""
        SELECT f FROM FamilyPlanning f
        WHERE (:patientId IS NULL OR f.patient.code = :patientId)
          AND (:method IS NULL OR f.method = :method)
          AND (:status IS NULL OR f.status = :status)
          AND (:fromDate IS NULL OR f.startDate >= :fromDate)
          AND (:toDate IS NULL OR f.startDate <= :toDate)
        ORDER BY f.startDate DESC
    """)
    Page<FamilyPlanning> searchFamilyPlannings(
        @Param("patientId") Integer patientId,
        @Param("method") FPMethod method,
        @Param("status") FPStatus status,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate,
        Pageable pageable
    );
}
