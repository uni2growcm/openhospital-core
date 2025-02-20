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

package org.isf.reductionplan.manager;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.generaldata.MessageBundle;
import org.isf.reductionplan.model.ExamReduction;
import org.isf.reductionplan.model.MedicalReduction;
import org.isf.reductionplan.model.OperationReduction;
import org.isf.reductionplan.model.PriceOtherReduction;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.reductionplan.service.ReductionPlanIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;
import java.util.ArrayList;

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
	public List<ReductionPlan> getAll() throws OHServiceException {
		return reductionPlanIoOperations.getAll();
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
	 * Validates an ExamReduction to ensure that it's exam is not associated with the exam of a saved ExamReduction.
	 *
	 * @param examReduction the ExamReduction to validate
	 * @param errorMessage the error message to include in the exception if validation fails
	 * @throws OHServiceException if a duplicate Exam is found in the ReductionPlan
	 */
	public void validateExamReduction(ExamReduction examReduction, String errorMessage) throws OHServiceException {
		boolean valid = getExamReductionByReductionPlanId(examReduction.getReductionPlan().getId())
			.stream()
			.noneMatch(er -> er.getExam().equals(examReduction.getExam())
				&& !er.equals(examReduction));
		if (!valid) {
			throw new OHServiceException(
				new OHExceptionMessage(MessageBundle.getMessage(errorMessage))
			);
		}
	}

	/**
	 * Validates an MedicalReduction to ensure that it's exam is not associated with the exam of a saved MedicalReduction.
	 *
	 * @param medicalReduction the MedicalReduction to validate
	 * @param errorMessage the error message to include in the exception if validation fails
	 * @throws OHServiceException if a duplicate Medical is found in the ReductionPlan
	 */
	public void validateMedicalReduction(MedicalReduction medicalReduction, String errorMessage) throws OHServiceException {
		boolean valid = getMedicalReductionByReductionPlanId(medicalReduction.getReductionPlan().getId())
			.stream()
			.noneMatch(mr -> mr.getMedical().equals(medicalReduction.getMedical())
				&& !mr.equals(medicalReduction));
		if (!valid) {
			throw new OHServiceException(
				new OHExceptionMessage(MessageBundle.getMessage(errorMessage))
			);
		}
	}

	/**
	 * Validates an OperationReduction to ensure that it's exam is not associated with the exam of a saved OperationReduction.
	 * @param operationReduction the OperationReduction to validate
	 * @param errorMessage the error message to include in the exception if validation fails
	 * @throws OHServiceException if a duplicate Operation is found in the ReductionPlan
	 */
	public void validateOperationReduction(OperationReduction operationReduction, String errorMessage) throws OHServiceException {
		boolean valid = getOperationReductionByReductionPlanId(operationReduction.getReductionPlan().getId())
			.stream()
			.noneMatch(or -> or.getOperation().equals(operationReduction.getOperation())
				&& !or.equals(operationReduction));
		if (!valid) {
			throw new OHServiceException(
				new OHExceptionMessage(MessageBundle.getMessage(errorMessage))
			);
		}
	}

	/**
	 * Validates an PriceOtherReduction to ensure that it's exam is not associated with the exam of a saved PriceOtherReduction.
	 *
	 * @param priceOtherReduction the PriceOtherReduction to validate
	 * @param errorMessage the error message to include in the exception if validation fails
	 * @throws OHServiceException if a duplicate Operation is found in the ReductionPlan
	 */
	public void validatePriceOtherReduction(PriceOtherReduction priceOtherReduction, String errorMessage) throws OHServiceException {
		boolean valid = getPriceOtherReductionByReductionPlanId(priceOtherReduction.getReductionPlan().getId())
			.stream()
			.noneMatch(pr -> pr.getPricesOthers().equals(priceOtherReduction.getPricesOthers())
				&& !pr.equals(priceOtherReduction));
		if (!valid) {
			throw new OHServiceException(
				new OHExceptionMessage(MessageBundle.getMessage(errorMessage))
			);
		}
	}

	/**
	 * validates reduction Items
	 * @param reductionPlan the {@link ReductionPlan} to insert
	 * @throws OHServiceException when failed to save {@link ReductionPlan}
	 */
	public void validateItems(ReductionPlan reductionPlan) throws OHServiceException{
		for (ExamReduction r : reductionPlan.getExamReductions()) {
			validateExamReduction(r, "Duplicate Exam found for this ReductionPlan");
			r.setReductionPlan(reductionPlan);
		}

		for (OperationReduction r : reductionPlan.getOperationReductions()) {
			validateOperationReduction(r, "Duplicate Operation found for this ReductionPlan");
			r.setReductionPlan(reductionPlan);
		}

		for (MedicalReduction r : reductionPlan.getMedicalReductions()) {
			validateMedicalReduction(r, "Duplicate Medical found for this ReductionPlan");
			r.setReductionPlan(reductionPlan);
		}

		for (PriceOtherReduction r : reductionPlan.getPriceOtherReductions()) {
			validatePriceOtherReduction(r, "Duplicate PriceOther found for this ReductionPlan");
			r.setReductionPlan(reductionPlan);
		}
	}

	 /**
	 * Save a {@link ReductionPlan}
	 * @param reductionPlan the {@link ReductionPlan} to insert
	 * @throws OHServiceException when failed to save {@link ReductionPlan}
	 */
	public void add(ReductionPlan reductionPlan) throws OHServiceException {
		validateItems(reductionPlan);
		reductionPlanIoOperations.add(reductionPlan);
	}

	/**
	 * Update a {@link ReductionPlan}
	 * @param reductionPlan the {@link ReductionPlan} to update
	 * @throws OHServiceException when failed to update {@link ReductionPlan}
	 */
	public void update(ReductionPlan reductionPlan) throws OHServiceException {
		validateItems(reductionPlan);
		reductionPlanIoOperations.add(reductionPlan);
	}

	/**
	 * Delete a {@link ReductionPlan}
	 * @param reductionPlan the {@link ReductionPlan} to delete
	 * @throws OHServiceException when failed to delete {@link ReductionPlan}
	 */
	public ReductionPlan delete(ReductionPlan reductionPlan) throws OHServiceException {
		List<ExamReduction> examReductionList = reductionPlan.getExamReductions();
		List<MedicalReduction> medicalReductionList = reductionPlan.getMedicalReductions();
		List<OperationReduction> operationReductionList = reductionPlan.getOperationReductions();
		List<PriceOtherReduction> priceOtherReductionList = reductionPlan.getPriceOtherReductions();

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
}