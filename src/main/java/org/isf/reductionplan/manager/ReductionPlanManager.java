/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
	 * Get  reduction plans by description
	 * @return The list of {@link ReductionPlan}s
	 * @throws OHServiceException When failed to get  reduction plans by description
	 */
	public List<ReductionPlan> getByDescription(String description) throws OHServiceException {
		return reductionPlanIoOperations.getByDescription(description);
	}
}