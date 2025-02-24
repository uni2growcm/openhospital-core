/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

package org.isf.mortuary.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.isf.generaldata.MessageBundle;
import org.isf.mortuary.model.DeathReason;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DeathReasonIoOperations {

	private static DeathReasonRepository deathReasonRepository;

	@Autowired
	public DeathReasonIoOperations(DeathReasonRepository deathReasonRepository) {
		DeathReasonIoOperations.deathReasonRepository = deathReasonRepository;
	}

	/**
	 * Get all the {@link DeathReason}s.
	 * @return all the {@link DeathReason}s.
	 * @throws OHServiceException
	 */
	public List<DeathReason> getAll() throws OHServiceException {
		return deathReasonRepository.findByDeleted(false);
	}

	/**
	 * Get a specific {@link DeathReason} by id.
	 * @param id DeathReason specific id.
	 * @return {@link DeathReason}.
	 */
	public DeathReason getById(int id) {
		return deathReasonRepository.findByIdAndDeleted(id, false);
	}

	/**
	 * Store the specified {@link DeathReason}.
	 * @param deathReason specific DeathReason to store.
	 * @return {@link DeathReason}.
	 * @throws OHServiceException
	 */
	public DeathReason add(DeathReason deathReason) throws OHServiceException {
		List<OHExceptionMessage> errors = validate(deathReason);
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
		return deathReasonRepository.save(deathReason);
	}

	/**
	 * Deletes a {@link DeathReason} in the DB.
	 *
	 * @param deathReason - the item to delete
	 * return true if deletion works and false otherwise
	 * @throws OHServiceException
	 */
	public boolean delete(DeathReason deathReason) throws OHServiceException {
		DeathReason deathReasonFound = deathReasonRepository.findByTitleAndDeleted(deathReason.getTitle(), false);
		if (deathReasonFound == null) {
			throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuary.deathreason.notfound.msg")));
		}
		deathReasonFound.setDeleted(true);
		DeathReason deleted = deathReasonRepository.save(deathReasonFound);
		return deleted.getDeleted();
	}

	/**
	 * Updates the specified {@link DeathReason}.
	 *
	 * @param deathReason - the {@link DeathReason} to update.
	 * @return deathReason that has been updated.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public DeathReason update(DeathReason deathReason) throws OHServiceException {
		DeathReason deathReasonFound = deathReasonRepository.findById(deathReason.getId()).orElse(null);
		if (deathReasonFound == null) {
			throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.mortuary.deathReason.thisdeathreasondontexist.msg")));
		}
		deathReasonFound.setDescription(deathReason.getDescription());
		return deathReasonRepository.save(deathReason);
	}

	/**
	 * Returns the page of {@link DeathReason} based on code
	 *
	 * @param key - the code, must not be {@literal null}
	 * @return the page of {@link DeathReason}
	 * @throws OHServiceException if {@code code} is {@literal null}
	 */
	public Page<DeathReason> getByTitleOrDescriptionPageable(String key, Pageable pageable) throws OHServiceException {
		if (key != null) {
			return deathReasonRepository.findByTitleContainsAndDeletedOrDescriptionContainsAndDeleted(key, false, key,false, pageable);
		}
		return deathReasonRepository.findByTitleContainsAndDeletedOrDescriptionContainsAndDeleted("", false,"",false, pageable);
	}

	/**
	 * Checks if the death reason exist.
	 *
	 * @param deathReason - the {@link DeathReason} code
	 * @return {@code true} if the death reason is already in exist where deleted is false, {@code false} otherwise
	 */
	public boolean exists(DeathReason deathReason) {
		DeathReason deathReasonFound = deathReasonRepository.findByTitleAndDeleted(deathReason.getTitle(), false);
		return Objects.equals(deathReasonFound, deathReason);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any.
	 * @param deathReason the {@link DeathReason} object to validate.
	 * @throws OHServiceException
	 */
	private List<OHExceptionMessage> validate(DeathReason deathReason) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (deathReason == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.commom.anullentrycannotberegistered.msg")));
			return errors;
		}
		if (deathReason.getTitle() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
			return errors;
		}
		if (deathReason.getTitle().trim().isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		}
		if (exists(deathReason)) {
			throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}
		return errors;
	}
}