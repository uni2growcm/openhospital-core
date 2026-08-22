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
package org.isf.partner.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.isf.OHCoreTestCase;
import org.isf.partner.model.Partner;
import org.isf.partnertype.manager.PartnerTypeBrowserManager;
import org.isf.partnertype.model.PartnerType;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PartnerBrowserManagerTest extends OHCoreTestCase {

	@Autowired
	private PartnerBrowserManager partnerBrowserManager;

	@Autowired
	private PartnerTypeBrowserManager partnerTypeBrowserManager;

	@Autowired
	private PatientIoOperationRepository patientIoOperationRepository;

	private final TestPatient testPatient = new TestPatient();

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	private PartnerType buildPartnerType(String code) throws OHServiceException {
		return partnerTypeBrowserManager.newPartnerType(new PartnerType(code, code));
	}

	@Test
	@DisplayName("Create, list, update and soft-delete a partner")
	void testCrud() throws OHServiceException {
		PartnerType type = buildPartnerType("COM");

		Partner saved = partnerBrowserManager.savePartner(
			new Partner("Commune de Test", type, "M. Dupont", "123456", "commune@test.org", "1 rue de la Mairie", null));

		assertThat(partnerBrowserManager.getPartners()).extracting(Partner::getName).contains("Commune de Test");

		saved.setPhone("654321");
		partnerBrowserManager.savePartner(saved);
		assertThat(partnerBrowserManager.getPartner(saved.getCode()).getPhone()).isEqualTo("654321");

		partnerBrowserManager.deletePartner(saved.getCode());
		assertThat(partnerBrowserManager.getPartners()).extracting(Partner::getName).doesNotContain("Commune de Test");
	}

	@Test
	@DisplayName("Reject a partner without a name or a type")
	void testRejectInvalid() throws OHServiceException {
		PartnerType type = buildPartnerType("ONG");

		assertThatThrownBy(() -> partnerBrowserManager.savePartner(new Partner(null, type, null, null, null, null, null)))
			.isInstanceOf(OHServiceException.class);
		assertThatThrownBy(() -> partnerBrowserManager.savePartner(new Partner("Sans type", null, null, null, null, null, null)))
			.isInstanceOf(OHServiceException.class);
	}

	@Test
	@DisplayName("getPartner throws a checked OHServiceException for an unknown partner")
	void testGetPartnerNotFound() {
		assertThatThrownBy(() -> partnerBrowserManager.getPartner(999999)).isInstanceOf(OHServiceException.class);
	}

	@Test
	@DisplayName("patientHasPartners reflects the patient-partner attachment")
	void testPatientHasPartners() throws Exception {
		PartnerType type = buildPartnerType("CIS");
		Partner partner = partnerBrowserManager.savePartner(new Partner("CIS Test", type, null, null, null, null, null));

		Patient patient = patientIoOperationRepository.save(testPatient.setup(false));
		assertThat(partnerBrowserManager.patientHasPartners(patient.getCode())).isFalse();

		patient.addPartner(partner);
		patientIoOperationRepository.save(patient);

		assertThat(partnerBrowserManager.patientHasPartners(patient.getCode())).isTrue();
	}
}
