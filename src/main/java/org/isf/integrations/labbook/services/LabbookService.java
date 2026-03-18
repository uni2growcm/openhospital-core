package org.isf.integrations.labbook.services;

import org.isf.integrations.labbook.models.CreatePatientRequest;
import org.isf.integrations.labbook.ports.ILabbookService;
import org.springframework.stereotype.Service;

@Service
public class LabbookService implements ILabbookService {

	public void createPatient(CreatePatientRequest request) {
		try {
			sendLabbookRequest(request);
		} catch (Exception e) {
			throw e;
		}
	}
}
