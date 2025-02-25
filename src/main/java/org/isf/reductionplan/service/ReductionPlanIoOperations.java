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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.isf.generaldata.MessageBundle;

import org.isf.exa.model.Exam;
import org.isf.medicals.model.Medical;
import org.isf.operation.model.Operation;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.reductionplan.model.ExamReduction;
import org.isf.reductionplan.model.MedicalReduction;
import org.isf.reductionplan.model.OperationReduction;
import org.isf.reductionplan.model.PriceOtherReduction;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class ReductionPlanIoOperations {

	private final ReductionPlanRepository reductionPlanRepository;
	private final ExamReductionRepository examReductionRepository;
	private final MedicalReductionRepository medicalReductionRepository;
	private final OperationReductionRepository operationReductionRepository;
	private final PriceOtherReductionRepository priceOtherReductionRepository;

	public ReductionPlanIoOperations(
		ReductionPlanRepository reductionPlanIoOperationRepository,
		ExamReductionRepository examReductionIoOperationsRepository,
		MedicalReductionRepository medicalReductionIoOperationRepository,
		OperationReductionRepository operationReductionIoOperationRepository,
		PriceOtherReductionRepository priceOtherReductionIoOperationRepository
	) {
		this.reductionPlanRepository = reductionPlanIoOperationRepository;
		this.examReductionRepository = examReductionIoOperationsRepository;
		this.medicalReductionRepository = medicalReductionIoOperationRepository;
		this.operationReductionRepository = operationReductionIoOperationRepository;
		this.priceOtherReductionRepository = priceOtherReductionIoOperationRepository;
	}

	/**
	 * Get all reduction plans
	 * @return The list of {@link ReductionPlan}s
	 * @throws OHServiceException When failed to get all reduction plans
	 */
	public List<ReductionPlan> getAll() throws OHServiceException {
		return reductionPlanRepository.findByDeleted(false);
	}

	/**
	 * Get a {@link ReductionPlan} by ID
	 * @param id {@link ReductionPlan}'s ID
	 * @return a {@link ReductionPlan}
	 * @throws OHServiceException When failed to get  reduction plans by description
	 */
	public ReductionPlan getById(int id) throws OHServiceException {
		return reductionPlanRepository.findByIdAndDeleted(id, false);
	}

	/**
	 * Get reduction plans by description
	 * @param description {@link ReductionPlan}'s description to be used
	 * @return The list of {@link ReductionPlan}s
	 * @throws OHServiceException When failed to get  reduction plans by description
	 */
	public List<ReductionPlan> getByDescription(String description) throws OHServiceException {
		return reductionPlanRepository.findByDescriptionAndDeleted(description, false);
	}

	/**
	 * Validates a {@link ReductionPlan} making sure no items properties are duplicated
	 * @param reductionPlan the {@link ReductionPlan} to add
	 */
	public List<OHDataValidationException> validateReductionPlan(ReductionPlan reductionPlan) {
		List<OHDataValidationException> errors = new ArrayList<>();

		if (reductionPlan.getExamReductions() != null && !reductionPlan.getExamReductions().isEmpty()) {
			Set<Exam> duplicates = reductionPlan.getExamReductions().stream()
				.collect(Collectors.groupingBy(ExamReduction::getExam, Collectors.counting()))
				.entrySet().stream()
				.filter(entry -> entry.getValue() > 1)
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());

			if (!duplicates.isEmpty()) {
				errors.add(
					new OHDataValidationException(new OHExceptionMessage("angal.reductionplan.duplicateexamfound.msg")
				));
			}
		}

		if (reductionPlan.getOperationReductions() != null && !reductionPlan.getOperationReductions().isEmpty()) {
			Set<Medical> duplicates = reductionPlan.getMedicalReductions().stream()
				.collect(Collectors.groupingBy(MedicalReduction::getMedical, Collectors.counting()))
				.entrySet().stream()
				.filter(entry -> entry.getValue() > 1)
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());

			if (!duplicates.isEmpty()) {
				errors.add(
					new OHDataValidationException(new OHExceptionMessage("angal.reductionplan.duplicatemedicalfound.msg"))
				);
			}
		}

		if (reductionPlan.getMedicalReductions() != null && !reductionPlan.getMedicalReductions().isEmpty()) {
			Set<Operation> duplicates = reductionPlan.getOperationReductions().stream()
				.collect(Collectors.groupingBy(OperationReduction::getOperation, Collectors.counting()))
				.entrySet().stream()
				.filter(entry -> entry.getValue() > 1)
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());

			if (!duplicates.isEmpty()) {
				errors.add(
					new OHDataValidationException(new OHExceptionMessage("angal.reductionplan.duplicateoperationfound.msg"))
				);
			}
		}

		if (reductionPlan.getPriceOtherReductions() != null && !reductionPlan.getPriceOtherReductions().isEmpty()) {
			Set<PricesOthers> duplicates = reductionPlan.getPriceOtherReductions().stream()
				.collect(Collectors.groupingBy(PriceOtherReduction::getPricesOthers, Collectors.counting()))
				.entrySet().stream()
				.filter(entry -> entry.getValue() > 1)
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());

			if (!duplicates.isEmpty()) {
				errors.add(
					new OHDataValidationException(new OHExceptionMessage("angal.reductionplan.duplicatepriceotherfound.msg"))
				);
			}
		}

		return errors;
	}

	/**
	 * Save a {@link ReductionPlan}
	 * @param reductionPlan the {@link ReductionPlan} to insert
	 * @throws OHServiceException when failed to save {@link ReductionPlan}
	 */
	public void add(ReductionPlan reductionPlan) throws OHServiceException{
		List<OHDataValidationException> errors = validateReductionPlan(reductionPlan);

		if (!errors.isEmpty()) {
			String combinedMessage = errors.stream()
				.flatMap(e -> e.getMessages().stream().map(OHExceptionMessage::getMessage))
				.collect(Collectors.joining("; "));
			throw new OHDataValidationException(new OHExceptionMessage(combinedMessage));
		}

		reductionPlanRepository.save(reductionPlan);
	}

	/**
	 * Delete a {@link ReductionPlan}
	 * @param reductionPlan the {@link ReductionPlan} to delete
	 * @throws OHServiceException when failed to delete {@link ReductionPlan}
	 */
	public void delete(ReductionPlan reductionPlan) throws OHServiceException {
		reductionPlan = reductionPlanRepository.findByIdAndDeleted(reductionPlan.getId(), false);
		reductionPlan.setDeleted(true);
		reductionPlanRepository.save(reductionPlan);
	}

	/**
	 * fetch a list of {@link ExamReduction}s by {@link ReductionPlan} id
	 * @param reductionPlanId the {@link ExamReduction} id
	 * @return the list of {@link ExamReduction}s
	 * @throws OHServiceException if the error happened during the get process
	 */
	public List<ExamReduction> getExamReductionsByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return examReductionRepository.findByReductionPlanId(reductionPlanId);
	}

	/**
	 * fetch a list of {@link MedicalReduction}s by {@link ReductionPlan} id.
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link MedicalReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<MedicalReduction> getMedicalReductionsByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return medicalReductionRepository.findByReductionPlanId(reductionPlanId);
	}

	/**
	 * fetch a list of {@link OperationReduction}s by {@link ReductionPlan}
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link OperationReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<OperationReduction> getOperationReductionsByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return operationReductionRepository.findByReductionPlanId(reductionPlanId);
	}

	/**
	 * Fetch a list of {@link PriceOtherReduction}s by {@link ReductionPlan}
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link PriceOtherReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<PriceOtherReduction> getPriceOtherReductionsByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return priceOtherReductionRepository.findByReductionPlanId(reductionPlanId);
	}
}