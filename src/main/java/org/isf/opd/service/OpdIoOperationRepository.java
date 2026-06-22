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
package org.isf.opd.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.isf.distype.model.DiseaseType;
import org.isf.opd.model.Opd;
import org.isf.opd.model.DiagnosisEntry;;
import org.isf.patient.model.Patient;
import org.isf.ward.model.Ward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;

public interface OpdIoOperationRepository extends JpaRepository<Opd, Integer>, OpdIoOperationRepositoryCustom {

	Opd findOneByPatientAndNextVisitDate(Patient patient, LocalDateTime visitDate);

	@Query("select o from Opd o order by o.prog_year")
	List<Opd> findAllOrderByProgYearDesc();

	@Query("select o from Opd o where o.patient.code = :code order by o.prog_year")
	List<Opd> findAllByPatient_CodeOrderByProgYearDesc(@Param("code") Integer code);

	@Query("select max(o.prog_year) from Opd o")
	Integer findMaxProgYear();

	@Query(value = "select max(o.prog_year) from Opd o where o.date >= :dateFrom and o.date < :dateTo")
	Integer findMaxProgYearWhereDateBetween(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo);

	List<Opd> findTop1ByPatient_CodeOrderByDateDesc(Integer code);

	@Query("select o from Opd o where o.prog_year = :prog_year")
	List<Opd> findByProgYear(@Param("prog_year") Integer prog_year);

	@Query(value = "select op from Opd op where op.prog_year = :prog_year and op.date >= :dateVisitFrom and op.date < :dateVisitTo")
	List<Opd> findByProgYearAndDateBetween(@Param("prog_year") Integer prog_year, @Param("dateVisitFrom") LocalDateTime dateVisitFrom,
					@Param("dateVisitTo") LocalDateTime dateVisitTo);

	@Query("select o from Opd o order by o.prog_year")
	Page<Opd> findAllOrderByProgYearDescPageable(Pageable pageable);

	@Query("select o from Opd o where o.ward = :ward order by o.prog_year")
	Page<Opd> findAllByWardOrderByProgYearDescPageable(@Param("ward") Ward ward, Pageable pageable);

	@Query("select o from Opd o where o.patient.code = :code order by o.prog_year")
	Page<Opd> findAllByPatient_CodeOrderByProgYearDescPageable(@Param("code") Integer code, Pageable pageable);

	Page<Opd> findAllByPatient_CodeOrderByDateDesc(Integer code, Pageable pageable);

	Page<Opd> findAllByOrderByDateDesc(Pageable pageable);

	@Query("select o from Opd o where o.prog_year = :prog_year")
	Page<Opd> findByProgYear(@Param("prog_year") Integer prog_year, Pageable pageable );

	@Query(value = "SELECT op FROM Opd op WHERE "
		+ "(:wardCode IS NULL OR op.ward.code = :wardCode) "
		+ "AND (:diseaseType IS NULL OR op.disease.diseaseType.code = :diseaseType) "
		+ "AND (:diseaseCode IS NULL OR op.disease.code = :diseaseCode) "
		+ "AND (op.date BETWEEN :dateFrom AND :dateTo) "
		+ "AND ((:ageFrom = 0 AND :ageTo = 0) OR op.age BETWEEN :ageFrom AND :ageTo) "
		+ "AND (:sex = 'A' OR op.sex = :sex) "
		+ "AND (:newPatient = 'A' OR op.newPatient = :newPatient) "
		+ "AND (:user IS NULL OR op.userID = :user) "
		+ "ORDER BY op.date DESC")
	Page<Opd> findOpdListPageable(
		@Param("wardCode") String wardCode,
		@Param("diseaseType") String diseaseType,
		@Param("diseaseCode") String diseaseCode,
		@Param("dateFrom") LocalDateTime dateFrom,
		@Param("dateTo") LocalDateTime dateTo,
		@Param("ageFrom") int ageFrom,
		@Param("ageTo") int ageTo,
		@Param("sex") String sex,
		@Param("newPatient") String newPatient,
		@Param("user") String user,
		Pageable pageable);
	@Query(value = "SELECT OPD_CREATED_DATE FROM OH_OPD O WHERE OPD_ACTIVE=1 ORDER BY OPD_ID DESC LIMIT 1", nativeQuery = true)
	LocalDateTime lastOpdCreationDate();

	@Query("select count(o) from Opd o where active=1")
	long countAllActiveOpds();

	@Query("select count(o) from Opd o where o.prog_year = :prog_year")
	long countByProgYear(@Param("prog_year") Integer prog_year);

	long countByPatient_CodeOrderByDateDesc(Integer code);

	@Query("select o from Opd o where o.prog_year = :prog_year order by o.prog_year")
	Page<Opd> findByProgYearPageable(@Param("prog_year") int progYear, Pageable pageable);
	@Query("SELECT d FROM DiagnosisEntry d WHERE d.opd.code = :opdId AND d.active = true ORDER BY d.orderNumber ASC")
	List<DiagnosisEntry> findActiveDiagnosesByOpdId(@Param("opdId") int opdId);

	@Query("SELECT d FROM DiagnosisEntry d WHERE d.opd.code = :opdId")
	List<DiagnosisEntry> findAllDiagnosesByOpdId(@Param("opdId") int opdId);

	@Query("SELECT COUNT(d) > 0 FROM DiagnosisEntry d WHERE d.opd.code = :opdId AND d.active = true")
	boolean hasActiveDiagnoses(@Param("opdId") int opdId);

	@Modifying
	@Transactional
	@Query("UPDATE DiagnosisEntry d SET d.active = false WHERE d.opd.code = :opdId")
	void softDeleteDiagnosesByOpdId(@Param("opdId") int opdId);
}