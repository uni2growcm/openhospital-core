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
package org.isf.homevisit;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.homevisit.model.Staff;
import org.isf.utils.exception.OHException;

public class TestStaff {

	private String code = "STF_TEST";
	private String firstName = "John";
	private String lastName = "Doe";
	private String profession = "Nurse";
	private String phone = "+237 690001234";

	public Staff setup(boolean usingSet) throws OHException {
		Staff staff;
		if (usingSet) {
			staff = new Staff();
			setParameters(staff);
		} else {
			staff = new Staff(code, firstName, lastName, profession, phone);
		}
		return staff;
	}

	public void setParameters(Staff staff) {
		staff.setCode(code);
		staff.setFirstName(firstName);
		staff.setLastName(lastName);
		staff.setProfession(profession);
		staff.setPhone(phone);
		staff.setActive(1);
	}

	public void check(Staff staff) {
		assertThat(staff.getCode()).isEqualTo(code);
		assertThat(staff.getFirstName()).isEqualTo(firstName);
		assertThat(staff.getLastName()).isEqualTo(lastName);
		assertThat(staff.getProfession()).isEqualTo(profession);
		assertThat(staff.getPhone()).isEqualTo(phone);
		assertThat(staff.getActive()).isEqualTo(1);
	}
}