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

import org.isf.exa.model.Exam;

public class ExamStat {

	private Exam exam;
	private int menNumber;
	private int womenNumber;

	public ExamStat(Exam exam, int menNumber, int womenNumber) {
		this.exam = exam;
		this.menNumber = menNumber;
		this.womenNumber = womenNumber;
	}

	public Exam getExam() {
		return exam;
	}

	public void setExam(Exam exam) {
		this.exam = exam;
	}

	public int getMenNumber() {
		return menNumber;
	}

	public void setMenNumber(int menNumber) {
		this.menNumber = menNumber;
	}

	public int getWomenNumber() {
		return womenNumber;
	}

	public void setWomenNumber(int womenNumber) {
		this.womenNumber = womenNumber;
	}
}