package org.isf.opd.model;

import jakarta.persistence.*;

import org.isf.disease.model.Disease;

@Entity
@Table(name = "OH_OPD_DIAGNOSIS")
public class DiagnosisEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "OPDD_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OPDD_OPD_ID", nullable = false)
	private Opd opd;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OPDD_DIS_ID_A", nullable = false)
	private Disease disease;

	@Column(name = "OPDD_ORDER")
	private Integer orderNumber = 0;

	@Column(name = "OPDD_IS_PRIMARY")
	private boolean primaryDiagnosis = false;

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

	public DiagnosisEntry(
		Opd opd,
		Disease disease,
		Integer orderNumber,
		boolean primaryDiagnosis) {

		this.opd = opd;
		this.disease = disease;
		this.orderNumber = orderNumber;
		this.primaryDiagnosis = primaryDiagnosis;
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

	public boolean isPrimaryDiagnosis() {
		return primaryDiagnosis;
	}

	public int getLock() {
		return lock;
	}

	public boolean isActive() {
		return active;
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

	public void setPrimaryDiagnosis(boolean primaryDiagnosis) {
		this.primaryDiagnosis = primaryDiagnosis;
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
			", primaryDiagnosis=" + primaryDiagnosis +
			", active=" + active +
			'}';
	}
}