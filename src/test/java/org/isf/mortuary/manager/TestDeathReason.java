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

import org.isf.mortuary.model.DeathReason;
import org.isf.utils.exception.OHException;

public class TestDeathReason {

	private final int id = 1;
	private final String title = "CARD001";
	private final String description = "Arrêt cardiaque";
	private final boolean deleted = false;

	public DeathReason setup(boolean usingSet) throws OHException {
		DeathReason deathReason;

		if (usingSet) {
			deathReason = new DeathReason();
			setParameters(deathReason);
		} else {
			deathReason = new DeathReason(id, title, description, deleted);
		}
		return deathReason;
	}

	public void setParameters(DeathReason deathReason) {
		deathReason.setId(id);
		deathReason.setTitle(title);
		deathReason.setDescription(description);
		deathReason.setDeleted(deleted);
	}

	public void check(DeathReason deathReason) {
		assertThat(deathReason.getId()).isEqualTo(id);
		assertThat(deathReason.getTitle()).isEqualTo(title);
		assertThat(deathReason.getDescription()).isEqualTo(description);
	}
}