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
package org.isf.opd.service;

import java.util.List;

import org.isf.disease.model.Disease;
import org.isf.opd.model.Opd;
import org.isf.opd.model.OpdDisease;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence class for the unbounded per-visit diagnosis list ({@link OpdDisease}).
 */
@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class OpdDiseaseIoOperation {

	private final OpdDiseaseIoOperationRepository repository;

	public OpdDiseaseIoOperation(OpdDiseaseIoOperationRepository opdDiseaseIoOperationRepository) {
		this.repository = opdDiseaseIoOperationRepository;
	}

	/**
	 * Returns all the {@link OpdDisease} rows for the specified opd id.
	 *
	 * @param opdCode the opd id
	 * @return the retrieved rows.
	 * @throws OHServiceException if an error occurs retrieving the rows.
	 */
	public List<OpdDisease> getOpdDiseasesByOpd(int opdCode) throws OHServiceException {
		return repository.findAllByOpdCode(opdCode);
	}

	/**
	 * Replaces the full set of {@link OpdDisease} rows for the given visit with one row per supplied
	 * {@link Disease}, in a single transaction.
	 *
	 * @param opd the visit whose diagnosis list is being replaced.
	 * @param diseases the new full list of diagnoses for the visit.
	 * @throws OHServiceException if an error occurs replacing the rows.
	 */
	public void replaceOpdDiseases(Opd opd, List<Disease> diseases) throws OHServiceException {
		List<OpdDisease> existing = repository.findAllByOpdCode(opd.getCode());
		repository.deleteAll(existing);
		for (Disease disease : diseases) {
			repository.save(new OpdDisease(opd, disease));
		}
	}
}
