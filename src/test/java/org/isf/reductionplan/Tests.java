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

package org.isf.reductionplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.reductionplan.manager.ReductionplanBrowserManager;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.reductionplan.service.ReductionPlanIoOperations;
import org.isf.reductionplan.service.ReductionplanIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestsReductionplan testsReductionplan;

	@Autowired
	ReductionPlanIoOperations ioOperations;

	@Autowired
	ReductionplanIoOperationRepository repository;

	@Autowired
	ReductionplanBrowserManager manager;

	@BeforeAll
	static void setUpClass() {
		testsReductionplan = new TestsReductionplan();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testReductionplanGets() throws Exception {
		// given:
		int id = setupTestReductionplan(false);

		// then:
		checkReductionplanIntoDb(id);
	}

	@Test
	void testReductionplanSets() throws Exception {
		// given:
		int id = setupTestReductionplan(true);

		// then:
		checkReductionplanIntoDb(id);
	}

	@Test
	void testIoReductionplanGetAll() throws Exception {
		// given:
		ReductionPlan reductionplan = testsReductionplan.setup(false);
		repository.save(reductionplan);
		List<ReductionPlan> foundReductionPlan = ioOperations.getReductionplan();
		assertThat(foundReductionPlan).isNotNull();
		assertThat(foundReductionPlan.size()).isGreaterThan(0);
		assertThat(foundReductionPlan.size()).isEqualTo(1);
	}

	@Test
	void testFindById() throws Exception {
		// GIVEN: Save multiple ReductionPlans
		ReductionPlan reduction1 = new ReductionPlan("Plan 1", 10, 20, 30, 40);
		ReductionPlan reduction2 = new ReductionPlan("Plan 2", 15, 25, 35, 45);
		repository.save(reduction1);
		repository.save(reduction2);

		// Retrieve the generated IDs
		List<Integer> ids = List.of(reduction1.getId(), reduction2.getId());

		// WHEN: Call the findById method
		List<ReductionPlan> result = manager.getReductionplanByIds(ids);

		// THEN: Verify that the results are correct
		assertThat(result).isNotNull();
		assertThat(result.size()).isEqualTo(2);
		assertThat(result).extracting(ReductionPlan::getDescription)
						.containsExactlyInAnyOrder("Plan 1", "Plan 2");
	}

	@Test
	void testMgrGetReductionplanByDescription() throws Exception {
		// given: Initialize and save a ReductionPlan
		ReductionPlan reductionplan = testsReductionplan.setup(false);
		repository.save(reductionplan);

		// when: Use the manager method to search by description
		List<ReductionPlan> result = manager.getReductionplan(reductionplan.getDescription());

		// then: Verify that the expected ReductionPlan is in the results
		assertThat(result).isNotEmpty();
		assertThat(result.get(0).getDescription()).isEqualTo(reductionplan.getDescription());
	}

	@Test
	void testMgrUpdateReductionplan() throws Exception {
		int id = setupTestReductionplan(false);
		ReductionPlan foundReductionPlan = repository.findById(id).orElse(null);
		assertThat(foundReductionPlan).isNotNull();
		foundReductionPlan.setDescription("Updated Manager Description");
		ReductionPlan updatedReductionPlan = manager.updateReductionplan(foundReductionPlan);
		assertThat(updatedReductionPlan.getDescription()).isEqualTo("Updated Manager Description");
	}

	@Test
	void testMgrNewReductionplan() throws Exception {
		// given:
		ReductionPlan reductionplan = testsReductionplan.setup(true);

		// when:
		ReductionPlan newReductionPlan = manager.newReductionplan(reductionplan);

		// then:
		assertThat(newReductionPlan.getId()).isGreaterThan(0);
		checkReductionplanIntoDb(newReductionPlan.getId());
	}

	@Test
	void testMgrDeleteReductionplan() throws Exception {
		// given:
		int id = setupTestReductionplan(false);
		ReductionPlan foundReductionPlan = repository.findById(id).orElse(null);
		assertThat(foundReductionPlan).isNotNull();

		// when:
		manager.deleteReductionplan(foundReductionPlan);

		// then:
		assertThat(repository.existsById(id)).isFalse();
	}

	@Test
	void testMgrValidationCodeEmpty() throws Exception {
		assertThatThrownBy(() -> {

			ReductionPlan reductionplan = testsReductionplan.setup(true);
			reductionplan.setDescription("");
			manager.newReductionplan(reductionplan);
		}).isInstanceOf(OHDataValidationException.class)
						.has(new Condition<>(e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@Test
	void testMgrValidationDescriptionEmpty() throws Exception {
		assertThatThrownBy(() -> {
			ReductionPlan reductionplan = testsReductionplan.setup(true);
			reductionplan.setDescription("");
			manager.newReductionplan(reductionplan);
		}).isInstanceOf(OHDataValidationException.class)
						.has(new Condition<>(e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@Test
	void setTestsReductionplanToString() throws Exception {
		ReductionPlan reductionplan = new ReductionPlan(1, "TestDescription", 0.00f, 0.1f, 0.02f, 1.3f);
		assertThat(reductionplan).hasToString("TestDescription");
	}

	@Test
	void testReductionplanEquals() throws Exception {
		ReductionPlan reductionplan = new ReductionPlan(2, "TestDescription", 0.0f, 0.05f, 0.06f, 0.07f);

		assertThat(reductionplan)
						.isEqualTo(reductionplan)
						.isNotNull()
						.isNotEqualTo("someString");

		ReductionPlan reductionPlan1 = new ReductionPlan(3, "TestDescription", 0.0f, 0.05f, 0.06f, 0.07f);
		assertThat(reductionplan).isNotEqualTo(reductionPlan1);

		reductionPlan1.setId(reductionplan.getId());
		assertThat(reductionplan).isEqualTo(reductionPlan1);
	}

	@Test
	void testReductionplanHashCode() throws Exception {
		ReductionPlan reductionplan = testsReductionplan.setup(true);
		reductionplan.setId(1);
		int hashCode = reductionplan.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + 1);
		assertThat(reductionplan.hashCode()).isEqualTo(hashCode);
	}

	private int setupTestReductionplan(boolean usingSet) throws Exception {
		ReductionPlan reductionplan = testsReductionplan.setup(usingSet);
		repository.saveAndFlush(reductionplan);
		return reductionplan.getId();
	}

	private void checkReductionplanIntoDb(int id) throws OHServiceException {
		ReductionPlan foundReductionPlan = repository.findById(id).orElse(null);
		assertThat(foundReductionPlan).isNotNull();
		testsReductionplan.check(foundReductionPlan);
	}

	@Test
	public void testReductionplanUsingConstructor() {
		ReductionPlan reductionplan = testsReductionplan.setup(true);
		testsReductionplan.check(reductionplan);
	}


	@Test
	public void testReductionplanUsingSetters() {
		ReductionPlan reductionplan = testsReductionplan.setup(false);
		testsReductionplan.check(reductionplan);
	}
}