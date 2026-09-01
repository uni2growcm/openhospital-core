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
package org.isf.pregnancy.service;

import java.util.List;
import java.util.Optional;

import org.isf.pregnancy.model.PregnancyNewborn;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PregnancyNewbornIoOperations {

	private final PregnancyNewbornIoOperationRepository repository;

	public PregnancyNewbornIoOperations(PregnancyNewbornIoOperationRepository repository) {
		this.repository = repository;
	}

	public List<PregnancyNewborn> getByDeliveryId(int deliveryId) {
		return repository.findByDelivery_IdOrderByChildNumberAsc(deliveryId);
	}

	public Optional<PregnancyNewborn> getById(int id) {
		return repository.findById(id);
	}

	public PregnancyNewborn saveOrUpdate(PregnancyNewborn newborn) {
		return repository.save(newborn);
	}

	public void delete(PregnancyNewborn newborn) {
		repository.delete(newborn);
	}
}
