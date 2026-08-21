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
package org.isf.partnertype.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_PARTNERTYPES")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PRTT_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PRTT_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PRTT_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PRTT_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PRTT_LAST_MODIFIED_DATE"))
public class PartnerType extends Auditable<String> {

	@Id
	@Column(name = "PRTT_CODE")
	private String code;

	@NotNull
	@Column(name = "PRTT_DESCRIPTION")
	private String description;

	@Column(name = "PRTT_DELETED")
	private boolean deleted = false;

	public PartnerType() {
		super();
	}

	public PartnerType(String code, String description) {
		super();
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof PartnerType other)) {
			return false;
		}
		return code != null && code.equals(other.getCode());
	}

	@Override
	public int hashCode() {
		return code == null ? 0 : code.hashCode();
	}

	@Override
	public String toString() {
		return description;
	}
}
