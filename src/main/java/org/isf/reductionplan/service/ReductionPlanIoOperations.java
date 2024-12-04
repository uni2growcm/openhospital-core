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
import java.util.Optional;

import org.isf.generaldata.MessageBundle;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mwithi
 */
@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class ReductionPlanIoOperations {

	private final ReductionplanIoOperationRepository reductionplanIoOperationRepository;

	public ReductionPlanIoOperations(ReductionplanIoOperationRepository reductionplanIoOperationRepository
	) {
		this.reductionplanIoOperationRepository = reductionplanIoOperationRepository;
	}

	/**
	 * Return the list of {@link ReductionPlan}s in the DB
	 * @return the list of {@link ReductionPlan}s
	 * @throws OHServiceException
	 */
	public List<ReductionPlan> getAll() throws OHServiceException {
		return reductionplanIoOperationRepository.findAll();
	}

	/**
	 * Return the list of {@link ReductionPlan}s in the DB
	 * @return the list of {@link ReductionPlan}s
	 * @throws OHServiceException
	 */

	public List<ReductionPlan> findByIdIn(List<Integer> ids) {
		return reductionplanIoOperationRepository.findByIdIn(ids);
	}

	public List<ReductionPlan> getReductionPlan(String description) throws OHServiceException {

		List<ReductionPlan> reductionPlans = reductionplanIoOperationRepository.findByDescription(description);
		return reductionPlans;
	}

	public ReductionPlan newReductionPlan(ReductionPlan reductionPlan) throws OHServiceException {
		return reductionplanIoOperationRepository.save(reductionPlan);
	}

	public ReductionPlan updateReductionPlan(ReductionPlan reductionPlan) throws OHServiceException {
		Optional<ReductionPlan> existingPlanOpt = reductionplanIoOperationRepository.findById(rpId);
		
		if (existingPlanOpt.isEmpty()) {
		    throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.reductionplan.notfound.msg")));
		}
		
		return reductionplanIoOperationRepository.save(reductionPlan);

	public void deleteReductionplan(ReductionPlan reductionplan) throws OHServiceException {
		reductionplanIoOperationRepository.delete(reductionplan);
	}

}