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

import org.isf.reductionplan.model.MedicalReduction;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.reductionplan.service.MedicalReductionIoOperation;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

@Component
public class MedicalReductionManager {
	private final MedicalReductionIoOperation medicalReductionIoOperation;

	public MedicalReductionManager(MedicalReductionIoOperation medicalReductionIoOperation) {
		this.medicalReductionIoOperation = medicalReductionIoOperation;
	}

	/**
	 * Save a {@link MedicalReduction}
	 * @param medicalReduction the {@link MedicalReduction} to insert
	 * @return the newly persisted {@link MedicalReduction} object
	 * @throws OHServiceException if an error happened during the save process
	 */
	public MedicalReduction save(MedicalReduction medicalReduction) throws OHServiceException {
		return medicalReductionIoOperation.save(medicalReduction);
	}

	/**
	 * fetch a list of {@link MedicalReduction}s by {@link ReductionPlan} id.
	 * @param reductionPlanId the {@link ReductionPlan} id
	 * @param deleted if get deteted or not deleted data
	 * @return the list of {@link MedicalReduction}s
	 * @throws OHServiceException if an error happened during the get process
	 */
	public List<MedicalReduction> getByReductionPlanId(int reductionPlanId, boolean deleted) throws OHServiceException {
		return medicalReductionIoOperation.getByReductionPlanId(reductionPlanId, deleted);
	}

	/**
	 * delete a {@link MedicalReduction}
	 * @param medicalReduction the {@link MedicalReduction} want to delete
	 * @throws OHServiceException if an error happened during the delete process
	 */
	public MedicalReduction delete(MedicalReduction medicalReduction) throws OHServiceException {
		return medicalReductionIoOperation.delete(medicalReduction);
	}

	/**
	 * Delete a list of {@link MedicalReduction}s
	 * @param medicalReductionList the list of {@link MedicalReduction}s to delete
	 * @throws OHServiceException if the error happen during the delete process
	 */
	public void deleteBulk(List<MedicalReduction> medicalReductionList) throws OHServiceException {
		for (MedicalReduction medicalReduction : medicalReductionList) {
			delete(medicalReduction);
		}
	}
}
