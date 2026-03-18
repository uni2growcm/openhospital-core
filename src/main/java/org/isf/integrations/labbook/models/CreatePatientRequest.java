package org.isf.integrations.labbook.models;

import org.springframework.stereotype.Component;

@Component
public class CreatePatientRequest {
	private Integer pat_code;
	private String pat_name;
	private String pat_firstname;
	private String pat_birth;
	private Integer pat_sex;
	private String pat_address;
	private String pat_city;
	private String pat_phone1;
	private String pat_profession;
	private String pat_blood_group;
	private String pat_rhesus;

	public Integer getPat_code() {
		return pat_code;
	}

	public void setPat_code(Integer pat_code) {
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

	public String getPat_blood_group() {
		return pat_blood_group;
	}

	public void setPat_blood_group(String pat_blood_group) {
		this.pat_blood_group = pat_blood_group;
	}

	public String getPat_rhesus() {
		return pat_rhesus;
	}

	public void setPat_rhesus(String pat_rhesus) {
		this.pat_rhesus = pat_rhesus;
	}
}
