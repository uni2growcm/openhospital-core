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

import org.isf.reductionplan.model.MedicalReduction;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class MedicalReductionIoOperation {

	private final MedicalReductionIoOperationRepository repository;

	public MedicalReductionIoOperation(MedicalReductionIoOperationRepository medicalReductionIoOperationRepository) {
		repository = medicalReductionIoOperationRepository;
	}

	/**
	 * Save a {@link MedicalReduction}
	 * @param medicalReduction the {@link MedicalReduction} to insert
	 * @return the newly persisted {@link MedicalReduction} object
	 * @throws OHServiceException if an error happened during the save process
	 */
	public MedicalReduction save(MedicalReduction medicalReduction) throws OHServiceException {
		return repository.save(medicalReduction);
	}

	/**
	 * fetch a list of {@link MedicalReduction}s by {@link ReductionPlan} id.
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @return the list of {@link MedicalReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<MedicalReduction> getByReductionPlanId(int reductionPlanId, boolean deleted) throws OHServiceException {
		return repository.findByReductionPlanIdAndDeleted(reductionPlanId, deleted);
	}

	/**
	 * delete a {@link MedicalReduction}
	 * @param medicalReduction the {@link MedicalReduction} want to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public MedicalReduction delete(MedicalReduction medicalReduction) throws OHServiceException {
		medicalReduction = repository.findByIdAndDeleted(medicalReduction.getId(), false);
		medicalReduction.setDeleted(true);
		return repository.save(medicalReduction);
	}
}
