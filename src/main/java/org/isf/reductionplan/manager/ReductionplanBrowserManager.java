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

package org.isf.reductionplan.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.reductionplan.service.ReductionPlanIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.vaccine.model.Vaccine;
import org.springframework.stereotype.Component;

@Component
public class ReductionplanBrowserManager {

	private final ReductionPlanIoOperations ioOperations;

	public ReductionplanBrowserManager(ReductionPlanIoOperations reductionPlanIoOperations) {
		this.ioOperations = reductionPlanIoOperations;
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any.
	 * @param reductionplan the {@link ReductionPlan object to validate
	 * @param insert {@code true} or updated {@code false}
	 * @throws OHServiceException
	 */
	public void validateReductionplan(ReductionPlan reductionplan) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		String description = reductionplan.getDescription();
		if (description == null || description.trim().isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.Theentereddescriptionisnotvalid.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Returns the list of {@link ReductionPlan}s in the DB.
	 * @return the list of {@link ReductionPlan}s
	 */
	public List<ReductionPlan> getReductionplan() throws OHServiceException {
		return ioOperations.getReductionplan();
	}

	public List<ReductionPlan> getReductionplan(String description) throws OHServiceException {
		return ioOperations.getReductionPlan(description);
	}
	public List<ReductionPlan> getReductionplanByIds(List<Integer> ids) throws OHServiceException {
		return ioOperations.findByIdIn(ids);
	}

	/**
	 * Inserts a new {@link ReductionPlan} into the DB.
	 * @param reductionplan - the {@link ReductionPlan} object to insert
	 * @return the newly inserted {@link Vaccine} object.
	 */
	public ReductionPlan newReductionplan(ReductionPlan reductionplan) throws OHServiceException {
		validateReductionplan(reductionplan);
		return ioOperations.newReductionPlan(reductionplan);
	}

	/**
	 * Updates the specified {@link ReductionPlan} object.
	 * @param reductionplan- the {@link ReductionPlan} object to update.
	 * @return the updated {@link ReductionPlan} object.
	 */
	public ReductionPlan updateReductionplan(ReductionPlan reductionplan) throws OHServiceException {
		validateReductionplan(reductionplan);
		return ioOperations.updateReductionPlan(reductionplan.getId(), reductionplan);
	}

	/**
	 * Deletes a {@link ReductionPlan} in the DB.
	 * @param reductionplan - the item to delete
	 */
	public void deleteReductionplan(ReductionPlan reductionplan) throws OHServiceException {
		ioOperations.deleteReductionplan(reductionplan);
	}

	/**
	 * Returns the {@link ReductionPlan} based on vaccine code.
	 * @param ids - the {@link ReductionPlan} code.
	 * @return the {@link ReductionPlan}
	 */
	public List<ReductionPlan> getById(List<Integer> ids) throws OHServiceException {
		return ioOperations.findByIdIn(ids);
	}

}