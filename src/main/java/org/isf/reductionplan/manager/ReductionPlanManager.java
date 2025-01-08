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

import org.isf.reductionplan.model.ExamReduction;
import org.isf.reductionplan.model.MedicalReduction;
import org.isf.reductionplan.model.OperationReduction;
import org.isf.reductionplan.model.PriceOtherReduction;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.reductionplan.service.ReductionPlanIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

@Component
public class ReductionPlanManager {

	private final ReductionPlanIoOperations reductionPlanIoOperations;
	private final ExamReductionManager examReductionManager;
	private final MedicalReductionManager medicalReductionManager;
	private final OperationReductionManager operationReductionManager;
	private final PriceOtherReductionManager priceOtherReductionManager;

	public ReductionPlanManager(ReductionPlanIoOperations reductionPlanIoOperations, ExamReductionManager examReductionManager,
		MedicalReductionManager medicalReductionManager, OperationReductionManager operationReductionManager,
		PriceOtherReductionManager priceOtherReductionManager) {
		this.reductionPlanIoOperations = reductionPlanIoOperations;
		this.examReductionManager = examReductionManager;
		this.medicalReductionManager = medicalReductionManager;
		this.operationReductionManager = operationReductionManager;
		this.priceOtherReductionManager = priceOtherReductionManager;
	}

	/**
	 * Get all reduction plans
	 * @return The list of {@link ReductionPlan}s
	 * @throws OHServiceException When failed to get all reduction plans
	 */
	public List<ReductionPlan> getAll(boolean deleted) throws OHServiceException {
		return reductionPlanIoOperations.getAll(deleted);
	}

	/**
	 * Get not deleted reduction plans
	 * @return The list of {@link ReductionPlan}s not deleted
	 * @throws OHServiceException When failed to get not deleted {@link ReductionPlan}s
	 */
	public List<ReductionPlan> getNotDeleted() throws OHServiceException {
		return reductionPlanIoOperations.getNotDeleted();
	}

	/**
	 * Get  reduction plans by description
	 * @return The list of {@link ReductionPlan}s
	 * @throws OHServiceException When failed to get  reduction plans by description
	 */
	public List<ReductionPlan> getByDescription(String description, boolean deleted) throws OHServiceException {
		return reductionPlanIoOperations.getByDescription(description, deleted);
	}

	/**
	 * Save a {@link ReductionPlan}
	 * @param reductionPlan the {@link ReductionPlan} to insert
	 * @return the newly persisted {@link ReductionPlan} object
	 * @throws OHServiceException when failed to save {@link ReductionPlan}
	 */
	public ReductionPlan save(ReductionPlan reductionPlan) throws OHServiceException {
		return reductionPlanIoOperations.save(reductionPlan);
	}

	/**
	 * Update a {@link ReductionPlan}
	 * @param reductionPlan the {@link ReductionPlan} to update
	 * @return the update persisted {@link ReductionPlan} object
	 * @throws OHServiceException when failed to update {@link ReductionPlan}
	 */
	public ReductionPlan update(ReductionPlan reductionPlan) throws OHServiceException {
		return reductionPlanIoOperations.save(reductionPlan);
	}

	/**
	 * Delete a {@link ReductionPlan}
	 * @param reductionPlan the {@link ReductionPlan} to delete
	 * @throws OHServiceException when failed to delete {@link ReductionPlan}
	 */
	public ReductionPlan delete(ReductionPlan reductionPlan) throws OHServiceException {
		reductionPlan = reductionPlanIoOperations.delete(reductionPlan);
		List<ExamReduction> examReductionList = examReductionManager.getByReductionPlanId(reductionPlan.getId(), false);
		List<MedicalReduction> medicalReductionList = medicalReductionManager.getByReductionPlanId(reductionPlan.getId(), false);
		List<OperationReduction> operationReductionList = operationReductionManager.getByReductionPlanId(reductionPlan.getId(), false);
		List<PriceOtherReduction> priceOtherReductionList = priceOtherReductionManager.getByReductionPlanId(reductionPlan.getId(), false);

		if (!examReductionList.isEmpty()) {
			for (ExamReduction examReduction : examReductionList) {
				examReductionManager.delete(examReduction);
			}
		}
		if (!medicalReductionList.isEmpty()) {
			for (MedicalReduction medicalReduction : medicalReductionList) {
				medicalReductionManager.delete(medicalReduction);
			}
		}
		if (!operationReductionList.isEmpty()) {
			for (OperationReduction operationReduction : operationReductionList) {
				operationReductionManager.delete(operationReduction);
			}
		}
		if (!priceOtherReductionList.isEmpty()) {
			for (PriceOtherReduction priceOtherReduction : priceOtherReductionList) {
				priceOtherReductionManager.delete(priceOtherReduction);
			}
		}

		return reductionPlan;
	}
}