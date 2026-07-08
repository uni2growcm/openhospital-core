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
package org.isf.maternity.manager;

import org.isf.generaldata.MessageBundle;
import org.isf.maternity.model.PregnancyDelivery;
import org.isf.maternity.service.PregnancyDeliveryIoOperation;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class PregnancyDeliveryBrowserManager {

	private final PregnancyDeliveryIoOperation service;

	public PregnancyDeliveryBrowserManager(PregnancyDeliveryIoOperation service) {
		this.service = service;
	}

	/**
	 * Retrieve a delivery linked to a specific pregnancy.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @return delivery if found, otherwise null
	 * @throws OHServiceException if retrieval fails
	 */
	public PregnancyDelivery getDeliveryByPregnancy(Integer pregnancyId) throws OHServiceException {
		return service.getDeliveryByPregnancy(pregnancyId).orElse(null);
	}

	/**
	 * Check whether a delivery exists for a given pregnancy.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @return true if a delivery exists, false otherwise
	 * @throws OHServiceException if check fails
	 */
	public boolean hasDelivery(Integer pregnancyId) throws OHServiceException {
		return service.hasDelivery(pregnancyId);
	}

	/**
	 * Create a new {@link PregnancyDelivery}.
	 *
	 * @param delivery the delivery to create
	 * @return created delivery
	 * @throws OHServiceException if creation fails
	 */
	public PregnancyDelivery newDelivery(PregnancyDelivery delivery) throws OHServiceException {
		validateDelivery(delivery);
		return service.newDelivery(delivery);
	}

	/**
	 * Update an existing {@link PregnancyDelivery}.
	 *
	 * @param delivery the delivery to update
	 * @return updated delivery
	 * @throws OHServiceException if update fails
	 */
	public PregnancyDelivery updateDelivery(PregnancyDelivery delivery) throws OHServiceException {
		validateDelivery(delivery);
		return service.updateDelivery(delivery);
	}

	/**
	 * Delete a {@link PregnancyDelivery}.
	 *
	 * @param delivery the delivery to delete
	 * @throws OHServiceException if deletion fails
	 */
	public void deleteDelivery(PregnancyDelivery delivery) throws OHServiceException {
		service.deleteDelivery(delivery);
	}

	/**
	 * Ensure a pregnancy has a registered delivery.
	 *
	 * @param pregnancyId the pregnancy identifier
	 * @return the delivery if present
	 * @throws OHServiceException if no delivery exists
	 */
	public PregnancyDelivery validateDeliveryExists(Integer pregnancyId) throws OHServiceException {
		return service.validateDeliveryExists(pregnancyId);
	}

	/**
	 * Validates delivery data before saving/updating.
	 * Checks all business rules for labor onset, ROM, and blood loss.
	 *
	 * @param delivery the delivery to validate
	 * @throws OHServiceException if validation fails
	 */
	private void validateDelivery(PregnancyDelivery delivery) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		LocalDateTime deliveryDate = delivery.getDeliveryDate();
		LocalDateTime laborOnset = delivery.getLaborOnsetDateTime();
		LocalDateTime rom = delivery.getRuptureMembranesDateTime();

		if (laborOnset != null && deliveryDate != null && laborOnset.isAfter(deliveryDate)) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.maternity.delivery.laboronset.beforedelivery.msg")));
		}

		if (rom != null && deliveryDate != null && rom.isAfter(deliveryDate)) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.maternity.delivery.rom.beforedelivery.msg")));
		}

		if (laborOnset != null && rom != null && rom.isBefore(laborOnset)) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.maternity.delivery.rom.afterlaboronset.msg")));
		}

		if (laborOnset != null && deliveryDate != null) {
			long hoursBetween = ChronoUnit.HOURS.between(laborOnset, deliveryDate);
			if (hoursBetween > 72) {
				errors.add(new OHExceptionMessage(
					MessageBundle.formatMessage("angal.maternity.delivery.laboronset.toofar.msg", hoursBetween)));
			}
		}

		if (rom != null && deliveryDate != null) {
			long hoursBetween = ChronoUnit.HOURS.between(rom, deliveryDate);
			if (hoursBetween > 72) {
				errors.add(new OHExceptionMessage(
					MessageBundle.formatMessage("angal.maternity.delivery.rom.toofar.msg", hoursBetween)));
			}
		}

		if (delivery.getEstimatedBloodLoss() != null && delivery.getEstimatedBloodLoss() < 0) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.maternity.delivery.bloodloss.positive.msg")));
		}

		if (delivery.getEstimatedBloodLoss() != null && delivery.getEstimatedBloodLoss() > 5000) {
			errors.add(new OHExceptionMessage(
				MessageBundle.formatMessage("angal.maternity.delivery.bloodloss.max.msg",
					delivery.getEstimatedBloodLoss())));
		}

		if (laborOnset != null && laborOnset.isAfter(java.time.LocalDateTime.now())) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.maternity.delivery.laboronset.future.msg")));
		}

		if (rom != null && rom.isAfter(java.time.LocalDateTime.now())) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.maternity.delivery.rom.future.msg")));
		}

		if (deliveryDate != null && deliveryDate.isAfter(java.time.LocalDateTime.now())) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.maternity.delivery.deliverydate.future.msg")));
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}