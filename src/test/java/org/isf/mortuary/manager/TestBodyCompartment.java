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

import org.isf.mortuary.model.BodyCompartment;
import org.isf.utils.exception.OHException;

public class TestBodyCompartment {
	private final int id = 1;
	private final String label = "BC001";
	private final String description = "Casier de la salle A";
	private final boolean deleted = false;

	public BodyCompartment setup(boolean usingSet) throws OHException {
		BodyCompartment bodyCompartment;

		if (usingSet) {
			bodyCompartment = new BodyCompartment();
			setParameters(bodyCompartment);
		} else {
			bodyCompartment = new BodyCompartment(id, label, description, deleted);
		}
		return bodyCompartment;
	}

	public void setParameters(BodyCompartment bodyCompartment) {
		bodyCompartment.setId(id);
		bodyCompartment.setLabel(label);
		bodyCompartment.setDescription(description);
		bodyCompartment.setDeleted(deleted);
	}

	public void check(BodyCompartment bodyCompartment) {
		assertThat(bodyCompartment.getId()).isEqualTo(id);
		assertThat(bodyCompartment.getLabel()).isEqualTo(label);
		assertThat(bodyCompartment.getDescription()).isEqualTo(description);
	}
}
