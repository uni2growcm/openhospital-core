/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

package org.isf.mortuary.manager;

import org.isf.mortuary.model.Mortuary;
import org.isf.mortuary.service.MortuaryIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MortuaryBrowserManager {
	private MortuaryIoOperations ioOperations;

	public MortuaryBrowserManager(MortuaryIoOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	/**
	 * Returns all stored {@link Mortuary}s.
	 * In case of error a message error is shown and a {@code null} value is returned.
	 *
	 * @return the stored Mortuaries.
	 * @throws OHServiceException
	 */
	public List<Mortuary> getMortuaries() throws OHServiceException {
		return ioOperations.getMortuaries();
	}

	/**
	 * Updates the specified {@link Mortuary}.
	 *
	 * @param mortuary - the {@link Mortuary} to update.
	 * @return mortuary that has been updated.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public Mortuary updateMortary(Mortuary mortuary) throws OHServiceException {
		return ioOperations.updateMortary(mortuary);
	}

	/**
	 * Stores the specified {@link Mortuary}.
	 *
	 * @param mortuary the mortuary to store.
	 * @return mortuary that has been stored.
	 * @throws OHServiceException if an error occurs storing the mortuary.
	 */
	public Mortuary newMortuary(Mortuary mortuary) throws OHServiceException {
		return ioOperations.newMortuary(mortuary);
	}
}
