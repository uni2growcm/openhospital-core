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

import java.time.LocalDateTime;

import org.isf.maternity.model.Pregnancy;
import org.isf.maternity.model.PregnancyVisit;
import org.isf.typology.model.Typology;

public class TestVisit {

	private final LocalDateTime visitDate = LocalDateTime.of(2025, 3, 15, 10, 30);

	private final Integer weeks = 8;
	private final Integer days = 3;
	private final Double weight = 65.5;

	public PregnancyVisit setup(Pregnancy p, Typology vt, boolean usingSet) {
		PregnancyVisit v;

		if (usingSet) {
			v = new PregnancyVisit();
			set(v, p, vt);
		} else {
			v = new PregnancyVisit(p, vt, visitDate);
			set(v, p, vt);
		}

		return v;
	}

	private void set(PregnancyVisit v, Pregnancy p, Typology vt) {
		v.setPregnancy(p);
		v.setVisitDate(visitDate);
		v.setMaternalWeight(weight);
		v.setVisitType(vt);
	}

	public void check(PregnancyVisit v) {
		assertThat(v.getMaternalWeight()).isEqualTo(weight);
		assertThat(v.getVisitType()).isNotNull();
	}
}