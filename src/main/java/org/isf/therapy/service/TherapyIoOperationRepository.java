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
package org.isf.therapy.service;

import java.util.List;

import org.isf.patient.model.Patient;
import org.isf.therapy.model.TherapyRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TherapyIoOperationRepository extends JpaRepository<TherapyRow, Integer> {

	List<TherapyRow> findAllByOrderByPatientAscTherapyIDAsc();

	List<TherapyRow> findByPatientCodeOrderByPatientCodeAscTherapyIDAsc(Integer patient);

	@Modifying
	void deleteByPatient(@Param("patient") Patient patient);

	@Query("select count(t) from TherapyRow t where active=1")
	long countAllActiveTherapies();

	@Query("SELECT COUNT(t) FROM TherapyRow t WHERE t.patient.code = :patientCode AND (t.qtyBougth IS NULL OR t.qtyBougth < t.qty)")
	long countUnbilledTherapies(@Param("patientCode") int patientCode);

	@Modifying
	@Query("UPDATE TherapyRow t SET t.qtyBougth = COALESCE(t.qtyBougth, 0) + :quantity WHERE t.therapyID = :therapyId")
	void updateBougthQuantity(@Param("therapyId") int therapyId, @Param("quantity") double quantity);
}