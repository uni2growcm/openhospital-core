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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.isf.pregnancy.model.Pregnancy;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PregnancyIoOperations {

	private final PregnancyIoOperationRepository repository;

	public PregnancyIoOperations(PregnancyIoOperationRepository repository) {
		this.repository = repository;
	}

	public List<Pregnancy> getByPatientId(int patientCode) {
		return repository.findByPatient_Code(patientCode);
	}

	public List<Pregnancy> getActiveByPatientId(int patientCode) {
		return repository.findByPatient_CodeAndActive(patientCode, 1);
	}

	public List<Pregnancy> getActive() {
		return repository.findByActiveOrderByLmpDesc(1);
	}

	/**
	 * Returns a page of {@link Pregnancy} matching the given optional filters, most recent LMP first.
	 *
	 * @param search free-text search on the patient's first/second name; {@code null}/blank for no filter.
	 * @param active {@code 1} for active only, {@code 0} for inactive only, {@code null} for either.
	 * @param dateFrom the earliest LMP date (inclusive); {@code null} for no lower bound.
	 * @param dateTo the latest LMP date (inclusive); {@code null} for no upper bound.
	 * @param page the page number (0-based).
	 * @param size the page size.
	 * @return the matching page of {@link Pregnancy}.
	 */
	public Page<Pregnancy> getFiltered(String search, Integer active, LocalDate dateFrom, LocalDate dateTo, int page, int size) {
		return repository.findAllFiltered(search, active, dateFrom, dateTo, PageRequest.of(page, size));
	}

	public Optional<Pregnancy> getById(int id) {
		return repository.findById(id);
	}

	public Pregnancy saveOrUpdate(Pregnancy pregnancy) {
		return repository.save(pregnancy);
	}

	public void delete(Pregnancy pregnancy) {
		repository.delete(pregnancy);
	}

	public boolean isCodePresent(int id) {
		return repository.existsById(id);
	}
}
