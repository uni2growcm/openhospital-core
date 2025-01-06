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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.model.Mortuary;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DeathReasonIoOperations {

	private static DeathReasonRepository deathReasonRepository;

	public DeathReasonIoOperations() {
	}

	@Autowired
	public DeathReasonIoOperations(DeathReasonRepository deathReasonRepository) {
		this.deathReasonRepository = deathReasonRepository;
	}

	public List<DeathReason> getAll() throws OHServiceException {
		return deathReasonRepository.findAll();
	}

	public Optional<DeathReason> getById(int id) throws OHServiceException{
		return deathReasonRepository.findById(id);
	}
}

