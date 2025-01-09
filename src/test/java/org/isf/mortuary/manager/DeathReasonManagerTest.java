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

import org.isf.OHCoreTestCase;
import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.service.DeathReasonIoOperations;
import org.isf.utils.exception.OHException;
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
		int id = setupTestMortuaryStays(false);
		DeathReason foundDeathReason = deathReasonManager.getById(id);
		assertThat(foundDeathReason).isNotNull();
		assertThat(foundDeathReason.getId()).isEqualTo(id);
	}

	private int setupTestMortuaryStays(boolean usingSet) throws OHException {
		DeathReason deathReason = testDeathReason.setup(usingSet);
		deathReasonIoOperations.add(deathReason);
		return deathReason.getId();
	}
}
