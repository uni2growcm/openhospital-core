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

package org.isf.reductionplan.service;

import java.util.List;

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

	public ReductionPlanIoOperations(ReductionplanIoOperationRepository reductionplanIoOperationRepository) {
		this.reductionplanIoOperationRepository = reductionplanIoOperationRepository;
	}

	/**
	 * Get all reduction plans
	 * @return The list of {@link ReductionPlan}s
	 * @throws OHServiceException When failed to get all reduction plans
	 */
	public List<ReductionPlan> getAll(boolean deleted) throws OHServiceException {
		return reductionplanIoOperationRepository.findByDeleted(deleted);
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
	 * @return the newly persisted {@link ReductionPlan} object
	 * @throws OHServiceException when failed to save {@link ReductionPlan}
	 */
	public ReductionPlan save(ReductionPlan reductionPlan) throws OHServiceException {
		return reductionplanIoOperationRepository.save(reductionPlan);
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
}