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
@Table(name="OH_ORTHANC_PATIENT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "ORP_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "ORP_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "ORP_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "ORP_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "ORP_LAST_MODIFIED_DATE"))
public class OrthancPatient {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ORP_ID")
	private int id;
	
	@Column(name="ORP_PAT_ID_A")
	private Integer ohPatienId;
	
	@Column(name="ORP_PATIENT_ID")
	private String orthancPatientID;

	public OrthancPatient() {}
	
	public OrthancPatient(Integer ohPatienId, String orthancPatientID) {
		this.ohPatienId = ohPatienId;
		this.orthancPatientID = orthancPatientID;
	}

	public OrthancPatient(int id, Integer ohPatienId, String orthancPatientID) {
		this.id = id;
		this.ohPatienId = ohPatienId;
		this.orthancPatientID = orthancPatientID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getOhPatienId() {
		return ohPatienId;
	}

	public void setOhPatienId(Integer ohPatienId) {
		this.ohPatienId = ohPatienId;
	}

	public String getOrthancPatientID() {
		return orthancPatientID;
	}

	public void setOrthancPatientID(String orthancPatientID) {
		this.orthancPatientID = orthancPatientID;
	}

}
