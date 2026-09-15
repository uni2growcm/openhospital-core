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
package org.isf.vaccinestock.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.isf.OHCoreTestCase;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patvac.model.PatientVaccine;
import org.isf.patvac.service.PatVacIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vaccine.TestVaccine;
import org.isf.vaccine.manager.VaccineBrowserManager;
import org.isf.vaccine.model.Vaccine;
import org.isf.vaccinestock.model.VaccineLot;
import org.isf.vaccinestock.model.VaccineStockMovement;
import org.isf.vaccinestock.model.VaccineStockMovementReason;
import org.isf.vactype.TestVaccineType;
import org.isf.vactype.manager.VaccineTypeBrowserManager;
import org.isf.vactype.model.VaccineType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class VaccineStockManagerTest extends OHCoreTestCase {

	@Autowired
	private VaccineStockManager vaccineStockManager;

	@Autowired
	private VaccineBrowserManager vaccineBrowserManager;

	@Autowired
	private VaccineTypeBrowserManager vaccineTypeBrowserManager;

	@Autowired
	private PatientIoOperationRepository patientIoOperationRepository;

	@Autowired
	private PatVacIoOperationRepository patVacIoOperationRepository;

	private Vaccine vaccine;

	@BeforeEach
	void setUp() throws OHException, OHServiceException {
		cleanH2InMemoryDb();
		VaccineType vaccineType = vaccineTypeBrowserManager.newVaccineType(new TestVaccineType().setup(false));
		vaccine = vaccineBrowserManager.newVaccine(new TestVaccine().setup(vaccineType, false));
	}

	private PatientVaccine savePatientVaccine(LocalDateTime date) throws OHException {
		Patient patient = patientIoOperationRepository.saveAndFlush(new TestPatient().setup(false));
		return patVacIoOperationRepository.save(new PatientVaccine(0, 1, date, patient, vaccine, 0));
	}

	@Test
	@DisplayName("Charge a brand-new lot")
	void testNewChargeCreatesLot() throws OHServiceException {
		LocalDateTime now = LocalDateTime.now();
		VaccineLot newLot = new VaccineLot(vaccine, "LOT1", now, now.plusMonths(6));

		VaccineStockMovement movement = vaccineStockManager.newCharge(vaccine, newLot, 10, now, "first supply");

		assertThat(movement.getQuantity()).isEqualTo(10);
		assertThat(movement.getReason()).isEqualTo(VaccineStockMovementReason.SUPPLY.name());
		assertThat(movement.getLot().getCode()).isEqualTo("LOT1");
		assertThat(vaccineStockManager.getQuantity(vaccine)).isEqualTo(10);
		assertThat(vaccineStockManager.getLots(vaccine)).hasSize(1);
	}

	@Test
	@DisplayName("Charging into an existing lot ignores the dates passed in and keeps the persisted ones")
	void testNewChargeReusesExistingLot() throws OHServiceException {
		LocalDateTime now = LocalDateTime.now();
		VaccineLot newLot = new VaccineLot(vaccine, "LOT1", now, now.plusMonths(6));
		vaccineStockManager.newCharge(vaccine, newLot, 10, now, null);
		LocalDateTime originalDueDate = vaccineStockManager.getLots(vaccine).get(0).getDueDate();

		VaccineLot conflictingLot = new VaccineLot(vaccine, "LOT1", now.plusDays(1), now.plusYears(2));
		vaccineStockManager.newCharge(vaccine, conflictingLot, 5, now, null);

		assertThat(vaccineStockManager.getQuantity(vaccine)).isEqualTo(15);
		assertThat(vaccineStockManager.getLots(vaccine)).hasSize(1);
		assertThat(vaccineStockManager.getLots(vaccine).get(0).getDueDate()).isEqualTo(originalDueDate);
	}

	@Test
	@DisplayName("Charging with no vaccine is rejected")
	void testNewChargeRequiresVaccine() {
		LocalDateTime now = LocalDateTime.now();
		VaccineLot lot = new VaccineLot(vaccine, "LOT1", now, now.plusMonths(6));

		assertThatThrownBy(() -> vaccineStockManager.newCharge(null, lot, 10, now, null))
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	@DisplayName("Charging a non-positive quantity is rejected")
	void testNewChargeRequiresPositiveQuantity() {
		LocalDateTime now = LocalDateTime.now();
		VaccineLot lot = new VaccineLot(vaccine, "LOT1", now, now.plusMonths(6));

		assertThatThrownBy(() -> vaccineStockManager.newCharge(vaccine, lot, 0, now, null))
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	@DisplayName("Charging a lot without a due date is rejected")
	void testNewChargeRequiresLotDates() {
		LocalDateTime now = LocalDateTime.now();
		VaccineLot lot = new VaccineLot("LOT1");
		lot.setVaccine(vaccine);
		lot.setPreparationDate(now);
		// due date intentionally left unset

		assertThatThrownBy(() -> vaccineStockManager.newCharge(vaccine, lot, 10, now, null))
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	@DisplayName("Charging a lot whose due date precedes its preparation date is rejected")
	void testNewChargeRequiresDueDateAfterPreparationDate() {
		LocalDateTime now = LocalDateTime.now();
		VaccineLot lot = new VaccineLot(vaccine, "LOT1", now, now.minusDays(1));

		assertThatThrownBy(() -> vaccineStockManager.newCharge(vaccine, lot, 10, now, null))
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	@DisplayName("Manual discharge decrements the lot balance")
	void testNewManualDischarge() throws OHServiceException {
		LocalDateTime now = LocalDateTime.now();
		VaccineLot lot = new VaccineLot(vaccine, "LOT1", now, now.plusMonths(6));
		vaccineStockManager.newCharge(vaccine, lot, 10, now, null);

		VaccineStockMovement movement = vaccineStockManager.newManualDischarge(vaccine, lot, 4, VaccineStockMovementReason.MANUAL_DISCHARGE, now, "broken vials");

		assertThat(movement.getQuantity()).isEqualTo(-4);
		assertThat(movement.getReason()).isEqualTo(VaccineStockMovementReason.MANUAL_DISCHARGE.name());
		assertThat(vaccineStockManager.getQuantity(vaccine)).isEqualTo(6);
	}

	@Test
	@DisplayName("Manual discharge is blocked when it would exceed the lot balance")
	void testNewManualDischargeBlocksWhenInsufficientStock() throws OHServiceException {
		LocalDateTime now = LocalDateTime.now();
		VaccineLot lot = new VaccineLot(vaccine, "LOT1", now, now.plusMonths(6));
		vaccineStockManager.newCharge(vaccine, lot, 5, now, null);

		assertThatThrownBy(() -> vaccineStockManager.newManualDischarge(vaccine, lot, 10, VaccineStockMovementReason.MANUAL_DISCHARGE, now, null))
				.isInstanceOf(OHDataValidationException.class);
		assertThat(vaccineStockManager.getQuantity(vaccine)).isEqualTo(5);
	}

	@Test
	@DisplayName("Manual discharge rejects reasons reserved for automatic administration")
	void testNewManualDischargeRejectsAdministrationReasons() throws OHServiceException {
		LocalDateTime now = LocalDateTime.now();
		VaccineLot lot = new VaccineLot(vaccine, "LOT1", now, now.plusMonths(6));
		vaccineStockManager.newCharge(vaccine, lot, 5, now, null);

		assertThatThrownBy(() -> vaccineStockManager.newManualDischarge(vaccine, lot, 1, VaccineStockMovementReason.ADMINISTRATION, now, null))
				.isInstanceOf(OHDataValidationException.class);
		assertThatThrownBy(
				() -> vaccineStockManager.newManualDischarge(vaccine, lot, 1, VaccineStockMovementReason.ADMINISTRATION_CANCELLED, now, null))
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	@DisplayName("Dispensing a dose selects the lot with the soonest expiry (FEFO)")
	void testDispenseDoseUsesFefoOrder() throws OHServiceException, OHException {
		LocalDateTime now = LocalDateTime.now();
		VaccineLot laterLot = new VaccineLot(vaccine, "LOT-LATER", now, now.plusMonths(12));
		VaccineLot soonerLot = new VaccineLot(vaccine, "LOT-SOONER", now, now.plusMonths(1));
		vaccineStockManager.newCharge(vaccine, laterLot, 10, now, null);
		vaccineStockManager.newCharge(vaccine, soonerLot, 10, now, null);

		PatientVaccine patientVaccine = savePatientVaccine(now);
		VaccineStockMovement movement = vaccineStockManager.dispenseDose(patientVaccine);

		assertThat(movement).isNotNull();
		assertThat(movement.getLot().getCode()).isEqualTo("LOT-SOONER");
		assertThat(movement.getQuantity()).isEqualTo(-1);
		assertThat(movement.getReason()).isEqualTo(VaccineStockMovementReason.ADMINISTRATION.name());
		assertThat(movement.getPatientVaccine().getCode()).isEqualTo(patientVaccine.getCode());
		assertThat(vaccineStockManager.getQuantity(vaccine)).isEqualTo(19);
	}

	@Test
	@DisplayName("Dispensing a dose with no stock available is blocked")
	void testDispenseDoseWithNoStockThrows() throws OHException {
		PatientVaccine patientVaccine = savePatientVaccine(LocalDateTime.now());

		assertThatThrownBy(() -> vaccineStockManager.dispenseDose(patientVaccine))
				.isInstanceOf(OHDataValidationException.class);
		assertThat(vaccineStockManager.getQuantity(vaccine)).isZero();
	}

	@Test
	@DisplayName("Cancelling an administration reverses the decrement without deleting the original movement")
	void testCancelAdministrationReversesTheDecrement() throws OHServiceException, OHException {
		LocalDateTime now = LocalDateTime.now();
		VaccineLot lot = new VaccineLot(vaccine, "LOT1", now, now.plusMonths(6));
		vaccineStockManager.newCharge(vaccine, lot, 10, now, null);
		PatientVaccine patientVaccine = savePatientVaccine(now);
		vaccineStockManager.dispenseDose(patientVaccine);
		assertThat(vaccineStockManager.getQuantity(vaccine)).isEqualTo(9);

		VaccineStockMovement reversal = vaccineStockManager.cancelAdministration(patientVaccine);

		assertThat(reversal).isNotNull();
		assertThat(reversal.getQuantity()).isEqualTo(1);
		assertThat(reversal.getReason()).isEqualTo(VaccineStockMovementReason.ADMINISTRATION_CANCELLED.name());
		assertThat(reversal.getLot().getCode()).isEqualTo("LOT1");
		assertThat(vaccineStockManager.getQuantity(vaccine)).isEqualTo(10);
		assertThat(vaccineStockManager.getMovements(vaccine, null, null)).hasSize(3); // charge + administration + reversal
	}

	@Test
	@DisplayName("Cancelling an administration that never decremented stock is a no-op")
	void testCancelAdministrationWithNothingToReverse() throws OHServiceException, OHException {
		PatientVaccine patientVaccine = savePatientVaccine(LocalDateTime.now());
		// No stock was ever charged, so dispenseDose (not called here) would have found nothing to decrement.

		VaccineStockMovement reversal = vaccineStockManager.cancelAdministration(patientVaccine);

		assertThat(reversal).isNull();
	}

	@Test
	@DisplayName("A vaccine is below its minimum quantity once the balance drops under the configured threshold")
	void testIsBelowMinQuantity() throws OHServiceException {
		vaccine.setMinQuantity(5);
		LocalDateTime now = LocalDateTime.now();
		VaccineLot lot = new VaccineLot(vaccine, "LOT1", now, now.plusMonths(6));
		vaccineStockManager.newCharge(vaccine, lot, 3, now, null);

		assertThat(vaccineStockManager.isBelowMinQuantity(vaccine)).isTrue();

		vaccineStockManager.newCharge(vaccine, lot, 10, now, null);

		assertThat(vaccineStockManager.isBelowMinQuantity(vaccine)).isFalse();
	}

	@Test
	@DisplayName("A vaccine with no configured minimum quantity is never reported as below threshold")
	void testIsBelowMinQuantityWithNoThresholdConfigured() throws OHServiceException {
		LocalDateTime now = LocalDateTime.now();
		VaccineLot lot = new VaccineLot(vaccine, "LOT1", now, now.plusMonths(6));
		vaccineStockManager.newCharge(vaccine, lot, 1, now, null);

		assertThat(vaccineStockManager.isBelowMinQuantity(vaccine)).isFalse();
	}
}
