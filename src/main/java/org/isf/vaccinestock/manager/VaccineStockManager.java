/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.vaccinestock.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.isf.generaldata.MessageBundle;
import org.isf.patvac.model.PatientVaccine;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.time.TimeTools;
import org.isf.vaccine.model.Vaccine;
import org.isf.vaccinestock.model.VaccineLot;
import org.isf.vaccinestock.model.VaccineStockMovement;
import org.isf.vaccinestock.model.VaccineStockMovementReason;
import org.isf.vaccinestock.service.VaccineLotIoOperationRepository;
import org.isf.vaccinestock.service.VaccineStockMovementIoOperationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class VaccineStockManager {

	private final VaccineLotIoOperationRepository lotRepository;

	private final VaccineStockMovementIoOperationRepository movementRepository;

	public VaccineStockManager(VaccineLotIoOperationRepository lotRepository, VaccineStockMovementIoOperationRepository movementRepository) {
		this.lotRepository = lotRepository;
		this.movementRepository = movementRepository;
	}

	/**
	 * Records a charge (entry) of {@code quantity} doses into the given lot, creating the lot if it
	 * doesn't already exist. If the lot already exists, its stored fields (preparation/due date,
	 * cost) are used as-is; {@code lot}'s fields are only persisted for a brand-new lot.
	 *
	 * @throws OHServiceException if validation fails or the movement cannot be stored.
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	public VaccineStockMovement newCharge(Vaccine vaccine, VaccineLot lot, int quantity, LocalDateTime date, String note) throws OHServiceException {
		validateVaccine(vaccine);
		validateLot(lot);
		validateQuantity(quantity);

		VaccineLot persistedLot = lotRepository.findById(lot.getCode()).orElse(null);
		if (persistedLot == null) {
			lot.setVaccine(vaccine);
			persistedLot = lotRepository.save(lot);
		}

		VaccineStockMovement movement = new VaccineStockMovement(vaccine, persistedLot, quantity, VaccineStockMovementReason.SUPPLY, date);
		movement.setNote(note);
		return movementRepository.save(movement);
	}

	/**
	 * Records a manual discharge (loss, breakage, expired stock removed, correction) from the given
	 * lot. Unlike {@link #dispenseDose(PatientVaccine)}, this always blocks if it would drive the
	 * lot's balance negative - a manual entry is made by staff who should get immediate feedback.
	 *
	 * @throws OHServiceException if validation fails, the reason is one of the
	 *             administration-only reasons, there isn't enough stock in the lot, or the movement
	 *             cannot be stored.
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	public VaccineStockMovement newManualDischarge(Vaccine vaccine, VaccineLot lot, int quantity, VaccineStockMovementReason reason, LocalDateTime date,
			String note) throws OHServiceException {
		validateVaccine(vaccine);
		validateLot(lot);
		validateQuantity(quantity);
		if (reason == VaccineStockMovementReason.ADMINISTRATION || reason == VaccineStockMovementReason.ADMINISTRATION_CANCELLED) {
			throw new OHDataValidationException(new OHExceptionMessage(MessageBundle.getMessage("angal.vaccinestock.invalidmanualdischargereason.msg")));
		}

		VaccineLot persistedLot = lotRepository.findById(lot.getCode())
				.orElseThrow(() -> new OHDataValidationException(new OHExceptionMessage(MessageBundle.getMessage("angal.vaccinestock.lotnotfound.msg"))));
		int available = getQuantity(persistedLot);
		if (quantity > available) {
			throw new OHDataValidationException(
					new OHExceptionMessage(MessageBundle.formatMessage("angal.vaccinestock.notenoughstockinlot.fmt.msg", available, persistedLot.getCode())));
		}

		VaccineStockMovement movement = new VaccineStockMovement(vaccine, persistedLot, -quantity, reason, date);
		movement.setNote(note);
		return movementRepository.save(movement);
	}

	/**
	 * Decrements one dose for the given administration, FEFO-selecting the lot with the soonest
	 * expiry among those with a positive balance. Blocking: a patient cannot be vaccinated with a
	 * product that isn't in stock.
	 *
	 * @throws OHServiceException if no lot has a positive balance for the vaccine, or if the
	 *             movement cannot be stored once a lot has been selected.
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	public VaccineStockMovement dispenseDose(PatientVaccine patientVaccine) throws OHServiceException {
		Vaccine vaccine = patientVaccine.getVaccine();
		List<VaccineLot> availableLots = lotRepository.findAvailableByVaccineOrderByDueDate(vaccine.getCode());
		if (availableLots.isEmpty()) {
			throw new OHDataValidationException(
					new OHExceptionMessage(MessageBundle.formatMessage("angal.vaccinestock.nostockavailable.fmt.msg", vaccine.getDescription())));
		}

		VaccineLot lot = availableLots.get(0);
		VaccineStockMovement movement = new VaccineStockMovement(vaccine, lot, -1, VaccineStockMovementReason.ADMINISTRATION,
				patientVaccine.getVaccineDate());
		movement.setPatientVaccine(patientVaccine);
		return movementRepository.save(movement);
	}

	/**
	 * Reverses the dose decrement tied to the given administration, if any, by inserting an inverse
	 * (+1) movement on the same lot rather than deleting the original - preserves the lot's audit
	 * trail. Returns {@code null} (no-op) if {@link #dispenseDose(PatientVaccine)} found no stock at
	 * administration time, since there is then nothing to reverse.
	 *
	 * @throws OHServiceException if the reversal movement cannot be stored.
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	public VaccineStockMovement cancelAdministration(PatientVaccine patientVaccine) throws OHServiceException {
		List<VaccineStockMovement> movements = movementRepository.findByPatientVaccine_code(patientVaccine.getCode());
		Optional<VaccineStockMovement> administration = movements.stream()
				.filter(m -> VaccineStockMovementReason.ADMINISTRATION.name().equals(m.getReason()))
				.findFirst();
		if (administration.isEmpty()) {
			return null;
		}

		VaccineStockMovement original = administration.get();
		VaccineStockMovement reversal = new VaccineStockMovement(original.getVaccine(), original.getLot(), 1,
				VaccineStockMovementReason.ADMINISTRATION_CANCELLED, TimeTools.getNow());
		reversal.setPatientVaccine(patientVaccine);
		VaccineStockMovement savedReversal = movementRepository.save(reversal);

		// Callers (PatVacManager) delete patientVaccine right after this returns, in the same
		// transaction. The DB's ON DELETE SET NULL on VSM_PAV_ID is invisible to Hibernate, so
		// without detaching both movements here first, the pending delete leaves an in-memory
		// reference to a row that's about to disappear and Hibernate rejects the next flush with a
		// TransientObjectException. Null the link on both sides now, mirroring what the DB would do.
		original.setPatientVaccine(null);
		savedReversal.setPatientVaccine(null);
		movementRepository.save(original);
		return movementRepository.saveAndFlush(savedReversal);
	}

	/**
	 * All lots ever created for the vaccine, soonest expiry first, regardless of balance.
	 */
	public List<VaccineLot> getLots(Vaccine vaccine) {
		return lotRepository.findByVaccineOrderByDueDate(vaccine.getCode());
	}

	/**
	 * Lots with a positive balance for the vaccine, soonest expiry first (FEFO order) - the set a
	 * manual discharge or {@link #dispenseDose(PatientVaccine)} can draw from.
	 */
	public List<VaccineLot> getAvailableLots(Vaccine vaccine) {
		return lotRepository.findAvailableByVaccineOrderByDueDate(vaccine.getCode());
	}

	/**
	 * Movements for the vaccine within the given date range (either bound optional), most recent
	 * first.
	 */
	public List<VaccineStockMovement> getMovements(Vaccine vaccine, LocalDateTime dateFrom, LocalDateTime dateTo) {
		return movementRepository.findByVaccineAndDates(vaccine.getCode(), dateFrom, dateTo);
	}

	/**
	 * The vaccine's current total balance (sum of all its movements, across all lots).
	 */
	public int getQuantity(Vaccine vaccine) {
		Integer quantity = movementRepository.getQuantity(vaccine.getCode());
		return quantity == null ? 0 : quantity;
	}

	/**
	 * The lot's current balance (sum of its movements).
	 */
	public int getQuantity(VaccineLot lot) {
		Integer quantity = lotRepository.getQuantity(lot);
		return quantity == null ? 0 : quantity;
	}

	/**
	 * {@code true} if the vaccine has a configured {@link Vaccine#getMinQuantity() min quantity}
	 * threshold and its current balance is below it.
	 */
	public boolean isBelowMinQuantity(Vaccine vaccine) {
		Integer minQuantity = vaccine.getMinQuantity();
		return minQuantity != null && getQuantity(vaccine) < minQuantity;
	}

	private void validateVaccine(Vaccine vaccine) throws OHDataValidationException {
		if (vaccine == null) {
			throw new OHDataValidationException(new OHExceptionMessage(MessageBundle.getMessage("angal.vaccinestock.pleaseselectavaccine.msg")));
		}
	}

	private void validateLot(VaccineLot lot) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (lot == null || lot.getCode() == null || lot.getCode().isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.vaccinestock.pleaseinsertalotcode.msg")));
		} else if (lot.getPreparationDate() == null || lot.getDueDate() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.vaccinestock.pleaseinsertthelotdates.msg")));
		} else if (lot.getDueDate().isBefore(lot.getPreparationDate())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.vaccinestock.duedatemustbeafterthepreparationdate.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	private void validateQuantity(int quantity) throws OHDataValidationException {
		if (quantity <= 0) {
			throw new OHDataValidationException(new OHExceptionMessage(MessageBundle.getMessage("angal.vaccinestock.pleaseinsertavalidquantity.msg")));
		}
	}
}
