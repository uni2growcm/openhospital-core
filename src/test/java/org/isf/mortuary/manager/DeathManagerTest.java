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

package org.isf.mortuary.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import org.isf.OHCoreTestCase;
import org.isf.mortuary.model.BodyCompartment;
import org.isf.mortuary.model.Death;
import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.service.BodyCompartmentRepository;
import org.isf.mortuary.service.DeathReasonRepository;
import org.isf.mortuary.service.DeathIoOperations;
import org.isf.mortuary.service.DeathRepository;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

public class DeathManagerTest extends OHCoreTestCase {

	private static TestDeathReason testDeathReason;
	private static TestWard testWard;
	private final LocalDateTime date = LocalDateTime.of(2024, 1, 10, 0, 0, 0);

	@Autowired
	PatientIoOperationRepository patientIoOperationsRepository;

	@Autowired
	DeathManager deathManager;

	@Autowired
	DeathIoOperations deathIoOperations;

	@Autowired
	private DeathRepository deathRepository;

	@Autowired
	private DeathReasonRepository deathReasonRepository;

	@Autowired
	private WardIoOperationRepository wardIoOperationRepository;

	@Autowired
	private BodyCompartmentRepository bodyCompartmentRepository;

	@BeforeAll
	static void setUpClass() {
		testDeathReason = new TestDeathReason();
		testWard = new TestWard();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	@DisplayName("Should successfully add a death")
	void testAdd() throws OHException, OHServiceException {
		List<Death> deaths = generateDeaths(2);
		Death deathSaved = deathManager.add(deaths.get(0));
		assertThat(deaths.get(0).getPatient().getName()).isEqualTo(deathSaved.getPatient().getName());
		assertThat(deaths.get(0).getDeclaringName()).isEqualTo(deathSaved.getDeclaringName());
		assertThat(deaths.get(0).getDeathReason().getTitle()).isEqualTo(deathSaved.getDeathReason().getTitle());
	}

	@Test
	@DisplayName("Should catch an OHServiceException")
	void testAddFailed() throws OHException, OHServiceException {
		List<Death> deaths = generateDeaths(2);
		Death deathSaved = deathManager.add(deaths.get(0));

		Death death = deaths.get(1);
		death.setPatient(deathSaved.getPatient());
		assertThatThrownBy(() -> deathManager.add(death))
			.isInstanceOf(OHServiceException.class);

		death.setPatient(deaths.get(1).getPatient());
		death.setAdmissionDate(date.plusYears(date.getYear() + 3));
		assertThatThrownBy(() -> deathManager.add(death))
			.isInstanceOf(OHServiceException.class);

		death.setAdmissionDate(date);
		death.setDate(date.plusYears(date.getYear() + 3));
		assertThatThrownBy(() -> deathManager.add(death))
			.isInstanceOf(OHServiceException.class);

		death.setDate(date);
		death.setEstimatedDischargeDate(date.plusYears(date.getYear() + 3));
		assertThatThrownBy(() -> deathManager.add(death))
			.isInstanceOf(OHServiceException.class);
	}

	@Test
	@DisplayName("Should successfully update a death")
	void testUpdate() throws OHException, OHServiceException {
		List<Death> deaths = deathRepository.saveAllAndFlush(generateDeaths(2));
		Death deathSaved1 = deaths.get(0);
		deathSaved1.setDeclaringName("John Doe");
		Death deathUpdated = deathManager.update(deathSaved1);
		assertThat(deathSaved1.getPatient().getName()).isEqualTo(deathUpdated.getPatient().getName());
		assertThat(deathSaved1.getDeathReason().getTitle()).isEqualTo(deathUpdated.getDeathReason().getTitle());
		assertThat(deathUpdated.getDeclaringName()).isEqualTo("John Doe");
		deathSaved1.setPatient(deaths.get(1).getPatient());
		assertThatThrownBy(() -> deathManager.update(deathSaved1))
			.isInstanceOf(OHServiceException.class);
	}

	@Test
	@DisplayName("Should catch an OHServiceException")
	void testUpdateFailed() throws OHException, OHServiceException {
		List<Death> deaths = generateDeaths(2);
		Death deathSaved = deathManager.add(deaths.get(0));

		Death death = deaths.get(1);
		death.setPatient(deathSaved.getPatient());
		assertThatThrownBy(() -> deathManager.update(death))
			.isInstanceOf(OHServiceException.class);

		death.setPatient(deaths.get(1).getPatient());
		death.setAdmissionDate(date.plusYears(date.getYear() + 3));
		assertThatThrownBy(() -> deathManager.update(death))
			.isInstanceOf(OHServiceException.class);

		death.setAdmissionDate(date);
		death.setDate(date.plusYears(date.getYear() + 3));
		assertThatThrownBy(() -> deathManager.update(death))
			.isInstanceOf(OHServiceException.class);

		death.setDate(date);
		death.setEstimatedDischargeDate(date.plusYears(date.getYear() + 3));
		assertThatThrownBy(() -> deathManager.update(death))
			.isInstanceOf(OHServiceException.class);
	}

	@Test
	@DisplayName("Should successfully delete a death")
	void testDelete() throws OHException, OHServiceException {
		List<Death> deathsSaved = deathRepository.saveAllAndFlush(generateDeaths(1));
		Death death = deathIoOperations.findById(deathsSaved.get(0).getId());
		assertThat(death).isNotNull();
		System.out.println(death.getId());
		deathManager.delete(death);
		Death deathDeleted = deathIoOperations.findById(death.getId());
		assertThat(deathDeleted).isNull();
	}

	@Test
	@DisplayName("Should retrieve death pages filtered by patient name and admission or discharge date")
	void testGetByPatientNameAndDates() throws OHException, OHServiceException {
		List<Death> savedDeaths = deathRepository.saveAllAndFlush(generateDeaths(10));

		LocalDateTime fromDate = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2025, 3, 3, 0, 0, 0);

		Page<Death> deaths = deathManager.getByPatientNameAndDates("FirstName 0", fromDate, toDate, false, 0, 3);

		assertThat(deaths.getContent().size()).isEqualTo(1);
		assertThat(deaths.getTotalPages()).isEqualTo(1);
		assertThat(deaths.getTotalElements()).isEqualTo(1);
	}

	@Test
	@DisplayName("Should retrieve death pages filtered by patient name, ward code, admission or discharge date and death reason title")
	void testGetMortuariesPageable() throws OHServiceException, OHException {
		List<Death> savedDeaths = deathRepository.saveAllAndFlush(generateDeaths(10));

		LocalDateTime fromDate = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2025, 3, 3, 0, 0, 0);

		Ward ward = savedDeaths.get(0).getWard();
		DeathReason reason = savedDeaths.get(0).getDeathReason();

		Page<Death> deaths = deathManager.getMortuariesPageable(
			"FirstName 0",
			ward.getCode(),
			fromDate,
			toDate,
			reason.getTitle(),
			false,
			0,
			3
		);

		assertThat(deaths.getContent().size()).isEqualTo(1);
		assertThat(deaths.getTotalPages()).isEqualTo(1);
		assertThat(deaths.getTotalElements()).isEqualTo(1);
		assertThat(deaths.getSize()).isEqualTo(3);
	}

	private List<Death> generateDeaths(int size) throws OHException {

		Ward sharedWard;

		sharedWard = wardIoOperationRepository.saveAndFlush(testWard.setup(true));

		DeathReason sharedReason;

		sharedReason = deathReasonRepository.saveAndFlush(testDeathReason.setup(true));

		BodyCompartment bodyCompartment =
			bodyCompartmentRepository.saveAndFlush(
				new BodyCompartment("BC_" + System.nanoTime(),
					"Body Compartment 1",
					false));

		return IntStream.range(0, size).mapToObj(i -> {

			Patient patient = patientIoOperationsRepository.saveAndFlush(
				new Patient(
					"FirstName " + i,
					"SecondName " + i,
					LocalDate.of(1984, 8, 14),
					20 + i,
					"d" + i,
					'M',
					"Address",
					"City " + i,
					"NextKin",
					"Telephone " + i,
					"MotherName",
					'A',
					"FatherName",
					'A',
					"0-/+",
					'Y',
					'Y',
					"TaxCode " + i,
					"divorced",
					"business"
				)
			);

			Ward ward = sharedWard;

			DeathReason reason = sharedReason;

			return new Death(
				"Test place" + i,
				patient,
				ward,
				date,
				date,
				date,
				date,
				reason,
				"Declaring " + i,
				"Phone",
				"Nid",
				"Family",
				"Family phone",
				"Family phone",
				bodyCompartment,
				false
			);

		}).toList();
	}
}