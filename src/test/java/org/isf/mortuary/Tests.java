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
package org.isf.mortuary;


import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.mortuary.manager.MortuaryBrowserManager;
import org.isf.mortuary.model.Mortuary;
import org.isf.mortuary.service.MortuaryIoOperationRepository;
import org.isf.mortuary.service.MortuaryIoOperations;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vactype.model.VaccineType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class Tests extends OHCoreTestCase {

	private static TestMortuary testMortuary;

	@Autowired
	MortuaryIoOperations mortuaryIoOperations;
	@Autowired
	MortuaryIoOperationRepository mortuaryIoOperationRepository;
	@Autowired
	MortuaryBrowserManager mortuaryBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testMortuary = new TestMortuary();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testMortuaryGets() throws Exception {
		String code = setupTestMortuary(false);
		checkMortuaryIntoDb(code);
	}

	@Test
	void testMgrGetMortuary() throws Exception {
		String code = setupTestMortuary(false);
		Mortuary foundMortuary = mortuaryBrowserManager.findMortuaryByCode(code);
		assertThat(foundMortuary).isNotNull();
		List<Mortuary> mortuaries = mortuaryBrowserManager.getMortuaries();
		assertThat(mortuaries.get(mortuaries.size() - 1).getDescription()).isEqualTo(foundMortuary.getDescription());
	}

	@Test
	void testMgrUpdateMortuary() throws Exception {
		String code = setupTestMortuary(false);
		Mortuary foundMortuary = mortuaryBrowserManager.findMortuaryByCode(code);
		assertThat(foundMortuary).isNotNull();
		foundMortuary.setDescription("Update");
		Mortuary updatedMortuary = mortuaryBrowserManager.updateMortuary(foundMortuary);
		assertThat(updatedMortuary).isNotNull();
		assertThat(updatedMortuary.getDescription()).isEqualTo("Update");
	}

	@Test
	void testMgrNewMortuary() throws Exception {
		Mortuary mortuary = testMortuary.setup(true);
		assertThat(mortuaryBrowserManager.newMortuary(mortuary)).isNotNull();
		checkMortuaryIntoDb(mortuary.getCode());
	}

	@Test
	void testMgrIsCodePresent() throws Exception {
		String code = setupTestMortuary(false);
		assertThat(mortuaryBrowserManager.isCodePresent(code)).isTrue();
	}

	@Test
	void testMgrDeleteMortuary() throws Exception {
		String code = setupTestMortuary(false);
		Mortuary foundMortuary = mortuaryIoOperations.findMortuaryByCode(code);
		assertThat(foundMortuary).isNotNull();
		mortuaryBrowserManager.deleteMortuary(foundMortuary);
		assertThat(mortuaryBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	void testMgrFindMortuary() throws Exception {
		String code = setupTestMortuary(false);
		Mortuary foundMortuary = mortuaryBrowserManager.findMortuaryByCode(code);
		assertThat(foundMortuary).isNotNull();
		assertThat(foundMortuary.getCode()).isEqualTo(code);
	}

	@Test
	void testMgrFindMortuaryNull() throws Exception {
		assertThatThrownBy(() -> mortuaryBrowserManager.findMortuaryByCode(null))
			.isInstanceOf(RuntimeException.class);
	}

	@Test
	void testMgrValidationCodeEmpty() throws Exception {
		String code = setupTestMortuary(true);
		Mortuary mortuary = mortuaryBrowserManager.findMortuaryByCode(code);
		mortuary.setCode("");
		assertThatThrownBy(() -> mortuaryBrowserManager.newMortuary(mortuary))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testMgrValidationCodeTooLong() throws Exception {
		Mortuary mortuary = testMortuary.setup(false);
		mortuary.setCode("thisIsACodeThatIsTooLong");
		assertThatThrownBy(() -> mortuaryBrowserManager.newMortuary(mortuary))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testMgrValidationDescriptionEmpty() throws Exception {
		String code = setupTestMortuary(true);
		Mortuary mortuary = mortuaryBrowserManager.findMortuaryByCode(code);
		mortuary.setDescription("");
		assertThatThrownBy(() -> mortuaryBrowserManager.newMortuary(mortuary))
			.isInstanceOf(OHDataIntegrityViolationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testMgrValidationCodeExists() throws Exception {
		String code = setupTestMortuary(true);
		Mortuary mortuary = mortuaryBrowserManager.findMortuaryByCode(code);
		assertThatThrownBy(() -> mortuaryBrowserManager.newMortuary(mortuary))
			.isInstanceOf(OHDataIntegrityViolationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}
//
//	@Test
//	void testMortuaryPrint() throws Exception {
//		String code = setupTestMortuary(true);
//		Mortuary mortuary = mortuaryBrowserManager.findMortuaryByCode()(code);
//		assertThat(mortuary).isNotNull();
//		assertThat(mortuary.print()).isEqualTo("mortuary code=." + mortuary.getCode() + ". description=." + mortuary.getDescription() + '.');
//	}

	@Test
	void testMortuaryEquals() throws Exception {
		Mortuary vaccineType = testMortuary.setup(true);

		assertThat(vaccineType)
			.isEqualTo(vaccineType)
			.isNotNull()
			.isNotEqualTo("someStringValue");

		Mortuary vaccineType2 = new Mortuary("A", "adescription", 4, 1);
		assertThat(vaccineType).isNotEqualTo(vaccineType2);

		vaccineType2.setCode(vaccineType.getCode());
		assertThat(vaccineType).isNotEqualTo(vaccineType2);

		vaccineType2.setDescription(vaccineType.getDescription());
		assertThat(vaccineType).isEqualTo(vaccineType2);
	}

	@Test
	void testMortuaryHashCode() throws Exception {
		Mortuary mortuary = testMortuary.setup(false);
		// compute value
		int hashCode = mortuary.hashCode();
		// compare with stored value
		assertThat(mortuary.hashCode()).isEqualTo(hashCode);
	}

	private String setupTestMortuary(boolean usingSet) throws OHException {
		Mortuary mortuary = testMortuary.setup(usingSet);
		mortuaryIoOperationRepository.save(mortuary);
		return mortuary.getCode();
	}

	private void checkMortuaryIntoDb(String code) throws OHServiceException {
		Mortuary foundMortuary = mortuaryIoOperations.findMortuaryByCode(code);
		testMortuary.check(foundMortuary);
	}

}
