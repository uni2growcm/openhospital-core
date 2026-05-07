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
package org.isf.country;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.country.model.Country;
import org.isf.utils.exception.OHException;

public class TestCountry {

	private String isoCode   = "CM";
	private String phoneCode = "+237";
	private String name      = "Cameroon";

	public Country setup(boolean usingSet) throws OHException {
		Country country;
		if (usingSet) {
			country = new Country();
			setParameters(country);
		} else {
			country = new Country(isoCode, phoneCode, name);
		}
		return country;
	}

	public void setParameters(Country country) {
		country.setIsoCode(isoCode);
		country.setPhoneCode(phoneCode);
		country.setName(name);
	}

	public void check(Country country) {
		assertThat(country.getIsoCode()).isEqualTo(isoCode);
		assertThat(country.getPhoneCode()).isEqualTo(phoneCode);
		assertThat(country.getName()).isEqualTo(name);
		assertThat(country.getActive()).isEqualTo(1);
	}
}