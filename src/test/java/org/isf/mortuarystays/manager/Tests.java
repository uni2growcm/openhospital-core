/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.mortuarystays.manager;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.mortuarystays.model.MortuaryStays;
import org.isf.mortuarystays.service.MortuaryStaysIoOperationRepository;
import org.isf.mortuarystays.service.MortuaryStaysIoOperations;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class Tests extends OHCoreTestCase {

	private static MortuaryStayManagerTest testMortuaryStaysManager;

	@Autowired
	MortuaryStaysIoOperations mortuaryStaysIoOperations;
	@Autowired
	MortuaryStaysIoOperationRepository mortuaryStaysIoOperationRepository;
	@Autowired
	MortuaryStaysBrowserManager mortuaryStaysBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testMortuaryStaysManager = new MortuaryStayManagerTest();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testMortuaryStaysManagerGets() throws Exception {
		String code = setupTestMortuaryStays(false);
		checkMortuaryIntoDb(code);
	}

	@Test
	void testMgrGetMortuariesStays() throws Exception {
		String code = setupTestMortuaryStays(false);
		MortuaryStays foundMortuary = mortuaryStaysBrowserManager.getByCode(code);
		assertThat(foundMortuary).isNotNull();
		List<MortuaryStays> mortuaries = mortuaryStaysBrowserManager.getMortuariesStays();
		assertThat(mortuaries.get(mortuaries.size() - 1).getDescription()).isEqualTo(foundMortuary.getDescription());
	}

	@Test
	void testMgrUpdate() throws Exception {
		String code = setupTestMortuaryStays(false);
		MortuaryStays foundMortuary = mortuaryStaysBrowserManager.getByCode(code);
		assertThat(foundMortuary).isNotNull();
		foundMortuary.setDescription("Update");
		MortuaryStays updatedMortuary = mortuaryStaysBrowserManager.update(foundMortuary);
		assertThat(updatedMortuary).isNotNull();
		assertThat(updatedMortuary.getDescription()).isEqualTo("Update");
	}

	@Test
	void testMgrNewMortuaryStays() throws Exception {
		MortuaryStays mortuary = testMortuaryStaysManager.setup(true);
		assertThat(mortuaryStaysBrowserManager.newMortuaryStays(mortuary)).isNotNull();
		checkMortuaryIntoDb(mortuary.getCode());
	}

	@Test
	void testMgrIsCodePresent() throws Exception {
		String code = setupTestMortuaryStays(false);
		assertThat(mortuaryStaysBrowserManager.isCodePresent(code)).isTrue();
	}

	@Test
	void testMgrDelete() throws Exception {
		String code = setupTestMortuaryStays(false);
		MortuaryStays foundMortuary = mortuaryStaysIoOperations.getByCode(code);
		assertThat(foundMortuary).isNotNull();
		mortuaryStaysBrowserManager.delete(foundMortuary);
		assertThat(mortuaryStaysBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	void testMgrGetByCode() throws Exception {
		String code = setupTestMortuaryStays(false);
		MortuaryStays foundMortuary = mortuaryStaysBrowserManager.getByCode(code);
		assertThat(foundMortuary).isNotNull();
		assertThat(foundMortuary.getCode()).isEqualTo(code);
	}

	@Test
	void testMgrFindMortuaryNull() throws Exception {
		assertThatThrownBy(() -> mortuaryStaysBrowserManager.getByCode(null))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testMgrValidationCodeTooLong() throws Exception {
		MortuaryStays mortuary = testMortuaryStaysManager.setup(false);
		mortuary.setCode("thisIsACodeThatIsTooLong");
		assertThatThrownBy(() -> mortuaryStaysBrowserManager.newMortuaryStays(mortuary))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testMgrValidationDescriptionEmpty() throws Exception {
		String code = setupTestMortuaryStays(true);
		MortuaryStays mortuary = mortuaryStaysBrowserManager.getByCode(code);
		mortuary.setDescription("");
		assertThatThrownBy(() -> mortuaryStaysBrowserManager.newMortuaryStays(mortuary))
			.isInstanceOf(OHDataIntegrityViolationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testMgrValidationCodeExists() throws Exception {
		String code = setupTestMortuaryStays(true);
		MortuaryStays mortuary = mortuaryStaysBrowserManager.getByCode(code);
		assertThatThrownBy(() -> mortuaryStaysBrowserManager.newMortuaryStays(mortuary))
			.isInstanceOf(OHDataIntegrityViolationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testMortuaryStaysManagerHashCode() throws Exception {
		MortuaryStays mortuary = testMortuaryStaysManager.setup(false);
		// compute value
		int hashCode = mortuary.hashCode();
		// compare with stored value
		assertThat(mortuary.hashCode()).isEqualTo(hashCode);
	}

	private String setupTestMortuaryStays(boolean usingSet) throws OHException {
		MortuaryStays mortuary = testMortuaryStaysManager.setup(usingSet);
		mortuaryStaysIoOperationRepository.save(mortuary);
		return mortuary.getCode();
	}

	private void checkMortuaryIntoDb(String code) throws OHServiceException {
		MortuaryStays foundMortuary = mortuaryStaysBrowserManager.getByCode(code);
		testMortuaryStaysManager.check(foundMortuary);
	}

}
