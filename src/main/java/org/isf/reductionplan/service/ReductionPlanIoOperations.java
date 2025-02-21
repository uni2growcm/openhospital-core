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

package org.isf.reductionplan.service;

import java.util.List;

import org.isf.reductionplan.model.ExamReduction;
import org.isf.reductionplan.model.MedicalReduction;
import org.isf.reductionplan.model.OperationReduction;
import org.isf.reductionplan.model.PriceOtherReduction;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class ReductionPlanIoOperations {

	private final ReductionplanRepository ReductionplanRepository;
	private final ExamReductionRepository ExamReductionRepository;
	private final MedicalReductionRepository MedicalReductionRepository;
	private final OperationReductionRepository OperationReductionRepository;
	private final PriceOtherReductionRepository PriceOtherReductionRepository;

	public ReductionPlanIoOperations(
		ReductionplanRepository reductionplanIoOperationRepository,
		ExamReductionRepository examReductionIoOperationsRepository,
		MedicalReductionRepository medicalReductionIoOperationRepository,
		OperationReductionRepository operationReductionIoOperationRepository,
		PriceOtherReductionRepository priceOtherReductionIoOperationRepository
	) {
		this.ReductionplanRepository = reductionplanIoOperationRepository;
		this.ExamReductionRepository = examReductionIoOperationsRepository;
		this.MedicalReductionRepository = medicalReductionIoOperationRepository;
		this.OperationReductionRepository = operationReductionIoOperationRepository;
		this.PriceOtherReductionRepository = priceOtherReductionIoOperationRepository;
	}

	/**
	 * Get all reduction plans
	 * @return The list of {@link ReductionPlan}s
	 * @throws OHServiceException When failed to get all reduction plans
	 */
	public List<ReductionPlan> getAll() throws OHServiceException {
		return ReductionplanRepository.findByDeleted(false);
	}

	/**
	 * Get not deleted reduction plans
	 * @return The list of {@link ReductionPlan}s not deleted
	 * @throws OHServiceException when failed to get not deleted {@link ReductionPlan}s
	 */
	public List<ReductionPlan> getNotDeleted() throws OHServiceException {
		return ReductionplanRepository.findByDeleted(false);
	}

	/**
	 * Get reduction plans by description
	 * @return The list of {@link ReductionPlan}s
	 * @throws OHServiceException When failed to get  reduction plans by description
	 */
	public List<ReductionPlan> getByDescription(String description, boolean deleted) throws OHServiceException {
		return ReductionplanRepository.findByDescriptionAndDeleted(description, deleted);
	}

	/**
	 * Save a {@link ReductionPlan}
	 * @param reductionPlan the {@link ReductionPlan} to insert
	 * @throws OHServiceException when failed to save {@link ReductionPlan}
	 */
	public void add(ReductionPlan reductionPlan) throws OHServiceException {
		ReductionplanRepository.save(reductionPlan);
	}

	/**
	 * Delete a {@link ReductionPlan}
	 * @param reductionPlan the {@link ReductionPlan} to delete
	 * @throws OHServiceException when failed to delete {@link ReductionPlan}
	 */
	public void delete(ReductionPlan reductionPlan) throws OHServiceException {
		reductionPlan = ReductionplanRepository.findByIdAndDeleted(reductionPlan.getId(), false);
		reductionPlan.setDeleted(true);
		ReductionplanRepository.save(reductionPlan);
	}

	/**
	 * fetch a list of {@link ExamReduction}s by {@link ReductionPlan} id
	 * @param reductionPlanId the {@link ExamReduction} id
	 * @return the list of {@link ExamReduction}s
	 * @throws OHServiceException if the error happened during the get process
	 */
	public List<ExamReduction> getExamReductionsByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return ExamReductionRepository.findByReductionPlanId(reductionPlanId);
	}

	/**
	 * delete an {@link ExamReduction}
	 * @param examReduction the {@link ExamReduction} to delete
	 * @throws OHServiceException if the error happened during the delete process
	 */
	public void deleteExamReduction(ExamReduction examReduction) throws OHServiceException {
		ExamReductionRepository.deleteById(examReduction.getId());
	}

	/**
	 * delete list of {@link ExamReduction}
	 * @param examReductionList the list of {@link ExamReduction} to delete
	 * @throws OHServiceException if the error happen during the delete process
	 */
	public void deleteExamReductions(List<ExamReduction> examReductionList) throws OHServiceException {
		ExamReductionRepository.deleteAll(examReductionList);
	}

	/**
	 * fetch a list of {@link MedicalReduction}s by {@link ReductionPlan} id.
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link MedicalReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<MedicalReduction> getMedicalReductionsByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return MedicalReductionRepository.findByReductionPlanId(reductionPlanId);
	}

	/**
	 * delete a {@link MedicalReduction}
	 * @param medicalReduction the {@link MedicalReduction} to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public void deleteMedicalReduction(MedicalReduction medicalReduction) throws OHServiceException {
		MedicalReductionRepository.deleteById(medicalReduction.getId());
	}

	/**
	 * delete a list of {@link MedicalReduction}
	 * @param medicalReductionList the list of {@link MedicalReduction} to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public void deleteMedicalReductions(List<MedicalReduction> medicalReductionList) throws OHServiceException {
		MedicalReductionRepository.deleteAll(medicalReductionList);
	}

	/**
	 * fetch a list of {@link OperationReduction}s by {@link ReductionPlan}
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link OperationReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<OperationReduction> getOperationReductionsByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return OperationReductionRepository.findByReductionPlanId(reductionPlanId);
	}

	/**
	 * Delete an {@link OperationReduction}
	 * @param operationReduction the {@link OperationReduction} to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public void deleteOperationReduction(OperationReduction operationReduction) throws OHServiceException {
		OperationReductionRepository.deleteById(operationReduction.getId());
	}

	/**
	 * Delete a list of {@link OperationReduction}
	 * @param operationReductionList the list of {@link OperationReduction} to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public void deleteOperationReductions(List<OperationReduction> operationReductionList) throws OHServiceException {
		OperationReductionRepository.deleteAll(operationReductionList);
	}

	/**
	 * Fetch a list of {@link PriceOtherReduction}s by {@link ReductionPlan}
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link PriceOtherReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<PriceOtherReduction> getPriceOtherReductionsByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return PriceOtherReductionRepository.findByReductionPlanId(reductionPlanId);
	}

	/**
	 * Delete a {@link PriceOtherReduction}
	 * @param priceOtherReduction the {@link PriceOtherReduction} to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public void deletePriceOtherReduction(PriceOtherReduction priceOtherReduction) throws OHServiceException {
		 PriceOtherReductionRepository.deleteById(priceOtherReduction.getId());
	}

	/**
	 * Delete a list {@link PriceOtherReduction}
	 * @param priceOtherReductionList the list of {@link PriceOtherReduction} to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public void deletePriceOtherReductions(List<PriceOtherReduction> priceOtherReductionList) throws OHServiceException{
		PriceOtherReductionRepository.deleteAll(priceOtherReductionList);
	}
}