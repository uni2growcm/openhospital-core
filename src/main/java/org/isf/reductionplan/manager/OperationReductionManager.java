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

import java.util.List;

import org.isf.reductionplan.model.MedicalReduction;
import org.isf.reductionplan.model.OperationReduction;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.reductionplan.service.OperationReductionIoOperation;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

@Component
public class OperationReductionManager {
	public final OperationReductionIoOperation operationReductionIoOperation;

	public OperationReductionManager(OperationReductionIoOperation operationReductionIoOperation) {
		this.operationReductionIoOperation = operationReductionIoOperation;
	}

	/**
	 * Save an {@link OperationReduction}
	 * @param operationReduction the {@link OperationReduction} to insert
	 * @return the newly persisted {@link OperationReduction} object
	 * @throws OHServiceException if an error happened during the save process
	 */
	public OperationReduction save(OperationReduction operationReduction) throws OHServiceException {
		return operationReductionIoOperation.save(operationReduction);
	}

	/**
	 * fetch a list of {@link OperationReduction}s by {@link ReductionPlan}
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @param deleted
	 * @return the list of {@link OperationReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<OperationReduction> getByReductionPlanId(int reductionPlanId, boolean deleted) throws OHServiceException {
		return operationReductionIoOperation.getByReductionPlan(reductionPlanId, deleted);
	}

	/**
	 * Delete an {@link OperationReduction}
	 * @param operationReduction the {@link OperationReduction} want to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public OperationReduction delete(OperationReduction operationReduction) throws OHServiceException {
		return operationReductionIoOperation.delete(operationReduction);
	}

	/**
	 * Delete a list of {@link OperationReduction}s
	 * @param operationReductionList the list of {@link OperationReduction}s to delete
	 * @throws OHServiceException if the error happen during the delete process
	 */
	public void deleteBulk(List<OperationReduction> operationReductionList) throws OHServiceException {
		for (OperationReduction operationReduction : operationReductionList) {
			delete(operationReduction);
		}
	}
}
