package org.isf.integrations.labbook.models;

import java.util.List;

public class PatientAnalysisResponse {

	private Object patient;
	private List<Object> analyzes;

	public Object getPatient() {
		return patient;
	}

	public void setPatient(Object patient) {
		this.patient = patient;
	}

	public List<Object> getAnalyzes() {
		return analyzes;
	}

	public void setAnalyzes(List<Object> analyzes) {
		this.analyzes = analyzes;
	}
}