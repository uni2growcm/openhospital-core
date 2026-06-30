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
package org.isf.stat2;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.admission.TestAdmission;
import org.isf.admission.model.Admission;
import org.isf.admission.service.AdmissionIoOperationRepository;
import org.isf.disease.TestDisease;
import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperationRepository;
import org.isf.distype.TestDiseaseType;
import org.isf.distype.model.DiseaseType;
import org.isf.exa.TestExam;
import org.isf.exa.model.Exam;
import org.isf.exa.service.ExamIoOperationRepository;
import org.isf.exatype.TestExamType;
import org.isf.exatype.model.ExamType;
import org.isf.lab.TestLaboratory;
import org.isf.lab.model.Laboratory;
import org.isf.lab.service.LabIoOperationRepository;
import org.isf.operation.TestOperation;
import org.isf.operation.model.Operation;
import org.isf.operation.service.OperationIoOperationRepository;
import org.isf.opetype.TestOperationType;
import org.isf.opetype.model.OperationType;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patvac.TestPatientVaccine;
import org.isf.patvac.model.PatientVaccine;
import org.isf.patvac.service.PatVacIoOperationRepository;
import org.isf.stat2.manager.StatsManager;
import org.isf.stat2.model.DiseaseStat;
import org.isf.stat2.model.ExamStat;
import org.isf.stat2.model.VaccineStat;
import org.isf.stat2.service.StatsIoOperations;
import org.isf.vaccine.TestVaccine;
import org.isf.vaccine.model.Vaccine;
import org.isf.vaccine.service.VaccineIoOperationRepository;
import org.isf.vactype.TestVaccineType;
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.service.VaccineTypeIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;


class StatsIoOperationsTest extends OHCoreTestCase {

	private static TestPatient testPatient;
	private static TestVaccine testVaccine;
	private static TestVaccineType testVaccineType;
	private static TestExam testExam;
	private static TestDisease testDisease;
	private static TestOperation testOperation;
	private static TestAdmission testAdmission;
	private static TestLaboratory testLaboratory;
	private static TestPatientVaccine testPatientVaccine;
	private static TestExamType testExamType;
	private static TestDiseaseType testDiseaseType;
	private static TestOperationType testOperationType;

	@Autowired
	private PatientIoOperationRepository patientIoOperationRepository;

	@Autowired
	private VaccineIoOperationRepository vaccineIoOperationRepository;

	@Autowired
	private VaccineTypeIoOperationRepository vaccineTypeIoOperationRepository;

	@Autowired
	private ExamIoOperationRepository examIoOperationRepository;

	@Autowired
	private DiseaseIoOperationRepository diseaseIoOperationRepository;

	@Autowired
	private OperationIoOperationRepository operationIoOperationRepository;

	@Autowired
	private AdmissionIoOperationRepository admissionIoOperationRepository;

	@Autowired
	private LabIoOperationRepository labIoOperationRepository;

	@Autowired
	private PatVacIoOperationRepository patVacIoOperationRepository;

	@Autowired
	private StatsIoOperations statsIoOperations;

	@Autowired
	private StatsManager statsManager;

	@BeforeAll
	static void setUpClass() {
		testPatient = new TestPatient();
		testVaccine = new TestVaccine();
		testVaccineType = new TestVaccineType();
		testExam = new TestExam();
		testDisease = new TestDisease();
		testOperation = new TestOperation();
		testAdmission = new TestAdmission();
		testLaboratory = new TestLaboratory();
		testPatientVaccine = new TestPatientVaccine();
		testExamType = new TestExamType();
		testDiseaseType = new TestDiseaseType();
		testOperationType = new TestOperationType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	private Patient setupTestPatient() throws Exception {
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient;
	}

	private Vaccine setupTestVaccine() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(true);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		Vaccine vaccine = testVaccine.setup(vaccineType, false);
		vaccineIoOperationRepository.saveAndFlush(vaccine);
		return vaccine;
	}

	private Exam setupTestExam() throws Exception {
		ExamType examType = testExamType.setup(true);
		Exam exam = testExam.setup(examType, 1, true);
		examIoOperationRepository.saveAndFlush(exam);
		return exam;
	}

	private Disease setupTestDisease() throws Exception {
		DiseaseType diseaseType = testDiseaseType.setup(true);
		Disease disease = testDisease.setup(diseaseType, true);
		diseaseIoOperationRepository.saveAndFlush(disease);
		return disease;
	}

	private Operation setupTestOperation() throws Exception {
		OperationType opType = testOperationType.setup(true);
		Operation operation = testOperation.setup(opType, true);
		operationIoOperationRepository.saveAndFlush(operation);
		return operation;
	}

	private Admission setupTestAdmission(Patient patient) throws Exception {
		Admission admission = testAdmission.setup(
			null,
			patient,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			false
		);
		admissionIoOperationRepository.saveAndFlush(admission);
		return admission;
	}

	private Laboratory setupTestLaboratory(Exam exam, Patient patient) throws Exception {
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		labIoOperationRepository.saveAndFlush(laboratory);
		return laboratory;
	}

	private PatientVaccine setupTestPatientVaccine(Patient patient, Vaccine vaccine) throws Exception {
		PatientVaccine patientVaccine = testPatientVaccine.setup(patient, vaccine, false);
		patVacIoOperationRepository.saveAndFlush(patientVaccine);
		return patientVaccine;
	}

	@Test
	void testIoGetPatientsStats_WithNoFilters() throws Exception {
		Patient patient1 = setupTestPatient();
		Patient patient2 = setupTestPatient();

		Page<Patient> result = statsIoOperations.getPatientsStats(
			0, 10,
			0, 0,
			"", "",
			"", "",
			"", "",
			"", "",
			"", "", "",
			"", "",
			"", "",
			"", ""
		);

		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isGreaterThanOrEqualTo(2);
		assertThat(result.getContent()).extracting(Patient::getCode)
			.contains(patient1.getCode(), patient2.getCode());
	}

	@Test
	void testIoGetPatientsStats_WithNoPatients() throws Exception {
		Page<Patient> result = statsIoOperations.getPatientsStats(
			0, 10,
			0, 0,
			"", "",
			"", "",
			"", "",
			"", "",
			"", "", "",
			"", "",
			"", "",
			"", ""
		);
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(0);
		assertThat(result.getContent()).isEmpty();
	}

	@Test
	void testIoGetPatientsStatsCount_WithFilters() throws Exception {
		Patient patient1 = setupTestPatient();
		patient1.setSex('M');
		patient1.setAge(25);
		patientIoOperationRepository.saveAndFlush(patient1);

		Patient patient2 = setupTestPatient();
		patient2.setSex('F');
		patient2.setAge(40);
		patientIoOperationRepository.saveAndFlush(patient2);

		long count = statsIoOperations.getPatientsStatsCount(
			0, 0,
			"", "",
			"", "",
			"", "",
			"", "",
			"", "", "",
			"", "",
			"", "",
			"", ""
		);
		assertThat(count).isGreaterThanOrEqualTo(2);
	}

	@Test
	void testIoGetVaccinesStats() throws Exception {
		Vaccine vaccine = setupTestVaccine();
		Patient patient = setupTestPatient();
		PatientVaccine patientVaccine = setupTestPatientVaccine(patient, vaccine);

		Page<VaccineStat> result = statsIoOperations.getVaccinesStats(
			0, 10,
			0, 0,
			"", "",
			"",
			"", "",
			"", "",
			"", "",
			"", "",
			"", "",
			"", ""
		);

		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isGreaterThanOrEqualTo(1);
		assertThat(result.getContent())
			.extracting(VaccineStat::getVaccine)
			.extracting(Vaccine::getDescription)
			.contains(vaccine.getDescription());
	}

	@Test
	void testIoGetVaccinesStats_WithNoVaccines() throws Exception {
		Page<VaccineStat> result = statsIoOperations.getVaccinesStats(
			0, 10,
			0, 0,
			"", "",
			"",
			"", "",
			"", "",
			"", "",
			"", "",
			"", "",
			"", ""
		);

		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(0);
		assertThat(result.getContent()).isEmpty();
	}

	@Test
	void testIoGetExamsStats_WithNoExams() throws Exception {
		Page<ExamStat> result = statsIoOperations.getExamsStats(
			0, 10,
			0, 0,
			"", "",
			"",
			"",
			"", "",
			"", "", "",
			"", "",
			"", "",
			"", ""
		);

		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(0);
		assertThat(result.getContent()).isEmpty();
	}

	@Test
	void testIoGetDiseasesStats_WithNoDiseases() throws Exception {
		Page<DiseaseStat> result = statsIoOperations.getDiseasesStats(
			0, 10,
			0, 0,
			"", "",
			"",
			"", "",
			"", "",
			"", "", "",
			"", "",
			"", "",
			""
		);

		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(0);
		assertThat(result.getContent()).isEmpty();
	}

	@Test
	void testMgrGetPatientsStats_ReturnsList() throws Exception {
		Patient patient = setupTestPatient();
		List<Patient> result = statsManager.getPatientsStats(
			0, 10,
			0, 0,
			"", "",
			"", "",
			"", "",
			"", "",
			"", "", "",
			"", "",
			"", "",
			"", ""
		);
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).extracting(Patient::getCode).contains(patient.getCode());
	}

	@Test
	void testMgrGetPatientsStatsCount_ReturnsInt() throws Exception {
		Patient patient1 = setupTestPatient();
		Patient patient2 = setupTestPatient();

		int count = statsManager.getPatientsStatsCount(
			0, 0,
			"", "",
			"", "",
			"", "",
			"", "",
			"", "", "",
			"", "",
			"", "",
			"", ""
		);

		assertThat(count).isGreaterThanOrEqualTo(2);
	}

	@Test
	void testMgrGetVaccinesStats_ReturnsList() throws Exception {
		Vaccine vaccine = setupTestVaccine();
		Patient patient = setupTestPatient();
		PatientVaccine patientVaccine = setupTestPatientVaccine(patient, vaccine);

		List<VaccineStat> result = statsManager.getVaccinesStats(
			0, 10,
			0, 0,
			"", "",
			"",
			"", "",
			"", "",
			"", "",
			"", "",
			"", "",
			"", ""
		);

		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).extracting(VaccineStat::getVaccine)
			.extracting(Vaccine::getDescription)
			.contains(vaccine.getDescription());
	}

	@Test
	void testPagination_FirstPage() throws Exception {
		for (int i = 0; i < 25; i++) {
			Patient patient = setupTestPatient();
			patient.setFirstName("Patient" + i);
			patientIoOperationRepository.saveAndFlush(patient);
		}

		Page<Patient> result = statsIoOperations.getPatientsStats(
			0, 10,
			0, 0,
			"", "",
			"", "",
			"", "",
			"", "",
			"", "", "",
			"", "",
			"", "",
			"", ""
		);
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(10);
		assertThat(result.getTotalElements()).isGreaterThanOrEqualTo(25);
	}

	@Test
	void testPagination_SecondPage() throws Exception {
		for (int i = 0; i < 25; i++) {
			Patient patient = setupTestPatient();
			patient.setFirstName("Patient" + i);
			patientIoOperationRepository.saveAndFlush(patient);
		}

		Page<Patient> result = statsIoOperations.getPatientsStats(
			10, 10,
			0, 0,
			"", "",
			"", "",
			"", "",
			"", "",
			"", "", "",
			"", "",
			"", "",
			"", ""
		);

		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(10);
		assertThat(result.getTotalElements()).isGreaterThanOrEqualTo(25);
	}
}