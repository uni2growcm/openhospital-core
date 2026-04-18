/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
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
	private DeathReasonManager deathReasonManager;

	@Autowired
	private DeathReasonIoOperations deathReasonIoOperations;

	@Autowired
	private DeathReasonRepository deathReasonRepository;

	@BeforeAll
	static void setUpClass() {
		testDeathReason = new TestDeathReason();
	}

	@BeforeEach
	void setUp() throws Exception {
		cleanH2InMemoryDb();
	}

	// =======================
	// TEST CASES
	// =======================

	@Test
	void testGetAll() throws Exception {
		generateDeathReasons(10);
		List<DeathReason> deathReasons = deathReasonManager.getAll();

		assertThat(deathReasons).isNotNull();
		assertThat(deathReasons.size()).isEqualTo(10);
	}

	@Test
	@DisplayName("Should successfully add a death reason")
	void testAdd() throws OHServiceException {
		DeathReason dr = new DeathReason("BC001", "Description", false);
		DeathReason saved = deathReasonManager.add(dr);

		assertThat(saved).isNotNull();
		assertThat(saved.getTitle()).isEqualTo(dr.getTitle());
		assertThat(saved.getDescription()).isEqualTo(dr.getDescription());
		assertThat(saved.getDeleted()).isEqualTo(dr.getDeleted());
	}

	@Test
	@DisplayName("Retrieve death reason pages by title or description")
	void testGetByTitleOrDescriptionPageable() throws Exception {
		generateDeathReasons(20);

		Page<DeathReason> page = deathReasonManager.getByTitleOrDescriptionPageable("", 0, 4);

		assertThat(page).isNotNull();
		assertThat(page.getContent().size()).isEqualTo(4);
		assertThat(page.getTotalElements()).isEqualTo(20);
		assertThat(page.getTotalPages()).isEqualTo(5);
		assertThat(page.getSize()).isEqualTo(4);
	}

	@Test
	@DisplayName("Should successfully update a death reason")
	void testUpdate() throws OHException, OHServiceException {
		DeathReason dr = new DeathReason("CARD001", "Arrêt cardiaque", false);

		assertThatThrownBy(() -> deathReasonManager.update(dr))
			.isInstanceOf(OHServiceException.class);

		DeathReason saved = deathReasonIoOperations.add(dr);
		saved.setDescription("Updated");

		DeathReason updated = deathReasonManager.update(saved);
		assertThat(updated.getDescription()).isEqualTo("Updated");
	}

	@Test
	@DisplayName("Should successfully delete a death reason")
	void testDelete() throws OHException, OHServiceException {
		List<DeathReason> savedList = generateDeathReasons(1);
		DeathReason toDelete = deathReasonRepository.findByTitleAndDeleted(savedList.get(0).getTitle(), false);

		assertThat(toDelete).isNotNull();
		boolean deleted = deathReasonManager.delete(toDelete);
		assertThat(deleted).isTrue();
	}

	private List<DeathReason> generateDeathReasons(int size) {
		String codePrefix = "DTHR";
		String descPrefix = "Description for death reason";

		List<DeathReason> deathReasons = IntStream.range(0, size).mapToObj(i ->
			new DeathReason(codePrefix + i, descPrefix + i, false)
		).toList();

		return deathReasonRepository.saveAllAndFlush(deathReasons);
	}
}
