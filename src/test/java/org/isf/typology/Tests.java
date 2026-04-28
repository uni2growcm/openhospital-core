/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.typology;

import org.isf.OHCoreTestCase;
import org.isf.maternity.model.*;
import org.isf.typology.manager.TypologyBrowserManager;
import org.isf.typology.model.Family;
import org.isf.typology.model.Typology;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class Tests extends OHCoreTestCase {


		private static TestTypology testTypology;

		@Autowired
		TypologyBrowserManager manager;

		@BeforeAll
		static void setUpClass() {
			testTypology = new TestTypology();
		}

		@BeforeEach
		void setUp() {
			cleanH2InMemoryDb();
		}

		@Test
		void testDeliveryTypeCRUD() throws Exception {

			// CREATE
			Typology dt = testTypology.setupDeliveryType();
			dt = manager.newTypology(dt);

			assertThat(dt).isNotNull();
			assertThat(dt.getCode()).isNotNull();

			// READ by code
			Typology found =
				manager.getTypologyByCode(dt.getCode());

			assertThat(found).isNotNull();
			assertThat(found.getCode()).isEqualTo(dt.getCode());

			// UPDATE
			dt.setDescription("Updated");
			dt = manager.updateTypology(dt);

			assertThat(dt.getDescription()).isEqualTo("Updated");

			// EXISTS CHECK
			assertThat(
				manager.isCodePresent(dt.getCode())
			).isTrue();

			// READ ALL (optional but good coverage)
			Page<Typology> page = manager.getTypologies(0,1);

			assertThat(page).isNotNull();
			assertThat(page.getContent()).isNotEmpty();

			// READ ALL for a particular family
			assertThat(
				manager.getTypologies(Family.VISITTYPE)
			).isEmpty();

			assertThat(
				manager.getTypologies(Family.DELIVERYTYPE)
			).isNotEmpty();

			// DELETE
			manager.deleteTypology(dt);

			// VERIFY DELETION
			assertThat(
				manager.isCodePresent(dt.getCode())
			).isFalse();

			assertThat(
				manager.getTypologyByCode(dt.getCode())
			).isNull();
		}
	}