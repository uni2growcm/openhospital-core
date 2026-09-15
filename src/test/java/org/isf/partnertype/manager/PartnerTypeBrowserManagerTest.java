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
package org.isf.partnertype.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.isf.OHCoreTestCase;
import org.isf.partnertype.model.PartnerType;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PartnerTypeBrowserManagerTest extends OHCoreTestCase {

	@Autowired
	private PartnerTypeBrowserManager partnerTypeBrowserManager;

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	@DisplayName("Create, list, update and soft-delete a partner type")
	void testCrud() throws OHServiceException {
		PartnerType saved = partnerTypeBrowserManager.newPartnerType(new PartnerType("COM", "Commune"));

		assertThat(partnerTypeBrowserManager.getPartnerTypes()).extracting(PartnerType::getCode).contains("COM");

		saved.setDescription("Commune urbaine");
		partnerTypeBrowserManager.updatePartnerType(saved);
		assertThat(partnerTypeBrowserManager.getPartnerTypes())
			.filteredOn(pt -> pt.getCode().equals("COM"))
			.extracting(PartnerType::getDescription)
			.containsExactly("Commune urbaine");

		partnerTypeBrowserManager.deletePartnerType(saved);
		assertThat(partnerTypeBrowserManager.getPartnerTypes()).extracting(PartnerType::getCode).doesNotContain("COM");
	}

	@Test
	@DisplayName("Reject a partner type without a code or description")
	void testRejectInvalid() {
		assertThatThrownBy(() -> partnerTypeBrowserManager.newPartnerType(new PartnerType("", "Sans code")))
			.isInstanceOf(OHServiceException.class);
		assertThatThrownBy(() -> partnerTypeBrowserManager.newPartnerType(new PartnerType("X", "")))
			.isInstanceOf(OHServiceException.class);
	}

	@Test
	@DisplayName("Reject a duplicate partner type code on insert")
	void testRejectDuplicateCode() throws OHServiceException {
		partnerTypeBrowserManager.newPartnerType(new PartnerType("ONG", "ONG"));

		assertThatThrownBy(() -> partnerTypeBrowserManager.newPartnerType(new PartnerType("ONG", "Une autre ONG")))
			.isInstanceOf(OHServiceException.class);
	}
}
