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

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

import org.isf.OHCoreTestCase;
import org.isf.homevisit.manager.HomeVisitBrowserManager;
import org.isf.homevisit.model.HomeVisit;
import org.isf.homevisit.model.HomeVisitStatus;
import org.isf.homevisit.service.HomeVisitIoOperationRepository;
import org.isf.homevisit.service.HomeVisitIoOperations;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

class HomeVisitTests extends OHCoreTestCase {

	private static TestHomeVisit testHomeVisit;
	private static Patient testPatient;

	@Autowired
	HomeVisitIoOperations homeVisitIoOperations;

	@Autowired
	HomeVisitIoOperationRepository homeVisitIoOperationRepository;

	@Autowired
	HomeVisitBrowserManager homeVisitBrowserManager;

	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;

	@Autowired
	private EntityManager entityManager;

	@BeforeAll
	static void setUpClass() {
		testHomeVisit = new TestHomeVisit();
	}

	@BeforeEach
	void setUp() throws OHException {
		cleanH2InMemoryDb();

		testPatient = new Patient();
		testPatient.setFirstName("Mr");
		testPatient.setSecondName("Bobo");
		testPatient.setBirthDate(LocalDateTime.now().minusYears(30).toLocalDate());
		patientIoOperationRepository.saveAndFlush(testPatient);
	}

	@Test
	void testHomeVisitGets() throws Exception {
		int id = setupTestHomeVisit(false);
		checkHomeVisitIntoDb(id);
	}

	@Test
	void testHomeVisitSets() throws Exception {
		int id = setupTestHomeVisit(true);
		checkHomeVisitIntoDb(id);
	}

	@Test
	void testHomeVisitEquals() throws Exception {
		HomeVisit homeVisit1 = testHomeVisit.setup(testPatient, false);
		homeVisitIoOperationRepository.saveAndFlush(homeVisit1);

		HomeVisit homeVisit2 = new HomeVisit();
		homeVisit2.setId(homeVisit1.getId());
		assertThat(homeVisit1).isEqualTo(homeVisit2);

		HomeVisit homeVisit3 = new HomeVisit();
		homeVisit3.setId(homeVisit1.getId() + 1);
		assertThat(homeVisit1).isNotEqualTo(homeVisit3);

		assertThat(homeVisit1).isNotNull();
		assertThat(homeVisit1).isNotEqualTo("someString");
	}

	@Test
	void testHomeVisitHashCode() throws Exception {
		HomeVisit homeVisit = testHomeVisit.setup(testPatient, true);
		homeVisitIoOperationRepository.saveAndFlush(homeVisit);

		int hashCode = homeVisit.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + homeVisit.getId());
		assertThat(homeVisit.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void testHomeVisitToString() throws Exception {
		HomeVisit homeVisit = testHomeVisit.setup(testPatient, false);
		homeVisit.setVisitStartDate(LocalDateTime.of(2026, 12, 15, 10, 0, 0));
		assertThat(homeVisit.toString()).contains("Mr Bobo");
	}

	@Test
	void testIoGetAllActive() throws Exception {
		int id = setupTestHomeVisit(false);
		HomeVisit foundHomeVisit = homeVisitIoOperationRepository.findById(id).orElse(null);
		assertThat(foundHomeVisit).isNotNull();

		Page<HomeVisit> result = homeVisitIoOperations.getAllActive(org.springframework.data.domain.PageRequest.of(0, 10));

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getPurpose()).isEqualTo(foundHomeVisit.getPurpose());
	}

	@Test
	void testIoGetByPatient() throws Exception {
		setupTestHomeVisit(false);

		List<HomeVisit> result = homeVisitIoOperations.getByPatient(testPatient);

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getPurpose()).isEqualTo("Test purpose");
	}

	@Test
	void testIoGetHomeVisitById() throws Exception {
		int id = setupTestHomeVisit(false);

		java.util.Optional<HomeVisit> result = homeVisitIoOperations.getById(id);

		assertThat(result).isPresent();
		assertThat(result.get().getPurpose()).isEqualTo("Test purpose");
	}

	@Test
	void testIoGetByStatus() throws Exception {
		setupTestHomeVisit(false);

		Page<HomeVisit> result = homeVisitIoOperations.getByStatus(HomeVisitStatus.PLANNED, org.springframework.data.domain.PageRequest.of(0, 10));

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getStatus()).isEqualTo(HomeVisitStatus.PLANNED);
	}

	@Test
	void testIoGetByDateRange() throws Exception {
		setupTestHomeVisit(false);

		LocalDateTime startDate = LocalDateTime.of(2026, 12, 1, 0, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59, 59);

		Page<HomeVisit> result = homeVisitIoOperations.getByDateRange(startDate, endDate, org.springframework.data.domain.PageRequest.of(0, 10));

		assertThat(result.getContent()).hasSize(1);
	}

	@Test
	void testIoSaveNewHomeVisit() throws Exception {
		HomeVisit homeVisit = testHomeVisit.setup(testPatient, true);
		HomeVisit saved = homeVisitIoOperations.save(homeVisit);
		assertThat(saved.getId()).isPositive();
		assertThat(saved.getPurpose()).isEqualTo("Test purpose");
		assertThat(saved.getActive()).isEqualTo(1);
	}

	@Test
	void testIoUpdateHomeVisit() throws Exception {
		int id = setupTestHomeVisit(false);
		HomeVisit foundHomeVisit = homeVisitIoOperationRepository.findById(id).orElse(null);
		assertThat(foundHomeVisit).isNotNull();

		foundHomeVisit.setPurpose("Updated purpose");
		HomeVisit updated = homeVisitIoOperations.save(foundHomeVisit);

		assertThat(updated.getPurpose()).isEqualTo("Updated purpose");
	}

	@Test
	void testIoUpdateStatus() throws Exception {
		int id = setupTestHomeVisit(false);

		homeVisitIoOperations.updateStatus(id, HomeVisitStatus.COMPLETED);
		entityManager.flush();
		entityManager.clear();

		java.util.Optional<HomeVisit> updated = homeVisitIoOperations.getById(id);
		assertThat(updated).isPresent();
		assertThat(updated.get().getStatus()).isEqualTo(HomeVisitStatus.COMPLETED);
	}

	@Test
	void testIoCompleteVisit() throws Exception {
		int id = setupTestHomeVisit(false);

		LocalDateTime endDate = LocalDateTime.now();
		homeVisitIoOperations.completeVisit(id, endDate);
		entityManager.flush();
		entityManager.clear();

		java.util.Optional<HomeVisit> updated = homeVisitIoOperations.getById(id);
		assertThat(updated).isPresent();
		assertThat(updated.get().getStatus()).isEqualTo(HomeVisitStatus.COMPLETED);
		assertThat(updated.get().getVisitEndDate()).isNotNull();
	}

	@Test
	void testIoSoftDeleteHomeVisit() throws Exception {
		int id = setupTestHomeVisit(false);

		java.util.Optional<HomeVisit> beforeDelete = homeVisitIoOperations.getById(id);
		assertThat(beforeDelete).isPresent();

		homeVisitIoOperations.softDelete(id);
		entityManager.flush();
		entityManager.clear();

		java.util.Optional<HomeVisit> afterDelete = homeVisitIoOperations.getById(id);
		assertThat(afterDelete).isEmpty();

		java.util.Optional<HomeVisit> raw = homeVisitIoOperationRepository.findById(id);
		assertThat(raw).isPresent();
		assertThat(raw.get().getActive()).isEqualTo(0);
	}

	@Test
	void testMgrGetHomeVisits() throws Exception {
		setupTestHomeVisit(false);

		Page<HomeVisit> result = homeVisitBrowserManager.getHomeVisits(0, 10);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getPurpose()).isEqualTo("Test purpose");
	}

	@Test
	void testMgrGetHomeVisitById() throws Exception {
		int id = setupTestHomeVisit(false);

		HomeVisit result = homeVisitBrowserManager.getHomeVisit(id);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(id);
	}

	@Test
	void testMgrGetHomeVisit_shouldThrowWhenNotFound() throws Exception {
		assertThatThrownBy(() -> homeVisitBrowserManager.getHomeVisit(9999))
			.isInstanceOf(EntityNotFoundException.class)
			.hasMessageContaining("angal.homevisit.notfound.msg");
	}

	@Test
	void testMgrGetHomeVisitsByPatient() throws Exception {
		setupTestHomeVisit(false);

		List<HomeVisit> result = homeVisitBrowserManager.getHomeVisitsByPatient(testPatient);

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getPurpose()).isEqualTo("Test purpose");
	}

	@Test
	void testMgrGetHomeVisitsByStatus() throws Exception {
		setupTestHomeVisit(false);

		Page<HomeVisit> result = homeVisitBrowserManager.getHomeVisitsByStatus(HomeVisitStatus.PLANNED, 0, 10);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getStatus()).isEqualTo(HomeVisitStatus.PLANNED);
	}

	@Test
	void testMgrSaveNewHomeVisit() throws Exception {
		HomeVisit homeVisit = testHomeVisit.setup(testPatient, true);

		HomeVisit saved = homeVisitBrowserManager.saveHomeVisit(homeVisit);

		assertThat(saved.getId()).isPositive();
		checkHomeVisitIntoDb(saved.getId());
	}

	@Test
	void testMgrUpdateHomeVisit() throws Exception {
		int id = setupTestHomeVisit(false);
		HomeVisit foundHomeVisit = homeVisitIoOperationRepository.findById(id).orElse(null);
		assertThat(foundHomeVisit).isNotNull();

		foundHomeVisit.setPurpose("Updated purpose");
		HomeVisit updated = homeVisitBrowserManager.saveHomeVisit(foundHomeVisit);

		assertThat(updated.getPurpose()).isEqualTo("Updated purpose");
	}

	@Test
	void testMgrUpdateHomeVisitStatus() throws Exception {
		int id = setupTestHomeVisit(false);

		homeVisitBrowserManager.updateHomeVisitStatus(id, HomeVisitStatus.COMPLETED);
		entityManager.flush();
		entityManager.clear();

		HomeVisit updated = homeVisitBrowserManager.getHomeVisit(id);
		assertThat(updated.getStatus()).isEqualTo(HomeVisitStatus.COMPLETED);
	}

	@Test
	void testMgrCompleteHomeVisit() throws Exception {
		int id = setupTestHomeVisit(false);

		homeVisitBrowserManager.completeHomeVisit(id);
		entityManager.flush();
		entityManager.clear();

		HomeVisit updated = homeVisitBrowserManager.getHomeVisit(id);
		assertThat(updated.getStatus()).isEqualTo(HomeVisitStatus.COMPLETED);
		assertThat(updated.getVisitEndDate()).isNotNull();
	}

	@Test
	void testMgrCancelHomeVisit() throws Exception {
		int id = setupTestHomeVisit(false);

		homeVisitBrowserManager.cancelHomeVisit(id);
		entityManager.flush();
		entityManager.clear();

		HomeVisit updated = homeVisitBrowserManager.getHomeVisit(id);
		assertThat(updated.getStatus()).isEqualTo(HomeVisitStatus.CANCELLED);
	}

	@Test
	void testMgrPostponeHomeVisit() throws Exception {
		int id = setupTestHomeVisit(false);
		LocalDateTime newDate = LocalDateTime.of(2026, 12, 20, 14, 0, 0);

		homeVisitBrowserManager.postponeHomeVisit(id, newDate);
		entityManager.flush();
		entityManager.clear();

		HomeVisit updated = homeVisitBrowserManager.getHomeVisit(id);
		assertThat(updated.getStatus()).isEqualTo(HomeVisitStatus.POSTPONED);
		assertThat(updated.getNextVisitDate()).isEqualTo(newDate);
	}

	@Test
	void testMgrDeleteHomeVisit() throws Exception {
		int id = setupTestHomeVisit(false);

		homeVisitBrowserManager.deleteHomeVisit(id);
		entityManager.flush();
		entityManager.clear();

		assertThatThrownBy(() -> homeVisitBrowserManager.getHomeVisit(id))
			.isInstanceOf(EntityNotFoundException.class);

		java.util.Optional<HomeVisit> raw = homeVisitIoOperationRepository.findById(id);
		assertThat(raw).isPresent();
		assertThat(raw.get().getActive()).isEqualTo(0);
	}

	private int setupTestHomeVisit(boolean usingSet) throws OHException {
		HomeVisit homeVisit = testHomeVisit.setup(testPatient, usingSet);
		homeVisitIoOperationRepository.saveAndFlush(homeVisit);
		return homeVisit.getId();
	}

	private void checkHomeVisitIntoDb(int id) throws OHException {
		HomeVisit foundHomeVisit = homeVisitIoOperationRepository.findById(id).orElse(null);
		assertThat(foundHomeVisit).isNotNull();
		testHomeVisit.check(foundHomeVisit);
	}
}