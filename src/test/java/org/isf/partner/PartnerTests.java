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

	// ============================================
	// MODEL TESTS
	// ============================================

	@Test
	void testPartnerGets() throws Exception {
		int id = setupTestPartner(false);
		checkPartnerIntoDb(id);
	}

	@Test
	void testPartnerSets() throws Exception {
		int id = setupTestPartner(true);
		checkPartnerIntoDb(id);
	}

	@Test
	void testPartnerEquals() throws Exception {
		Partner partner1 = testPartner.setup(false, partnerType);
		partnerIoOperationRepository.saveAndFlush(partner1);

		Partner partner2 = new Partner();
		partner2.setId(partner1.getId());
		assertThat(partner1).isEqualTo(partner2);

		Partner partner3 = new Partner();
		partner3.setId(partner1.getId() + 1);
		assertThat(partner1).isNotEqualTo(partner3);

		assertThat(partner1).isNotNull();
		assertThat(partner1).isNotEqualTo("someString");
	}

	@Test
	void testPartnerHashCode() throws Exception {
		Partner partner = testPartner.setup(true, partnerType);
		partnerIoOperationRepository.saveAndFlush(partner);

		int hashCode = partner.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + partner.getId());
		assertThat(partner.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void testPartnerToString() throws Exception {
		Partner partner = testPartner.setup(false, partnerType);
		partner.setName("Test Partner");
		assertThat(partner).hasToString("Test Partner");
	}

	// ============================================
	// IO (SERVICE) TESTS
	// ============================================

	@Test
	void testIoGetAllPartners() throws Exception {
		int id = setupTestPartner(false);
		Partner foundPartner = partnerIoOperationRepository.findById(id).orElse(null);
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
		int id = setupTestPartner(false);

		Optional<Partner> result = partnerIoOperations.getById(id);

		assertThat(result).isPresent();
		assertThat(result.get().getCode()).isEqualTo("PRT_TEST");
	}

	@Test
	void testIoGetPartnerByCode() throws Exception {
		setupTestPartner(false);

		Optional<Partner> result = partnerIoOperations.getByCode("PRT_TEST");

		assertThat(result).isPresent();
		assertThat(result.get().getName()).isEqualTo("Test Partner");
	}

	@Test
	void testIoGetPartnersByType() throws Exception {
		setupTestPartner(false);

		List<Partner> result = partnerIoOperations.getByType(partnerType);

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getCode()).isEqualTo("PRT_TEST");
	}

	@Test
	void testIoSaveNewPartner() throws Exception {
		Partner partner = testPartner.setup(true, partnerType);
		Partner saved = partnerIoOperations.save(partner);
		assertThat(saved.getId()).isPositive();
		assertThat(saved.getName()).isEqualTo("Test Partner");
		assertThat(saved.getActive()).isEqualTo(1);
	}

	@Test
	void testIoUpdatePartner() throws Exception {
		int id = setupTestPartner(false);
		Partner foundPartner = partnerIoOperationRepository.findById(id).orElse(null);
		assertThat(foundPartner).isNotNull();

		foundPartner.setName("Updated Partner");
		Partner updated = partnerIoOperations.save(foundPartner);

		assertThat(updated.getName()).isEqualTo("Updated Partner");
	}

	@Test
	void testIoSoftDeletePartner() throws Exception {
		int id = setupTestPartner(false);

		Optional<Partner> beforeDelete = partnerIoOperations.getById(id);
		assertThat(beforeDelete).isPresent();

		partnerIoOperations.softDelete(id);
		entityManager.flush();
		entityManager.clear();

		Optional<Partner> afterDelete = partnerIoOperations.getById(id);
		assertThat(afterDelete).isEmpty();

		Optional<Partner> raw = partnerIoOperationRepository.findById(id);
		assertThat(raw).isPresent();
		assertThat(raw.get().getActive()).isEqualTo(0);
	}

	@Test
	void testIoExistsByCode() throws Exception {
		setupTestPartner(false);

		boolean exists = partnerIoOperations.existsByCode("PRT_TEST");
		assertThat(exists).isTrue();

		boolean notExists = partnerIoOperations.existsByCode("NON_EXISTENT");
		assertThat(notExists).isFalse();
	}

	@Test
	void testIoSearchPartners() throws Exception {
		setupTestPartner(false);

		List<Partner> result = partnerIoOperations.search("Test Partner");

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getName()).isEqualTo("Test Partner");
	}

	// ============================================
	// MANAGER TESTS
	// ============================================

	@Test
	void testMgrGetPartners() throws Exception {
		setupTestPartner(false);

		List<Partner> result = partnerBrowserManager.getPartners();

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getName()).isEqualTo("Test Partner");
	}

	@Test
	void testMgrGetPartner() throws Exception {
		int id = setupTestPartner(false);

		Partner result = partnerBrowserManager.getPartner(id);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(id);
	}

	@Test
	void testMgrGetPartner_shouldThrowWhenNotFound() throws Exception {
		assertThatThrownBy(() -> partnerBrowserManager.getPartner(9999))
			.isInstanceOf(EntityNotFoundException.class)
			.hasMessageContaining("angal.partner.notfound.msg");
	}

	@Test
	void testMgrGetPartnerByCode() throws Exception {
		setupTestPartner(false);

		Optional<Partner> result = partnerBrowserManager.getPartnerByCode("PRT_TEST");

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

		assertThat(saved.getId()).isPositive();
		checkPartnerIntoDb(saved.getId());
	}

	@Test
	void testMgrUpdatePartner() throws Exception {
		int id = setupTestPartner(false);
		Partner foundPartner = partnerIoOperationRepository.findById(id).orElse(null);
		assertThat(foundPartner).isNotNull();

		foundPartner.setName("Updated Name");
		Partner updated = partnerBrowserManager.savePartner(foundPartner);

		assertThat(updated.getName()).isEqualTo("Updated Name");
	}

	@Test
	void testMgrDeletePartner() throws Exception {
		int id = setupTestPartner(false);

		partnerBrowserManager.deletePartner(id);
		entityManager.flush();
		entityManager.clear();

		assertThatThrownBy(() -> partnerBrowserManager.getPartner(id))
			.isInstanceOf(EntityNotFoundException.class)
			.hasMessageContaining("angal.partner.notfound.msg");

		Optional<Partner> raw = partnerIoOperationRepository.findById(id);
		assertThat(raw).isPresent();
		assertThat(raw.get().getActive()).isEqualTo(0);
	}

	@Test
	void testMgrSearchPartners() throws Exception {
		setupTestPartner(false);

		List<Partner> result = partnerBrowserManager.searchPartners("test");

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getName()).isEqualTo("Test Partner");
	}

	@Test
	void testMgrIsCodeUnique_true() throws Exception {
		boolean result = partnerBrowserManager.isCodeUnique("NEW_CODE", null);
		assertThat(result).isTrue();
	}

	@Test
	void testMgrIsCodeUnique_false() throws Exception {
		setupTestPartner(false);

		boolean result = partnerBrowserManager.isCodeUnique("PRT_TEST", null);

		assertThat(result).isFalse();
	}

	@Test
	void testMgrIsCodeUnique_trueWhenSamePartner() throws Exception {
		int id = setupTestPartner(false);

		boolean result = partnerBrowserManager.isCodeUnique("PRT_TEST", id);

		assertThat(result).isTrue();
	}

	// ============================================
	// HELPERS
	// ============================================

	private int setupTestPartner(boolean usingSet) throws OHException {
		Partner partner = testPartner.setup(usingSet, partnerType);
		partnerIoOperationRepository.saveAndFlush(partner);
		return partner.getId();
	}

	private void checkPartnerIntoDb(int id) throws OHException {
		Partner foundPartner = partnerIoOperationRepository.findById(id).orElse(null);
		assertThat(foundPartner).isNotNull();
		testPartner.check(foundPartner);
	}
}