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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.isf.exa.model.Exam;
import org.isf.generaldata.MessageBundle;
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
		ReductionPlanRepository reductionPlanRepository,
		ExamReductionRepository examReductionRepository,
		MedicalReductionRepository medicalReductionRepository,
		OperationReductionRepository operationReductionRepository,
		PriceOtherReductionRepository priceOtherReductionRepository
	) {
		this.reductionPlanRepository = reductionPlanRepository;
		this.examReductionRepository = examReductionRepository;
		this.medicalReductionRepository = medicalReductionRepository;
		this.operationReductionRepository = operationReductionRepository;
		this.priceOtherReductionRepository = priceOtherReductionRepository;
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
	 * @throws OHServiceException When failed to get reduction plan by id
	 */
	public ReductionPlan getById(int id) throws OHServiceException {
		return reductionPlanRepository.findByIdAndDeleted(id, false);
	}

	/**
	 * Get reduction plans by description
	 * @param description {@link ReductionPlan}'s description to be used
	 * @return The list of {@link ReductionPlan}s
	 * @throws OHServiceException When failed to get reduction plans by description
	 */
	public List<ReductionPlan> getByDescription(String description) throws OHServiceException {
		return reductionPlanRepository.findByDescriptionAndDeleted(description, false);
	}

	/**
	 * Validates a {@link ReductionPlan} making sure no item's properties are duplicated and every rate is valid
	 * @param reductionPlan the {@link ReductionPlan} to add
	 * @return a list of OHExceptionMessage containing any validation errors
	 */
	public List<OHExceptionMessage> validateReductionPlan(ReductionPlan reductionPlan) {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (reductionPlan.getExamReductions() != null && !reductionPlan.getExamReductions().isEmpty()) {
			Set<Exam> duplicates = reductionPlan.getExamReductions().stream()
				.collect(Collectors.groupingBy(ExamReduction::getExam, Collectors.counting()))
				.entrySet().stream()
				.filter(entry -> entry.getValue() > 1)
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());

			if (!duplicates.isEmpty()) {
				errors.add(
					new OHExceptionMessage(MessageBundle.getMessage("angal.reductionplan.duplicateexamfound.msg"))
				);
			}
		}

		if (reductionPlan.getMedicalReductions() != null && !reductionPlan.getMedicalReductions().isEmpty()) {
			Set<Medical> duplicates = reductionPlan.getMedicalReductions().stream()
				.collect(Collectors.groupingBy(MedicalReduction::getMedical, Collectors.counting()))
				.entrySet().stream()
				.filter(entry -> entry.getValue() > 1)
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());

			if (!duplicates.isEmpty()) {
				errors.add(
					new OHExceptionMessage(MessageBundle.getMessage("angal.reductionplan.duplicatemedicalfound.msg"))
				);
			}
		}

		if (reductionPlan.getOperationReductions() != null && !reductionPlan.getOperationReductions().isEmpty()) {
			Set<Operation> duplicates = reductionPlan.getOperationReductions().stream()
				.collect(Collectors.groupingBy(OperationReduction::getOperation, Collectors.counting()))
				.entrySet().stream()
				.filter(entry -> entry.getValue() > 1)
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());

			if (!duplicates.isEmpty()) {
				errors.add(
					new OHExceptionMessage(MessageBundle.getMessage(
						"angal.reductionplan.duplicateoperationfound.msg"))
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
					new OHExceptionMessage(MessageBundle.getMessage(
						"angal.reductionplan.duplicatepriceotherfound.msg"))
				);
			}
		}

		errors.addAll(validateReductionRates(reductionPlan));

		return errors;
	}

	/**
	 * Validates that an item-level reduction rate is greater than 0.00 and less than or equal to 100.00.
	 * @param rate the rate to validate
	 * @return true if valid, false if not
	 */
	private boolean isValidRate(BigDecimal rate) {
		return rate.compareTo(BigDecimal.ZERO) > 0 && rate.compareTo(BigDecimal.valueOf(100)) <= 0;
	}

	/**
	 * Validates that a {@link ReductionPlan} global category rate is between 0.00 and 100.00 inclusive.
	 * @param rate the rate to validate
	 * @return true if valid, false if not
	 */
	private boolean isValidGlobalRate(BigDecimal rate) {
		return rate.compareTo(BigDecimal.ZERO) >= 0 && rate.compareTo(BigDecimal.valueOf(100)) <= 0;
	}

	/**
	 * Validates a collection of item-level reduction rates.
	 * @param reductions the item reductions whose rate is to be validated
	 * @param rateExtractor extracts the rate from an item reduction
	 * @param errorMsg the message to report if any rate is invalid
	 * @return a list of OHExceptionMessage containing any validation errors
	 */
	private <T> List<OHExceptionMessage> validateRates(Collection<T> reductions,
		Function<T, BigDecimal> rateExtractor, String errorMsg) {
		if (reductions == null || reductions.isEmpty()) {
			return Collections.emptyList();
		}
		boolean anyInvalid = reductions.stream()
			.anyMatch(r -> !isValidRate(rateExtractor.apply(r)));
		if (anyInvalid) {
			return Collections.singletonList(new OHExceptionMessage(errorMsg));
		}
		return Collections.emptyList();
	}

	/**
	 * Validates the {@link ReductionPlan}'s global category rates and its item-level exception rates.
	 * @param reductionPlan the {@link ReductionPlan} to validate
	 * @return a list of OHExceptionMessage containing any validation errors
	 */
	public List<OHExceptionMessage> validateReductionRates(ReductionPlan reductionPlan) {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (!isValidGlobalRate(reductionPlan.getExamRate())
			|| !isValidGlobalRate(reductionPlan.getMedicalRate())
			|| !isValidGlobalRate(reductionPlan.getOperationRate())
			|| !isValidGlobalRate(reductionPlan.getOtherRate())) {
			errors.add(
				new OHExceptionMessage(MessageBundle.getMessage("angal.reductionplan.invalidglobalreductionrate.msg"))
			);
		}

		errors.addAll(validateRates(
			reductionPlan.getExamReductions(),
			ExamReduction::getReductionRate,
			MessageBundle.getMessage("angal.reductionplan.oneormoreinvalidexamreductionrate.msg"))
		);

		errors.addAll(validateRates(
			reductionPlan.getMedicalReductions(),
			MedicalReduction::getReductionRate,
			MessageBundle.getMessage("angal.reductionplan.oneormoreinvalidmedicalreductionrate.msg"))
		);

		errors.addAll(validateRates(
			reductionPlan.getOperationReductions(),
			OperationReduction::getReductionRate,
			MessageBundle.getMessage("angal.reductionplan.oneormoreinvalidoperationreductionrate.msg"))
		);

		errors.addAll(validateRates(
			reductionPlan.getPriceOtherReductions(),
			PriceOtherReduction::getReductionRate,
			MessageBundle.getMessage("angal.reductionplan.oneormoreinvalidotherreductionrate.msg"))
		);

		return errors;
	}

	/**
	 * Save a {@link ReductionPlan}. Also handles updates since JPA's save() upserts when the entity
	 * carries a non-zero id.
	 * @param reductionPlan the {@link ReductionPlan} to save
	 * @return the saved {@link ReductionPlan}
	 * @throws OHServiceException when failed to save {@link ReductionPlan}
	 */
	public ReductionPlan add(ReductionPlan reductionPlan) throws OHServiceException {
		List<OHExceptionMessage> errors = validateReductionPlan(reductionPlan);

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
		return reductionPlanRepository.save(reductionPlan);
	}

	/**
	 * Soft-delete a {@link ReductionPlan}.
	 * @param reductionPlan the {@link ReductionPlan} to delete
	 * @throws OHServiceException when failed to delete {@link ReductionPlan}
	 */
	public void delete(ReductionPlan reductionPlan) throws OHServiceException {
		ReductionPlan toDelete = reductionPlanRepository.findByIdAndDeleted(reductionPlan.getId(), false);
		toDelete.setDeleted(true);
		reductionPlanRepository.save(toDelete);
	}

	/**
	 * Fetch a list of {@link ExamReduction}s by {@link ReductionPlan} id
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link ExamReduction}s
	 * @throws OHServiceException if the error happened during the get process
	 */
	public List<ExamReduction> getExamReductionsByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return examReductionRepository.findByReductionPlanId(reductionPlanId);
	}

	/**
	 * Fetch a list of {@link MedicalReduction}s by {@link ReductionPlan} id
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link MedicalReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<MedicalReduction> getMedicalReductionsByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return medicalReductionRepository.findByReductionPlanId(reductionPlanId);
	}

	/**
	 * Fetch a list of {@link OperationReduction}s by {@link ReductionPlan} id
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link OperationReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<OperationReduction> getOperationReductionsByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return operationReductionRepository.findByReductionPlanId(reductionPlanId);
	}

	/**
	 * Fetch a list of {@link PriceOtherReduction}s by {@link ReductionPlan} id
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link PriceOtherReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<PriceOtherReduction> getPriceOtherReductionsByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return priceOtherReductionRepository.findByReductionPlanId(reductionPlanId);
	}
}
