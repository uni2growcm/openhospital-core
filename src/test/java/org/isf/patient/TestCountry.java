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
package org.isf.patient;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.patient.model.Country;
import org.isf.utils.exception.OHException;

public class TestCountry {

	private static String code = "TC";
	private static String name = "TestCountry";
	private static String phoneCode = "+000";

	public Country setup(boolean usingSet) throws OHException {
		Country country;

		if (usingSet) {
			country = new Country();
			setParameters(country);
		} else {
			country = new Country(code, name, phoneCode);
		}

		return country;
	}

	public void setParameters(Country country) {
		country.setCode(code);
		country.setName(name);
		country.setPhoneCode(phoneCode);
	}

	public void check(Country country) {
		assertThat(country.getCode()).isEqualTo(code);
		assertThat(country.getName()).isEqualTo(name);
		assertThat(country.getPhoneCode()).isEqualTo(phoneCode);
	}
}
