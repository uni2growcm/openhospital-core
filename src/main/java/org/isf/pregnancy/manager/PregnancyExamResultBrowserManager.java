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
package org.isf.pregnancy.manager;

import java.util.List;

import org.isf.pregnancy.model.PregnancyExamResult;
import org.isf.pregnancy.service.PregnancyExamResultIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

@Component
public class PregnancyExamResultBrowserManager {

	private final PregnancyExamResultIoOperations ioOperations;
	private final PregnancyExamParameterBrowserManager examParameterManager;

	public PregnancyExamResultBrowserManager(PregnancyExamResultIoOperations ioOperations,
			PregnancyExamParameterBrowserManager examParameterManager) {
		this.ioOperations = ioOperations;
		this.examParameterManager = examParameterManager;
	}

	public List<PregnancyExamResult> getByVisitId(int visitId) throws OHServiceException {
		return ioOperations.getByVisitId(visitId);
	}

	/**
	 * Validates the outcome against the {@link org.isf.pregnancy.model.PregnancyExamParameter} definition
	 * (data type, max value, allowed values) before persisting it.
	 */
	public PregnancyExamResult saveResult(PregnancyExamResult result) throws OHServiceException {
		examParameterManager.validateOutcome(result.getExamParameter(), result.getOutcome());
		return ioOperations.saveOrUpdate(result);
	}

	public void deleteByVisitId(int visitId) throws OHServiceException {
		ioOperations.deleteByVisitId(visitId);
	}
}
