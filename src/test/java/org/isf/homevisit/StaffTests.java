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

	@Test
	void testStaffGets() throws Exception {
		Integer code = setupTestStaff(false);
		checkStaffIntoDb(code);
	}

	@Test
	void testStaffSets() throws Exception {
		Integer code = setupTestStaff(true);
		checkStaffIntoDb(code);
	}

	@Test
	void testStaffEquals() throws Exception {
		Staff staff1 = testStaff.setup(false);
		staffIoOperationRepository.saveAndFlush(staff1);

		Staff staff2 = new Staff();
		staff2.setCode(staff1.getCode());
		assertThat(staff1).isEqualTo(staff2);

		Staff staff3 = new Staff();
		staff3.setCode(staff1.getCode() + 1);
		assertThat(staff1).isNotEqualTo(staff3);

		assertThat(staff1).isNotNull();
		assertThat(staff1).isNotEqualTo("someString");
	}

	@Test
	void testStaffHashCode() throws Exception {
		Staff staff = testStaff.setup(true);
		staffIoOperationRepository.saveAndFlush(staff);

		int hashCode = staff.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + (staff.getCode() == null ? 0 : staff.getCode()));
		assertThat(staff.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void testStaffToString() throws Exception {
		Staff staff = testStaff.setup(false);
		staff.setFirstName("Bobo");
		staff.setLastName("Mr");
		staff.setPosition("Head Nurse");
		assertThat(staff).hasToString("Bobo Mr");
	}

	@Test
	void testStaffToStringWithoutPosition() throws Exception {
		Staff staff = testStaff.setup(false);
		staff.setFirstName("Bobo");
		staff.setLastName("Mr");
		staff.setPosition(null);
		assertThat(staff.toString()).isEqualTo("Bobo Mr");
	}

	@Test
	void testIoGetAllActive() throws Exception {
		Integer code = setupTestStaff(false);
		Staff foundStaff = staffIoOperationRepository.findById(code).orElse(null);
		assertThat(foundStaff).isNotNull();

		List<Staff> staffList = staffIoOperations.getAllActive();

		assertThat(staffList).hasSize(1);
		assertThat(staffList.get(0).getFirstName()).isEqualTo(foundStaff.getFirstName());
		assertThat(staffList.get(0).getPosition()).isEqualTo(foundStaff.getPosition());
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
		Integer code = setupTestStaff(false);

		Optional<Staff> result = staffIoOperations.getById(code);

		assertThat(result).isPresent();
		assertThat(result.get().getFirstName()).isEqualTo("Bobo");
		assertThat(result.get().getPosition()).isEqualTo("Head Nurse");
	}

	@Test
	void testIoGetStaffByCode() throws Exception {
		Integer code = setupTestStaff(false);

		Optional<Staff> result = staffIoOperations.getById(code);

		assertThat(result).isPresent();
		assertThat(result.get().getFirstName()).isEqualTo("Bobo");
		assertThat(result.get().getPosition()).isEqualTo("Head Nurse");
	}

	@Test
	void testIoSaveNewStaff() throws Exception {
		Staff staff = testStaff.setup(true);
		Staff saved = staffIoOperations.save(staff);
		assertThat(saved.getCode()).isPositive();
		assertThat(saved.getFirstName()).isEqualTo("Bobo");
		assertThat(saved.getPosition()).isEqualTo("Head Nurse");
		assertThat(saved.getActive()).isEqualTo(1);
	}

	@Test
	void testIoUpdateStaff() throws Exception {
		Integer code = setupTestStaff(false);
		Staff foundStaff = staffIoOperationRepository.findById(code).orElse(null);
		assertThat(foundStaff).isNotNull();

		foundStaff.setFirstName("Jane");
		foundStaff.setPosition("Senior Nurse");
		Staff updated = staffIoOperations.save(foundStaff);

		assertThat(updated.getFirstName()).isEqualTo("Jane");
		assertThat(updated.getPosition()).isEqualTo("Senior Nurse");
	}

	@Test
	void testIoSoftDeleteStaff() throws Exception {
		Integer code = setupTestStaff(false);

		Optional<Staff> beforeDelete = staffIoOperations.getById(code);
		assertThat(beforeDelete).isPresent();

		staffIoOperations.softDelete(code);
		entityManager.flush();
		entityManager.clear();

		Optional<Staff> afterDelete = staffIoOperations.getById(code);
		assertThat(afterDelete).isEmpty();

		Optional<Staff> raw = staffIoOperationRepository.findById(code);
		assertThat(raw).isPresent();
		assertThat(raw.get().getActive()).isEqualTo(0);
	}

	@Test
	void testIoSearchStaffByPosition() throws Exception {
		setupTestStaff(false);
		List<Staff> resultByPosition = staffIoOperations.search("Head Nurse");
		assertThat(resultByPosition).hasSize(1);
		assertThat(resultByPosition.get(0).getPosition()).isEqualTo("Head Nurse");
	}

	@Test
	void testIoSearchStaffByPhone() throws Exception {
		setupTestStaff(false);
		List<Staff> resultByPhone = staffIoOperations.search("690001234");
		assertThat(resultByPhone).hasSize(1);
		assertThat(resultByPhone.get(0).getPhone()).isEqualTo("+237 690001234");
	}

	@Test
	void testIoSearchStaff() throws Exception {
		Integer code = setupTestStaff(false);

		List<Staff> resultByFirstName = staffIoOperations.search("Bobo");
		assertThat(resultByFirstName).hasSize(1);
		assertThat(resultByFirstName.get(0).getFirstName()).isEqualTo("Bobo");

		List<Staff> resultByCode = staffIoOperations.search(code.toString());
		assertThat(resultByCode).hasSize(1);
		assertThat(resultByCode.get(0).getCode()).isEqualTo(code);
	}

	@Test
	void testMgrGetStaff() throws Exception {
		setupTestStaff(false);

		List<Staff> result = staffBrowserManager.getStaff();

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getFirstName()).isEqualTo("Bobo");
		assertThat(result.get(0).getPosition()).isEqualTo("Head Nurse");
	}

	@Test
	void testMgrGetStaffById() throws Exception {
		Integer code = setupTestStaff(false);

		Staff result = staffBrowserManager.getStaff(code);

		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo(code);
		assertThat(result.getPosition()).isEqualTo("Head Nurse");
	}

	@Test
	void testMgrGetStaff_shouldThrowWhenNotFound() throws Exception {
		assertThatThrownBy(() -> staffBrowserManager.getStaff(9999))
			.isInstanceOf(EntityNotFoundException.class)
			.hasMessageContaining("angal.staff.notfound.msg");
	}

	@Test
	void testMgrSaveNewStaff() throws Exception {
		Staff staff = testStaff.setup(true);

		Staff saved = staffBrowserManager.saveStaff(staff);

		assertThat(saved.getCode()).isPositive();
		assertThat(saved.getPosition()).isEqualTo("Head Nurse");
		checkStaffIntoDb(saved.getCode());
	}

	@Test
	void testMgrUpdateStaff() throws Exception {
		Integer code = setupTestStaff(false);
		Staff foundStaff = staffIoOperationRepository.findById(code).orElse(null);
		assertThat(foundStaff).isNotNull();

		foundStaff.setFirstName("Jane");
		foundStaff.setPosition("Senior Nurse");
		Staff updated = staffBrowserManager.saveStaff(foundStaff);

		assertThat(updated.getFirstName()).isEqualTo("Jane");
		assertThat(updated.getPosition()).isEqualTo("Senior Nurse");
	}

	@Test
	void testMgrDeleteStaff() throws Exception {
		Integer code = setupTestStaff(false);

		staffBrowserManager.deleteStaff(code);
		entityManager.flush();
		entityManager.clear();

		assertThatThrownBy(() -> staffBrowserManager.getStaff(code))
			.isInstanceOf(EntityNotFoundException.class);

		Optional<Staff> raw = staffIoOperationRepository.findById(code);
		assertThat(raw).isPresent();
		assertThat(raw.get().getActive()).isEqualTo(0);
	}

	@Test
	void testMgrSearchStaffByPosition() throws Exception {
		setupTestStaff(false);
		List<Staff> resultByPosition = staffBrowserManager.searchStaff("Head Nurse");
		assertThat(resultByPosition).hasSize(1);
		assertThat(resultByPosition.get(0).getPosition()).isEqualTo("Head Nurse");
	}

	@Test
	void testMgrSearchStaff() throws Exception {
		Integer code = setupTestStaff(false);

		List<Staff> resultByFirstName = staffBrowserManager.searchStaff("Bobo");
		assertThat(resultByFirstName).hasSize(1);
		assertThat(resultByFirstName.get(0).getFirstName()).isEqualTo("Bobo");

		List<Staff> resultByCode = staffBrowserManager.searchStaff(code.toString());
		assertThat(resultByCode).hasSize(1);
		assertThat(resultByCode.get(0).getCode()).isEqualTo(code);
	}

	private Integer setupTestStaff(boolean usingSet) throws OHException {
		Staff staff = testStaff.setup(usingSet);
		staffIoOperationRepository.saveAndFlush(staff);
		return staff.getCode();
	}

	private void checkStaffIntoDb(Integer code) throws OHException {
		Staff foundStaff = staffIoOperationRepository.findById(code).orElse(null);
		assertThat(foundStaff).isNotNull();
		testStaff.check(foundStaff);
	}
}