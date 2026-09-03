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
package org.isf.familyplanning.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.isf.familyplanning.model.FamilyPlanningMethod;
import org.isf.familyplanning.model.FamilyPlanningReason;
import org.isf.familyplanning.model.FamilyPlanningRecord;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class FamilyPlanningIoOperations {

	private final FamilyPlanningIoOperationRepository repository;

	public FamilyPlanningIoOperations(FamilyPlanningIoOperationRepository repository) {
		this.repository = repository;
	}

	public List<FamilyPlanningRecord> getByPatientId(int patientCode) {
		return repository.findByPatient_CodeOrderByVisitDateDesc(patientCode);
	}

	public List<FamilyPlanningRecord> getByDateRange(LocalDate dateFrom, LocalDate dateTo) {
		return repository.findByVisitDateBetweenOrderByVisitDateAsc(dateFrom, dateTo);
	}

	/**
	 * Returns a page of {@link FamilyPlanningRecord}s matching the given optional filters, most recent
	 * visit first.
	 *
	 * @param search free-text search on the patient's first/second name; {@code null}/blank for no filter.
	 * @param method the contraceptive method to filter by; {@code null} for any method.
	 * @param reason the visit reason to filter by; {@code null} for any reason.
	 * @param dateFrom the earliest visit date (inclusive); {@code null} for no lower bound.
	 * @param dateTo the latest visit date (inclusive); {@code null} for no upper bound.
	 * @param page the page number (0-based).
	 * @param size the page size.
	 * @return the matching page of {@link FamilyPlanningRecord}s.
	 */
	public Page<FamilyPlanningRecord> getFiltered(String search, FamilyPlanningMethod method, FamilyPlanningReason reason, LocalDate dateFrom,
					LocalDate dateTo, int page, int size) {
		return repository.findAllFiltered(search, method, reason, dateFrom, dateTo, PageRequest.of(page, size));
	}

	public Optional<FamilyPlanningRecord> getById(int id) {
		return repository.findById(id);
	}

	public FamilyPlanningRecord saveOrUpdate(FamilyPlanningRecord record) {
		return repository.save(record);
	}

	public void delete(FamilyPlanningRecord record) {
		repository.delete(record);
	}

	public boolean isCodePresent(int id) {
		return repository.existsById(id);
	}
}
