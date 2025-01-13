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
import org.isf.reductionplan.data.ReductionPlanDataGenerate;
import org.isf.reductionplan.model.ExamReduction;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.reductionplan.service.ExamReductionIoOperationsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ExamReductionManagerTest extends OHCoreTestCase {

	@Autowired
	ExamReductionIoOperationsRepository repository;

	@Autowired
	ExamReductionManager manager;

	@Autowired
	ReductionPlanManager reductionPlanManager;

	@Autowired
	ExamBrowsingManager examBrowsingManager;

	@Autowired
	ExamTypeBrowserManager examTypeBrowserManager;

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	@DisplayName("Should get exam reduction by reduction plan")
	void testGetByReductionPlan() throws Exception {
		TestExamType testExamType = new TestExamType();
		TestExam testExam = new TestExam();
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = reductionPlanManager.save(reductionPlan);

		ExamReduction examReduction = ReductionPlanDataGenerate.generateExamReductionFixture(null, reductionPlan);
		ExamType examType = examTypeBrowserManager.newExamType(examReduction.getExam().getExamtype());
		examReduction.getExam().setExamtype(examType);
		Exam exam = examBrowsingManager.newExam(examReduction.getExam());
		examReduction.setExam(exam);
		repository.saveAndFlush(examReduction);

		ExamType examType1 = testExamType.setup(false);
		examType1.setCode("A");
		examType1.setDescription("test description 1");
		examType1 = examTypeBrowserManager.newExamType(examType1);

		Exam exam1 = testExam.setup(examType1, 2, false);
		exam1.setCode("test exam code 1");
		exam1.setDescription("test exam description 1");
		exam1 = examBrowsingManager.newExam(exam1);

		ExamReduction examReduction1 = ReductionPlanDataGenerate.generateExamReductionFixture(exam1, reductionPlan);
		repository.saveAndFlush(examReduction1);

		List<ExamReduction> existingExamReductionList = manager.getByReductionPlanId(reductionPlan.getId(), false);

		assertThat(existingExamReductionList.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Should save exam reduction")
	void testSave() throws Exception {
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = reductionPlanManager.save(reductionPlan);

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
	void testDelete() throws Exception {
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = reductionPlanManager.save(reductionPlan);

		ExamReduction examReduction = ReductionPlanDataGenerate.generateExamReductionFixture(null, reductionPlan);
		ExamType examType = examTypeBrowserManager.newExamType(examReduction.getExam().getExamtype());
		examReduction.getExam().setExamtype(examType);
		Exam exam = examBrowsingManager.newExam(examReduction.getExam());
		examReduction.setExam(exam);
		examReduction = manager.save(examReduction);

		ExamReduction deletedExamReduction = manager.delete(examReduction);

		assertThat(deletedExamReduction.isDeleted()).isTrue();
		assertThat(manager.getByReductionPlanId(reductionPlan.getId(), false).size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should delete a list of exam reduction")
	void testDeleteBulk() throws Exception {
		TestExamType testExamType = new TestExamType();
		TestExam testExam = new TestExam();
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = reductionPlanManager.save(reductionPlan);

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

		repository.saveAndFlush(examReduction);
		repository.saveAndFlush(examReduction1);

		List<ExamReduction> examReductionList = manager.getByReductionPlanId(reductionPlan.getId(), false);

		manager.deleteBulk(examReductionList);

		List<ExamReduction> deletedExamReductionList = manager.getByReductionPlanId(reductionPlan.getId(), false);

		assertThat(deletedExamReductionList.size()).isEqualTo(0);
	}
}
