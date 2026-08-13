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
package org.isf.opd;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.disease.model.Disease;
import org.isf.opd.model.Opd;
import org.isf.opd.model.OpdDisease;
import org.isf.utils.exception.OHException;

public class TestOpdDisease {

	public OpdDisease setup(Opd opd, Disease disease, boolean usingSet) throws OHException {
		OpdDisease opdDisease;

		if (usingSet) {
			opdDisease = new OpdDisease();
			setParameters(opd, disease, opdDisease);
		} else {
			opdDisease = new OpdDisease(opd, disease);
		}

		return opdDisease;
	}

	public void setParameters(Opd opd, Disease disease, OpdDisease opdDisease) {
		opdDisease.setOpd(opd);
		opdDisease.setDisease(disease);
	}

	public void check(OpdDisease opdDisease, Opd opd, Disease disease) {
		assertThat(opdDisease.getOpd().getCode()).isEqualTo(opd.getCode());
		assertThat(opdDisease.getDisease().getCode()).isEqualTo(disease.getCode());
	}
}
