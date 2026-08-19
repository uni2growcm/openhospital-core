/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.accounting.service.AccountingIoOperations;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.patient.model.Patient;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BillBrowserManager {

	private static final String MEDICAL_GROUP_CODE = "MED";

	private final AccountingIoOperations ioOperations;
	private final MovWardBrowserManager movWardBrowserManager;
	private final MedicalBrowsingManager medicalBrowsingManager;

	public BillBrowserManager(
		AccountingIoOperations accountingIoOperations,
		MovWardBrowserManager movWardBrowserManager,
		MedicalBrowsingManager medicalBrowsingManager) {
		this.ioOperations = accountingIoOperations;
		this.movWardBrowserManager = movWardBrowserManager;
		this.medicalBrowsingManager = medicalBrowsingManager;
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 * 
	 * @param bill
	 * @param billPayments
	 * @throws OHDataValidationException
	 */
	protected void validateBill(Bill bill, List<BillPayments> billPayments) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		LocalDateTime today = TimeTools.getNow();
		LocalDateTime upDate;
		LocalDateTime firstPay = today;
		LocalDateTime lastPay = today;

		LocalDateTime billDate = bill.getDate();
		if (!billPayments.isEmpty()) {
			firstPay = billPayments.get(0).getDate();
			lastPay = billPayments.get(billPayments.size() - 1).getDate(); // most recent payment
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

	/**
	 * Retrieves all the {@link BillItems} associated to the passed {@link Bill} id.
	 * 
	 * @param billID the bill id.
	 * @return a list of {@link BillItems} or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<BillItems> getItems(int billID) throws OHServiceException {
		if (billID == 0) {
			return new ArrayList<>();
		}
		return ioOperations.getItems(billID);
	}

	/**
	 * Retrieves all the bills of a given patient between dateFrom and datTo
	 * 
	 * @param dateFrom
	 * @param dateTo
	 * @param patient
	 * @return the bills list
	 * @throws OHServiceException
	 */
	public List<Bill> getBills(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient) throws OHServiceException {
		return ioOperations.getBillsBetweenDatesWherePatient(dateFrom, dateTo, patient);
	}

	/**
	 * Retrieves all the billPayments for a given patient between dateFrom and dateTo
	 * 
	 * @param dateFrom
	 * @param dateTo
	 * @param patient
	 * @return the list of payments
	 * @throws OHServiceException
	 */
	public List<BillPayments> getPayments(LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient) throws OHServiceException {
		return ioOperations.getPaymentsBetweenDatesWherePatient(dateFrom, dateTo, patient);
	}

	/**
	 * Gets all the {@link BillPayments} for the specified {@link Bill}.
	 * 
	 * @param billID the bill id.
	 * @return a list of {@link BillPayments}
	 * @throws OHServiceException
	 */
	public List<BillPayments> getPayments(int billID) throws OHServiceException {
		return ioOperations.getPayments(billID);
	}

	/**
	 * Stores a new {@link Bill} along with all its {@link BillItems} and {@link BillPayments}
	 * 
	 * @param bill the bill to store.
	 * @param billItems the list of bill's items
	 * @param billPayments the list of bill's payments
	 * @returns the persisted Bill object
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public Bill newBill(
		Bill bill,
		List<BillItems> billItems,
		List<BillPayments> billPayments) throws OHServiceException {
		validateBill(bill, billPayments);
		Bill newBill = newBill(bill);
		int billId = newBill.getId();
		if (!billItems.isEmpty()) {
			newBillItems(billId, billItems);
		}
		if (!billPayments.isEmpty()) {
			newBillPayments(billId, billPayments);
		}
		if (GeneralData.STOCKMVTONBILLSAVE && newBill.getWard() != null) {
			createBillStockMovements(newBill, billItems);
		}
		return newBill;
	}

	/**
	 * Stores a new {@link Bill}.
	 * 
	 * @param newBill the bill to store.
	 * @return the persisted Bill object
	 * @throws OHServiceException
	 */
	private Bill newBill(Bill newBill) throws OHServiceException {
		return ioOperations.newBill(newBill);
	}

	/**
	 * Stores a list of {@link BillItems} associated to a {@link Bill}.
	 * 
	 * @param billID the bill id.
	 * @param billItems the bill items to store.
	 * @throws OHServiceException
	 */
	private void newBillItems(int billID, List<BillItems> billItems) throws OHServiceException {
		ioOperations.newBillItems(ioOperations.getBill(billID), billItems);
	}

	/**
	 * Stores a list of {@link BillPayments} associated to a {@link Bill}.
	 * 
	 * @param billID the bill id.
	 * @param payItems the bill payments.
	 * @throws OHServiceException
	 */
	private void newBillPayments(int billID, List<BillPayments> payItems) throws OHServiceException {
		ioOperations.newBillPayments(ioOperations.getBill(billID), payItems);
	}

	/**
	 * Updates the specified {@link Bill} along with all its {@link BillItems} and {@link BillPayments}
	 * 
	 * @param updateBill the bill to update.
	 * @param billItems the list of bill's items
	 * @param billPayments the list of bill's payments
	 * @return the updated Bill object
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public Bill updateBill(Bill updateBill,
		List<BillItems> billItems,
		List<BillPayments> billPayments) throws OHServiceException {
		validateBill(updateBill, billPayments);
		if (GeneralData.STOCKMVTONBILLSAVE) {
			reconcileBillStockMovements(updateBill, billItems);
		}
		Bill updatedBill = updateBill(updateBill);
		newBillItems(updateBill.getId(), billItems);
		newBillPayments(updateBill.getId(), billPayments);
		return updatedBill;
	}

	/**
	 * Updates the specified {@link Bill}.
	 * 
	 * @param updateBill the bill to update.
	 * @return the updated Bill object
	 * @throws OHServiceException
	 */
	private Bill updateBill(Bill updateBill) throws OHServiceException {
		return ioOperations.updateBill(updateBill);
	}

	/**
	 * Returns all the pending {@link Bill}s for the specified patient.
	 * 
	 * @param patID the patient id.
	 * @return the list of pending bills or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<Bill> getPendingBills(int patID) throws OHServiceException {
		return ioOperations.getPendingBills(patID);
	}

	/**
	 * Get the {@link Bill} with specified billID
	 * 
	 * @param billID
	 * @return the {@link Bill} or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public Bill getBill(int billID) throws OHServiceException {
		return ioOperations.getBill(billID);
	}

	/**
	 * Returns all user ids related to a {@link BillPayments}.
	 * 
	 * @return a list of user id or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<String> getUsers() throws OHServiceException {
		return ioOperations.getUsers();
	}

	/**
	 * Deletes the specified {@link Bill}. If the argument is NULL then an error is thrown. If the Bill is not found it is silently ignored.
	 * 
	 * @param deleteBill the bill to delete.
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	public void deleteBill(Bill deleteBill) throws OHServiceException {
		if (GeneralData.STOCKMVTONBILLSAVE) {
			reverseBillStockMovements(deleteBill.getId());
		}
		ioOperations.deleteBill(deleteBill);
	}

	/**
	 * Retrieves all the {@link Bill}s for the specified date range.
	 * 
	 * @param dateFrom the low date range endpoint, inclusive.
	 * @param dateTo the high date range endpoint, inclusive.
	 * @return a list of retrieved {@link Bill}s or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<Bill> getBills(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return ioOperations.getBillsBetweenDates(dateFrom, dateTo);
	}

	/**
	 * Gets all the {@link Bill}s associated to the passed {@link BillPayments}.
	 * 
	 * @param billPayments the {@link BillPayments} associated to the bill to retrieve.
	 * @return a list of {@link Bill} associated to the passed {@link BillPayments} or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<Bill> getBills(List<BillPayments> billPayments) throws OHServiceException {
		if (billPayments.isEmpty()) {
			return new ArrayList<>();
		}
		return ioOperations.getBills(billPayments);
	}

	/**
	 * Retrieves all the {@link BillPayments} for the specified date range.
	 * 
	 * @param dateFrom low endpoint, inclusive, for the date range.
	 * @param dateTo high endpoint, inclusive, for the date range.
	 * @return a list of {@link BillPayments} for the specified date range or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<BillPayments> getPayments(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return ioOperations.getPayments(dateFrom, dateTo);
	}

	/**
	 * Retrieves all the {@link BillPayments} associated to the passed {@link Bill} list.
	 * 
	 * @param billArray the bill array list of {@link Bill}s.
	 * @return a list of {@link BillPayments} associated to the passed bill list or {@code null} if an error occurred.
	 * @throws OHServiceException
	 */
	public List<BillPayments> getPayments(List<Bill> billArray) throws OHServiceException {
		return ioOperations.getPayments(billArray);
	}

	/**
	 * Retrieves all the {@link Bill}s associated to the specified {@link Patient}.
	 * 
	 * @param patID the Patient's ID
	 * @return the list of {@link Bill}s
	 * @throws OHServiceException
	 */
	public List<Bill> getPendingBillsAffiliate(int patID) throws OHServiceException {
		return ioOperations.getPendingBillsAffiliate(patID);
	}

	/**
	 * Returns all the distinct stored {@link BillItems}.
	 * 
	 * @return a list of distinct {@link BillItems} or null if an error occurs.
	 * @throws OHServiceException
	 */
	public List<BillItems> getDistinctItems() throws OHServiceException {
		return ioOperations.getDistictsBillItems();
	}

	/**
	 * Get the bills list with a given billItem
	 *
	 * @param dateFrom
	 * @param dateTo
	 * @param billItem
	 * @return
	 * @throws OHServiceException
	 */
	public List<Bill> getBills(LocalDateTime dateFrom, LocalDateTime dateTo, BillItems billItem) throws OHServiceException {
		return ioOperations.getBillsBetweenDatesWhereBillItem(dateFrom, dateTo, billItem);
	}

	/**
	 * Reverses every ward stock movement currently tagged (via {@link MovementWard#getBillId()}) with
	 * the specified bill id, restoring the stock they deducted. Called when a bill is deleted, or
	 * (per-medical, via {@link #reconcileBillStockMovements}) when it's edited.
	 *
	 * @param billId the bill id.
	 * @throws OHServiceException
	 */
	private void reverseBillStockMovements(int billId) throws OHServiceException {
		for (MovementWard movement : movWardBrowserManager.getMovementWardByBillId(billId)) {
			movWardBrowserManager.reverseMovementWard(movement);
		}
	}

	/**
	 * Reconciles a bill's ward stock movements against its new item list, medical by medical: a
	 * medical whose total requested quantity and ward are unchanged from what's already tagged to
	 * this bill is left untouched (no reversal, no new movement, no churn in the ward's movement
	 * history); any other medical (quantity changed, removed, or ward changed) has its previously
	 * tagged movements reversed and, if it's still on the bill with quantity &gt; 0, fresh movement(s)
	 * created for the new quantity. Matching is by medical code (parsed from {@code BillItems.priceID},
	 * e.g. {@code "MED42"}), not by bill item identity - {@code BillItems} rows have no identity
	 * stable across saves ({@code AccountingIoOperations.newBillItems} deletes and reinserts every row
	 * on every save), so aggregating per medical is what's actually reliable to diff.
	 *
	 * @param bill the bill being updated (its {@code ward} reflects the value about to be saved).
	 * @param billItems the bill's new item list (about to be saved).
	 * @throws OHServiceException
	 */
	private void reconcileBillStockMovements(Bill bill, List<BillItems> billItems) throws OHServiceException {
		Ward newWard = bill.getWard();

		Map<Integer, List<MovementWard>> oldMovementsByMedicalCode = new LinkedHashMap<>();
		for (MovementWard movement : movWardBrowserManager.getMovementWardByBillId(bill.getId())) {
			oldMovementsByMedicalCode.computeIfAbsent(movement.getMedical().getCode(), code -> new ArrayList<>()).add(movement);
		}

		Map<Integer, Integer> newQtyByMedicalCode = newWard != null ? aggregateMedicalQuantities(billItems) : Map.of();

		Set<Integer> medicalCodes = new LinkedHashSet<>();
		medicalCodes.addAll(oldMovementsByMedicalCode.keySet());
		medicalCodes.addAll(newQtyByMedicalCode.keySet());

		Map<Integer, Integer> qtyToCreateByMedicalCode = new LinkedHashMap<>();
		for (Integer medicalCode : medicalCodes) {
			List<MovementWard> oldMovements = oldMovementsByMedicalCode.getOrDefault(medicalCode, List.of());
			int oldQty = oldMovements.stream().mapToInt(movement -> movement.getQuantity().intValue()).sum();
			int newQty = newQtyByMedicalCode.getOrDefault(medicalCode, 0);
			boolean sameWard = !oldMovements.isEmpty() && sameWard(oldMovements.get(0).getWard(), newWard);

			if (oldQty == newQty && sameWard) {
				continue; // unchanged - leave the existing movement(s) untouched
			}
			for (MovementWard movement : oldMovements) {
				movWardBrowserManager.reverseMovementWard(movement);
			}
			if (newQty > 0) {
				qtyToCreateByMedicalCode.put(medicalCode, newQty);
			}
		}

		createMovementsForMedicals(bill, qtyToCreateByMedicalCode);
	}

	private static boolean sameWard(Ward a, Ward b) {
		return a != null && b != null && Objects.equals(a.getCode(), b.getCode());
	}

	/**
	 * Sums {@code billItems}' quantities by medical code (parsed from the "MED"-prefixed
	 * {@code priceID}), so a medical added via more than one bill item is treated as one aggregate
	 * quantity.
	 */
	private Map<Integer, Integer> aggregateMedicalQuantities(List<BillItems> billItems) {
		Map<Integer, Integer> qtyByMedicalCode = new LinkedHashMap<>();
		for (BillItems item : billItems) {
			if (item.getPriceID() != null && item.getPriceID().startsWith(MEDICAL_GROUP_CODE)) {
				int medicalCode = Integer.parseInt(item.getPriceID().substring(MEDICAL_GROUP_CODE.length()));
				qtyByMedicalCode.merge(medicalCode, item.getItemQuantity(), Integer::sum);
			}
		}
		return qtyByMedicalCode;
	}

	/**
	 * Creates ward stock movements for every medical item in {@code billItems}, deducting from
	 * {@code bill.getWard()}'s stock (oldest-expiring lot first), tagging each created movement with
	 * {@code bill.getId()}. Validates that the ward has enough total stock for every medical first,
	 * throwing {@link OHDataValidationException} (rolling back the whole save) before creating
	 * anything if not.
	 *
	 * @param bill the bill (already persisted, with its final id and ward).
	 * @param billItems the bill's items.
	 * @throws OHServiceException
	 */
	private void createBillStockMovements(Bill bill, List<BillItems> billItems) throws OHServiceException {
		createMovementsForMedicals(bill, aggregateMedicalQuantities(billItems));
	}

	/**
	 * @param qtyByMedicalCode medical code -&gt; total quantity to deduct from {@code bill.getWard()}'s
	 * stock and tag as caused by {@code bill}.
	 */
	private void createMovementsForMedicals(Bill bill, Map<Integer, Integer> qtyByMedicalCode) throws OHServiceException {
		if (qtyByMedicalCode.isEmpty()) {
			return;
		}
		Ward ward = bill.getWard();

		List<OHExceptionMessage> shortages = new ArrayList<>();
		Map<Integer, Medical> medicalsByCode = new LinkedHashMap<>();
		for (Map.Entry<Integer, Integer> entry : qtyByMedicalCode.entrySet()) {
			Medical medical = medicalBrowsingManager.getMedical(entry.getKey());
			medicalsByCode.put(entry.getKey(), medical);
			int available = movWardBrowserManager.getCurrentQuantityInWard(ward, medical);
			if (entry.getValue() > available) {
				shortages.add(new OHExceptionMessage(
					MessageBundle.formatMessage("angal.newbill.notenoughstockinwardforfmt.msg", medical.getDescription())));
			}
		}
		if (!shortages.isEmpty()) {
			throw new OHDataValidationException(shortages);
		}

		for (Map.Entry<Integer, Integer> entry : qtyByMedicalCode.entrySet()) {
			Medical medical = medicalsByCode.get(entry.getKey());
			int remainingQty = entry.getValue();

			List<MedicalWard> lots = movWardBrowserManager.getMedicalsWard(ward.getCode(), medical.getCode(), true);
			lots.sort(Comparator.comparing(medicalWard -> medicalWard.getLot() != null ? medicalWard.getLot().getDueDate() : LocalDateTime.MAX));

			for (MedicalWard medicalWard : lots) {
				if (remainingQty <= 0) {
					break;
				}
				int lotQty = Math.min(remainingQty, medicalWard.getQty().intValue());
				if (lotQty <= 0) {
					continue;
				}
				MovementWard movement = new MovementWard(ward, bill.getDate(), bill.isPatient(), bill.getBillPatient(), 0, 0f,
					MessageBundle.formatMessage("angal.newbill.stockmovementfrombill.fmt.msg", bill.getId()), medical, (double) lotQty, "pieces",
					medicalWard.getLot());
				movement.setBillId(bill.getId());
				movWardBrowserManager.newMovementWard(movement);
				remainingQty -= lotQty;
			}
		}
	}
}
