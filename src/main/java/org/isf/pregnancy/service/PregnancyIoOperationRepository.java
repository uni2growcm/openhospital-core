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
package org.isf.pregnancy.service;

import java.time.LocalDate;
import java.util.List;

import org.isf.pregnancy.model.Pregnancy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PregnancyIoOperationRepository extends JpaRepository<Pregnancy, Integer> {

	List<Pregnancy> findByPatient_Code(int patientCode);

	List<Pregnancy> findByPatient_CodeAndActive(int patientCode, int active);

	List<Pregnancy> findByActiveOrderByLmpDesc(int active);

	@Query("""
		select p from Pregnancy p
			where (:search is null or :search = ''
				or lower(p.patient.firstName) like lower(concat('%', :search, '%'))
				or lower(p.patient.secondName) like lower(concat('%', :search, '%')))
			and (:active is null or p.active = :active)
			and (:dateFrom is null or p.lmp >= :dateFrom)
			and (:dateTo is null or p.lmp <= :dateTo)
			order by p.lmp desc
		""")
	Page<Pregnancy> findAllFiltered(@Param("search") String search, @Param("active") Integer active, @Param("dateFrom") LocalDate dateFrom,
					@Param("dateTo") LocalDate dateTo, Pageable pageable);
}
