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
package org.isf.integrations.labbook.models;

import org.springframework.stereotype.Component;

@Component
public class CreatePatientRequest {
	private Integer id_user;
	private String pat_code;
	private String pat_name;
	private String pat_firstname;
	private String pat_birth;
	private Integer pat_sex;
	private String pat_address;
	private String pat_city;
	private String pat_phone1;
	private String pat_profession;
	private Integer pat_blood_group;
	private Integer pat_blood_rhesus;
	private Integer pat_age;

	public String getPat_code() {
		return pat_code;
	}

	public void setPat_code(String pat_code) {
		this.pat_code = pat_code;
	}

	public String getPat_name() {
		return pat_name;
	}

	public void setPat_name(String pat_name) {
		this.pat_name = pat_name;
	}

	public String getPat_firstname() {
		return pat_firstname;
	}

	public void setPat_firstname(String pat_firstname) {
		this.pat_firstname = pat_firstname;
	}

	public String getPat_birth() {
		return pat_birth;
	}

	public void setPat_birth(String pat_birth) {
		this.pat_birth = pat_birth;
	}

	public Integer getPat_sex() {
		return pat_sex;
	}

	public void setPat_sex(Integer pat_sex) {
		this.pat_sex = pat_sex;
	}

	public String getPat_address() {
		return pat_address;
	}

	public void setPat_address(String pat_address) {
		this.pat_address = pat_address;
	}

	public String getPat_city() {
		return pat_city;
	}

	public void setPat_city(String pat_city) {
		this.pat_city = pat_city;
	}

	public String getPat_phone1() {
		return pat_phone1;
	}

	public void setPat_phone1(String pat_phone1) {
		this.pat_phone1 = pat_phone1;
	}

	public String getPat_profession() {
		return pat_profession;
	}

	public void setPat_profession(String pat_profession) {
		this.pat_profession = pat_profession;
	}

	public Integer getPat_blood_group() {
		return pat_blood_group;
	}

	public void setPat_blood_group(Integer pat_blood_group) {
		this.pat_blood_group = pat_blood_group;
	}

	public Integer getPat_blood_rhesus() {
		return pat_blood_rhesus;
	}

	public void setPat_blood_rhesus(Integer pat_blood_rhesus) {
		this.pat_blood_rhesus = pat_blood_rhesus;
	}

	public Integer getId_user() {
		return id_user;
	}

	public void setId_user(Integer id_user) {
		this.id_user = id_user;
	}

	public Integer getPat_age() {
		return pat_age;
	}

	public void setPat_age(Integer pat_age) {
		this.pat_age = pat_age;
	}
}