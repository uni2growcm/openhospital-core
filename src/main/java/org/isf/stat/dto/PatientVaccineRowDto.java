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
package org.isf.stat.dto;

import java.io.Serializable;

/**
 * DTO representing an individual row in the Patient Vaccine detailed table report.
 */
public class PatientVaccineRowDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer patientCode;
	private String patientName;
	private String sex;
	private Integer age;
	private String vaccineDate;
	private String vaccineDescription;
	private String vaccineTypeDescription;
	private String village;

	public PatientVaccineRowDto() {
	}

	public PatientVaccineRowDto(Integer patientCode, String patientName, String sex, Integer age,
			String vaccineDate, String vaccineDescription, String vaccineTypeDescription, String village) {
		this.patientCode = patientCode;
		this.patientName = patientName;
		this.sex = sex;
		this.age = age;
		this.vaccineDate = vaccineDate;
		this.vaccineDescription = vaccineDescription;
		this.vaccineTypeDescription = vaccineTypeDescription;
		this.village = village;
	}

	public Integer getPatientCode() {
		return patientCode;
	}

	public void setPatientCode(Integer patientCode) {
		this.patientCode = patientCode;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getVaccineDate() {
		return vaccineDate;
	}

	public void setVaccineDate(String vaccineDate) {
		this.vaccineDate = vaccineDate;
	}

	public String getVaccineDescription() {
		return vaccineDescription;
	}

	public void setVaccineDescription(String vaccineDescription) {
		this.vaccineDescription = vaccineDescription;
	}

	public String getVaccineTypeDescription() {
		return vaccineTypeDescription;
	}

	public void setVaccineTypeDescription(String vaccineTypeDescription) {
		this.vaccineTypeDescription = vaccineTypeDescription;
	}

	public String getVillage() {
		return village;
	}

	public void setVillage(String village) {
		this.village = village;
	}
}
