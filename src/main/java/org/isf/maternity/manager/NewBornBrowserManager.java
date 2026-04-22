package org.isf.maternity.manager;

import org.isf.maternity.model.Newborn;
import org.isf.maternity.service.NewbornIoOperation;
import org.isf.utils.exception.OHServiceException;

import java.util.List;

public class NewBornBrowserManager {
	private final NewbornIoOperation ioOperations;

	public NewBornBrowserManager(NewbornIoOperation ioOperations) {
		this.ioOperations = ioOperations;
	}

	/**
	 * Get all newborns for a delivery.
	 *
	 * @param deliveryId delivery ID
	 * @return list of newborns
	 * @throws OHServiceException if retrieval fails
	 */
	public List<Newborn> getNewbornsByDelivery(Integer deliveryId) throws OHServiceException {
		return ioOperations.getNewbornsByDelivery(deliveryId);
	}

	/**
	 * Create a new newborn record.
	 *
	 * @param newborn newborn entity
	 * @return saved newborn
	 * @throws OHServiceException if creation fails
	 */
	public Newborn newNewborn(Newborn newborn) throws OHServiceException {
		return ioOperations.newNewborn(newborn);
	}

	/**
	 * Update newborn record.
	 *
	 * @param newborn newborn entity
	 * @return updated newborn
	 * @throws OHServiceException if update fails
	 */
	public Newborn updateNewborn(Newborn newborn) throws OHServiceException {
		return ioOperations.updateNewborn(newborn);
	}

	/**
	 * Delete newborn record.
	 *
	 * @param newborn newborn entity
	 * @throws OHServiceException if deletion fails
	 */
	public void deleteNewborn(Newborn newborn) throws OHServiceException {
		ioOperations.deleteNewborn(newborn);
	}

	/**
	 * Count newborns in a delivery.
	 *
	 * @param deliveryId delivery ID
	 * @return number of newborns
	 * @throws OHServiceException if query fails
	 */
	public long countNewborns(Integer deliveryId) throws OHServiceException {
		return ioOperations.countByDelivery(deliveryId);
	}
}
