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
package org.isf.maternity.model;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

/**
 * Newborn Model - Records for each newborn child from a delivery
 *
 * @author Hema
 * @version 1.15
 */
@Entity
@Table(name = "OH_NEWBORN")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "NBN_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "NBN_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "NBN_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "NBN_LAST_MODIFIED_DATE"))
@AttributeOverride(name = "active", column = @Column(name = "NBN_ACTIVE"))
public class Newborn extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "NBN_ID")
	private Integer id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "NBN_DLV_ID", nullable = false)
	private Delivery delivery;

	@NotNull
	@Column(name = "NBN_NAME", nullable = false)
	private String name;

	@Column(name = "NBN_NEONATAL_STATUS")
	private String neonatalStatus;
	// LIVE, STILLBORN, DEAD, CRITICAL

	@Column(name = "NBN_BIRTH_ORDER")
	private String birthOrder;

	@Column(name = "NBN_GENDER")
	private String gender;

	@NotNull
	@Column(name = "NBN_BIRTH_WEIGHT", nullable = false)
	private Integer birthWeight;

	@Column(name = "NBN_BIRTH_LENGTH")
	private Double birthLength;

	@Column(name = "NBN_HEAD_CIRCUMFERENCE")
	private Double headCircumference;

	@Column(name = "NBN_APGAR_SCORE_1MIN")
	private Integer apgarScore1Min;

	@Column(name = "NBN_APGAR_SCORE_5MIN")
	private Integer apgarScore5Min;

	@Column(name = "NBN_RESUSCITATION_REQUIRED")
	private Boolean resuscitationRequired;

	@Column(name = "NBN_CRY_TIME")
	private String cryTime;

	@Column(name = "NBN_CONGENITAL_ANOMALIES", columnDefinition = "LONGTEXT")
	private String congenitalAnomalies;

	@NotNull
	@Column(name = "NBN_HIV_STATUS", nullable = false)
	private String hivStatus; // P / N / U

	@Version
	@Column(name = "NBN_LOCK")
	private Integer lock;

	// getters & setters...
}