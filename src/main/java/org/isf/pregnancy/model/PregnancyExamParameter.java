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
package org.isf.pregnancy.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------ PregnancyExamParameter - configurable catalog of CPN parameters
 * -----------------------------------------
 * This is the "paramètres CPN" catalog: besides the standard free-text parameters (mother's weight, blood
 * pressure, fetal heart rate...) it supports bounded numeric parameters (with a {@link #maxValue}) and closed
 * lists of values (conjunctiva, presentation, oedema...).
 */
@Entity
@Table(name = "OH_PREGNANCYEXAM")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PREGEX_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PREGEX_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PREGEX_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PREGEX_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PREGEX_LAST_MODIFIED_DATE"))
public class PregnancyExamParameter extends Auditable<String> {

	public static final int PRENATAL = -1;
	public static final int POSTNATAL = 1;
	public static final int BOTH = 0;

	@Id
	@Column(name = "PREGEX_CODE")
	private String code;

	@NotNull
	@Column(name = "PREGEX_DESC")
	private String description;

	@Column(name = "PREGEX_TYPE")
	private int examType = BOTH;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "PREGEX_DATA_TYPE")
	private PregnancyExamDataType dataType = PregnancyExamDataType.TEXT;

	@Column(name = "PREGEX_DEFAULT")
	private String defaultValue;

	/**
	 * Semicolon-separated list of the allowed values, only used when {@link #dataType} is
	 * {@link PregnancyExamDataType#ENUM}.
	 */
	@Column(name = "PREGEX_VALUES")
	private String allowedValues;

	/**
	 * Upper bound, only used when {@link #dataType} is {@link PregnancyExamDataType#NUMERIC}
	 * (e.g. 60 for the uterine height, 200 for the abdominal circumference).
	 */
	@Column(name = "PREGEX_MAX_VALUE")
	private Double maxValue;

	@Column(name = "PREGEX_UNIT")
	private String unit;

	@Transient
	private volatile int hashCode;

	public PregnancyExamParameter() {
		super();
	}

	public PregnancyExamParameter(String code, String description, int examType, PregnancyExamDataType dataType) {
		this.code = code;
		this.description = description;
		this.examType = examType;
		this.dataType = dataType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getExamType() {
		return examType;
	}

	public void setExamType(int examType) {
		this.examType = examType;
	}

	public PregnancyExamDataType getDataType() {
		return dataType;
	}

	public void setDataType(PregnancyExamDataType dataType) {
		this.dataType = dataType;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getAllowedValues() {
		return allowedValues;
	}

	public void setAllowedValues(String allowedValues) {
		this.allowedValues = allowedValues;
	}

	public String[] getAllowedValuesList() {
		if (allowedValues == null || allowedValues.isBlank()) {
			return new String[0];
		}
		return allowedValues.split(";");
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return description;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof PregnancyExamParameter && code != null && code.equals(((PregnancyExamParameter) other).getCode());
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			final int m = 23;
			int c = 133;
			c = m * c + (code == null ? 0 : code.hashCode());
			this.hashCode = c;
		}
		return this.hashCode;
	}
}
