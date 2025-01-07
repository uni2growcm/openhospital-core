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
	 * @return the list of {@link OperationReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<OperationReduction> getByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return operationReductionIoOperation.getByReductionPlan(reductionPlanId);
	}

	/**
	 * Delete an {@link OperationReduction}
	 * @param operationReduction the {@link OperationReduction} want to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public void delete(OperationReduction operationReduction) throws OHServiceException {
		operationReductionIoOperation.delete(operationReduction);
	}
}
