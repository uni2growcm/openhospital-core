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

	public ReductionPlanManager(ReductionPlanIoOperations reductionPlanIoOperations) {
		this.reductionPlanIoOperations = reductionPlanIoOperations;
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
		List<ExamReduction> examReductionList = reductionPlanIoOperations.getExamReductionByReductionPlanId(reductionPlan.getId(), false);
		List<MedicalReduction> medicalReductionList = reductionPlanIoOperations.getMedicalReductionByReductionPlanId(reductionPlan.getId(), false);
		List<OperationReduction> operationReductionList = reductionPlanIoOperations.getOperationReductionByReductionPlanId(reductionPlan.getId(), false);
		List<PriceOtherReduction> priceOtherReductionList = reductionPlanIoOperations.getPriceOtherReductionByReductionPlanId(reductionPlan.getId(), false);

		if (!examReductionList.isEmpty()) {
			this.deleteBulkExamReduction(examReductionList);
		}
		if (!medicalReductionList.isEmpty()) {
			this.deleteBulkMedicalReduction(medicalReductionList);
		}
		if (!operationReductionList.isEmpty()) {
			this.deleteBulkOperationReduction(operationReductionList);
		}
		if (!priceOtherReductionList.isEmpty()) {
			this.deleteBulkPriceOtherReduction(priceOtherReductionList);
		}

		return reductionPlan;
	}

//	/**
//	 * save a {@link ExamReduction}
//	 * @param examReduction the {@link ExamReduction} to insert
//	 * @return the newly persisted {@link ExamReduction} object
//	 * @throws OHServiceException if the error happened during the save process
//	 */
//	public ExamReduction save(ExamReduction examReduction) throws OHServiceException {
//		return reductionPlanIoOperations.save(examReduction);
//	}

	/**
	 * fetch a list of {@link ExamReduction}s by {@link ReductionPlan} id
	 * @param reductionPlanId the {@link ExamReduction} id
	 * @return the list of {@link ExamReduction}s
	 * @throws OHServiceException if the error happened during the get process
	 */
	public List<ExamReduction> getExamReductionByReductionPlanId(int reductionPlanId, boolean deleted) throws OHServiceException {
		return reductionPlanIoOperations.getExamReductionByReductionPlanId(reductionPlanId, deleted);
	}

	/**
	 * delete an {@link ExamReduction}
	 * @param examReduction the {@link ExamReduction} you want to delete
	 * @throws OHServiceException if the error happen during the delete process
	 */
	public ExamReduction delete(ExamReduction examReduction) throws OHServiceException {
		return reductionPlanIoOperations.delete(examReduction);
	}

	/**
	 * Delete a list of {@link ExamReduction}s
	 * @param examReductionList the list of {@link ExamReduction}s to delete
	 * @throws OHServiceException if the error happen during the delete process
	 */
	public void deleteBulkExamReduction(List<ExamReduction> examReductionList) throws OHServiceException {
		for (ExamReduction examReduction : examReductionList) {
			delete(examReduction);
		}
	}

//	/**
//	 * Save a {@link MedicalReduction}
//	 * @param medicalReduction the {@link MedicalReduction} to insert
//	 * @return the newly persisted {@link MedicalReduction} object
//	 * @throws OHServiceException if an error happened during the save process
//	 */
//	public MedicalReduction save(MedicalReduction medicalReduction) throws OHServiceException {
//		return reductionPlanIoOperations.save(medicalReduction);
//	}

	/**
	 * fetch a list of {@link MedicalReduction}s by {@link ReductionPlan} id.
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @param deleted if get deleted data or not
	 * @return the list of {@link MedicalReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<MedicalReduction> getMedicalReductionByReductionPlanId(int reductionPlanId, boolean deleted) throws OHServiceException {
		return reductionPlanIoOperations.getMedicalReductionByReductionPlanId(reductionPlanId, deleted);
	}

	/**
	 * delete a {@link MedicalReduction}
	 * @param medicalReduction the {@link MedicalReduction} want to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public MedicalReduction delete(MedicalReduction medicalReduction) throws OHServiceException {
		return reductionPlanIoOperations.delete(medicalReduction);
	}

	/**
	 * Delete a list of {@link MedicalReduction}s
	 * @param medicalReductionList the list of {@link MedicalReduction}s to delete
	 * @throws OHServiceException if the error happen during the delete process
	 */
	public void deleteBulkMedicalReduction(List<MedicalReduction> medicalReductionList) throws OHServiceException {
		for (MedicalReduction medicalReduction : medicalReductionList) {
			delete(medicalReduction);
		}
	}

//	/**
//	 * Save an {@link OperationReduction}
//	 * @param operationReduction the {@link OperationReduction} to insert
//	 * @return the newly persisted {@link OperationReduction} object
//	 * @throws OHServiceException if an error happened during the save process
//	 */
//	public OperationReduction save(OperationReduction operationReduction) throws OHServiceException {
//		return reductionPlanIoOperations.save(operationReduction);
//	}

	/**
	 * fetch a list of {@link OperationReduction}s by {@link ReductionPlan}
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @param deleted if get deleted data or not
	 * @return the list of {@link OperationReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<OperationReduction> getOperationReductionByReductionPlanId(int reductionPlanId, boolean deleted) throws OHServiceException {
		return reductionPlanIoOperations.getOperationReductionByReductionPlanId(reductionPlanId, deleted);
	}

	/**
	 * Delete an {@link OperationReduction}
	 * @param operationReduction the {@link OperationReduction} want to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public OperationReduction delete(OperationReduction operationReduction) throws OHServiceException {
		return reductionPlanIoOperations.delete(operationReduction);
	}

	/**
	 * Delete a list of {@link OperationReduction}s
	 * @param operationReductionList the list of {@link OperationReduction}s to delete
	 * @throws OHServiceException if the error happen during the delete process
	 */
	public void deleteBulkOperationReduction(List<OperationReduction> operationReductionList) throws OHServiceException {
		for (OperationReduction operationReduction : operationReductionList) {
			delete(operationReduction);
		}
	}

//	/**
//	 * Save a {@link PriceOtherReduction}
//	 * @param priceOtherReduction the {@link PriceOtherReduction} to insert
//	 * @return the newly persisted {@link PriceOtherReduction} object
//	 * @throws OHServiceException if an error happened during the save process
//	 */
//	public PriceOtherReduction save(PriceOtherReduction priceOtherReduction) throws OHServiceException {
//		return reductionPlanIoOperations.save(priceOtherReduction);
//	}

	/**
	 * Fetch a list of {@link PriceOtherReduction}s by {@link ReductionPlan}
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @param deleted if get deleted data or not
	 * @return the list of {@link PriceOtherReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<PriceOtherReduction> getPriceOtherReductionByReductionPlanId(int reductionPlanId, boolean deleted) throws OHServiceException {
		return reductionPlanIoOperations.getPriceOtherReductionByReductionPlanId(reductionPlanId, deleted);
	}

	/**
	 * Delete a {@link PriceOtherReduction}
	 * @param priceOtherReduction the {@link PriceOtherReduction} want to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public PriceOtherReduction delete(PriceOtherReduction priceOtherReduction) throws OHServiceException {
		return reductionPlanIoOperations.delete(priceOtherReduction);
	}

	/**
	 * Delete a list of {@link PriceOtherReduction}s
	 * @param priceOtherReductionList the list of {@link PriceOtherReduction}s to delete
	 * @throws OHServiceException if the error happen during the delete process
	 */
	public void deleteBulkPriceOtherReduction(List<PriceOtherReduction> priceOtherReductionList) throws OHServiceException {
		for (PriceOtherReduction priceOtherReduction : priceOtherReductionList) {
			delete(priceOtherReduction);
		}
	}
}