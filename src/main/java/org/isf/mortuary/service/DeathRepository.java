/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import org.isf.mortuary.model.Death;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeathRepository extends JpaRepository<Death, Integer> {

	@Query("SELECT CASE WHEN COUNT(d) > 0 THEN TRUE ELSE FALSE END FROM Death d WHERE d.bodyCompartment.id = :bodyCompartmentId AND d.deleted = false AND d.dischargeDate IS NULL")
	boolean isBodyCompartmentOccupied(@Param("bodyCompartmentId") int bodyCompartmentId);

	@Query("SELECT CASE WHEN COUNT(d) > 0 THEN TRUE ELSE FALSE END FROM Death d WHERE d.deathReason.id = :deathReasonId AND d.deleted = false")
	boolean isDeathReasonUsed(@Param("deathReasonId") int deathReasonId);

	Page<Death> findAllByPatientNameContainsAndAdmissionDateBetweenAndDeleted(
		String patientName,
		LocalDateTime admissionDateFrom,
		LocalDateTime admissionDateTo,
		boolean deleted,
		Pageable pageable
	);

	Page<Death> findAllByPatientNameContainsAndEstimatedDischargeDateBetweenAndDeleted(
		String patientName,
		LocalDateTime admissionDateFrom,
		LocalDateTime admissionDateTo,
		boolean deleted,
		Pageable pageable
	);

	Page<Death> findAllByAdmissionDateBetweenOrEstimatedDischargeDateBetweenAndDeleted(
		LocalDateTime admissionDateFrom,
		LocalDateTime admissionDateTo,
		LocalDateTime dischargeDateFrom,
		LocalDateTime dischargeDateTo,
		boolean deleted,
		Pageable pageable
	);

	Page<Death> findAllByPatientNameContainsAndWardCodeContainsAndAdmissionDateBetweenAndDeathReasonTitleContainsAndDeleted(
		String patientName,
		String wardCode,
		LocalDateTime admissionDateFrom,
		LocalDateTime admissionDateTo,
		String deathReasonCode,
		boolean deleted,
		Pageable pageable
	);

	Page<Death> findAllByPatientNameContainsAndWardCodeContainsAndAdmissionDateBetweenAndDeathReasonTitleContainsAndDeletedAndDischargeDateIsNotNull(
		String patientName,
		String wardCode,
		LocalDateTime admissionDateFrom,
		LocalDateTime admissionDateTo,
		String deathReasonCode,
		boolean deleted,
		Pageable pageable
	);

	Page<Death> findAllByPatientNameContainsAndWardCodeContainsAndAdmissionDateBetweenAndDeathReasonTitleContainsAndDeletedAndDischargeDateIsNull(
		String patientName,
		String wardCode,
		LocalDateTime admissionDateFrom,
		LocalDateTime admissionDateTo,
		String deathReasonCode,
		boolean deleted,
		Pageable pageable
	);

	Page<Death> findAllByPatientNameContainsAndWardCodeContainsAndEstimatedDischargeDateBetweenAndDeathReasonTitleContainsAndDeleted(
		String patientName,
		String wardCode,
		LocalDateTime dischargeDateFrom,
		LocalDateTime dischargeDateTo,
		String deathReasonCode,
		boolean deleted,
		Pageable pageable
	);

	Page<Death> findAllByPatientNameContainsAndWardCodeContainsAndEstimatedDischargeDateBetweenAndDeathReasonTitleContainsAndDeletedAndDischargeDateIsNotNull(
		String patientName,
		String wardCode,
		LocalDateTime dischargeDateFrom,
		LocalDateTime dischargeDateTo,
		String deathReasonCode,
		boolean deleted,
		Pageable pageable
	);

	Page<Death> findAllByPatientNameContainsAndWardCodeContainsAndEstimatedDischargeDateBetweenAndDeathReasonTitleContainsAndDeletedAndDischargeDateIsNull(
		String patientName,
		String wardCode,
		LocalDateTime dischargeDateFrom,
		LocalDateTime dischargeDateTo,
		String deathReasonCode,
		boolean deleted,
		Pageable pageable
	);

	Death findByPatientCodeAndDeleted(int patientCode, boolean deleted);

	Death findByIdAndDeleted(int id, boolean b);

	boolean existsByPatientCodeAndDeletedAndIdNot(Integer code, boolean b, int id);

	boolean existsByPatientCodeAndDeleted(Integer code, boolean b);
}