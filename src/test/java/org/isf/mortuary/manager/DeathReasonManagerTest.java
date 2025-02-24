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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.IntStream;

import org.isf.OHCoreTestCase;
import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.service.DeathReasonIoOperations;
import org.isf.mortuary.service.DeathReasonRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

public class DeathReasonManagerTest extends OHCoreTestCase {

	private static TestDeathReason testDeathReason;

	@Autowired
	DeathReasonManager deathReasonManager;

	@Autowired
	DeathReasonIoOperations deathReasonIoOperations;

	@Autowired
	DeathReasonRepository deathReasonRepository;

	@BeforeAll
	static void setUpClass() {
		testDeathReason = new TestDeathReason();
	}

	@BeforeEach
	void setUp() throws Exception {
		cleanH2InMemoryDb();
	}

	@Test
	void testGetAll() throws Exception {
		List<DeathReason> deathReasonsSaved = generateDeathReasons(10);
		List<DeathReason> deathReasons = deathReasonManager.getAll();
		assertThat(deathReasons).isNotNull();
		assertThat(deathReasons.size()).isEqualTo(10);
	}

	@Test
	@DisplayName("Should successfully add a death reason")
	void testAdd() throws OHServiceException {
		DeathReason deathReason = new DeathReason("BC001", "Description", false);
		DeathReason deathReasonSaved = deathReasonManager.add(deathReason);
		assertThat(deathReason.getTitle()).isEqualTo(deathReasonSaved.getTitle());
		assertThat(deathReason.getDescription()).isEqualTo(deathReasonSaved.getDescription());
		assertThat(deathReason.getDeleted()).isEqualTo(deathReasonSaved.getDeleted());
	}

	@Test
	@DisplayName("It should be possible to retrieve death reason pages based on the title or description")
	void testGetByCodeOrDescriptionPageable() throws Exception {
		List<DeathReason> deathReasons = generateDeathReasons(20);

		Page<DeathReason> deathReason = deathReasonManager.getByTitleOrDescriptionPageable("",0, 4);

		assertThat(deathReason).isNotNull();
		assertThat(deathReason.getContent().size()).isEqualTo(4);
		assertThat(deathReason.getTotalElements()).isEqualTo(20);
		assertThat(deathReason.getTotalPages()).isEqualTo(5);
		assertThat(deathReason.getSize()).isEqualTo(4);
	}

	@Test
	@DisplayName("Should successfully update a death reason")
	void testUpdate() throws OHException, OHServiceException {
		DeathReason deathReason = testDeathReason.setup(false);
		assertThatThrownBy(() -> deathReasonManager.update(deathReason))
			.isInstanceOf(OHServiceException.class);
		DeathReason deathReasonSaved = deathReasonIoOperations.add(deathReason);
		deathReasonSaved.setDescription("Updated");
		DeathReason deathReasonUpdated = deathReasonManager.update(deathReasonSaved);
		assertThat(deathReasonUpdated.getDescription()).isEqualTo("Updated");
	}

	@Test
	@DisplayName("Should successfully delete a death reason")
	void testDelete() throws OHException, OHServiceException {
		List<DeathReason> deathReasonSaved = generateDeathReasons(1);
		DeathReason deathReason = deathReasonRepository.findByTitleAndDeleted(deathReasonSaved.get(0).getTitle(), false);
		assertThat(deathReason).isNotNull();
		boolean isDeleted = deathReasonManager.delete(deathReason);
		assertThat(isDeleted).isEqualTo(true);
	}

	private List<DeathReason> generateDeathReasons(int size) {
		String codePrefix = "DTHR";
		String desc = "Description for death reason ";
		List<DeathReason> deathReasons = IntStream.range(0, size).mapToObj(i -> {
			return new DeathReason(
				codePrefix + i,
				desc + i,
				false
			);
		}).toList();

		return deathReasonRepository.saveAllAndFlush(deathReasons);
	}
}