package org.isf.integrations.labbook.models;

import org.springframework.stereotype.Component;

/**
 * Request to get a list of patient with patient code.
 *
 * @author Tatemsa B.
 */

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
