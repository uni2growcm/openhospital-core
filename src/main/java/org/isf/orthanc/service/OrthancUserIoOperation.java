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

import org.isf.orthanc.model.OrthancUser;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class OrthancUserIoOperation {

	private OrthancUserIoOperationRepository repository;
	
	public OrthancUserIoOperation(OrthancUserIoOperationRepository repository) {
		this.repository = repository;
	}
	
	/**
	 * Return {@link OrthancUser}
	 * 
	 * @param ohUserId - the oh user name
	 * @return {@link OrthancUser}. It could be {@code null}.
	 * @throws OHServiceException 
	 */
	public OrthancUser getOrtancUserByOhUserId(String ohUserId) throws OHServiceException {
		return repository.findByUserName(ohUserId);
	}
	
	/**
	 * Insert an {@link OrthancUser} in the DB
	 * 
	 * @param orthancUser - the {@link OrthancUser} to insert
	 * @return the {@link OrthancUser} that has been inserted, {@code null} otherwise.
	 * @throws OHServiceException 
	 */
	public OrthancUser newOrthancUser(OrthancUser orthancUser) throws OHServiceException {
		return repository.save(orthancUser);
	}
	
	/**
	 * Update an {@link OrthancUser}
	 * 
	 * @param orthancUser - the {@link OrthancUser} to update
	 * @return the {@link OrthancUser} that has been updated, {@code null} otherwise.
	 * @throws OHServiceException 
	 */
	public OrthancUser updateOrthancUser(OrthancUser orthancUser) throws OHServiceException {
		return repository.save(orthancUser);
	}
}
