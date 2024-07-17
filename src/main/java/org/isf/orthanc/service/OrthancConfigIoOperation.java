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

import org.isf.orthanc.model.OrthancConfig;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class OrthancConfigIoOperation {

	private OrthancConfigIoOperationRepository repository;
	
	public OrthancConfigIoOperation(OrthancConfigIoOperationRepository repository) {
		this.repository = repository;
	}
	
	/**
	 * Return {@link OrthancConfig}
	 * 
	 * @param userName - the user name
	 * @return {@link OrthancConfig}. It could be {@code null}.
	 * @throws OHServiceException 
	 */
	public OrthancConfig getOrtancConfigByUserName(String userName) throws OHServiceException {
		return repository.findByUserName(userName);
	}
	
	/**
	 * Insert an {@link OrthancConfig} in the DB
	 * 
	 * @param orthancConfig - the {@link OrthancConfig} to insert
	 * @return the {@link OrthancConfig} that has been inserted, {@code null} otherwise.
	 * @throws OHServiceException 
	 */
	public OrthancConfig newOrthancConfig(OrthancConfig orthancConfig) throws OHServiceException {
		return repository.save(orthancConfig);
	}
	
	/**
	 * Update an {@link OrthancConfig}
	 * 
	 * @param orthancConfig - the {@link OrthancConfig} to update
	 * @return the {@link OrthancConfig} that has been updated, {@code null} otherwise.
	 * @throws OHServiceException 
	 */
	public OrthancConfig updateOrthancConfig(OrthancConfig orthancConfig) throws OHServiceException {
		return repository.save(orthancConfig);
	}
}
