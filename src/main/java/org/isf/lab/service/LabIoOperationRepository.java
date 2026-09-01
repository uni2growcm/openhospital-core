/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.lab.service;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.lab.model.Laboratory;
import org.isf.patient.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LabIoOperationRepository extends JpaRepository<Laboratory, Integer> {

	List<Laboratory> findByLabDateBetweenOrderByLabDateDesc(LocalDateTime dateFrom, LocalDateTime dateTo);

	List<Laboratory> findByLabDateBetweenAndExamDescriptionOrderByLabDateDesc(LocalDateTime dateFrom, LocalDateTime dateTo, String exam);

	List<Laboratory> findByPatient_CodeOrderByLabDate(Integer patient);

	List<Laboratory> findByPatient_CodeAndBillIsNullOrderByLabDateDesc(Integer patient);

	List<Laboratory> findByLabDateBetweenOrderByExam_Examtype_DescriptionDesc(LocalDateTime dateFrom, LocalDateTime dateTo);

	List<Laboratory> findByLabDateBetweenAndExam_DescriptionContainingOrderByExam_Examtype_DescriptionDesc(LocalDateTime dateFrom, LocalDateTime dateTo,
					String exam);

	List<Laboratory> findByLabDateBetweenAndPatientCode(LocalDateTime dateFrom, LocalDateTime dateTo, Integer patientCode);

	List<Laboratory> findByLabDateBetweenAndExamDescriptionAndPatientCode(LocalDateTime dateFrom, LocalDateTime dateTo, String exam, Integer patient);

	@Query(value = "select lab from Laboratory lab where lab.labDate >= :dateFrom and lab.labDate < :dateTo order by lab.labDate desc")
	Page<Laboratory> findByLabDateBetweenOrderByLabDateDescPage(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo,
					Pageable pageable);

	@Query(value = "select lab from Laboratory lab where (lab.labDate >= :dateFrom and lab.labDate < :dateTo) and lab.exam.description = :exam order by lab.labDate desc")
	Page<Laboratory> findByLabDateBetweenAndExam_DescriptionOrderByLabDateDescPage(@Param("dateFrom") LocalDateTime dateFrom,
					@Param("dateTo") LocalDateTime dateTo, @Param("exam") String exam, Pageable pageable);

	@Query(value = "select lab from Laboratory lab where (lab.labDate >= :dateFrom and lab.labDate < :dateTo) and lab.patient = :patient order by lab.labDate desc")
	Page<Laboratory> findByLabDateBetweenAndPatientCodePage(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo,
					@Param("patient") Patient patient, Pageable pageable);

	@Query(value = "select lab from Laboratory lab where (lab.labDate >= :dateFrom and lab.labDate < :dateTo) and lab.exam.description = :exam and lab.patient = :patient order by lab.labDate desc")
	Page<Laboratory> findByLabDateBetweenAndExamDescriptionAndPatientCodePage(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo,
					@Param("exam") String exam, @Param("patient") Patient patient, Pageable pageable);

	@Query("select count(l) from Laboratory l where active=1")
	long countAllActiveLabs();

	/**
	 * Return the list of {@link Laboratory}s between specified dates matching the passed optional filters.
	 *
	 * @param dateFrom the lower date for the range
	 * @param dateTo the highest date for the range
	 * @param exam the exam description; {@code null} for all exams
	 * @param patient the patient; {@code null} for all patients
	 * @param prescriber the prescriber name; {@code null} for all prescribers
	 * @param paidCode the paid status code: {@code null} for all, {@code "0"} for not charged,
	 *            {@code "C"} for paid, {@code "O"} for not paid
	 * @param withResult the result filter: {@code -1} for all, {@code 0} for empty results, {@code 1} for non-empty results
	 * @return the list of {@link Laboratory}s
	 */
	@Query("""
			select lab from Laboratory lab
			left join lab.bill bill
			where lab.labDate >= :dateFrom and lab.labDate <= :dateTo
			  and (:exam is null or lab.exam.description = :exam)
			  and (:patient is null or lab.patient = :patient)
			  and (:prescriber is null or lab.prescriber = :prescriber)
			  and (:paidCode is null
			      or (:paidCode = '0' and lab.bill is null)
			      or (:paidCode <> '0' and bill.status = :paidCode))
			  and (:withResult = -1
			      or (:withResult = 0 and (lab.result = '' or lab.result is null))
			      or (:withResult = 1 and lab.result <> '' and lab.result is not null))
			order by lab.labDate desc
			""")
	List<Laboratory> findLaboratoryWithFilters(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo,
					@Param("exam") String exam, @Param("patient") Patient patient, @Param("prescriber") String prescriber,
					@Param("paidCode") String paidCode, @Param("withResult") int withResult);

	/**
	 * Return the count of {@link Laboratory}s between specified dates matching the passed optional filters.
	 *
	 * @param dateFrom the lower date for the range
	 * @param dateTo the highest date for the range
	 * @param exam the exam description; {@code null} for all exams
	 * @param patient the patient; {@code null} for all patients
	 * @param prescriber the prescriber name; {@code null} for all prescribers
	 * @param paidCode the paid status code: {@code null} for all, {@code "0"} for not charged,
	 *            {@code "C"} for paid, {@code "O"} for not paid
	 * @param withResult the result filter: {@code -1} for all, {@code 0} for empty results, {@code 1} for non-empty results
	 * @return the count of {@link Laboratory}s
	 */
	@Query("""
			select count(lab) from Laboratory lab
			left join lab.bill bill
			where lab.labDate >= :dateFrom and lab.labDate <= :dateTo
			  and (:exam is null or lab.exam.description = :exam)
			  and (:patient is null or lab.patient = :patient)
			  and (:prescriber is null or lab.prescriber = :prescriber)
			  and (:paidCode is null
			      or (:paidCode = '0' and lab.bill is null)
			      or (:paidCode <> '0' and bill.status = :paidCode))
			  and (:withResult = -1
			      or (:withResult = 0 and (lab.result = '' or lab.result is null))
			      or (:withResult = 1 and lab.result <> '' and lab.result is not null))
			""")
	long countLaboratoryWithFilters(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo,
					@Param("exam") String exam, @Param("patient") Patient patient, @Param("prescriber") String prescriber,
					@Param("paidCode") String paidCode, @Param("withResult") int withResult);

	/**
	 * Return the list of distinct prescribers already registered in the {@link Laboratory}s.
	 *
	 * @return the list of distinct prescriber names
	 */
	@Query("select distinct lab.prescriber from Laboratory lab where lab.prescriber is not null and lab.prescriber <> '' order by lab.prescriber asc")
	List<String> findDistinctPrescribers();

}
