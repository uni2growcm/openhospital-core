package org.isf.maternity;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.maternity.model.Pregnancy;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

import java.time.LocalDateTime;

public class TestPregnancy {

	private final LocalDateTime lmp = LocalDateTime.of(2025, 1, 15, 10, 0);
	private final  LocalDateTime eddLmp = LocalDateTime.of(2025, 10, 22, 0, 0);
	private final LocalDateTime eddScan = LocalDateTime.of(2025, 10, 20, 0, 0);
	private final String riskLevel = "Low";
	private final String status = "Ongoing";

	public Pregnancy setup(Patient patient, boolean usingSet) throws OHException {
		Pregnancy pregnancy;

		if (usingSet) {
			pregnancy = new Pregnancy();
			setParameters(pregnancy, patient);
		} else {
			pregnancy = new Pregnancy(patient, status);
			setParameters(pregnancy, patient);
		}

		return pregnancy;
	}

	private void setParameters(Pregnancy p, Patient patient) {
		p.setPatient(patient);
		p.setLmp(lmp);
		p.setEddLmp(eddLmp);
		p.setEddScan(eddScan);
		Integer gravidity = 1;
		p.setGravidity(gravidity);
		Integer parity = 0;
		p.setParity(parity);
		Integer miscarriages = 0;
		p.setMiscarriages(miscarriages);
		String bloodGroup = "O+";
		p.setBloodGroup(bloodGroup);
		p.setRiskLevel(riskLevel);
		p.setStatus(status);
	}

	public void check(Pregnancy p) {
		assertThat(p.getStatus()).isEqualTo(status);
		assertThat(p.getRiskLevel()).isEqualTo(riskLevel);
		assertThat(p.getLmp()).isEqualTo(lmp);
	}
}