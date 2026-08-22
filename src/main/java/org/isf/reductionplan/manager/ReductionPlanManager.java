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

import java.math.BigDecimal;
import java.math.MathContext;
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
	 * @throws OHServiceException When failed to get reduction plan by id
	 */
	public ReductionPlan getById(int id) throws OHServiceException {
		return reductionPlanIoOperations.getById(id);
	}

	/**
	 * Get reduction plans by description
	 * @param description {@link ReductionPlan}'s description
	 * @return The list of {@link ReductionPlan}s
	 * @throws OHServiceException When failed to get reduction plans by description
	 */
	public List<ReductionPlan> getByDescription(String description) throws OHServiceException {
		return reductionPlanIoOperations.getByDescription(description);
	}

	/**
	 * Save a {@link ReductionPlan}
	 * @param reductionPlan the {@link ReductionPlan} to add
	 * @return the saved {@link ReductionPlan}
	 * @throws OHServiceException when failed to save {@link ReductionPlan}
	 */
	public ReductionPlan add(ReductionPlan reductionPlan) throws OHServiceException {
		return reductionPlanIoOperations.add(reductionPlan);
	}

	/**
	 * Update a {@link ReductionPlan}.
	 * Delegates to {@link ReductionPlanIoOperations#add(ReductionPlan)}: JPA's save() upserts when the
	 * entity carries a non-zero id, so a separate update path isn't needed.
	 * @param reductionPlan the {@link ReductionPlan} to update
	 * @return the updated {@link ReductionPlan}
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
	 * Fetch a list of {@link ExamReduction}s by {@link ReductionPlan} id
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link ExamReduction}s
	 * @throws OHServiceException if the error happened during the get process
	 */
	public List<ExamReduction> getExamReductionsByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return reductionPlanIoOperations.getExamReductionsByReductionPlanId(reductionPlanId);
	}

	/**
	 * Fetch a list of {@link MedicalReduction}s by {@link ReductionPlan} id.
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link MedicalReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<MedicalReduction> getMedicalReductionsByReductionPlanId(int reductionPlanId) throws OHServiceException {
		return reductionPlanIoOperations.getMedicalReductionsByReductionPlanId(reductionPlanId);
	}

	/**
	 * Fetch a list of {@link OperationReduction}s by {@link ReductionPlan}
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

	/**
	 * Apply the medical reduction rate of the given reduction plan to a catalog {@link Price}: the
	 * item-level exception rate if the medical has one, otherwise the plan's medical category rate.
	 * @param price the catalog {@link Price} to discount
	 * @param reductionPlanId the id of the patient's {@link ReductionPlan}, or 0 if none is assigned
	 * @return the {@link Price} with its price discounted, or unchanged if no plan applies
	 * @throws OHServiceException if the error happened during the get process
	 */
	public Price getMedicalPrice(Price price, int reductionPlanId) throws OHServiceException {
		if (reductionPlanId == 0 || price == null) {
			return price;
		}
		ReductionPlan plan = reductionPlanIoOperations.getById(reductionPlanId);
		if (plan == null) {
			return price;
		}
		BigDecimal rate = reductionPlanIoOperations.getMedicalReductionsByReductionPlanId(reductionPlanId).stream()
			.filter(mr -> mr.getMedical() != null && String.valueOf(mr.getMedical().getCode()).equals(price.getItem()))
			.map(MedicalReduction::getReductionRate)
			.findFirst()
			.orElse(plan.getMedicalRate());
		Price discounted = new Price(price);
		discounted.setPrice(applyReduction(price.getPrice(), rate));
		return discounted;
	}

	/**
	 * Apply the exam reduction rate of the given reduction plan to a catalog {@link Price}: the
	 * item-level exception rate if the exam has one, otherwise the plan's exam category rate.
	 * @param price the catalog {@link Price} to discount
	 * @param reductionPlanId the id of the patient's {@link ReductionPlan}, or 0 if none is assigned
	 * @return the {@link Price} with its price discounted, or unchanged if no plan applies
	 * @throws OHServiceException if the error happened during the get process
	 */
	public Price getExamPrice(Price price, int reductionPlanId) throws OHServiceException {
		if (reductionPlanId == 0 || price == null) {
			return price;
		}
		ReductionPlan plan = reductionPlanIoOperations.getById(reductionPlanId);
		if (plan == null) {
			return price;
		}
		BigDecimal rate = reductionPlanIoOperations.getExamReductionsByReductionPlanId(reductionPlanId).stream()
			.filter(er -> er.getExam() != null && er.getExam().getCode().equals(price.getItem()))
			.map(ExamReduction::getReductionRate)
			.findFirst()
			.orElse(plan.getExamRate());
		Price discounted = new Price(price);
		discounted.setPrice(applyReduction(price.getPrice(), rate));
		return discounted;
	}

	/**
	 * Apply the operation reduction rate of the given reduction plan to a catalog {@link Price}: the
	 * item-level exception rate if the operation has one, otherwise the plan's operation category rate.
	 * @param price the catalog {@link Price} to discount
	 * @param reductionPlanId the id of the patient's {@link ReductionPlan}, or 0 if none is assigned
	 * @return the {@link Price} with its price discounted, or unchanged if no plan applies
	 * @throws OHServiceException if the error happened during the get process
	 */
	public Price getOperationPrice(Price price, int reductionPlanId) throws OHServiceException {
		if (reductionPlanId == 0 || price == null) {
			return price;
		}
		ReductionPlan plan = reductionPlanIoOperations.getById(reductionPlanId);
		if (plan == null) {
			return price;
		}
		BigDecimal rate = reductionPlanIoOperations.getOperationReductionsByReductionPlanId(reductionPlanId).stream()
			.filter(or -> or.getOperation() != null && or.getOperation().getCode().equals(price.getItem()))
			.map(OperationReduction::getReductionRate)
			.findFirst()
			.orElse(plan.getOperationRate());
		Price discounted = new Price(price);
		discounted.setPrice(applyReduction(price.getPrice(), rate));
		return discounted;
	}

	/**
	 * Apply the other-service reduction rate of the given reduction plan to a catalog {@link Price}: the
	 * item-level exception rate if the other-service has one, otherwise the plan's other category rate.
	 * @param price the catalog {@link Price} to discount
	 * @param reductionPlanId the id of the patient's {@link ReductionPlan}, or 0 if none is assigned
	 * @return the {@link Price} with its price discounted, or unchanged if no plan applies
	 * @throws OHServiceException if the error happened during the get process
	 */
	public Price getOtherPrice(Price price, int reductionPlanId) throws OHServiceException {
		if (reductionPlanId == 0 || price == null) {
			return price;
		}
		ReductionPlan plan = reductionPlanIoOperations.getById(reductionPlanId);
		if (plan == null) {
			return price;
		}
		BigDecimal rate = reductionPlanIoOperations.getPriceOtherReductionsByReductionPlanId(reductionPlanId).stream()
			.filter(pr -> pr.getPricesOthers() != null && String.valueOf(pr.getPricesOthers().getId()).equals(price.getItem()))
			.map(PriceOtherReduction::getReductionRate)
			.findFirst()
			.orElse(plan.getOtherRate());
		Price discounted = new Price(price);
		discounted.setPrice(applyReduction(price.getPrice(), rate));
		return discounted;
	}

	/**
	 * @param price the price to discount
	 * @param rate the percentage rate to subtract, e.g. 20.00 for 20%
	 * @return {@code price - (price * rate / 100)}
	 */
	private double applyReduction(double price, BigDecimal rate) {
		if (rate == null) {
			return price;
		}
		BigDecimal p = BigDecimal.valueOf(price);
		return p.subtract(p.multiply(rate).divide(BigDecimal.valueOf(100), MathContext.DECIMAL64)).doubleValue();
	}
}
