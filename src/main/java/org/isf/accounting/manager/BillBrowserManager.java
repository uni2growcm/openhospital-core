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
package org.isf.accounting.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.isf.therapy.manager.TherapyManager;
import org.isf.lab.manager.LabManager;
import org.isf.operation.manager.OperationRowBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.accounting.service.AccountingIoOperations;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.model.User;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.menu.manager.Context;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.priceslist.model.ItemGroup;
import org.isf.priceslist.model.Price;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BillBrowserManager {

	private final AccountingIoOperations ioOperations;
	private final TherapyManager therapyManager;
	private final LabManager labManager;
	private final OperationRowBrowserManager operationRowManager;
	private final MovWardBrowserManager mvtManager;
	private final PriceListManager priceListManager;
	private final MedicalBrowsingManager medicalBrowsingManager;
	private final MovStockInsertingManager movStockInsertingManager;

	public BillBrowserManager(AccountingIoOperations ioOperations,
	                          MovWardBrowserManager mvtManager,
	                          PriceListManager priceListManager,
	                          MedicalBrowsingManager medicalBrowsingManager,
	                          MovStockInsertingManager movStockInsertingManager,
	                          TherapyManager therapyManager,
	                          LabManager labManager,
	                          OperationRowBrowserManager operationRowManager) {
		this.ioOperations = ioOperations;
		this.mvtManager = mvtManager;
		this.priceListManager = priceListManager;
		this.medicalBrowsingManager = medicalBrowsingManager;
		this.movStockInsertingManager = movStockInsertingManager;
		this.therapyManager = therapyManager;
		this.labManager = labManager;
		this.operationRowManager = operationRowManager;
	}

	private List<BillItems> getDeletedItems(int billID, List<BillItems> updatedItems) throws OHServiceException {
		List<BillItems> oldItems = this.ioOperations.getItems(billID);
		if (oldItems == null || oldItems.isEmpty()) return new ArrayList<>();

		if (updatedItems == null) updatedItems = new ArrayList<>();

		Map<Integer, BillItems> newItemsMap = updatedItems.stream()
			.filter(item -> item.getId() > 0)
			.collect(Collectors.toMap(BillItems::getId, item -> item));

		List<BillItems> removedOrReduced = new ArrayList<>();

		for (BillItems oldItem : oldItems) {
			BillItems newItem = newItemsMap.get(oldItem.getId());
			if (newItem == null) {
				removedOrReduced.add(oldItem);
			} else if (oldItem.getItemQuantity() > newItem.getItemQuantity()) {
				int diff = oldItem.getItemQuantity() - newItem.getItemQuantity();
				BillItems reducedItem = new BillItems(oldItem.getId(), oldItem.getBill(), oldItem.isPrice(),
					oldItem.getPriceID(), oldItem.getItemDescription(), oldItem.getItemAmount(), diff);
				reducedItem.setItemId(oldItem.getItemId());
				reducedItem.setItemDisplayCode(oldItem.getItemDisplayCode());
				removedOrReduced.add(reducedItem);
			}
		}
		return removedOrReduced;
	}

	private List<BillItems> getNewItems(int billID, List<BillItems> updatedItems) throws OHServiceException {
		List<BillItems> oldItems = this.ioOperations.getItems(billID);
		if (updatedItems == null || updatedItems.isEmpty()) return new ArrayList<>();

		Map<Integer, BillItems> oldItemsMap = oldItems != null ? oldItems.stream()
																 .filter(item -> item.getId() > 0)
																 .collect(Collectors.toMap(BillItems::getId, item -> item)) : new HashMap<>();

		List<BillItems> addedOrIncreased = new ArrayList<>();

		for (BillItems updatedItem : updatedItems) {
			if (updatedItem.getId() == 0) {
				addedOrIncreased.add(updatedItem);
			} else {
				BillItems oldItem = oldItemsMap.get(updatedItem.getId());
				if (oldItem != null && updatedItem.getItemQuantity() > oldItem.getItemQuantity()) {
					int diff = updatedItem.getItemQuantity() - oldItem.getItemQuantity();
					BillItems increasedItem = new BillItems(updatedItem.getId(), updatedItem.getBill(), updatedItem.isPrice(),
						updatedItem.getPriceID(), updatedItem.getItemDescription(), updatedItem.getItemAmount(), diff);
					increasedItem.setItemId(updatedItem.getItemId());
					increasedItem.setItemDisplayCode(updatedItem.getItemDisplayCode());
					addedOrIncreased.add(increasedItem);
				}
			}
		}
		return addedOrIncreased;
	}

	protected void validateBill(Bill bill, List<BillPayments> billPayments) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		LocalDateTime today = TimeTools.getNow();
		LocalDateTime upDate;
		LocalDateTime firstPay = today;
		LocalDateTime lastPay = today;
		LocalDateTime billDate = bill.getDate();

		if (!billPayments.isEmpty()) {
			firstPay = billPayments.get(0).getDate();
			lastPay = billPayments.get(billPayments.size() - 1).getDate();
			upDate = lastPay;
		} else {
			upDate = billDate;
		}
		bill.setUpdate(upDate);

		if (billDate.isAfter(today)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.billsinthefuturearenotallowed.msg")));
		}
		if (lastPay.isAfter(today)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.payementsinthefuturearenotallowed.msg")));
		}
		if (billDate.isAfter(firstPay)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.billdateaisfterthefirstpayment.msg")));
		}
		if (bill.getPatName().isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.pleaseinsertanameforthepatient.msg")));
		}
		if (bill.getStatus().equals("C") && bill.getBalance() != 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.abillwithanoutstandingbalancecannotbeclosed.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	public List<BillItems> getItems(int billID) throws OHServiceException {
		if (billID == 0) {
			return new ArrayList<>();
		}
		return ioOperations.getItems(billID);
	}

	public List<Bill> getBills(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient) throws OHServiceException {
		return ioOperations.getBillsBetweenDatesWherePatient(dateFrom, dateTo, patient);
	}

	public List<BillPayments> getPayments(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient) throws OHServiceException {
		return ioOperations.getPaymentsBetweenDatesWherePatient(dateFrom, dateTo, patient);
	}

	public List<BillPayments> getPayments(int billID) throws OHServiceException {
		return ioOperations.getPayments(billID);
	}

	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public Bill newBill(Bill bill, List<BillItems> billItems, List<BillPayments> billPayments) throws OHServiceException {
		validateBill(bill, billPayments);
		Bill newBill = ioOperations.newBill(bill);

		if (billItems != null && !billItems.isEmpty()) {
			ioOperations.newBillItems(newBill, billItems);
			markPrescriptionsAsBilled(billItems, newBill);
			if (GeneralData.STOCKMVTONBILLSAVE) {
				updateMedicalStock(billItems, newBill.getId(), false);
			}
		}

		if (billPayments != null && !billPayments.isEmpty()) {
			ioOperations.newBillPayments(newBill, billPayments);
		}
		return newBill;
	}

	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public Bill updateBill(Bill updateBill, List<BillItems> billItems, List<BillPayments> billPayments) throws OHServiceException {
		validateBill(updateBill, billPayments);

		if (GeneralData.STOCKMVTONBILLSAVE) {
			List<BillItems> newItems = getNewItems(updateBill.getId(), billItems);
			List<BillItems> deletedItems = getDeletedItems(updateBill.getId(), billItems);
			updateMedicalStock(deletedItems, updateBill.getId(), true);
			updateMedicalStock(newItems, updateBill.getId(), false);
		}

		Bill updatedBill = ioOperations.updateBill(updateBill);

		if (billItems != null && !billItems.isEmpty()) {
			ioOperations.newBillItems(updatedBill, billItems);
		}

		if (billPayments != null && !billPayments.isEmpty()) {
			ioOperations.newBillPayments(updatedBill, billPayments);
		}

		markPrescriptionsAsBilled(billItems, updatedBill);
		return updatedBill;
	}

	public List<Bill> getPendingBills(int patID) throws OHServiceException {
		return ioOperations.getPendingBills(patID);
	}

	public Bill getBill(int billID) throws OHServiceException {
		return ioOperations.getBill(billID);
	}

	public List<String> getUsers() throws OHServiceException {
		return ioOperations.getUsers();
	}

	public void deleteBill(Bill deleteBill) throws OHServiceException {
		ioOperations.deleteBill(deleteBill);
	}

	public List<Bill> getBills(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return ioOperations.getBillsBetweenDates(dateFrom, dateTo);
	}

	public List<Bill> getBills(List<BillPayments> billPayments) throws OHServiceException {
		if (billPayments.isEmpty()) {
			return new ArrayList<>();
		}
		return ioOperations.getBills(billPayments);
	}

	public List<BillPayments> getPayments(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return ioOperations.getPayments(dateFrom, dateTo);
	}

	public List<BillPayments> getPayments(List<Bill> billArray) throws OHServiceException {
		return ioOperations.getPayments(billArray);
	}

	public List<Bill> getPendingBillsAffiliate(int patID) throws OHServiceException {
		return ioOperations.getPendingBillsAffiliate(patID);
	}

	public List<BillItems> getDistinctItems() throws OHServiceException {
		return ioOperations.getDistictsBillItems();
	}

	public List<Bill> getBills(LocalDateTime dateFrom, LocalDateTime dateTo, BillItems billItem) throws OHServiceException {
		return ioOperations.getBillsBetweenDatesWhereBillItem(dateFrom, dateTo, billItem);
	}

	public List<Bill> getBillsByDatePatientAndGuarantor(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient, User guarantor) throws OHServiceException {
		return patient == null ? ioOperations.getBillsByDatesAndGuarantor(dateFrom, dateTo, guarantor) : ioOperations.getBillsByDatesPatientAndGuarantor(dateFrom, dateTo, patient, guarantor);
	}

	public List<BillPayments> getPaymentsByDatePatientAndGuarantor(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient, User guarantor) throws OHServiceException {
		if (dateFrom == null || dateTo == null) {
			throw new IllegalArgumentException("Date cannot be null");
		}
		return patient == null ? ioOperations.getPaymentsByDatesAndGuarantor(dateFrom, dateTo, guarantor) : ioOperations.getPaymentsByDatesPatientAndGuarantor(dateFrom, dateTo, patient, guarantor);
	}

	public List<Bill> getBillsByGuarantor(List<BillPayments> billPayments, User guarantor) throws OHServiceException {
		return billPayments.isEmpty() ? new ArrayList<>() : ioOperations.getBillsByGuarantor(billPayments, guarantor);
	}

	private void updateMedicalStock(List<BillItems> medicalItems, int billID, boolean isCharge) throws OHServiceException {
		if (medicalItems == null || medicalItems.isEmpty()) return;

		PatientBrowserManager patientManager = Context.getApplicationContext().getBean(PatientBrowserManager.class);
		Bill bill = getBill(billID);
		Ward ward = bill.getWard();
		if (ward == null) return;

		Patient patient = patientManager.getPatientById(bill.getBillPatient().getCode());
		List<Price> prices = priceListManager.getPrices();

		List<OHExceptionMessage> validationErrors = new ArrayList<>();

		for (BillItems item : medicalItems) {
			Price price = prices.stream()
				.filter(p -> p != null && "MED".equals(p.getGroup()) && item.getItemDescription().equals(p.getDesc()))
				.findFirst()
				.orElse(null);

			if (price != null) {
				try {
					addStockMvt(ward, patient, item, isCharge);
				} catch (OHDataValidationException e) {
					validationErrors.addAll(e.getMessages());
				}
			}
		}

		if (!validationErrors.isEmpty()) {
			throw new OHDataValidationException(validationErrors);
		}
	}

	private void addStockMvt(Ward ward, Patient patient, BillItems billItem, boolean isCharge) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		double qty = billItem.getItemQuantity();

		if (isCharge) {
			qty = -qty;
		}

		List<MedicalWard> medWards = mvtManager.getMedicalsWard(ward.getCode(), true);

		if (!isCharge && (medWards == null || medWards.isEmpty())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.stocknotavailableforitem") + " : " + billItem.getItemDescription()));
			throw new OHDataValidationException(errors);
		}

		MedicalWard medicalWard = medWards.stream()
			.filter(med -> med.getId().getMedical().getDescription().equals(billItem.getItemDescription()))
			.findFirst()
			.orElse(null);

		if (!isCharge && medicalWard == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.stocknotavailableforitem") + " : " + billItem.getItemDescription()));
			throw new OHDataValidationException(errors);
		}

		if (!isCharge && medicalWard.getQty() < qty) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.newbill.qtynotinstock") + " : " + billItem.getItemDescription()));
			throw new OHDataValidationException(errors);
		}

		MovementWard mvt = new MovementWard();
		mvt.setWard(ward);
		mvt.setPatient(patient);
		mvt.setDate(TimeTools.getServerDateTime());
		mvt.setPatient(true);
		mvt.setQuantity(qty);
		mvt.setDescription(patient.getName());

		if (isCharge) {
			Medical medical = medicalBrowsingManager.getMedicals(billItem.getItemDescription())
				.stream()
				.filter(med -> Objects.equals(med.getDescription(), billItem.getItemDescription()))
				.findFirst()
				.orElse(null);
			Lot lot = movStockInsertingManager.getLotByMedical(medical, false).stream().findFirst().orElse(null);
			mvt.setMedical(medical);
			mvt.setlot(lot);
		} else {
			mvt.setMedical(medicalWard.getId().getMedical());
			mvt.setlot(medicalWard.getLot());
		}
		mvt.setUnits("pieces");

		mvtManager.newMovementWard(mvt);
	}

	public Page<Bill> getBillsWithFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient, User guarantor, int page, int size) throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size);
		return ioOperations.getBillsWithFilters(status, dateFrom, dateTo, patient, guarantor, pageable);
	}

	public List<Bill> getBillsListWithFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient, User guarantor, int limit, int offset) throws OHServiceException {
		return ioOperations.getBillsListWithFilters(status, dateFrom, dateTo, patient, guarantor, limit, offset);
	}

	public long countBillsWithFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient, User guarantor) throws OHServiceException {
		return ioOperations.countBillsWithFilters(status, dateFrom, dateTo, patient, guarantor);
	}

	public List<BillItems> getAllBillItems(Bill bill) throws OHServiceException {
		return ioOperations.getAllBillItems(bill);
	}

	public List<BillPayments> getAllBillPayments(Bill bill) throws OHServiceException {
		return ioOperations.getAllBillPayments(bill);
	}

	public boolean hasPrescription(Integer patientCode) throws OHServiceException {
		return ioOperations.hasPrescription(patientCode);
	}

	public Price getPrice(String itemId, ItemGroup group, Patient patient) throws OHServiceException {
		return ioOperations.getPrice(itemId, group, patient);
	}

	public Price getPriceFromListWithoutReduction(String itemId, ItemGroup group, Patient patient) throws OHServiceException {
		return ioOperations.getPriceFromListWithoutReduction(itemId, group, patient);
	}

	public boolean isPrescriptionAlreadyBilledAndPaid(Integer patientCode, Integer prescriptionId, String itemGroup) throws OHServiceException {
		return ioOperations.isPrescriptionAlreadyBilledAndPaid(patientCode, prescriptionId, itemGroup);
	}

	private void markPrescriptionsAsBilled(List<BillItems> billItems, Bill bill) throws OHServiceException {
		if (billItems == null) return;

		for (BillItems item : billItems) {
			if (item.getPrescriptionId() == null || item.getPrescriptionId() == 0) {
				continue;
			}
			if (ItemGroup.MEDICAL.getCode().equals(item.getItemGroup())) {
				therapyManager.updateBougthQuantity(item.getPrescriptionId(), item.getItemQuantity());
			} else if (ItemGroup.EXAM.getCode().equals(item.getItemGroup())) {
				labManager.updateBillForLaboratory(item.getPrescriptionId(), bill);
			} else if (ItemGroup.OPERATION.getCode().equals(item.getItemGroup())) {
				operationRowManager.updateBillForOperationRow(item.getPrescriptionId(), bill);
			}
		}
	}
}