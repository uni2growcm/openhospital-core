package org.isf.mortuary.model;

import java.util.GregorianCalendar;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_DEATHREASON")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "DR_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "DR_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "DR_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "DR_LAST_MODIFIED_DATE"))
@AttributeOverride(name = "active", column = @Column(name = "DR_ACTIVE"))
public class DeathReason {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DR_ID")
	private int id;

	@Column(name = "DR_CODE")
	private String code;

	@Column(name = "DR_DESC")
	private String description;

	public DeathReason() {

	}

	public DeathReason(int id, String code, String description) {
		super();
		this.id = id;
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

	@Override
	public String toString() {
		return description;
	}
}
