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
package org.isf.accounting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.service.AccountingBillIoOperationRepository;
import org.isf.generaldata.GeneralData;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicalstock.TestLot;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.medicalstockward.service.MedicalStockWardIoOperationRepository;
import org.isf.medicalstockward.service.MedicalStockWardIoOperations;
import org.isf.medicalstockward.service.MovementWardIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.menu.manager.Context;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.priceslist.TestPriceList;
import org.isf.priceslist.model.Price;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.service.PriceIoOperationRepository;
import org.isf.priceslist.service.PricesListIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

class TestBillDelete extends OHCoreTestCase {

	private static final String MED_DESCRIPTION = "Aspirin";
	private static final double MED_UNIT_PRICE = 5.0;
	private static final int MED_QUANTITY = 3;

	private static TestBill testBill;
	private static TestPatient testPatient;
	private static TestPriceList testPriceList;
	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestWard testWard;
	private static TestLot testLot;

	@Autowired private BillBrowserManager billBrowserManager;
	@Autowired private AccountingBillIoOperationRepository billRepository;
	@Autowired private PricesListIoOperationRepository priceListRepository;
	@Autowired private PriceIoOperationRepository priceRepository;
	@Autowired private PatientIoOperationRepository patientRepository;
	@Autowired private MedicalTypeIoOperationRepository medicalTypeRepository;
	@Autowired private MedicalsIoOperationRepository medicalRepository;
	@Autowired private WardIoOperationRepository wardRepository;
	@Autowired private LotIoOperationRepository lotRepository;
	@Autowired private MedicalStockWardIoOperationRepository medicalWardRepository;
	@Autowired private MedicalStockWardIoOperations medicalStockWardIoOperations;
	@Autowired private MovementWardIoOperationRepository movementWardRepository;
	@Autowired private ApplicationContext applicationContext;

	@BeforeAll
	static void setUpClass() {
		testBill = new TestBill();
		testPatient = new TestPatient();
		testPriceList = new TestPriceList();
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
		testWard = new TestWard();
		testLot = new TestLot();
	}

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		Context.setApplicationContext(applicationContext);
		cleanH2InMemoryDb();
		GeneralData.STOCKMVTONBILLSAVE = false;
		GeneralData.PATIENTPHOTOSTORAGE = "DB";
	}

	@Test
	@DisplayName("deleteBill creates reversal ward movement and restores stock when STOCKMVTONBILLSAVE is enabled")
	void deleteBillReversesMovementsCreatedAtBillCreation() throws OHException, OHServiceException {
		GeneralData.STOCKMVTONBILLSAVE = true;

		Bill bill = persistBillWithMedicalItem();
		int billId = bill.getId();

		List<MovementWard> beforeDelete = movementWardRepository.findByPatient_code(bill.getBillPatient().getCode());
		assertThat(beforeDelete).hasSize(1);
		assertThat(beforeDelete.get(0).getQuantity()).isCloseTo(MED_QUANTITY, offset(0.01));

		double quantityBeforeDelete = medicalStockWardIoOperations.getCurrentQuantityInWard(bill.getWard(), getMedical());
		assertThat(quantityBeforeDelete).isCloseTo(100.0 - MED_QUANTITY, offset(0.01));

		billBrowserManager.deleteBill(bill);

		assertThat(billRepository.findById(billId)).isEmpty();

		List<MovementWard> afterDelete = movementWardRepository.findByPatient_code(bill.getBillPatient().getCode());
		assertThat(afterDelete).hasSize(2);
		assertThat(afterDelete).extracting(MovementWard::getQuantity)
			.containsExactlyInAnyOrder((double) MED_QUANTITY, (double) -MED_QUANTITY);

		double quantityAfterDelete = medicalStockWardIoOperations.getCurrentQuantityInWard(bill.getWard(), getMedical());
		assertThat(quantityAfterDelete).isCloseTo(100.0, offset(0.01));
	}

	@Test
	@DisplayName("deleteBill does not touch ward movements when STOCKMVTONBILLSAVE is disabled")
	void deleteBillLeavesNoMovementWhenFlagDisabled() throws OHException, OHServiceException {
		Bill bill = persistBillWithMedicalItem();
		int billId = bill.getId();

		assertThat(movementWardRepository.findByPatient_code(bill.getBillPatient().getCode())).isEmpty();

		billBrowserManager.deleteBill(bill);

		assertThat(billRepository.findById(billId)).isEmpty();
		assertThat(movementWardRepository.findByPatient_code(bill.getBillPatient().getCode())).isEmpty();
	}

	@Test
	@DisplayName("deleteBill creates no movement for items without a matching medical price")
	void deleteBillSkipsItemsWithoutMedicalPrice() throws OHException, OHServiceException {
		GeneralData.STOCKMVTONBILLSAVE = true;

		Bill bill = persistBillWithoutMedicalPrice();
		int billId = bill.getId();

		assertThat(movementWardRepository.findByPatient_code(bill.getBillPatient().getCode())).isEmpty();

		billBrowserManager.deleteBill(bill);

		assertThat(billRepository.findById(billId)).isEmpty();
		assertThat(movementWardRepository.findByPatient_code(bill.getBillPatient().getCode())).isEmpty();
	}

	private Bill persistBillWithMedicalItem() throws OHException, OHServiceException {
		Medical medical = persistBaseEntities();
		Price price = new Price(0, getPriceList(), "MED", MED_DESCRIPTION, MED_DESCRIPTION, MED_UNIT_PRICE);
		priceRepository.saveAndFlush(price);

		Bill bill = testBill.setup(getPriceList(), getPatient(), null, false);
		bill.setWard(getWard());
		bill.setAmount(MED_UNIT_PRICE * MED_QUANTITY);
		bill.setBalance(0.0);

		List<BillItems> items = new ArrayList<>();
		items.add(new BillItems(0, null, false, null, MED_DESCRIPTION, MED_UNIT_PRICE, MED_QUANTITY));
		return billBrowserManager.newBill(bill, items, new ArrayList<>());
	}

	private Bill persistBillWithoutMedicalPrice() throws OHException, OHServiceException {
		persistBaseEntities();

		Bill bill = testBill.setup(getPriceList(), getPatient(), null, false);
		bill.setWard(getWard());
		bill.setAmount(MED_UNIT_PRICE * MED_QUANTITY);
		bill.setBalance(0.0);

		List<BillItems> items = new ArrayList<>();
		items.add(new BillItems(0, null, false, null, "UnknownItem", MED_UNIT_PRICE, MED_QUANTITY));
		return billBrowserManager.newBill(bill, items, new ArrayList<>());
	}

	private Medical persistBaseEntities() throws OHException, OHServiceException {
		Patient patient = testPatient.setup(false);
		Ward ward = testWard.setup(false);
		PriceList priceList = testPriceList.setup(false);
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		medical.setDescription(MED_DESCRIPTION);
		Lot lot = testLot.setup(medical, false);

		medicalTypeRepository.saveAndFlush(medicalType);
		medicalRepository.saveAndFlush(medical);
		wardRepository.saveAndFlush(ward);
		patientRepository.saveAndFlush(patient);
		lotRepository.saveAndFlush(lot);

		MedicalWard medicalWard = new MedicalWard(ward, medical, 100.0f, 0.0f, lot);
		medicalWardRepository.saveAndFlush(medicalWard);

		priceListRepository.saveAndFlush(priceList);

		this.patient = patient;
		this.ward = ward;
		this.priceList = priceList;
		this.medical = medical;
		return medical;
	}

	private Patient patient;
	private Ward ward;
	private PriceList priceList;
	private Medical medical;

	private Patient getPatient() {
		return patient;
	}

	private Ward getWard() {
		return ward;
	}

	private PriceList getPriceList() {
		return priceList;
	}

	private Medical getMedical() {
		return medical;
	}
}
