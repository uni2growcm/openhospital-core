package org.isf.country.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "country")
public class Country implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "iso_code", length = 2, nullable = false, unique = true)
	private String isoCode;

	@Column(name = "phone_code", nullable = false)
	private String phoneCode;

	@Column(length = 100, nullable = false, unique = true)
	private String name;

	public Country() {}

	public Country(String isoCode, String phoneCode, String name) {
		this.isoCode = isoCode;
		this.phoneCode = phoneCode;
		this.name = name;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getIsoCode() { return isoCode; }
	public void setIsoCode(String isoCode) { this.isoCode = isoCode; }

	public String getPhoneCode() { return phoneCode; }
	public void setPhoneCode(String phoneCode) { this.phoneCode = phoneCode; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	@Override
	public String toString() {
		return name;
	}
}