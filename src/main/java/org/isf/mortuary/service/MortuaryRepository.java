/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free AND open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it AND/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-stANDalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.isf.mortuary.service;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.mortuary.model.Death;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MortuaryRepository extends JpaRepository<Death, Integer> {

	Page<Death> findAllByPatientNameContainsAndAdmissionDateBetween(
		String patientName,
		LocalDateTime admissionDateFrom,
		LocalDateTime admissionDateTo,
		Pageable pageable
	);

	Page<Death> findAllByPatientNameContainsAndDischargeDateBetween(
		String patientName,
		LocalDateTime admissionDateFrom,
		LocalDateTime admissionDateTo,
		Pageable pageable
	);

	@Query("SELECT d FROM Death d WHERE d.patient.name LIKE %:patientName% AND d.ward.code LIKE %:wardCode% AND (d.admissionDate BETWEEN :dateFrom AND :dateTo) AND d.deathReason.code LIKE %:deathReasonCode% ")
	List<Death> findAllByPatientNameAndWardCodeAndAdmissionDateBetweenAndDeathReasonCode(
		@Param("patientName") String patientName,
		@Param("wardCode") String wardCode,
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("deathReasonCode") String deathReasonCode
	);

	@Query("SELECT d FROM Death d WHERE d.patient.name LIKE %:patientName% AND d.ward.code LIKE %:wardCode% AND (d.dischargeDate BETWEEN :dateFrom AND :dateTo) AND d.deathReason.code LIKE %:deathReasonCode% ")
	List<Death> findAllByPatientNameAndWardCodeAndDischargeDateBetweenAndDeathReasonCode(
		@Param("patientName") String patientName,
		@Param("wardCode") String wardCode,
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("deathReasonCode") String deathReasonCode
	);

	Page<Death> findAllByAdmissionDateBetweenOrDischargeDateBetween(
		LocalDateTime admissionDateFrom, LocalDateTime admissionDateTo, LocalDateTime dischargeDateFrom, LocalDateTime dischargeDateTo, Pageable pageable
	);

	Page<Death> findAllByPatientNameContainsAndWardCodeContainsAndAdmissionDateBetweenAndDeathReasonCodeContains(
		String patientName, String wardCode, LocalDateTime admissionDateFrom, LocalDateTime admissionDateTo, String deathReasonCode, Pageable pageable
	);

	Page<Death> findAllByPatientNameContainsAndWardCodeContainsAndDischargeDateBetweenAndDeathReasonCodeContains(
		String patientName, String wardCode, LocalDateTime dischargeDateFrom, LocalDateTime dischargeDateTo, String deathReasonCode, Pageable pageable
	);
}
