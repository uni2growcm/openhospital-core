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
package org.isf.stockorder.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

import org.isf.medicals.model.Medical;

/**
 * One line item (medical + quantity) of a {@link StockOrder}.
 */
@Entity
@Table(name = "OH_STOCKORDER_ROW")
public class StockOrderRow implements Serializable, Comparable<StockOrderRow> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SOR_ID")
	private int id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "SOR_SO_ID")
	private StockOrder order;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "SOR_MDSR_ID")
	private Medical medical;

	@NotNull
	@Column(name = "SOR_QTY")
	private Integer quantity;

	@Version
	@Column(name = "SOR_LOCK")
	private int lock;

	@Transient
	private volatile int hashCode;

	public StockOrderRow() {
	}

	public StockOrderRow(Medical medical, Integer quantity) {
		this.medical = medical;
		this.quantity = quantity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public StockOrder getOrder() {
		return order;
	}

	public void setOrder(StockOrder order) {
		this.order = order;
	}

	public Medical getMedical() {
		return medical;
	}

	public void setMedical(Medical medical) {
		this.medical = medical;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	@Override
	public int compareTo(StockOrderRow other) {
		return this.id - other.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof StockOrderRow stockOrderRow)) {
			return false;
		}
		return this.getId() == stockOrderRow.getId();
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			final int m = 23;
			int c = 137;
			c = m * c + id;
			this.hashCode = c;
		}
		return this.hashCode;
	}
}
