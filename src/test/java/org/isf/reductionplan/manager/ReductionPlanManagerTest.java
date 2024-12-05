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
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.reductionplan.service.ReductionPlanIoOperations;
import org.isf.reductionplan.service.ReductionplanIoOperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReductionPlanManagerTest extends OHCoreTestCase {

	@Autowired
	ReductionPlanIoOperations ioOperations;

	@Autowired
	ReductionplanIoOperationRepository repository;

	@Autowired
	ReductionplanManager manager;

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	private List<ReductionPlan> generateFixtures(int number) {
		return IntStream.range(0, number).mapToObj(i -> new ReductionPlan(
						"Description " + i,
						1.0 * i,
						2.0 * i,
						3.0 * i,
						3.0 * i
		)).toList();
	}

	@Test
	@DisplayName("Should get all reduction plans")
	void testGetAll() throws Exception {
		List<ReductionPlan> reductionPlans = generateFixtures(2);
		repository.saveAllAndFlush(reductionPlans);

		List<ReductionPlan> existingReductionPlan = manager.getAll();

		assertThat(existingReductionPlan.size()).isEqualTo(reductionPlans.size());
	}




}