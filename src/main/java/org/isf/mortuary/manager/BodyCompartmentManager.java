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

package org.isf.mortuary.manager;

import java.util.List;

import org.isf.mortuary.model.BodyCompartment;
import org.isf.mortuary.service.BodyCompartmentIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class BodyCompartmentManager {

	private final BodyCompartmentIoOperations bodyCompartmentIoOperations;

	public BodyCompartmentManager(BodyCompartmentIoOperations deathReasonIoOperations) {
		this.bodyCompartmentIoOperations = deathReasonIoOperations;
	}

	/**
	 * Store the specified {@link BodyCompartment}.
	 * @param bodyCompartment specific BodyCompartment to store.
	 * @return {@link BodyCompartment}.
	 * @throws OHServiceException
	 */
	public BodyCompartment add(BodyCompartment bodyCompartment) throws OHServiceException {
		return bodyCompartmentIoOperations.add(bodyCompartment);
	}

	/**
	 * Deletes a {@link BodyCompartment} in the DB.
	 * @param bodyCompartment - the item to delete
	 * return true if deletion works and false otherwise
	 * @throws OHServiceException
	 */
	public boolean delete(BodyCompartment bodyCompartment) throws OHServiceException {
		return bodyCompartmentIoOperations.delete(bodyCompartment);
	}

	/**
	 * Updates the specified {@link BodyCompartment}.
	 * @param bodyCompartment - the {@link BodyCompartment} to update.
	 * @return bodyCompartment that has been updated.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public BodyCompartment update(BodyCompartment bodyCompartment) throws OHServiceException {
		return bodyCompartmentIoOperations.update(bodyCompartment);
	}

	/**
	 * Checks if the label exist.
	 * @param label - the {@link BodyCompartment} label
	 * @return {@label true} if the label is present in the database, {@label false} otherwise
	 * @throws OHServiceException
	 */
	public boolean isLabelPresent(String label) throws OHServiceException {
		return bodyCompartmentIoOperations.isLabelPresent(label);
	}

	/**
	 * Returns the page of {@link BodyCompartment} based on label
	 *
	 * @param label - the label, must not be {@literal null}
	 * @param description - the description, must not be {@literal null}
	 * @param page current page.
	 * @param size the size of the page.
	 * @return the page of {@link BodyCompartment}
	 * @throws OHServiceException if {@label label} is {@literal null}
	 */
	public Page<BodyCompartment> getByLabelOrDescriptionPageable(String label, String description ,int page, int size) throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
		return bodyCompartmentIoOperations.getByLabelOrDescriptionPageable(label, description,pageable);
	}

	public List<BodyCompartment> getBodyCompartments() {
		return bodyCompartmentIoOperations.getBodyCompartments();
	}
}