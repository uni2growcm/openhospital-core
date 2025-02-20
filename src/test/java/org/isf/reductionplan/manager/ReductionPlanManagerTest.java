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

package org.isf.reductionplan.manager;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.isf.OHCoreTestCase;
import org.isf.exa.TestExam;
import org.isf.exa.model.Exam;
import org.isf.exa.service.ExamIoOperationRepository;
import org.isf.exatype.TestExamType;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.operation.TestOperation;
import org.isf.operation.model.Operation;
import org.isf.operation.service.OperationIoOperationRepository;
import org.isf.opetype.TestOperationType;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperationRepository;
import org.isf.pricesothers.TestPricesOthers;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.pricesothers.service.PriceOthersIoOperationRepository;
import org.isf.reductionplan.model.ExamReduction;
import org.isf.reductionplan.model.MedicalReduction;
import org.isf.reductionplan.model.OperationReduction;
import org.isf.reductionplan.model.PriceOtherReduction;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.reductionplan.service.ExamReductionIoOperationsRepository;
import org.isf.reductionplan.service.MedicalReductionIoOperationRepository;
import org.isf.reductionplan.service.OperationReductionIoOperationRepository;
import org.isf.reductionplan.service.PriceOtherReductionIoOperationRepository;
import org.isf.reductionplan.service.ReductionPlanIoOperations;
import org.isf.reductionplan.service.ReductionplanIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReductionPlanManagerTest extends OHCoreTestCase {

	@Autowired
	ReductionplanIoOperationRepository repository;

	@Autowired
	ReductionPlanManager manager;

	@Autowired
	ExamReductionIoOperationsRepository ExamReductionRepository;

	@Autowired
	ExamIoOperationRepository ExamRepository;

	@Autowired
	ExamTypeIoOperationRepository ExamTypeRepository;

	@Autowired
	MedicalsIoOperationRepository MedicalsRepository;

	@Autowired
	MedicalTypeIoOperationRepository MedicalsTypeRepository;

	@Autowired
	OperationIoOperationRepository OperationRepository;

	@Autowired
	OperationTypeIoOperationRepository OperationTypeRepository;

	@Autowired
	PriceOthersIoOperationRepository PriceOthersRepository;

	@Autowired
	MedicalReductionIoOperationRepository MedicalReductionRepository;

	@Autowired
	OperationReductionIoOperationRepository OperationReductionRepository;

	@Autowired
	PriceOtherReductionIoOperationRepository PricesOtherReductionRepository;

	@Autowired
	ReductionPlanIoOperations reductionPlanIoOperations;

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	@DisplayName("Should get all reduction plans")
	void testGetAll() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(2, null);
		repository.saveAllAndFlush(reductionPlans);

		List<ReductionPlan> existingReductionPlan = manager.getAll();
		assertThat(existingReductionPlan.size()).isEqualTo(reductionPlans.size());
	}

	@Test
	@DisplayName("Should get all reduction plans by description")
	void testGetByDescription() throws Exception {
		String description = "Fixed Description";
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(2, description);
		repository.saveAllAndFlush(reductionPlans);

		List<ReductionPlan> existingReductionPlans = manager.getByDescription(description, false);

		assertThat(existingReductionPlans).isNotNull();
		assertThat(existingReductionPlans.size()).isEqualTo(2);
		existingReductionPlans.forEach(plan ->
			assertThat(plan.getDescription()).isEqualTo(description));
	}

	@Test
	@DisplayName("Should save reduction plan")
	void testSave() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		ReductionPlan reductionPlan = reductionPlans.get(0);
		manager.add(reductionPlan);
		ReductionPlan saveReductionPlan = manager.getByDescription(reductionPlans.get(0).getDescription(), false).get(0);

		assertThat(saveReductionPlan).isNotNull();
		assertThat(saveReductionPlan.getId()).isGreaterThan(0);
		assertThat(saveReductionPlan.getDescription()).isEqualTo("Description 0");
		assertThat(saveReductionPlan.getExamRate()).isEqualTo(3.0);
		assertThat(saveReductionPlan.getOperationRate()).isEqualTo(1.0);
		assertThat(saveReductionPlan.getMedicalRate()).isEqualTo(2.0);
		assertThat(saveReductionPlan.getOtherRate()).isEqualTo(3.0);
		assertThat(saveReductionPlan.getExamReductions().get(0).getId()).isEqualTo(1);
		assertThat(saveReductionPlan.getMedicalReductions().get(0).getId()).isEqualTo(1);
		assertThat(saveReductionPlan.getOperationReductions().get(0).getId()).isEqualTo(1);
		assertThat(saveReductionPlan.getPriceOtherReductions().get(0).getId()).isEqualTo(1);
	}

	@Test
	@DisplayName("Should update reduction plan")
	void testUpdate() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = manager.getByDescription(reductionPlans.get(0).getDescription(), false).get(0);
		ReductionPlan existingReductionPlan = manager.getByDescription(reductionPlan.getDescription(), false).get(0);
		existingReductionPlan.setDescription("update");
		existingReductionPlan.setOperationRate(0.0);
		existingReductionPlan.setMedicalRate(0.0);
		existingReductionPlan.setExamRate(0.0);
		existingReductionPlan.setOtherRate(0.0);
		manager.update(existingReductionPlan);

		ReductionPlan updateReductionPlan = manager.getByDescription(reductionPlan.getDescription(), false).get(0);
		assertThat(updateReductionPlan).isNotNull();
		assertThat(updateReductionPlan.getId()).isEqualTo(existingReductionPlan.getId());
		assertThat(updateReductionPlan.getDescription()).isEqualTo("update");
		assertThat(updateReductionPlan.getOperationRate()).isEqualTo(0.0);
		assertThat(updateReductionPlan.getMedicalRate()).isEqualTo(0.0);
		assertThat(updateReductionPlan.getExamRate()).isEqualTo(0.0);
		assertThat(updateReductionPlan.getOtherRate()).isEqualTo(0.0);
	}

	@Test
	@DisplayName("Should delete reduction plan")
	void testDelete() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(2, null);
		repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = manager.getByDescription(reductionPlans.get(0).getDescription(), false).get(0);
		int id = reductionPlan.getId();
		assertThat(manager.getAll()).hasSize(2);
		ReductionPlan existingReductionPlan = manager.getByDescription(reductionPlan.getDescription(), false).get(0);

		ReductionPlan deletedReductionPlan = manager.delete(existingReductionPlan);
		assertThat(deletedReductionPlan.isDeleted()).isTrue();
		assertThat(manager.getAll()).hasSize(1);
		assertThat(manager.getExamReductionByReductionPlanId(id).size()).isEqualTo(0);
		assertThat(manager.getOperationReductionByReductionPlanId(id).size()).isEqualTo(0);
		assertThat(manager.getMedicalReductionByReductionPlanId(id).size()).isEqualTo(0);
		assertThat(manager.getPriceOtherReductionByReductionPlanId(id).size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should get exam reduction by reduction plan")
	void testGetExamReductionByReductionPlanId() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(2, null);
		repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = reductionPlans.get(0);

		ExamType testExamType1 = new TestExamType().setup(false);
		testExamType1.setCode("AZ");
		testExamType1.setDescription("AZ_DES");
		ExamTypeRepository.saveAndFlush(testExamType1);
		Exam testExam = new TestExam().setup(testExamType1, 1, true);
		testExam.setCode("EXA");
		testExam.setDescription("EXAM_DES");
		ExamRepository.saveAndFlush(testExam);
		ExamReduction examReduction = generate.generateExamReductionFixture(testExam, reductionPlan);
		ExamReductionRepository.saveAndFlush(examReduction);
		reductionPlan.getExamReductions().add(examReduction);

		List<ExamReduction> existingExamReductionList = manager.getExamReductionByReductionPlanId(reductionPlan.getId());

		assertThat(existingExamReductionList.size()).isEqualTo(2);
		assertThat(existingExamReductionList.get(1).getExam().getCode()).isEqualTo("EXA");
	}

	@Test
	@DisplayName("Should save and delete an exam reduction")
	void testSaveAndDeleteExamReduction() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = manager.getByDescription(reductionPlans.get(0).getDescription(), false).get(0);

		ExamType testExamType1 = new TestExamType().setup(false);
		testExamType1.setCode("AZ");
		testExamType1.setDescription("AZ_DES");
		testExamType1= ExamTypeRepository.saveAndFlush(testExamType1);
		Exam testExam = new TestExam().setup(testExamType1, 1, true);
		testExam.setCode("EXA");
		testExam.setDescription("EXAM_DES");
		testExam = ExamRepository.saveAndFlush(testExam);
		ExamReduction examReduction = generate.generateExamReductionFixture(testExam, reductionPlan);
		ExamReductionRepository.saveAndFlush(examReduction);

		assertThat(manager.getExamReductionByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(2);
		manager.deleteExamReduction(examReduction);
		assertThat(manager.getExamReductionByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Should delete a list of exam reduction")
	void testDeleteBulkExamReduction() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		reductionPlans = repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = manager.getByDescription(reductionPlans.get(0).getDescription(), false).get(0);

		ExamType testExamType1 = new TestExamType().setup(false);
		testExamType1.setCode("AZ");
		testExamType1.setDescription("AZ_DES");
		testExamType1 = ExamTypeRepository.saveAndFlush(testExamType1);
		Exam testExam = new TestExam().setup(testExamType1, 1, true);
		testExam.setCode("EXA");
		testExam.setDescription("EXAM_DES");
		testExam = ExamRepository.saveAndFlush(testExam);
		ExamReduction examReduction = generate.generateExamReductionFixture(testExam, reductionPlan);
		ExamReductionRepository.saveAndFlush(examReduction);
		reductionPlan.getExamReductions().add(examReduction);

		assertThat(manager.getExamReductionByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(2);
		reductionPlan.getExamReductions().clear();
		manager.add(reductionPlan);

		assertThat(reductionPlan.getExamReductions().size()).isEqualTo(0);
		assertThat(manager.getExamReductionByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should get medical reduction by reduction plan")
	void testGetMedicalReductionByReductionPlanId() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(2, null);
		repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = reductionPlans.get(0);

		MedicalType testMedicalType1 = new TestMedicalType().setup(false);
		testMedicalType1.setCode("AZ");
		testMedicalType1.setDescription("AZ_DES");
		MedicalsTypeRepository.saveAndFlush(testMedicalType1);
		MedicalsTypeRepository.saveAndFlush(testMedicalType1);
		Medical testMedical = new TestMedical().setup(testMedicalType1, true);
		testMedical.setDescription("EXAM_DES");
		MedicalsRepository.saveAndFlush(testMedical);
		MedicalsRepository.saveAndFlush(testMedical);
		MedicalReduction medicalReduction = generate.generateMedicalReductionFixture(testMedical, reductionPlan);
		MedicalReductionRepository.saveAndFlush(medicalReduction);
		reductionPlan.getMedicalReductions().add(medicalReduction);

		List<MedicalReduction> existingMedicalReductionList = manager.getMedicalReductionByReductionPlanId(reductionPlan.getId());
		assertThat(existingMedicalReductionList.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Should save and dalete a medical reduction")
	void testDeleteMedicalReduction() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		reductionPlans = repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = manager.getByDescription(reductionPlans.get(0).getDescription(), false).get(0);

		MedicalType testMedicalType1 = new TestMedicalType().setup(false);
		testMedicalType1.setCode("AZ");
		testMedicalType1.setDescription("AZ_DES");
		MedicalsTypeRepository.saveAndFlush(testMedicalType1);
		Medical testMedical = new TestMedical().setup(testMedicalType1, true);
		testMedical.setDescription("EXAM_DES");
		MedicalsRepository.saveAndFlush(testMedical);
		MedicalReduction medicalReduction = generate.generateMedicalReductionFixture(testMedical, reductionPlan);
		MedicalReductionRepository.saveAndFlush(medicalReduction);

		assertThat(manager.getMedicalReductionByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(2);
		manager.deleteMedicalReduction(medicalReduction);
		assertThat(manager.getMedicalReductionByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Should delete a list of medical reduction")
	void testDeleteBulkMedicalReduction() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		reductionPlans = repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = manager.getByDescription(reductionPlans.get(0).getDescription(), false).get(0);

		MedicalType testMedicalType1 = new TestMedicalType().setup(false);
		testMedicalType1.setCode("AZR");
		testMedicalType1.setDescription("MED");
		testMedicalType1 = MedicalsTypeRepository.saveAndFlush(testMedicalType1);
		Medical testMedical = new TestMedical().setup(testMedicalType1, true);
		testMedical.setDescription("MED_DES");
		testMedical = MedicalsRepository.saveAndFlush(testMedical);
		MedicalReduction medicalReduction = generate.generateMedicalReductionFixture(testMedical, reductionPlan);
		MedicalReductionRepository.saveAndFlush(medicalReduction);
		reductionPlan.getMedicalReductions().add(medicalReduction);

		assertThat(manager.getMedicalReductionByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(2);
		reductionPlan.getMedicalReductions().clear();
		manager.add(reductionPlan);
		assertThat(reductionPlan.getMedicalReductions().size()).isEqualTo(0);
		assertThat(manager.getMedicalReductionByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should get operation reduction by reduction plan")
	void testGetOperationReductionByReductionPlanId() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(2, null);ReductionPlan reductionPlan = reductionPlans.get(0);
		repository.saveAllAndFlush(reductionPlans);

		OperationType testOperationType1 = new TestOperationType().setup(false);
		testOperationType1.setCode("AZ");
		testOperationType1.setDescription("AZ_DES");
		OperationTypeRepository.saveAndFlush(testOperationType1);
		Operation testOperation = new TestOperation().setup(testOperationType1, true);
		testOperation.setDescription("EXAM_DES");
		OperationRepository.saveAndFlush(testOperation);
		OperationReduction operationReduction = generate.generateOperationReductionFixture(testOperation, reductionPlan);

		OperationReductionRepository.saveAndFlush(operationReduction);
		reductionPlan.getOperationReductions().add(operationReduction);

		List<OperationReduction> existingOperationReductionList = manager.getOperationReductionByReductionPlanId(reductionPlan.getId());
		assertThat(existingOperationReductionList.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Should save and Delete an operation reduction")
	void testDeleteOperationReduction() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		reductionPlans = repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = manager.getByDescription(reductionPlans.get(0).getDescription(), false).get(0);

		OperationType testOperationType1 = new TestOperationType().setup(false);
		testOperationType1.setCode("AT");
		testOperationType1.setDescription("AT_DES");
		OperationTypeRepository.saveAndFlush(testOperationType1);
		Operation testOperation = new TestOperation().setup(testOperationType1, true);
		testOperation.setDescription("OP_DES");
		OperationRepository.saveAndFlush(testOperation);
		OperationReduction operationReduction = generate.generateOperationReductionFixture(testOperation, reductionPlan);
		OperationReductionRepository.saveAndFlush(operationReduction);

		assertThat(manager.getOperationReductionByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(2);
		manager.deleteOperationReduction(operationReduction);
		assertThat(manager.getOperationReductionByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Should delete a list of operation reduction")
	void testDeleteBulkOperationReduction() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		reductionPlans = repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = manager.getByDescription(reductionPlans.get(0).getDescription(), false).get(0);

		OperationType testOperationType1 = new TestOperationType().setup(false);
		testOperationType1.setCode("AP");
		testOperationType1.setDescription("AP_DES");
		OperationTypeRepository.saveAndFlush(testOperationType1);
		Operation testOperation = new TestOperation().setup(testOperationType1, true);
		testOperation.setDescription("OP_DES");
		OperationRepository.saveAndFlush(testOperation);
		OperationReduction operationReduction = generate.generateOperationReductionFixture(testOperation, reductionPlan);
		OperationReductionRepository.saveAndFlush(operationReduction);

		reductionPlan.getOperationReductions().add(operationReduction);

		assertThat(manager.getOperationReductionByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(2);
		assertThat(reductionPlan.getOperationReductions().size()).isEqualTo(2);
		reductionPlan.getOperationReductions().clear();
		manager.add(reductionPlan);
		assertThat(reductionPlan.getOperationReductions().size()).isEqualTo(0);
		assertThat(manager.getOperationReductionByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should get price other reduction by reduction plan")
	void testGetPricesOtherReductionByReductionPlanId() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(2, null);
		reductionPlans = repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = manager.getByDescription(reductionPlans.get(0).getDescription(), false).get(0);

		PricesOthers testPriceOthers = new TestPricesOthers().setup(true);
		testPriceOthers.setDescription("ZE");
		testPriceOthers = PriceOthersRepository.saveAndFlush(testPriceOthers);
		PriceOtherReduction priceOtherReduction = generate.generatePriceOtherReductionFixture(testPriceOthers, reductionPlan);
		PricesOtherReductionRepository.saveAndFlush(priceOtherReduction);
		reductionPlan.getPriceOtherReductions().add(priceOtherReduction);

		List<PriceOtherReduction> existingPriceOtherReductionList = manager.getPriceOtherReductionByReductionPlanId(reductionPlan.getId());
		assertThat(existingPriceOtherReductionList.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Should save and delete price other reduction")
	void testDeletePricesOtherReduction() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		reductionPlans = repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = manager.getByDescription(reductionPlans.get(0).getDescription(), false).get(0);

		PricesOthers testPriceOthers = new TestPricesOthers().setup(true);
		testPriceOthers.setDescription("ZE");
		testPriceOthers = PriceOthersRepository.saveAndFlush(testPriceOthers);
		PriceOtherReduction priceOtherReduction = generate.generatePriceOtherReductionFixture(testPriceOthers, reductionPlan);
		PricesOtherReductionRepository.saveAndFlush(priceOtherReduction);

		assertThat(manager.getPriceOtherReductionByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(2);
		manager.deleteOtherReduction(priceOtherReduction);
		assertThat(manager.getPriceOtherReductionByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Should delete a list of price other reduction")
	void testDeleteBulkPricesOtherReduction() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		reductionPlans = repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = manager.getByDescription(reductionPlans.get(0).getDescription(), false).get(0);

		PricesOthers testPriceOthers = new TestPricesOthers().setup(true);
		testPriceOthers.setDescription("ZE");
		testPriceOthers = PriceOthersRepository.saveAndFlush(testPriceOthers);
		PriceOtherReduction priceOtherReduction = generate.generatePriceOtherReductionFixture(testPriceOthers, reductionPlan);
		PricesOtherReductionRepository.saveAndFlush(priceOtherReduction);
		reductionPlan.getPriceOtherReductions().add(priceOtherReduction);

		assertThat(reductionPlan.getPriceOtherReductions().size()).isEqualTo(2);
		assertThat(manager.getPriceOtherReductionByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(2);
		reductionPlan.getPriceOtherReductions().clear();
		assertThat(reductionPlan.getPriceOtherReductions().size()).isEqualTo(0);
		assertThat(manager.getPriceOtherReductionByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(0);
	}

	public class ReductionPlanDataGenerate {
		public ReductionPlanDataGenerate() {

		}
		public List<ReductionPlan> generateReductionPlanFixtures(int number, String fixedDescription) {
			return IntStream.range(0, number).mapToObj(i -> {
				String description = fixedDescription != null ? fixedDescription : "Description " + i;
				double opRate = 1.0 + i;
				double medRate = 2.0 + i;
				double examRate = 3.0 + i;
				double otherRate = 3.0 + i;

				ReductionPlan reductionPlan = new ReductionPlan(description, opRate, medRate, examRate, otherRate);
				List<ExamReduction> examReductions = new ArrayList<>();
				List<MedicalReduction> medicalReductions = new ArrayList<>();
				List<OperationReduction> operationReductions = new ArrayList<>();
				List<PriceOtherReduction> priceOtherReductions = new ArrayList<>();

				try {
					ExamType testExamType = new TestExamType().setup(false);
					testExamType.setCode(testExamType.getCode() + i);
					testExamType.setDescription(testExamType.getDescription() + i);
					testExamType = ExamTypeRepository.saveAndFlush(testExamType);
					Exam testExam = new TestExam().setup(testExamType, 1, true);
					testExam.setCode(testExam.getCode() + i);
					testExam.setDescription(testExam.getCode() + i);
					testExam = ExamRepository.saveAndFlush(testExam);
					ExamReduction examReduction = generateExamReductionFixture(testExam, reductionPlan);
					examReductions.add(examReduction);

					MedicalType testMedicalType = new TestMedicalType().setup(false);
					testMedicalType.setCode(testMedicalType.getCode() +i);
					MedicalsTypeRepository.saveAndFlush(testMedicalType);
					Medical testMedical = new TestMedical().setup(testMedicalType, true);
					testMedical.setProdCode(testMedical.getProdCode() + i);
					testMedical.setDescription(testMedical.getDescription() + i);
					MedicalsRepository.saveAndFlush(testMedical);
					MedicalReduction medicalReduction = generateMedicalReductionFixture(testMedical, reductionPlan);
					medicalReductions.add(medicalReduction);

					OperationType testOperationType = new TestOperationType().setup(false);
					testOperationType.setCode(testOperationType.getCode() + i);
					OperationTypeRepository.saveAndFlush(testOperationType);
					Operation testOperation = new TestOperation().setup(testOperationType, true);
					testOperation.setCode(testOperation.getCode() + i);
					testOperation.setDescription(testOperation.getDescription() + i);
					OperationRepository.saveAndFlush(testOperation);
					OperationReduction operationReduction = generateOperationReductionFixture(testOperation, reductionPlan);
					operationReductions.add(operationReduction);

					TestPricesOthers testPricesOthers = new TestPricesOthers();
					PricesOthers pricesOthers = testPricesOthers.setup(false);
					PriceOthersRepository.saveAndFlush(pricesOthers);
					priceOtherReductions.add(generatePriceOtherReductionFixture(pricesOthers, reductionPlan));
				} catch (OHException e) {
					throw new RuntimeException("Failed to generate fixture for reduction", e);
				}

				reductionPlan.setExamReductions(examReductions);
				reductionPlan.setMedicalReductions(medicalReductions);
				reductionPlan.setOperationReductions(operationReductions);
				reductionPlan.setPriceOtherReductions(priceOtherReductions);

				return reductionPlan;
			}).toList();
		}

		public ExamReduction generateExamReductionFixture(Exam exam, ReductionPlan reductionPlan) throws OHException {
			if (exam == null) {
				TestExam testExam = new TestExam();
				TestExamType testExamType = new TestExamType();
				exam = testExam.setup(testExamType.setup(false), 1, false);
			}
			return new ExamReduction(reductionPlan != null ? reductionPlan : generateReductionPlanFixtures(1, null).get(0)
				, exam, 1.0);
		}

		public OperationReduction generateOperationReductionFixture(Operation operation, ReductionPlan reductionPlan) throws OHException {
			if (operation == null) {
				TestOperation testOperation = new TestOperation();
				TestOperationType testOperationType = new TestOperationType();
				operation = testOperation.setup(testOperationType.setup(false), false);
			}
			return new OperationReduction(reductionPlan != null ? reductionPlan : generateReductionPlanFixtures(1, null).get(0)
				, operation, 1.0);
		}

		public MedicalReduction generateMedicalReductionFixture(Medical medical, ReductionPlan reductionPlan) throws OHException {
			if (medical == null) {
				TestMedical testMedical = new TestMedical();
				TestMedicalType testMedicalType = new TestMedicalType();
				medical = testMedical.setup(testMedicalType.setup(false), false);
			}
			return new MedicalReduction(reductionPlan != null ? reductionPlan : generateReductionPlanFixtures(1, null).get(0)
				, medical, 1.0);
		}

		public PriceOtherReduction generatePriceOtherReductionFixture(PricesOthers pricesOthers, ReductionPlan reductionPlan) throws OHException {
			if (pricesOthers == null) {
				TestPricesOthers testPricesOthers = new TestPricesOthers();
				pricesOthers = testPricesOthers.setup(false);
			}
			return new PriceOtherReduction(reductionPlan != null ? reductionPlan : generateReductionPlanFixtures(1, null).get(0)
				, pricesOthers, 1.0);
		}
	}
}