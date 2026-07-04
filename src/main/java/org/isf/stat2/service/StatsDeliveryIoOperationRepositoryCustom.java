package org.isf.stat2.service;

import java.time.LocalDateTime;

import org.isf.stat2.model.StatsDelivery;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StatsDeliveryIoOperationRepositoryCustom {

	Page<StatsDelivery> findDeliveriesStatsByFilters(
		// ===== PERIOD =====
		LocalDateTime periodFrom,
		LocalDateTime periodTo,

		// ===== MÈRE =====
		Integer motherAgeMin,
		Integer motherAgeMax,

		// ===== SEX & WEIGHT (Nouveau-né) =====
		String sex,
		Double weightMin,
		Double weightMax,

		// ===== DELIVERY TYPES & RESULTS =====
		String deliveryType,
		String deliveryResultType,
		String deliveryMode,
		String laborDuration,
		String romRange,
		String perinealIntegrity,
		Boolean placentaComplete,
		String bloodLossRange,

		// ===== NEWBORN =====
		String newbornSex,
		String birthWeightRange,
		String neonatalStatus,
		String apgar1Range,
		String apgar5Range,
		Boolean resuscitationRequired,
		String cryTime,
		String hivStatus,
		Boolean congenitalAnomalies,

		// ===== DISEASES & DISCHARGE =====
		String disease,
		String dischargeType,

		// ===== PAGINATION =====
		Pageable pageable
	) throws OHServiceException;
}