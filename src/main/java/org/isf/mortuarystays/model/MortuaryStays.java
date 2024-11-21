/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.mortuarystays.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_MORTUARYSTAYS")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MRTST_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "MRTST_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MRTST_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "MRTST_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MRTST_LAST_MODIFIED_DATE"))
public class MortuaryStays extends Auditable<String> {

	@Id
	@Column(name = "MRTST_CODE")
	private String code;

	@NotNull
	@Column(name = "MRTST_NAME")
	private String name;

	@NotNull
	@Column(name = "MRTST_DESC")
	private String description;

	@NotNull
	@Column(name = "MRTST_MAX_DAYS")
	private int daysMax;

	@NotNull
	@Column(name = "MRTST_MIN_DAYS")
	private int daysMin;

	public MortuaryStays(){
		super();
	}

	public MortuaryStays(String code, String name,String description, int daysMax, int daysMin){
		this.code = code;
		this.name = name;
		this.description = description;
		this.daysMax = daysMax;
		this.daysMin = daysMin;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDaysMax(int daysMax) {
		this.daysMax = daysMax;
	}

	public int getDaysMax() {
		return daysMax;
	}

	public void setDaysMin(int daysMin) {
		this.daysMin = daysMin;
	}

	public int getDaysMin() {
		return daysMin;
	}

	public String print() {
		return "mortuarystays code=." + getCode() + ". description=." + getDescription() + ". name=." + getName() +'.';
	}
}
