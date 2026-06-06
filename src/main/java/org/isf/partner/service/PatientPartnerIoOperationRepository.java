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
package org.isf.partner.service;

import org.isf.partner.model.PatientPartner;
import org.isf.patient.model.Patient;
import org.isf.partner.model.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientPartnerIoOperationRepository extends JpaRepository<PatientPartner, Integer> {

	List<PatientPartner> findByPatientAndActive(Patient patient, int active);

	List<PatientPartner> findByPartnerAndActive(Partner partner, int active);

	List<PatientPartner> findByPatientAndEndDateIsNullAndActive(Patient patient, int active);

	Optional<PatientPartner> findByPatientAndPartnerAndEndDateIsNullAndActive(Patient patient, Partner partner, int active);

	@Query("SELECT pp FROM PatientPartner pp WHERE pp.patient = :patient AND pp.active = 1 AND " +
		"(pp.endDate IS NULL OR pp.endDate > :today)")
	List<PatientPartner> findActiveByPatient(@Param("patient") Patient patient,
	                                         @Param("today") LocalDate today);

	@Modifying
	@Transactional
	@Query("UPDATE PatientPartner pp SET pp.endDate = :endDate, pp.active = 0 WHERE pp.id = :id")
	void endPartnership(@Param("id") int id, @Param("endDate") LocalDate endDate);
}