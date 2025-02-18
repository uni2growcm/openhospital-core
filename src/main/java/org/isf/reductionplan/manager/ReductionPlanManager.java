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

		List<ExamReduction> examReductionList = reductionPlan.getExamReductionList();
		List<MedicalReduction> medicalReductionList = reductionPlan.getMedicalReductionList();
		List<OperationReduction> operationReductionList = reductionPlan.getOperationReductionList();
		List<PriceOtherReduction> priceOtherReductionList = reductionPlan.getPriceOtherReductionList();

		if (!examReductionList.isEmpty()) {
			for (ExamReduction examReduction : examReductionList) {
				examReduction.setReductionPlan(reductionPlan);
				reductionPlanIoOperations.saveExamReduction(examReduction);
			}
		}
		if (!medicalReductionList.isEmpty()) {
			for (MedicalReduction medicalReduction : medicalReductionList) {
				medicalReduction.setReductionPlan(reductionPlan);
				reductionPlanIoOperations.saveMedicalReduction(medicalReduction);
			}
		}
		if (!operationReductionList.isEmpty()) {
			for (OperationReduction operationReduction : operationReductionList) {
				operationReduction.setReductionPlan(reductionPlan);
				reductionPlanIoOperations.saveOperationReduction(operationReduction);
			}
		}
		if (!priceOtherReductionList.isEmpty()) {
			for (PriceOtherReduction priceOtherReduction : priceOtherReductionList) {
				priceOtherReduction.setReductionPlan(reductionPlan);
				reductionPlanIoOperations.savePriceOtherReduction(priceOtherReduction);
			}
		}

		ReductionPlan savedPlan;
		savedPlan = reductionPlanIoOperations.save(reductionPlan);
		return savedPlan;
	}

	/**
	 * Update a {@link ReductionPlan}
	 * @param reductionPlan the {@link ReductionPlan} to update
	 * @return the update persisted {@link ReductionPlan} object
	 * @throws OHServiceException when failed to update {@link ReductionPlan}
	 */
	public ReductionPlan update(ReductionPlan reductionPlan) throws OHServiceException {
		List<ExamReduction> examReductionList = reductionPlan.getExamReductionList();
		List<MedicalReduction> medicalReductionList = reductionPlan.getMedicalReductionList();
		List<OperationReduction> operationReductionList = reductionPlan.getOperationReductionList();
		List<PriceOtherReduction> priceOtherReductionList = reductionPlan.getPriceOtherReductionList();

		if (!examReductionList.isEmpty()) {
			for (ExamReduction examReduction : examReductionList) {
				examReduction.setReductionPlan(reductionPlan);
				reductionPlanIoOperations.saveExamReduction(examReduction);
			}
		}
		if (!medicalReductionList.isEmpty()) {
			for (MedicalReduction medicalReduction : medicalReductionList) {
				medicalReduction.setReductionPlan(reductionPlan);
				reductionPlanIoOperations.saveMedicalReduction(medicalReduction);
			}
		}
		if (!operationReductionList.isEmpty()) {
			for (OperationReduction operationReduction : operationReductionList) {
				operationReduction.setReductionPlan(reductionPlan);
				reductionPlanIoOperations.saveOperationReduction(operationReduction);
			}
		}
		if (!priceOtherReductionList.isEmpty()) {
			for (PriceOtherReduction priceOtherReduction : priceOtherReductionList) {
				priceOtherReduction.setReductionPlan(reductionPlan);
				reductionPlanIoOperations.savePriceOtherReduction(priceOtherReduction);
			}
		}

		return reductionPlanIoOperations.save(reductionPlan);
	}

	/**
	 * Delete a {@link ReductionPlan}
	 * @param reductionPlan the {@link ReductionPlan} to delete
	 * @throws OHServiceException when failed to delete {@link ReductionPlan}
	 */
	public ReductionPlan delete(ReductionPlan reductionPlan) throws OHServiceException {
		List<ExamReduction> examReductionList = reductionPlan.getExamReductionList();
		List<MedicalReduction> medicalReductionList = reductionPlan.getMedicalReductionList();
		List<OperationReduction> operationReductionList = reductionPlan.getOperationReductionList();
		List<PriceOtherReduction> priceOtherReductionList = reductionPlan.getPriceOtherReductionList();

		if (!examReductionList.isEmpty()) {
			examReductionList.clear();
		}
		if (!medicalReductionList.isEmpty()) {
			medicalReductionList.clear();
		}
		if (!operationReductionList.isEmpty()) {
			operationReductionList.clear();
		}
		if (!priceOtherReductionList.isEmpty()) {
			priceOtherReductionList.clear();
		}

		return reductionPlanIoOperations.delete(reductionPlan);
	}

	/**
	 * fetch a list of {@link ExamReduction}s by {@link ReductionPlan} id
	 * @param reductionPlanId the {@link ExamReduction} id
	 * @return the list of {@link ExamReduction}s
	 * @throws OHServiceException if the error happened during the get process
	 */
	public List<ExamReduction> getExamReductionByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return reductionPlanIoOperations.getExamReductionByReductionPlanId(reductionPlanId);
	}

	/**
	 * delete an {@link ExamReduction}
	 * @param examReduction the {@link ExamReduction} you want to delete
	 * @throws OHServiceException if the error happen during the delete process
	 */
	public void deleteExamReduction(ExamReduction examReduction) throws OHServiceException {
		reductionPlanIoOperations.deleteExamReduction(examReduction);
	}

	/**
	 * Delete a list of {@link ExamReduction}s
	 * @param examReductionList the list of {@link ExamReduction}s to delete
	 * @throws OHServiceException if the error happen during the delete process
	 */
	public void deleteBulkExamReduction(List<ExamReduction> examReductionList) throws OHServiceException {
		for (ExamReduction examReduction : examReductionList) {
			deleteExamReduction(examReduction);
		}
	}

	/**
	 * fetch a list of {@link MedicalReduction}s by {@link ReductionPlan} id.
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link MedicalReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<MedicalReduction> getMedicalReductionByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return reductionPlanIoOperations.getMedicalReductionByReductionPlanId(reductionPlanId);
	}

	/**
	 * delete a {@link MedicalReduction}
	 * @param medicalReduction the {@link MedicalReduction} want to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public void deleteMedicalReduction(MedicalReduction medicalReduction) throws OHServiceException {
		reductionPlanIoOperations.deleteMedicalReduction(medicalReduction);
	}

	/**
	 * Delete a list of {@link MedicalReduction}s
	 * @param medicalReductionList the list of {@link MedicalReduction}s to delete
	 * @throws OHServiceException if the error happen during the delete process
	 */
	public void deleteBulkMedicalReduction(List<MedicalReduction> medicalReductionList) throws OHServiceException {
		for (MedicalReduction medicalReduction : medicalReductionList) {
			deleteMedicalReduction(medicalReduction);
		}
	}

	/**
	 * fetch a list of {@link OperationReduction}s by {@link ReductionPlan}
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link OperationReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<OperationReduction> getOperationReductionByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return reductionPlanIoOperations.getOperationReductionByReductionPlanId(reductionPlanId);
	}

	/**
	 * Delete an {@link OperationReduction}
	 * @param operationReduction the {@link OperationReduction} want to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public void deleteOperationReduction(OperationReduction operationReduction) throws OHServiceException {
		reductionPlanIoOperations.deleteOperationReduction(operationReduction);
	}

	/**
	 * Delete a list of {@link OperationReduction}s
	 * @param operationReductionList the list of {@link OperationReduction}s to delete
	 * @throws OHServiceException if the error happen during the delete process
	 */
	public void deleteBulkOperationReduction(List<OperationReduction> operationReductionList) throws OHServiceException {
		for (OperationReduction operationReduction : operationReductionList) {
			deleteOperationReduction(operationReduction);
		}
	}

	/**
	 * Fetch a list of {@link PriceOtherReduction}s by {@link ReductionPlan}
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link PriceOtherReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<PriceOtherReduction> getPriceOtherReductionByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return reductionPlanIoOperations.getPriceOtherReductionByReductionPlanId(reductionPlanId);
	}

	/**
	 * Delete a {@link PriceOtherReduction}
	 * @param priceOtherReduction the {@link PriceOtherReduction} want to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public void deleteOtherReduction(PriceOtherReduction priceOtherReduction) throws OHServiceException {
		reductionPlanIoOperations.deleteOtherReduction(priceOtherReduction);
	}

	/**
	 * Delete a list of {@link PriceOtherReduction}s
	 * @param priceOtherReductionList the list of {@link PriceOtherReduction}s to delete
	 * @throws OHServiceException if the error happen during the delete process
	 */
	public void deleteBulkPriceOtherReduction(List<PriceOtherReduction> priceOtherReductionList) throws OHServiceException {
		for (PriceOtherReduction priceOtherReduction : priceOtherReductionList) {
			deleteOtherReduction(priceOtherReduction);
		}
	}
}