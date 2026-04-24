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

import org.isf.maternity.model.HivStatus;
import org.isf.maternity.model.PregnancyDelivery;
import org.isf.maternity.model.Newborn;
import org.isf.patient.model.Patient;

import java.time.LocalDateTime;

public class TestNewBorn {
	private final double weight = 3200;
	private final LocalDateTime date = LocalDateTime.of(2025, 10, 19, 15, 15);

	public Newborn setup(Patient babyPatient, PregnancyDelivery d, boolean usingSet) {
		Newborn n;

		if (usingSet) {
			n = new Newborn();
			set(n, d);
		} else {
			n = new Newborn(babyPatient, d, date, HivStatus.UNKNOWN);
			set(n, d);
		}

		return n;
	}

	private void set(Newborn n, PregnancyDelivery d) {
		n.setDelivery(d);
		n.setBirthWeight(weight);
		n.setHivStatus(HivStatus.UNKNOWN);
	}

	public void check(Newborn n) {
		assertThat(n.getBirthWeight()).isEqualTo(weight);
		assertThat(n.getHivStatus()).isEqualTo(HivStatus.UNKNOWN);
	}
}
