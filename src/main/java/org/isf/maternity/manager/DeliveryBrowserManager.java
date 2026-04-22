package org.isf.maternity.manager;

import org.isf.maternity.model.Delivery;
import org.isf.maternity.service.DeliveryIoOperation;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

@Component
public class DeliveryBrowserManager {
	private final DeliveryIoOperation ioOperations;

	public DeliveryBrowserManager(DeliveryIoOperation ioOperations) {
		this.ioOperations = ioOperations;
	}

	/**
	 * Retrieve Delivery by Pregnancy.
	 *
	 * @param pregnancyId the pregnancy ID
	 * @return Delivery linked to pregnancy
	 * @throws OHServiceException if an error occurs
	 */
	public Delivery getDeliveryByPregnancy(Integer pregnancyId) throws OHServiceException {
		return ioOperations.getDeliveryByPregnancy(pregnancyId);
	}

	/**
	 * Create a new Delivery.
	 *
	 * @param delivery the Delivery to create
	 * @return created Delivery
	 * @throws OHServiceException if validation or persistence fails
	 */
	public Delivery newDelivery(Delivery delivery) throws OHServiceException {
		return ioOperations.newDelivery(delivery);
	}

	/**
	 * Update an existing Delivery.
	 *
	 * @param delivery the Delivery to update
	 * @return updated Delivery
	 * @throws OHServiceException if update fails
	 */
	public Delivery updateDelivery(Delivery delivery) throws OHServiceException {
		return ioOperations.updateDelivery(delivery);
	}

	/**
	 * Delete a Delivery.
	 *
	 * @param delivery the Delivery to delete
	 * @throws OHServiceException if deletion fails
	 */
	public void deleteDelivery(Delivery delivery) throws OHServiceException {
		ioOperations.deleteDelivery(delivery);
	}

	/**
	 * Check if delivery exists for pregnancy.
	 *
	 * @param pregnancyId pregnancy ID
	 * @return true if exists
	 * @throws OHServiceException if check fails
	 */
	public boolean isDeliveryPresent(Integer pregnancyId) throws OHServiceException {
		return ioOperations.isDeliveryPresent(pregnancyId);
	}
}
