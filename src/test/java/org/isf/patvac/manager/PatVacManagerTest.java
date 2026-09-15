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
package org.isf.patvac.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.isf.OHCoreTestCase;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patvac.TestPatientVaccine;
import org.isf.patvac.model.PatientVaccine;
import org.isf.patvac.service.PatVacIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vaccine.TestVaccine;
import org.isf.vaccine.manager.VaccineBrowserManager;
import org.isf.vaccine.model.Vaccine;
import org.isf.vaccinestock.manager.VaccineStockManager;
import org.isf.vaccinestock.model.VaccineLot;
import org.isf.vaccinestock.model.VaccineStockMovementReason;
import org.isf.vactype.TestVaccineType;
import org.isf.vactype.manager.VaccineTypeBrowserManager;
import org.isf.vactype.model.VaccineType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the vaccine stock decrement/reversal that {@link PatVacManager} triggers around
 * {@link PatVacManager#newPatientVaccine(PatientVaccine)} and
 * {@link PatVacManager#deletePatientVaccine(PatientVaccine)}.
 */
class PatVacManagerTest extends OHCoreTestCase {

	@Autowired
	private PatVacManager patVacManager;

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
	private Patient patient;

	@BeforeEach
	void setUp() throws OHException, OHServiceException {
		cleanH2InMemoryDb();
		VaccineType vaccineType = vaccineTypeBrowserManager.newVaccineType(new TestVaccineType().setup(false));
		vaccine = vaccineBrowserManager.newVaccine(new TestVaccine().setup(vaccineType, false));
		patient = patientIoOperationRepository.saveAndFlush(new TestPatient().setup(false));
	}

	private void chargeStock(int quantity) throws OHServiceException {
		LocalDateTime now = LocalDateTime.now();
		VaccineLot lot = new VaccineLot(vaccine, "LOT1", now, now.plusMonths(6));
		vaccineStockManager.newCharge(vaccine, lot, quantity, now, null);
	}

	private PatientVaccine newPatientVaccine() throws OHException {
		return new TestPatientVaccine().setup(patient, vaccine, false);
	}

	@Test
	@DisplayName("Recording a vaccination decrements the vaccine stock by one dose")
	void testNewPatientVaccineDecrementsStock() throws OHServiceException, OHException {
		chargeStock(10);

		PatientVaccine inserted = patVacManager.newPatientVaccine(newPatientVaccine());

		assertThat(vaccineStockManager.getQuantity(vaccine)).isEqualTo(9);
		assertThat(vaccineStockManager.getMovements(vaccine, null, null)).anySatisfy(movement -> {
			assertThat(movement.getReason()).isEqualTo(VaccineStockMovementReason.ADMINISTRATION.name());
			assertThat(movement.getQuantity()).isEqualTo(-1);
			assertThat(movement.getPatientVaccine().getCode()).isEqualTo(inserted.getCode());
		});
	}

	@Test
	@DisplayName("Recording a vaccination with no stock available is blocked and nothing is saved")
	void testNewPatientVaccineWithNoStockThrowsAndDoesNotSave() throws OHException {
		// No charge: the vaccine has zero stock. newPatientVaccine is @Transactional so the insert
		// below is rolled back together with the failed stock decrement - not directly observable
		// here via a row count, since this test method itself runs inside OHCoreTestCase's own
		// ambient transaction (uncommitted own-writes stay visible until that outer transaction
		// ends), but it is a real, separate top-level transaction in the running application.
		assertThatThrownBy(() -> patVacManager.newPatientVaccine(newPatientVaccine()))
				.isInstanceOf(OHDataValidationException.class);

		assertThat(vaccineStockManager.getQuantity(vaccine)).isZero();
		assertThat(vaccineStockManager.getMovements(vaccine, null, null)).isEmpty();
	}

	@Test
	@DisplayName("Updating a vaccination does not decrement the stock again")
	void testUpdatePatientVaccineDoesNotDecrementStockAgain() throws OHServiceException, OHException {
		chargeStock(10);
		PatientVaccine inserted = patVacManager.newPatientVaccine(newPatientVaccine());
		assertThat(vaccineStockManager.getQuantity(vaccine)).isEqualTo(9);

		inserted.setProgr(inserted.getProgr() + 1);
		patVacManager.updatePatientVaccine(inserted);

		assertThat(vaccineStockManager.getQuantity(vaccine)).isEqualTo(9);
		assertThat(vaccineStockManager.getMovements(vaccine, null, null)).hasSize(2); // charge + administration
	}

	@Test
	@DisplayName("Deleting a vaccination reverses the stock decrement")
	void testDeletePatientVaccineReversesStockDecrement() throws OHServiceException, OHException {
		chargeStock(10);
		PatientVaccine inserted = patVacManager.newPatientVaccine(newPatientVaccine());
		assertThat(vaccineStockManager.getQuantity(vaccine)).isEqualTo(9);

		patVacManager.deletePatientVaccine(inserted);

		assertThat(vaccineStockManager.getQuantity(vaccine)).isEqualTo(10);
		assertThat(patVacManager.getPatientVaccine(inserted.getCode())).isEmpty();
	}

	@Test
	@DisplayName("Deleting a vaccination that never decremented stock does not fail")
	void testDeletePatientVaccineWithNoStockMovementDoesNotFail() throws OHServiceException, OHException {
		// Persisted directly through the repository (bypassing PatVacManager, which now blocks an
		// insert with no stock) to simulate a legacy record saved before stock tracking existed.
		PatientVaccine inserted = patVacIoOperationRepository.save(newPatientVaccine());

		patVacManager.deletePatientVaccine(inserted);

		assertThat(patVacManager.getPatientVaccine(inserted.getCode())).isEmpty();
		assertThat(vaccineStockManager.getQuantity(vaccine)).isZero();
	}
}
