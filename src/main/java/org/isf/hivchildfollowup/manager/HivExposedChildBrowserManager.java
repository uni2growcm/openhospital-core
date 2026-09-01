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
package org.isf.hivchildfollowup.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.hivchildfollowup.model.HivExposedChild;
import org.isf.hivchildfollowup.model.HivExposedChildVisit;
import org.isf.hivchildfollowup.service.HivExposedChildIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

/**
 * Manages {@link HivExposedChild} (a PTME follow-up enrollment) together with its
 * {@link HivExposedChildVisit} history: saving a child also saves/updates/removes its visits in the same
 * operation, exactly like {@code PregnancyDeliveryBrowserManager} does for a delivery and its newborns.
 */
@Component
public class HivExposedChildBrowserManager {

	private final HivExposedChildIoOperations ioOperations;

	public HivExposedChildBrowserManager(HivExposedChildIoOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	public List<HivExposedChild> getByMotherPatientId(int motherPatientCode) throws OHServiceException {
		return ioOperations.getByMotherPatientId(motherPatientCode);
	}

	public List<HivExposedChild> getAll() throws OHServiceException {
		return ioOperations.getAll();
	}

	public HivExposedChild saveOrUpdate(HivExposedChild child) throws OHServiceException {
		validate(child);
		for (HivExposedChildVisit visit : child.getVisits()) {
			visit.setChild(child);
		}
		return ioOperations.saveOrUpdate(child);
	}

	public void delete(HivExposedChild child) throws OHServiceException {
		ioOperations.delete(child);
	}

	protected void validate(HivExposedChild child) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (child.getMotherPatient() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseselectapatient.msg")));
		}
		if (child.getDateOfBirth() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hivchildfollowup.pleaseinsertthedateofbirth.msg")));
		}
		if (child.getFinalStatus() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hivchildfollowup.pleaseselectastatus.msg")));
		}
		for (HivExposedChildVisit visit : child.getVisits()) {
			if (visit.getVisitDate() == null) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hivchildfollowup.pleaseinsertavaliddateforeachvisit.msg")));
				break;
			}
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
