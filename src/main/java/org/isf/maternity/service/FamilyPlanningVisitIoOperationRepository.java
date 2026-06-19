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

import org.isf.maternity.model.FamilyPlanningVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository("familyPlanningVisitIoOperationRepository")
public interface FamilyPlanningVisitIoOperationRepository extends JpaRepository<FamilyPlanningVisit, Integer> {

    List<FamilyPlanningVisit> findByFamilyPlanningIdOrderByVisitDateAsc(Integer familyPlanningId);

    Optional<FamilyPlanningVisit> findTopByFamilyPlanningIdOrderByVisitDateDesc(Integer familyPlanningId);

    List<FamilyPlanningVisit> findByFamilyPlanningIdAndVisitDateBetween(
        Integer familyPlanningId,
        LocalDateTime dateFrom,
        LocalDateTime dateTo
    );

    long countByFamilyPlanningId(Integer familyPlanningId);

    boolean existsByFamilyPlanningId(Integer familyPlanningId);

    @Query("""
        SELECT v FROM FamilyPlanningVisit v
        WHERE v.familyPlanning.id = :familyPlanningId
          AND (:fromDate IS NULL OR v.visitDate >= :fromDate)
          AND (:toDate IS NULL OR v.visitDate <= :toDate)
          AND (:visitTypeCode IS NULL OR v.visitType.code = :visitTypeCode)
        ORDER BY v.visitDate ASC
    """)
    List<FamilyPlanningVisit> findVisitsByFilters(
        @Param("familyPlanningId") Integer familyPlanningId,
        @Param("fromDate") LocalDateTime fromDate,
        @Param("toDate") LocalDateTime toDate,
        @Param("visitTypeCode") String visitTypeCode
    );
}
