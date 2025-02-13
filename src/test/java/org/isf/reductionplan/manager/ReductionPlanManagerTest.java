/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.exa.TestExam;
import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.model.Exam;
import org.isf.exatype.TestExamType;
import org.isf.exatype.manager.ExamTypeBrowserManager;
import org.isf.exatype.model.ExamType;
import org.isf.medicals.TestMedical;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.manager.MedicalTypeBrowserManager;
import org.isf.medtype.model.MedicalType;
import org.isf.operation.TestOperation;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.operation.model.Operation;
import org.isf.opetype.TestOperationType;
import org.isf.opetype.manager.OperationTypeBrowserManager;
import org.isf.opetype.model.OperationType;
import org.isf.pricesothers.TestPricesOthers;
import org.isf.pricesothers.manager.PricesOthersManager;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.reductionplan.data.ReductionPlanDataGenerate;
import org.isf.reductionplan.model.ExamReduction;
import org.isf.reductionplan.model.MedicalReduction;
import org.isf.reductionplan.model.OperationReduction;
import org.isf.reductionplan.model.PriceOtherReduction;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.reductionplan.service.ExamReductionIoOperationsRepository;
import org.isf.reductionplan.service.MedicalReductionIoOperationRepository;
import org.isf.reductionplan.service.OperationReductionIoOperationRepository;
import org.isf.reductionplan.service.PriceOtherReductionIoOperationRepository;
import org.isf.reductionplan.service.ReductionplanIoOperationRepository;
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
	ExamBrowsingManager examBrowsingManager;

	@Autowired
	ExamTypeBrowserManager examTypeBrowserManager;

	@Autowired
	MedicalReductionIoOperationRepository MedicalReductionRepository;

	@Autowired
	MedicalBrowsingManager medicalBrowsingManager;

	@Autowired
	MedicalTypeBrowserManager medicalTypeBrowserManager;

	@Autowired
	OperationReductionIoOperationRepository OperationReductionRepository;

	@Autowired
	OperationBrowserManager operationBrowserManager;

	@Autowired
	OperationTypeBrowserManager operationTypeBrowserManager;

	@Autowired
	PriceOtherReductionIoOperationRepository PricesOtherReductionRepository;

	@Autowired
	PricesOthersManager pricesOthersManager;

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	@DisplayName("Should get all reduction plans")
	void testGetAll() throws Exception {
		List<ReductionPlan> reductionPlans = ReductionPlanDataGenerate.generateReductionPlanFixtures(2, null);
		repository.saveAllAndFlush(reductionPlans);

		List<ReductionPlan> existingReductionPlan = manager.getAll(false);

		assertThat(existingReductionPlan.size()).isEqualTo(reductionPlans.size());
	}

	@Test
	@DisplayName("Should get all reduction plans by description")
	void testGetByDescription() throws Exception {
		String description = "Fixed Description";
		List<ReductionPlan> reductionPlans = ReductionPlanDataGenerate.generateReductionPlanFixtures(2, description);

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
		String description = "Test description";
		ReductionPlan reductionPlan = new ReductionPlan(description, 10, 10, 10, 10);
		ReductionPlan saveReductionPlan = manager.save(reductionPlan);

		assertThat(saveReductionPlan).isNotNull();
		assertThat(saveReductionPlan.getId()).isGreaterThan(0);
		assertThat(saveReductionPlan.getDescription()).isEqualTo(description);
		assertThat(saveReductionPlan.getExamRate()).isEqualTo(10);
		assertThat(saveReductionPlan.getOperationRate()).isEqualTo(10);
		assertThat(saveReductionPlan.getMedicalRate()).isEqualTo(10);
		assertThat(saveReductionPlan.getOtherRate()).isEqualTo(10);
	}

	@Test
	@DisplayName("Should update reduction plan")
	void testUpdate() throws Exception {
		String description = "Test description";
		ReductionPlan reductionPlan = new ReductionPlan(description, 10, 10, 10, 10);
		repository.saveAndFlush(reductionPlan);

		ReductionPlan existingReductionPlan = manager.getByDescription(description, false).get(0);
		existingReductionPlan.setDescription("update");
		existingReductionPlan.setOperationRate(0.0);
		existingReductionPlan.setMedicalRate(0.0);
		existingReductionPlan.setExamRate(0.0);
		existingReductionPlan.setOtherRate(0.0);

		ReductionPlan updateReductionPlan = manager.update(existingReductionPlan);
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
		String description = "Test Description";
		ReductionPlan reductionPlan = new ReductionPlan(description, 10, 10, 10, 10);
		repository.saveAndFlush(reductionPlan);

		ReductionPlan existingReductionPlan = manager.getByDescription(description, false).get(0);

		ReductionPlan deletedReductionPlan = manager.delete(existingReductionPlan);
		assertThat(deletedReductionPlan.isDeleted()).isTrue();
		assertThat(manager.getAll(false)).hasSize(0);
	}

	@Test
	@DisplayName("Should get exam reduction by reduction plan")
	void testGetExamReductionByReductionPlanId() throws Exception {
		TestExamType testExamType = new TestExamType();
		TestExam testExam = new TestExam();
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = manager.save(reductionPlan);

		ExamReduction examReduction = ReductionPlanDataGenerate.generateExamReductionFixture(null, reductionPlan);
		ExamType examType = examTypeBrowserManager.newExamType(examReduction.getExam().getExamtype());
		examReduction.getExam().setExamtype(examType);
		Exam exam = examBrowsingManager.newExam(examReduction.getExam());
		examReduction.setExam(exam);
		ExamReductionRepository.saveAndFlush(examReduction);

		ExamType examType1 = testExamType.setup(false);
		examType1.setCode("A");
		examType1.setDescription("test description 1");
		examType1 = examTypeBrowserManager.newExamType(examType1);

		Exam exam1 = testExam.setup(examType1, 2, false);
		exam1.setCode("test exam code 1");
		exam1.setDescription("test exam description 1");
		exam1 = examBrowsingManager.newExam(exam1);

		ExamReduction examReduction1 = ReductionPlanDataGenerate.generateExamReductionFixture(exam1, reductionPlan);
		ExamReductionRepository.saveAndFlush(examReduction1);

		List<ExamReduction> existingExamReductionList = manager.getExamReductionByReductionPlanId(reductionPlan.getId(), false);

		assertThat(existingExamReductionList.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Should save exam reduction")
	void testSaveExamReduction() throws Exception {
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = manager.save(reductionPlan);

		ExamReduction examReduction = ReductionPlanDataGenerate.generateExamReductionFixture(null, reductionPlan);
		ExamType examType = examTypeBrowserManager.newExamType(examReduction.getExam().getExamtype());
		examReduction.getExam().setExamtype(examType);
		Exam exam = examBrowsingManager.newExam(examReduction.getExam());
		examReduction.setExam(exam);

		ExamReduction saveExamReduction = manager.save(examReduction);
		assertThat(saveExamReduction).isNotNull();
		assertThat(saveExamReduction.getReductionPlan()).isEqualTo(reductionPlan);
		assertThat(saveExamReduction.getExam()).isEqualTo(exam);
		assertThat(saveExamReduction.getReductionRate()).isEqualTo(1.0);
	}

	@Test
	@DisplayName("Should delete an exam reduction")
	void testDeleteExamReduction() throws Exception {
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = manager.save(reductionPlan);

		ExamReduction examReduction = ReductionPlanDataGenerate.generateExamReductionFixture(null, reductionPlan);
		ExamType examType = examTypeBrowserManager.newExamType(examReduction.getExam().getExamtype());
		examReduction.getExam().setExamtype(examType);
		Exam exam = examBrowsingManager.newExam(examReduction.getExam());
		examReduction.setExam(exam);
		examReduction = manager.save(examReduction);

		ExamReduction deletedExamReduction = manager.delete(examReduction);

		assertThat(deletedExamReduction.isDeleted()).isTrue();
		assertThat(manager.getExamReductionByReductionPlanId(reductionPlan.getId(), false).size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should delete a list of exam reduction")
	void testDeleteBulkExamReduction() throws Exception {
		TestExamType testExamType = new TestExamType();
		TestExam testExam = new TestExam();
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = manager.save(reductionPlan);

		ExamReduction examReduction = ReductionPlanDataGenerate.generateExamReductionFixture(null, reductionPlan);
		ExamType examType = examTypeBrowserManager.newExamType(examReduction.getExam().getExamtype());
		examReduction.getExam().setExamtype(examType);
		Exam exam = examBrowsingManager.newExam(examReduction.getExam());
		examReduction.setExam(exam);

		ExamType examType1 = testExamType.setup(false);
		examType1.setCode("A");
		examType1.setDescription("test description 1");
		examType1 = examTypeBrowserManager.newExamType(examType1);

		Exam exam1 = testExam.setup(examType1, 2, false);
		exam1.setCode("test exam code 1");
		exam1.setDescription("test exam description 1");
		exam1 = examBrowsingManager.newExam(exam1);

		ExamReduction examReduction1 = ReductionPlanDataGenerate.generateExamReductionFixture(exam1, reductionPlan);

		ExamReductionRepository.saveAndFlush(examReduction);
		ExamReductionRepository.saveAndFlush(examReduction1);

		List<ExamReduction> examReductionList = manager.getExamReductionByReductionPlanId(reductionPlan.getId(), false);

		manager.deleteBulkExamReduction(examReductionList);

		List<ExamReduction> deletedExamReductionList = manager.getExamReductionByReductionPlanId(reductionPlan.getId(), false);

		assertThat(deletedExamReductionList.size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should get medical reduction by reduction plan")
	void testGetMedicalReductionByReductionPlan() throws Exception {
		TestMedicalType testMedicalType = new TestMedicalType();
		TestMedical testMedical = new TestMedical();
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = manager.save(reductionPlan);

		MedicalReduction medicalReduction = ReductionPlanDataGenerate.generateMedicalReductionFixture(null, reductionPlan);
		MedicalType medicalType = medicalTypeBrowserManager.newMedicalType(medicalReduction.getMedical().getType());
		medicalReduction.getMedical().setType(medicalType);
		Medical medical = medicalBrowsingManager.newMedical(medicalReduction.getMedical());
		medicalReduction.setMedical(medical);
		MedicalReductionRepository.saveAndFlush(medicalReduction);

		MedicalType medicalType1 = testMedicalType.setup(false);
		medicalType1.setCode("A");
		medicalType1.setDescription("test description 1");
		medicalType1 = medicalTypeBrowserManager.newMedicalType(medicalType1);

		Medical medical1 = testMedical.setup(medicalType1, false);
		medical1.setProdCode("TP2");
		medical1.setDescription("test operation description 1");
		medical1 = medicalBrowsingManager.newMedical(medical1);

		MedicalReduction medicalReduction1 = ReductionPlanDataGenerate.generateMedicalReductionFixture(medical1, reductionPlan);
		MedicalReductionRepository.saveAndFlush(medicalReduction1);

		List<MedicalReduction> existingMedicalReductionList = manager.getMedicalReductionByReductionPlanId(reductionPlan.getId(), false);

		assertThat(existingMedicalReductionList.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Should save medical reduction")
	void testSaveMedicalReduction() throws Exception {
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = manager.save(reductionPlan);

		MedicalReduction medicalReduction = ReductionPlanDataGenerate.generateMedicalReductionFixture(null, reductionPlan);
		MedicalType medicalType = medicalTypeBrowserManager.newMedicalType(medicalReduction.getMedical().getType());
		medicalReduction.getMedical().setType(medicalType);
		Medical medical = medicalBrowsingManager.newMedical(medicalReduction.getMedical());
		medicalReduction.setMedical(medical);

		MedicalReduction saveMedicalReduction = manager.save(medicalReduction);
		assertThat(saveMedicalReduction).isNotNull();
		assertThat(saveMedicalReduction.getReductionPlan()).isEqualTo(reductionPlan);
		assertThat(saveMedicalReduction.getMedical()).isEqualTo(medical);
		assertThat(saveMedicalReduction.getReductionRate()).isEqualTo(1.0);
	}

	@Test
	@DisplayName("Should delete an medical reduction")
	void testDeleteMedicalReduction() throws Exception {
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = manager.save(reductionPlan);

		MedicalReduction medicalReduction = ReductionPlanDataGenerate.generateMedicalReductionFixture(null, reductionPlan);
		MedicalType medicalType = medicalTypeBrowserManager.newMedicalType(medicalReduction.getMedical().getType());
		medicalReduction.getMedical().setType(medicalType);
		Medical medical = medicalBrowsingManager.newMedical(medicalReduction.getMedical());
		medicalReduction.setMedical(medical);
		medicalReduction = manager.save(medicalReduction);

		MedicalReduction deletedMedicalReduction = manager.delete(medicalReduction);

		assertThat(deletedMedicalReduction.isDeleted()).isTrue();
		assertThat(manager.getMedicalReductionByReductionPlanId(reductionPlan.getId(), false).size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should delete a list of medical reduction")
	void testDeleteBulkMedicalReduction() throws Exception {
		TestMedicalType testMedicalType = new TestMedicalType();
		TestMedical testMedical = new TestMedical();
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = manager.save(reductionPlan);

		MedicalReduction medicalReduction = ReductionPlanDataGenerate.generateMedicalReductionFixture(null, reductionPlan);
		MedicalType medicalType = medicalTypeBrowserManager.newMedicalType(medicalReduction.getMedical().getType());
		medicalReduction.getMedical().setType(medicalType);
		Medical medical = medicalBrowsingManager.newMedical(medicalReduction.getMedical());
		medicalReduction.setMedical(medical);

		MedicalType medicalType1 = testMedicalType.setup(false);
		medicalType1.setCode("A");
		medicalType1.setDescription("test description 1");
		medicalType1 = medicalTypeBrowserManager.newMedicalType(medicalType1);

		Medical medical1 = testMedical.setup(medicalType1, false);
		medical1.setProdCode("TP2");
		medical1.setDescription("test operation description 1");
		medical1 = medicalBrowsingManager.newMedical(medical1);

		MedicalReduction medicalReduction1 = ReductionPlanDataGenerate.generateMedicalReductionFixture(medical1, reductionPlan);

		MedicalReductionRepository.saveAndFlush(medicalReduction);
		MedicalReductionRepository.saveAndFlush(medicalReduction1);

		List<MedicalReduction> medicalReductionList = manager.getMedicalReductionByReductionPlanId(reductionPlan.getId(), false);

		manager.deleteBulkMedicalReduction(medicalReductionList);

		List<MedicalReduction> deletedMedicalReductionList = manager.getMedicalReductionByReductionPlanId(reductionPlan.getId(), false);

		assertThat(deletedMedicalReductionList.size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should get operation reduction by reduction plan")
	void testGetOperationReductionByReductionPlan() throws Exception {
		TestOperationType testOperationType = new TestOperationType();
		TestOperation testOperation = new TestOperation();
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = manager.save(reductionPlan);

		OperationReduction operationReduction = ReductionPlanDataGenerate.generateOperationReductionFixture(null, reductionPlan);
		OperationType operationType = operationTypeBrowserManager.newOperationType(operationReduction.getOperation().getType());
		operationReduction.getOperation().setType(operationType);
		Operation operation = operationBrowserManager.newOperation(operationReduction.getOperation());
		operationReduction.setOperation(operation);
		OperationReductionRepository.saveAndFlush(operationReduction);

		OperationType operationType1 = testOperationType.setup(false);
		operationType1.setCode("A");
		operationType1.setDescription("test description 1");
		operationType1 = operationTypeBrowserManager.newOperationType(operationType1);

		Operation operation1 = testOperation.setup(operationType1, false);
		operation1.setCode("test operation code 1");
		operation1.setDescription("test operation description 1");
		operation1 = operationBrowserManager.newOperation(operation1);

		OperationReduction operationReduction1 = ReductionPlanDataGenerate.generateOperationReductionFixture(operation1, reductionPlan);
		OperationReductionRepository.saveAndFlush(operationReduction1);

		List<OperationReduction> existingOperationReductionList = manager.getOperationReductionByReductionPlanId(reductionPlan.getId(), false);

		assertThat(existingOperationReductionList.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Should save operation reduction")
	void testSaveOperationReduction() throws Exception {
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = manager.save(reductionPlan);

		OperationReduction operationReduction = ReductionPlanDataGenerate.generateOperationReductionFixture(null, reductionPlan);
		OperationType operationType = operationTypeBrowserManager.newOperationType(operationReduction.getOperation().getType());
		operationReduction.getOperation().setType(operationType);
		Operation operation = operationBrowserManager.newOperation(operationReduction.getOperation());
		operationReduction.setOperation(operation);

		OperationReduction saveOperationReduction = manager.save(operationReduction);
		assertThat(saveOperationReduction).isNotNull();
		assertThat(saveOperationReduction.getReductionPlan()).isEqualTo(reductionPlan);
		assertThat(saveOperationReduction.getOperation()).isEqualTo(operation);
		assertThat(saveOperationReduction.getReductionRate()).isEqualTo(1.0);
	}

	@Test
	@DisplayName("Should delete an operation reduction")
	void testDeleteOperationReduction() throws Exception {
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = manager.save(reductionPlan);

		OperationReduction operationReduction = ReductionPlanDataGenerate.generateOperationReductionFixture(null, reductionPlan);
		OperationType operationType = operationTypeBrowserManager.newOperationType(operationReduction.getOperation().getType());
		operationReduction.getOperation().setType(operationType);
		Operation operation = operationBrowserManager.newOperation(operationReduction.getOperation());
		operationReduction.setOperation(operation);
		operationReduction = manager.save(operationReduction);

		OperationReduction deletedOperationReduction = manager.delete(operationReduction);

		assertThat(deletedOperationReduction.isDeleted()).isTrue();
		assertThat(manager.getOperationReductionByReductionPlanId(reductionPlan.getId(), false).size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should delete a list of operation reduction")
	void testDeleteBulkOperationReduction() throws Exception {
		TestOperationType testOperationType = new TestOperationType();
		TestOperation testOperation = new TestOperation();
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = manager.save(reductionPlan);

		OperationReduction operationReduction = ReductionPlanDataGenerate.generateOperationReductionFixture(null, reductionPlan);
		OperationType operationType = operationTypeBrowserManager.newOperationType(operationReduction.getOperation().getType());
		operationReduction.getOperation().setType(operationType);
		Operation operation = operationBrowserManager.newOperation(operationReduction.getOperation());
		operationReduction.setOperation(operation);

		OperationType operationType1 = testOperationType.setup(false);
		operationType1.setCode("A");
		operationType1.setDescription("test description 1");
		operationType1 = operationTypeBrowserManager.newOperationType(operationType1);

		Operation operation1 = testOperation.setup(operationType1, false);
		operation1.setCode("test operation code 1");
		operation1.setDescription("test operation description 1");
		operation1 = operationBrowserManager.newOperation(operation1);

		OperationReduction operationReduction1 = ReductionPlanDataGenerate.generateOperationReductionFixture(operation1, reductionPlan);

		OperationReductionRepository.saveAndFlush(operationReduction);
		OperationReductionRepository.saveAndFlush(operationReduction1);

		List<OperationReduction> operationReductionList = manager.getOperationReductionByReductionPlanId(reductionPlan.getId(), false);

		manager.deleteBulkOperationReduction(operationReductionList);

		List<OperationReduction> deletedOperationReductionList = manager.getOperationReductionByReductionPlanId(reductionPlan.getId(), false);

		assertThat(deletedOperationReductionList.size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should get price other reduction by reduction plan")
	void testGetPricesOtherReductionByReductionPlan() throws Exception {
		TestPricesOthers testPricesOthers = new TestPricesOthers();
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = manager.save(reductionPlan);

		PriceOtherReduction priceOtherReduction = ReductionPlanDataGenerate.generatePriceOtherReductionFixture(null, reductionPlan);
		PricesOthers pricesOthers = pricesOthersManager.newOther(priceOtherReduction.getPricesOthers());
		priceOtherReduction.setPricesOthers(pricesOthers);
		PricesOtherReductionRepository.saveAndFlush(priceOtherReduction);

		PricesOthers pricesOthers1 = testPricesOthers.setup(false);
		pricesOthers1.setCode("testPriceOtherCode1");
		pricesOthers1.setDescription("test price other description 1");
		pricesOthers1 = pricesOthersManager.newOther(pricesOthers1);

		PriceOtherReduction priceOtherReduction1 = ReductionPlanDataGenerate.generatePriceOtherReductionFixture(pricesOthers1, reductionPlan);
		PricesOtherReductionRepository.saveAndFlush(priceOtherReduction1);

		List<PriceOtherReduction> existingPriceOtherReductionList = manager.getPriceOtherReductionByReductionPlanId(reductionPlan.getId(), false);

		assertThat(existingPriceOtherReductionList.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Should save price other reduction")
	void testSavePricesOtherReduction() throws Exception {
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = manager.save(reductionPlan);

		PriceOtherReduction priceOtherReduction = ReductionPlanDataGenerate.generatePriceOtherReductionFixture(null, reductionPlan);
		PricesOthers pricesOthers = pricesOthersManager.newOther(priceOtherReduction.getPricesOthers());
		priceOtherReduction.setPricesOthers(pricesOthers);

		PriceOtherReduction savePriceOtherReduction = manager.save(priceOtherReduction);
		assertThat(savePriceOtherReduction).isNotNull();
		assertThat(savePriceOtherReduction.getReductionPlan()).isEqualTo(reductionPlan);
		assertThat(savePriceOtherReduction.getPricesOthers()).isEqualTo(pricesOthers);
		assertThat(savePriceOtherReduction.getReductionRate()).isEqualTo(1.0);
	}

	@Test
	@DisplayName("Should delete an price other reduction")
	void testDeletePricesOtherReduction() throws Exception {
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = manager.save(reductionPlan);

		PriceOtherReduction priceOtherReduction = ReductionPlanDataGenerate.generatePriceOtherReductionFixture(null, reductionPlan);
		PricesOthers pricesOthers = pricesOthersManager.newOther(priceOtherReduction.getPricesOthers());
		priceOtherReduction.setPricesOthers(pricesOthers);
		priceOtherReduction = manager.save(priceOtherReduction);

		PriceOtherReduction deletedPriceOtherReduction = manager.delete(priceOtherReduction);

		assertThat(deletedPriceOtherReduction.isDeleted()).isTrue();
		assertThat(manager.getPriceOtherReductionByReductionPlanId(reductionPlan.getId(), false).size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should delete a list of price other reduction")
	void testDeleteBulkPricesOtherReduction() throws Exception {
		TestPricesOthers testPricesOthers = new TestPricesOthers();
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = manager.save(reductionPlan);

		PriceOtherReduction priceOtherReduction = ReductionPlanDataGenerate.generatePriceOtherReductionFixture(null, reductionPlan);
		PricesOthers pricesOthers = pricesOthersManager.newOther(priceOtherReduction.getPricesOthers());
		priceOtherReduction.setPricesOthers(pricesOthers);

		PricesOthers pricesOthers1 = testPricesOthers.setup(false);
		pricesOthers1.setCode("testPriceOtherCode1");
		pricesOthers1.setDescription("test price other description 1");
		pricesOthers1 = pricesOthersManager.newOther(pricesOthers1);

		PriceOtherReduction priceOtherReduction1 = ReductionPlanDataGenerate.generatePriceOtherReductionFixture(pricesOthers1, reductionPlan);

		PricesOtherReductionRepository.saveAndFlush(priceOtherReduction);
		PricesOtherReductionRepository.saveAndFlush(priceOtherReduction1);

		List<PriceOtherReduction> priceOtherReductionList = manager.getPriceOtherReductionByReductionPlanId(reductionPlan.getId(), false);

		manager.deleteBulkPriceOtherReduction(priceOtherReductionList);

		List<PriceOtherReduction> deletedPriceOtherReductionList = manager.getPriceOtherReductionByReductionPlanId(reductionPlan.getId(), false);

		assertThat(deletedPriceOtherReductionList.size()).isEqualTo(0);
	}
}