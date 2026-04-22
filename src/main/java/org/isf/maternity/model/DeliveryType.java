package org.isf.maternity.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Delivery type Model
 * @author Hema
 * @version 1.15
 */
@Entity
@Table(name = "OH_PREGNANCYDELIVERYTYPE")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PRGDLVT_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PRGDLVT_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PRGDLVT_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PRGDLVT_LAST_MODIFIED_DATE"))
public class DeliveryType extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PRGDLVT_ID")
	private Integer id;

	@NotNull
	@Column(name = "PRGDLVT_CODE", nullable = false, unique = true, length = 20)
	private String code;

	@NotNull
	@Column(name = "PRGDLVT_DESCRIPTION", nullable = false, length = 255)
	private String description;

	public DeliveryType() {}

	public DeliveryType(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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
	public boolean equals(Object anObject) {
		return anObject instanceof DeliveryType && getCode().equals(((DeliveryType) anObject).getCode())
			&& getDescription().equalsIgnoreCase(((DeliveryType) anObject).getDescription());
	}

	@Override
	public String toString() {
		return "Delivery Type{" +
			"id=" + id +
			", code='" + code + '\'' +
			", description='" + description + '\'' +
			'}';
	}
}
