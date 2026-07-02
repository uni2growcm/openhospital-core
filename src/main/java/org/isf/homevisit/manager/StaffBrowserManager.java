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
package org.isf.homevisit.manager;

import jakarta.persistence.EntityNotFoundException;
import org.isf.homevisit.model.Staff;
import org.isf.homevisit.service.StaffIoOperations;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class StaffBrowserManager {

	private final StaffIoOperations ioOperations;

	public StaffBrowserManager(StaffIoOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	/**
	 * Returns all active staff members
	 * @return list of active staff
	 * @throws OHServiceException
	 */
	public List<Staff> getStaff() throws OHServiceException {
		return ioOperations.getAllActive();
	}

	/**
	 * Returns a staff member by id
	 * @param id staff id
	 * @return staff member
	 * @throws EntityNotFoundException if not found
	 * @throws OHServiceException
	 */
	public Staff getStaff(int id) throws OHServiceException {
		return ioOperations.getById(id)
			.orElseThrow(() -> new EntityNotFoundException(
				MessageBundle.formatMessage("angal.staff.notfound.msg", String.valueOf(id))
			));
	}

	/**
	 * Searches staff members by keyword
	 * @param keyword search keyword
	 * @return list of matching staff
	 * @throws OHServiceException
	 */
	public List<Staff> searchStaff(String keyword) throws OHServiceException {
		return ioOperations.search(keyword);
	}

	/**
	 * Saves a staff member (create or update)
	 * @param staff staff to save
	 * @return saved staff
	 * @throws OHServiceException
	 */
	public Staff saveStaff(Staff staff) throws OHServiceException {
		validateStaff(staff);
		return ioOperations.save(staff);
	}

	/**
	 * Soft deletes a staff member
	 * @param id staff id to delete
	 * @throws OHServiceException
	 */
	public void deleteStaff(int id) throws OHServiceException {
		ioOperations.softDelete(id);
	}

	/**
	 * Validates staff data
	 * @param staff staff to validate
	 * @throws OHDataValidationException
	 * @throws OHServiceException
	 */
	private void validateStaff(Staff staff) throws OHDataValidationException, OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (staff.getFirstName() == null || staff.getFirstName().trim().isEmpty()) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.staff.validation.firstname.required.msg")));
		}

		if (staff.getLastName() == null || staff.getLastName().trim().isEmpty()) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.staff.validation.lastname.required.msg")));
		}

		if (staff.getPosition() == null || staff.getPosition().trim().isEmpty()) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.staff.validation.position.required.msg")));
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}