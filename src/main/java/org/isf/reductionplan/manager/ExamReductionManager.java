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

import org.isf.reductionplan.model.ExamReduction;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.reductionplan.service.ExamReductionIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

@Component
public class ExamReductionManager {
	private final ExamReductionIoOperations examReductionIoOperations;

	public ExamReductionManager(ExamReductionIoOperations examReductionIoOperations) {
		this.examReductionIoOperations = examReductionIoOperations;
	}

	/**
	 * save a {@link ExamReduction}
	 * @param examReduction the {@link ExamReduction} to insert
	 * @return the newly persisted {@link ExamReduction} object
	 * @throws OHServiceException if the error happened during the save process
	 */
	public ExamReduction save(ExamReduction examReduction) throws OHServiceException {
		return examReductionIoOperations.save(examReduction);
	}

	/**
	 * fetch a list of {@link ExamReduction}s by {@link ReductionPlan} id
	 * @param reductionPlanId the {@link ExamReduction} id
	 * @return the list of {@link ExamReduction}s
	 * @throws OHServiceException if the error happened during the get process
	 */
	public List<ExamReduction> getByReductionPlanId(int reductionPlanId, boolean deleted) throws OHServiceException {
		return examReductionIoOperations.getByReductionPlan(reductionPlanId, deleted);
	}

	/**
	 * delete an {@link ExamReduction}
	 * @param examReduction the {@link ExamReduction} you want to delete
	 * @throws OHServiceException if the error happened during the delete process
	 */
	public ExamReduction delete(ExamReduction examReduction) throws OHServiceException {
		return examReductionIoOperations.delete(examReduction);
	}
}
