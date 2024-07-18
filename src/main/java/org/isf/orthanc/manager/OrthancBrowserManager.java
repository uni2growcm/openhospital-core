/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.orthanc.manager;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import org.isf.generaldata.GeneralData;
import org.isf.orthanc.model.OrthancPatient;
import org.isf.orthanc.model.OrthancUser;
import org.isf.orthanc.service.OrthancUserIoOperation;
import org.isf.orthanc.service.OrthancPatientIoOperation;
import org.isf.utils.exception.OHServiceException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrthancBrowserManager {

    private final String ORTHANCBASEURL = GeneralData.ORTHANCBASEURL;

    
    private OrthancUserIoOperation orthancUserIoOperation;
    
    private OrthancPatientIoOperation orthancPatientIoOperation;

    public OrthancBrowserManager(OrthancUserIoOperation orthancUserIoOperation,
			OrthancPatientIoOperation orthancPatientIoOperation) {
		super();
		this.orthancUserIoOperation = orthancUserIoOperation;
		this.orthancPatientIoOperation = orthancPatientIoOperation;
	}

    /**
	 * Return {@link OrthancUser}
	 * 
	 * @param ohUserId - the oh user name
	 * @return {@link OrthancUser}. It could be {@code null}.
	 * @throws OHServiceException 
	 */
	public OrthancUser getOrtancUserByOhUserId(String ohUserId) throws OHServiceException {
		return orthancUserIoOperation.getOrtancUserByOhUserId(ohUserId);
	}
	
	/**
	 * Insert an {@link OrthancUser} in the DB
	 * 
	 * @param orthancUser - the {@link OrthancUser} to insert
	 * @return the {@link OrthancUser} that has been inserted, {@code null} otherwise.
	 * @throws OHServiceException 
	 */
	public OrthancUser newOrthancUser(OrthancUser orthancUser) throws OHServiceException {
		return orthancUserIoOperation.newOrthancUser(orthancUser);
	}
	
	/**
	 * Update an {@link OrthancUser}
	 * 
	 * @param orthancUser - the {@link OrthancUser} to update
	 * @return the {@link OrthancUser} that has been updated, {@code null} otherwise.
	 * @throws OHServiceException 
	 */
	public OrthancUser updateOrthancConfig(OrthancUser orthancUser) throws OHServiceException {
		return orthancUserIoOperation.updateOrthancUser(orthancUser);
	}

	/**
	 * Return {@link OrthancPatient}
	 * 
	 * @param patId - the patient id
	 * @return {@link OrthancPatient}. It could be {@code null}.
	 * @throws OHServiceException 
	 */
	public OrthancPatient getOrthancPatientByPatientId(int patId) throws OHServiceException {
		return orthancPatientIoOperation.getOrthancPatientByPatientId(patId);
	}
	
	/**
	 * Return {@link OrthancPatient}
	 * 
	 * @param id - the orthan patient id
	 * @return {@link OrthancPatient}. It could be {@code null}.
	 * @throws OHServiceException 
	 */
	public OrthancPatient getOrthancPatientById(int id) throws OHServiceException {
		Optional<OrthancPatient> optionalOrthancPatient = orthancPatientIoOperation.getOrthancPatientById(id);
		if (optionalOrthancPatient.isPresent()) {
			return optionalOrthancPatient.get();
		}
		return null;
	}
	
	/**
	 * Insert an {@link OrthancPatient} in the DB
	 * 
	 * @param orthancPatient - the {@link OrthancPatient} to insert
	 * @return the {@link OrthancPatient} that has been inserted, {@code null} otherwise.
	 * @throws OHServiceException 
	 */
	public OrthancPatient newOrthancPatient(OrthancPatient orthancPatient) throws OHServiceException {
		return orthancPatientIoOperation.newOrthancPatient(orthancPatient);
	}
	
	/**
	 * Update an {@link OrthancPatient}
	 * 
	 * @param orthancPatient - the {@link OrthancPatient} to update
	 * @return the {@link OrthancPatient} that has been updated, {@code null} otherwise.
	 * @throws OHServiceException 
	 */
	public OrthancPatient updateOrthancPatient(OrthancPatient orthancPatient) throws OHServiceException {
		return orthancPatientIoOperation.updateOrthancPatient(orthancPatient);
	}

	public String getStudies() {
		HttpHeaders headers = createHeaders("u2g", "u2g123"); // use the credential of current user here
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = createRestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(ORTHANCBASEURL + "/studies", HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    private HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {
            {
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
            }
        };
    }
    
    private RestTemplate createRestTemplate() {
    	RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }
}
