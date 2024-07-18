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
import org.isf.orthanc.model.OrthancConfig;
import org.isf.orthanc.model.OrthancPatient;
import org.isf.orthanc.service.OrthancConfigIoOperation;
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

    
    private OrthancConfigIoOperation orthancConfigIoOperation;
    
    private OrthancPatientIoOperation orthancPatientIoOperation;

    public OrthancBrowserManager(OrthancConfigIoOperation orthancConfigIoOperation,
			OrthancPatientIoOperation orthancPatientIoOperation) {
		super();
		this.orthancConfigIoOperation = orthancConfigIoOperation;
		this.orthancPatientIoOperation = orthancPatientIoOperation;
	}

    /**
	 * Return {@link OrthancConfig}
	 * 
	 * @param userName - the user name
	 * @return {@link OrthancConfig}. It could be {@code null}.
	 * @throws OHServiceException 
	 */
	public OrthancConfig getOrtancConfigByUserName(String userName) throws OHServiceException {
		return orthancConfigIoOperation.getOrtancConfigByUserName(userName);
	}
	
	/**
	 * Insert an {@link OrthancConfig} in the DB
	 * 
	 * @param orthancConfig - the {@link OrthancConfig} to insert
	 * @return the {@link OrthancConfig} that has been inserted, {@code null} otherwise.
	 * @throws OHServiceException 
	 */
	public OrthancConfig newOrthancConfig(OrthancConfig orthancConfig) throws OHServiceException {
		return orthancConfigIoOperation.newOrthancConfig(orthancConfig);
	}
	
	/**
	 * Update an {@link OrthancConfig}
	 * 
	 * @param orthancConfig - the {@link OrthancConfig} to update
	 * @return the {@link OrthancConfig} that has been updated, {@code null} otherwise.
	 * @throws OHServiceException 
	 */
	public OrthancConfig updateOrthancConfig(OrthancConfig orthancConfig) throws OHServiceException {
		return orthancConfigIoOperation.updateOrthancConfig(orthancConfig);
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
	
	public Object getPatientById(String id, String userName) throws OHServiceException {
		OrthancConfig config = orthancConfigIoOperation.getOrtancConfigByUserName(userName);
		String user = config.getOrthancUserName();
		String password = config.getOrthancPassword();
		HttpHeaders headers = createHeaders(user, password);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = createRestTemplate();
        ResponseEntity<Object> response = restTemplate.exchange(ORTHANCBASEURL + "/patients/"+id, HttpMethod.GET, entity, Object.class);
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
