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
package org.isf.exa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "OH_BLOCK_EXAM")
public class BlockExam {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BLE_ID")
	private int id;

	@Column(name = "BLE_BLO_CODE")
	private String blockCode;

	@Column(name = "BLE_EXA_ID_A")
	private String examCode;

	public BlockExam() {
	}

	public BlockExam(String blockCode, String examCode) {
		this.blockCode = blockCode;
		this.examCode = examCode;
	}

	public BlockExam(int id, String blockCode, String examCode) {
		this.id = id;
		this.blockCode = blockCode;
		this.examCode = examCode;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBlockCode() {
		return blockCode;
	}

	public void setBlockCode(String blockCode) {
		this.blockCode = blockCode;
	}

	public String getExamCode() {
		return examCode;
	}

	public void setExamCode(String examCode) {
		this.examCode = examCode;
	}
}