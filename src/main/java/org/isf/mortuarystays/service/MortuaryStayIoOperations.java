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
package org.isf.mortuarystays.service;

import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.mortuarystays.model.MortuaryStay;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

@Component
public class MortuaryStayIoOperations {
	private MortuaryStayIoOperationRepository repository;

	public MortuaryStayIoOperations(MortuaryStayIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Retrieves all stored {@link MortuaryStay}s
	 * @return
	 */
	public List<MortuaryStay> getAll(boolean deleted) throws OHServiceException {
		return repository.findByDeletedOrderByNameAsc(deleted);
	}

	/**
	 * Retrieves all stored {@link MortuaryStay}s
	 * @return
	 */
	public List<MortuaryStay> getAll() throws OHServiceException {
		return repository.findAll();
	}

	/**
	 * Updates the specified {@link MortuaryStay}.
	 *
	 * @param mortuary - the {@link MortuaryStay} to update.
	 * @return mortuary that has been updated.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public MortuaryStay update(MortuaryStay mortuary) throws OHServiceException {
		return repository.save(mortuary);
	}

	/**
	 * Stores the specified {@link MortuaryStay}.
	 *
	 * @param mortuary the mortuary to store.
	 * @return mortuary that has been stored.
	 * @throws OHServiceException if an error occurs storing the mortuary.
	 */
	public MortuaryStay add(MortuaryStay mortuary) throws OHServiceException {
		return repository.save(mortuary);
	}

	/**
	 * Deletes a {@link MortuaryStay} in the DB.
	 *
	 * @param mortuary - the item to delete
	 * @throws OHServiceException
	 */
	public MortuaryStay delete(MortuaryStay mortuary) throws OHServiceException {
		MortuaryStay mortuaryStay = repository.findByIdWhereNotDeleted(mortuary.getCode());
		mortuaryStay.setDeleted(true);
		return repository.save(mortuaryStay);
	}

	/**
	 * Checks if the code is already in use.
	 *
	 * @param code - the {@link MortuaryStay} code
	 * @return {@code true} if the code is already in use and deleted is false, {@code false} otherwise
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		boolean existed = false;
		MortuaryStay mortuaryStay = repository.findByIdWhereNotDeleted(code);
		if (mortuaryStay != null) {
			existed = true;
		}
		return existed;
	}

	/**
	 * Returns the {@link MortuaryStay} based on code
	 *
	 * @param code - the code, must not be {@literal null}
	 * @return the {@link MortuaryStay} or {@literal null} if none found
	 * @throws OHServiceException if {@code code} is {@literal null}
	 */
	public MortuaryStay getByCode(String code) throws OHServiceException {
		if (code != null) {
			return repository.findByIdWhereNotDeleted(code);
		}
		throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuarystays.codemostnotbenull.msg")));
	}
}
