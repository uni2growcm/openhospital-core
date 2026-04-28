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

import org.isf.maternity.model.DeliveryMode;
import org.isf.maternity.model.PregnancyDelivery;
import org.isf.maternity.model.Pregnancy;
import org.isf.typology.model.Typology;

public class TestDelivery {

	private final LocalDateTime deliveryDate = LocalDateTime.of(2025, 10, 19, 12, 15);
	private final String father = "John Doe";

	public PregnancyDelivery setup(Pregnancy p, Typology dvt, boolean usingSet) {
		PregnancyDelivery d;

		if (usingSet) {
			d = new PregnancyDelivery();
			set(d, p, dvt);
		} else {
			d = new PregnancyDelivery(p, dvt,  deliveryDate);
			set(d, p, dvt);
		}

		return d;
	}

	private void set(PregnancyDelivery d, Pregnancy p, Typology pregnancyDeliveryType) {
		d.setPregnancy(p);
		d.setDeliveryDate(deliveryDate);
		d.setDeliveryMode(DeliveryMode.SVD);
		d.setFatherName(father);
		d.setDeliveryType(pregnancyDeliveryType);
	}

	public void check(PregnancyDelivery d) {
		assertThat(d.getDeliveryMode()).isEqualTo(DeliveryMode.SVD);
		assertThat(d.getFatherName()).isEqualTo(father);
	}
}