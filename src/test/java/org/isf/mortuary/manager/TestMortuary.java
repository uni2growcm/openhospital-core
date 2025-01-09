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

import java.time.LocalDate;

import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.model.Mortuary;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

public class TestMortuary {

	private final int id = 1;
	private final String place = "Salle A1";
	private final String provenance = "Urgences";
	private final LocalDate deathDate = LocalDate.of(2024, 12, 01);
	private final LocalDate enteredDate = LocalDate.of(2024, 12, 01);
	private final LocalDate releaseDate = LocalDate.of(2024, 12, 05);
	private final LocalDate provisionalReleaseDate = LocalDate.of(2024, 12, 04);
	private final String declaringName = "John Doe";
	private final String declaringPhone = "6543210001";
	private final String declaringNest = "12345";
	private final String familyName = null;
	private final String familyPhone = null;
	private final String familyNest = null;
	private final String locker = "L-001";

	public Mortuary setup(Patient patient, DeathReason deathReason, boolean usingSet) throws OHException {
		Mortuary mortuary;

		if (usingSet) {
			mortuary = new Mortuary();
			setParameters(patient, deathReason, mortuary);
		} else {
			mortuary = new Mortuary(id, place, patient, provenance, deathDate, enteredDate,
				releaseDate, provisionalReleaseDate, deathReason, declaringName, declaringPhone, declaringNest,
				familyName, familyPhone, familyNest, locker);
		}
		return mortuary;
	}

	public Mortuary setup(Patient patient, DeathReason deathReason, boolean usingSet, int id) throws OHException {
		Mortuary mortuary;
		if (usingSet) {
			mortuary = new Mortuary();
			setParameters(patient, deathReason, mortuary);
		} else {
			mortuary = new Mortuary(id, place, patient, provenance, deathDate, enteredDate,
				releaseDate, provisionalReleaseDate, deathReason, declaringName, declaringPhone, declaringNest,
				familyName, familyPhone, familyNest, locker);
		}
		return mortuary;
	}

	public void setParameters(Patient patient, DeathReason deathReason, Mortuary mortuary) {
		mortuary.setId(id);
		mortuary.setProvenance(provenance);
		mortuary.setPlace(place);
		mortuary.setPatient(patient);
		mortuary.setDeathReason(deathReason);
		mortuary.setDeathDate(deathDate);
		mortuary.setEnteredDate(enteredDate);
		mortuary.setReleaseDate(releaseDate);
		mortuary.setProvisionalReleaseDate(provisionalReleaseDate);
		mortuary.setDeclaringName(declaringName);
		mortuary.setDeclaringPhone(declaringPhone);
		mortuary.setDeclaringNest(declaringNest);
		mortuary.setFamilyName(familyName);
		mortuary.setFamilyPhone(familyPhone);
		mortuary.setFamilyNest(familyNest);
		mortuary.setLocker(locker);
	}

	public void check(Mortuary mortuary) {
		assertThat(mortuary.getId()).isEqualTo(id);
		assertThat(mortuary.getProvenance()).isEqualTo(provenance);
		assertThat(mortuary.getDeathDate()).isEqualTo(deathDate);
		assertThat(mortuary.getDeclaringName()).isEqualTo(declaringName);
	}
}