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
package org.isf.reductionplan.model;

import java.math.BigDecimal;

/**
 * The {@link ReductionPlan} selected for a priced item and the reduction rate it applies.
 *
 * @param reductionPlan the selected plan
 * @param rate the reduction rate (percentage) applied by the plan on the item
 */
public record AppliedReduction(ReductionPlan reductionPlan, BigDecimal rate) {

	/**
	 * @param price the original price
	 * @return the price after the reduction
	 */
	public Double apply(Double price) {
		if (price == null || rate == null) {
			return price;
		}
		return price - ((price * rate.doubleValue()) / 100);
	}
}
