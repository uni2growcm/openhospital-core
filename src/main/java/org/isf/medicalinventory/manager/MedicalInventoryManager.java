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
package org.isf.medicalinventory.manager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.medicalinventory.model.InventoryStatus;
import org.isf.medicalinventory.model.InventoryType;
import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.medicalinventory.service.MedicalInventoryIoOperation;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.manager.MovBrowserManager;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.medstockmovtype.manager.MedicalDsrStockMovementTypeBrowserManager;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.supplier.manager.SupplierBrowserManager;
import org.isf.supplier.model.Supplier;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.utils.time.TimeTools;
import org.isf.utils.validator.DefaultSorter;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MedicalInventoryManager {

	private final MedicalInventoryIoOperation ioOperations;

	private final MedicalInventoryRowManager medicalInventoryRowManager;

	private final MovStockInsertingManager movStockInsertingManager;

	private final MovBrowserManager movBrowserManager;

	private final MedicalDsrStockMovementTypeBrowserManager medicalDsrStockMovementTypeBrowserManager;

	private final SupplierBrowserManager supplierManager;

	private final WardBrowserManager wardManager;

	protected Map<String, String> statusHashMap;

	private MovWardBrowserManager movWardBrowserManager;

	public MedicalInventoryManager(MedicalInventoryIoOperation medicalInventoryIoOperation, MedicalInventoryRowManager medicalInventoryRowManager,
		MedicalDsrStockMovementTypeBrowserManager medicalDsrStockMovementTypeBrowserManager,
		SupplierBrowserManager supplierManager, MovStockInsertingManager movStockInsertingManager, WardBrowserManager wardManager,
		MovBrowserManager movBrowserManager, MovWardBrowserManager movWardBrowserManager) {
		this.ioOperations = medicalInventoryIoOperation;
		this.medicalInventoryRowManager = medicalInventoryRowManager;
		this.medicalDsrStockMovementTypeBrowserManager = medicalDsrStockMovementTypeBrowserManager;
		this.supplierManager = supplierManager;
		this.movStockInsertingManager = movStockInsertingManager;
		this.wardManager = wardManager;
		this.movBrowserManager = movBrowserManager;
		this.movWardBrowserManager = movWardBrowserManager;
	}

	/**
	 * Insert a new {@link MedicalInventory}.
	 *
	 * @param medicalInventory the {@link MedicalInventory} to insert.
	 * @param newMedicalInventoryRows the list of {@link MedicalInventoryRow}s to insert.
	 * @return the newly persisted {@link MedicalInventory} object.
	 * @throws OHServiceException
	 */
	@Transactional
	public MedicalInventory newMedicalInventory(MedicalInventory medicalInventory, List<MedicalInventoryRow> newMedicalInventoryRows)
		throws OHServiceException {
		if (GeneralData.REFERENCE_AUTOMATIC &&
			(medicalInventory.getInventoryReference() == null || medicalInventory.getInventoryReference().isBlank())) {
			LocalDateTime now = TimeTools.getNow();
			String autoRef = generateReference(GeneralData.REFERENCE_PREFIX_INVENTORY, now);
			medicalInventory.setInventoryReference(autoRef);
		}
		validateMedicalInventory(medicalInventory, true);
		MedicalInventory inventory = ioOperations.newMedicalInventory(medicalInventory);
		for (MedicalInventoryRow inventoryRow : newMedicalInventoryRows) {
			medicalInventoryRowManager.newMedicalInventoryRow(inventoryRow);
		}
		return inventory;
	}

	/**
	 * Update an existing {@link MedicalInventory}.
	 *
	 * @param medicalInventory the {@link MedicalInventory} to update.
	 * @param checkReferenceExists if {@code true}, it verifies the reference uniqueness
	 * @return the updated {@link MedicalInventory} object.
	 * @throws OHServiceException
	 */
	public MedicalInventory updateMedicalInventory(MedicalInventory medicalInventory, boolean checkReferenceExists) throws OHServiceException {
		validateMedicalInventory(medicalInventory, checkReferenceExists);
		return ioOperations.updateMedicalInventory(medicalInventory);
	}

	/**
	 * Check if the reference number is already used.
	 * 
	 * @param reference the {@link MedicalInventory} reference.
	 * @return {@code true} if the code is already in use, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public boolean referenceExists(String reference) throws OHServiceException {
		return ioOperations.referenceExists(reference);
	}

	/**
	 * Return a list of {@link MedicalInventory}s for passed params.
	 *
	 * @param status the {@link MedicalInventory} status.
	 * @param wardCode the {@link Ward} code.
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MedicalInventory> getMedicalInventoryByStatusAndWard(String status, String wardCode) throws OHServiceException {
		return ioOperations.getMedicalInventoryByStatusAndWard(status, wardCode);
	}

	/**
	 * Return a list {@link MedicalInventory}s for passed params.
	 *
	 * @param status the {@link MedicalInventory} status.
	 * @param inventoryType the {@link MedicalInventory} type.
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MedicalInventory> getMedicalInventoryByStatusAndInventoryType(String status, String inventoryType) throws OHServiceException {
		return ioOperations.getMedicalInventoryByStatusAndInventoryType(status, inventoryType);
	}

	/**
	 * Return all {@link MedicalInventory}s without params.<br>
	 * For a detailed search use {@code getMedicalInventoryByParams(LocalDateTime dateFrom, LocalDateTime dateTo, String status, String type)}
	 *
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MedicalInventory> getMedicalInventory() throws OHServiceException {
		return ioOperations.getMedicalInventory();
	}

	/**
	 * Return a list of {@link MedicalInventory}s for passed params.
	 * 
	 * @param dateFrom the lowest date for the range.
	 * @param dateTo the highest date for the range.
	 * @param status the {@link MedicalInventory} status.
	 * @param type the {@link MedicalInventory} type.
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MedicalInventory> getMedicalInventoryByParams(LocalDateTime dateFrom, LocalDateTime dateTo, String status, String type)
		throws OHServiceException {
		dateFrom = TimeTools.getBeginningOfDay(dateFrom);
		dateTo = TimeTools.getBeginningOfNextDay(dateTo);
		return ioOperations.getMedicalInventoryByParams(dateFrom, dateTo, status, type);
	}

	/**
	 * Return a list of {@link MedicalInventory}s for passed params.
	 * 
	 * @param dateFrom the lower date for the range.
	 * @param dateTo the highest date for the range.
	 * @param status the {@link MedicalInventory} status.
	 * @param type the {@link MedicalInventory} type.
	 * @param page the page number.
	 * @param size the page size.
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public Page<MedicalInventory> getMedicalInventoryByParamsPageable(LocalDateTime dateFrom, LocalDateTime dateTo, String status, String type, int page,
		int size) throws OHServiceException {
		dateFrom = TimeTools.getBeginningOfDay(dateFrom);
		dateTo = TimeTools.getBeginningOfNextDay(dateTo);
		return ioOperations.getMedicalInventoryByParamsPageable(dateFrom, dateTo, status, type, page, size);
	}

	/**
	 * Fetch {@link MedicalInventory} with param.
	 * 
	 * @param inventoryId the {@link MedicalInventory} id.
	 * @return {@link MedicalInventory}. It could be {@code null}.
	 * @throws OHServiceException
	 */
	public MedicalInventory getInventoryById(int inventoryId) throws OHServiceException {
		return ioOperations.getInventoryById(inventoryId);
	}

	/**
	 * Fetch {@link MedicalInventory} by reference, excluding canceled ones.
	 * 
	 * @param reference the {@link MedicalInventory} reference.
	 * @return {@link MedicalInventory}. It could be {@code null}.
	 * @throws OHServiceException
	 */
	public MedicalInventory getInventoryByReference(String reference) throws OHServiceException {
		return ioOperations.getInventoryByReference(reference);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any.
	 *
	 * @param medicalInventory the {@link MedicalInventory} to validate
	 * @param checkReferenceExists if {@code true}, it verifies the reference uniqueness
	 * @throws OHDataValidationException
	 */
	private void validateMedicalInventory(MedicalInventory medicalInventory, boolean checkReferenceExists) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
		String reference = medicalInventory.getInventoryReference();
		if (medicalInventory.getInventoryDate() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.pleaseinsertavalidinventorydate.msg")));
		}
		if (medicalInventory.getInventoryDate() != null && medicalInventory.getInventoryDate().isAfter(tomorrow)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.notdateinfuture.msg")));
		}
		if (reference == null || reference.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.mustenterareference.msg")));
		} else {
			/*
			 * Despite MINVT_REFERENCE is VARCHAR(50), this field will be copied into MMV_REFNO and concatenate to either "-charge" or "-discharge" suffixes,
			 * hence the limit to 40. For similarity, we keep the same validation method for "ward" type inventories, despite the field would be copied into
			 * MMVN_DESC VARCHAR(100) in those cases.
			 */
			if (reference.length() > 40) {
				errors.add(new OHExceptionMessage(MessageBundle.formatMessage("angal.inventory.thereferenceistoolongmaxchars.fmt.msg", 40)));
			}
			if (checkReferenceExists) {
				try {
					checkReference(medicalInventory);
				} catch (OHServiceException e) {
					errors.addAll(e.getMessages());
				}
			}
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Validate the Inventory rows of the main store inventory.
	 * <p>
	 * Stock movements done after the inventory are <b>not</b> a reason to refuse the validation anymore: what has been counted is the difference
	 * ({@code realQty - theoreticQty}) for each lot and this difference is kept whatever happens to the stock in the meantime (see
	 * {@link #actualizeMedicalInventoryRow(MedicalInventory, boolean)}).
	 * <p>
	 * The only blocking case is when the stock of a lot has decreased so much that the counted difference can no longer be applied (the real quantity
	 * would become negative).
	 *
	 * @param inventory the {@link MedicalInventory}
	 * @param inventoryRowSearchList The list of {@link MedicalInventoryRow}
	 * @param allMedicals kept for compatibility, it has no impact on the validation
	 * @throws OHServiceException if the counted difference of some rows can no longer be applied
	 */
	public void validateMedicalInventoryRow(MedicalInventory inventory, List<MedicalInventoryRow> inventoryRowSearchList, boolean allMedicals)
		throws OHServiceException {
		alignRowsOnCurrentStock(inventoryRowSearchList, getMainStoreQuantities(inventoryRowSearchList), null, false);
	}

	/**
	 * Validate the Inventory rows of a ward inventory.
	 * <p>
	 * Same rules as {@link #validateMedicalInventoryRow(MedicalInventory, List, boolean)} but with the quantities of the ward.
	 *
	 * @param inventory the {@link MedicalInventory}
	 * @param inventoryRowSearchList the list of {@link MedicalInventoryRow}
	 * @param allMedicals kept for compatibility, it has no impact on the validation
	 * @throws OHServiceException if the counted difference of some rows can no longer be applied
	 */
	public void validateMedicalWardInventoryRow(MedicalInventory inventory, List<MedicalInventoryRow> inventoryRowSearchList, boolean allMedicals)
		throws OHServiceException {
		alignRowsOnCurrentStock(inventoryRowSearchList, getWardQuantities(inventory.getWardCode(), inventoryRowSearchList), 0.0, false);
	}

	/**
	 * Align the {@link MedicalInventoryRow}s on the current stock, keeping the counted difference ({@code realQty - theoreticQty}).
	 * <p>
	 * Example: theoretic 100, real 95 (difference -5). Then 20 units are discharged, current stock is 80: the row becomes theoretic 80, real 75,
	 * difference still -5.
	 *
	 * @param rows the rows to align
	 * @param currentQuantities the current quantity by lot code
	 * @param defaultQty the quantity to use when a lot is not found in {@code currentQuantities}; if {@code null} the row is left untouched
	 * @param apply if {@code true} the rows are modified (not persisted), otherwise only the check is done
	 * @return the rows that are (or would be) modified
	 * @throws OHDataValidationException if the real quantity of some rows would become negative
	 */
	private List<MedicalInventoryRow> alignRowsOnCurrentStock(List<MedicalInventoryRow> rows, Map<String, Double> currentQuantities, Double defaultQty,
		boolean apply) throws OHDataValidationException {
		List<MedicalInventoryRow> changedRows = new ArrayList<>();
		List<Double> alignedTheoreticQties = new ArrayList<>();
		List<Double> alignedRealQties = new ArrayList<>();
		StringBuilder negativeDetails = new StringBuilder();
		// first pass: compute and check, nothing is modified
		for (MedicalInventoryRow row : rows) {
			Lot lot = row.getLot();
			if (lot == null) {
				continue;
			}
			Double currentQty = currentQuantities.getOrDefault(lot.getCode(), defaultQty);
			if (currentQty == null || currentQty == row.getTheoreticQty()) {
				continue;
			}
			double countedDifference = row.getRealQty() - row.getTheoreticQty();
			double alignedRealQty = currentQty + countedDifference;
			if (alignedRealQty < 0) {
				String medicalDesc = row.getMedical() != null ? row.getMedical().getDescription() : "";
				negativeDetails.append("\n").append(MessageBundle.formatMessage("angal.inventory.counteddifferencecannotbeapplied.detail.fmt.msg",
					medicalDesc, getLotInfo(lot), formatQty(countedDifference, true), formatQty(currentQty, false)));
				continue;
			}
			changedRows.add(row);
			alignedTheoreticQties.add(currentQty);
			alignedRealQties.add(alignedRealQty);
		}
		if (!negativeDetails.isEmpty()) {
			throw new OHDataValidationException(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.validate.btn"),
				MessageBundle.formatMessage("angal.inventory.counteddifferencecannotbeapplied.fmt.msg", negativeDetails), OHSeverityLevel.ERROR));
		}
		// second pass: apply
		if (apply) {
			for (int i = 0; i < changedRows.size(); i++) {
				changedRows.get(i).setTheoreticQty(alignedTheoreticQties.get(i));
				changedRows.get(i).setRealqty(alignedRealQties.get(i));
			}
		}
		return changedRows;
	}

	/**
	 * Get the current main store quantity of all the lots of the medicals present in {@code rows}.
	 *
	 * @param rows the inventory rows
	 * @return a map lot code -> current main store quantity
	 * @throws OHServiceException
	 */
	private Map<String, Double> getMainStoreQuantities(List<MedicalInventoryRow> rows) throws OHServiceException {
		Map<String, Double> quantities = new HashMap<>();
		List<Medical> medicals = rows.stream().map(MedicalInventoryRow::getMedical).filter(Objects::nonNull).distinct().toList();
		for (Medical medical : medicals) {
			for (Lot lot : movStockInsertingManager.getLotByMedical(medical, false)) {
				Integer qty = lot.getMainStoreQuantity();
				quantities.put(lot.getCode(), qty == null ? 0.0 : qty.doubleValue());
			}
		}
		return quantities;
	}

	/**
	 * Get the current quantity in the ward of all the lots of the medicals present in {@code rows}.
	 *
	 * @param wardCode the ward code
	 * @param rows the inventory rows
	 * @return a map lot code -> current quantity in the ward
	 * @throws OHServiceException
	 */
	private Map<String, Double> getWardQuantities(String wardCode, List<MedicalInventoryRow> rows) throws OHServiceException {
		Map<String, Double> quantities = new HashMap<>();
		List<Medical> medicals = rows.stream().map(MedicalInventoryRow::getMedical).filter(Objects::nonNull).distinct().toList();
		for (Medical medical : medicals) {
			for (MedicalWard medicalWard : movWardBrowserManager.getMedicalsWard(wardCode, medical.getCode(), false)) {
				if (medicalWard.getLot() != null) {
					Double qty = medicalWard.getQty();
					quantities.put(medicalWard.getLot().getCode(), qty == null ? 0.0 : qty);
				}
			}
		}
		return quantities;
	}

	private static String getLotInfo(Lot lot) {
		if (GeneralData.AUTOMATICLOT_IN && lot.getDueDate() != null) {
			return TimeTools.formatDateTime(lot.getDueDate(), TimeTools.DD_MM_YYYY);
		}
		return lot.getCode();
	}

	private static String formatQty(double qty, boolean signed) {
		String value = qty == Math.rint(qty) ? String.valueOf((long) qty) : String.valueOf(qty);
		return signed && qty > 0 ? "+" + value : value;
	}

	/**
	 * Marks an inventory as deleted by changing its status.
	 * 
	 * @param medicalInventory the medicalInventory of the inventory to delete.
	 * @throws OHServiceException if an error occurs during the operation.
	 */
	public void deleteInventory(MedicalInventory medicalInventory) throws OHServiceException {
		int inventoryId = medicalInventory.getId();
		List<MedicalInventoryRow> inventoryRows = medicalInventoryRowManager.getMedicalInventoryRowByInventoryId(inventoryId);
		for (MedicalInventoryRow invRow : inventoryRows) {
			boolean isNewLot = invRow.isNewLot();
			Lot lot = invRow.getLot();
			if (isNewLot && lot != null) {
				invRow.setLot(null);
				medicalInventoryRowManager.updateMedicalInventoryRow(invRow);
				movStockInsertingManager.deleteLot(lot);
			}
		}
		ioOperations.deleteInventory(medicalInventory);
	}

	/**
	 * Confirm the Inventory rows of the main store inventory.
	 * <p>
	 * Before creating the adjustment movements, the rows are aligned on the current stock keeping the counted differences, so the stock movements done
	 * between the count and the confirmation are preserved and only the counted differences are applied. The adjustment movements are dated now (a
	 * movement cannot be dated before the last stock movement).
	 *
	 * @param inventory the {@link MedicalInventory}
	 * @param inventoryRowSearchList The list of {@link MedicalInventoryRow}
	 * @param allMedicals kept for compatibility, it has no impact on the confirmation
	 * @return List {@link Movement}. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	public List<Movement> confirmMedicalInventoryRow(MedicalInventory inventory, List<MedicalInventoryRow> inventoryRowSearchList, boolean allMedicals)
		throws OHServiceException {
		// align the rows on the current stock, keeping the counted differences
		List<MedicalInventoryRow> changedRows = alignRowsOnCurrentStock(inventoryRowSearchList, getMainStoreQuantities(inventoryRowSearchList), null, true);
		for (MedicalInventoryRow row : changedRows) {
			medicalInventoryRowManager.updateMedicalInventoryRow(row);
		}

		// get general info
		String referenceNumber = inventory.getInventoryReference();
		// TODO: to explore the possibility to allow charges and discharges with same referenceNumber
		String chargeReferenceNumber = referenceNumber + "-charge";
		String dischargeReferenceNumber = referenceNumber + "-discharge";
		MovementType dischargeType = medicalDsrStockMovementTypeBrowserManager.getMovementType(inventory.getDischargeType());
		LocalDateTime movementDate = TimeTools.getNow();
		// prepare movements
		List<Movement> chargeMovements = new ArrayList<>();
		List<Movement> dischargeMovements = new ArrayList<>();
		for (MedicalInventoryRow medicalInventoryRow : inventoryRowSearchList) {
			double theoQty = medicalInventoryRow.getTheoreticQty();
			double realQty = medicalInventoryRow.getRealQty();
			Double ajustQty = realQty - theoQty;
			Medical medical = medicalInventoryRow.getMedical();
			Lot currentLot = medicalInventoryRow.getLot();
			if (ajustQty > 0) { // charge movement when realQty > theoQty
				Movement movement = new Movement(medical, dischargeType, null, currentLot, movementDate, -ajustQty.intValue(), null, chargeReferenceNumber);
				chargeMovements.add(movement);
			} else if (ajustQty < 0) { // discharge movement when realQty < theoQty
				Movement movement = new Movement(medical, dischargeType, null, currentLot, movementDate, -ajustQty.intValue(), null, dischargeReferenceNumber);
				dischargeMovements.add(movement);
			} // else ajustQty = 0, continue
		}
		// create movements
		List<Movement> insertedMovements = new ArrayList<>();
		if (!chargeMovements.isEmpty()) {
			insertedMovements.addAll(movStockInsertingManager.newMultipleDischargingMovements(chargeMovements, chargeReferenceNumber, true));
		}
		if (!dischargeMovements.isEmpty()) {
			insertedMovements.addAll(movStockInsertingManager.newMultipleDischargingMovements(dischargeMovements, dischargeReferenceNumber, true));
		}
		String status = InventoryStatus.done.toString();
		inventory.setStatus(status);
		this.updateMedicalInventory(inventory, false);
		return insertedMovements;
	}

	/**
	 * Confirm the Inventory rows of ward inventory.
	 * <p>
	 * Same rules as {@link #confirmMedicalInventoryRow(MedicalInventory, List, boolean)} but with the quantities of the ward.
	 *
	 * @param inventory the {@link MedicalInventory}
	 * @param inventoryRowSearchList The list of {@link MedicalInventoryRow}
	 * @param allMedicals kept for compatibility, it has no impact on the confirmation
	 * @return List of {@link MovementWard}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	public List<MovementWard> confirmMedicalWardInventoryRow(MedicalInventory inventory, List<MedicalInventoryRow> inventoryRowSearchList, boolean allMedicals)
		throws OHServiceException {
		// align the rows on the current ward stock, keeping the counted differences
		List<MedicalInventoryRow> changedRows = alignRowsOnCurrentStock(inventoryRowSearchList,
			getWardQuantities(inventory.getWardCode(), inventoryRowSearchList), 0.0, true);
		for (MedicalInventoryRow row : changedRows) {
			medicalInventoryRowManager.updateMedicalInventoryRow(row);
		}

		// get general info
		Ward selectedWard = wardManager.findWard(inventory.getWardCode());
		LocalDateTime now = TimeTools.getNow();
		String reason = inventory.getInventoryReference();
		List<MovementWard> insertedMovements = new ArrayList<>();
		for (MedicalInventoryRow medicalInventoryRow : inventoryRowSearchList) {
			double theoQty = medicalInventoryRow.getTheoreticQty();
			double realQty = medicalInventoryRow.getRealQty();
			double movQuantity = theoQty - realQty;
			Medical medical = medicalInventoryRow.getMedical();
			Lot currentLot = medicalInventoryRow.getLot();
			if (movQuantity != 0) {
				insertedMovements
					.add(movWardBrowserManager.newMovementWard(new MovementWard(selectedWard, now, false, null, 0, 0, reason, medical, movQuantity,
						MessageBundle.getMessage("angal.medicalstockward.rectify.pieces"), currentLot)));
			}
		}
		String status = InventoryStatus.done.toString();
		inventory.setStatus(status);
		this.updateMedicalInventory(inventory, false);
		return insertedMovements;
	}

	/**
	 * Check if the reference is present with suffixes also once the {@link MedicalInventory} is confirmed.
	 * 
	 * @param medicalInventory
	 * @throws OHServiceException
	 */
	private void checkReference(MedicalInventory medicalInventory) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		String reference = medicalInventory.getInventoryReference();
		String chargeReferenceNumber = reference + "-charge";
		String dischargeReferenceNumber = reference + "-discharge";
		boolean existWithSuffixCharge = movStockInsertingManager.refNoExists(chargeReferenceNumber);
		boolean existWithSuffixDischarge = movStockInsertingManager.refNoExists(dischargeReferenceNumber);
		MedicalInventory inventory = getInventoryByReference(reference);
		if (existWithSuffixCharge || existWithSuffixDischarge || inventory != null && !inventory.getId().equals(medicalInventory.getId())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.referencealreadyused.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Actualize the main store {@link MedicalInventoryRow}s with the current stock.
	 * <p>
	 * For each row, the theoretic quantity takes the current quantity of the lot and the real quantity is moved by the same amount, so the counted
	 * difference ({@code realQty - theoreticQty}) is kept: the inventory can be validated/confirmed later without counting again.
	 * <p>
	 * The lots moved since the inventory date and not yet in the inventory are added with a difference of zero (they were not counted).
	 * <p>
	 * The inventory date is not modified anymore.
	 *
	 * @param inventory the {@link MedicalInventory} whose rows need to be updated
	 * @param allMedicals if {@code true}, the lots of medicals not yet in the inventory are added too. If {@code false}, only the lots of the medicals
	 *        already in the inventory are added.
	 * @return the {@link MedicalInventory}
	 * @throws OHServiceException if the counted difference of some rows can no longer be applied
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	public MedicalInventory actualizeMedicalInventoryRow(MedicalInventory inventory, boolean allMedicals) throws OHServiceException {
		List<MedicalInventoryRow> inventoryRowList = medicalInventoryRowManager.getMedicalInventoryRowByInventoryId(inventory.getId());
		// align existing rows keeping the counted differences
		for (MedicalInventoryRow row : alignRowsOnCurrentStock(inventoryRowList, getMainStoreQuantities(inventoryRowList), null, true)) {
			medicalInventoryRowManager.updateMedicalInventoryRow(row);
		}

		// add the lots moved since the inventory date and not yet in the inventory
		List<Medical> inventoryMedicalsList = inventoryRowList.stream().map(MedicalInventoryRow::getMedical).distinct().toList();
		LocalDateTime movFrom = inventory.getInventoryDate();
		LocalDateTime movTo = TimeTools.getNow();
		List<Movement> movs = new ArrayList<>();
		if (allMedicals) {
			movs.addAll(movBrowserManager.getMovements(null, null, null, null, movFrom, movTo, null, null, null, null));
		} else {
			for (Medical medical : inventoryMedicalsList) {
				movs.addAll(movBrowserManager.getMovements(medical.getCode(), null, null, null, movFrom, movTo, null, null, null, null));
			}
		}
		List<Lot> movedLots = movs.stream().map(Movement::getLot).toList();
		addRowsForNewLots(inventory, inventoryRowList, movedLots, false);
		return inventory;
	}

	/**
	 * Actualize the ward {@link MedicalInventoryRow}s with the current ward stock.
	 * <p>
	 * Same rules as {@link #actualizeMedicalInventoryRow(MedicalInventory, boolean)} but with the quantities of the ward.
	 *
	 * @param inventory the {@link MedicalInventory} whose rows need to be updated
	 * @param allMedicals if {@code true}, the lots of medicals not yet in the inventory are added too. If {@code false}, only the lots of the medicals
	 *        already in the inventory are added.
	 * @return the {@link MedicalInventory}
	 * @throws OHServiceException if the counted difference of some rows can no longer be applied
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	public MedicalInventory actualizeMedicalWardInventoryRow(MedicalInventory inventory, boolean allMedicals) throws OHServiceException {
		String wardCode = inventory.getWardCode();
		List<MedicalInventoryRow> inventoryRowList = medicalInventoryRowManager.getMedicalInventoryRowByInventoryId(inventory.getId());
		// align existing rows keeping the counted differences
		for (MedicalInventoryRow row : alignRowsOnCurrentStock(inventoryRowList, getWardQuantities(wardCode, inventoryRowList), 0.0, true)) {
			medicalInventoryRowManager.updateMedicalInventoryRow(row);
		}

		// add the lots moved since the inventory date and not yet in the inventory
		List<Medical> inventoryMedicalsList = inventoryRowList.stream().map(MedicalInventoryRow::getMedical).distinct().toList();
		LocalDateTime movFrom = inventory.getInventoryDate();
		LocalDateTime movTo = TimeTools.getNow();
		List<Lot> movedLots = new ArrayList<>();
		movWardBrowserManager.getMovementWard(wardCode, movFrom, movTo).stream()
			.filter(movement -> allMedicals || inventoryMedicalsList.contains(movement.getMedical()))
			.map(MovementWard::getLot)
			.forEach(movedLots::add);
		movBrowserManager.getMovements(wardCode, movFrom, movTo).stream()
			.filter(movement -> allMedicals || inventoryMedicalsList.contains(movement.getMedical()))
			.map(Movement::getLot)
			.forEach(movedLots::add);
		addRowsForNewLots(inventory, inventoryRowList, movedLots, true);
		return inventory;
	}

	/**
	 * Add a {@link MedicalInventoryRow} (with a difference of zero) for each lot of {@code movedLots} not yet in {@code inventoryRowList} and with a
	 * current quantity greater than zero.
	 */
	private void addRowsForNewLots(MedicalInventory inventory, List<MedicalInventoryRow> inventoryRowList, List<Lot> movedLots, boolean ward)
		throws OHServiceException {
		Set<String> inventoryLotCodes = inventoryRowList.stream()
			.map(MedicalInventoryRow::getLot)
			.filter(Objects::nonNull)
			.map(Lot::getCode)
			.collect(Collectors.toSet());
		Map<String, Lot> newLots = new LinkedHashMap<>();
		for (Lot lot : movedLots) {
			if (lot != null && lot.getMedical() != null && !inventoryLotCodes.contains(lot.getCode())) {
				newLots.putIfAbsent(lot.getCode(), lot);
			}
		}
		if (newLots.isEmpty()) {
			return;
		}
		List<MedicalInventoryRow> newRows = newLots.values().stream()
			.map(lot -> new MedicalInventoryRow(null, 0.0, 0.0, inventory, lot.getMedical(), lot))
			.toList();
		Map<String, Double> currentQuantities = ward ? getWardQuantities(inventory.getWardCode(), newRows) : getMainStoreQuantities(newRows);
		for (MedicalInventoryRow newRow : newRows) {
			double currentQty = currentQuantities.getOrDefault(newRow.getLot().getCode(), 0.0);
			if (currentQty > 0) {
				newRow.setTheoreticQty(currentQty);
				newRow.setRealqty(currentQty);
				medicalInventoryRowManager.newMedicalInventoryRow(newRow);
			}
		}
	}

	/**
	 * Return the number of inventories by {@link InventoryType}.
	 * 
	 * @param type {@link InventoryType}
	 * @return the number of records or zero.
	 */
	public int getInventoryCount(String type) {
		return ioOperations.getInventoryCount(type);
	}

	/**
	 * Build a map of translated statuses
	 */
	private void buildStatusHashMap() {
		statusHashMap = new HashMap<>(4);
		statusHashMap.put("canceled", MessageBundle.getMessage("angal.inventory.status.canceled.txt"));
		statusHashMap.put("draft", MessageBundle.getMessage("angal.inventory.status.draft.txt"));
		statusHashMap.put("done", MessageBundle.getMessage("angal.inventory.status.done.txt"));
		statusHashMap.put("validated", MessageBundle.getMessage("angal.inventory.status.validated.txt"));
	}

	/**
	 * Return a list of translated statuses for {@code draft, done, canceled, validated}
	 *
	 * @return
	 */
	public List<String> getStatusList() {
		if (statusHashMap == null) {
			buildStatusHashMap();
		}
		List<String> statusList = new ArrayList<>(statusHashMap.values());
		statusList.sort(new DefaultSorter(MessageBundle.getMessage("angal.inventory.status.draft.txt")));
		return statusList;
	}

	/**
	 * Return a value associated with the key on statusHashMap.
	 * 
	 * @param key - the key of status
	 * @return the value associated with the key or empty string if key is not on statusHashMap.
	 */
	public String getStatusByKey(String key) {
		if (statusHashMap == null) {
			buildStatusHashMap();
		}
		return statusHashMap.get(key);
	}

	/**
	 * Return the key from the value on statusHashMap.
	 * 
	 * @param status - the description
	 * @return the value of the key or null if description is not on statusHashMap.
	 */
	public String getKeyByStatus(String status) {
		if (statusHashMap == null) {
			buildStatusHashMap();
		}
		for (Map.Entry<String, String> entry : statusHashMap.entrySet()) {
			if (entry.getValue().equals(status)) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Generates an automatic inventory reference based on prefix and date.
	 * Uses the timestamp format defined in GeneralData.
	 *
	 * @param prefix The prefix (e.g., "INV")
	 * @param date The inventory date
	 * @return The generated reference string
	 */
	private String generateReference(String prefix, LocalDateTime date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(GeneralData.REFERENCE_TIMESTAMP_FORMAT);
		String timestamp = date.format(formatter);
		return prefix + timestamp;
	}
}
