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
package org.isf.stat2.service;

import java.time.LocalDateTime;

import org.isf.stat2.model.StatsDelivery;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class StatsDeliveryIoOperations {

	private final StatsDeliveryIoOperationRepositoryCustom repository;

	public StatsDeliveryIoOperations(StatsDeliveryIoOperationRepositoryCustom repository) {
		this.repository = repository;
	}

	/**
	 * Returns a paginated list of {@link StatsDelivery} matching all delivery filters.
	 *
	 * @param periodFrom start of delivery period, null for no start
	 * @param periodTo end of delivery period, null for no end
	 * @param sex newborn sex (M/F), null for no filter
	 * @param weightMin minimum birth weight, null for no lower bound
	 * @param weightMax maximum birth weight, null for no upper bound
	 * @param deliveryType delivery type description, null for no filter
	 * @param deliveryResultType delivery result (via neonatalStatus), null for no filter
	 * @param deliveryMode delivery mode, null for no filter
	 * @param laborDuration labor duration range, null for no filter
	 * @param romRange ROM range (<18h, >=18h), null for no filter
	 * @param perinealIntegrity perineal integrity status, null for no filter
	 * @param placentaComplete true/false, null for no filter
	 * @param bloodLossRange blood loss range, null for no filter
	 * @param newbornSex newborn sex, null for no filter (alias for sex)
	 * @param birthWeightRange birth weight category, null for no filter
	 * @param neonatalStatus neonatal status, null for no filter
	 * @param apgar1Range APGAR 1min range, null for no filter
	 * @param apgar5Range APGAR 5min range, null for no filter
	 * @param resuscitationRequired true/false, null for no filter
	 * @param cryTime cry time, null for no filter
	 * @param hivStatus HIV status, null for no filter
	 * @param congenitalAnomalies true/false, null for no filter
	 * @param disease disease description, null for no filter
	 * @param dischargeType discharge type description, null for no filter
	 * @param page page number (0-indexed)
	 * @param size page size
	 * @return a Page of StatsDelivery matching the filters
	 * @throws OHServiceException if an error occurs during the database operation
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
		String deliveryMode,
		String laborDuration,
		String romRange,
		String perinealIntegrity,
		Boolean placentaComplete,
		String bloodLossRange,
		String newbornSex,
		String birthWeightRange,
		String neonatalStatus,
		String apgar1Range,
		String apgar5Range,
		Boolean resuscitationRequired,
		String cryTime,
		String hivStatus,
		Boolean congenitalAnomalies,
		String disease,
		String dischargeType,
		int page,
		int size
	) throws OHServiceException {

		return repository.findDeliveriesStatsByFilters(
			periodFrom, periodTo,
			motherAgeMin, motherAgeMax,
			sex, weightMin, weightMax,
			deliveryType, deliveryResultType, deliveryMode,
			laborDuration, romRange,
			perinealIntegrity, placentaComplete, bloodLossRange,
			newbornSex, birthWeightRange, neonatalStatus,
			apgar1Range, apgar5Range, resuscitationRequired,
			cryTime, hivStatus, congenitalAnomalies,
			disease, dischargeType,
			PageRequest.of(page, size)
		);
	}
}