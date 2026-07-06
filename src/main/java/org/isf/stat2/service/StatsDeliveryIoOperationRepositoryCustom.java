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
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StatsDeliveryIoOperationRepositoryCustom {

	Page<StatsDelivery> findDeliveriesStatsByFilters(
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
		Pageable pageable
	) throws OHServiceException;
}