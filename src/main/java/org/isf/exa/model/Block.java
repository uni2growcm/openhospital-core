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
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "OH_BLOCK")
public class Block {

	@Id
	@Column(name = "BLO_CODE")
	private String code;

	@Column(name = "BLO_DESC")
	private String description;

	@Transient
	private volatile int hashCode;

	public Block() {
	}

	public Block(String code, String description) {
		this.code = code;
		this.description = description;
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

	public String getSearchString() {
		return (code != null ? code.toLowerCase() : "") + (description != null ? description.toLowerCase() : "");
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Block block)) {
			return false;
		}
		return code != null && code.equals(block.getCode());
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			this.hashCode = 23 * 133 + (code == null ? 0 : code.hashCode());
		}
		return this.hashCode;
	}
}