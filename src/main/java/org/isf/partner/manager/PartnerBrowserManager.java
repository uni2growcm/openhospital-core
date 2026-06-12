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
package org.isf.partner.manager;

import jakarta.persistence.EntityNotFoundException;
import org.isf.partner.model.Partner;
import org.isf.partner.service.PartnerIoOperations;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.typology.model.Typology;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PartnerBrowserManager {

	private final PartnerIoOperations ioOperations;

	public PartnerBrowserManager(PartnerIoOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	/**
	 * Returns all active {@link Partner}s.
	 *
	 * @return the list of active {@link Partner}s ordered by name.
	 * @throws OHServiceException when fails to fetch the partners.
	 */
	public List<Partner> getPartners() throws OHServiceException {
		return ioOperations.getAll();
	}

	/**
	 * Returns the {@link Partner} with the given id.
	 *
	 * @param code the code of the partner to retrieve.
	 * @return the {@link Partner} with the given id.
	 * @throws EntityNotFoundException if no active partner is found with the given id.
	 * @throws OHServiceException when fails to fetch the partner.
	 */
	public Partner getPartner(Integer code) throws OHServiceException {
		return ioOperations.getById(code)
			.orElseThrow(() -> new EntityNotFoundException(
				MessageBundle.formatMessage("angal.partner.notfound.msg", String.valueOf(code))
			));
	}

	/**
	 * Returns the active {@link Partner} with the given code, if it exists.
	 *
	 * @param code the code of the partner to retrieve.
	 * @return an {@link Optional} containing the {@link Partner} if found and active, or empty otherwise.
	 * @throws OHServiceException when fails to fetch the partner.
	 */
	public Optional<Partner> getPartnerByCode(Integer code) throws OHServiceException {
		return ioOperations.getById(code);
	}

	/**
	 * Returns all active {@link Partner}s with the given type.
	 *
	 * @param type the typology of the partner to retrieve.
	 * @return the list of active {@link Partner}s with the given type.
	 * @throws OHServiceException when fails to fetch the partners.
	 */
	public List<Partner> getPartnersByType(Typology type) throws OHServiceException {
		return ioOperations.getByType(type);
	}

	/**
	 * Returns all active {@link Partner}s with the given type code.
	 *
	 * @param typeCode the typology code of the partner to retrieve.
	 * @return the list of active {@link Partner}s with the given type code.
	 * @throws OHServiceException when fails to fetch the partners.
	 */
	public List<Partner> getPartnersByTypeCode(String typeCode) throws OHServiceException {
		return ioOperations.getByTypeCode(typeCode);
	}

	/**
	 * Searches for active {@link Partner}s whose name, contact person or type description
	 * contains the given keyword (case-insensitive).
	 * Returns all active partners if the keyword is {@code null} or blank.
	 *
	 * @param keyword the search keyword; may be {@code null} or empty.
	 * @return the list of matching {@link Partner}s.
	 * @throws OHServiceException when fails to execute the search.
	 */
	public List<Partner> searchPartners(String keyword) throws OHServiceException {
		if (keyword == null || keyword.trim().isEmpty()) {
			return getPartners();
		}
		return ioOperations.search(keyword.trim());
	}

	/**
	 * Saves a {@link Partner}. Creates it if new, updates it if already existing.
	 *
	 * @param partner the partner to save.
	 * @return the saved {@link Partner}.
	 * @throws OHServiceException when fails to save the partner.
	 */
	public Partner savePartner(Partner partner) throws OHServiceException {
		validatePartner(partner);
		return ioOperations.save(partner);
	}

	/**
	 * Soft-deletes the {@link Partner} with the given id by setting its active flag to {@code 0}.
	 * The record is not physically removed from the database.
	 *
	 * @param id the id of the partner to delete.
	 * @throws OHServiceException when fails to delete the partner.
	 */
	public void deletePartner(int id) throws OHServiceException {
		ioOperations.softDelete(id);
	}

	/**
	 * Validates the given {@link Partner} before saving.
	 *
	 * @param partner the partner to validate.
	 * @throws OHDataValidationException if validation fails.
	 * @throws OHServiceException when fails to check uniqueness.
	 */
	private void validatePartner(Partner partner) throws OHDataValidationException, OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (partner.getName() == null || partner.getName().trim().isEmpty()) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.partner.validation.name.required.msg")));
		}

		if (partner.getType() == null) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.partner.validation.type.required.msg")));
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}