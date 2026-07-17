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
package org.isf.homevisit.service;

import jakarta.persistence.EntityNotFoundException;
import org.isf.homevisit.model.HomeVisit;
import org.isf.homevisit.model.HomeVisitStatus;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HomeVisitIoOperations {

	private final HomeVisitIoOperationRepository repository;

	public HomeVisitIoOperations(HomeVisitIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Returns all active home visits (paginated)
	 * @param pageable pagination information
	 * @return page of home visits
	 * @throws OHServiceException
	 */
	public Page<HomeVisit> getAllActive(Pageable pageable) throws OHServiceException {
		return repository.findAllActive(pageable);
	}

	/**
	 * Returns home visits for a specific patient
	 * @param patient the patient
	 * @return list of home visits for the patient
	 * @throws OHServiceException
	 */
	public List<HomeVisit> getByPatient(Patient patient) throws OHServiceException {
		return repository.findByPatientAndActive(patient, 1);
	}

	/**
	 * Returns home visit by id
	 * @param id home visit id
	 * @return Optional containing home visit if found and active
	 * @throws OHServiceException
	 */
	public Optional<HomeVisit> getById(int id) throws OHServiceException {
		return repository.findById(id).filter(hv -> hv.getActive() == 1);
	}

	/**
	 * Returns home visits by status (paginated)
	 * @param status visit status
	 * @param pageable pagination information
	 * @return page of home visits
	 * @throws OHServiceException
	 */
	public Page<HomeVisit> getByStatus(HomeVisitStatus status, Pageable pageable) throws OHServiceException {
		return repository.findByStatus(status, pageable);
	}

	/**
	 * Returns home visits within a date range (paginated)
	 * @param startDate start date (inclusive)
	 * @param endDate end date (inclusive)
	 * @param pageable pagination information
	 * @return page of home visits
	 * @throws OHServiceException
	 */
	public Page<HomeVisit> getByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) throws OHServiceException {
		return repository.findByDateRange(startDate, endDate, pageable);
	}

	/**
	 * Saves a home visit (create or update)
	 * @param homeVisit home visit to save
	 * @return saved home visit
	 * @throws OHServiceException
	 */
	public HomeVisit save(HomeVisit homeVisit) throws OHServiceException {
		return repository.save(homeVisit);
	}

	/**
	 * Updates the status of a home visit
	 * @param id home visit id
	 * @param status new status
	 * @throws OHServiceException
	 */
	@Transactional
	public void updateStatus(int id, HomeVisitStatus status) throws OHServiceException {
		HomeVisit homeVisit = repository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Home visit not found: " + id));

		homeVisit.setStatus(status);

		if (status == HomeVisitStatus.COMPLETED) {
			homeVisit.setVisitEndDate(LocalDateTime.now());
		}

		repository.save(homeVisit);
	}

	/**
	 * Soft deletes a home visit (sets active = 0)
	 * @param id home visit id to delete
	 * @throws OHServiceException
	 */
	@Transactional
	public void softDelete(int id) throws OHServiceException {
		repository.softDelete(id);
	}

	public Page<HomeVisit> getWithFilters(
		Integer code,
		HomeVisitStatus status,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		Character sex,
		Integer ageFrom,
		Integer ageTo,
		String searchText,
		Pageable pageable) throws OHServiceException {
		return repository.findWithFilters(code, status, dateFrom, dateTo, sex, ageFrom, ageTo, searchText, pageable);
	}

	public HomeVisitStatus getCurrentStatus(int id) {
		String statusStr = repository.findStatusStringById(id);
		if (statusStr == null) return null;
		try {
			return HomeVisitStatus.valueOf(statusStr);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}