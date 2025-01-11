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

import org.isf.OHCoreTestCase;
import org.isf.mortuary.model.Death;
import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.service.DeathReasonIoOperations;
import org.isf.mortuary.service.MortuaryIoOperations;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperations;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

public class MortuaryManagerTest extends OHCoreTestCase {

	private static TestMortuary testMortuary;
	private static TestDeathReason testDeathReason;
	private static TestWard testWard;

	@Autowired
	PatientIoOperations patientIoOperations;

	@Autowired
	WardIoOperations wardIoOperations;

	@Autowired
	MortuaryBrowserManager mortuaryBrowserManager;

	@Autowired
	MortuaryIoOperations mortuaryIoOperations;

	@Autowired
	DeathReasonIoOperations deathReasonIoOperations;

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
	void testMgrGetMortuariesWhereData() throws OHException, OHServiceException {
		DeathReason deathReason = testDeathReason.setup(true);
		deathReasonIoOperations.add(deathReason);

		Ward ward = testWard.setup(true);
		wardIoOperations.newWard(ward);

		Patient patient = new Patient("TestFirstName", "TestSecondName", LocalDate.of(1984, 8, 14), 31,
			"d1", 'F', "TestAddress", "TestCity", "testNextKin", "testTelephone", "TestMotherName",
			'A', "TestFatherName", 'A', "0-/+", 'Y', 'Y', "TestTaxCode",
			"divorced", "business"
		);
		patientIoOperations.updatePatient(patient);
		LocalDateTime fromDate = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2025, 3, 3, 0, 0, 0);
		int id = setupTestMortuary(patient, deathReason, ward,false);
		Death foundMortuary = mortuaryIoOperations.findById(id);
		assertThat(foundMortuary).isNotNull();
		List<Death> mortuaries = mortuaryBrowserManager.getMortuariesWhereData(null, null, fromDate, toDate, null, "I");
		assertThat(mortuaries).isNotNull();
		assertThat(mortuaries.size()).isEqualTo(1);
	}

	@Test
	void testMgrGetMortuariesWhereDataPageable() throws OHException, OHServiceException {

		DeathReason deathReason = testDeathReason.setup(true);
		deathReasonIoOperations.add(deathReason);

		Ward ward = testWard.setup(true);
		wardIoOperations.newWard(ward);

		Patient patient1 = new Patient("TestFirstName", "TestSecondName", LocalDate.of(1984, 8, 14), 31,
			"d1", 'F', "TestAddress", "TestCity", "testNextKin", "testTelephone", "TestMotherName",
			'A', "TestFatherName", 'A', "0-/+", 'Y', 'Y', "TestTaxCode",
			"divorced", "business"
		);
		Patient patient2 = new Patient("FirstName", "SecondName", LocalDate.of(2000, 8, 14), 25,
			"d1", 'M', "Address", "City", "NextKin", "Telephone", "MotherName",
			'A', "FatherName", 'A', "0-/+", 'N', 'N', "TaxCode",
			"divorced", "business"
		);
		patientIoOperations.updatePatient(patient1);
		patientIoOperations.updatePatient(patient2);

		Death mortuary1 = testMortuary.setup(patient1, deathReason, ward, false, 1);
		assertThat(mortuary1).isNotNull();
		mortuaryIoOperations.save(mortuary1);
		Death mortuary2 = testMortuary.setup(patient2, deathReason, ward,false, 2);
		assertThat(mortuary2).isNotNull();
		mortuaryIoOperations.save(mortuary2);

		LocalDateTime fromDate = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2025, 3, 3, 0, 0, 0);
		Page<Death> mortuariesPages1 = mortuaryBrowserManager.getMortuariesWhereDataPageable("Arrêt cardiaque", "", fromDate, toDate, 1, 0, 1);
		assertThat(mortuariesPages1).isNotNull();
		assertThat(mortuariesPages1.getSize()).isEqualTo(1);
		Page<Death> mortuariesPages2 = mortuaryBrowserManager.getMortuariesWhereDataPageable("", "", fromDate, toDate, 1, 1, 1);
		assertThat(mortuariesPages2).isNotNull();
		assertThat(mortuariesPages2.getSize()).isEqualTo(1);

		assertThat(mortuariesPages1.getContent().get(0).getPatient().getName()).isNotEqualTo(mortuariesPages2.getContent().get(0).getPatient().getName());
	}

	@Test
	void testMgrCountTotalMortuaries() throws OHServiceException, OHException {
		DeathReason deathReason = testDeathReason.setup(true);
		deathReasonIoOperations.add(deathReason);

		Ward ward = testWard.setup(true);
		wardIoOperations.newWard(ward);

		Patient patient = new Patient("TestFirstName", "TestSecondName", LocalDate.of(1984, 8, 14), 31,
			"d1", 'F', "TestAddress", "TestCity", "testNextKin", "testTelephone", "TestMotherName",
			'A', "TestFatherName", 'A', "0-/+", 'Y', 'Y', "TestTaxCode",
			"divorced", "business"
		);
		patientIoOperations.updatePatient(patient);

		LocalDateTime fromDate = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2025, 3, 3, 0, 0, 0);
		int id = setupTestMortuary(patient, deathReason, ward,false);
		long count = mortuaryBrowserManager.countTotalMortuaries(null, null, fromDate, toDate, null, "I");
		assertThat(count).isEqualTo(1);
	}

	private int setupTestMortuary(Patient patient, DeathReason deathReason, Ward ward, boolean usingSet) throws OHException, OHServiceException {
		Death mortuary = testMortuary.setup(patient, deathReason, ward, usingSet);
		mortuaryIoOperations.save(mortuary);
		return mortuary.getId();
	}
}