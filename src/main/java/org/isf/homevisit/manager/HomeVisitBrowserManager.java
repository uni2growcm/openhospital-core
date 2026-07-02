/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.homevisit.manager;

import jakarta.persistence.EntityNotFoundException;
import org.isf.homevisit.model.HomeVisit;
import org.isf.homevisit.model.HomeVisitStatus;
import org.isf.homevisit.service.HomeVisitIoOperations;
import org.isf.patient.model.Patient;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class HomeVisitBrowserManager {

	private final HomeVisitIoOperations ioOperations;

	public HomeVisitBrowserManager(HomeVisitIoOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	/**
	 * Returns all active home visits (paginated)
	 * @param page page number (0-indexed)
	 * @param size page size
	 * @return page of home visits
	 * @throws OHServiceException
	 */
	public Page<HomeVisit> getHomeVisits(int page, int size) throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size);
		return ioOperations.getAllActive(pageable);
	}

	/**
	 * Returns home visits for a specific patient
	 * @param patient the patient
	 * @return list of home visits for the patient
	 * @throws OHServiceException
	 */
	public List<HomeVisit> getHomeVisitsByPatient(Patient patient) throws OHServiceException {
		return ioOperations.getByPatient(patient);
	}

	/**
	 * Returns a home visit by id
	 * @param id home visit id
	 * @return home visit
	 * @throws EntityNotFoundException if not found
	 * @throws OHServiceException
	 */
	public HomeVisit getHomeVisit(int id) throws OHServiceException {
		return ioOperations.getById(id)
			.orElseThrow(() -> new EntityNotFoundException(
				MessageBundle.formatMessage("angal.homevisit.notfound.msg", String.valueOf(id))
			));
	}

	/**
	 * Returns home visits by status (paginated)
	 * @param status visit status
	 * @param page page number
	 * @param size page size
	 * @return page of home visits
	 * @throws OHServiceException
	 */
	public Page<HomeVisit> getHomeVisitsByStatus(HomeVisitStatus status, int page, int size) throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size);
		return ioOperations.getByStatus(status, pageable);
	}

	/**
	 * Returns home visits within a date range (paginated)
	 * @param startDate start date
	 * @param endDate end date
	 * @param page page number
	 * @param size page size
	 * @return page of home visits
	 * @throws OHServiceException
	 */
	public Page<HomeVisit> getHomeVisitsByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size) throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size);
		return ioOperations.getByDateRange(startDate, endDate, pageable);
	}

	/**
	 * Saves a home visit (create or update)
	 * @param homeVisit home visit to save
	 * @return saved home visit
	 * @throws OHServiceException
	 */
	public HomeVisit saveHomeVisit(HomeVisit homeVisit) throws OHServiceException {
		validateHomeVisit(homeVisit);
		return ioOperations.save(homeVisit);
	}

	/**
	 * Updates the status of a home visit
	 * @param id home visit id
	 * @param status new status
	 * @throws OHServiceException
	 */
	public void updateHomeVisitStatus(int id, HomeVisitStatus status) throws OHServiceException {
		ioOperations.updateStatus(id, status);
	}

	/**
	 * Soft deletes a home visit
	 * @param id home visit id to delete
	 * @throws OHServiceException
	 */
	public void deleteHomeVisit(int id) throws OHServiceException {
		ioOperations.softDelete(id);
	}

	/**
	 * Validates home visit data
	 * @param homeVisit home visit to validate
	 * @throws OHDataValidationException
	 */
	private void validateHomeVisit(HomeVisit homeVisit) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (homeVisit.getPatient() == null) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.homevisit.validation.patient.required.msg")));
		}

		if (homeVisit.getVisitStartDate() == null) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.homevisit.validation.startdate.required.msg")));
		}

		if (homeVisit.getVisitEndDate() != null && homeVisit.getVisitStartDate() != null &&
			homeVisit.getVisitEndDate().isBefore(homeVisit.getVisitStartDate())) {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.homevisit.validation.enddate.after.start.msg")));
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * @param homeVisit entité avec le nouveau statut et les champs modifiés par la GUI
	 * @return home visit mis à jour et persisté
	 * @throws OHServiceException
	 */
	public HomeVisit updateHomeVisit(HomeVisit homeVisit) throws OHServiceException {

		if (homeVisit.getId() == 0) {
			throw new OHDataValidationException(List.of(new OHExceptionMessage(
				MessageBundle.getMessage("angal.homevisit.validation.id.required.msg")
			)));
		}

		HomeVisitStatus requestedStatus = homeVisit.getStatus();
		String requestedCancellationReason = homeVisit.getCancellationReason();
		LocalDateTime requestedVisitStartDate = homeVisit.getVisitStartDate();

		HomeVisitStatus currentStatus = ioOperations.getCurrentStatus(homeVisit.getId());
		if (currentStatus == null) {
			throw new EntityNotFoundException(
				MessageBundle.formatMessage("angal.homevisit.notfound.msg",
					String.valueOf(homeVisit.getId())));
		}

		List<OHExceptionMessage> errors = new ArrayList<>();

		if (requestedStatus == HomeVisitStatus.COMPLETED) {
			if (currentStatus != HomeVisitStatus.PLANNED &&
				currentStatus != HomeVisitStatus.POSTPONED) {
				errors.add(new OHExceptionMessage(
					MessageBundle.getMessage("angal.homevisit.complete.error")));
			} else {
				homeVisit.setVisitEndDate(LocalDateTime.now());
			}

		} else if (requestedStatus == HomeVisitStatus.CANCELLED) {
			if (currentStatus == HomeVisitStatus.CANCELLED) {
				errors.add(new OHExceptionMessage(
					MessageBundle.getMessage("angal.homevisit.cancel.error")));
			} else if (requestedCancellationReason == null ||
				requestedCancellationReason.trim().isEmpty()) {
				errors.add(new OHExceptionMessage(
					MessageBundle.getMessage("angal.homevisit.cancel.reason.required")));
			}

		} else if (requestedStatus == HomeVisitStatus.PLANNED) {
			if (currentStatus != HomeVisitStatus.CANCELLED) {
				errors.add(new OHExceptionMessage(
					MessageBundle.getMessage("angal.homevisit.update.invalid.transition.msg")));
			} else {
				homeVisit.setCancellationReason(null);
			}

		}  else if (requestedStatus == HomeVisitStatus.POSTPONED) {
			if (currentStatus != HomeVisitStatus.PLANNED &&
				currentStatus != HomeVisitStatus.POSTPONED) {
				errors.add(new OHExceptionMessage(
					MessageBundle.getMessage("angal.homevisit.postpone.error")));
			} else if (requestedVisitStartDate == null) {
				errors.add(new OHExceptionMessage(
					MessageBundle.getMessage("angal.homevisit.postpone.newdate.required")));
			} else {
				homeVisit.setNextVisitDate(null);
			}

		} else {
			errors.add(new OHExceptionMessage(
				MessageBundle.getMessage("angal.homevisit.update.invalid.transition.msg")));
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}

		validateHomeVisit(homeVisit);
		return ioOperations.save(homeVisit);
	}

	public Page<HomeVisit> getHomeVisitsWithFilters(
		Integer code,
		HomeVisitStatus status,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		Character sex,
		Integer ageFrom,
		Integer ageTo,
		String searchText,
		int page,
		int size) throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size);
		return ioOperations.getWithFilters(
			code,
			status,
			dateFrom,
			dateTo,
			sex,
			ageFrom,
			ageTo,
			searchText,
			pageable);
	}
}