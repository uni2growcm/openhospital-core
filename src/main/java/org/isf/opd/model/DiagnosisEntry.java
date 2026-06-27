package org.isf.opd.model;

import jakarta.persistence.*;
import org.isf.disease.model.Disease;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "OH_OPD_DIAGNOSIS")
@EntityListeners(AuditingEntityListener.class)
public class DiagnosisEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "OPDD_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OPDD_OPD_ID", nullable = false)
	private Opd opd;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OPDD_DIS_ID_A", nullable = false)
	private Disease disease;

	@Column(name = "OPDD_ORDER")
	private Integer orderNumber = 0;

	@Version
	@Column(name = "OPDD_LOCK")
	private int lock;

	@Column(name = "OPDD_ACTIVE")
	private boolean active = true;

	public DiagnosisEntry() {
	}

	public DiagnosisEntry(Opd opd, Disease disease) {
		this.opd = opd;
		this.disease = disease;
	}

	@CreatedBy
	@Column(name = "OPDD_CREATED_BY", updatable = false)
	private String createdBy;

	@CreatedDate
	@Column(name = "OPDD_CREATED_DATE", updatable = false)
	private LocalDateTime createdDate;

	@LastModifiedBy
	@Column(name = "OPDD_LAST_MODIFIED_BY")
	private String lastModifiedBy;

	@LastModifiedDate
	@Column(name = "OPDD_LAST_MODIFIED_DATE")
	private LocalDateTime lastModifiedDate;

	public DiagnosisEntry(
		Opd opd,
		Disease disease,
		Integer orderNumber,
		boolean primaryDiagnosis) {

		this.opd = opd;
		this.disease = disease;
		this.orderNumber = orderNumber;
	}

	public Long getId() {
		return id;
	}

	public Opd getOpd() {
		return opd;
	}

	public Disease getDisease() {
		return disease;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public int getLock() {
		return lock;
	}

	public boolean isActive() {
		return active;
	}

	public String getCreatedBy()                 {
		return createdBy;
	}

	public LocalDateTime getCreatedDate()        {
		return createdDate;
	}

	public String getLastModifiedBy()            {
		return lastModifiedBy;
	}

	public LocalDateTime getLastModifiedDate()   {
		return lastModifiedDate;
	}


	public void setId(Long id) {
		this.id = id;
	}

	public void setOpd(Opd opd) {
		this.opd = opd;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;

		if (!(o instanceof DiagnosisEntry that)) return false;

		return id != null && id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "DiagnosisEntry{" +
			"id=" + id +
			", disease=" +
			(disease != null ? disease.getDescription() : null) +
			", orderNumber=" + orderNumber +
			", active=" + active +
			'}';
	}
}