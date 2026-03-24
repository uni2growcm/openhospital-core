package org.isf.integrations.labbook.models;

import org.springframework.stereotype.Component;

@Component
public class GetPatientRequest {
	private String term;

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
}
