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
package org.isf.stat.dto;

import java.sql.Timestamp;
import java.util.Date;

/**
 * One row of the "StockSheet" (fiche de stock) report: a single stock movement with its running
 * balance. Property names intentionally match the {@code StockSheet.jrxml} report fields, which are
 * bound by JasperReports' JavaBean data source using exact-name getters.
 */
public class StockSheetMovementRow {

	private final Timestamp mmvDate;
	private final String mmvLtIdA;
	private final String origin;
	private final Date ltDueDate;
	private final Integer entryQty;
	private final Integer outQty;
	private final String destination;
	private final double mmvStockAfter;

	public StockSheetMovementRow(Timestamp mmvDate, String mmvLtIdA, String origin, Date ltDueDate, Integer entryQty,
					Integer outQty, String destination, double mmvStockAfter) {
		this.mmvDate = mmvDate;
		this.mmvLtIdA = mmvLtIdA;
		this.origin = origin;
		this.ltDueDate = ltDueDate;
		this.entryQty = entryQty;
		this.outQty = outQty;
		this.destination = destination;
		this.mmvStockAfter = mmvStockAfter;
	}

	public Timestamp getMMV_DATE() {
		return mmvDate;
	}

	public String getMMV_LT_ID_A() {
		return mmvLtIdA;
	}

	public String getORIGIN() {
		return origin;
	}

	public Date getLT_DUE_DATE() {
		return ltDueDate;
	}

	public Integer getENTRY_QTY() {
		return entryQty;
	}

	public Integer getOUT_QTY() {
		return outQty;
	}

	public String getDESTINATION() {
		return destination;
	}

	public double getMMV_STOCK_AFTER() {
		return mmvStockAfter;
	}
}
