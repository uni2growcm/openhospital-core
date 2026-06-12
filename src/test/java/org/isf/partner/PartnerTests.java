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
package org.isf.partner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

import org.isf.OHCoreTestCase;
import org.isf.partner.manager.PartnerBrowserManager;
import org.isf.partner.model.Partner;
import org.isf.partner.service.PartnerIoOperationRepository;
import org.isf.partner.service.PartnerIoOperations;
import org.isf.typology.model.Family;
import org.isf.typology.model.Typology;
import org.isf.typology.service.TypologyIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PartnerTests extends OHCoreTestCase {

	private static TestPartner testPartner;
	private static Typology partnerType;

	@Autowired
	PartnerIoOperations partnerIoOperations;

	@Autowired
	PartnerIoOperationRepository partnerIoOperationRepository;

	@Autowired
	PartnerBrowserManager partnerBrowserManager;

	@Autowired
	TypologyIoOperationRepository typologyIoOperationRepository;

	@Autowired
	private EntityManager entityManager;

	@BeforeAll
	static void setUpClass() {
		testPartner = new TestPartner();
		partnerType = new Typology();
		partnerType.setCode("PRT_TEST");
		partnerType.setDescription("Test Partner Type");
		partnerType.setFamily(Family.PARTNERTYPE);
		partnerType.setActive(1);
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
		typologyIoOperationRepository.saveAndFlush(partnerType);
	}

	@Test
	void testPartnerGets() throws Exception {
		Integer code = setupTestPartner(false);
		checkPartnerIntoDb(code);
	}

	@Test
	void testPartnerSets() throws Exception {
		Integer code = setupTestPartner(true);
		checkPartnerIntoDb(code);
	}

	@Test
	void testPartnerEquals() throws Exception {
		Partner partner1 = testPartner.setup(false, partnerType);
		partnerIoOperationRepository.saveAndFlush(partner1);

		Partner partner2 = new Partner();
		partner2.setCode(partner1.getCode());
		assertThat(partner1).isEqualTo(partner2);

		Partner partner3 = new Partner();
		partner3.setCode(partner1.getCode() + 1);
		assertThat(partner1).isNotEqualTo(partner3);

		assertThat(partner1).isNotNull();
		assertThat(partner1).isNotEqualTo("someString");
	}

	@Test
	void testPartnerHashCode() throws Exception {
		Partner partner = testPartner.setup(true, partnerType);
		partnerIoOperationRepository.saveAndFlush(partner);

		int hashCode = partner.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + (partner.getCode() == null ? 0 : partner.getCode()));
		assertThat(partner.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void testPartnerToString() throws Exception {
		Partner partner = testPartner.setup(false, partnerType);
		partner.setName("Test Partner");
		assertThat(partner).hasToString("Test Partner");
	}

	@Test
	void testIoGetAllPartners() throws Exception {
		Integer code = setupTestPartner(false);
		Partner foundPartner = partnerIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPartner).isNotNull();

		List<Partner> partners = partnerIoOperations.getAll();

		assertThat(partners).hasSize(1);
		assertThat(partners.get(0).getName()).isEqualTo(foundPartner.getName());
	}

	@Test
	void testIoGetAllPartners_shouldNotReturnSoftDeleted() throws Exception {
		Partner deleted = testPartner.setup(false, partnerType);
		deleted.setActive(0);
		partnerIoOperationRepository.saveAndFlush(deleted);

		List<Partner> partners = partnerIoOperations.getAll();

		assertThat(partners).isEmpty();
	}

	@Test
	void testIoGetPartnerById() throws Exception {
		Integer code = setupTestPartner(false);

		Optional<Partner> result = partnerIoOperations.getById(code);

		assertThat(result).isPresent();
		assertThat(result.get().getName()).isEqualTo("Test Partner");
	}

	@Test
	void testIoGetPartnersByType() throws Exception {
		setupTestPartner(false);

		List<Partner> result = partnerIoOperations.getByType(partnerType);

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getName()).isEqualTo("Test Partner");
	}

	@Test
	void testIoSaveNewPartner() throws Exception {
		Partner partner = testPartner.setup(true, partnerType);
		Partner saved = partnerIoOperations.save(partner);
		assertThat(saved.getCode()).isPositive();
		assertThat(saved.getName()).isEqualTo("Test Partner");
		assertThat(saved.getActive()).isEqualTo(1);
	}

	@Test
	void testIoUpdatePartner() throws Exception {
		Integer code = setupTestPartner(false);
		Partner foundPartner = partnerIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPartner).isNotNull();

		foundPartner.setName("Updated Partner");
		Partner updated = partnerIoOperations.save(foundPartner);

		assertThat(updated.getName()).isEqualTo("Updated Partner");
	}

	@Test
	void testIoSoftDeletePartner() throws Exception {
		Integer code = setupTestPartner(false);

		Optional<Partner> beforeDelete = partnerIoOperations.getById(code);
		assertThat(beforeDelete).isPresent();

		partnerIoOperations.softDelete(code);
		entityManager.flush();
		entityManager.clear();

		Optional<Partner> afterDelete = partnerIoOperations.getById(code);
		assertThat(afterDelete).isEmpty();

		Optional<Partner> raw = partnerIoOperationRepository.findById(code);
		assertThat(raw).isPresent();
		assertThat(raw.get().getActive()).isEqualTo(0);
	}

	@Test
	void testIoSearchPartners() throws Exception {
		setupTestPartner(false);

		List<Partner> resultByName = partnerIoOperations.search("Test Partner");
		assertThat(resultByName).hasSize(1);
		assertThat(resultByName.get(0).getName()).isEqualTo("Test Partner");

		Partner saved = partnerIoOperations.getAll().get(0);
		List<Partner> resultByCode = partnerIoOperations.search(saved.getCode().toString());
		assertThat(resultByCode).hasSize(1);
		assertThat(resultByCode.get(0).getCode()).isEqualTo(saved.getCode());
	}

	@Test
	void testMgrGetPartners() throws Exception {
		setupTestPartner(false);

		List<Partner> result = partnerBrowserManager.getPartners();

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getName()).isEqualTo("Test Partner");
	}

	@Test
	void testMgrGetPartner() throws Exception {
		Integer code = setupTestPartner(false);

		Partner result = partnerBrowserManager.getPartner(code);

		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo(code);
	}

	@Test
	void testMgrGetPartner_shouldThrowWhenNotFound() throws Exception {
		assertThatThrownBy(() -> partnerBrowserManager.getPartner(9999))
			.isInstanceOf(EntityNotFoundException.class)
			.hasMessageContaining("angal.partner.notfound.msg");
	}

	@Test
	void testMgrGetPartnerByCode() throws Exception {
		Integer code = setupTestPartner(false);

		Optional<Partner> result = partnerBrowserManager.getPartnerByCode(code);

		assertThat(result).isPresent();
		assertThat(result.get().getName()).isEqualTo("Test Partner");
	}

	@Test
	void testMgrGetPartnersByType() throws Exception {
		setupTestPartner(false);

		List<Partner> result = partnerBrowserManager.getPartnersByType(partnerType);

		assertThat(result).hasSize(1);
	}

	@Test
	void testMgrSaveNewPartner() throws Exception {
		Partner partner = testPartner.setup(true, partnerType);

		Partner saved = partnerBrowserManager.savePartner(partner);

		assertThat(saved.getCode()).isPositive();
		checkPartnerIntoDb(saved.getCode());
	}

	@Test
	void testMgrUpdatePartner() throws Exception {
		Integer code = setupTestPartner(false);
		Partner foundPartner = partnerIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPartner).isNotNull();

		foundPartner.setName("Updated Name");
		Partner updated = partnerBrowserManager.savePartner(foundPartner);

		assertThat(updated.getName()).isEqualTo("Updated Name");
	}

	@Test
	void testMgrDeletePartner() throws Exception {
		Integer code = setupTestPartner(false);

		partnerBrowserManager.deletePartner(code);
		entityManager.flush();
		entityManager.clear();

		assertThatThrownBy(() -> partnerBrowserManager.getPartner(code))
			.isInstanceOf(EntityNotFoundException.class)
			.hasMessageContaining("angal.partner.notfound.msg");

		Optional<Partner> raw = partnerIoOperationRepository.findById(code);
		assertThat(raw).isPresent();
		assertThat(raw.get().getActive()).isEqualTo(0);
	}

	@Test
	void testMgrSearchPartners() throws Exception {
		setupTestPartner(false);

		List<Partner> resultByName = partnerBrowserManager.searchPartners("test");
		assertThat(resultByName).hasSize(1);
		assertThat(resultByName.get(0).getName()).isEqualTo("Test Partner");

		Partner saved = partnerBrowserManager.getPartners().get(0);
		List<Partner> resultByCode = partnerBrowserManager.searchPartners(saved.getCode().toString());
		assertThat(resultByCode).hasSize(1);
		assertThat(resultByCode.get(0).getCode()).isEqualTo(saved.getCode());
	}

	private Integer setupTestPartner(boolean usingSet) throws OHException {
		Partner partner = testPartner.setup(usingSet, partnerType);
		partnerIoOperationRepository.saveAndFlush(partner);
		return partner.getCode();
	}

	private void checkPartnerIntoDb(Integer code) throws OHException {
		Partner foundPartner = partnerIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPartner).isNotNull();
		testPartner.check(foundPartner);
	}
}