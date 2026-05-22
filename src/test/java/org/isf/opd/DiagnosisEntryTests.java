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
package org.isf.opd;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.isf.OHCoreTestCase;
import org.isf.disease.TestDisease;
import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperationRepository;
import org.isf.distype.TestDiseaseType;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.generaldata.GeneralData;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.model.DiagnosisEntry;
import org.isf.opd.model.Opd;
import org.isf.opd.service.OpdIoOperationRepository;
import org.isf.opd.service.OpdIoOperations;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;

class DiagnosisEntryTests extends OHCoreTestCase {

	private static TestOpd testOpd;
	private static TestPatient testPatient;
	private static TestDiseaseType testDiseaseType;
	private static TestDisease testDisease;
	private static TestWard testWard;
	private static TestVisit testVisit;

	@Autowired
	OpdIoOperations opdIoOperation;
	@Autowired
	OpdIoOperationRepository opdIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	@Autowired
	VisitsIoOperationRepository visitsIoOperationRepository;
	@Autowired
	OpdBrowserManager opdBrowserManager;
	@Autowired
	DiseaseTypeIoOperationRepository diseaseTypeIoOperationRepository;
	@Autowired
	DiseaseIoOperationRepository diseaseIoOperationRepository;
	@Autowired
	EntityManager entityManager;

	static Stream<Arguments> opdExtended() {
		return Stream.of(Arguments.of(false), Arguments.of(true));
	}

	@BeforeAll
	static void setUpClass() {
		testOpd = new TestOpd();
		testPatient = new TestPatient();
		testDisease = new TestDisease();
		testDiseaseType = new TestDiseaseType();
		testWard = new TestWard();
		testVisit = new TestVisit();
	}

	@BeforeEach
	void setUp() throws Exception {
		cleanH2InMemoryDb();
		setupDiseaseRecords();
	}

	private void setupDiseaseRecords() throws Exception {
		DiseaseType diseaseType = testDiseaseType.setup(false);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);

		Disease disease1 = testDisease.setup(diseaseType, false);
		disease1.setCode("1");
		disease1.setOpdInclude(true);
		diseaseIoOperationRepository.saveAndFlush(disease1);

		Disease disease2 = testDisease.setup(diseaseType, false);
		disease2.setCode("2");
		disease2.setOpdInclude(true);
		diseaseIoOperationRepository.saveAndFlush(disease2);

		Disease disease3 = testDisease.setup(diseaseType, false);
		disease3.setCode("3");
		disease3.setOpdInclude(true);
		diseaseIoOperationRepository.saveAndFlush(disease3);
	}

	private int setupTestOpd() throws Exception {
		Patient patient = testPatient.setup(false);
		Disease disease = diseaseIoOperationRepository.findOneByCode("1");
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, false, ward);

		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
		patientIoOperationRepository.saveAndFlush(patient);
		diseaseIoOperationRepository.saveAndFlush(disease);
		wardIoOperationRepository.saveAndFlush(ward);
		visitsIoOperationRepository.saveAndFlush(nextVisit);

		opdIoOperationRepository.saveAndFlush(opd);
		return opd.getCode();
	}

	@ParameterizedTest
	@MethodSource("opdExtended")
	void testDiagnosisEntryGetsSets(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd();
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();

		Disease disease = diseaseIoOperationRepository.findOneByCode("1");

		DiagnosisEntry diagnosisWithoutSet = new DiagnosisEntry(foundOpd, disease, 0, true);
		DiagnosisEntry savedDiagnosisWithoutSet = opdIoOperation.newDiagnosis(diagnosisWithoutSet);
		assertThat(savedDiagnosisWithoutSet.getId()).isPositive();
		assertThat(savedDiagnosisWithoutSet.getOrderNumber()).isEqualTo(0);
		assertThat(savedDiagnosisWithoutSet.isActive()).isTrue();

		DiagnosisEntry diagnosisWithSet = new DiagnosisEntry();
		diagnosisWithSet.setOpd(foundOpd);
		diagnosisWithSet.setDisease(disease);
		diagnosisWithSet.setOrderNumber(0);
		diagnosisWithSet.setActive(true);
		DiagnosisEntry savedDiagnosisWithSet = opdIoOperation.newDiagnosis(diagnosisWithSet);
		assertThat(savedDiagnosisWithSet.getId()).isPositive();
		assertThat(savedDiagnosisWithSet.getOrderNumber()).isEqualTo(0);
		assertThat(savedDiagnosisWithSet.isActive()).isTrue();
	}

	@ParameterizedTest
	@MethodSource("opdExtended")
	void testGetDiagnosesList(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd();
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();

		Disease disease = diseaseIoOperationRepository.findOneByCode("1");

		DiagnosisEntry diagnosis1 = new DiagnosisEntry(foundOpd, disease, 0, true);
		DiagnosisEntry diagnosis2 = new DiagnosisEntry(foundOpd, disease, 1, false);
		DiagnosisEntry diagnosis3 = new DiagnosisEntry(foundOpd, disease, 2, false);

		opdIoOperation.newDiagnosis(diagnosis1);
		opdIoOperation.newDiagnosis(diagnosis2);
		opdIoOperation.newDiagnosis(diagnosis3);

		List<DiagnosisEntry> activeDiagnoses = opdIoOperation.getDiagnosesList(foundOpd.getCode());
		assertThat(activeDiagnoses).hasSize(3);
		assertThat(activeDiagnoses.get(0).getOrderNumber()).isEqualTo(0);
	}

	@Test
	void testGetAllDiagnosesList() throws Exception {
		GeneralData.OPDEXTENDED = false;
		int code = setupTestOpd();
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();

		Disease disease = diseaseIoOperationRepository.findOneByCode("1");

		DiagnosisEntry diagnosis = new DiagnosisEntry(foundOpd, disease, 0, true);
		DiagnosisEntry saved = opdIoOperation.newDiagnosis(diagnosis);

		assertThat(saved.isActive()).isTrue();

		saved.setActive(false);
		opdIoOperation.newDiagnosis(saved);

		List<DiagnosisEntry> allDiagnoses = opdIoOperation.getAllDiagnosesList(foundOpd.getCode());
		assertThat(allDiagnoses).hasSize(1);
		assertThat(allDiagnoses.get(0).isActive()).isFalse();
	}

	@ParameterizedTest
	@MethodSource("opdExtended")
	void testGetPrimaryDiagnosis(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd();
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();

		Disease disease = diseaseIoOperationRepository.findOneByCode("1");

		DiagnosisEntry primary = new DiagnosisEntry(foundOpd, disease, 0, true);
		DiagnosisEntry secondary = new DiagnosisEntry(foundOpd, disease, 1, false);

		opdIoOperation.newDiagnosis(primary);
		opdIoOperation.newDiagnosis(secondary);

	}

	@Test
	void testNewDiagnosis() throws Exception {
		GeneralData.OPDEXTENDED = false;
		int code = setupTestOpd();
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();

		Disease disease = diseaseIoOperationRepository.findOneByCode("1");
		DiagnosisEntry diagnosis = new DiagnosisEntry(foundOpd, disease, 0, true);

		DiagnosisEntry savedDiagnosis = opdIoOperation.newDiagnosis(diagnosis);

		assertThat(savedDiagnosis.getId()).isPositive();
		assertThat(savedDiagnosis.getOpd().getCode()).isEqualTo(foundOpd.getCode());
		assertThat(savedDiagnosis.getDisease().getCode()).isEqualTo(disease.getCode());
		assertThat(savedDiagnosis.isActive()).isTrue();
	}

	@ParameterizedTest
	@MethodSource("opdExtended")
	void testUpdateDiagnoses(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd();
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();

		Disease disease = diseaseIoOperationRepository.findOneByCode("1");
		Disease disease2 = diseaseIoOperationRepository.findOneByCode("2");

		DiagnosisEntry diagnosis1 = new DiagnosisEntry(foundOpd, disease, 0, true);
		opdIoOperation.newDiagnosis(diagnosis1);

		List<DiagnosisEntry> initialDiagnoses = opdIoOperation.getDiagnosesList(foundOpd.getCode());
		assertThat(initialDiagnoses).hasSize(1);

		List<DiagnosisEntry> newDiagnoses = new ArrayList<>();
		newDiagnoses.add(new DiagnosisEntry(foundOpd, disease2, 0, true));
		newDiagnoses.add(new DiagnosisEntry(foundOpd, disease, 1, false));

		opdIoOperation.updateDiagnoses(foundOpd.getCode(), newDiagnoses);

		List<DiagnosisEntry> updatedDiagnoses = opdIoOperation.getDiagnosesList(foundOpd.getCode());
		assertThat(updatedDiagnoses).hasSize(2);
		assertThat(updatedDiagnoses.get(0).getDisease().getCode()).isEqualTo(disease2.getCode());
	}

	@ParameterizedTest
	@MethodSource("opdExtended")
	void testHasDiagnoses(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd();
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();

		assertThat(opdIoOperation.hasDiagnoses(foundOpd.getCode())).isFalse();

		Disease disease = diseaseIoOperationRepository.findOneByCode("1");
		DiagnosisEntry diagnosis = new DiagnosisEntry(foundOpd, disease, 0, true);
		opdIoOperation.newDiagnosis(diagnosis);

		assertThat(opdIoOperation.hasDiagnoses(foundOpd.getCode())).isTrue();
	}

	@ParameterizedTest
	@MethodSource("opdExtended")
	void testDeleteDiagnoses(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd();
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();

		Disease disease = diseaseIoOperationRepository.findOneByCode("1");

		DiagnosisEntry diagnosis1 = new DiagnosisEntry(foundOpd, disease, 0, true);
		DiagnosisEntry diagnosis2 = new DiagnosisEntry(foundOpd, disease, 1, false);

		opdIoOperation.newDiagnosis(diagnosis1);
		opdIoOperation.newDiagnosis(diagnosis2);

		assertThat(opdIoOperation.hasDiagnoses(foundOpd.getCode())).isTrue();

		opdIoOperation.deleteDiagnoses(foundOpd.getCode());

		List<DiagnosisEntry> activeDiagnoses = opdIoOperation.getDiagnosesList(foundOpd.getCode());
		assertThat(activeDiagnoses).isEmpty();
	}

	@ParameterizedTest
	@MethodSource("opdExtended")
	void testMgrGetDiagnosesByOpdId(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd();
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();

		Disease disease = diseaseIoOperationRepository.findOneByCode("1");

		DiagnosisEntry diagnosis = new DiagnosisEntry(foundOpd, disease, 0, true);
		opdIoOperation.newDiagnosis(diagnosis);

		List<DiagnosisEntry> diagnoses = opdBrowserManager.getDiagnosesByOpdId(foundOpd.getCode());

		assertThat(diagnoses).hasSize(1);
		assertThat(diagnoses.get(0).getDisease().getCode()).isEqualTo(disease.getCode());
	}

	@Test
	void testDiagnosisEntryEqualsAndHashCode() throws Exception {
		GeneralData.OPDEXTENDED = false;
		int code = setupTestOpd();
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();

		Disease disease = diseaseIoOperationRepository.findOneByCode("1");

		DiagnosisEntry diagnosis1 = new DiagnosisEntry(foundOpd, disease, 0, true);
		DiagnosisEntry diagnosis2 = new DiagnosisEntry(foundOpd, disease, 0, true);

		assertThat(diagnosis1).isNotEqualTo(diagnosis2);

		DiagnosisEntry savedDiagnosis = opdIoOperation.newDiagnosis(diagnosis1);

		assertThat(savedDiagnosis).isEqualTo(savedDiagnosis);
		assertThat(savedDiagnosis).isNotEqualTo(null);
		assertThat(savedDiagnosis).isNotEqualTo("someString");

		assertThat(savedDiagnosis.hashCode()).isPositive();
	}

	@Test
	void testDiagnosisEntryToString() throws Exception {
		GeneralData.OPDEXTENDED = false;
		int code = setupTestOpd();
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();

		Disease disease = diseaseIoOperationRepository.findOneByCode("1");
		DiagnosisEntry diagnosis = new DiagnosisEntry(foundOpd, disease, 0, true);

		String toString = diagnosis.toString();
		assertThat(toString).contains("DiagnosisEntry");
		assertThat(toString).contains("orderNumber=0");
		assertThat(toString).contains("primaryDiagnosis=true");
	}
}