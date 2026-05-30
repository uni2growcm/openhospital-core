/*
 * Open Hospital (www.open-hospital.org)
 * Copyright  2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.time.LocalDateTime;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="OH_ARCHIVED_BILLPAYMENTS")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "BLP_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "BLP_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "BLP_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "BLP_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "BLP_LAST_MODIFIED_DATE"))
public class ArchivedBillPayments extends Auditable<String> {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="BLP_ID")
	private int id;

	@Column(name="BLP_ID_BILL")
	private Integer billId;

	@NotNull
	@Column(name="BLP_DATE")
	private LocalDateTime date;

	@NotNull
	@Column(name="BLP_AMOUNT")
	private double amount;

	@NotNull
	@Column(name="BLP_USR_ID_A")
	private String user;

	public ArchivedBillPayments() {
		super();
	}

	// Getters and Setters
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public Integer getBillId() { return billId; }
	public void setBillId(Integer billId) { this.billId = billId; }

	public LocalDateTime getDate() { return date; }
	public void setDate(LocalDateTime date) { this.date = date; }

	public double getAmount() { return amount; }
	public void setAmount(double amount) { this.amount = amount; }

	public String getUser() { return user; }
	public void setUser(String user) { this.user = user; }
}