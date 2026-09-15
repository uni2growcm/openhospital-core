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
package org.isf.hivchildfollowup.service;

import java.util.List;
import java.util.Optional;

import org.isf.hivchildfollowup.model.HivExposedChildVisit;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class HivExposedChildVisitIoOperations {

	private final HivExposedChildVisitIoOperationRepository repository;

	public HivExposedChildVisitIoOperations(HivExposedChildVisitIoOperationRepository repository) {
		this.repository = repository;
	}

	public List<HivExposedChildVisit> getByChildId(int childId) {
		return repository.findByChild_IdOrderByVisitDateAsc(childId);
	}

	public Optional<HivExposedChildVisit> getById(int id) {
		return repository.findById(id);
	}

	public HivExposedChildVisit saveOrUpdate(HivExposedChildVisit visit) {
		return repository.save(visit);
	}

	public void delete(HivExposedChildVisit visit) {
		repository.delete(visit);
	}
}
