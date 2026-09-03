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
package org.isf.hivchildfollowup.service;

import java.time.LocalDate;
import java.util.List;

import org.isf.hivchildfollowup.model.HivExposedChild;
import org.isf.hivchildfollowup.model.HivExposedChildStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HivExposedChildIoOperationRepository extends JpaRepository<HivExposedChild, Integer> {

	@Query("SELECT DISTINCT c FROM HivExposedChild c LEFT JOIN FETCH c.visits WHERE c.motherPatient.code = :motherPatientCode ORDER BY c.dateOfBirth DESC")
	List<HivExposedChild> findByMotherPatient_CodeOrderByDateOfBirthDesc(@Param("motherPatientCode") int motherPatientCode);

	@Query("SELECT DISTINCT c FROM HivExposedChild c LEFT JOIN FETCH c.visits ORDER BY c.dateOfBirth DESC")
	List<HivExposedChild> findAllByOrderByDateOfBirthDesc();

	@Query("""
		select c from HivExposedChild c
			where (:search is null or :search = ''
				or lower(c.motherPatient.firstName) like lower(concat('%', :search, '%'))
				or lower(c.motherPatient.secondName) like lower(concat('%', :search, '%'))
				or lower(c.childName) like lower(concat('%', :search, '%')))
			and (:status is null or c.finalStatus = :status)
			and (:dateFrom is null or c.dateOfBirth >= :dateFrom)
			and (:dateTo is null or c.dateOfBirth <= :dateTo)
			order by c.dateOfBirth desc
		""")
	Page<HivExposedChild> findAllFiltered(@Param("search") String search, @Param("status") HivExposedChildStatus status,
					@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, Pageable pageable);
}
