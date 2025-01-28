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
package org.isf.medicals;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.isf.OHCoreTestCase;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicals.service.MedicalsIoOperations;
import org.isf.medicalstock.TestLot;
import org.isf.medicalstock.TestMovement;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstock.service.MovementIoOperationRepository;
import org.isf.medstockmovtype.TestMovementType;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalDsrStockMovementTypeIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.supplier.TestSupplier;
import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.SupplierIoOperationRepository;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

class Tests extends OHCoreTestCase {

	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestMovement testMovement;
	private static TestMovementType testMovementType;
	private static TestWard testWard;
	private static TestLot testLot;
	private static TestSupplier testSupplier;

	@Autowired
	MedicalsIoOperations medicalsIoOperations;
	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;
	@Autowired
	MedicalBrowsingManager medicalBrowsingManager;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;
	@Autowired
	MovementIoOperationRepository movementIoOperationRepository;
	@Autowired
	MedicalDsrStockMovementTypeIoOperationRepository medicalDsrStockMovementTypeIoOperationRepository;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	@Autowired
	LotIoOperationRepository lotIoOperationRepository;
	@Autowired
	SupplierIoOperationRepository supplierIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
		testMovement = new TestMovement();
		testMovementType = new TestMovementType();
		testWard = new TestWard();
		testLot = new TestLot();
		testSupplier = new TestSupplier();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testMedicalGets() throws Exception {
		int code = setupTestMedical(false);
		checkMedicalIntoDb(code);
	}

	@Test
	void testMedicalSets() throws Exception {
		int code = setupTestMedical(true);
		checkMedicalIntoDb(code);
	}

	@Test
	void testIoGetMedicalWithCode() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		Medical medical = medicalsIoOperations.getMedical(code);
		assertThat(medical.getCode()).isEqualTo(foundMedical.getCode());
	}

	@Test
	void testIoGetMedicals() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals();
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void testIoGetMedicalsWithDescription() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(String.valueOf(foundMedical.getDescription()));
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void testIoGetMedicalsWithOutDescription() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(null);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void testIoGetMedicalsSortedNoDescription() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(null, false);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void testIoGetMedicalsSortedWithTypeDescription() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(String.valueOf(foundMedical.getDescription()), true);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void testIoGetMedicalsNoDescriptionTypeCritical() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(null, foundMedical.getType().getCode(), true);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void testIoGetMedicalsNoDescriptionTypeNotCritical() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(null, foundMedical.getType().getCode(), false);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void testIoGetMedicalsNoDescriptionNoTypeCritical() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(null, null, true);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void testIoGetMedicalsNoDescriptionNoTypeNotCritical() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(null, null, false);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void testIoGetMedicalsDescriptionTypeCritical() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(foundMedical.getDescription(), foundMedical.getType().getCode(), true);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void testIoGetMedicalsDescriptionTypeNotCritical() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(foundMedical.getDescription(), foundMedical.getType().getCode(), false);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void testIoGetMedicalsDescriptionNoTypeCritical() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(foundMedical.getDescription(), null, true);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void testIoGetMedicalsDescriptionNoTypeNotCritical() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(foundMedical.getDescription(), null, false);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void testIoMedicalCheckTrue() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.medicalCheck(foundMedical, false);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void testIoMedicalCheckFalse() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.medicalCheck(foundMedical, false);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void testIoProductCodeExistsUpdateTrue() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		assertThat(medicalsIoOperations.productCodeExists(foundMedical, true)).isFalse();
	}

	@Test
	void testIoProductCodeExistsUpdateFalse() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		assertThat(medicalsIoOperations.productCodeExists(foundMedical, false)).isTrue();
	}

	@Test
	void testIoMedicalExistsUpdateTrue() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		assertThat(medicalsIoOperations.medicalExists(foundMedical, true)).isFalse();
	}

	@Test
	void testIoMedicalExistsUpdateFalse() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		assertThat(medicalsIoOperations.medicalExists(foundMedical, false)).isTrue();
	}

	@Test
	void testIoNewMedical() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		Medical medical = testMedical.setup(medicalType, true);
		medical.setConditioning("Conditioning");
	    medical.setDosing("Dosing");
	    medical.setShape("Shape");
		Medical result = medicalsIoOperations.newMedical(medical);
		assertThat(result).isNotNull();
		checkMedicalIntoDb(medical.getCode());
		assertThat(result.getConditioning()).isEqualTo("Conditioning");
	    assertThat(result.getDosing()).isEqualTo("Dosing");
	    assertThat(result.getShape()).isEqualTo("Shape");
	}
	
	@Test
	void testIoUpdateMedical() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		foundMedical.setDescription("Update");
		Medical result = medicalsIoOperations.updateMedical(foundMedical);
		assertThat(result).isNotNull();
		Medical updatedMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(updatedMedical).isNotNull();
		assertThat(updatedMedical.getDescription()).isEqualTo("Update");
	}

	@Test
	void testIoDeleteMedical() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		medicalsIoOperations.deleteMedical(foundMedical);
		assertThat(medicalsIoOperationRepository.findById(code)).isEmpty();
	}

	@Test
	void testIsMedicalReferencedInStockMovement() throws Exception {
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		assertThat(medicalsIoOperations.isMedicalReferencedInStockMovement(foundMovement.getMedical().getCode())).isTrue();
	}

	@Test
	void testMgrGetMedicalWithCode() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		Medical medical = medicalBrowsingManager.getMedical(code);
		assertThat(medical.getCode()).isEqualTo(foundMedical.getCode());
	}

	@Test
	void testMgrGetMedicals() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalBrowsingManager.getMedicals();
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void testMgrGetMedicalsSortedByName() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalBrowsingManager.getMedicalsSortedByName();
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void testMgrGetMedicalsSortedByCode() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalBrowsingManager.getMedicalsSortedByCode();
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void testMgrGetMedicalsWithDescription() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalBrowsingManager.getMedicals(foundMedical.getDescription());
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void testMgrGetMedicalsWithTypeDescriptionNotSorted() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalBrowsingManager.getMedicals(foundMedical.getDescription(), false);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void testMgrGetMedicalsWithTypeDescriptionSorted() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalBrowsingManager.getMedicals(foundMedical.getDescription(), true);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void testMgrGetMedicalsWithCriteria() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalBrowsingManager.getMedicals(foundMedical.getDescription(), foundMedical.getType().getCode(), true);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void testMgrNewMedical() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		Medical medical = testMedical.setup(medicalType, true);
		assertThat(medicalBrowsingManager.newMedical(medical)).isNotNull();
		checkMedicalIntoDb(medical.getCode());
	}

	@Test
	void testMgrNewMedicalIgnoreSimilar() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		Medical medical = testMedical.setup(medicalType, true);
		assertThat(medicalBrowsingManager.newMedical(medical, false)).isNotNull();
		checkMedicalIntoDb(medical.getCode());
	}
	
	@Test
	void testMgrUpdateMedical() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		foundMedical.setDescription("Update");
		foundMedical.setConditioning("packet 3");
		foundMedical.setDosing("per 3 for 1day");
		foundMedical.setShape("form");
		assertThat(medicalBrowsingManager.updateMedical(foundMedical)).isNotNull();
		Medical updatedMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(updatedMedical).isNotNull();
		assertThat(updatedMedical.getDescription()).isEqualTo("Update");
		assertThat(updatedMedical.getConditioning()).isEqualTo("packet 3");
		assertThat(updatedMedical.getDosing()).isEqualTo("per 3 for 1day");
		assertThat(updatedMedical.getShape()).isEqualTo("form");
	}

	@Test
	void testMgrUpdateMedicalIgnoreSimilarTrue() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		foundMedical.setDescription("Update");
		assertThat(medicalBrowsingManager.updateMedical(foundMedical, true)).isNotNull();
		Medical updatedMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(updatedMedical).isNotNull();
		assertThat(updatedMedical.getDescription()).isEqualTo("Update");
	}

	@Test
	void testMgrUpdateMedicalIgnoreSimilarFalse() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		foundMedical.setDescription("Update");
		assertThat(medicalBrowsingManager.updateMedical(foundMedical, false)).isNotNull();
		Medical updatedMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(updatedMedical).isNotNull();
		assertThat(updatedMedical.getDescription()).isEqualTo("Update");
	}

	@Test
	void testMgrDeleteMedical() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		medicalBrowsingManager.deleteMedical(foundMedical);
		Medical medical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(medical).isNull();
	}

	@Test
	void testMgrDeleteMedicalInStockMovement() throws Exception {
		assertThatThrownBy(() -> {
			int medicalCode = setupTestMedical(false);
			Medical medical = medicalsIoOperationRepository.findById(medicalCode).orElse(null);
			assertThat(medical).isNotNull();
			MedicalType medicalType = testMedicalType.setup(false);
			MovementType movementType = testMovementType.setup(false);
			Ward ward = testWard.setup(false);
			Lot lot = testLot.setup(medical, false);
			Supplier supplier = testSupplier.setup(false);
			Movement movement = testMovement.setup(medical, movementType, ward, lot, supplier, true);
			supplierIoOperationRepository.saveAndFlush(supplier);
			wardIoOperationRepository.saveAndFlush(ward);
			medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(movementType);
			medicalTypeIoOperationRepository.saveAndFlush(medicalType);
			medicalsIoOperationRepository.saveAndFlush(medical);
			lotIoOperationRepository.saveAndFlush(lot);
			movementIoOperationRepository.saveAndFlush(movement);
			medicalBrowsingManager.deleteMedical(medical);
		})
			.isInstanceOf(OHDataIntegrityViolationException.class);
	}

	@Test
	void testMgrCheckMedicalCommonBadMinQty() throws Exception {
		assertThatThrownBy(() -> {
			MedicalType medicalType = testMedicalType.setup(false);
			Medical medical = testMedical.setup(medicalType, false);
			medical.setMinqty(-1);
			medicalBrowsingManager.validateMedical(medical, false, false);
		})
			.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void testMgrCheckMedicalCommonBadPcsperpck() throws Exception {
		assertThatThrownBy(() -> {
			MedicalType medicalType = testMedicalType.setup(false);
			Medical medical = testMedical.setup(medicalType, false);
			medical.setPcsperpck(-1);
			medicalBrowsingManager.validateMedical(medical, false, false);
		})
			.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void testMgrCheckMedicalCommonMissingDescription() throws Exception {
		assertThatThrownBy(() -> {
			MedicalType medicalType = testMedicalType.setup(false);
			Medical medical = testMedical.setup(medicalType, false);
			medical.setDescription("");
			medicalBrowsingManager.validateMedical(medical, false, false);
		})
			.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void testMgrCheckMedicalProductCodeExists() throws Exception {
		assertThatThrownBy(() -> {
			int medicalCode = setupTestMedical(false);
			Medical medical = medicalsIoOperationRepository.findById(medicalCode).orElse(null);
			assertThat(medical).isNotNull();
			medicalBrowsingManager.validateMedical(medical, false, false);
		})
			.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void testMgrCheckMedicalMedicalExists() throws Exception {
		assertThatThrownBy(() -> {
			int medicalCode = setupTestMedical(false);
			Medical medical = medicalsIoOperationRepository.findById(medicalCode).orElse(null);
			assertThat(medical).isNotNull();
			medical.setProdCode("");
			medicalBrowsingManager.validateMedical(medical, false, false);
		})
			.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void testMgrCheckMedicalNotIgnoreSimilarNotSimilarMedicalsEmpty() throws Exception {
		assertThatThrownBy(() -> {
			int medicalCode = setupTestMedical(false);
			Medical medical = medicalsIoOperationRepository.findById(medicalCode).orElse(null);
			assertThat(medical).isNotNull();
			medical.setProdCode("");
			MedicalType medicalType = new MedicalType("code", "description");
			medicalTypeIoOperationRepository.saveAndFlush(medicalType);
			medical.setType(medicalType);
			medicalBrowsingManager.validateMedical(medical, false, false);
		})
			.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void testMedicalSimpleConstructor() throws Exception {
		Medical medical = new Medical(-1);
		assertThat(medical).isNotNull();
		assertThat(medical.getCode()).isEqualTo(-1);
	}

	@Test
	void testMedicalSettersGetters() throws Exception {
		int medicalCode = setupTestMedical(false);
		Medical medical = medicalsIoOperationRepository.findById(medicalCode).orElse(null);
		assertThat(medical).isNotNull();
		medical.setInqty(50);
		medical.setOutqty(25);
		assertThat(medical.getTotalQuantity()).isEqualTo(50 - 25);

		medical.setCode(-1);
		assertThat(medical.getCode()).isEqualTo(-1);

		medical.setLock(-99);
		assertThat(medical.getLock()).isEqualTo(-99);
	}

	@Test
	void testMedicalEquals() throws Exception {
		MedicalType medicalType = new MedicalType("code", "description");
		Medical medical = new Medical(1, medicalType, "TP1", "TestDescription1", 1, 3, 5, 4);

		MedicalType medicalType2 = new MedicalType("code2", "description2");
		Medical medical2 = new Medical(2, medicalType2, "TP2", "TestDescription2", 1, 3, 5, 4);

		MedicalType medicalType3 = new MedicalType("code3", "description3");
		Medical medical3 = new Medical(3, medicalType3, "TP3", "TestDescription2", 1, 3, 5, 4);

		assertThat(medical)
			.isEqualTo(medical)
			.isNotEqualTo("someString")
			.isNotEqualTo(medical2);

		medical2.setProdCode(null);
		medical3.setProdCode(null);
		assertThat(medical2).isNotEqualTo(medical3);
		assertThat(medical3).isNotEqualTo(medical2);
	}

	@Test
	void testMedicalCompareTo() throws Exception {
		MedicalType medicalType = new MedicalType("code", "description");
		Medical medical = new Medical(1, medicalType, "TP1", "TestDescription1", 1, 3, 5, 4);

		MedicalType medicalType2 = new MedicalType("code2", "description2");
		Medical medical2 = new Medical(2, medicalType2, "TP2", "TestDescription2", 1, 3, 5, 4);

		assertThat(medical.compareTo(medical)).isZero();
		assertThat(medical.compareTo(medical2)).isEqualTo(-1);
	}

	@Test
	void testMedicalHashCode() throws Exception {
		MedicalType medicalType = new MedicalType("code", "description");
		Medical medical = new Medical(1, medicalType, "TP1", "TestDescription1", 1, 3, 5, 4);

		// compute hashCode
		int hashCode = medical.hashCode();
		assertThat(hashCode).isPositive();
		// use computed value
		assertThat(medical.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void testMedicalClone() throws Exception {
		MedicalType medicalType = new MedicalType("code", "description");
		Medical medical = new Medical(1, medicalType, "TP1", "TestDescription1", 2, 3, 4, 5);

		assertThat(medical.clone()).isEqualTo(medical);
	}

	private int setupTestMedical(boolean usingSet) throws OHException {
		return setupMedical(true).getCode();
	}

	private Medical setupMedical(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, usingSet);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		return medical;
	}

	private void checkMedicalIntoDb(int code) throws OHException {
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		testMedical.check(foundMedical);
	}

	private int setupTestMovement(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		MovementType movementType = testMovementType.setup(false);
		Ward ward = testWard.setup(false);
		Lot lot = testLot.setup(medical, false);
		Supplier supplier = testSupplier.setup(false);
		Movement movement = testMovement.setup(medical, movementType, ward, lot, supplier, usingSet);
		wardIoOperationRepository.saveAndFlush(ward);
		supplierIoOperationRepository.saveAndFlush(supplier);
		medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(movementType);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		lotIoOperationRepository.saveAndFlush(lot);
		movementIoOperationRepository.saveAndFlush(movement);
		return movement.getCode();
	}

	@Test
	@DisplayName("Should return requested page of medicals filtered by type, deleted status, and sorted by prod_code")
	void testMgrGetMedicalsByTypeSortedByProdCodePageable() throws Exception {

		List<Medical> savedMedicals = generateMedicals(10, true, 2);

		Page<Medical> firstPage = medicalBrowsingManager.getMedicalsByTypeAndDescription(
			savedMedicals.get(0).getType().getDescription(), null, 'N', false, 0, 3
		);

		assertThat(firstPage.getTotalElements()).isEqualTo(8);
		assertThat(firstPage.getContent().size()).isEqualTo(3);
		assertThat(firstPage.getContent().get(0).getProdCode()).isEqualTo("prod_code2");

		Page<Medical> lastPage = medicalBrowsingManager.getMedicalsByTypeAndDescription(
			savedMedicals.get(0).getType().getDescription(), null, 'N', false, 2, 3
		);

		assertThat(lastPage.getContent().size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Should filter medicals by description and deleted status")
	void testMgrGetMedicalsByDescriptionPageable() throws Exception {
		generateMedicals(10, true, 2);

		Page<Medical> medicals = medicalBrowsingManager.getMedicalsByTypeAndDescription(
			null, "description-0", 'N', false, 0, 3
		);
		assertThat(medicals.getTotalElements()).isEqualTo(0);
		assertThat(medicals.getContent().size()).isEqualTo(0);

		medicals = medicalBrowsingManager.getMedicalsByTypeAndDescription(
			null, "descr", 'N', false, 0, 3
		);
		assertThat(medicals.getTotalElements()).isEqualTo(8);
		assertThat(medicals.getContent().size()).isEqualTo(3);

		medicals = medicalBrowsingManager.getMedicalsByTypeAndDescription(
			null, "descr", 'N', true, 1, 3
		);
		assertThat(medicals.getContent().get(0).getDescription()).isEqualTo("4description6");

		generateMedicals(5, false, 5);
		medicals = medicalBrowsingManager.getMedicalsByTypeAndDescription(
			null, "descr", 'Y', false, 0, 2
		);
		assertThat(medicals.getTotalElements()).isEqualTo(7);
		assertThat(medicals.getContent().size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Should retrieve all medicals regardless of deleted status")
	void testMgrGetAllMedicalsRegardlessOfDeletedStatus() throws Exception {
		List<Medical> savedMedicals = generateMedicals(8, true, 2);

		Page<Medical> medicals = medicalBrowsingManager.getMedicalsByTypeAndDescription(
			savedMedicals.get(0).getType().getDescription(), null, null, false, 0, 8
		);

		assertThat(medicals.getTotalElements()).isEqualTo(8);
		assertThat(medicals.getContent().size()).isEqualTo(8);

		long deletedCount = medicals.getContent().stream().filter(m -> m.getDeleted() == 'Y').count();
		long notDeletedCount = medicals.getContent().stream().filter(m -> m.getDeleted() == 'N').count();

		assertThat(deletedCount).isEqualTo(2);
		assertThat(notDeletedCount).isEqualTo(6); 
	}

	/**
	 * Generate a list of Medical objects.
	 *
	 * @param size The total number of Medical objects to generate.
	 * @param sameType Whether to use the same MedicalType for all the generated Medical objects.
	 * @param deletedCount The number of Medical objects to mark as deleted ('Y').
	 * The rest will be marked as not deleted ('N').
	 * @return The list of generated {@link Medical} objects.
	 */
	private List<Medical> generateMedicals(int size, boolean sameType, int deletedCount) {
		String medicalTypeCode = "MT";

		List<Medical> medicals = IntStream.range(0, size).mapToObj(i -> {
			MedicalType medicalType;
			try {
				medicalType = testMedicalType.setup(false);
			} catch (OHException e) {
				throw new RuntimeException(e);
			}

			if (sameType) {
				medicalType.setCode(medicalTypeCode);
			} else {
				medicalType.setCode(medicalTypeCode + i);
			}
			medicalType = medicalTypeIoOperationRepository.saveAndFlush(medicalType);

			Medical medical = new Medical(
				0,
				medicalType,
				"prod_code" + i,
				(size - i) + "description" + i,
				10,
				4 + i,
				2 * i,
				1 + i
			);

			medical.setCode(null);

			if (deletedCount > 0 && i < deletedCount) {
				medical.setDeleted('Y');
			} else {
				medical.setDeleted('N');
			}

			return medical;
		}).toList();

		return medicalsIoOperationRepository.saveAllAndFlush(medicals);
	}
}