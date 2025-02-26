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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
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
import org.isf.reductionplan.service.ReductionPlanRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReductionPlanManagerTest extends OHCoreTestCase {

	@Autowired
	ReductionPlanRepository repository;

	@Autowired
	ReductionPlanManager manager;

	@Autowired
	ExamIoOperationRepository examRepository;

	@Autowired
	ExamTypeIoOperationRepository examTypeRepository;

	@Autowired
	MedicalsIoOperationRepository medicalsRepository;

	@Autowired
	MedicalTypeIoOperationRepository medicalsTypeRepository;

	@Autowired
	OperationIoOperationRepository operationRepository;

	@Autowired
	OperationTypeIoOperationRepository operationTypeRepository;

	@Autowired
	PriceOthersIoOperationRepository priceOthersRepository;
	@Autowired
	ReductionPlanRepository reductionPlanRepository;

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

		List<ReductionPlan> existingReductionPlans = manager.getByDescription(description);

		assertThat(existingReductionPlans).isNotNull();
		assertThat(existingReductionPlans.size()).isEqualTo(2);
		existingReductionPlans.forEach(plan ->
			assertThat(plan.getDescription()).isEqualTo(description));
	}

	@Test
	@DisplayName("Should successfully add reduction plan")
	void testAdd() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		ReductionPlan reductionPlan = reductionPlans.get(0);
		manager.add(reductionPlan);
		ReductionPlan saveReductionPlan = manager.getById(reductionPlans.get(0).getId());

		assertThat(saveReductionPlan).isNotNull();
		assertThat(saveReductionPlan.getId()).isGreaterThan(0);
		assertThat(saveReductionPlan.getDescription()).isEqualTo("Description 0");
		assertThat(saveReductionPlan.getExamRate()).isEqualTo(BigDecimal.valueOf(3.0));
		assertThat(saveReductionPlan.getOperationRate()).isEqualTo(BigDecimal.valueOf(1.0));
		assertThat(saveReductionPlan.getMedicalRate()).isEqualTo(BigDecimal.valueOf(2.0));
		assertThat(saveReductionPlan.getOtherRate()).isEqualTo(BigDecimal.valueOf(3.0));
		assertThat(saveReductionPlan.getExamReductions().get(0).getId()).isEqualTo(1);
		assertThat(saveReductionPlan.getMedicalReductions().get(0).getId()).isEqualTo(1);
		assertThat(saveReductionPlan.getOperationReductions().get(0).getId()).isEqualTo(1);
		assertThat(saveReductionPlan.getPriceOtherReductions().get(0).getId()).isEqualTo(1);
	}

	@Test
	@DisplayName("Should not add reduction plan")
	void shouldThrowExceptionWhenDuplicateItemFound() throws OHException, OHServiceException {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		ReductionPlan reductionPlan = reductionPlans.get(0);

		ExamReduction examReduction = generate.generateExamReductionFixture(reductionPlan.getExamReductions().get(0).getExam(), reductionPlan);
		reductionPlan.getExamReductions().add(examReduction);
		OHDataValidationException exception = assertThrows(OHDataValidationException.class, () -> manager.add(reductionPlan));
		assertNotNull(exception);
		assertFalse(exception.getMessages().isEmpty());
		assertEquals("angal.reductionplan.duplicateexamfound.msg", exception.getMessages().get(0).getMessage());

		ReductionPlan savedReductionPlan = manager.getById(reductionPlan.getId());
		assertNull(savedReductionPlan);
	}

	@Test
	@DisplayName("Should not add reduction plan if invalid reduction rate")
	void shouldThrowExceptionWhenInvalidRatesAreUsed() throws OHException, OHServiceException {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		ReductionPlan reductionPlan = reductionPlans.get(0);

		reductionPlan.setExamRate(BigDecimal.valueOf(1001));
		reductionPlan.getPriceOtherReductions().get(0).setReductionRate(BigDecimal.valueOf(0));
		reductionPlan.setOperationRate(BigDecimal.valueOf(-2));
		OHDataValidationException exception = assertThrows(OHDataValidationException.class, () -> manager.add(reductionPlan));
		assertNotNull(exception);
		assertFalse(exception.getMessages().isEmpty());
		assertEquals("angal.reductionplan.invalidglobalreductionrate.msg", exception.getMessages().get(0).getMessage());

		ReductionPlan savedReductionPlan = manager.getById(reductionPlan.getId());
		assertNull(savedReductionPlan);
	}

	@Test
	@DisplayName("Should update reduction plan")
	void testUpdate() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		repository.saveAllAndFlush(reductionPlans);
		ReductionPlan existingReductionPlan = manager.getById(reductionPlans.get(0).getId());
		existingReductionPlan.setDescription("update");
		existingReductionPlan.setOperationRate(BigDecimal.valueOf(0.1));
		existingReductionPlan.setMedicalRate(BigDecimal.valueOf(0.1));
		existingReductionPlan.setExamRate(BigDecimal.valueOf(0.1));
		existingReductionPlan.setOtherRate(BigDecimal.valueOf(0.1));

		manager.update(existingReductionPlan);
		ReductionPlan updateReductionPlan = manager.getById(existingReductionPlan.getId());
		assertThat(updateReductionPlan).isNotNull();
		assertThat(updateReductionPlan).isNotNull();
		assertThat(updateReductionPlan.getId()).isEqualTo(existingReductionPlan.getId());
		assertThat(updateReductionPlan.getDescription()).isEqualTo("update");
		assertThat(updateReductionPlan.getOperationRate()).isEqualTo(BigDecimal.valueOf(0.1));
		assertThat(updateReductionPlan.getMedicalRate()).isEqualTo(BigDecimal.valueOf(0.1));
		assertThat(updateReductionPlan.getExamRate()).isEqualTo(BigDecimal.valueOf(0.1));
		assertThat(updateReductionPlan.getOtherRate()).isEqualTo(BigDecimal.valueOf(0.1));
		assertThat(updateReductionPlan.getExamReductions().get(0).getId()).isEqualTo(1);
		assertThat(updateReductionPlan.getMedicalReductions().get(0).getId()).isEqualTo(1);
		assertThat(updateReductionPlan.getOperationReductions().get(0).getId()).isEqualTo(1);
		assertThat(updateReductionPlan.getPriceOtherReductions().get(0).getId()).isEqualTo(1);
	}

	@Test
	@DisplayName("Should delete reduction plan")
	void testDelete() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = manager.getById(reductionPlans.get(0).getId());
		int id = reductionPlan.getId();
		assertThat(manager.getAll()).hasSize(1);
		ReductionPlan existingReductionPlan = manager.getById(reductionPlan.getId());

		manager.delete(existingReductionPlan);
		ReductionPlan deletedReductionPlan = reductionPlanRepository.findByDeleted(true).get(0);
		assertThat(deletedReductionPlan.isDeleted()).isTrue();
		assertThat(manager.getExamReductionsByReductionPlanId(id).size()).isEqualTo(1);
		assertThat(manager.getOperationReductionsByReductionPlanId(id).size()).isEqualTo(1);
		assertThat(manager.getMedicalReductionsByReductionPlanId(id).size()).isEqualTo(1);
		assertThat(manager.getPriceOtherReductionsByReductionPlanId(id).size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Should get exam reduction by reduction plan")
	void testGetExamReductionByReductionPlanId() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = reductionPlans.get(0);

		List<ExamReduction> existingExamReductionList = manager.getExamReductionsByReductionPlanId(reductionPlan.getId());

		assertThat(existingExamReductionList.size()).isEqualTo(1);
		assertThat(existingExamReductionList.get(0).getExam().getCode()).isEqualTo("ZZ0");
	}

	@Test
	@DisplayName("Should delete a list of exam reduction")
	void testDeleteExamReductions() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		reductionPlans = repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = manager.getById(reductionPlans.get(0).getId());
		reductionPlan.getExamReductions().clear();

		assertThat(reductionPlan.getExamReductions().size()).isEqualTo(0);
		assertThat(manager.getExamReductionsByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should get medical reduction by reduction plan")
	void testGetMedicalReductionsByReductionPlanId() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = reductionPlans.get(0);

		List<MedicalReduction> existingMedicalReductionList = manager.getMedicalReductionsByReductionPlanId(reductionPlan.getId());
		assertThat(existingMedicalReductionList.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Should delete a list of medical reduction")
	void testDeleteMedicalReductions() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		reductionPlans = repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = manager.getById(reductionPlans.get(0).getId());

		assertThat(manager.getMedicalReductionsByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(1);
		reductionPlan.getMedicalReductions().clear();
		repository.saveAndFlush(reductionPlan);

		assertThat(reductionPlan.getMedicalReductions().size()).isEqualTo(0);
		assertThat(manager.getMedicalReductionsByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should get operation reduction by reduction plan")
	void testGetOperationReductionsByReductionPlanId() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);ReductionPlan reductionPlan = reductionPlans.get(0);
		repository.saveAllAndFlush(reductionPlans);

		List<OperationReduction> existingOperationReductionList = manager.getOperationReductionsByReductionPlanId(reductionPlan.getId());
		assertThat(existingOperationReductionList.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Should delete a list of operation reduction")
	void testDeleteOperationReductions() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		reductionPlans = repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = manager.getById(reductionPlans.get(0).getId());

		assertThat(manager.getOperationReductionsByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(1);
		assertThat(reductionPlan.getOperationReductions().size()).isEqualTo(1);
		reductionPlan.getOperationReductions().clear();
		repository.saveAndFlush(reductionPlan);
		assertThat(reductionPlan.getOperationReductions().size()).isEqualTo(0);
		assertThat(manager.getOperationReductionsByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should get price other reduction by reduction plan")
	void testGetPricesOtherReductionsByReductionPlanId() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		reductionPlans = repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = manager.getById(reductionPlans.get(0).getId());

		List<PriceOtherReduction> existingPriceOtherReductionList = manager.getPriceOtherReductionsByReductionPlanId(reductionPlan.getId());
		assertThat(existingPriceOtherReductionList.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Should delete a list of price other reduction")
	void testDeletePriceOtherReductions() throws Exception {
		ReductionPlanDataGenerate generate = new ReductionPlanDataGenerate();
		List<ReductionPlan> reductionPlans = generate.generateReductionPlanFixtures(1, null);
		reductionPlans = repository.saveAllAndFlush(reductionPlans);
		ReductionPlan reductionPlan = manager.getById(reductionPlans.get(0).getId());

		assertThat(reductionPlan.getPriceOtherReductions().size()).isEqualTo(1);
		assertThat(manager.getPriceOtherReductionsByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(1);
		reductionPlan.getPriceOtherReductions().clear();
		assertThat(reductionPlan.getPriceOtherReductions().size()).isEqualTo(0);
		assertThat(manager.getPriceOtherReductionsByReductionPlanId(reductionPlan.getId()).size()).isEqualTo(0);
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

				ReductionPlan reductionPlan = new ReductionPlan(description, BigDecimal.valueOf(opRate), BigDecimal.valueOf(medRate), BigDecimal.valueOf(examRate), BigDecimal.valueOf(otherRate));
				List<ExamReduction> examReductions = new ArrayList<>();
				List<MedicalReduction> medicalReductions = new ArrayList<>();
				List<OperationReduction> operationReductions = new ArrayList<>();
				List<PriceOtherReduction> priceOtherReductions = new ArrayList<>();

				try {
					ExamType testExamType = new TestExamType().setup(false);
					testExamType.setCode(testExamType.getCode() + i);
					testExamType.setDescription(testExamType.getDescription() + i);
					testExamType = examTypeRepository.saveAndFlush(testExamType);
					Exam testExam = new TestExam().setup(testExamType, 1, true);
					testExam.setCode(testExam.getCode() + i);
					testExam.setDescription(testExam.getCode() + i);
					testExam = examRepository.saveAndFlush(testExam);
					ExamReduction examReduction = generateExamReductionFixture(testExam, reductionPlan);
					examReductions.add(examReduction);

					MedicalType testMedicalType = new TestMedicalType().setup(false);
					testMedicalType.setCode(testMedicalType.getCode() +i);
					medicalsTypeRepository.saveAndFlush(testMedicalType);
					Medical testMedical = new TestMedical().setup(testMedicalType, true);
					testMedical.setProdCode(testMedical.getProdCode() + i);
					testMedical.setDescription(testMedical.getDescription() + i);
					medicalsRepository.saveAndFlush(testMedical);
					MedicalReduction medicalReduction = generateMedicalReductionFixture(testMedical, reductionPlan);
					medicalReductions.add(medicalReduction);

					OperationType testOperationType = new TestOperationType().setup(false);
					testOperationType.setCode(testOperationType.getCode() + i);
					operationTypeRepository.saveAndFlush(testOperationType);
					Operation testOperation = new TestOperation().setup(testOperationType, true);
					testOperation.setCode(testOperation.getCode() + i);
					testOperation.setDescription(testOperation.getDescription() + i);
					operationRepository.saveAndFlush(testOperation);
					OperationReduction operationReduction = generateOperationReductionFixture(testOperation, reductionPlan);
					operationReductions.add(operationReduction);

					TestPricesOthers testPricesOthers = new TestPricesOthers();
					PricesOthers pricesOthers = testPricesOthers.setup(false);
					priceOthersRepository.saveAndFlush(pricesOthers);
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
				, exam, BigDecimal.valueOf(1.0));
		}

		public OperationReduction generateOperationReductionFixture(Operation operation, ReductionPlan reductionPlan) throws OHException {
			if (operation == null) {
				TestOperation testOperation = new TestOperation();
				TestOperationType testOperationType = new TestOperationType();
				operation = testOperation.setup(testOperationType.setup(false), false);
			}
			return new OperationReduction(reductionPlan != null ? reductionPlan : generateReductionPlanFixtures(1, null).get(0)
				, operation, BigDecimal.valueOf(1.0));
		}

		public MedicalReduction generateMedicalReductionFixture(Medical medical, ReductionPlan reductionPlan) throws OHException {
			if (medical == null) {
				TestMedical testMedical = new TestMedical();
				TestMedicalType testMedicalType = new TestMedicalType();
				medical = testMedical.setup(testMedicalType.setup(false), false);
			}
			return new MedicalReduction(reductionPlan != null ? reductionPlan : generateReductionPlanFixtures(1, null).get(0)
				, medical, BigDecimal.valueOf(1.0));
		}

		public PriceOtherReduction generatePriceOtherReductionFixture(PricesOthers pricesOthers, ReductionPlan reductionPlan) throws OHException {
			if (pricesOthers == null) {
				TestPricesOthers testPricesOthers = new TestPricesOthers();
				pricesOthers = testPricesOthers.setup(false);
			}
			return new PriceOtherReduction(reductionPlan != null ? reductionPlan : generateReductionPlanFixtures(1, null).get(0)
				, pricesOthers, BigDecimal.valueOf(1.0));
		}
	}
}