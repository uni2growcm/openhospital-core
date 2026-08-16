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
package org.isf.articlefamily.model;

import java.io.Serializable;

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

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Family (category) an article (medical, exam, ...) belongs to.
 */
@Entity
@Table(name = "OH_ARTICLE_FAMILIES")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "AFM_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "AFM_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "AFM_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "AFM_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "AFM_LAST_MODIFIED_DATE"))
public class ArticleFamily extends Auditable<String> implements Serializable, Comparable<ArticleFamily> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AFM_ID")
	private int id;

	@Column(name = "AFM_CODE", length = 50, nullable = false, unique = true)
	private String code;

	@Column(name = "AFM_DESC", length = 255, nullable = false)
	private String description;

	@Version
	@Column(name = "AFM_LOCK")
	private int lock;

	@Transient
	private volatile int hashCode;

	public ArticleFamily() {
		super();
	}

	public ArticleFamily(String code, String description) {
		this.code = code;
		this.description = description;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	@Override
	public int compareTo(ArticleFamily other) {
		return this.id - other.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ArticleFamily articleFamily)) {
			return false;
		}

		return this.getId() == articleFamily.getId();
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
		return this.description;
	}
}
