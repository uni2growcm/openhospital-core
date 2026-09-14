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
package org.isf.hivchildfollowup.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.isf.hivchildfollowup.model.HivExposedChild;
import org.isf.hivchildfollowup.model.HivExposedChildStatus;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class HivExposedChildIoOperations {

	private final HivExposedChildIoOperationRepository repository;

	public HivExposedChildIoOperations(HivExposedChildIoOperationRepository repository) {
		this.repository = repository;
	}

	public List<HivExposedChild> getByMotherPatientId(int motherPatientCode) {
		return repository.findByMotherPatient_CodeOrderByDateOfBirthDesc(motherPatientCode);
	}

	public List<HivExposedChild> getAll() {
		return repository.findAllByOrderByDateOfBirthDesc();
	}

	/**
	 * Returns a page of {@link HivExposedChild} matching the given optional filters, most recent date of
	 * birth first.
	 *
	 * @param search free-text search on the mother's first/second name or the child's name; {@code null}/blank for no filter.
	 * @param status the follow-up status to filter by; {@code null} for any status.
	 * @param dateFrom the earliest date of birth (inclusive); {@code null} for no lower bound.
	 * @param dateTo the latest date of birth (inclusive); {@code null} for no upper bound.
	 * @param page the page number (0-based).
	 * @param size the page size.
	 * @return the matching page of {@link HivExposedChild}.
	 */
	public Page<HivExposedChild> getFiltered(String search, HivExposedChildStatus status, LocalDate dateFrom, LocalDate dateTo, int page, int size) {
		return repository.findAllFiltered(search, status, dateFrom, dateTo, PageRequest.of(page, size));
	}

	public Optional<HivExposedChild> getById(int id) {
		return repository.findById(id);
	}

	public HivExposedChild saveOrUpdate(HivExposedChild child) {
		return repository.save(child);
	}

	public void delete(HivExposedChild child) {
		repository.delete(child);
	}
}
