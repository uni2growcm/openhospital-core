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

import org.isf.OHCoreTestCase;
import org.isf.mortuary.model.BodyCompartment;
import org.isf.mortuary.service.BodyCompartmentIoOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

public class BodyCompartmentManagerTest  extends OHCoreTestCase {

	private static TestBodyCompartment testBodyCompartment;

	@Autowired
	BodyCompartmentManager bodyComportmentManager;

	@Autowired
	BodyCompartmentIoOperations bodyCompartmentIoOperations;

	@BeforeAll
	static void setUpClass() {
		testBodyCompartment = new TestBodyCompartment();
	}

	@BeforeEach
	void setUp() throws Exception {
		cleanH2InMemoryDb();
	}

	@Test
	void testGetByLabelPageable() throws Exception {
		int id = setupTestBodyCompartment(false);
		Page<BodyCompartment> bodyCompartment = bodyComportmentManager.getByLabelPageable("", 0, 1);
		assertThat(bodyCompartment).isNotNull();
		assertThat(bodyCompartment.getTotalElements()).isEqualTo(1);
		assertThat(bodyCompartment.getTotalPages()).isEqualTo(1);
		assertThat(bodyCompartment.getContent().get(0).getId()).isEqualTo(id);
	}

	@Test
	void testMgrUpdate() throws OHException, OHServiceException {
		BodyCompartment bodyCompartment = testBodyCompartment.setup(false);
		assertThatThrownBy(() -> bodyComportmentManager.update(bodyCompartment))
			.isInstanceOf(OHServiceException.class);
		BodyCompartment bodyCompartmentSaved = bodyCompartmentIoOperations.add(bodyCompartment);
		bodyCompartmentSaved.setDescription("Updated");
		BodyCompartment bodyCompartmentUpdated = bodyComportmentManager.update(bodyCompartmentSaved);
		assertThat(bodyCompartmentUpdated.getDescription()).isEqualTo("Updated");
	}

	private int setupTestBodyCompartment(boolean usingSet) throws OHException, OHServiceException {
		BodyCompartment bodyCompartment = testBodyCompartment.setup(usingSet);
		bodyCompartmentIoOperations.add(bodyCompartment);
		return bodyCompartment.getId();
	}
}