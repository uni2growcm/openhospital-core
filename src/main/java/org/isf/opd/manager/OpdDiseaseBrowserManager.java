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
package org.isf.opd.manager;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.disease.model.Disease;
import org.isf.opd.model.Opd;
import org.isf.opd.model.OpdDisease;
import org.isf.opd.service.OpdDiseaseIoOperation;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

/**
 * Manager for the unbounded per-visit diagnosis list ({@link OpdDisease}), used when
 * {@code GeneralData.ENHANCEDDIAGNOSTICINOPDEDIT} is enabled.
 */
@Component
public class OpdDiseaseBrowserManager {

	private final OpdDiseaseIoOperation ioOperation;

	public OpdDiseaseBrowserManager(OpdDiseaseIoOperation opdDiseaseIoOperation) {
		this.ioOperation = opdDiseaseIoOperation;
	}

	/**
	 * Returns the full diagnosis list currently saved for the given visit.
	 *
	 * @param opd the visit.
	 * @return the diagnoses, in the order they were originally added.
	 * @throws OHServiceException
	 */
	public List<Disease> getOpdDiseases(Opd opd) throws OHServiceException {
		return ioOperation.getOpdDiseasesByOpd(opd.getCode()).stream()
						.map(OpdDisease::getDisease)
						.collect(Collectors.toList());
	}

	/**
	 * Replaces the full diagnosis list for the given visit.
	 *
	 * @param opd the visit.
	 * @param diseases the new full list of diagnoses for the visit.
	 * @throws OHServiceException
	 */
	public void replaceOpdDiseases(Opd opd, List<Disease> diseases) throws OHServiceException {
		ioOperation.replaceOpdDiseases(opd, diseases);
	}
}
