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
package org.isf.prescriber.service;

import java.util.List;

import org.isf.prescriber.model.Prescriber;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PrescriberIoOperation {

	private PrescriberIoOperationRepository repository;

	public PrescriberIoOperation(PrescriberIoOperationRepository prescriberIoOperationRepository) {
		this.repository = prescriberIoOperationRepository;
	}

	/**
	 * Method that returns all {@link Prescriber}s in a list
	 *
	 * @return the list of all Prescribers
	 * @throws OHServiceException
	 */
	public List<Prescriber> getPrescriber() throws OHServiceException {
		return repository.findAllByOrderByDescriptionAsc();
	}

	/**
	 * Method that updates an already existing {@link Prescriber}
	 *
	 * @param prescriber
	 * @return the persisted updated Prescriber object.
	 * @throws OHServiceException
	 */
	public Prescriber updatePrescriber(Prescriber prescriber) throws OHServiceException {
		return repository.save(prescriber);
	}

	/**
	 * Method that create a new {@link Prescriber}.
	 *
	 * @param prescriber
	 * @return the persisted new Prescriber object.
	 * @throws OHServiceException
	 */
	public Prescriber newPrescriber(Prescriber prescriber) throws OHServiceException {
		return repository.save(prescriber);
	}

	/**
	 * Method that deletes a {@link Prescriber}.
	 *
	 * @param prescriber
	 * @throws OHServiceException
	 */
	public void deletePrescriber(Prescriber prescriber) throws OHServiceException {
		repository.delete(prescriber);
	}

	/**
	 * Method that checks if a {@link Prescriber} already exists.
	 *
	 * @param code
	 * @return true - if the Prescriber already exists
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}
}
