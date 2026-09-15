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
package org.isf.patient.model;

import org.isf.generaldata.MessageBundle;

public enum GeographicArea {

	UNDEFINED("", ""),
	INSIDE_AREA("IN", MessageBundle.getMessage("angal.patient.geographicarea.insidearea")),
	OUTSIDE_AREA("OUT", MessageBundle.getMessage("angal.patient.geographicarea.outsidearea")),
	OUTSIDE_DISTRICT("OUTD", MessageBundle.getMessage("angal.patient.geographicarea.outsidedistrict"));

	private final String code;
	private final String description;

	GeographicArea(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public static GeographicArea getByCode(String code) {
		for (GeographicArea geographicArea : values()) {
			if (geographicArea.getCode().equals(code)) {
				return geographicArea;
			}
		}
		return UNDEFINED;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return description;
	}
}
