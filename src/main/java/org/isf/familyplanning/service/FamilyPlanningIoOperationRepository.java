/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.familyplanning.service;

import java.time.LocalDate;
import java.util.List;

import org.isf.familyplanning.model.FamilyPlanningMethod;
import org.isf.familyplanning.model.FamilyPlanningReason;
import org.isf.familyplanning.model.FamilyPlanningRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyPlanningIoOperationRepository extends JpaRepository<FamilyPlanningRecord, Integer> {

	List<FamilyPlanningRecord> findByPatient_CodeOrderByVisitDateDesc(int patientCode);

	List<FamilyPlanningRecord> findByVisitDateBetweenOrderByVisitDateAsc(LocalDate dateFrom, LocalDate dateTo);

	@Query("""
		select fp from FamilyPlanningRecord fp
			where (:search is null or :search = ''
				or lower(fp.patient.firstName) like lower(concat('%', :search, '%'))
				or lower(fp.patient.secondName) like lower(concat('%', :search, '%')))
			and (:method is null or fp.method = :method)
			and (:reason is null or fp.reason = :reason)
			and (:dateFrom is null or fp.visitDate >= :dateFrom)
			and (:dateTo is null or fp.visitDate <= :dateTo)
			order by fp.visitDate desc
		""")
	Page<FamilyPlanningRecord> findAllFiltered(@Param("search") String search, @Param("method") FamilyPlanningMethod method,
					@Param("reason") FamilyPlanningReason reason, @Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo,
					Pageable pageable);
}
