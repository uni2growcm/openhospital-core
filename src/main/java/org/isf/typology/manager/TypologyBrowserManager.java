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
package org.isf.typology.manager;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.typology.model.Family;
import org.isf.typology.model.Typology;
import org.isf.typology.service.TypologyIoOperation;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class TypologyBrowserManager {
	private final TypologyIoOperation ioOperations;

	public TypologyBrowserManager(TypologyIoOperation ioOperations) {
		this.ioOperations = ioOperations;
	}

	/**
	 * Validate a {@link Typology} before persistence.
	 *
	 * @param typology entity
	 * @param insert true if create operation, false if update
	 * @throws OHServiceException validation error
	 */
	protected void validateTypology(Typology typology, boolean insert)
		throws OHServiceException {

		List<OHExceptionMessage> errors = new ArrayList<>();

		String code = typology.getCode();
		String description = typology.getDescription();

		if (code != null) {
			code = code.trim().toUpperCase();
			typology.setCode(code);
		}

		if (code == null || code.isEmpty()) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		}

		if (code != null && code.length() > 20) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.common.thecodeistoolong.msg")));
		}

		if (description == null || description.trim().isEmpty()) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}

		if (insert) {
			if (isCodePresent(code)) {
				throw new OHDataIntegrityViolationException(
					new OHExceptionMessage(
						MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
			}
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Retrieves all available pregnancy typologys.
	 *
	 * @param page the page
	 * @param size the number of typologies to be listed
	 * @return list of all {@link Typology} records
	 * @throws OHServiceException if an error occurs during retrieval
	 */
	public Page<Typology> getTypologies(int page, int size) throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size);
		return ioOperations.getTypologies(pageable);
	}

	/**
	 * Get all {@link Typology} ordered by description.
	 *
	 * @param search the search term
	 * @param family the family of the typology
	 * @param page the page
	 * @param size the number of typologies to be listed
	 * @return list of Typologies sorted alphabetically in pages
	 * @throws OHServiceException if retrieval fails
	 */
	public Page<Typology> searchTypologies(String search, Family family, int page, int size) throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size);
		if (family != null && !isFamilyEnabled(family)) {
			return new PageImpl<>(List.of(), pageable, 0);
		}
		Page<Typology> result = ioOperations.searchTypologies(search, family, pageable);
		List<Typology> enabledTypologies = result.getContent().stream().filter(typology -> isFamilyEnabled(typology.getFamily())).toList();

		return new PageImpl<>(enabledTypologies, pageable, enabledTypologies.size());
	}

	/**
	 * Get all {@link Typology} for a given family ordered by description.
	 *
	 * @param family the family of the {@link Typology}s to be fetched
	 * @return list of Typologies sorted alphabetically
	 * @throws OHServiceException if retrieval fails
	 */
	public List<Typology> getTypologies(Family family) throws OHServiceException {
		if (!isFamilyEnabled(family)) {
			return List.of();
		}
		return ioOperations.getTypologies(family);
	}

	/**
	 * Retrieves a pregnancy typology using its unique code.
	 *
	 * @param code the unique identifier of the typology
	 * @return the corresponding {@link Typology}, or {@code null} if not found
	 * @throws OHServiceException if an error occurs during retrieval
	 */
	public Typology getTypologyByCode(String code) throws OHServiceException {
		return ioOperations.getByCode(code).orElse(null);
	}

	/**
	 * Checks whether a pregnancy typology code already exists in the system.
	 *
	 * @param code the typology code to check
	 * @return {@code true} if the code exists, otherwise {@code false}
	 * @throws OHServiceException if an error occurs during the check
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.existsByCode(code);
	}

	/**
	 * Creates a new pregnancy typology after validating its data.
	 *
	 * @param typology the typology entity to create
	 * @return the persisted {@link Typology}
	 * @throws OHServiceException if validation fails or persistence error occurs
	 */
	public Typology newTypology(Typology typology) throws OHServiceException {
		validateTypology(typology, true);
		return ioOperations.newTypology(typology);
	}

	/**
	 * Updates an existing pregnancy typology after validation.
	 *
	 * @param typology the typology entity to update
	 * @return the updated {@link Typology}
	 * @throws OHServiceException if validation fails or update error occurs
	 */
	public Typology updateTypology(Typology typology) throws OHServiceException {
		validateTypology(typology, false);
		return ioOperations.updateTypology(typology);
	}

	/**
	 * Deletes a pregnancy typology from the system.
	 *
	 * @param typology the typology to delete
	 * @throws OHServiceException if an error occurs during deletion
	 */
	public void deleteTypology(Typology typology) throws OHServiceException {
		ioOperations.deleteTypology(typology);
	}

	/**
	 * display conditioning of typologies.
	 *
	 * @param family the family of the {@link Typology}s
	 */
	public boolean isFamilyEnabled(Family family) {
		if (family == null) {
			return true;
		}

		return switch (family) {
			case DELIVERYTYPE -> GeneralData.MATERNITYMODULEENABLED;

			case VISITTYPE -> GeneralData.HOMEVISITMODULEENABLED;

			case PARTNERTYPE -> GeneralData.PARTNERSMODULEENABLED;

			case FAMILYPLANNINGMETHODTYPE, FAMILYPLANNINGVISITTYPE -> GeneralData.MATERNITYMODULEENABLED && GeneralData.FAMILYPLANNINGMODULEENABLED;

			case HIVTREATMENTTYPE -> GeneralData.MATERNITYMODULEENABLED && GeneralData.HIVMODULEENABLED;

			case TUBERCULOSISREGIMEN, TUBERCULOSISCONTACT -> GeneralData.TUBERCULOSISMODULEENABLED;
		};
	}
}
