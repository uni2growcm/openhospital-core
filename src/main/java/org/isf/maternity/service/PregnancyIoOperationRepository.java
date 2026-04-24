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

import feign.Param;
import org.isf.maternity.model.Pregnancy;
import org.isf.maternity.model.PregnancyStatus;
import org.isf.maternity.model.RiskLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Pregnancy entity
 */
@Repository
public interface PregnancyIoOperationRepository extends JpaRepository<Pregnancy, Integer> {
	List<Pregnancy> findByPatient_CodeOrderByCreatedDateDesc(Integer patientCode);

	Optional<Pregnancy> findTopByPatient_CodeAndStatusOrderByCreatedDateDesc(
		Integer patientCode,
		PregnancyStatus status
	);

	boolean existsByPatient_CodeAndStatus(Integer patientCode, PregnancyStatus status);

	long countByPatient_CodeAndStatus(Integer patientCode, PregnancyStatus status);

	Page<Pregnancy> findByCreatedDateBetween(
		LocalDateTime from,
		LocalDateTime to,
		Pageable pageable
	);

	@Query("""
		SELECT p FROM Pregnancy p
		WHERE (:patientId IS NULL OR p.patient.code = :patientId)
		  AND (:status IS NULL OR p.status = :status)
		  AND (:riskLevel IS NULL OR p.riskLevel = :riskLevel)
		  AND (:fromDate IS NULL OR p.createdDate >= :fromDate)
		  AND (:toDate IS NULL OR p.createdDate <= :toDate)
	""")
	Page<Pregnancy> getPregnancies(
		@Param("patientId") Integer patientId,
		@Param("status") PregnancyStatus status,
		@Param("riskLevel") RiskLevel riskLevel,
		@Param("fromDate") LocalDateTime fromDate,
		@Param("toDate") LocalDateTime toDate,
		Pageable pageable
	);
}
