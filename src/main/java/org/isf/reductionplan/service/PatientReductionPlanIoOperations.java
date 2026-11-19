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
package org.isf.reductionplan.service;

import java.util.List;
import java.util.Optional;

import org.isf.reductionplan.model.PatientReductionPlan;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PatientReductionPlanIoOperations {

	private final PatientReductionPlanIoOperationRepository repository;

	public PatientReductionPlanIoOperations(PatientReductionPlanIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * @param patientCode the patient code
	 * @return the active assignments of the patient (deleted plans excluded)
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public List<PatientReductionPlan> getActiveByPatientCode(Integer patientCode) throws OHServiceException {
		return repository.findActiveByPatientCode(patientCode);
	}

	/**
	 * @param patientCode the patient code
	 * @param reductionPlanId the reduction plan id
	 * @return the assignment (active or not) of the plan to the patient, if any
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public Optional<PatientReductionPlan> getByPatientCodeAndReductionPlanId(Integer patientCode, int reductionPlanId) throws OHServiceException {
		return repository.findByPatientCodeAndReductionPlanId(patientCode, reductionPlanId);
	}

	/**
	 * @param reductionPlanId the reduction plan id
	 * @return the number of patients the plan has ever been assigned to
	 * @throws OHServiceException if an error occurs while retrieving data
	 */
	public long countPatients(int reductionPlanId) throws OHServiceException {
		return repository.countPatientsByReductionPlanId(reductionPlanId);
	}

	/**
	 * @param patientReductionPlan the assignment to save
	 * @return the saved assignment
	 * @throws OHServiceException if an error occurs while saving data
	 */
	public PatientReductionPlan save(PatientReductionPlan patientReductionPlan) throws OHServiceException {
		return repository.save(patientReductionPlan);
	}
}
