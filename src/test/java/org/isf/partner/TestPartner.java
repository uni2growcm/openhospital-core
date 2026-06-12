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
package org.isf.partner;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.partner.model.Partner;
import org.isf.typology.model.Typology;
import org.isf.utils.exception.OHException;

public class TestPartner {

	private String name = "Test Partner";
	private String contactPerson = "John Doe";
	private String phone = "+237 690001234";
	private String email = "test@partner.org";
	private String address = "123 Test Street";
	private String notes = "Test notes for partner";

	public Partner setup(boolean usingSet, Typology type) throws OHException {
		Partner partner;
		if (usingSet) {
			partner = new Partner();
			setParameters(partner, type);
		} else {
			partner = new Partner(name, type, contactPerson, phone, email, address, notes);
		}
		return partner;
	}

	public void setParameters(Partner partner, Typology type) {
		partner.setName(name);
		partner.setType(type);
		partner.setContactPerson(contactPerson);
		partner.setPhone(phone);
		partner.setEmail(email);
		partner.setAddress(address);
		partner.setNotes(notes);
		partner.setActive(1);
	}

	public void check(Partner partner) {
		assertThat(partner.getCode()).isNotNull();
		assertThat(partner.getCode()).isPositive();
		assertThat(partner.getName()).isEqualTo(name);
		assertThat(partner.getContactPerson()).isEqualTo(contactPerson);
		assertThat(partner.getPhone()).isEqualTo(phone);
		assertThat(partner.getEmail()).isEqualTo(email);
		assertThat(partner.getAddress()).isEqualTo(address);
		assertThat(partner.getNotes()).isEqualTo(notes);
		assertThat(partner.getActive()).isEqualTo(1);
	}
}