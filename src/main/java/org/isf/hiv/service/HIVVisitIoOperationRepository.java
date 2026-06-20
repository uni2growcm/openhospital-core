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

import java.time.LocalDateTime;
import java.util.List;

import org.isf.hiv.model.HIVVisit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HIVVisitIoOperationRepository extends JpaRepository<HIVVisit, Integer> {

	List<HIVVisit> findByHivInfant_IdOrderByVisitDateDesc(Integer infantId);

	Page<HIVVisit> findByHivInfant_Id(Integer infantId, Pageable pageable);

	@Query("SELECT v FROM HIVVisit v WHERE v.hivInfant.id = :infantId "
		+ "AND (:dateFrom IS NULL OR v.visitDate >= :dateFrom) "
		+ "AND (:dateTo IS NULL OR v.visitDate <= :dateTo)")
	List<HIVVisit> findByInfantIdAndDateRange(
		@Param("infantId") Integer infantId,
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo);
}