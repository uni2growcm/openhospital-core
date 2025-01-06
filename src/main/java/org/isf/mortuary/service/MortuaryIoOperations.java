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

import org.isf.mortuary.model.Mortuary;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class MortuaryIoOperations {

	private final MortuaryRepository mortuaryRepository;

	public MortuaryIoOperations(MortuaryRepository mortuaryRepository) {
		this.mortuaryRepository = mortuaryRepository;
	}

	/**
	 * Store the specified {@link Mortuary}.
	 * @param mortuary the death  to store.
	 * @return {@link Mortuary} if the {@link Mortuary} has been stored, null otherwise.
	 * @throws OHException if an error occurs during the store operation.
	 */
	public Mortuary add(Mortuary mortuary) throws OHException {
		return mortuaryRepository.save(mortuary);
	}

	/**
	 * Get all the {@link Mortuary}s.
	 *
	 * @return a list of deaths.
	 * @throws OHException if an error occurs retrieving the deaths.
	 */
	public List<Mortuary> getAll() throws OHException {
		return mortuaryRepository.findAll();
	}

	/**
	 *
	 * method that update an existing {@link Mortuary} in the db
	 *
	 * @param mortuary - the {@link Mortuary} to update
	 * @return {@link Mortuary} has been updated
	 * @throws OHException
	 */
	public Mortuary update(Mortuary mortuary) throws OHException{
		return mortuaryRepository.save(mortuary);
	}

	/**
	 * method that delete a death
	 *
	 * @param mortuary
	 * @throws OHException
	 */
	public void delete(Mortuary mortuary) throws OHException {
		mortuaryRepository.delete(mortuary);
	}

//	public List<Mortuary> getMortuariesWhereData(
//		String patientName,
//		String deathReason,
//		String ward,
//		LocalDateTime movFrom,
//		LocalDateTime movTo
//	) throws OHServiceException {
//		movFrom = movFrom.withHour(0).withMinute(0);
//		movTo = movTo.withHour(23).withMinute(59);
//
//		return mortuaryRepository.getMortuariesWhereData(
//			patientName,
//			deathReason,
//			ward,
//			TimeTools.truncateToSeconds(movFrom),
//			TimeTools.truncateToSeconds(movTo)
//		);
//	}

	public List<Mortuary> getMortuariesWhereData(String patientName) {
		return mortuaryRepository.getMortuariesWhereData(patientName);
	}

	public List<Mortuary> getMortuariesWhereData(
		String patientName,
		String provenance,
		String deathReason
	){
		return mortuaryRepository.findAllWhereData(patientName, provenance, deathReason);
	}
}
