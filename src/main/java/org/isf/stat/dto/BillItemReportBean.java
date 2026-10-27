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
 * One row of the "All sales by product and service per year" report (OH004-3): one bill item
 * description, with monthly sold/refunded quantity and amount, plus yearly totals.
 *
 * Field/getter names are kept uppercase to match the {@code $F{...}} bindings declared in
 * {@code OH004_IncomesAllByPriceCodes_3.jrxml}, which is filled from a bean collection rather
 * than a SQL query.
 */
public class BillItemReportBean implements Serializable {

	private String BLI_ITEM_DESC;
	private String BLI_ITEM_GROUP;
	private Double COUNT_JANUARY;
	private Double AMOUNT_JANUARY;
	private Double COUNT_FEBRUARY;
	private Double AMOUNT_FEBRUARY;
	private Double COUNT_MARCH;
	private Double AMOUNT_MARCH;
	private Double COUNT_APRIL;
	private Double AMOUNT_APRIL;
	private Double COUNT_MAY;
	private Double AMOUNT_MAY;
	private Double COUNT_JUNE;
	private Double AMOUNT_JUNE;
	private Double COUNT_JULY;
	private Double AMOUNT_JULY;
	private Double COUNT_AUGUST;
	private Double AMOUNT_AUGUST;
	private Double COUNT_SEPTEMBER;
	private Double AMOUNT_SEPTEMBER;
	private Double COUNT_OCTOBER;
	private Double AMOUNT_OCTOBER;
	private Double COUNT_NOVEMBER;
	private Double AMOUNT_NOVEMBER;
	private Double COUNT_DECEMBER;
	private Double AMOUNT_DECEMBER;
	private Double TOTAL_YEAR_COUNT;
	private Double TOTAL_YEAR_AMOUNT;

	private Double REFUNDED_COUNT_JANUARY;
	private Double REFUNDED_AMOUNT_JANUARY;
	private Double REFUNDED_COUNT_FEBRUARY;
	private Double REFUNDED_AMOUNT_FEBRUARY;
	private Double REFUNDED_COUNT_MARCH;
	private Double REFUNDED_AMOUNT_MARCH;
	private Double REFUNDED_COUNT_APRIL;
	private Double REFUNDED_AMOUNT_APRIL;
	private Double REFUNDED_COUNT_MAY;
	private Double REFUNDED_AMOUNT_MAY;
	private Double REFUNDED_COUNT_JUNE;
	private Double REFUNDED_AMOUNT_JUNE;
	private Double REFUNDED_COUNT_JULY;
	private Double REFUNDED_AMOUNT_JULY;
	private Double REFUNDED_COUNT_AUGUST;
	private Double REFUNDED_AMOUNT_AUGUST;
	private Double REFUNDED_COUNT_SEPTEMBER;
	private Double REFUNDED_AMOUNT_SEPTEMBER;
	private Double REFUNDED_COUNT_OCTOBER;
	private Double REFUNDED_AMOUNT_OCTOBER;
	private Double REFUNDED_COUNT_NOVEMBER;
	private Double REFUNDED_AMOUNT_NOVEMBER;
	private Double REFUNDED_COUNT_DECEMBER;
	private Double REFUNDED_AMOUNT_DECEMBER;
	private Double REFUNDED_TOTAL_YEAR_COUNT;
	private Double REFUNDED_TOTAL_YEAR_AMOUNT;

	public BillItemReportBean() {
		BLI_ITEM_DESC = "";
		BLI_ITEM_GROUP = "";
		COUNT_JANUARY = 0.0;
		AMOUNT_JANUARY = 0.0;
		COUNT_FEBRUARY = 0.0;
		AMOUNT_FEBRUARY = 0.0;
		COUNT_MARCH = 0.0;
		AMOUNT_MARCH = 0.0;
		COUNT_APRIL = 0.0;
		AMOUNT_APRIL = 0.0;
		COUNT_MAY = 0.0;
		AMOUNT_MAY = 0.0;
		COUNT_JUNE = 0.0;
		AMOUNT_JUNE = 0.0;
		COUNT_JULY = 0.0;
		AMOUNT_JULY = 0.0;
		COUNT_AUGUST = 0.0;
		AMOUNT_AUGUST = 0.0;
		COUNT_SEPTEMBER = 0.0;
		AMOUNT_SEPTEMBER = 0.0;
		COUNT_OCTOBER = 0.0;
		AMOUNT_OCTOBER = 0.0;
		COUNT_NOVEMBER = 0.0;
		AMOUNT_NOVEMBER = 0.0;
		COUNT_DECEMBER = 0.0;
		AMOUNT_DECEMBER = 0.0;
		TOTAL_YEAR_COUNT = 0.0;
		TOTAL_YEAR_AMOUNT = 0.0;

		REFUNDED_COUNT_JANUARY = 0.0;
		REFUNDED_AMOUNT_JANUARY = 0.0;
		REFUNDED_COUNT_FEBRUARY = 0.0;
		REFUNDED_AMOUNT_FEBRUARY = 0.0;
		REFUNDED_COUNT_MARCH = 0.0;
		REFUNDED_AMOUNT_MARCH = 0.0;
		REFUNDED_COUNT_APRIL = 0.0;
		REFUNDED_AMOUNT_APRIL = 0.0;
		REFUNDED_COUNT_MAY = 0.0;
		REFUNDED_AMOUNT_MAY = 0.0;
		REFUNDED_COUNT_JUNE = 0.0;
		REFUNDED_AMOUNT_JUNE = 0.0;
		REFUNDED_COUNT_JULY = 0.0;
		REFUNDED_AMOUNT_JULY = 0.0;
		REFUNDED_COUNT_AUGUST = 0.0;
		REFUNDED_AMOUNT_AUGUST = 0.0;
		REFUNDED_COUNT_SEPTEMBER = 0.0;
		REFUNDED_AMOUNT_SEPTEMBER = 0.0;
		REFUNDED_COUNT_OCTOBER = 0.0;
		REFUNDED_AMOUNT_OCTOBER = 0.0;
		REFUNDED_COUNT_NOVEMBER = 0.0;
		REFUNDED_AMOUNT_NOVEMBER = 0.0;
		REFUNDED_COUNT_DECEMBER = 0.0;
		REFUNDED_AMOUNT_DECEMBER = 0.0;
		REFUNDED_TOTAL_YEAR_COUNT = 0.0;
		REFUNDED_TOTAL_YEAR_AMOUNT = 0.0;
	}

	public String getBLI_ITEM_DESC() {
		return BLI_ITEM_DESC;
	}

	public void setBLI_ITEM_DESC(String bliItemDesc) {
		BLI_ITEM_DESC = bliItemDesc;
	}

	public String getBLI_ITEM_GROUP() {
		return BLI_ITEM_GROUP;
	}

	public void setBLI_ITEM_GROUP(String bliItemGroup) {
		BLI_ITEM_GROUP = bliItemGroup;
	}

	public Double getCOUNT_JANUARY() {
		return COUNT_JANUARY;
	}

	public void setCOUNT_JANUARY(Double count) {
		COUNT_JANUARY = count;
	}

	public Double getAMOUNT_JANUARY() {
		return AMOUNT_JANUARY;
	}

	public void setAMOUNT_JANUARY(Double amount) {
		AMOUNT_JANUARY = amount;
	}

	public Double getCOUNT_FEBRUARY() {
		return COUNT_FEBRUARY;
	}

	public void setCOUNT_FEBRUARY(Double count) {
		COUNT_FEBRUARY = count;
	}

	public Double getAMOUNT_FEBRUARY() {
		return AMOUNT_FEBRUARY;
	}

	public void setAMOUNT_FEBRUARY(Double amount) {
		AMOUNT_FEBRUARY = amount;
	}

	public Double getCOUNT_MARCH() {
		return COUNT_MARCH;
	}

	public void setCOUNT_MARCH(Double count) {
		COUNT_MARCH = count;
	}

	public Double getAMOUNT_MARCH() {
		return AMOUNT_MARCH;
	}

	public void setAMOUNT_MARCH(Double amount) {
		AMOUNT_MARCH = amount;
	}

	public Double getCOUNT_APRIL() {
		return COUNT_APRIL;
	}

	public void setCOUNT_APRIL(Double count) {
		COUNT_APRIL = count;
	}

	public Double getAMOUNT_APRIL() {
		return AMOUNT_APRIL;
	}

	public void setAMOUNT_APRIL(Double amount) {
		AMOUNT_APRIL = amount;
	}

	public Double getCOUNT_MAY() {
		return COUNT_MAY;
	}

	public void setCOUNT_MAY(Double count) {
		COUNT_MAY = count;
	}

	public Double getAMOUNT_MAY() {
		return AMOUNT_MAY;
	}

	public void setAMOUNT_MAY(Double amount) {
		AMOUNT_MAY = amount;
	}

	public Double getCOUNT_JUNE() {
		return COUNT_JUNE;
	}

	public void setCOUNT_JUNE(Double count) {
		COUNT_JUNE = count;
	}

	public Double getAMOUNT_JUNE() {
		return AMOUNT_JUNE;
	}

	public void setAMOUNT_JUNE(Double amount) {
		AMOUNT_JUNE = amount;
	}

	public Double getCOUNT_JULY() {
		return COUNT_JULY;
	}

	public void setCOUNT_JULY(Double count) {
		COUNT_JULY = count;
	}

	public Double getAMOUNT_JULY() {
		return AMOUNT_JULY;
	}

	public void setAMOUNT_JULY(Double amount) {
		AMOUNT_JULY = amount;
	}

	public Double getCOUNT_AUGUST() {
		return COUNT_AUGUST;
	}

	public void setCOUNT_AUGUST(Double count) {
		COUNT_AUGUST = count;
	}

	public Double getAMOUNT_AUGUST() {
		return AMOUNT_AUGUST;
	}

	public void setAMOUNT_AUGUST(Double amount) {
		AMOUNT_AUGUST = amount;
	}

	public Double getCOUNT_SEPTEMBER() {
		return COUNT_SEPTEMBER;
	}

	public void setCOUNT_SEPTEMBER(Double count) {
		COUNT_SEPTEMBER = count;
	}

	public Double getAMOUNT_SEPTEMBER() {
		return AMOUNT_SEPTEMBER;
	}

	public void setAMOUNT_SEPTEMBER(Double amount) {
		AMOUNT_SEPTEMBER = amount;
	}

	public Double getCOUNT_OCTOBER() {
		return COUNT_OCTOBER;
	}

	public void setCOUNT_OCTOBER(Double count) {
		COUNT_OCTOBER = count;
	}

	public Double getAMOUNT_OCTOBER() {
		return AMOUNT_OCTOBER;
	}

	public void setAMOUNT_OCTOBER(Double amount) {
		AMOUNT_OCTOBER = amount;
	}

	public Double getCOUNT_NOVEMBER() {
		return COUNT_NOVEMBER;
	}

	public void setCOUNT_NOVEMBER(Double count) {
		COUNT_NOVEMBER = count;
	}

	public Double getAMOUNT_NOVEMBER() {
		return AMOUNT_NOVEMBER;
	}

	public void setAMOUNT_NOVEMBER(Double amount) {
		AMOUNT_NOVEMBER = amount;
	}

	public Double getCOUNT_DECEMBER() {
		return COUNT_DECEMBER;
	}

	public void setCOUNT_DECEMBER(Double count) {
		COUNT_DECEMBER = count;
	}

	public Double getAMOUNT_DECEMBER() {
		return AMOUNT_DECEMBER;
	}

	public void setAMOUNT_DECEMBER(Double amount) {
		AMOUNT_DECEMBER = amount;
	}

	public Double getTOTAL_YEAR_COUNT() {
		return TOTAL_YEAR_COUNT;
	}

	public void setTOTAL_YEAR_COUNT(Double totalYearCount) {
		TOTAL_YEAR_COUNT = totalYearCount;
	}

	public Double getTOTAL_YEAR_AMOUNT() {
		return TOTAL_YEAR_AMOUNT;
	}

	public void setTOTAL_YEAR_AMOUNT(Double totalYearAmount) {
		TOTAL_YEAR_AMOUNT = totalYearAmount;
	}

	public Double getREFUNDED_COUNT_JANUARY() {
		return REFUNDED_COUNT_JANUARY;
	}

	public void setREFUNDED_COUNT_JANUARY(Double count) {
		REFUNDED_COUNT_JANUARY = count;
	}

	public Double getREFUNDED_AMOUNT_JANUARY() {
		return REFUNDED_AMOUNT_JANUARY;
	}

	public void setREFUNDED_AMOUNT_JANUARY(Double amount) {
		REFUNDED_AMOUNT_JANUARY = amount;
	}

	public Double getREFUNDED_COUNT_FEBRUARY() {
		return REFUNDED_COUNT_FEBRUARY;
	}

	public void setREFUNDED_COUNT_FEBRUARY(Double count) {
		REFUNDED_COUNT_FEBRUARY = count;
	}

	public Double getREFUNDED_AMOUNT_FEBRUARY() {
		return REFUNDED_AMOUNT_FEBRUARY;
	}

	public void setREFUNDED_AMOUNT_FEBRUARY(Double amount) {
		REFUNDED_AMOUNT_FEBRUARY = amount;
	}

	public Double getREFUNDED_COUNT_MARCH() {
		return REFUNDED_COUNT_MARCH;
	}

	public void setREFUNDED_COUNT_MARCH(Double count) {
		REFUNDED_COUNT_MARCH = count;
	}

	public Double getREFUNDED_AMOUNT_MARCH() {
		return REFUNDED_AMOUNT_MARCH;
	}

	public void setREFUNDED_AMOUNT_MARCH(Double amount) {
		REFUNDED_AMOUNT_MARCH = amount;
	}

	public Double getREFUNDED_COUNT_APRIL() {
		return REFUNDED_COUNT_APRIL;
	}

	public void setREFUNDED_COUNT_APRIL(Double count) {
		REFUNDED_COUNT_APRIL = count;
	}

	public Double getREFUNDED_AMOUNT_APRIL() {
		return REFUNDED_AMOUNT_APRIL;
	}

	public void setREFUNDED_AMOUNT_APRIL(Double amount) {
		REFUNDED_AMOUNT_APRIL = amount;
	}

	public Double getREFUNDED_COUNT_MAY() {
		return REFUNDED_COUNT_MAY;
	}

	public void setREFUNDED_COUNT_MAY(Double count) {
		REFUNDED_COUNT_MAY = count;
	}

	public Double getREFUNDED_AMOUNT_MAY() {
		return REFUNDED_AMOUNT_MAY;
	}

	public void setREFUNDED_AMOUNT_MAY(Double amount) {
		REFUNDED_AMOUNT_MAY = amount;
	}

	public Double getREFUNDED_COUNT_JUNE() {
		return REFUNDED_COUNT_JUNE;
	}

	public void setREFUNDED_COUNT_JUNE(Double count) {
		REFUNDED_COUNT_JUNE = count;
	}

	public Double getREFUNDED_AMOUNT_JUNE() {
		return REFUNDED_AMOUNT_JUNE;
	}

	public void setREFUNDED_AMOUNT_JUNE(Double amount) {
		REFUNDED_AMOUNT_JUNE = amount;
	}

	public Double getREFUNDED_COUNT_JULY() {
		return REFUNDED_COUNT_JULY;
	}

	public void setREFUNDED_COUNT_JULY(Double count) {
		REFUNDED_COUNT_JULY = count;
	}

	public Double getREFUNDED_AMOUNT_JULY() {
		return REFUNDED_AMOUNT_JULY;
	}

	public void setREFUNDED_AMOUNT_JULY(Double amount) {
		REFUNDED_AMOUNT_JULY = amount;
	}

	public Double getREFUNDED_COUNT_AUGUST() {
		return REFUNDED_COUNT_AUGUST;
	}

	public void setREFUNDED_COUNT_AUGUST(Double count) {
		REFUNDED_COUNT_AUGUST = count;
	}

	public Double getREFUNDED_AMOUNT_AUGUST() {
		return REFUNDED_AMOUNT_AUGUST;
	}

	public void setREFUNDED_AMOUNT_AUGUST(Double amount) {
		REFUNDED_AMOUNT_AUGUST = amount;
	}

	public Double getREFUNDED_COUNT_SEPTEMBER() {
		return REFUNDED_COUNT_SEPTEMBER;
	}

	public void setREFUNDED_COUNT_SEPTEMBER(Double count) {
		REFUNDED_COUNT_SEPTEMBER = count;
	}

	public Double getREFUNDED_AMOUNT_SEPTEMBER() {
		return REFUNDED_AMOUNT_SEPTEMBER;
	}

	public void setREFUNDED_AMOUNT_SEPTEMBER(Double amount) {
		REFUNDED_AMOUNT_SEPTEMBER = amount;
	}

	public Double getREFUNDED_COUNT_OCTOBER() {
		return REFUNDED_COUNT_OCTOBER;
	}

	public void setREFUNDED_COUNT_OCTOBER(Double count) {
		REFUNDED_COUNT_OCTOBER = count;
	}

	public Double getREFUNDED_AMOUNT_OCTOBER() {
		return REFUNDED_AMOUNT_OCTOBER;
	}

	public void setREFUNDED_AMOUNT_OCTOBER(Double amount) {
		REFUNDED_AMOUNT_OCTOBER = amount;
	}

	public Double getREFUNDED_COUNT_NOVEMBER() {
		return REFUNDED_COUNT_NOVEMBER;
	}

	public void setREFUNDED_COUNT_NOVEMBER(Double count) {
		REFUNDED_COUNT_NOVEMBER = count;
	}

	public Double getREFUNDED_AMOUNT_NOVEMBER() {
		return REFUNDED_AMOUNT_NOVEMBER;
	}

	public void setREFUNDED_AMOUNT_NOVEMBER(Double amount) {
		REFUNDED_AMOUNT_NOVEMBER = amount;
	}

	public Double getREFUNDED_COUNT_DECEMBER() {
		return REFUNDED_COUNT_DECEMBER;
	}

	public void setREFUNDED_COUNT_DECEMBER(Double count) {
		REFUNDED_COUNT_DECEMBER = count;
	}

	public Double getREFUNDED_AMOUNT_DECEMBER() {
		return REFUNDED_AMOUNT_DECEMBER;
	}

	public void setREFUNDED_AMOUNT_DECEMBER(Double amount) {
		REFUNDED_AMOUNT_DECEMBER = amount;
	}

	public Double getREFUNDED_TOTAL_YEAR_COUNT() {
		return REFUNDED_TOTAL_YEAR_COUNT;
	}

	public void setREFUNDED_TOTAL_YEAR_COUNT(Double totalYearCount) {
		REFUNDED_TOTAL_YEAR_COUNT = totalYearCount;
	}

	public Double getREFUNDED_TOTAL_YEAR_AMOUNT() {
		return REFUNDED_TOTAL_YEAR_AMOUNT;
	}

	public void setREFUNDED_TOTAL_YEAR_AMOUNT(Double totalYearAmount) {
		REFUNDED_TOTAL_YEAR_AMOUNT = totalYearAmount;
	}

}
