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
package org.isf.orthanc.service;

import java.util.Optional;

import org.isf.orthanc.model.OrthancPatient;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class OrthancPatientIoOperation {

	private OrthancPatientIoOperationRepository repository;
	
	public OrthancPatientIoOperation(OrthancPatientIoOperationRepository repository) {
		this.repository = repository;
	}
	
	/**
	 * Return {@link OrthancPatient}
	 * 
	 * @param patId - the patient id
	 * @return {@link OrthancPatient}. It could be {@code null}.
	 * @throws OHServiceException 
	 */
	public OrthancPatient getOrthancPatientByPatientId(int patId) throws OHServiceException {
		return repository.findByPatientId(patId);
	}
	
	/**
	 * Insert an {@link OrthancPatient} in the DB
	 * 
	 * @param orthancPatient - the {@link OrthancPatient} to insert
	 * @return the {@link OrthancPatient} that has been inserted, {@code null} otherwise.
	 * @throws OHServiceException 
	 */
	public OrthancPatient newOrthancPatient(OrthancPatient orthancPatient) throws OHServiceException {
		return repository.save(orthancPatient);
	}
	
	/**
	 * Update an {@link OrthancPatient}
	 * 
	 * @param orthancPatient - the {@link OrthancPatient} to update
	 * @return the {@link OrthancPatient} that has been updated, {@code null} otherwise.
	 * @throws OHServiceException 
	 */
	public OrthancPatient updateOrthancPatient(OrthancPatient orthancPatient) throws OHServiceException {
		return repository.save(orthancPatient);
	}

	/**
	 * Return {@link OrthancPatient}
	 * 
	 * @param id - the orthan patient id
	 * @return {@link OrthancPatient}. It could be {@code null}.
	 * @throws OHServiceException 
	 */
	public Optional<OrthancPatient> getOrthancPatientById(int id) {
		return repository.findById(id);
	}
}
