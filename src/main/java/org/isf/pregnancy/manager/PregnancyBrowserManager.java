package org.isf.pregnancy.manager;

import java.util.ArrayList;

import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.AdmittedPatient;
import org.isf.patient.model.Patient;

/**
 * Martin Reinstadler
 * This class manages the IO operations by calling the various methods from them. 
 * The GUI instantiates only this class and no IoOperaitions class for simplicity
 *
 */
public class PregnancyBrowserManager {
	
	/**
	 * @param regex the searchkey
	 * @return a list of female {@link Patient} 
	 */
	public List<Admission> getPregnancyPatients(String regex){
		AdmissionBrowserManager adManager=new AdmissionBrowserManager();
		return adManager.getPregnancyAdmittedPatients(regex);
	}
	
	/**
	 * @param regex the searchkey
	 * @param pAGE_SIZE 
	 * @param sTART_INDEX 
	 * @return a list of female {@link Patient} 
	 */
	public List<Admission> getPregnancyPatients(String regex, int sTART_INDEX, int pAGE_SIZE){
		AdmissionBrowserManager adManager=new AdmissionBrowserManager();
		return adManager.getPregnancyAdmittedPatients(regex, sTART_INDEX, pAGE_SIZE);
	}
	 
	 public int getPregnancyPatientsCount(String regex){
		AdmissionBrowserManager adManager=new AdmissionBrowserManager();
		return adManager.getPregnancyPatientsCount(regex);
	 }
}