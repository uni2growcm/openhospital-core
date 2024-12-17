package org.isf.pregnancy.manager;

import java.util.List;

import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.utils.exception.OHServiceException;

public class PregnancyBrowserManager {

private final AdmissionBrowserManager admissionBrowserManager;
	
	public PregnancyBrowserManager(AdmissionBrowserManager admissionBrowserManager) {
		this.admissionBrowserManager = admissionBrowserManager;
	}
	
	public List<Admission> getAdmissionByName(String name) throws OHServiceException {
		return admissionBrowserManager.getAdmissionsBySex('F', name);
	}
}
