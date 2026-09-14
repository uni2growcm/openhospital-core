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
package org.isf.opd.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import org.isf.disease.model.Disease;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * One diagnosis attached to an OPD visit, when the visit was edited with an unbounded number of
 * diagnoses (see {@code GeneralData.ENHANCEDDIAGNOSTICINOPDEDIT}), rather than the default three-slot
 * {@code disease}/{@code disease2}/{@code disease3} fields on {@link Opd} itself.
 */
@Entity
@Table(name = "OH_OPD_DISEASE")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "ODS_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "ODS_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "ODS_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "ODS_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "ODS_LAST_MODIFIED_DATE"))
public class OpdDisease extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ODS_ID")
	private int id;

	@ManyToOne
	@JoinColumn(name = "ODS_OPD_ID")
	private Opd opd;

	@ManyToOne
	@JoinColumn(name = "ODS_DIS_ID_A")
	private Disease disease;

	@Version
	@Column(name = "ODS_LOCK")
	private int lock;

	public OpdDisease() {
	}

	public OpdDisease(Opd opd, Disease disease) {
		this.opd = opd;
		this.disease = disease;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Opd getOpd() {
		return opd;
	}

	public void setOpd(Opd opd) {
		this.opd = opd;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

}
