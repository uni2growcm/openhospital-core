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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.exa.TestExam;
import org.isf.exa.model.Exam;
import org.isf.exa.service.ExamIoOperationRepository;
import org.isf.exatype.TestExamType;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.priceslist.model.Price;
import org.isf.reductionplan.model.ExamReduction;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link ReductionPlanManager}, in particular the automatic-discount price-calculation
 * methods ({@code getExamPrice}/{@code getMedicalPrice}/{@code getOperationPrice}/{@code getOtherPrice})
 * which have no prior implementation to port from and are the core of this feature.
 */
class ReductionPlanManagerTest extends OHCoreTestCase {

	@Autowired
	private ReductionPlanManager reductionPlanManager;

	@Autowired
	private ExamIoOperationRepository examIoOperationRepository;

	@Autowired
	private ExamTypeIoOperationRepository examTypeIoOperationRepository;

	private final TestExam testExam = new TestExam();
	private final TestExamType testExamType = new TestExamType();

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	private ReductionPlan buildPlan(String description, double examRate) {
		ReductionPlan plan = new ReductionPlan();
		plan.setDescription(description);
		plan.setExamRate(BigDecimal.valueOf(examRate));
		plan.setMedicalRate(BigDecimal.valueOf(0));
		plan.setOperationRate(BigDecimal.valueOf(0));
		plan.setOtherRate(BigDecimal.valueOf(0));
		return plan;
	}

	@Test
	@DisplayName("Add, list, fetch by id/description a reduction plan")
	void testAddAndGetReductionPlan() throws OHServiceException {
		ReductionPlan saved = reductionPlanManager.add(buildPlan("Partner NGO 20%", 20));

		assertThat(saved.getId()).isPositive();
		assertThat(reductionPlanManager.getAll()).extracting(ReductionPlan::getId).contains(saved.getId());
		assertThat(reductionPlanManager.getById(saved.getId()).getDescription()).isEqualTo("Partner NGO 20%");
		assertThat(reductionPlanManager.getByDescription("Partner NGO 20%")).extracting(ReductionPlan::getId).contains(saved.getId());
	}

	@Test
	@DisplayName("Delete a reduction plan is a soft delete")
	void testDeleteReductionPlan() throws OHServiceException {
		ReductionPlan saved = reductionPlanManager.add(buildPlan("Staff discount", 10));

		reductionPlanManager.delete(saved);

		assertThat(reductionPlanManager.getById(saved.getId())).isNull();
		assertThat(reductionPlanManager.getAll()).extracting(ReductionPlan::getId).doesNotContain(saved.getId());
	}

	@Test
	@DisplayName("Reject a reduction plan with an out-of-range global category rate")
	void testRejectInvalidGlobalRate() {
		ReductionPlan plan = buildPlan("Invalid plan", -5);

		assertThatThrownBy(() -> reductionPlanManager.add(plan)).isInstanceOf(OHServiceException.class);
	}

	@Test
	@DisplayName("Reject a reduction plan with duplicate exam exceptions")
	void testRejectDuplicateExamException() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);

		ReductionPlan plan = buildPlan("Duplicate exam exceptions", 20);
		plan.setExamReductions(List.of(
			new ExamReduction(plan, exam, BigDecimal.valueOf(10)),
			new ExamReduction(plan, exam, BigDecimal.valueOf(15))
		));

		assertThatThrownBy(() -> reductionPlanManager.add(plan)).isInstanceOf(OHServiceException.class);
	}

	@Test
	@DisplayName("Reject a reduction plan with an exam exception rate of exactly 0")
	void testRejectZeroExceptionRate() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);

		ReductionPlan plan = buildPlan("Zero exception rate", 20);
		plan.setExamReductions(List.of(new ExamReduction(plan, exam, BigDecimal.ZERO)));

		assertThatThrownBy(() -> reductionPlanManager.add(plan)).isInstanceOf(OHServiceException.class);
	}

	@Test
	@DisplayName("getExamPrice returns the price unchanged when no reduction plan is assigned")
	void testGetExamPriceNoPlanAssigned() throws OHServiceException {
		Price price = new Price(null, "EXA", "EX1", "Exam 1", 100.0, true);

		Price result = reductionPlanManager.getExamPrice(price, 0);

		assertThat(result.getPrice()).isEqualTo(100.0);
	}

	@Test
	@DisplayName("getExamPrice returns the price unchanged when the reduction plan no longer exists")
	void testGetExamPriceUnknownPlan() throws OHServiceException {
		Price price = new Price(null, "EXA", "EX1", "Exam 1", 100.0, true);

		Price result = reductionPlanManager.getExamPrice(price, 999999);

		assertThat(result.getPrice()).isEqualTo(100.0);
	}

	@Test
	@DisplayName("getExamPrice applies the plan's exam category rate when the exam has no exception")
	void testGetExamPriceCategoryDefault() throws Exception {
		ExamType examType = examTypeIoOperationRepository.save(testExamType.setup(false));
		Exam exam = examIoOperationRepository.save(testExam.setup(examType, 1, false));

		ReductionPlan plan = reductionPlanManager.add(buildPlan("20 percent exam plan", 20));

		Price price = new Price(null, "EXA", exam.getCode(), "Exam 1", 100.0, true);
		Price result = reductionPlanManager.getExamPrice(price, plan.getId());

		assertThat(result.getPrice()).isEqualTo(80.0);
	}

	@Test
	@DisplayName("getExamPrice applies the item-level exception rate instead of the category default")
	void testGetExamPriceItemException() throws Exception {
		ExamType examType = examTypeIoOperationRepository.save(testExamType.setup(false));
		Exam exam = examIoOperationRepository.save(testExam.setup(examType, 1, false));

		ReductionPlan plan = buildPlan("20 percent plan with 10 percent exception", 20);
		plan.setExamReductions(List.of(new ExamReduction(plan, exam, BigDecimal.valueOf(10))));
		plan = reductionPlanManager.add(plan);

		Price price = new Price(null, "EXA", exam.getCode(), "Exam 1", 100.0, true);
		Price result = reductionPlanManager.getExamPrice(price, plan.getId());

		assertThat(result.getPrice()).isEqualTo(90.0);
	}

	@Test
	@DisplayName("getExamPrice does not mutate the input Price, so applying it repeatedly does not compound the discount")
	void testGetExamPriceDoesNotCompound() throws Exception {
		ExamType examType = examTypeIoOperationRepository.save(testExamType.setup(false));
		Exam exam = examIoOperationRepository.save(testExam.setup(examType, 1, false));

		ReductionPlan plan = reductionPlanManager.add(buildPlan("20 percent exam plan", 20));

		Price catalogPrice = new Price(null, "EXA", exam.getCode(), "Exam 1", 100.0, true);

		Price firstPick = reductionPlanManager.getExamPrice(catalogPrice, plan.getId());
		Price secondPick = reductionPlanManager.getExamPrice(catalogPrice, plan.getId());

		assertThat(catalogPrice.getPrice()).isEqualTo(100.0);
		assertThat(firstPick.getPrice()).isEqualTo(80.0);
		assertThat(secondPick.getPrice()).isEqualTo(80.0);
		assertThat(firstPick).isNotSameAs(catalogPrice);
	}
}
