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