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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.isf.generaldata.MessageBundle;
import org.isf.partner.model.Partner;
import org.isf.partner.service.PartnerIoOperations;
import org.isf.patient.model.Patient;
import org.isf.priceslist.model.Price;
import org.isf.reductionplan.model.AppliedReduction;
import org.isf.reductionplan.model.PatientReductionPlan;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.reductionplan.service.PatientReductionPlanIoOperations;
import org.isf.reductionplan.service.ReductionPlanIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages the {@link ReductionPlan}s assigned to the patients (hospital plans and partner plans)
 * and resolves, for a priced item, which plan applies during billing.
 */
@Component
public class PatientReductionPlanManager {

	public static final String GROUP_EXAM = "EXA";
	public static final String GROUP_OPERATION = "OPE";
	public static final String GROUP_MEDICAL = "MED";
	public static final String GROUP_OTHER = "OTH";

	private final ReductionPlanIoOperations reductionPlanIoOperations;
	private final PatientReductionPlanIoOperations patientReductionPlanIoOperations;
	private final PartnerIoOperations partnerIoOperations;

	public PatientReductionPlanManager(ReductionPlanIoOperations reductionPlanIoOperations,
		PatientReductionPlanIoOperations patientReductionPlanIoOperations,
		PartnerIoOperations partnerIoOperations) {
		this.reductionPlanIoOperations = reductionPlanIoOperations;
		this.patientReductionPlanIoOperations = patientReductionPlanIoOperations;
		this.partnerIoOperations = partnerIoOperations;
	}

	/* ------------------------------------------------------------------ */
	/* Assignments                                                          */
	/* ------------------------------------------------------------------ */

	/**
	 * @param patientCode the patient code
	 * @return all the reduction plans currently assigned to the patient (expired ones included)
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public List<ReductionPlan> getPatientReductionPlans(Integer patientCode) throws OHServiceException {
		if (patientCode == null || patientCode == 0) {
			return new ArrayList<>();
		}
		return patientReductionPlanIoOperations.getActiveByPatientCode(patientCode).stream()
			.map(PatientReductionPlan::getReductionPlan)
			.toList();
	}

	/**
	 * @param patientCode the patient code
	 * @return the hospital reduction plans currently assigned to the patient
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public List<ReductionPlan> getPatientHospitalPlans(Integer patientCode) throws OHServiceException {
		return getPatientReductionPlans(patientCode).stream().filter(ReductionPlan::isHospitalPlan).toList();
	}

	/**
	 * @param patientCode the patient code
	 * @param partnerCode the partner code
	 * @return the reduction plans of the partner currently assigned to the patient
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public List<ReductionPlan> getPatientPartnerPlans(Integer patientCode, Integer partnerCode) throws OHServiceException {
		return getPatientReductionPlans(patientCode).stream().filter(plan -> belongsTo(plan, partnerCode)).toList();
	}

	/**
	 * @param patientCode the patient code ({@code null} or 0 for a new patient)
	 * @return the hospital plans that can be assigned to the patient (not expired, quota not reached)
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public List<ReductionPlan> getAssignableHospitalPlans(Integer patientCode) throws OHServiceException {
		return filterAssignable(reductionPlanIoOperations.getHospitalPlans(), patientCode);
	}

	/**
	 * @param partnerCode the partner code
	 * @param patientCode the patient code ({@code null} or 0 for a new patient)
	 * @return the plans of the partner that can be assigned to the patient (not expired, quota not reached)
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public List<ReductionPlan> getAssignablePartnerPlans(Integer partnerCode, Integer patientCode) throws OHServiceException {
		return filterAssignable(reductionPlanIoOperations.getPartnerPlans(partnerCode), patientCode);
	}

	/**
	 * @param reductionPlan the reduction plan
	 * @return the number of patients the plan has ever been assigned to
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public long getPatientCount(ReductionPlan reductionPlan) throws OHServiceException {
		return patientReductionPlanIoOperations.countPatients(reductionPlan.getId());
	}

	/**
	 * Replaces the hospital reduction plans of a patient.
	 *
	 * @param patient the (saved) patient
	 * @param reductionPlans the hospital plans to assign
	 * @throws OHServiceException if a plan cannot be assigned or an error occurs while saving data
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	public void setPatientHospitalPlans(Patient patient, List<ReductionPlan> reductionPlans) throws OHServiceException {
		setPatientPlans(patient, null, reductionPlans);
	}

	/**
	 * Replaces the reduction plans of a partner assigned to a patient.
	 *
	 * @param patient the (saved) patient
	 * @param partner the partner, which must be associated to the patient
	 * @param reductionPlans the plans of the partner to assign
	 * @throws OHServiceException if a plan cannot be assigned or an error occurs while saving data
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	public void setPatientPartnerPlans(Patient patient, Partner partner, List<ReductionPlan> reductionPlans) throws OHServiceException {
		Objects.requireNonNull(partner, "partner");
		if (reductionPlans != null && !reductionPlans.isEmpty()
			&& partnerIoOperations.getActivePartnersByPatientCode(patient.getCode()).stream()
				.noneMatch(p -> p.getCode().equals(partner.getCode()))) {
			throw new OHDataValidationException(new OHExceptionMessage(
				MessageBundle.formatMessage("angal.reductionplan.partnernotassociated.fmt.msg", partner.getName())));
		}
		setPatientPlans(patient, partner.getCode(), reductionPlans);
	}

	/**
	 * Removes from a patient all the reduction plans of a partner (e.g. when the partner is removed from the patient).
	 *
	 * @param patient the patient
	 * @param partner the partner
	 * @throws OHServiceException if an error occurs while saving data
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	public void removePatientPartnerPlans(Patient patient, Partner partner) throws OHServiceException {
		setPatientPlans(patient, partner.getCode(), List.of());
	}

	private void setPatientPlans(Patient patient, Integer partnerCode, List<ReductionPlan> reductionPlans) throws OHServiceException {
		if (patient == null || patient.getCode() == null || patient.getCode() == 0) {
			throw new OHDataValidationException(new OHExceptionMessage(
				MessageBundle.getMessage("angal.reductionplan.patientnotsaved.msg")));
		}
		Integer patientCode = patient.getCode();
		List<ReductionPlan> wanted = reductionPlans == null ? List.of() : reductionPlans;
		Set<Integer> wantedIds = new HashSet<>();
		List<OHExceptionMessage> errors = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now();

		List<PatientReductionPlan> toSave = new ArrayList<>();
		for (ReductionPlan plan : wanted) {
			ReductionPlan current = reductionPlanIoOperations.getById(plan.getId());
			if (current == null || !wantedIds.add(current.getId())) {
				continue;
			}
			if (!belongsTo(current, partnerCode)) {
				errors.add(new OHExceptionMessage(
					MessageBundle.formatMessage("angal.reductionplan.wrongowner.fmt.msg", current.getDescription())));
				continue;
			}
			Optional<PatientReductionPlan> existing = patientReductionPlanIoOperations.getByPatientCodeAndReductionPlanId(patientCode, current.getId());
			if (existing.isPresent() && existing.get().isActive()) {
				continue; // already assigned
			}
			if (current.isExpiredAt(now)) {
				errors.add(new OHExceptionMessage(
					MessageBundle.formatMessage("angal.reductionplan.expired.fmt.msg", current.getDescription())));
				continue;
			}
			if (existing.isEmpty() && isFull(current)) {
				errors.add(new OHExceptionMessage(
					MessageBundle.formatMessage("angal.reductionplan.maxpatientsreached.fmt.msg", current.getDescription(),
						current.getMaxPatients())));
				continue;
			}
			PatientReductionPlan assignment = existing.orElseGet(() -> new PatientReductionPlan(patient, current));
			assignment.setActive(1);
			toSave.add(assignment);
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}

		for (PatientReductionPlan assignment : patientReductionPlanIoOperations.getActiveByPatientCode(patientCode)) {
			if (belongsTo(assignment.getReductionPlan(), partnerCode) && !wantedIds.contains(assignment.getReductionPlan().getId())) {
				assignment.setActive(0);
				toSave.add(assignment);
			}
		}
		for (PatientReductionPlan assignment : toSave) {
			patientReductionPlanIoOperations.save(assignment);
		}
	}

	private List<ReductionPlan> filterAssignable(List<ReductionPlan> plans, Integer patientCode) throws OHServiceException {
		LocalDateTime now = LocalDateTime.now();
		List<ReductionPlan> result = new ArrayList<>();
		for (ReductionPlan plan : plans) {
			if (plan.isExpiredAt(now)) {
				continue;
			}
			boolean alreadyCounted = patientCode != null && patientCode != 0
				&& patientReductionPlanIoOperations.getByPatientCodeAndReductionPlanId(patientCode, plan.getId()).isPresent();
			if (alreadyCounted || !isFull(plan)) {
				result.add(plan);
			}
		}
		return result;
	}

	private boolean isFull(ReductionPlan plan) throws OHServiceException {
		Integer max = plan.getMaxPatients();
		return max != null && max > 0 && patientReductionPlanIoOperations.countPatients(plan.getId()) >= max;
	}

	private static boolean belongsTo(ReductionPlan plan, Integer partnerCode) {
		if (partnerCode == null) {
			return plan.isHospitalPlan();
		}
		return !plan.isHospitalPlan() && partnerCode.equals(plan.getPartner().getCode());
	}

	/* ------------------------------------------------------------------ */
	/* Billing                                                              */
	/* ------------------------------------------------------------------ */

	/**
	 * Returns the reduction plans of a patient that can apply at the given date: plans not deleted,
	 * not expired and, for partner plans, whose partner is still active and associated to the patient.
	 *
	 * @param patientCode the patient code
	 * @param date the reference date (usually the bill date), {@code null} for now
	 * @return the applicable plans
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public List<ReductionPlan> getApplicableReductionPlans(Integer patientCode, LocalDateTime date) throws OHServiceException {
		if (patientCode == null || patientCode == 0) {
			return new ArrayList<>();
		}
		LocalDateTime referenceDate = date == null ? LocalDateTime.now() : date;
		Set<Integer> patientPartners = new HashSet<>();
		for (Partner partner : partnerIoOperations.getActivePartnersByPatientCode(patientCode)) {
			patientPartners.add(partner.getCode());
		}
		return getPatientReductionPlans(patientCode).stream()
			.filter(plan -> !plan.isExpiredAt(referenceDate))
			.filter(plan -> plan.isHospitalPlan() || patientPartners.contains(plan.getPartner().getCode()))
			.toList();
	}

	/**
	 * Resolves the reduction to apply on a priced item, choosing among the applicable plans
	 * according to the settings (see {@link ReductionPlanSelector}).
	 *
	 * @param applicablePlans the applicable plans of the patient, see {@link #getApplicableReductionPlans(Integer, LocalDateTime)}
	 * @param group the price group ({@code EXA}, {@code OPE}, {@code MED} or {@code OTH})
	 * @param item the item code in the group
	 * @return the reduction to apply, {@code null} if no plan gives a reduction on the item
	 */
	public AppliedReduction resolveReduction(List<ReductionPlan> applicablePlans, String group, String item) {
		return resolveReduction(applicablePlans, group, item, new ReductionPlanSelector(ReductionPlanSelector.Policy.fromSettings()));
	}

	AppliedReduction resolveReduction(List<ReductionPlan> applicablePlans, String group, String item, ReductionPlanSelector selector) {
		if (applicablePlans == null || applicablePlans.isEmpty() || group == null || item == null) {
			return null;
		}
		List<AppliedReduction> candidates = new ArrayList<>();
		for (ReductionPlan plan : applicablePlans) {
			BigDecimal rate = getRate(plan, group, item);
			if (ReductionPlanSelector.isReduction(rate)) {
				candidates.add(new AppliedReduction(plan, rate));
			}
		}
		return selector.select(candidates);
	}

	/**
	 * Returns a copy of the price with the reduction resolved for the patient applied.
	 *
	 * @param price the price (not modified)
	 * @param applicablePlans the applicable plans of the patient, see {@link #getApplicableReductionPlans(Integer, LocalDateTime)}
	 * @return a copy of the price, reduced if a plan applies
	 */
	public Price getReducedPrice(Price price, List<ReductionPlan> applicablePlans) {
		if (price == null) {
			return null;
		}
		Price copy = copyOf(price);
		AppliedReduction reduction = resolveReduction(applicablePlans, price.getGroup(), price.getItem());
		if (reduction != null) {
			copy.setPrice(reduction.apply(price.getPrice()));
		}
		return copy;
	}

	/**
	 * @param plan the reduction plan
	 * @param group the price group
	 * @param item the item code in the group
	 * @return the rate given by the plan on the item: the specific rate of the item if defined,
	 *         otherwise the global rate of the group
	 */
	public static BigDecimal getRate(ReductionPlan plan, String group, String item) {
		return switch (group) {
			case GROUP_EXAM -> rateOf(plan.getExamReductions(), r -> String.valueOf(r.getExam().getCode()), r -> r.getReductionRate(), item,
				plan.getExamRate());
			case GROUP_OPERATION -> rateOf(plan.getOperationReductions(), r -> String.valueOf(r.getOperation().getCode()), r -> r.getReductionRate(),
				item, plan.getOperationRate());
			case GROUP_MEDICAL -> rateOf(plan.getMedicalReductions(), r -> String.valueOf(r.getMedical().getCode()), r -> r.getReductionRate(),
				item, plan.getMedicalRate());
			case GROUP_OTHER -> rateOf(plan.getPriceOtherReductions(), r -> String.valueOf(r.getPricesOthers().getId()), r -> r.getReductionRate(),
				item, plan.getOtherRate());
			default -> null;
		};
	}

	private static <T> BigDecimal rateOf(List<T> reductions, Function<T, String> itemOf, Function<T, BigDecimal> rateOf, String item,
		BigDecimal globalRate) {
		if (reductions != null) {
			for (T reduction : reductions) {
				if (item.equals(itemOf.apply(reduction))) {
					return rateOf.apply(reduction);
				}
			}
		}
		return globalRate;
	}

	private static Price copyOf(Price original) {
		Price copy = new Price();
		copy.setId(original.getId());
		copy.setList(original.getList());
		copy.setGroup(original.getGroup());
		copy.setItem(original.getItem());
		copy.setDesc(original.getDesc());
		copy.setPrice(original.getPrice());
		copy.setVariable(original.isVariable());
		copy.setEditable(original.isEditable());
		return copy;
	}
}
