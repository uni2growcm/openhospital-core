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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Consolidated statistics DTO for patient vaccination reports.
 */
public class PatientVaccineStatsDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private long totalVaccinations;
	private long distinctPatients;

	private long maleCount;
	private double malePercentage;
	private long femaleCount;
	private double femalePercentage;

	private long under1Count;
	private double under1Percentage;
	private long from1To5Count;
	private double from1To5Percentage;
	private long over5Count;
	private double over5Percentage;

	private List<PatientVaccineItemStatsDto> vaccineStats = new ArrayList<>();
	private List<PatientVaccineRowDto> rows = new ArrayList<>();

	private LocalDateTime filterDateFrom;
	private LocalDateTime filterDateTo;
	private String filterVaccineType;
	private String filterVaccine;
	private String filterSex;
	private Integer filterAgeFrom;
	private Integer filterAgeTo;
	private String filterPatientSearch;
	private String filterVillage;

	public PatientVaccineStatsDto() {
	}

	public long getTotalVaccinations() {
		return totalVaccinations;
	}

	public void setTotalVaccinations(long totalVaccinations) {
		this.totalVaccinations = totalVaccinations;
	}

	public long getDistinctPatients() {
		return distinctPatients;
	}

	public void setDistinctPatients(long distinctPatients) {
		this.distinctPatients = distinctPatients;
	}

	public long getMaleCount() {
		return maleCount;
	}

	public void setMaleCount(long maleCount) {
		this.maleCount = maleCount;
	}

	public double getMalePercentage() {
		return malePercentage;
	}

	public void setMalePercentage(double malePercentage) {
		this.malePercentage = malePercentage;
	}

	public long getFemaleCount() {
		return femaleCount;
	}

	public void setFemaleCount(long femaleCount) {
		this.femaleCount = femaleCount;
	}

	public double getFemalePercentage() {
		return femalePercentage;
	}

	public void setFemalePercentage(double femalePercentage) {
		this.femalePercentage = femalePercentage;
	}

	public long getUnder1Count() {
		return under1Count;
	}

	public void setUnder1Count(long under1Count) {
		this.under1Count = under1Count;
	}

	public double getUnder1Percentage() {
		return under1Percentage;
	}

	public void setUnder1Percentage(double under1Percentage) {
		this.under1Percentage = under1Percentage;
	}

	public long getFrom1To5Count() {
		return from1To5Count;
	}

	public void setFrom1To5Count(long from1To5Count) {
		this.from1To5Count = from1To5Count;
	}

	public double getFrom1To5Percentage() {
		return from1To5Percentage;
	}

	public void setFrom1To5Percentage(double from1To5Percentage) {
		this.from1To5Percentage = from1To5Percentage;
	}

	public long getOver5Count() {
		return over5Count;
	}

	public void setOver5Count(long over5Count) {
		this.over5Count = over5Count;
	}

	public double getOver5Percentage() {
		return over5Percentage;
	}

	public void setOver5Percentage(double over5Percentage) {
		this.over5Percentage = over5Percentage;
	}

	public List<PatientVaccineItemStatsDto> getVaccineStats() {
		return vaccineStats;
	}

	public void setVaccineStats(List<PatientVaccineItemStatsDto> vaccineStats) {
		this.vaccineStats = vaccineStats;
	}

	public List<PatientVaccineRowDto> getRows() {
		return rows;
	}

	public void setRows(List<PatientVaccineRowDto> rows) {
		this.rows = rows;
	}

	public LocalDateTime getFilterDateFrom() {
		return filterDateFrom;
	}

	public void setFilterDateFrom(LocalDateTime filterDateFrom) {
		this.filterDateFrom = filterDateFrom;
	}

	public LocalDateTime getFilterDateTo() {
		return filterDateTo;
	}

	public void setFilterDateTo(LocalDateTime filterDateTo) {
		this.filterDateTo = filterDateTo;
	}

	public String getFilterVaccineType() {
		return filterVaccineType;
	}

	public void setFilterVaccineType(String filterVaccineType) {
		this.filterVaccineType = filterVaccineType;
	}

	public String getFilterVaccine() {
		return filterVaccine;
	}

	public void setFilterVaccine(String filterVaccine) {
		this.filterVaccine = filterVaccine;
	}

	public String getFilterSex() {
		return filterSex;
	}

	public void setFilterSex(String filterSex) {
		this.filterSex = filterSex;
	}

	public Integer getFilterAgeFrom() {
		return filterAgeFrom;
	}

	public void setFilterAgeFrom(Integer filterAgeFrom) {
		this.filterAgeFrom = filterAgeFrom;
	}

	public Integer getFilterAgeTo() {
		return filterAgeTo;
	}

	public void setFilterAgeTo(Integer filterAgeTo) {
		this.filterAgeTo = filterAgeTo;
	}

	public String getFilterPatientSearch() {
		return filterPatientSearch;
	}

	public void setFilterPatientSearch(String filterPatientSearch) {
		this.filterPatientSearch = filterPatientSearch;
	}

	public String getFilterVillage() {
		return filterVillage;
	}

	public void setFilterVillage(String filterVillage) {
		this.filterVillage = filterVillage;
	}
}
