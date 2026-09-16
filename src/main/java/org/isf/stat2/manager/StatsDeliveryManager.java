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
package org.isf.stat2.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.stat2.model.StatsDelivery;
import org.isf.stat2.service.StatsDeliveryIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Delivery statistics manager on top of the OH-538 pregnancy module
 * ({@link StatsDeliveryIoOperations}).
 */
@Component
public class StatsDeliveryManager {

	private final StatsDeliveryIoOperations ioOperations;

	public StatsDeliveryManager(StatsDeliveryIoOperations statsDeliveryIoOperations) {
		this.ioOperations = statsDeliveryIoOperations;
	}

	/**
	 * Returns paginated delivery statistics with all filters applied.
	 *
	 * @param periodFrom             start of delivery period, null for no start
	 * @param periodTo               end of delivery period, null for no end
	 * @param motherAgeMin           minimum mother age, null for no lower bound
	 * @param motherAgeMax           maximum mother age, null for no upper bound
	 * @param sex                    newborn sex (M/F), null for no filter
	 * @param weightMin              minimum birth weight (kg), null for no lower bound
	 * @param weightMax              maximum birth weight (kg), null for no upper bound
	 * @param deliveryType           delivery type description, null for no filter
	 * @param deliveryResultType     delivery result type description, null for no filter
	 * @param hivExposed             HIV exposed newborn, null for no filter
	 * @param congenitalMalformation congenital malformation present, null for no filter
	 * @param disease                mother disease description, null for no filter
	 * @param dischargeType          mother discharge type description, null for no filter
	 * @param page                   page number (0-indexed)
	 * @param size                   page size
	 * @return a Page of StatsDelivery matching the filters
	 * @throws OHServiceException if validation fails or an error occurs
	 */
	public Page<StatsDelivery> getDeliveriesStats(
		LocalDateTime periodFrom,
		LocalDateTime periodTo,
		Integer motherAgeMin,
		Integer motherAgeMax,
		String sex,
		Double weightMin,
		Double weightMax,
		String deliveryType,
		String deliveryResultType,
		Boolean hivExposed,
		Boolean congenitalMalformation,
		String disease,
		String dischargeType,
		int page,
		int size
	) throws OHServiceException {

		validateFilters(periodFrom, periodTo, motherAgeMin, motherAgeMax, weightMin, weightMax);

		return ioOperations.getDeliveriesStats(
			periodFrom, periodTo,
			motherAgeMin, motherAgeMax,
			sex, weightMin, weightMax,
			deliveryType, deliveryResultType,
			hivExposed, congenitalMalformation,
			disease, dischargeType,
			page, size
		);
	}

	private void validateFilters(
		LocalDateTime periodFrom,
		LocalDateTime periodTo,
		Integer motherAgeMin,
		Integer motherAgeMax,
		Double weightMin,
		Double weightMax
	) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (periodFrom != null && periodTo != null && periodTo.isBefore(periodFrom)) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.stat.error.pleaseinsertvalidperiod")));
		}

		if (motherAgeMin != null && motherAgeMax != null && motherAgeMin > motherAgeMax) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.stat.error.pleaseinsertvalidmotheragerange")));
		}

		if (weightMin != null && weightMax != null && weightMin > weightMax) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.stat.error.pleaseinsertvalidweightrange")));
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
