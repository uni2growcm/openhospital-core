/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.isf.priceslist.model.Price;
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
	public List<ReductionPlan> getAll() throws OHServiceException {
		return reductionPlanIoOperations.getAll();
	}

	/**
	 * Get a {@link ReductionPlan} by ID
	 * @param id {@link ReductionPlan}'s ID
	 * @return a {@link ReductionPlan}
	 * @throws OHServiceException When failed to get  reduction plans by description
	 */
	public ReductionPlan getById(int id) throws OHServiceException {
		return reductionPlanIoOperations.getById(id);
	}

	/**
	 * Get  reduction plans by description
	 * @param description {@link ReductionPlan}'s description
	 * @return The list of {@link ReductionPlan}s
	 * @throws OHServiceException When failed to get  reduction plans by description
	 */
	public List<ReductionPlan> getByDescription(String description) throws OHServiceException {
		return reductionPlanIoOperations.getByDescription(description);
	}

	/**
	 * Save a {@link ReductionPlan}
	 * @param reductionPlan the {@link ReductionPlan} to add
	 * @throws OHServiceException when failed to save {@link ReductionPlan}
	 */
	public ReductionPlan add(ReductionPlan reductionPlan) throws OHServiceException {
		return reductionPlanIoOperations.add(reductionPlan);
	}

	/**
	 * Update a {@link ReductionPlan}
	 * @param reductionPlan the {@link ReductionPlan} to update
	 * @throws OHServiceException when failed to update {@link ReductionPlan}
	 */
	public ReductionPlan update(ReductionPlan reductionPlan) throws OHServiceException {
		return reductionPlanIoOperations.add(reductionPlan);
	}

	/**
	 * Delete a {@link ReductionPlan}
	 * @param reductionPlan the {@link ReductionPlan} to delete
	 * @throws OHServiceException when failed to delete {@link ReductionPlan}
	 */
	public void delete(ReductionPlan reductionPlan) throws OHServiceException {
		reductionPlanIoOperations.delete(reductionPlan);
	}

	/**
	 * fetch a list of {@link ExamReduction}s by {@link ReductionPlan} id
	 * @param reductionPlanId the {@link ExamReduction} id
	 * @return the list of {@link ExamReduction}s
	 * @throws OHServiceException if the error happened during the get process
	 */
	public List<ExamReduction> getExamReductionsByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return reductionPlanIoOperations.getExamReductionsByReductionPlanId(reductionPlanId);
	}

	/**
	 * fetch a list of {@link MedicalReduction}s by {@link ReductionPlan} id.
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link MedicalReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<MedicalReduction> getMedicalReductionsByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return reductionPlanIoOperations.getMedicalReductionsByReductionPlanId(reductionPlanId);
	}

	/**
	 * fetch a list of {@link OperationReduction}s by {@link ReductionPlan}
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link OperationReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<OperationReduction> getOperationReductionsByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return reductionPlanIoOperations.getOperationReductionsByReductionPlanId(reductionPlanId);
	}

	/**
	 * Fetch a list of {@link PriceOtherReduction}s by {@link ReductionPlan}
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link PriceOtherReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<PriceOtherReduction> getPriceOtherReductionsByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return reductionPlanIoOperations.getPriceOtherReductionsByReductionPlanId(reductionPlanId);
	}

	private Double applyReduction(Double price, Double rate) {

		if (price == null || rate == null) {
			return price;
		}

		return price - ((price * rate) / 100);
	}

	public Price getExamPrice(Price price, int reductionPlanId) throws OHServiceException {

		ReductionPlan reductionPlan = reductionPlanIoOperations.getById(reductionPlanId);

		if (reductionPlan == null) {
			return price;
		}

		List<ExamReduction> reductions = reductionPlanIoOperations.getExamReductionsByReductionPlanId(reductionPlanId);

		Double newPrice = price.getPrice();

		for (ExamReduction reduction : reductions) {

			if (price.getItem().equals(String.valueOf(reduction.getExam().getCode()))) {

				newPrice = applyReduction(price.getPrice(), reduction.getReductionRate().doubleValue());
				price.setPrice(newPrice);
				return price;
			}
		}

		newPrice = applyReduction(price.getPrice(), reductionPlan.getExamRate().doubleValue());

		price.setPrice(newPrice);

		return price;
	}

	public Price getOperationPrice(Price price, int reductionPlanId) throws OHServiceException {

		ReductionPlan reductionPlan = reductionPlanIoOperations.getById(reductionPlanId);

		if (reductionPlan == null) {
			return price;
		}

		List<OperationReduction> reductions = reductionPlanIoOperations.getOperationReductionsByReductionPlanId(reductionPlanId);

		Double newPrice = price.getPrice();

		for (OperationReduction reduction : reductions) {

			if (price.getItem().equals(String.valueOf(reduction.getOperation().getCode()))) {

				newPrice = applyReduction(
					price.getPrice(),
					reduction.getReductionRate().doubleValue()
				);

				price.setPrice(newPrice);
				return price;
			}
		}

		newPrice = applyReduction(price.getPrice(), reductionPlan.getOperationRate().doubleValue());

		price.setPrice(newPrice);

		return price;
	}

	public Price getMedicalPrice(Price price, int reductionPlanId) throws OHServiceException {

		ReductionPlan reductionPlan = reductionPlanIoOperations.getById(reductionPlanId);

		if (reductionPlan == null) {
			return price;
		}

		List<MedicalReduction> reductions = reductionPlanIoOperations.getMedicalReductionsByReductionPlanId(reductionPlanId);

		Double newPrice = price.getPrice();

		for (MedicalReduction reduction : reductions) {

			if (price.getItem().equals(String.valueOf(reduction.getMedical().getCode()))) {

				newPrice = applyReduction(price.getPrice(), reduction.getReductionRate().doubleValue());

				price.setPrice(newPrice);
				return price;
			}
		}

		newPrice = applyReduction(price.getPrice(), reductionPlan.getMedicalRate().doubleValue());

		price.setPrice(newPrice);

		return price;
	}

	public Price getOtherPrice(Price price, int reductionPlanId) throws OHServiceException {

		ReductionPlan reductionPlan = reductionPlanIoOperations.getById(reductionPlanId);

		if (reductionPlan == null) {
			return price;
		}

		List<PriceOtherReduction> reductions = reductionPlanIoOperations.getPriceOtherReductionsByReductionPlanId(reductionPlanId);

		Double newPrice = price.getPrice();

		for (PriceOtherReduction reduction : reductions) {

			if (price.getItem().equals(String.valueOf(reduction.getPricesOthers().getId()))) {

				newPrice = applyReduction(price.getPrice(), reduction.getReductionRate().doubleValue());

				price.setPrice(newPrice);
				return price;
			}
		}

		newPrice = applyReduction(price.getPrice(), reductionPlan.getOtherRate().doubleValue());

		price.setPrice(newPrice);

		return price;
	}
}