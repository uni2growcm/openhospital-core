package org.isf.reductionplan.model;

import java.io.Serializable;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_SUPPLIER")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "SUP_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "SUP_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "SUP_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "SUP_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "SUP_LAST_MODIFIED_DATE"))

public class Reductionplan extends Auditable<String> implements Serializable {


	RP_ID
					RP_DESCRIPTION
	RP_OPERATIONRATE
					RP_MEDICALRATE,  RP_EXAMRATE,  RP_DATE, RP_UPDATE
}

