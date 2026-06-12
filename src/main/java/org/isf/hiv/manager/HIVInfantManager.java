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
package org.isf.hiv.manager;

import java.time.LocalDate;
import java.util.List;

import org.isf.hiv.model.HIVInfant;
import org.isf.hiv.model.HIVInfant.HIVInfantStatus;
import org.isf.hiv.model.HIVInfant.FeedingType;
import org.isf.hiv.service.HIVInfantIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class HIVInfantManager {

	@Autowired
	private HIVInfantIoOperations ioOperations;

	public HIVInfant newInfant(HIVInfant infant) throws OHServiceException {
		return ioOperations.saveInfant(infant);
	}

	public HIVInfant updateInfant(HIVInfant infant) throws OHServiceException {
		return ioOperations.updateInfant(infant);
	}

	public void deleteInfant(HIVInfant infant) throws OHServiceException {
		ioOperations.deleteInfant(infant);
	}

	public HIVInfant getInfantById(Integer id) throws OHServiceException {
		return ioOperations.findInfantById(id);
	}

	public List<HIVInfant> getInfantsByPatientCode(Integer patientCode) throws OHServiceException {
		return ioOperations.findInfantsByPatientCode(patientCode);
	}

	public Page<HIVInfant> getAllInfants(Pageable pageable) throws OHServiceException {
		return ioOperations.findAllInfants(pageable);
	}

	public Page<HIVInfant> getInfantsByStatus(HIVInfantStatus status, Pageable pageable) throws OHServiceException {
		return ioOperations.findInfantsByStatus(status, pageable);
	}

	public Page<HIVInfant> getInfantsByFilters(
		Integer patientCode,
		HIVInfantStatus status,
		FeedingType feedingType,
		LocalDate dateFrom,
		LocalDate dateTo,
		LocalDate startDateFrom,
		LocalDate startDateTo,
		Pageable pageable) throws OHServiceException {
		return ioOperations.findInfantsByFilters(
			patientCode, status, feedingType,
			dateFrom, dateTo, startDateFrom, startDateTo, pageable);
	}
}