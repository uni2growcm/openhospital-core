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

	private final ReductionplanIoOperationRepository reductionplanIoOperationRepository;
	private final ExamReductionIoOperationsRepository examReductionIoOperationsRepository;
	private final MedicalReductionIoOperationRepository medicalReductionIoOperationRepository;
	private final OperationReductionIoOperationRepository operationReductionIoOperationRepository;
	private final PriceOtherReductionIoOperationRepository PriceOtherReductionIoOperationRepository;

	public ReductionPlanIoOperations(
		ReductionplanIoOperationRepository reductionplanIoOperationRepository,
		ExamReductionIoOperationsRepository examReductionIoOperationsRepository,
		MedicalReductionIoOperationRepository medicalReductionIoOperationRepository,
		OperationReductionIoOperationRepository operationReductionIoOperationRepository,
		PriceOtherReductionIoOperationRepository priceOtherReductionIoOperationRepository
	) {
		this.reductionplanIoOperationRepository = reductionplanIoOperationRepository;
		this.examReductionIoOperationsRepository = examReductionIoOperationsRepository;
		this.medicalReductionIoOperationRepository = medicalReductionIoOperationRepository;
		this.operationReductionIoOperationRepository = operationReductionIoOperationRepository;
		this.PriceOtherReductionIoOperationRepository = priceOtherReductionIoOperationRepository;
	}

	/**
	 * Get all reduction plans
	 * @return The list of {@link ReductionPlan}s
	 * @throws OHServiceException When failed to get all reduction plans
	 */
	public List<ReductionPlan> getAll() throws OHServiceException {
		return reductionplanIoOperationRepository.findByDeleted(false);
	}

	/**
	 * Get not deleted reduction plans
	 * @return The list of {@link ReductionPlan}s not deleted
	 * @throws OHServiceException when failed to get not deleted {@link ReductionPlan}s
	 */
	public List<ReductionPlan> getNotDeleted() throws OHServiceException {
		return reductionplanIoOperationRepository.findByDeleted(false);
	}

	/**
	 * Get reduction plans by description
	 * @return The list of {@link ReductionPlan}s
	 * @throws OHServiceException When failed to get  reduction plans by description
	 */
	public List<ReductionPlan> getByDescription(String description, boolean deleted) throws OHServiceException {
		return reductionplanIoOperationRepository.findByDescriptionAndDeleted(description, deleted);
	}

	/**
	 * Save a {@link ReductionPlan}
	 * @param reductionPlan the {@link ReductionPlan} to insert
	 * @throws OHServiceException when failed to save {@link ReductionPlan}
	 */
	public void add(ReductionPlan reductionPlan) throws OHServiceException {
		reductionplanIoOperationRepository.save(reductionPlan);
	}

	/**
	 * Delete a {@link ReductionPlan}
	 * @param reductionPlan the {@link ReductionPlan} to delete
	 * @throws OHServiceException when failed to delete {@link ReductionPlan}
	 */
	public ReductionPlan delete(ReductionPlan reductionPlan) throws OHServiceException {
		reductionPlan = reductionplanIoOperationRepository.findByIdAndDeleted(reductionPlan.getId(), false);
		reductionPlan.setDeleted(true);
		return reductionplanIoOperationRepository.save(reductionPlan);
	}

	/**
	 * fetch a list of {@link ExamReduction}s by {@link ReductionPlan} id
	 * @param reductionPlanId the {@link ExamReduction} id
	 * @return the list of {@link ExamReduction}s
	 * @throws OHServiceException if the error happened during the get process
	 */
	public List<ExamReduction> getExamReductionByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return examReductionIoOperationsRepository.findByReductionPlanId(reductionPlanId);
	}

	/**
	 * delete an {@link ExamReduction}
	 * @param examReduction the {@link ExamReduction} you want to delete
	 * @throws OHServiceException if the error happened during the delete process
	 */
	public void deleteExamReduction(ExamReduction examReduction) throws OHServiceException {
		examReductionIoOperationsRepository.deleteById(examReduction.getId());
	}

	/**
	 * fetch a list of {@link MedicalReduction}s by {@link ReductionPlan} id.
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link MedicalReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<MedicalReduction> getMedicalReductionByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return medicalReductionIoOperationRepository.findByReductionPlanId(reductionPlanId);
	}

	/**
	 * delete a {@link MedicalReduction}
	 * @param medicalReduction the {@link MedicalReduction} want to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public void deleteMedicalReduction(MedicalReduction medicalReduction) throws OHServiceException {
		medicalReductionIoOperationRepository.deleteById(medicalReduction.getId());
	}

	/**
	 * fetch a list of {@link OperationReduction}s by {@link ReductionPlan}
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link OperationReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<OperationReduction> getOperationReductionByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return operationReductionIoOperationRepository.findByReductionPlanId(reductionPlanId);
	}

	/**
	 * Delete an {@link OperationReduction}
	 * @param operationReduction the {@link OperationReduction} want to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public void deleteOperationReduction(OperationReduction operationReduction) throws OHServiceException {
		operationReductionIoOperationRepository.deleteById(operationReduction.getId());
	}

	/**
	 * Fetch a list of {@link PriceOtherReduction}s by {@link ReductionPlan}
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link PriceOtherReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<PriceOtherReduction> getPriceOtherReductionByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return PriceOtherReductionIoOperationRepository.findByReductionPlanId(reductionPlanId);
	}

	/**
	 * Delete a {@link PriceOtherReduction}
	 * @param priceOtherReduction the {@link PriceOtherReduction} want to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public void deleteOtherReduction(PriceOtherReduction priceOtherReduction) throws OHServiceException {
		 PriceOtherReductionIoOperationRepository.deleteById(priceOtherReduction.getId());
	}
}