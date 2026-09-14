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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

import org.isf.medstockmovtype.model.MovementType;
import org.isf.supplier.model.Supplier;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * A "fiche de commande" (product order form): a set of medicals ordered from a supplier, that can later
 * be converted into a stock-in (charging) movement once the goods and their lots are received.
 */
@Entity
@Table(name = "OH_STOCKORDER")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "SO_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "SO_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "SO_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "SO_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "SO_LAST_MODIFIED_DATE"))
public class StockOrder extends Auditable<String> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SO_ID")
	private int id;

	@NotNull
	@Column(name = "SO_REFNO", unique = true)
	private String refNo;

	@NotNull
	@Column(name = "SO_DATE")
	private LocalDateTime orderDate;

	@ManyToOne
	@JoinColumn(name = "SO_SUP_ID")
	private Supplier supplier;

	@ManyToOne
	@JoinColumn(name = "SO_MMVT_ID_A")
	private MovementType chargeType;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "SO_STATUS")
	private StockOrderStatus status = StockOrderStatus.open;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderBy("id")
	private List<StockOrderRow> rows = new ArrayList<>();

	@Version
	@Column(name = "SO_LOCK")
	private int lock;

	@Transient
	private volatile int hashCode;

	public StockOrder() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public MovementType getChargeType() {
		return chargeType;
	}

	public void setChargeType(MovementType chargeType) {
		this.chargeType = chargeType;
	}

	public StockOrderStatus getStatus() {
		return status;
	}

	public void setStatus(StockOrderStatus status) {
		this.status = status;
	}

	public List<StockOrderRow> getRows() {
		return rows;
	}

	public void setRows(List<StockOrderRow> rows) {
		this.rows.clear();
		if (rows != null) {
			for (StockOrderRow row : rows) {
				row.setOrder(this);
				this.rows.add(row);
			}
		}
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof StockOrder stockOrder)) {
			return false;
		}
		return this.getId() == stockOrder.getId();
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			final int m = 23;
			int c = 139;
			c = m * c + id;
			this.hashCode = c;
		}
		return this.hashCode;
	}
}
