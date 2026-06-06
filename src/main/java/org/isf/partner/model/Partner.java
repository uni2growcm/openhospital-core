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
package org.isf.partner.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import org.isf.typology.model.Typology;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_PARTNERS")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PRT_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PRT_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PRT_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PRT_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PRT_LAST_MODIFIED_DATE"))
public class Partner extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PRT_ID")
	private int id;

	@NotNull
	@Column(name = "PRT_CODE", unique = true, length = 20)
	private String code;

	@NotNull
	@Column(name = "PRT_NAME", length = 100)
	private String name;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "PRT_TYPE")
	private Typology type;

	@Column(name = "PRT_CONTACT_PERSON", length = 100)
	private String contactPerson;

	@Column(name = "PRT_PHONE", length = 50)
	private String phone;

	@Column(name = "PRT_EMAIL", length = 100)
	private String email;

	@Column(name = "PRT_ADDRESS", length = 255)
	private String address;

	@Column(name = "PRT_NOTES", columnDefinition = "TEXT")
	private String notes;

	@Version
	@Column(name = "PRT_LOCK")
	private Integer lock;

	@Transient
	private volatile int hashCode;

	public Partner() {
		super();
	}

	public Partner(String code, String name, Typology type, String contactPerson,
	               String phone, String email, String address, String notes) {
		this.code = code;
		this.name = name;
		this.type = type;
		this.contactPerson = contactPerson;
		this.phone = phone;
		this.email = email;
		this.address = address;
		this.notes = notes;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Typology getType() {
		return type;
	}

	public void setType(Typology type) {
		this.type = type;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Integer getLock() {
		return lock;
	}

	public void setLock(Integer lock) {
		this.lock = lock;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Partner other)) return false;
		return id != 0 && id == other.id;
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
		return name != null ? name : "";
	}

	public boolean isActive() {
		return getActive() == 1;
	}
}