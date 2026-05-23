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
package org.isf.maternity.model;

public enum PerinealIntegrity {
	INTACT("angal.maternity.perineal.intact"),
	FIRST_DEGREE("angal.maternity.perineal.first"),
	SECOND_DEGREE("angal.maternity.perineal.second"),
	THIRD_DEGREE("angal.maternity.perineal.third"),
	FOURTH_DEGREE("angal.maternity.perineal.fourth"),
	EPISIOTOMY("angal.maternity.perineal.episiotomy");

	private final String key;

	PerinealIntegrity(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
