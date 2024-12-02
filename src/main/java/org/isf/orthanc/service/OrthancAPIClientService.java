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
package org.isf.orthanc.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.isf.orthanc.client.OrthancAPIClient;
import org.isf.orthanc.client.OrthancFeignClientFactory;
import org.isf.orthanc.model.FindRequest;
import org.isf.orthanc.model.FindRequestLevel;
import org.isf.orthanc.model.InstanceResponse;
import org.isf.orthanc.model.Query;
import org.isf.orthanc.model.SeriesResponse;
import org.isf.orthanc.model.StudyResponse;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;

import feign.Response;

/**
 * @author Silevester D.
 * @since 1.15
 */
@Service
public class OrthancAPIClientService {

	private final OrthancFeignClientFactory feignClientFactory;

	public OrthancAPIClientService(OrthancFeignClientFactory feignClientFactory) {
		this.feignClientFactory = feignClientFactory;
	}

	/**
	 * Test if the connection with ORTHANC server can be established
	 *
	 * @return <code>true</code> if connection successfully established, <code>false</code> otherwise
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 */
	public boolean testConnection() throws OHServiceException {
		String time = getClient().testConnection();

		return time != null && !time.isEmpty() ;
	}

	/**
	 * Get Feign instance
	 *
	 * @return instance of {@link OrthancAPIClient}
	 * @throws OHServiceException see when {@link OrthancFeignClientFactory#createClient()} throws this exception
	 */
	public OrthancAPIClient getClient() throws OHServiceException {
		return feignClientFactory.createClient();
	}

	/**
	 * Get all studies
	 *
	 * @return The list of studies
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 */
	public List<StudyResponse> getAllStudies() throws OHServiceException {
		return getClient().getAllStudies();
	}

	/**
	 * Get patient's studies
	 * <p>This method uses the find tool of ORTHANC to matches patient and retrieve his studies.
	 * We assume that the value of the tag <code>PatientId</code> in the study's prop refers
	 * OH patient's ID.</p>
	 *
	 * @return The list of studies related to the given patient ID
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 */
	public List<StudyResponse> getPatientStudiesById(String patientId) throws OHServiceException {
		FindRequest request = new FindRequest(
			FindRequestLevel.Study.name(),
			true,
			new Query(patientId)
		);

		return getClient().getPatientStudiesById(request);
	}

	/**
	 * Get patient's studies
	 * <p>This method retrieves studies related to a given patient using patient's UUID
	 * stored in ORTHANC server.</p>
	 *
	 * @return The list of studies related to the given patient ID
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 */
	public List<StudyResponse> getPatientStudiesByUuid(String patientUUID) throws OHServiceException {
		return getClient().getPatientStudiesByUuid(patientUUID);
	}

	/**
	 * Get a study using study's UUID
	 *
	 * @param studyId Study UUID
	 * @return the study that matched the provided UUID
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 */
	public StudyResponse getStudyById(String studyId) throws OHServiceException {
		return getClient().getStudyById(studyId);
	}

	/**
	 * Get study's series using study's UUID
	 *
	 * @param studyId Study UUID
	 * @return the list of study's series
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 */
	public List<SeriesResponse> getStudySeries(String studyId) throws OHServiceException {
		return getClient().getStudySeries(studyId);
	}

	/**
	 * Get a series using series' UUID
	 *
	 * @param seriesId Series UUID
	 * @return the series that matched the provided UUID
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 */
	public SeriesResponse getSeriesById(String seriesId) throws OHServiceException {
		return getClient().getSeriesById(seriesId);
	}

	/**
	 * Get series' instances using series' UUID
	 *
	 * @param seriesId Series UUID
	 * @return the list of series' instances
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 */
	public List<InstanceResponse> getSeriesInstances(String seriesId) throws OHServiceException {
		return getClient().getSeriesInstances(seriesId);
	}

	/**
	 * Get an instance using instance's UUID
	 *
	 * @param instanceId Instance UUID
	 * @return the instance that matched the provided UUID
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 */
	public InstanceResponse getInstanceById(String instanceId) throws OHServiceException {
		return getClient().getInstanceById(instanceId);
	}

	/**
	 * Get the preview of an instance using instance's UUID
	 *
	 * @param instanceId Instance UUID
	 * @return the instance preview image in PNG format
	 * @throws OHServiceException see when {@link #getClient()} throws this exception
	 */
	public byte[] getInstancePreview(String instanceId) throws OHServiceException {
		// TODO: Better handle the file download to avoid {@link OutOfMemoryError} error

		try (Response response = getClient().getInstancePreview(instanceId)) {
			InputStream inputStream = response.body().asInputStream();

			return inputStream.readAllBytes();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
