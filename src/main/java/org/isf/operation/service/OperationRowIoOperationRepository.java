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
package org.isf.operation.service;

import java.util.List;

import feign.Param;
import org.isf.accounting.model.Bill;
import org.isf.admission.model.Admission;
import org.isf.opd.model.Opd;
import org.isf.operation.model.OperationRow;
import org.isf.patient.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author hp
 */
@Repository
public interface OperationRowIoOperationRepository extends JpaRepository<OperationRow, String> {

	List<OperationRow> findByOrderByOpDateDesc();

	List<OperationRow> findByAdmission(Admission adm);

	OperationRow findById(int id);

	List<OperationRow> findByOpd(Opd opd);

	List<OperationRow> findByAdmissionPatientOrOpdPatient(Patient patient, Patient patient1);

	@Query("select count(o) from OperationRow o where active=1")
	long countAllActiveOperations();

	@Query("""
		SELECT o
		FROM OperationRow o
		LEFT JOIN o.admission a
		LEFT JOIN o.opd opd
		WHERE (a.patient = :patient OR opd.patient = :patient)
		AND (o.bill IS NULL OR o.bill.id = 0)
		"""
	)
	List<OperationRow> findByPatientAndBillIsNull(@Param("patient") Patient patient);

	@Modifying
	@Query("UPDATE OperationRow o SET o.bill = :bill WHERE o.id = :operationId")
	void updateBillForOperationRow(@Param("operationId") int operationId, @Param("bill") Bill bill);
}
