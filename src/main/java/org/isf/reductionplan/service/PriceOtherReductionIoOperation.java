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

import org.isf.reductionplan.model.PriceOtherReduction;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PriceOtherReductionIoOperation {

	private final PriceOtherReductionIoOperationRepository repository;

	public PriceOtherReductionIoOperation(PriceOtherReductionIoOperationRepository priceOtherReductionIoOperationRepository) {
		repository = priceOtherReductionIoOperationRepository;
	}

	/**
	 * Save a {@link PriceOtherReduction}
	 * @param priceOtherReduction the {@link PriceOtherReduction} to insert
	 * @return the newly persisted {@link PriceOtherReduction} object
	 * @throws OHServiceException if an error happened during the save process
	 */
	public PriceOtherReduction save(PriceOtherReduction priceOtherReduction) throws OHServiceException {
		return repository.save(priceOtherReduction);
	}

	/**
	 * Fetch a list of {@link PriceOtherReduction}s by {@link ReductionPlan}
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @param deleted
	 * @return the list of {@link PriceOtherReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<PriceOtherReduction> getByReductionPlanId(int reductionPlanId, boolean deleted) throws OHServiceException {
		return repository.findByReductionPlanIdAndDeleted(reductionPlanId, deleted);
	}

	/**
	 * Delete a {@link PriceOtherReduction}
	 * @param priceOtherReduction the {@link PriceOtherReduction} want to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public PriceOtherReduction delete(PriceOtherReduction priceOtherReduction) throws OHServiceException {
		priceOtherReduction = repository.findByIdAndDeleted(priceOtherReduction.getId(), false);
		priceOtherReduction.setDeleted(true);
		return repository.save(priceOtherReduction);
	}
}
