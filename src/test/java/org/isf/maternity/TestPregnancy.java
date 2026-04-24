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

	private final LocalDateTime lmp = LocalDateTime.of(2025, 1, 15, 10, 0);
	private final  LocalDateTime eddLmp = LocalDateTime.of(2025, 10, 22, 0, 0);
	private final LocalDateTime eddScan = LocalDateTime.of(2025, 10, 20, 0, 0);

	public Pregnancy setup(Patient patient, boolean usingSet) throws OHException {
		Pregnancy pregnancy;

		if (usingSet) {
			pregnancy = new Pregnancy();
			setParameters(pregnancy, patient);
		} else {
			pregnancy = new Pregnancy(patient, PregnancyStatus.ONGOING);
			setParameters(pregnancy, patient);
		}

		return pregnancy;
	}

	private void setParameters(Pregnancy p, Patient patient) {
		p.setPatient(patient);
		p.setLmp(lmp);
		p.setEddLmp(eddLmp);
		p.setEddScan(eddScan);
		Integer gravidity = 1;
		p.setGravidity(gravidity);
		Integer parity = 0;
		p.setParity(parity);
		Integer miscarriages = 0;
		p.setMiscarriages(miscarriages);
		p.setRiskLevel(RiskLevel.LOW);
		p.setStatus(PregnancyStatus.ONGOING);
	}

	public void check(Pregnancy p) {
		assertThat(p.getStatus()).isEqualTo(PregnancyStatus.ONGOING);
		assertThat(p.getRiskLevel()).isEqualTo(RiskLevel.LOW);
		assertThat(p.getLmp()).isEqualTo(lmp);
	}
}