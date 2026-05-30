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
@Table(name = "OH_ARCHIVED_BILLS")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "BLL_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "BLL_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "BLL_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "BLL_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "BLL_LAST_MODIFIED_DATE"))
public class ArchivedBill extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BLL_ID")
	private int id;

	@NotNull
	@Column(name = "BLL_DATE")
	private LocalDateTime date;

	@NotNull
	@Column(name = "BLL_UPDATE")
	private LocalDateTime update;

	@NotNull
	@Column(name = "BLL_IS_LST")
	private boolean isList;

	@Column(name = "BLL_ID_LST")
	private Integer listId;

	@Column(name = "BLL_LST_NAME")
	private String listName;

	@NotNull
	@Column(name = "BLL_IS_PAT")
	private boolean isPatient;

	@Column(name = "BLL_ID_PAT")
	private Integer billPatientId;

	@Column(name = "BLL_PAT_NAME")
	private String patName;

	@Column(name = "BLL_STATUS")
	private String status;

	@Column(name = "BLL_AMOUNT")
	private Double amount;

	@Column(name = "BLL_BALANCE")
	private Double balance;

	@NotNull
	@Column(name = "BLL_LOCK")
	private int lock;

	@NotNull
	@Column(name = "BLL_USR_ID_A")
	private String user;

	@Column(name = "BLL_ADM_ID")
	private Integer admissionId;

	@Column(name = "BLL_GUARANTOR")
	private String guarantorId;

	@Column(name = "BLL_PARENT_ID")
	private Integer parentId; // Réintroduit pour correspondre à oh_bills

	@Column(name = "BLL_GARANTE")
	private String garante; // Ajouté

	@Column(name = "BLL_IS_CLOSED_MANUALLY")
	private Boolean isClosedManually; // Ajouté

	@Column(name = "BLL_RP_ID")
	private Integer rpId;

	@Column(name = "BLL_WARD_ID")
	private String wardId;

	public ArchivedBill() {
		super();
	}

	// Getters and Setters
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public LocalDateTime getDate() { return date; }
	public void setDate(LocalDateTime date) { this.date = date; }
	public LocalDateTime getUpdate() { return update; }
	public void setUpdate(LocalDateTime update) { this.update = update; }
	public boolean isList() { return isList; }
	public void setIsList(boolean isList) { this.isList = isList; }
	public Integer getListId() { return listId; }
	public void setListId(Integer listId) { this.listId = listId; }
	public String getListName() { return listName; }
	public void setListName(String listName) { this.listName = listName; }
	public boolean isPatient() { return isPatient; }
	public void setIsPatient(boolean isPatient) { this.isPatient = isPatient; }
	public Integer getBillPatientId() { return billPatientId; }
	public void setBillPatientId(Integer billPatientId) { this.billPatientId = billPatientId; }
	public String getPatName() { return patName; }
	public void setPatName(String patName) { this.patName = patName; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	public Double getAmount() { return amount; }
	public void setAmount(Double amount) { this.amount = amount; }
	public Double getBalance() { return balance; }
	public void setBalance(Double balance) { this.balance = balance; }
	public int getLock() { return lock; }
	public void setLock(int lock) { this.lock = lock; }
	public String getUser() { return user; }
	public void setUser(String user) { this.user = user; }
	public Integer getAdmissionId() { return admissionId; }
	public void setAdmissionId(Integer admissionId) { this.admissionId = admissionId; }
	public String getGuarantorId() { return guarantorId; }
	public void setGuarantorId(String guarantorId) { this.guarantorId = guarantorId; }
	public Integer getParentId() { return parentId; }
	public void setParentId(Integer parentId) { this.parentId = parentId; }
	public String getGarante() { return garante; }
	public void setGarante(String garante) { this.garante = garante; }
	public Boolean getIsClosedManually() { return isClosedManually; }
	public void setIsClosedManually(Boolean isClosedManually) { this.isClosedManually = isClosedManually; }
	public Integer getRpId() { return rpId; }
	public void setRpId(Integer rpId) { this.rpId = rpId; }
	public String getWardId() { return wardId; }
	public void setWardId(String wardId) { this.wardId = wardId; }
}