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
package org.isf.accounting.model;

import java.util.ArrayList;
import java.util.List;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "OH_BILLITEMGROUP")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "BLIG_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "BLIG_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "BLIG_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "BLIG_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "BLIG_LAST_MODIFIED_DATE"))
public class BillItemGroup extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BLIG_ID")
	private int id;

	@NotNull
	@Column(name = "BLIG_TITLE")
	private String title;

	@Column(name = "BLIG_DESCRIPTION")
	private String description;

	@NotNull
	@Column(name = "BLIG_TOTAL")
	private Double total = 0.0;

	@OneToMany(mappedBy = "billItemGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BillItemGroupItem> items = new ArrayList<>();

	@Version
	@Column(name = "BLIG_LOCK")
	private Integer lock = 0;

	@Transient
	private volatile int hashCode;

	/**
	 * Default constructor
	 */
	public BillItemGroup() {
		super();
	}

	/**
	 * Constructor with parameters
	 * 
	 * @param title the title of the group
	 * @param description the description of the group
	 */
	public BillItemGroup(String title, String description) {
		super();
		this.title = title;
		this.description = description;
		this.total = 0.0;
	}

	/**
	 * Constructor with parameters
	 * 
	 * @param title the title of the group
	 * @param description the description of the group
	 * @param total the total amount
	 */
	public BillItemGroup(String title, String description, Double total) {
		super();
		this.title = title;
		this.description = description;
		this.total = total;
	}

	/**
	 * Full constructor
	 * 
	 * @param id the id
	 * @param title the title of the group
	 * @param description the description of the group
	 * @param total the total amount
	 */
	public BillItemGroup(int id, String title, String description, Double total) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.total = total;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Integer getLock() {
		return lock;
	}

	public void setLock(Integer lock) {
		this.lock = lock;
	}

	public List<BillItemGroupItem> getItems() {
		return items;
	}

	public void setItems(List<BillItemGroupItem> items) {
		this.items.clear();

		if (items != null) {
			for (BillItemGroupItem item : items) {
				addItem(item);
			}
		}
	}

	/**
	 * Adds an item to the bill item group
	 * 
	 * @param item the BillItemGroupItem to add
	 */
	public void addItem(BillItemGroupItem item) {
		if (item != null) {
			item.setBillItemGroup(this);
			this.items.add(item);
		}
	}

	/**
	 * Removes an item from the bill item group
	 * 
	 * @param item the BillItemGroupItem to remove
	 */
	public void removeItem(BillItemGroupItem item) {
		if (item != null) {
			this.items.remove(item);
			item.setBillItemGroup(null);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BillItemGroup) {
			BillItemGroup other = (BillItemGroup) obj;
			return this.id == other.id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			this.hashCode = 31 + this.id;
		}
		return this.hashCode;
	}

	@Override
	public String toString() {
		return "BillItemGroup [id=" + id + ", title=" + title + ", description=" + description + ", total=" + total + "]";
	}
}
