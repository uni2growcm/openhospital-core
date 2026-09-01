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
import java.util.Optional;

import org.isf.pregnancy.model.PregnancyNewborn;
import org.isf.pregnancy.service.PregnancyNewbornIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

/**
 * Read-oriented manager for {@link PregnancyNewborn}, used by other modules (e.g. the Phase 5 HIV-exposed
 * child follow-up) that need to look up a newborn by id without going through its parent
 * {@link org.isf.pregnancy.model.PregnancyDelivery}.
 */
@Component
public class PregnancyNewbornBrowserManager {

	private final PregnancyNewbornIoOperations ioOperations;

	public PregnancyNewbornBrowserManager(PregnancyNewbornIoOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	public Optional<PregnancyNewborn> getById(int id) throws OHServiceException {
		return ioOperations.getById(id);
	}

	public List<PregnancyNewborn> getByDeliveryId(int deliveryId) throws OHServiceException {
		return ioOperations.getByDeliveryId(deliveryId);
	}
}
