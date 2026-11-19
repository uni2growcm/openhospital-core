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
package org.isf.prescriber.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_PRESCRIBER")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PRS_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PRS_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PRS_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PRS_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PRS_LAST_MODIFIED_DATE"))
public class Prescriber extends Auditable<String> implements Serializable, Comparable<Prescriber> {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PRS_ID")
	private int id;

	@NotNull
	@Column(name = "PRS_NAME", length = 100, nullable = false)
	private String name;

	@Version
	@Column(name = "PRS_LOCK")
	private int lock;

	@Transient
	private volatile int hashCode;

	public Prescriber() {
		super();
	}

	public Prescriber(String name) {
		this.name = name;
	}

	public Prescriber(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Prescriber other && id == other.id;
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			this.hashCode = Integer.hashCode(id);
		}
		return this.hashCode;
	}

	@Override
	public int compareTo(Prescriber other) {
		return String.CASE_INSENSITIVE_ORDER.compare(name == null ? "" : name, other.name == null ? "" : other.name);
	}

	@Override
	public String toString() {
		return Objects.toString(name, "");
	}
}
