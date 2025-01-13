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
import org.isf.pricesothers.TestPricesOthers;
import org.isf.pricesothers.manager.PricesOthersManager;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.reductionplan.data.ReductionPlanDataGenerate;
import org.isf.reductionplan.model.PriceOtherReduction;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.reductionplan.service.PriceOtherReductionIoOperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PricesOthersReductionManagerTest extends OHCoreTestCase {

	@Autowired
	PriceOtherReductionIoOperationRepository repository;

	@Autowired
	PriceOtherReductionManager manager;

	@Autowired
	ReductionPlanManager reductionPlanManager;

	@Autowired
	PricesOthersManager pricesOthersManager;

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	@DisplayName("Should get price other reduction by reduction plan")
	void testGetByReductionPlan() throws Exception {
		TestPricesOthers testPricesOthers = new TestPricesOthers();
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = reductionPlanManager.save(reductionPlan);

		PriceOtherReduction priceOtherReduction = ReductionPlanDataGenerate.generatePriceOtherReductionFixture(null, reductionPlan);
		PricesOthers pricesOthers = pricesOthersManager.newOther(priceOtherReduction.getPricesOthers());
		priceOtherReduction.setPricesOthers(pricesOthers);
		repository.saveAndFlush(priceOtherReduction);

		PricesOthers pricesOthers1 = testPricesOthers.setup(false);
		pricesOthers1.setCode("testPriceOtherCode1");
		pricesOthers1.setDescription("test price other description 1");
		pricesOthers1 = pricesOthersManager.newOther(pricesOthers1);

		PriceOtherReduction priceOtherReduction1 = ReductionPlanDataGenerate.generatePriceOtherReductionFixture(pricesOthers1, reductionPlan);
		repository.saveAndFlush(priceOtherReduction1);

		List<PriceOtherReduction> existingPriceOtherReductionList = manager.getByReductionPlanId(reductionPlan.getId(), false);

		assertThat(existingPriceOtherReductionList.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Should save price other reduction")
	void testSave() throws Exception {
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = reductionPlanManager.save(reductionPlan);

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
	void testDelete() throws Exception {
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = reductionPlanManager.save(reductionPlan);

		PriceOtherReduction priceOtherReduction = ReductionPlanDataGenerate.generatePriceOtherReductionFixture(null, reductionPlan);
		PricesOthers pricesOthers = pricesOthersManager.newOther(priceOtherReduction.getPricesOthers());
		priceOtherReduction.setPricesOthers(pricesOthers);
		priceOtherReduction = manager.save(priceOtherReduction);

		PriceOtherReduction deletedPriceOtherReduction = manager.delete(priceOtherReduction);

		assertThat(deletedPriceOtherReduction.isDeleted()).isTrue();
		assertThat(manager.getByReductionPlanId(reductionPlan.getId(), false).size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should delete a list of price other reduction")
	void testDeleteBulk() throws Exception {
		TestPricesOthers testPricesOthers = new TestPricesOthers();
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = reductionPlanManager.save(reductionPlan);

		PriceOtherReduction priceOtherReduction = ReductionPlanDataGenerate.generatePriceOtherReductionFixture(null, reductionPlan);
		PricesOthers pricesOthers = pricesOthersManager.newOther(priceOtherReduction.getPricesOthers());
		priceOtherReduction.setPricesOthers(pricesOthers);

		PricesOthers pricesOthers1 = testPricesOthers.setup(false);
		pricesOthers1.setCode("testPriceOtherCode1");
		pricesOthers1.setDescription("test price other description 1");
		pricesOthers1 = pricesOthersManager.newOther(pricesOthers1);

		PriceOtherReduction priceOtherReduction1 = ReductionPlanDataGenerate.generatePriceOtherReductionFixture(pricesOthers1, reductionPlan);

		repository.saveAndFlush(priceOtherReduction);
		repository.saveAndFlush(priceOtherReduction1);

		List<PriceOtherReduction> priceOtherReductionList = manager.getByReductionPlanId(reductionPlan.getId(), false);

		manager.deleteBulk(priceOtherReductionList);

		List<PriceOtherReduction> deletedPriceOtherReductionList = manager.getByReductionPlanId(reductionPlan.getId(), false);

		assertThat(deletedPriceOtherReductionList.size()).isEqualTo(0);
	}
}
