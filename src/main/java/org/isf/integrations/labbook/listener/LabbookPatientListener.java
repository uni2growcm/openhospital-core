package org.isf.integrations.labbook.listener;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import org.isf.integrations.labbook.mappers.LabbookPatientMapper;
import org.isf.integrations.labbook.ports.ILabbookService;
import org.isf.integrations.labbook.services.LabbookService;
import org.isf.patient.model.Patient;
import org.springframework.stereotype.Component;

@Component
public class LabbookPatientListener implements ILabbookService {

	private final LabbookService labbookService;
	private final LabbookPatientMapper mapper;

	public LabbookPatientListener(LabbookService labbookService, LabbookPatientMapper mapper) {
		this.labbookService = labbookService;
		this.mapper = mapper;
	}

	@PostPersist
	public void onPatientCreated(Patient patient) {
		if (patient == null || patient.getCode() == null) return;
		labbookService.createPatient(mapper.toCreatePatientRequest(patient));
	}

	@PostUpdate
	public void onPatientUpdated(Patient patient) {
		if (patient == null || patient.getCode() == null) return;
		labbookService.createPatient(mapper.toCreatePatientRequest(patient));
	}
}
