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

package org.isf.country.model;

import jakarta.persistence.*;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "OH_COUNTRY")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "CNT_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "CNT_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "CNT_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "CNT_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "CNT_LAST_MODIFIED_DATE"))
public class Country extends Auditable<String> implements Serializable, Comparable<Country> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CNT_ID")
	private int id;

	@Column(name = "CNT_ISO_CODE", length = 2, nullable = false, unique = true)
	private String isoCode;

	@Column(name = "CNT_PHONE_CODE", length = 10, nullable = false, unique = true)
	private String phoneCode;

	@Column(name = "CNT_NAME", length = 100, nullable = false, unique = true)
	private String name;

	@Version
	@Column(name = "CNT_LOCK")
	private int lock;

	@Transient
	private volatile int hashCode;

	public Country() {
		super();
	}

	public Country(String isoCode, String phoneCode, String name) {
		this.isoCode = isoCode;
		this.phoneCode = phoneCode;
		this.name = name;
	}

	public Country(int id, String isoCode, String phoneCode, String name) {
		this.id = id;
		this.isoCode = isoCode;
		this.phoneCode = phoneCode;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

	public String getPhoneCode() {
		return phoneCode;
	}

	public void setPhoneCode(String phoneCode) {
		this.phoneCode = phoneCode;
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
	public int compareTo(Country other) {
		return this.id - other.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Country country)) {
			return false;
		}

		return this.getId() == country.getId();
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			final int m = 23;
			int c = 133;

			c = m * c + id;

			this.hashCode = c;
		}

		return this.hashCode;
	}

	@Override
	public String toString() {
		return this.name;
	}
}