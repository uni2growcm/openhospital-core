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
 * Describes a statistic report available in one of the report folders (rpt_stat, rpt_extra)
 * and the kind of parameters the user must provide to launch it.
 */
public class StatisticReportDto implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum ParameterType {
		/** The report expects {@code fromdate} and {@code todate}. */
		DATE_RANGE,
		/** The report expects {@code month} and {@code year}. */
		MONTH_YEAR,
		/** The report does not expect any user parameter. */
		NONE
	}

	private String folder;
	private String fileName;
	private String title;
	private ParameterType parameterType;

	public StatisticReportDto() {
	}

	public StatisticReportDto(String folder, String fileName, String title, ParameterType parameterType) {
		this.folder = folder;
		this.fileName = fileName;
		this.title = title;
		this.parameterType = parameterType;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ParameterType getParameterType() {
		return parameterType;
	}

	public void setParameterType(ParameterType parameterType) {
		this.parameterType = parameterType;
	}
}
