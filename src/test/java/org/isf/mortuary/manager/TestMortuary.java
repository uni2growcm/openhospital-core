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
package org.isf.mortuary.manager;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.isf.mortuary.model.BodyCompartment;
import org.isf.mortuary.model.Death;
import org.isf.mortuary.model.DeathReason;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;

public class TestMortuary {

	private final int id = 1;
	private final String place = "Salle A1";
	private final LocalDateTime deathDate = LocalDateTime.of(2024, 12, 1, 0, 0, 0);
	private final LocalDateTime enteredDate = LocalDateTime.of(2024, 12, 1, 0, 0, 0);
	private final LocalDateTime releaseDate = LocalDateTime.of(2024, 12, 5, 0, 0, 0);
	private final LocalDateTime provisionalReleaseDate = LocalDateTime.of(2024, 12, 4, 0, 0, 0);
	private final String declaringName = "John Doe";
	private final String declaringPhone = "6543210001";
	private final String declaringNid = "12345";
	private final boolean deleted = false;

	public Death setup(Patient patient, DeathReason deathReason, Ward ward, BodyCompartment locker, boolean usingSet) throws OHException {
		Death mortuary;

		if (usingSet) {
			mortuary = new Death();
			setParameters(patient, deathReason, ward, mortuary, locker);
		} else {
			mortuary = new Death(id, place, patient, ward, deathDate, enteredDate,
				releaseDate, provisionalReleaseDate, deathReason, declaringName, declaringPhone, declaringNid,
				null, null, null, locker, deleted);
		}
		return mortuary;
	}

	public Death setup(Patient patient, DeathReason deathReason, Ward ward, BodyCompartment locker, boolean usingSet, int id) throws OHException {
		Death mortuary;
		if (usingSet) {
			mortuary = new Death();
			setParameters(patient, deathReason, ward, mortuary, locker);
		} else {
			mortuary = new Death(id, place, patient, ward, deathDate, enteredDate,
				releaseDate, provisionalReleaseDate, deathReason, declaringName, declaringPhone, declaringNid,
				null, null, null, locker, deleted);
		}
		return mortuary;
	}

	public void setParameters(Patient patient, DeathReason deathReason, Ward ward, Death mortuary, BodyCompartment locker) {
		mortuary.setId(id);
		mortuary.setWard(ward);
		mortuary.setPlace(place);
		mortuary.setPatient(patient);
		mortuary.setDeathReason(deathReason);
		mortuary.setDate(deathDate);
		mortuary.setAdmissionDate(enteredDate);
		mortuary.setDischargeDate(releaseDate);
		mortuary.setEstimatedDischargeDate(provisionalReleaseDate);
		mortuary.setDeclaringName(declaringName);
		mortuary.setDeclaringPhone(declaringPhone);
		mortuary.setDeclaringNid(declaringNid);
		mortuary.setFamilyName(null);
		mortuary.setFamilyPhone(null);
		mortuary.setFamilyNid(null);
		mortuary.setLockerNumber(locker);
	}

	public void check(Death mortuary) {
		assertThat(mortuary.getId()).isEqualTo(id);
		assertThat(mortuary.getDate()).isEqualTo(deathDate);
		assertThat(mortuary.getDeclaringName()).isEqualTo(declaringName);
	}
}