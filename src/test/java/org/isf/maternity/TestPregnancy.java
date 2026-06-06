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
package org.isf.maternity;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.maternity.model.Pregnancy;
import org.isf.maternity.model.PregnancyStatus;
import org.isf.maternity.model.RiskLevel;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

import java.time.LocalDateTime;

public class TestPregnancy {

	private final LocalDateTime date  = LocalDateTime.of(2025, 2, 15, 10, 0);
	private final LocalDateTime lmp = LocalDateTime.of(2025, 1, 15, 10, 0);
	private final  LocalDateTime eddLmp = LocalDateTime.of(2025, 10, 22, 0, 0);
	private final LocalDateTime eddScan = LocalDateTime.of(2025, 10, 20, 0, 0);

	// Test data for obstetric history
	private final Integer testGravidity = 4;
	private final Integer testParity = 2;
	private final Integer testMiscarriages = 1;
	private final Integer testTermDeliveries = 2;
	private final Integer testPretermDeliveries = 1;
	private final Integer testLivingChildren = 2;
	private final Integer testStillbirths = 0;
	private final Integer testDeceasedChildren = 0;
	private final Integer testDesiredChildren = 4;
	private final String testBreastfeeding = "Y";
	private final Integer testLastChildYears = 2;
	private final Integer testLastChildMonths = 3;
	private final Integer testLastChildWeeks = 0;
	private final Integer testLastChildDays = 0;

	public Pregnancy setup(Patient patient, boolean usingSet) throws OHException {
		Pregnancy pregnancy;

		if (usingSet) {
			pregnancy = new Pregnancy();
		} else {
			pregnancy = new Pregnancy(patient, date, lmp);
		}
		setParameters(pregnancy, patient);
		return pregnancy;
	}

	private void setParameters(Pregnancy p, Patient patient) {
		p.setPatient(patient);
		p.setLmp(lmp);
		p.setEddLmp(eddLmp);
		p.setEddScan(eddScan);

		// Obstetric history
		p.setGravidity(testGravidity);
		p.setParity(testParity);
		p.setMiscarriages(testMiscarriages);
		p.setTermDeliveries(testTermDeliveries);
		p.setPretermDeliveries(testPretermDeliveries);
		p.setLivingChildren(testLivingChildren);
		p.setStillbirths(testStillbirths);
		p.setDeceasedChildren(testDeceasedChildren);
		p.setDesiredChildren(testDesiredChildren);
		p.setBreastfeeding(testBreastfeeding);
		p.setLastChildYears(testLastChildYears);
		p.setLastChildMonths(testLastChildMonths);
		p.setLastChildWeeks(testLastChildWeeks);
		p.setLastChildDays(testLastChildDays);

		p.setRiskLevel(RiskLevel.LOW);
		p.setStatus(PregnancyStatus.ONGOING);
	}

	public void check(Pregnancy p) {
		assertThat(p.getStatus()).isEqualTo(PregnancyStatus.ONGOING);
		assertThat(p.getRiskLevel()).isEqualTo(RiskLevel.LOW);
		assertThat(p.getLmp()).isEqualTo(lmp);

		// Obstetric history checks
		assertThat(p.getGravidity()).isEqualTo(testGravidity);
		assertThat(p.getParity()).isEqualTo(testParity);
		assertThat(p.getMiscarriages()).isEqualTo(testMiscarriages);
		assertThat(p.getTermDeliveries()).isEqualTo(testTermDeliveries);
		assertThat(p.getPretermDeliveries()).isEqualTo(testPretermDeliveries);
		assertThat(p.getLivingChildren()).isEqualTo(testLivingChildren);
		assertThat(p.getStillbirths()).isEqualTo(testStillbirths);
		assertThat(p.getDeceasedChildren()).isEqualTo(testDeceasedChildren);
		assertThat(p.getDesiredChildren()).isEqualTo(testDesiredChildren);
		assertThat(p.getBreastfeeding()).isEqualTo(testBreastfeeding);
		assertThat(p.getLastChildYears()).isEqualTo(testLastChildYears);
		assertThat(p.getLastChildMonths()).isEqualTo(testLastChildMonths);
		assertThat(p.getLastChildWeeks()).isEqualTo(testLastChildWeeks);
		assertThat(p.getLastChildDays()).isEqualTo(testLastChildDays);
	}
}