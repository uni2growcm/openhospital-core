/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.orthanc.model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="OH_ORTHANC_CONFIG")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "ORC_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "ORC_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "ORC_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "ORC_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "ORC_LAST_MODIFIED_DATE"))
public class OrthancUser {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ORC_ID")
	private Integer id; 
	
	@Column(name="ORC_US_ID_A")		
	private String ohUserId;
	
	@Column(name="ORC_USER_NAME")
	private String username;
	
	@Column(name="ORC_PASSWORD")
	private String password;

	public OrthancConfig() {}

	public OrthancConfig(String userName, String orthancUserName, String orthancPassword) {
		this.userName = userName;
		this.orthancUserName = orthancUserName;
		this.orthancPassword = orthancPassword;
	}

	public OrthancConfig(Integer id, String userName, String orthancUserName, String orthancPassword) {
		this.id = id;
		this.userName = userName;
		this.orthancUserName = orthancUserName;
		this.orthancPassword = orthancPassword;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOrthancUserName() {
		return orthancUserName;
	}

	public void setOrthancUserName(String orthancUserName) {
		this.orthancUserName = orthancUserName;
	}

	public String getOrthancPassword() {
		return orthancPassword;
	}

	public void setOrthancPassword(String orthancPassword) {
		this.orthancPassword = orthancPassword;
	}
}
