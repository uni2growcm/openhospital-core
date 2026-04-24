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

import org.isf.maternity.model.PregnancyVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository("maternityVisitIoOperationRepository")
public interface PregnancyVisitIoOperationRepository extends JpaRepository<PregnancyVisit, Integer> {

	List<PregnancyVisit> findByPregnancyIdOrderByVisitDateAsc(Integer pregnancyId);

	Optional<PregnancyVisit> findTopByPregnancyIdOrderByVisitDateDesc(Integer pregnancyId);

	List<PregnancyVisit> findByPregnancyIdAndVisitDateBetween(
		Integer pregnancyId,
		LocalDateTime dateFrom,
		LocalDateTime dateTo
	);

	long countByPregnancyId(Integer pregnancyId);

	boolean existsByPregnancyId(Integer pregnancyId);

	@Query("""
        SELECT v FROM PregnancyVisit v
        WHERE v.pregnancy.id = :pregnancyId
        AND v.visitDate <= :deliveryDate
        ORDER BY v.visitDate DESC
    """)
	List<PregnancyVisit> findPrenatalVisits(Integer pregnancyId, LocalDateTime deliveryDate);

	@Query("""
        SELECT v FROM PregnancyVisit v
        WHERE v.pregnancy.id = :pregnancyId
        AND v.visitDate > :deliveryDate
        ORDER BY v.visitDate ASC
    """)
	List<PregnancyVisit> findPostnatalVisits(Integer pregnancyId, LocalDateTime deliveryDate);

	@Query("""
		SELECT v FROM PregnancyVisit v
		WHERE v.pregnancy.id = :pregnancyId
		AND (:fromDate IS NULL OR v.visitDate >= :fromDate)
		AND (:toDate IS NULL OR v.visitDate <= :toDate)
		AND (:visitTypeCode IS NULL OR v.visitType.code = :visitTypeCode)
		ORDER BY v.visitDate ASC
	""")
	List<PregnancyVisit> findVisitsByFilters(
		Integer pregnancyId,
		LocalDateTime fromDate,
		LocalDateTime toDate,
		String visitTypeCode
	);
}