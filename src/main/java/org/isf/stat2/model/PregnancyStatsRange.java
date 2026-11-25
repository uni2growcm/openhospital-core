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
package org.isf.stat2.model;

/**
 * Language independent codes of the range filters of the CPN (pregnancy) statistics.
 * All ranges are half-open [min, max) so that a value never belongs to two ranges.
 */
public final class PregnancyStatsRange {

	public static final String MATERNAL_WEIGHT_LT50 = "LT50";      // < 50 kg
	public static final String MATERNAL_WEIGHT_50_70 = "50TO70";   // >= 50 and < 70 kg
	public static final String MATERNAL_WEIGHT_70_90 = "70TO90";   // >= 70 and < 90 kg
	public static final String MATERNAL_WEIGHT_GE90 = "GE90";      // >= 90 kg

	public static final String SYSTOLIC_LT120 = "LT120";           // < 120 mmHg
	public static final String SYSTOLIC_120_139 = "120TO139";      // >= 120 and < 140 mmHg
	public static final String SYSTOLIC_GE140 = "GE140";           // >= 140 mmHg

	public static final String DIASTOLIC_LT80 = "LT80";            // < 80 mmHg
	public static final String DIASTOLIC_80_89 = "80TO89";         // >= 80 and < 90 mmHg
	public static final String DIASTOLIC_GE90 = "GE90";            // >= 90 mmHg

	private PregnancyStatsRange() {
	}
}