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

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.isf.admission.model.Admission;
import org.isf.generaldata.MessageBundle;
import org.isf.pregnancy.model.PregnancyDelivery;
import org.isf.pregnancy.model.PregnancyNewborn;
import org.isf.pregnancy.service.PregnancyDeliveryIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

/**
 * Manages {@link PregnancyDelivery} (the childbirth event of an {@link Admission}) together with its
 * {@link PregnancyNewborn} rows (1 to 4 children), and computes the length-of-stay in nights used on the
 * delivery screen.
 */
@Component
public class PregnancyDeliveryBrowserManager {

	private final PregnancyDeliveryIoOperations ioOperations;

	public PregnancyDeliveryBrowserManager(PregnancyDeliveryIoOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	public List<PregnancyDelivery> getByAdmissionId(int admissionId) throws OHServiceException {
		return ioOperations.getByAdmissionId(admissionId);
	}

	public List<PregnancyDelivery> getByPatientId(int patientCode) throws OHServiceException {
		return ioOperations.getByPatientId(patientCode);
	}

	/**
	 * Returns the most recent {@link PregnancyDelivery} recorded for the given admission, or {@code null} if
	 * none has been entered yet.
	 */
	public PregnancyDelivery getCurrentForAdmission(int admissionId) throws OHServiceException {
		List<PregnancyDelivery> deliveries = getByAdmissionId(admissionId);
		return deliveries.isEmpty() ? null : deliveries.get(0);
	}

	public PregnancyDelivery saveOrUpdate(PregnancyDelivery delivery) throws OHServiceException {
		validate(delivery);
		for (PregnancyNewborn newborn : delivery.getNewborns()) {
			newborn.setDelivery(delivery);
		}
		return ioOperations.saveOrUpdate(delivery);
	}

	public void delete(PregnancyDelivery delivery) throws OHServiceException {
		ioOperations.delete(delivery);
	}

	/**
	 * Length of stay in nights: {@code 0} when the patient is discharged the same day she gave birth (or has
	 * not been discharged yet), otherwise the number of nights between the admission and discharge dates.
	 */
	public int computeNightsOfStay(Admission admission) {
		if (admission == null || admission.getAdmDate() == null || admission.getDisDate() == null) {
			return 0;
		}
		long nights = ChronoUnit.DAYS.between(admission.getAdmDate().toLocalDate(), admission.getDisDate().toLocalDate());
		return (int) Math.max(0, nights);
	}

	protected void validate(PregnancyDelivery delivery) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (delivery.getAdmission() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.cpn.pleasesavetheadmissionfirst.msg")));
		}
		if (delivery.getNewborns().size() > PregnancyNewborn.MAX_CHILDREN) {
			errors.add(new OHExceptionMessage(
					MessageBundle.formatMessage("angal.cpn.amaximumofnnewbornsissupported.fmt.msg", PregnancyNewborn.MAX_CHILDREN)));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
