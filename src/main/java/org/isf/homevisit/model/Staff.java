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
package org.isf.homevisit.model;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Version;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "OH_STAFF")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "STF_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "STF_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "STF_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "STF_IS_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "STF_LAST_MODIFIED_DATE"))
public class Staff extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "STF_ID")
	private Integer code;

	@NotNull
	@Column(name = "STF_FIRST_NAME", length = 50)
	private String firstName;

	@NotNull
	@Column(name = "STF_LAST_NAME", length = 50)
	private String lastName;

	@Column(name = "STF_PROFESSION", length = 50)
	private String profession;

	@Column(name = "STF_POSITION", length = 50)
	private String position;

	@Column(name = "STF_PHONE", length = 50)
	private String phone;

	@Version
	@Column(name = "STF_LOCK")
	private Integer lock;

	@Transient
	private volatile int hashCode;

	public Staff() {
		super();
	}

	public Staff(String firstName, String lastName, String position, String profession, String phone) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.profession = profession;
		this.phone = phone;
		this.position = position;
	}

	public Staff(Integer code, String firstName, String lastName, String position, String profession, String phone) {
		this.code = code;
		this.firstName = firstName;
		this.lastName = lastName;
		this.profession = profession;
		this.phone = phone;
		this.position = position;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getLock() {
		return lock;
	}

	public void setLock(Integer lock) {
		this.lock = lock;
	}

	public String getFullName() {
		return firstName + " " + lastName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Staff other)) return false;
		return code != null && code.equals(other.code);
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			final int m = 23;
			int c = 133;
			c = m * c + (code == null ? 0 : code);
			this.hashCode = c;
		}
		return this.hashCode;
	}

	@Override
	public String toString() {
		return getFullName();
	}
}