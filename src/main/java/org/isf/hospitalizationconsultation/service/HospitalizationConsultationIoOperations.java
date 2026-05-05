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
package org.isf.hospitalizationconsultation.service;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.encounter.model.Encounter;
import org.isf.hospitalizationconsultation.model.HospitalizationConsultation;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.pagination.PageInfo;
import org.isf.utils.pagination.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class HospitalizationConsultationIoOperations {

	private final HospitalizationConsultationIoOperationRepository repository;

	public HospitalizationConsultationIoOperations(HospitalizationConsultationIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Returns all HospitalizationConsultations.
	 *
	 * @return the list of HospitalizationConsultations.
	 * @throws OHServiceException
	 */
	public List<HospitalizationConsultation> getHospitalizationConsultations() throws OHServiceException {
		return repository.findAll();
	}

	/**
	 * Returns all HospitalizationConsultations for a specific Encounter.
	 *
	 * @param encounter the Encounter
	 * @return the list of HospitalizationConsultations for the Encounter.
	 * @throws OHServiceException
	 */
	public List<HospitalizationConsultation> getHospitalizationConsultationsByEncounter(Encounter encounter) throws OHServiceException {
		return repository.findByEncounter(encounter);
	}

	/**
	 * Returns all HospitalizationConsultations within a date range.
	 *
	 * @param dateFrom start date
	 * @param dateTo end date
	 * @return the list of HospitalizationConsultations within the date range.
	 * @throws OHServiceException
	 */
	public List<HospitalizationConsultation> getHospitalizationConsultationsByDateRange(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return repository.findByDateTimeBetween(dateFrom, dateTo);
	}

	/**
	 * Returns the HospitalizationConsultation with the specified id.
	 *
	 * @param id the consultation id
	 * @return the HospitalizationConsultation with the specified id, {@code null} otherwise.
	 * @throws OHServiceException
	 */
	public HospitalizationConsultation getHospitalizationConsultation(int id) throws OHServiceException {
		return repository.findById(id).orElse(null);
	}

	/**
	 * Inserts a new HospitalizationConsultation.
	 *
	 * @param consultation the consultation to insert
	 * @return the inserted consultation
	 * @throws OHServiceException
	 */
	public HospitalizationConsultation newHospitalizationConsultation(HospitalizationConsultation consultation) throws OHServiceException {
		return repository.save(consultation);
	}

	/**
	 * Updates the specified HospitalizationConsultation.
	 *
	 * @param consultation the consultation to update
	 * @return the updated consultation
	 * @throws OHServiceException
	 */
	public HospitalizationConsultation updateHospitalizationConsultation(HospitalizationConsultation consultation) throws OHServiceException {
		return repository.save(consultation);
	}

	/**
	 * Deletes a HospitalizationConsultation.
	 *
	 * @param consultation the consultation to delete
	 * @throws OHServiceException
	 */
	public void deleteHospitalizationConsultation(HospitalizationConsultation consultation) throws OHServiceException {
		repository.delete(consultation);
	}

	/**
	 * Returns a paginated list of HospitalizationConsultations.
	 *
	 * @param pageRequest the page request
	 * @return the paginated list of HospitalizationConsultations
	 * @throws OHServiceException
	 */
	public PagedResponse<HospitalizationConsultation> getHospitalizationConsultationsPageable(PageRequest pageRequest) throws OHServiceException {
		Page<HospitalizationConsultation> page = repository.findAll(pageRequest);
		return setPaginationData(page);
	}

	/**
	 * Returns a paginated list of HospitalizationConsultations for a specific Encounter.
	 *
	 * @param encounter the Encounter
	 * @param pageRequest the page request
	 * @return the paginated list of HospitalizationConsultations for the Encounter
	 * @throws OHServiceException
	 */
	public PagedResponse<HospitalizationConsultation> getHospitalizationConsultationsByEncounterPageable(Encounter encounter, PageRequest pageRequest) throws OHServiceException {
		Page<HospitalizationConsultation> page = repository.findByEncounter(encounter, pageRequest);
		return setPaginationData(page);
	}

	/**
	 * Returns a paginated list of HospitalizationConsultations within a date range.
	 *
	 * @param dateFrom start date
	 * @param dateTo end date
	 * @param pageRequest the page request
	 * @return the paginated list of HospitalizationConsultations within the date range
	 * @throws OHServiceException
	 */
	public PagedResponse<HospitalizationConsultation> getHospitalizationConsultationsByDateRangePageable(LocalDateTime dateFrom, LocalDateTime dateTo, PageRequest pageRequest) throws OHServiceException {
		Page<HospitalizationConsultation> page = repository.findByDateTimeBetween(dateFrom, dateTo, pageRequest);
		return setPaginationData(page);
	}

	/**
	 * Checks if a consultation with the specified id exists.
	 *
	 * @param id the consultation id
	 * @return true if exists, false otherwise
	 * @throws OHServiceException
	 */
	public boolean existsById(int id) throws OHServiceException {
		return repository.existsById(id);
	}

	/**
	 * Counts all HospitalizationConsultations.
	 *
	 * @return the total number of consultations
	 * @throws OHServiceException
	 */
	public long countAll() throws OHServiceException {
		return repository.count();
	}

	/**
	 * Counts HospitalizationConsultations for a specific Encounter.
	 *
	 * @param encounter the Encounter
	 * @return the number of consultations for the Encounter
	 * @throws OHServiceException
	 */
	public long countByEncounter(Encounter encounter) throws OHServiceException {
		return repository.countByEncounter(encounter);
	}

	private PagedResponse<HospitalizationConsultation> setPaginationData(Page<HospitalizationConsultation> pages) {
		PagedResponse<HospitalizationConsultation> data = new PagedResponse<>();
		data.setData(pages.getContent());
		data.setPageInfo(PageInfo.from(pages));
		return data;
	}
}
