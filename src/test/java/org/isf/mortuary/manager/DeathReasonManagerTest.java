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

package org.isf.mortuary.manager;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.service.DeathReasonIoOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DeathReasonManagerTest extends OHCoreTestCase {

	private static TestDeathReason testDeathReason;

	@Autowired
	DeathReasonManager deathReasonManager;

	@Autowired
	DeathReasonIoOperations deathReasonIoOperations;

	@BeforeAll
	static void setUpClass() {
		testDeathReason = new TestDeathReason();
	}

	@BeforeEach
	void setUp() throws Exception {
		cleanH2InMemoryDb();
	}

	@Test
	void testGetById() throws Exception {
		int id = setupTestDeathReason(false);
		DeathReason foundDeathReason = deathReasonManager.getById(id);
		assertThat(foundDeathReason).isNotNull();
		assertThat(foundDeathReason.getId()).isEqualTo(id);
	}

	@Test
	void testGetAll() throws Exception {
		int id = setupTestDeathReason(false);
		List<DeathReason> deathReasons = deathReasonManager.getAll();
		assertThat(deathReasons).isNotNull();
		assertThat(deathReasons.size()).isEqualTo(1);
	}

	private int setupTestDeathReason(boolean usingSet) throws OHException, OHServiceException {
		DeathReason deathReason = testDeathReason.setup(usingSet);
		deathReasonIoOperations.add(deathReason);
		return deathReason.getId();
	}
}
