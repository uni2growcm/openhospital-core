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
package org.isf.opd;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.disease.TestDisease;
import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperationRepository;
import org.isf.distype.TestDiseaseType;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.opd.manager.OpdDiseaseBrowserManager;
import org.isf.opd.model.Opd;
import org.isf.opd.model.OpdDisease;
import org.isf.opd.service.OpdDiseaseIoOperationRepository;
import org.isf.opd.service.OpdIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.visits.TestVisit;
import org.isf.visits.model.Visit;
import org.isf.visits.service.VisitsIoOperationRepository;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OpdDiseaseTests extends OHCoreTestCase {

	private static TestOpd testOpd;
	private static TestOpdDisease testOpdDisease;
	private static TestPatient testPatient;
	private static TestDiseaseType testDiseaseType;
	private static TestDisease testDisease;
	private static TestWard testWard;
	private static TestVisit testVisit;

	@Autowired
	OpdIoOperationRepository opdIoOperationRepository;
	@Autowired
	OpdDiseaseIoOperationRepository opdDiseaseIoOperationRepository;
	@Autowired
	OpdDiseaseBrowserManager opdDiseaseBrowserManager;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	DiseaseTypeIoOperationRepository diseaseTypeIoOperationRepository;
	@Autowired
	DiseaseIoOperationRepository diseaseIoOperationRepository;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	@Autowired
	VisitsIoOperationRepository visitsIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testOpd = new TestOpd();
		testOpdDisease = new TestOpdDisease();
		testPatient = new TestPatient();
		testDiseaseType = new TestDiseaseType();
		testDisease = new TestDisease();
		testWard = new TestWard();
		testVisit = new TestVisit();
	}

	@BeforeEach
	void setUp() throws Exception {
		cleanH2InMemoryDb();
	}

	private Opd setupTestOpd() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("199");
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, false, ward);

		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		wardIoOperationRepository.saveAndFlush(ward);
		visitsIoOperationRepository.saveAndFlush(nextVisit);
		opdIoOperationRepository.saveAndFlush(opd);
		return opd;
	}

	private Disease setupExtraDisease(String code) throws Exception {
		DiseaseType diseaseType = testDiseaseType.setup(false);
		diseaseType.setCode("EX" + code);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode(code);
		diseaseIoOperationRepository.saveAndFlush(disease);
		return disease;
	}

	@Test
	void testFindAllByOpdCodeReturnsInInsertionOrder() throws Exception {
		Opd opd = setupTestOpd();
		Disease diseaseA = setupExtraDisease("A1");
		Disease diseaseB = setupExtraDisease("A2");

		OpdDisease first = testOpdDisease.setup(opd, diseaseA, false);
		OpdDisease second = testOpdDisease.setup(opd, diseaseB, false);
		opdDiseaseIoOperationRepository.saveAndFlush(first);
		opdDiseaseIoOperationRepository.saveAndFlush(second);

		List<OpdDisease> found = opdDiseaseIoOperationRepository.findAllByOpdCode(opd.getCode());

		assertThat(found).hasSize(2);
		assertThat(found.get(0).getDisease().getCode()).isEqualTo(diseaseA.getCode());
		assertThat(found.get(1).getDisease().getCode()).isEqualTo(diseaseB.getCode());
	}

	@Test
	void testReplaceOpdDiseasesDeletesOldAndInsertsNew() throws Exception {
		Opd opd = setupTestOpd();
		Disease diseaseA = setupExtraDisease("B1");
		Disease diseaseB = setupExtraDisease("B2");
		Disease diseaseC = setupExtraDisease("B3");

		opdDiseaseBrowserManager.replaceOpdDiseases(opd, List.of(diseaseA, diseaseB));
		assertThat(opdDiseaseBrowserManager.getOpdDiseases(opd)).hasSize(2);

		opdDiseaseBrowserManager.replaceOpdDiseases(opd, List.of(diseaseC));

		List<Disease> found = opdDiseaseBrowserManager.getOpdDiseases(opd);
		assertThat(found).hasSize(1);
		assertThat(found.get(0).getCode()).isEqualTo(diseaseC.getCode());
	}

	@Test
	void testReplaceOpdDiseasesSupportsMoreThanThree() throws Exception {
		Opd opd = setupTestOpd();
		Disease d1 = setupExtraDisease("C1");
		Disease d2 = setupExtraDisease("C2");
		Disease d3 = setupExtraDisease("C3");
		Disease d4 = setupExtraDisease("C4");

		opdDiseaseBrowserManager.replaceOpdDiseases(opd, List.of(d1, d2, d3, d4));

		assertThat(opdDiseaseBrowserManager.getOpdDiseases(opd)).hasSize(4);
	}

	@Test
	void testReplaceOpdDiseasesWithEmptyListClearsAll() throws Exception {
		Opd opd = setupTestOpd();
		Disease diseaseA = setupExtraDisease("D1");

		opdDiseaseBrowserManager.replaceOpdDiseases(opd, List.of(diseaseA));
		assertThat(opdDiseaseBrowserManager.getOpdDiseases(opd)).hasSize(1);

		opdDiseaseBrowserManager.replaceOpdDiseases(opd, List.of());

		assertThat(opdDiseaseBrowserManager.getOpdDiseases(opd)).isEmpty();
		assertThat(opdDiseaseIoOperationRepository.findAllByOpdCode(opd.getCode())).isEmpty();
	}
}
