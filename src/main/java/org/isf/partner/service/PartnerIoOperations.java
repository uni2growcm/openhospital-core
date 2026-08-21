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
package org.isf.partner.service;

import java.util.List;
import java.util.Optional;

import org.isf.partner.model.Partner;
import org.isf.partnertype.model.PartnerType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PartnerIoOperations {

	private final PartnerIoOperationRepository repository;

	public PartnerIoOperations(PartnerIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Returns all active {@link Partner}s ordered by name.
	 * @return the list of active {@link Partner}s.
	 * @throws OHServiceException if an error occurs retrieving data.
	 */
	public List<Partner> getAll() throws OHServiceException {
		return repository.findByActiveTrueOrderByNameAsc();
	}

	/**
	 * Returns an active {@link Partner} by its identifier.
	 * @param code the partner identifier.
	 * @return an {@link Optional} containing the matching {@link Partner}, or empty if not found or inactive.
	 * @throws OHServiceException if an error occurs retrieving data.
	 */
	public Optional<Partner> getById(int code) throws OHServiceException {
		return repository.findById(code).filter(Partner::isActive);
	}

	/**
	 * Returns all active {@link Partner}s of the given {@link PartnerType}.
	 * @param type the partner type.
	 * @return the list of matching active {@link Partner}s.
	 * @throws OHServiceException if an error occurs retrieving data.
	 */
	public List<Partner> getByType(PartnerType type) throws OHServiceException {
		return repository.findByTypeAndActiveTrue(type);
	}

	/**
	 * Returns all active {@link Partner}s whose type has the given code.
	 * @param typeCode the partner type code.
	 * @return the list of matching active {@link Partner}s.
	 * @throws OHServiceException if an error occurs retrieving data.
	 */
	public List<Partner> getByTypeCode(String typeCode) throws OHServiceException {
		return repository.findByType_CodeAndActiveTrue(typeCode);
	}

	/**
	 * Searches active {@link Partner}s whose name, contact person or type description contains the keyword.
	 * @param keyword the search keyword.
	 * @return the list of matching active {@link Partner}s.
	 * @throws OHServiceException if an error occurs retrieving data.
	 */
	public List<Partner> search(String keyword) throws OHServiceException {
		return repository.searchActive(keyword);
	}

	/**
	 * Saves or updates a {@link Partner}.
	 * @param partner the {@link Partner} to save.
	 * @return the saved {@link Partner}.
	 * @throws OHServiceException if an error occurs saving data.
	 */
	public Partner save(Partner partner) throws OHServiceException {
		return repository.save(partner);
	}

	/**
	 * Logically deletes a {@link Partner} (marks it inactive; it remains stored).
	 * @param code the identifier of the partner to delete.
	 * @throws OHServiceException if an error occurs updating data.
	 */
	public void softDelete(int code) throws OHServiceException {
		repository.softDelete(code);
	}

	/**
	 * Checks whether the given patient has at least one active partner.
	 * @param patientCode the patient's code.
	 * @return {@code true} if the patient has at least one active partner.
	 * @throws OHServiceException if an error occurs retrieving data.
	 */
	public boolean patientHasActivePartners(Integer patientCode) throws OHServiceException {
		return repository.countActivePartnersByPatientCode(patientCode) > 0;
	}
}
