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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import org.isf.OHCoreTestCase;
import org.isf.mortuary.model.Death;
import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.service.DeathReasonRepository;
import org.isf.mortuary.service.MortuaryRepository;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class MortuaryManagerTest extends OHCoreTestCase {

	private static TestMortuary testMortuary;
	private static TestDeathReason testDeathReason;
	private static TestWard testWard;
	@Autowired
	PatientIoOperationRepository patientIoOperationsRepository;
	@Autowired
	MortuaryBrowserManager mortuaryBrowserManager;
	private final LocalDateTime date = LocalDateTime.of(2024, 1, 10, 0, 0, 0);
	@Autowired
	private MortuaryRepository repository;

	@Autowired
	private DeathReasonRepository deathReasonRepository;

	@Autowired
	private WardIoOperationRepository wardIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testMortuary = new TestMortuary();
		testDeathReason = new TestDeathReason();
		testWard = new TestWard();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testGetDeathByAdmissionOrDischargeDate() throws OHException {
		List<Death> savedDeaths = generateDeaths(10, true, true);

		LocalDateTime fromDate = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2025, 3, 3, 0, 0, 0);

		Page<Death> deaths = repository.findAllByAdmissionDateBetweenOrDischargeDateBetween(fromDate, toDate, fromDate, toDate, PageRequest.of(0, 3));

		assertThat(deaths.getContent().size()).isEqualTo(3);
	}

	@Test
	void testGetPatientNameContainsAndWardCodeContainsAndAdmissionDateBetweenAndDeathReasonCodeContains() throws OHException {
		List<Death> savedDeaths = generateDeaths(10, true, true);

		LocalDateTime fromDate = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2025, 3, 3, 0, 0, 0);

		Page<Death> deaths = repository.findAllByPatientNameContainsAndWardCodeContainsAndDischargeDateBetweenAndDeathReasonCodeContains("FirstName 0", "w",
			fromDate, toDate, "CARD001", PageRequest.of(0, 3));

		assertThat(deaths.getContent().size()).isEqualTo(1);
		assertThat(deaths.getTotalPages()).isEqualTo(1);
		assertThat(deaths.getTotalElements()).isEqualTo(1);
		assertThat(deaths.getSize()).isEqualTo(3);
	}

	@Test
	void testMgrGetMortuariesWhereData() throws OHException, OHServiceException {
		int totalElements = 10;
		List<Death> savedDeaths = generateDeaths(totalElements, true, true);

		LocalDateTime fromDate = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2025, 3, 3, 0, 0, 0);

		List<Death> mortuaries = mortuaryBrowserManager.getMortuariesWhereData("", "", fromDate, toDate, true, "");
		assertThat(mortuaries).isNotNull();
		assertThat(mortuaries.size()).isEqualTo(totalElements);
	}

	@Test
	void testMgrGetMortuariesWhereDataPageable() throws OHException, OHServiceException {
		int totalElements = 12;
		List<Death> savedDeaths = generateDeaths(totalElements, true, true);

		LocalDateTime fromDate = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2025, 3, 3, 0, 0, 0);

		Page<Death> mortuariesPages = mortuaryBrowserManager.getMortuariesWhereDataPageable("", "", fromDate, toDate, "", true, 0, 3);

		assertThat(mortuariesPages).isNotNull();
		assertThat(mortuariesPages.getTotalElements()).isEqualTo(totalElements);
		assertThat(mortuariesPages.getTotalPages()).isEqualTo(4);
		assertThat(mortuariesPages.getSize()).isEqualTo(3);
	}

	private List<Death> generateDeaths(int size, boolean sameWard, boolean sameDeathReason) throws OHException {
		Ward ward = testWard.setup(true);
		DeathReason deathReason = testDeathReason.setup(true);
		deathReason = deathReasonRepository.save(deathReason);

		String wardCode = "W";
		DeathReason finalDeathReason = deathReason;
		List<Death> deaths = IntStream.range(0, size).mapToObj(i -> {
			Ward deathWard;
			DeathReason deathReason1;

			Patient patient = new Patient("FirstName " + i, "SecondName " + i, LocalDate.of(1984, 8, 14), 20 + i,
				"d" + i, 'M', "Address " + 1, "City " + i, "NextKin " + 1, "Telephone " + i, "MotherName " + i,
				'A', "FatherName " + i, 'A', "0-/+", 'Y', 'Y', "TaxCode " + i,
				"divorced", "business"
			);
			Patient savePatient = patientIoOperationsRepository.saveAndFlush(patient);

			if (!sameWard) {
				Ward saveWard;
				if (i < 100) {
					saveWard = new Ward(wardCode + i, "Description " + i, "Telephone " + i, "Fax " + i, "Email " + i, i, i, i, true, false);
				} else {
					saveWard = new Ward("" + (i - 100), "Description " + i, "Telephone " + i, "Fax " + i, "Email " + i, i, i, i, true, false);
				}
				deathWard = wardIoOperationRepository.saveAndFlush(saveWard);
			} else {
				ward.setCode(wardCode);
				deathWard = wardIoOperationRepository.saveAndFlush(ward);
			}

			if (!sameDeathReason) {
				DeathReason deathReasonTest = new DeathReason("Code " + i, "Description" + i, false);
				deathReason1 = deathReasonRepository.saveAndFlush(deathReasonTest);
			} else {
				deathReason1 = finalDeathReason;
			}

			return new Death(
				"Test place" + i,
				savePatient,
				deathWard,
				date.plusDays(date.getDayOfMonth() + 2),
				date.plusDays(date.getDayOfMonth() + 2),
				date.plusDays(date.getDayOfMonth() + 2),
				date.plusDays(date.getDayOfMonth() + 2),
				deathReason1,
				"Declaring " + i,
				"Declaring phone number " + 1,
				"Declaring Nid " + i,
				"Family name " + i,
				"Family phone number" + i,
				"Family phone number" + i,
				"Locker number" + i
			);
		}).toList();

		return repository.saveAllAndFlush(deaths);
	}
}