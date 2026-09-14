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

import java.util.List;

import org.isf.pregnancy.model.PregnancyDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PregnancyDeliveryIoOperationRepository extends JpaRepository<PregnancyDelivery, Integer> {

	List<PregnancyDelivery> findByAdmission_IdOrderByIdDesc(int admissionId);

	List<PregnancyDelivery> findByPregnancy_IdOrderByIdDesc(int pregnancyId);

	@Query("select distinct d.pregnancy.id from PregnancyDelivery d where d.pregnancy.id in :pregnancyIds")
	List<Integer> findPregnancyIdsWithDelivery(@Param("pregnancyIds") List<Integer> pregnancyIds);

	@Query("select d from PregnancyDelivery d "
					+ "where (d.admission is not null and d.admission.patient.code = :patientCode) "
					+ "or (d.pregnancy is not null and d.pregnancy.patient.code = :patientCode) "
					+ "order by d.id desc")
	List<PregnancyDelivery> findByPatientCodeOrderByIdDesc(@Param("patientCode") int patientCode);
}
