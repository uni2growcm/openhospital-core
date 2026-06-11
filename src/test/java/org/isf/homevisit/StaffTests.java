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
package org.isf.homevisit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

import org.isf.OHCoreTestCase;
import org.isf.homevisit.manager.StaffBrowserManager;
import org.isf.homevisit.model.Staff;
import org.isf.homevisit.service.StaffIoOperationRepository;
import org.isf.homevisit.service.StaffIoOperations;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StaffTests extends OHCoreTestCase {

	private static TestStaff testStaff;

	@Autowired
	StaffIoOperations staffIoOperations;

	@Autowired
	StaffIoOperationRepository staffIoOperationRepository;

	@Autowired
	StaffBrowserManager staffBrowserManager;

	@Autowired
	private EntityManager entityManager;

	@BeforeAll
	static void setUpClass() {
		testStaff = new TestStaff();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	// ============================================
	// MODEL TESTS
	// ============================================

	@Test
	void testStaffGets() throws Exception {
		int id = setupTestStaff(false);
		checkStaffIntoDb(id);
	}

	@Test
	void testStaffSets() throws Exception {
		int id = setupTestStaff(true);
		checkStaffIntoDb(id);
	}

	@Test
	void testStaffEquals() throws Exception {
		Staff staff1 = testStaff.setup(false);
		staffIoOperationRepository.saveAndFlush(staff1);

		Staff staff2 = new Staff();
		staff2.setId(staff1.getId());
		assertThat(staff1).isEqualTo(staff2);

		Staff staff3 = new Staff();
		staff3.setId(staff1.getId() + 1);
		assertThat(staff1).isNotEqualTo(staff3);

		assertThat(staff1).isNotNull();
		assertThat(staff1).isNotEqualTo("someString");
	}

	@Test
	void testStaffHashCode() throws Exception {
		Staff staff = testStaff.setup(true);
		staffIoOperationRepository.saveAndFlush(staff);

		int hashCode = staff.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + staff.getId());
		assertThat(staff.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void testStaffToString() throws Exception {
		Staff staff = testStaff.setup(false);
		staff.setFirstName("John");
		staff.setLastName("Doe");
		assertThat(staff).hasToString("John Doe");
	}

	// ============================================
	// IO (SERVICE) TESTS
	// ============================================

	@Test
	void testIoGetAllActive() throws Exception {
		int id = setupTestStaff(false);
		Staff foundStaff = staffIoOperationRepository.findById(id).orElse(null);
		assertThat(foundStaff).isNotNull();

		List<Staff> staffList = staffIoOperations.getAllActive();

		assertThat(staffList).hasSize(1);
		assertThat(staffList.get(0).getFirstName()).isEqualTo(foundStaff.getFirstName());
	}

	@Test
	void testIoGetAllActive_shouldNotReturnSoftDeleted() throws Exception {
		Staff deleted = testStaff.setup(false);
		deleted.setActive(0);
		staffIoOperationRepository.saveAndFlush(deleted);

		List<Staff> staffList = staffIoOperations.getAllActive();

		assertThat(staffList).isEmpty();
	}

	@Test
	void testIoGetStaffById() throws Exception {
		int id = setupTestStaff(false);

		Optional<Staff> result = staffIoOperations.getById(id);

		assertThat(result).isPresent();
		assertThat(result.get().getCode()).isEqualTo("STF_TEST");
	}

	@Test
	void testIoGetStaffByCode() throws Exception {
		setupTestStaff(false);

		Optional<Staff> result = staffIoOperations.getByCode("STF_TEST");

		assertThat(result).isPresent();
		assertThat(result.get().getFirstName()).isEqualTo("John");
	}

	@Test
	void testIoSaveNewStaff() throws Exception {
		Staff staff = testStaff.setup(true);
		Staff saved = staffIoOperations.save(staff);
		assertThat(saved.getId()).isPositive();
		assertThat(saved.getFirstName()).isEqualTo("John");
		assertThat(saved.getActive()).isEqualTo(1);
	}

	@Test
	void testIoUpdateStaff() throws Exception {
		int id = setupTestStaff(false);
		Staff foundStaff = staffIoOperationRepository.findById(id).orElse(null);
		assertThat(foundStaff).isNotNull();

		foundStaff.setFirstName("Jane");
		Staff updated = staffIoOperations.save(foundStaff);

		assertThat(updated.getFirstName()).isEqualTo("Jane");
	}

	@Test
	void testIoSoftDeleteStaff() throws Exception {
		int id = setupTestStaff(false);

		Optional<Staff> beforeDelete = staffIoOperations.getById(id);
		assertThat(beforeDelete).isPresent();

		staffIoOperations.softDelete(id);
		entityManager.flush();
		entityManager.clear();

		Optional<Staff> afterDelete = staffIoOperations.getById(id);
		assertThat(afterDelete).isEmpty();

		Optional<Staff> raw = staffIoOperationRepository.findById(id);
		assertThat(raw).isPresent();
		assertThat(raw.get().getActive()).isEqualTo(0);
	}

	@Test
	void testIoExistsByCode() throws Exception {
		setupTestStaff(false);

		boolean exists = staffIoOperations.existsByCode("STF_TEST");
		assertThat(exists).isTrue();

		boolean notExists = staffIoOperations.existsByCode("NON_EXISTENT");
		assertThat(notExists).isFalse();
	}

	@Test
	void testIoSearchStaff() throws Exception {
		setupTestStaff(false);

		List<Staff> result = staffIoOperations.search("John");

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getFirstName()).isEqualTo("John");
	}

	// ============================================
	// MANAGER TESTS
	// ============================================

	@Test
	void testMgrGetStaff() throws Exception {
		setupTestStaff(false);

		List<Staff> result = staffBrowserManager.getStaff();

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getFirstName()).isEqualTo("John");
	}

	@Test
	void testMgrGetStaffById() throws Exception {
		int id = setupTestStaff(false);

		Staff result = staffBrowserManager.getStaff(id);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(id);
	}

	@Test
	void testMgrGetStaff_shouldThrowWhenNotFound() throws Exception {
		assertThatThrownBy(() -> staffBrowserManager.getStaff(9999))
			.isInstanceOf(EntityNotFoundException.class)
			.hasMessageContaining("angal.staff.notfound.msg");
	}

	@Test
	void testMgrGetStaffByCode() throws Exception {
		setupTestStaff(false);

		Optional<Staff> result = staffBrowserManager.getStaffByCode("STF_TEST");

		assertThat(result).isPresent();
		assertThat(result.get().getFirstName()).isEqualTo("John");
	}

	@Test
	void testMgrSaveNewStaff() throws Exception {
		Staff staff = testStaff.setup(true);

		Staff saved = staffBrowserManager.saveStaff(staff);

		assertThat(saved.getId()).isPositive();
		checkStaffIntoDb(saved.getId());
	}

	@Test
	void testMgrUpdateStaff() throws Exception {
		int id = setupTestStaff(false);
		Staff foundStaff = staffIoOperationRepository.findById(id).orElse(null);
		assertThat(foundStaff).isNotNull();

		foundStaff.setFirstName("Jane");
		Staff updated = staffBrowserManager.saveStaff(foundStaff);

		assertThat(updated.getFirstName()).isEqualTo("Jane");
	}

	@Test
	void testMgrDeleteStaff() throws Exception {
		int id = setupTestStaff(false);

		staffBrowserManager.deleteStaff(id);
		entityManager.flush();
		entityManager.clear();

		assertThatThrownBy(() -> staffBrowserManager.getStaff(id))
			.isInstanceOf(EntityNotFoundException.class);

		Optional<Staff> raw = staffIoOperationRepository.findById(id);
		assertThat(raw).isPresent();
		assertThat(raw.get().getActive()).isEqualTo(0);
	}

	@Test
	void testMgrSearchStaff() throws Exception {
		setupTestStaff(false);

		List<Staff> result = staffBrowserManager.searchStaff("John");

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getFirstName()).isEqualTo("John");
	}

	@Test
	void testMgrIsCodeUnique_true() throws Exception {
		boolean result = staffBrowserManager.isCodeUnique("NEW_CODE", null);
		assertThat(result).isTrue();
	}

	@Test
	void testMgrIsCodeUnique_false() throws Exception {
		setupTestStaff(false);

		boolean result = staffBrowserManager.isCodeUnique("STF_TEST", null);

		assertThat(result).isFalse();
	}

	@Test
	void testMgrIsCodeUnique_trueWhenSameStaff() throws Exception {
		int id = setupTestStaff(false);

		boolean result = staffBrowserManager.isCodeUnique("STF_TEST", id);

		assertThat(result).isTrue();
	}

	// ============================================
	// HELPERS
	// ============================================

	private int setupTestStaff(boolean usingSet) throws OHException {
		Staff staff = testStaff.setup(usingSet);
		staffIoOperationRepository.saveAndFlush(staff);
		return staff.getId();
	}

	private void checkStaffIntoDb(int id) throws OHException {
		Staff foundStaff = staffIoOperationRepository.findById(id).orElse(null);
		assertThat(foundStaff).isNotNull();
		testStaff.check(foundStaff);
	}
}