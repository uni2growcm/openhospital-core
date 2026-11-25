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
 * Statistics row for a single vaccine administered.
 */
public class PatientVaccineItemStatsDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String vaccineTypeCode;
	private String vaccineTypeDescription;
	private String vaccineCode;
	private String vaccineDescription;
	private Long count;
	private Double percentage;

	public PatientVaccineItemStatsDto() {
	}

	public PatientVaccineItemStatsDto(String vaccineTypeCode, String vaccineTypeDescription, String vaccineCode,
			String vaccineDescription, Long count, Double percentage) {
		this.vaccineTypeCode = vaccineTypeCode;
		this.vaccineTypeDescription = vaccineTypeDescription;
		this.vaccineCode = vaccineCode;
		this.vaccineDescription = vaccineDescription;
		this.count = count;
		this.percentage = percentage;
	}

	public String getVaccineTypeCode() {
		return vaccineTypeCode;
	}

	public void setVaccineTypeCode(String vaccineTypeCode) {
		this.vaccineTypeCode = vaccineTypeCode;
	}

	public String getVaccineTypeDescription() {
		return vaccineTypeDescription;
	}

	public void setVaccineTypeDescription(String vaccineTypeDescription) {
		this.vaccineTypeDescription = vaccineTypeDescription;
	}

	public String getVaccineCode() {
		return vaccineCode;
	}

	public void setVaccineCode(String vaccineCode) {
		this.vaccineCode = vaccineCode;
	}

	public String getVaccineDescription() {
		return vaccineDescription;
	}

	public void setVaccineDescription(String vaccineDescription) {
		this.vaccineDescription = vaccineDescription;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}
}
