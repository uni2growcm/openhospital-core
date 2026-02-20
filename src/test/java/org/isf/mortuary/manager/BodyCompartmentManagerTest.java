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
import org.isf.mortuary.model.BodyCompartment;
import org.isf.mortuary.service.BodyCompartmentIoOperations;
import org.isf.mortuary.service.BodyCompartmentRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

public class BodyCompartmentManagerTest  extends OHCoreTestCase {

	private static TestBodyCompartment testBodyCompartment;

	@Autowired
	BodyCompartmentManager bodyComportmentManager;

	@Autowired
	BodyCompartmentIoOperations bodyCompartmentIoOperations;

	@Autowired
	BodyCompartmentRepository bodyCompartmentRepository;

	@BeforeAll
	static void setUpClass() {
		testBodyCompartment = new TestBodyCompartment();
	}

	@BeforeEach
	void setUp() throws Exception {
		cleanH2InMemoryDb();
	}

	@Test
	@DisplayName("Should successfully add a body compartment")
	void testAdd() throws OHServiceException {
		BodyCompartment bodyCompartment = new BodyCompartment("BC001", "Description", false);
		BodyCompartment bodyCompartmentSaved = bodyComportmentManager.add(bodyCompartment);
		assertThat(bodyCompartment.getLabel()).isEqualTo(bodyCompartmentSaved.getLabel());
		assertThat(bodyCompartment.getDescription()).isEqualTo(bodyCompartmentSaved.getDescription());
		assertThat(bodyCompartment.isDeleted()).isEqualTo(bodyCompartmentSaved.isDeleted());
	}

	@Test
	@DisplayName("It should be possible to retrieve body compartment pages based on the label or description")
	void testGetByLabelOrDescriptionPageable() throws Exception {
		List<BodyCompartment> bodyCompartments = generateBodyCompartments(20);

		Page<BodyCompartment> bodyCompartment = bodyComportmentManager.getByLabelOrDescriptionPageable("", "",0, 4);

		assertThat(bodyCompartment).isNotNull();
		assertThat(bodyCompartment.getContent().size()).isEqualTo(4);
		assertThat(bodyCompartment.getTotalElements()).isEqualTo(20);
		assertThat(bodyCompartment.getTotalPages()).isEqualTo(5);
		assertThat(bodyCompartment.getSize()).isEqualTo(4);
	}

	@Test
	@DisplayName("Should successfully update a body compartment")
	void testUpdate() throws OHException, OHServiceException {
		BodyCompartment bodyCompartment = testBodyCompartment.setup();
		assertThatThrownBy(() -> bodyComportmentManager.update(bodyCompartment))
			.isInstanceOf(OHServiceException.class);
		BodyCompartment bodyCompartmentSaved = bodyCompartmentIoOperations.add(bodyCompartment);
		bodyCompartmentSaved.setDescription("Updated");
		BodyCompartment bodyCompartmentUpdated = bodyComportmentManager.update(bodyCompartmentSaved);
		assertThat(bodyCompartmentUpdated.getDescription()).isEqualTo("Updated");
	}

	@Test
	@DisplayName("Should successfully delete a body compartment")
	void testDelete() throws OHException, OHServiceException {
		String label = setupTestBodyCompartment(false);
		BodyCompartment bodyCompartment = bodyCompartmentRepository.findByLabelAndDeleted(label, false);
		assertThat(bodyCompartment).isNotNull();
		boolean isDeleted = bodyComportmentManager.delete(bodyCompartment);
		assertThat(isDeleted).isEqualTo(true);
	}

	private String setupTestBodyCompartment(boolean usingSet) throws OHException, OHServiceException {
		BodyCompartment bodyCompartment = testBodyCompartment.setup();
		bodyCompartmentIoOperations.add(bodyCompartment);
		return bodyCompartment.getLabel();
	}

	private List<BodyCompartment> generateBodyCompartments(int size) {
		String labelPrefix = "BC";
		String desc = "Description for body compartment ";
		List<BodyCompartment> bodyCompartments = IntStream.range(0, size).mapToObj(i -> {
			return new BodyCompartment(
				labelPrefix + i,
				desc + i,
				false
			);
		}).toList();

		return bodyCompartmentRepository.saveAllAndFlush(bodyCompartments);
	}

	private static class TestBodyCompartment {

		private final String label = "BC001";
		private final String description = "Casier de la salle A";
		private final boolean deleted = false;

		public BodyCompartment setup() {
			BodyCompartment bodyCompartment = new BodyCompartment();
			bodyCompartment.setLabel(label);
			bodyCompartment.setDescription(description);
			bodyCompartment.setDeleted(deleted);
			return bodyCompartment;
		}

		public void check(BodyCompartment bodyCompartment) {
			assertThat(bodyCompartment.getLabel()).isEqualTo(label);
			assertThat(bodyCompartment.getDescription()).isEqualTo(description);
		}
	}
}