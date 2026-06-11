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

import org.isf.homevisit.model.Staff;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffIoOperations {

	private final StaffIoOperationRepository repository;

	public StaffIoOperations(StaffIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Returns all active staff members ordered by name
	 * @return list of active staff
	 * @throws OHServiceException
	 */
	public List<Staff> getAllActive() throws OHServiceException {
		return repository.findAllActive();
	}

	/**
	 * Returns a staff member by id
	 * @param id staff id
	 * @return Optional containing staff if found and active
	 * @throws OHServiceException
	 */
	public Optional<Staff> getById(int id) throws OHServiceException {
		return repository.findById(id).filter(s -> s.getActive() == 1);
	}

	/**
	 * Returns a staff member by code
	 * @param code staff code
	 * @return Optional containing staff if found and active
	 * @throws OHServiceException
	 */
	public Optional<Staff> getByCode(String code) throws OHServiceException {
		return repository.findByCodeAndActive(code, 1);
	}

	/**
	 * Searches active staff members by keyword (name or profession)
	 * @param keyword search keyword
	 * @return list of matching staff
	 * @throws OHServiceException
	 */
	public List<Staff> search(String keyword) throws OHServiceException {
		if (keyword == null || keyword.trim().isEmpty()) {
			return getAllActive();
		}
		return repository.searchActive(keyword.trim());
	}

	/**
	 * Saves a staff member (create or update)
	 * @param staff staff to save
	 * @return saved staff
	 * @throws OHServiceException
	 */
	public Staff save(Staff staff) throws OHServiceException {
		return repository.save(staff);
	}

	/**
	 * Soft deletes a staff member (sets active = 0)
	 * @param id staff id to delete
	 * @throws OHServiceException
	 */
	public void softDelete(int id) throws OHServiceException {
		repository.softDelete(id);
	}

	/**
	 * Checks if a staff code already exists for active staff
	 * @param code staff code to check
	 * @return true if exists, false otherwise
	 * @throws OHServiceException
	 */
	public boolean existsByCode(String code) throws OHServiceException {
		return repository.existsByCodeAndActive(code, 1);
	}
}