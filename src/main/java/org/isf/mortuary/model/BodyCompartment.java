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

package org.isf.mortuary.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_BODYCOMPARTMENT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "BC_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "BC_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "BC_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "BC_LAST_MODIFIED_DATE"))
@AttributeOverride(name = "active", column = @Column(name = "BC_ACTIVE"))
public class BodyCompartment extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BC_ID")
	private int id;

	@Column(name = "BC_LABEL")
	private String label;

	@Column(name = "BC_DESC")
	private String description;

	@Column(name = "BC_DELETED")
	private boolean deleted;

	public BodyCompartment() {

	}

	public BodyCompartment(String code, String description, boolean deleted) {
		this.label = code;
		this.description = description;
		this.deleted = deleted;
	}

	public BodyCompartment(int id, String code, String description, boolean deleted) {
		this(code, description, deleted);
		this.id = id;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
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
	public String toString() {
		return "BodyCompartment{" +
			"id=" + id +
			", label='" + label + '\'' +
			", description='" + description + '\'' +
			'}';
	}
}