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
package org.isf.typology;

import org.isf.typology.model.Family;
import org.isf.typology.model.Typology;

import static org.assertj.core.api.Assertions.assertThat;

public class TestTypology {

	private final String code = "ANC";
	private final String description = "Antenatal Care";

	public Typology setupVisitType() {
		Typology vt = new Typology();
		vt.setCode(code);
		vt.setDescription(description);
		vt.setFamily(Family.VISITTYPE);
		return vt;
	}

	public void checkVisitType(Typology vt) {
		assertThat(vt.getCode()).isEqualTo(code);
		assertThat(vt.getDescription()).isEqualTo(description);
		assertThat(vt.getFamily()).isEqualTo(Family.VISITTYPE);
	}

	public Typology setupDeliveryType() {
		Typology vt = new Typology();
		vt.setCode(code + "Del");
		vt.setDescription(description);
		vt.setFamily(Family.DELIVERYTYPE);
		return vt;
	}

	public void checkDeliveryType(Typology vt) {
		assertThat(vt.getCode()).isEqualTo(code + "Del");
		assertThat(vt.getDescription()).isEqualTo(description);
		assertThat(vt.getFamily()).isEqualTo(Family.DELIVERYTYPE);
	}
}