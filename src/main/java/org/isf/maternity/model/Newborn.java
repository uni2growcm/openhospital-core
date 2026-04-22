package org.isf.maternity.model;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;

/**
 * Newborn Model - Records for each newborn child from a delivery
 *
 * @author Hema
 * @version 1.15
 */
@Entity
@Table(name = "OH_NEWBORN")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "NBN_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "NBN_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "NBN_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "NBN_LAST_MODIFIED_DATE"))
@AttributeOverride(name = "active", column = @Column(name = "NBN_ACTIVE"))
public class Newborn extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "NBN_ID")
	private Integer id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "NBN_DLV_ID", nullable = false)
	private Delivery delivery;

	@Column(name = "NBN_NAME")
	private String name;

	@Column(name = "NBN_NEONATAL_STATUS")
	private String neonatalStatus;
	// LIVE, STILLBORN, DEAD, CRITICAL

	@Column(name = "NBN_BIRTH_ORDER")
	private String birthOrder;

	@Column(name = "NBN_GENDER")
	private String gender;

	@NotNull
	@Column(name = "NBN_BIRTH_WEIGHT", nullable = false)
	private Integer birthWeight;

	@Column(name = "NBN_BIRTH_LENGTH")
	private Double birthLength;

	@Column(name = "NBN_HEAD_CIRCUMFERENCE")
	private Double headCircumference;

	@Column(name = "NBN_APGAR_SCORE_1MIN")
	private Integer apgarScore1Min;

	@Column(name = "NBN_APGAR_SCORE_5MIN")
	private Integer apgarScore5Min;

	@Column(name = "NBN_RESUSCITATION_REQUIRED")
	private Boolean resuscitationRequired;

	@Column(name = "NBN_CRY_TIME")
	private String cryTime;

	@Column(name = "NBN_CONGENITAL_ANOMALIES", columnDefinition = "LONGTEXT")
	private String congenitalAnomalies;

	@NotNull
	@Column(name = "NBN_HIV_STATUS", nullable = false)
	private String hivStatus; // P / N / U

	@Version
	@Column(name = "NBN_LOCK")
	private Integer lock;

	public Newborn () {}

	public Newborn(
		Delivery delivery,
		String name,
		String gender
	) {
		this.delivery = delivery;
		this.name = name;
		this.gender = gender;
	}

	public Newborn(
		Delivery delivery,
		String name,
		String neonatalStatus,
		String birthOrder,
		String gender,
		Integer birthWeight,
		Double birthLength,
		Double headCircumference,
		Integer apgarScore1Min,
		Integer apgarScore5Min,
		Boolean resuscitationRequired,
		String cryTime,
		String congenitalAnomalies,
		String hivStatus
	) {
		this.delivery = delivery;
		this.name = name;
		this.neonatalStatus = neonatalStatus;
		this.birthOrder = birthOrder;
		this.gender = gender;
		this.birthWeight = birthWeight;
		this.birthLength = birthLength;
		this.headCircumference = headCircumference;
		this.apgarScore1Min = apgarScore1Min;
		this.apgarScore5Min = apgarScore5Min;
		this.resuscitationRequired = resuscitationRequired;
		this.cryTime = cryTime;
		this.congenitalAnomalies = congenitalAnomalies;
		this.hivStatus = hivStatus;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNeonatalStatus() {
		return neonatalStatus;
	}

	public void setNeonatalStatus(String neonatalStatus) {
		this.neonatalStatus = neonatalStatus;
	}

	public String getBirthOrder() {
		return birthOrder;
	}

	public void setBirthOrder(String birthOrder) {
		this.birthOrder = birthOrder;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getBirthWeight() {
		return birthWeight;
	}

	public void setBirthWeight(Integer birthWeight) {
		this.birthWeight = birthWeight;
	}

	public Double getBirthLength() {
		return birthLength;
	}

	public void setBirthLength(Double birthLength) {
		this.birthLength = birthLength;
	}

	public Double getHeadCircumference() {
		return headCircumference;
	}

	public void setHeadCircumference(Double headCircumference) {
		this.headCircumference = headCircumference;
	}

	public Integer getApgarScore1Min() {
		return apgarScore1Min;
	}

	public void setApgarScore1Min(Integer apgarScore1Min) {
		this.apgarScore1Min = apgarScore1Min;
	}

	public Integer getApgarScore5Min() {
		return apgarScore5Min;
	}

	public void setApgarScore5Min(Integer apgarScore5Min) {
		this.apgarScore5Min = apgarScore5Min;
	}

	public Boolean getResuscitationRequired() {
		return resuscitationRequired;
	}

	public void setResuscitationRequired(Boolean resuscitationRequired) {
		this.resuscitationRequired = resuscitationRequired;
	}

	public String getCryTime() {
		return cryTime;
	}

	public void setCryTime(String cryTime) {
		this.cryTime = cryTime;
	}

	public String getCongenitalAnomalies() {
		return congenitalAnomalies;
	}

	public void setCongenitalAnomalies(String congenitalAnomalies) {
		this.congenitalAnomalies = congenitalAnomalies;
	}

	public String getHivStatus() {
		return hivStatus;
	}

	public void setHivStatus(String hivStatus) {
		this.hivStatus = hivStatus;
	}

	public Integer getLock() {
		return lock;
	}

	public void setLock(Integer lock) {
		this.lock = lock;
	}
}