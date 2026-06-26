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
package org.isf.typology.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


/**
 * Typologies model
 *
 * @author Hema
 * @version 1.15
 */
@Entity
@Table(name = "OH_TYPOLOGIES")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "TYPO_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "TYPO_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "TYPO_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "TYPO_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "TYPO_LAST_MODIFIED_DATE"))
public class Typology extends Auditable<String> {

	@Id
	@Column(name = "TYPO_CODE", length = 20)
	private String code;

	@NotNull
	@Column(name = "TYPO_DESCRIPTION", nullable = false, length = 255)
	private String description;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "TYPO_FAMILY", nullable = false, length = 255)
	private Family family;

	@Transient
	private volatile int hashCode;

	public Typology() {
		super();
	}

	public Typology(String code, String description, Family family) {
		this.code = code;
		this.description = description;
		this.family = family;
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

	public Family getFamily() {
		return this.family;
	}

	public void setFamily(Family family) {
		this.family = family;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Typology other)) return false;
		return code != null && code.equals(other.code);
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			final int m = 23;
			int c = 133;
			c = m * c + code.hashCode();
			this.hashCode = c;
		}
		return this.hashCode;
	}

	@Override
	public String toString() {
		return description;
	}
}