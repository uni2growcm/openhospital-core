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
import org.isf.operation.TestOperation;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.operation.model.Operation;
import org.isf.opetype.TestOperationType;
import org.isf.opetype.manager.OperationTypeBrowserManager;
import org.isf.opetype.model.OperationType;
import org.isf.reductionplan.data.ReductionPlanDataGenerate;
import org.isf.reductionplan.model.OperationReduction;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.reductionplan.service.OperationReductionIoOperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OperationReductionManagerTest extends OHCoreTestCase {

	@Autowired
	OperationReductionIoOperationRepository repository;

	@Autowired
	OperationReductionManager manager;

	@Autowired
	ReductionPlanManager reductionPlanManager;

	@Autowired
	OperationBrowserManager operationBrowserManager;

	@Autowired
	OperationTypeBrowserManager operationTypeBrowserManager;

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	@DisplayName("Should get operation reduction by reduction plan")
	void testGetByReductionPlan() throws Exception {
		TestOperationType testOperationType = new TestOperationType();
		TestOperation testOperation = new TestOperation();
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = reductionPlanManager.save(reductionPlan);

		OperationReduction operationReduction = ReductionPlanDataGenerate.generateOperationReductionFixture(null, reductionPlan);
		OperationType operationType = operationTypeBrowserManager.newOperationType(operationReduction.getOperation().getType());
		operationReduction.getOperation().setType(operationType);
		Operation operation = operationBrowserManager.newOperation(operationReduction.getOperation());
		operationReduction.setOperation(operation);
		repository.saveAndFlush(operationReduction);

		OperationType operationType1 = testOperationType.setup(false);
		operationType1.setCode("A");
		operationType1.setDescription("test description 1");
		operationType1 = operationTypeBrowserManager.newOperationType(operationType1);

		Operation operation1 = testOperation.setup(operationType1, false);
		operation1.setCode("test operation code 1");
		operation1.setDescription("test operation description 1");
		operation1 = operationBrowserManager.newOperation(operation1);

		OperationReduction operationReduction1 = ReductionPlanDataGenerate.generateOperationReductionFixture(operation1, reductionPlan);
		repository.saveAndFlush(operationReduction1);

		List<OperationReduction> existingOperationReductionList = manager.getByReductionPlanId(reductionPlan.getId(), false);

		assertThat(existingOperationReductionList.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Should save operation reduction")
	void testSave() throws Exception {
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = reductionPlanManager.save(reductionPlan);

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
	void testDelete() throws Exception {
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = reductionPlanManager.save(reductionPlan);

		OperationReduction operationReduction = ReductionPlanDataGenerate.generateOperationReductionFixture(null, reductionPlan);
		OperationType operationType = operationTypeBrowserManager.newOperationType(operationReduction.getOperation().getType());
		operationReduction.getOperation().setType(operationType);
		Operation operation = operationBrowserManager.newOperation(operationReduction.getOperation());
		operationReduction.setOperation(operation);
		operationReduction = manager.save(operationReduction);

		OperationReduction deletedOperationReduction = manager.delete(operationReduction);

		assertThat(deletedOperationReduction.isDeleted()).isTrue();
		assertThat(manager.getByReductionPlanId(reductionPlan.getId(), false).size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should delete a list of operation reduction")
	void testDeleteBulk() throws Exception {
		TestOperationType testOperationType = new TestOperationType();
		TestOperation testOperation = new TestOperation();
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = reductionPlanManager.save(reductionPlan);

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

		repository.saveAndFlush(operationReduction);
		repository.saveAndFlush(operationReduction1);

		List<OperationReduction> operationReductionList = manager.getByReductionPlanId(reductionPlan.getId(), false);

		manager.deleteBulk(operationReductionList);

		List<OperationReduction> deletedOperationReductionList = manager.getByReductionPlanId(reductionPlan.getId(), false);

		assertThat(deletedOperationReductionList.size()).isEqualTo(0);
	}
}
