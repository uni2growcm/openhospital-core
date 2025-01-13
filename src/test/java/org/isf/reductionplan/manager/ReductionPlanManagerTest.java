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
import java.util.stream.IntStream;

import org.isf.OHCoreTestCase;
import org.isf.reductionplan.data.ReductionPlanDataGenerate;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.reductionplan.service.ReductionPlanIoOperations;
import org.isf.reductionplan.service.ReductionplanIoOperationRepository;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReductionPlanManagerTest extends OHCoreTestCase {

	@Autowired
	ReductionplanIoOperationRepository repository;

	@Autowired
	ReductionPlanManager manager;

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
		ReductionPlan reductionPlan = new ReductionPlan(description, 10, 10, 10, 10);

		repository.saveAndFlush(reductionPlan);

		ReductionPlan existingReductionPlans = manager.getByDescription(description, false);

		assertThat(existingReductionPlans).isNotNull();
		assertThat(existingReductionPlans.getDescription()).isEqualTo(description);
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

		ReductionPlan existingReductionPlan = manager.getByDescription(description, false);
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

		ReductionPlan existingReductionPlan = manager.getByDescription(description, false);
	}
}