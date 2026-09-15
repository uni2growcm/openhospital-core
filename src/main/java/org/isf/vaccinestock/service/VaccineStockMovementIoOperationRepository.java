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
package org.isf.vaccinestock.service;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.vaccinestock.model.VaccineStockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VaccineStockMovementIoOperationRepository extends JpaRepository<VaccineStockMovement, Integer> {

	@Query("select coalesce(sum(m.quantity), 0) from VaccineStockMovement m where m.vaccine.code = :vaccine")
	Integer getQuantity(@Param("vaccine") String vaccineCode);

	@Query("select m from VaccineStockMovement m where m.vaccine.code = :vaccine "
			+ "and (:dateFrom is null or m.date >= :dateFrom) and (:dateTo is null or m.date <= :dateTo) "
			+ "order by m.date desc")
	List<VaccineStockMovement> findByVaccineAndDates(
			@Param("vaccine") String vaccineCode,
			@Param("dateFrom") LocalDateTime dateFrom,
			@Param("dateTo") LocalDateTime dateTo);

	List<VaccineStockMovement> findByPatientVaccine_code(int patientVaccineCode);
}
