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
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import org.isf.generaldata.GeneralData;
import org.isf.reductionplan.model.AppliedReduction;
import org.isf.reductionplan.model.ReductionPlan;

/**
 * Chooses the {@link ReductionPlan} to apply on an item when several plans of a patient
 * (hospital plans and partner plans) give a reduction on that same item.
 * <ol>
 * <li>the highest (or lowest) reduction rate wins, see {@link Policy#highestRateFirst()}</li>
 * <li>on equal rates, partner plans (or hospital plans) win, see {@link Policy#partnerFirst()}</li>
 * <li>among partner plans, the most (or least) prioritary partner wins, see {@link Policy#mostPrioritaryPartnerFirst()}</li>
 * <li>among plans still equal (hospital plans or plans of the same partner), one is chosen randomly</li>
 * </ol>
 */
public class ReductionPlanSelector {

	/**
	 * Selection settings.
	 *
	 * @param highestRateFirst {@code true} to apply the highest rate, {@code false} to apply the lowest one
	 * @param partnerFirst {@code true} to prefer partner plans on equal rates, {@code false} to prefer hospital plans
	 * @param mostPrioritaryPartnerFirst {@code true} to prefer the most prioritary partner (priority 1),
	 *        {@code false} to prefer the least prioritary one
	 */
	public record Policy(boolean highestRateFirst, boolean partnerFirst, boolean mostPrioritaryPartnerFirst) {

		/**
		 * @return the policy defined in the application settings
		 */
		public static Policy fromSettings() {
			GeneralData.getGeneralData();
			return new Policy(GeneralData.REDUCTIONPLANHIGHESTRATEFIRST, GeneralData.REDUCTIONPLANPARTNERFIRST,
				GeneralData.REDUCTIONPLANMOSTPRIORITARYPARTNERFIRST);
		}
	}

	private final Policy policy;
	private final Random random;

	public ReductionPlanSelector(Policy policy) {
		this(policy, new Random());
	}

	public ReductionPlanSelector(Policy policy, Random random) {
		this.policy = policy;
		this.random = random;
	}

	/**
	 * @param candidates the reductions that apply on the same item (rate greater than zero)
	 * @return the reduction to apply, {@code null} if there is no candidate
	 */
	public AppliedReduction select(List<AppliedReduction> candidates) {
		if (candidates == null || candidates.isEmpty()) {
			return null;
		}
		List<AppliedReduction> remaining = keepExtreme(candidates, AppliedReduction::rate, policy.highestRateFirst());
		if (remaining.size() == 1) {
			return remaining.get(0);
		}

		List<AppliedReduction> partnerPlans = remaining.stream().filter(r -> !r.reductionPlan().isHospitalPlan()).toList();
		List<AppliedReduction> hospitalPlans = remaining.stream().filter(r -> r.reductionPlan().isHospitalPlan()).toList();
		boolean usePartnerPlans = policy.partnerFirst() ? !partnerPlans.isEmpty() : hospitalPlans.isEmpty();

		if (usePartnerPlans) {
			remaining = keepExtreme(partnerPlans, ReductionPlanSelector::partnerPriority, !policy.mostPrioritaryPartnerFirst());
		} else {
			remaining = hospitalPlans;
		}

		return remaining.size() == 1 ? remaining.get(0) : remaining.get(random.nextInt(remaining.size()));
	}

	private static Integer partnerPriority(AppliedReduction reduction) {
		Integer priority = reduction.reductionPlan().getPartner().getPriority();
		return priority == null ? Integer.MAX_VALUE : priority;
	}

	private static <T extends Comparable<T>> List<AppliedReduction> keepExtreme(List<AppliedReduction> reductions,
		Function<AppliedReduction, T> key, boolean highest) {
		Comparator<T> comparator = Comparator.naturalOrder();
		T extreme = reductions.stream()
			.map(key)
			.reduce((a, b) -> (comparator.compare(a, b) >= 0) == highest ? a : b)
			.orElseThrow();
		return reductions.stream().filter(r -> comparator.compare(key.apply(r), extreme) == 0).toList();
	}

	/**
	 * @param rate a reduction rate
	 * @return {@code true} if the rate actually gives a reduction
	 */
	static boolean isReduction(BigDecimal rate) {
		return rate != null && rate.signum() > 0;
	}
}
