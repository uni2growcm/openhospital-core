/*
 * Open Hospital (www.open-hospital.org)
 * Copyright  2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.accounting.service.archive;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.isf.accounting.model.ArchivedBill;
import org.isf.accounting.model.ArchivedBillItems;
import org.isf.accounting.model.ArchivedBillPayments;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.time.TimeTools;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class ArchiveIoOperations {

	private final Logger log = LoggerFactory.getLogger(ArchiveIoOperations.class);

	private final ArchiveRepository archiveRepository;
	private final ArchivedBillRepository archivedBillRepository;
	private final ArchivedBillItemsRepository archivedBillItemsRepository;
	private final ArchivedBillPaymentsRepository archivedBillPaymentsRepository;

	public ArchiveIoOperations(ArchiveRepository archiveRepository,
	                           ArchivedBillRepository archivedBillRepository,
	                           ArchivedBillItemsRepository archivedBillItemsRepository,
	                           ArchivedBillPaymentsRepository archivedBillPaymentsRepository) {
		this.archiveRepository = archiveRepository;
		this.archivedBillRepository = archivedBillRepository;
		this.archivedBillItemsRepository = archivedBillItemsRepository;
		this.archivedBillPaymentsRepository = archivedBillPaymentsRepository;
	}

	/**
	 * Executes the complete billing archive task.
	 * Moves closed bills and their dependencies (items, payments, refunds) older than 365 days
	 * (or custom parameter) to historical archive tables and cleans up active tables.
	 * * @return total number of main bills archived, -2 if no bills matched criteria.
	 * @throws OHServiceException if any phase of archiving or cleaning fails.
	 */
	public int archiveClosedBills() throws OHServiceException {
		LocalDateTime currentTime = LocalDateTime.now();
		String status = "C";
		int archiveBillsWithinNbDays = 365;

		try {
			String paramValue = archiveRepository.findParameterValueByCode("ARCHIVEBILLSWITHINNBDAYS");
			if (paramValue != null) {
				archiveBillsWithinNbDays = Integer.parseInt(paramValue);
			}
		} catch (Exception e) {
			log.warn("ArchiveIoOperations: Could not read ARCHIVEBILLSWITHINNBDAYS parameter, falling back to default 365 days. {}", e.getMessage());

			if (e instanceof OHServiceException) {
				throw (OHServiceException) e;
			}

			throw new OHServiceException(
				e,
				new OHExceptionMessage("angal.accounting.archive.execution.error")
			);
		}

		int nbBillsToArchive = archiveRepository.countBillsToArchive(currentTime, archiveBillsWithinNbDays, status);
		if (nbBillsToArchive <= 0) {
			log.info("ArchiveIoOperations: No bills to archive.");
			return -2;
		}
		log.info("ArchiveIoOperations: Target main bills count to process: {}", nbBillsToArchive);

		try {
			int billsArchivedCount = archiveRepository.archiveMainBills(currentTime, archiveBillsWithinNbDays, status);
			boolean billsArchived = billsArchivedCount >= 0;

			int refundBillsToArchive = archiveRepository.countRefundBillsToArchive(currentTime, archiveBillsWithinNbDays, status);
			boolean refundBillsArchived = true;
			if (refundBillsToArchive > 0) {
				int refundCount = archiveRepository.archiveRefundBills(currentTime, archiveBillsWithinNbDays, status);
				refundBillsArchived = refundCount >= 0;
			}

			int paymentsToArchive = archiveRepository.countPaymentsToArchive(currentTime, archiveBillsWithinNbDays, status);
			boolean billPaymentsArchived = true;
			if (paymentsToArchive > 0) {
				int paymentsCount = archiveRepository.archivePayments(currentTime, archiveBillsWithinNbDays, status);
				billPaymentsArchived = paymentsCount >= 0;
			}

			int itemsToArchive = archiveRepository.countItemsToArchive(currentTime, archiveBillsWithinNbDays, status);
			boolean billItemsArchived = true;
			if (itemsToArchive > 0) {
				int itemsCount = archiveRepository.archiveItems(currentTime, archiveBillsWithinNbDays, status);
				billItemsArchived = itemsCount >= 0;
			}

			if (!(billsArchived && billPaymentsArchived && billItemsArchived && refundBillsArchived)) {
				log.error("ArchiveIoOperations: Critical write failure during data copy phase.");
				throw new OHServiceException(new OHExceptionMessage("angal.accounting.archive.copy.failed"));
			}

			boolean billPaymentsCleaned = true;
			if (paymentsToArchive > 0) {
				int cleanedPayments = archiveRepository.deletePayments(currentTime, archiveBillsWithinNbDays, status);
				billPaymentsCleaned = cleanedPayments >= 0;
			}

			boolean billItemsCleaned = true;
			if (itemsToArchive > 0) {
				int cleanedItems = archiveRepository.deleteItems(currentTime, archiveBillsWithinNbDays, status);
				billItemsCleaned = cleanedItems >= 0;
			}

			boolean refundBillsCleaned = true;
			if (refundBillsToArchive > 0) {
				int cleanedRefunds = archiveRepository.deleteRefundBills(currentTime, archiveBillsWithinNbDays, status);
				refundBillsCleaned = cleanedRefunds >= 0;
			}

			int cleanedMainBills = archiveRepository.deleteMainBills(currentTime, archiveBillsWithinNbDays, status);
			boolean billsCleaned = cleanedMainBills >= 0;

			if (!(billsCleaned && billPaymentsCleaned && billItemsCleaned && refundBillsCleaned)) {
				log.error("ArchiveIoOperations: Critical failure during active data purge phase.");
				throw new OHServiceException(new OHExceptionMessage("angal.accounting.archive.purge.failed"));
			}

			log.info("ArchiveIoOperations: Archiving workflow finished successfully. {} items moved.", nbBillsToArchive);
			return nbBillsToArchive;

		} catch (Exception e) {
			log.error(
				"ArchiveIoOperations: Exception intercepted during workflow execution. Triggering transactional fallback.",
				e
			);

			if (e instanceof OHServiceException) {
				throw (OHServiceException) e;
			}

			throw new OHServiceException(
				e,
				new OHExceptionMessage("angal.accounting.archive.execution.error")
			);
		}
	}

	// ---------------------------------------------------------------------------
	// Archived Bills query methods
	// ---------------------------------------------------------------------------

	public List<ArchivedBill> getArchivedBills() throws OHServiceException {
		return archivedBillRepository.findAllByOrderByDateDesc();
	}

	public ArchivedBill getArchivedBill(int billId) throws OHServiceException {
		return archivedBillRepository.findById(billId).orElse(null);
	}

	public List<ArchivedBill> getArchivedBills(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return archivedBillRepository.findByDateBetween(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo));
	}

	public List<ArchivedBill> getArchivedBills(LocalDateTime dateFrom, LocalDateTime dateTo, Integer patientId) throws OHServiceException {
		if (patientId == null || patientId == 0) {
			return archivedBillRepository.findByDateBetween(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo));
		}
		return archivedBillRepository.findByDateAndPatient(TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo), patientId);
	}

	public List<ArchivedBill> getArchivedPendingBills(Integer patientId) throws OHServiceException {
		if (patientId == null || patientId == 0) {
			return archivedBillRepository.findByStatusOrderByDateDesc("O");
		}
		return archivedBillRepository.findByStatusAndBillPatientIdOrderByDateDesc("O", patientId);
	}

	public List<ArchivedBill> getArchivedBillsByDatePatientAndGuarantor(LocalDateTime dateFrom, LocalDateTime dateTo,
	                                                                     Integer patientId, String guarantorId) throws OHServiceException {
		if (patientId == null) {
			return archivedBillRepository.findByDateBetweenAndGuarantorId(
				TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo), guarantorId);
		}
		return archivedBillRepository.findByDateBetweenAndBillPatientIdAndGuarantorId(
			TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo), patientId, guarantorId);
	}

	public List<ArchivedBill> getArchivedBillsWithFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                                       Integer patientId, String guarantorId) throws OHServiceException {
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;
		return archivedBillRepository.findArchivedBillsWithFilters(status, from, to, patientId, guarantorId);
	}

	public Page<ArchivedBill> getArchivedBillsWithFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                                       Integer patientId, String guarantorId, Pageable pageable) throws OHServiceException {
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;
		return archivedBillRepository.findArchivedBillsWithFilters(status, from, to, patientId, guarantorId, pageable);
	}

	public long countArchivedBillsWithFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                           Integer patientId, String guarantorId) throws OHServiceException {
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;
		return archivedBillRepository.countArchivedBillsWithFilters(status, from, to, patientId, guarantorId);
	}

	public double sumArchivedAmountByFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                          Integer patientId, String guarantorId) throws OHServiceException {
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;
		return archivedBillRepository.sumAmountByFilters(status, from, to, patientId, guarantorId);
	}

	public double sumArchivedBalanceByFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                           Integer patientId, String guarantorId) throws OHServiceException {
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;
		return archivedBillRepository.sumBalanceByFilters(status, from, to, patientId, guarantorId);
	}

	public long countAllActiveArchivedBills() throws OHServiceException {
		return archivedBillRepository.countAllActiveArchivedBills();
	}

	public List<String> getArchivedUsers() throws OHServiceException {
		Set<String> accountingUsers = new TreeSet<>(String::compareTo);
		accountingUsers.addAll(archivedBillRepository.findUserDistinctByOrderByUserAsc());
		accountingUsers.addAll(archivedBillPaymentsRepository.findUserDistinctByOrderByUserAsc());
		return new ArrayList<>(accountingUsers);
	}

	// ---------------------------------------------------------------------------
	// Archived BillItems query methods
	// ---------------------------------------------------------------------------

	public List<ArchivedBillItems> getArchivedItems(int billId) throws OHServiceException {
		if (billId == 0) {
			return new ArrayList<>();
		}
		return archivedBillItemsRepository.findByBillIdOrderByIdAsc(billId);
	}

	public List<ArchivedBillItems> getArchivedDistinctItems() throws OHServiceException {
		return archivedBillItemsRepository.findAllGroupByDescription();
	}

	public List<ArchivedBillItems> getAllArchivedBillItems(ArchivedBill bill) throws OHServiceException {
		if (bill == null || bill.getId() == 0) {
			return new ArrayList<>();
		}

		List<ArchivedBillItems> allItems = new ArrayList<>();

		List<ArchivedBillItems> mainItems = archivedBillItemsRepository.findByBillIdOrderByItemDateAsc(bill.getId());
		allItems.addAll(mainItems);

		List<ArchivedBillItems> refundItems = archivedBillItemsRepository.findByBillParentIdOrderByItemDateAsc(bill.getId());
		for (ArchivedBillItems refundItem : refundItems) {
			refundItem.setItemQuantity(-refundItem.getItemQuantity());
			allItems.add(refundItem);
		}

		allItems.sort((a, b) -> {
			if (a.getItemDate() == null || b.getItemDate() == null) return 0;
			return a.getItemDate().compareTo(b.getItemDate());
		});

		return allItems;
	}

	// ---------------------------------------------------------------------------
	// Archived BillPayments query methods
	// ---------------------------------------------------------------------------

	public List<ArchivedBillPayments> getArchivedPayments(int billId) throws OHServiceException {
		if (billId == 0) {
			return new ArrayList<>();
		}
		return archivedBillPaymentsRepository.findByBillIdOrderByIdAsc(billId);
	}

	public List<ArchivedBillPayments> getArchivedPayments(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return archivedBillPaymentsRepository.findByDateBetweenOrderByIdAscDateAsc(
			TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo));
	}

	public List<ArchivedBillPayments> getArchivedPayments(LocalDateTime dateFrom, LocalDateTime dateTo, Integer patientId) throws OHServiceException {
		if (patientId == null || patientId == 0) {
			return archivedBillPaymentsRepository.findByDateBetweenOrderByIdAscDateAsc(
				TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo));
		}
		return archivedBillPaymentsRepository.findByDateAndPatient(
			TimeTools.getBeginningOfDay(dateFrom), TimeTools.getBeginningOfNextDay(dateTo), patientId);
	}

	public List<ArchivedBillPayments> getArchivedPayments(List<ArchivedBill> bills) throws OHServiceException {
		List<ArchivedBillPayments> allPayments = new ArrayList<>();
		for (ArchivedBill bill : bills) {
			allPayments.addAll(archivedBillPaymentsRepository.findByBillIdOrderByIdAsc(bill.getId()));
		}
		return allPayments;
	}

	public List<ArchivedBillPayments> getArchivedPaymentsByDatePatientAndGuarantor(LocalDateTime dateFrom, LocalDateTime dateTo,
	                                                                                Integer patientId, String guarantorId) throws OHServiceException {
		if (dateFrom == null || dateTo == null) {
			throw new IllegalArgumentException("Date cannot be null");
		}
		LocalDateTime from = TimeTools.getBeginningOfDay(dateFrom);
		LocalDateTime to = TimeTools.getBeginningOfNextDay(dateTo);

		if (patientId == null) {
			return archivedBillPaymentsRepository.findByDateBetweenOrderByIdAscDateAsc(from, to);
		}
		return archivedBillPaymentsRepository.findByDateAndPatient(from, to, patientId);
	}

	public List<ArchivedBill> getArchivedBillsFromPayments(List<ArchivedBillPayments> payments) throws OHServiceException {
		Set<Integer> billIds = new TreeSet<>();
		for (ArchivedBillPayments bp : payments) {
			billIds.add(bp.getBillId());
		}
		List<ArchivedBill> bills = new ArrayList<>();
		for (Integer billId : billIds) {
			ArchivedBill bill = archivedBillRepository.findById(billId).orElse(null);
			if (bill != null) {
				bills.add(bill);
			}
		}
		bills.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
		return bills;
	}

	public List<ArchivedBillPayments> getAllArchivedBillPayments(ArchivedBill bill) throws OHServiceException {
		if (bill == null || bill.getId() == 0) {
			return new ArrayList<>();
		}

		List<ArchivedBillPayments> allPayments = new ArrayList<>();

		List<ArchivedBillPayments> mainPayments = archivedBillPaymentsRepository.findByBillIdOrderByDateAsc(bill.getId());
		allPayments.addAll(mainPayments);

		List<ArchivedBillPayments> refundPayments = archivedBillPaymentsRepository.findByBillParentIdOrderByDateAsc(bill.getId());
		allPayments.addAll(refundPayments);

		allPayments.sort((a, b) -> {
			if (a.getDate() == null || b.getDate() == null) return 0;
			return a.getDate().compareTo(b.getDate());
		});

		return allPayments;
	}

	public double sumArchivedPaymentsByFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                            Integer patientId, String guarantorId) throws OHServiceException {
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;
		return archivedBillPaymentsRepository.sumPaymentsByFilters(status, from, to, patientId, guarantorId);
	}

	public double sumArchivedPaymentsByUserAndFilters(String username, String status, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                                   Integer patientId, String guarantorId) throws OHServiceException {
		LocalDateTime from = dateFrom != null ? TimeTools.getBeginningOfDay(dateFrom) : null;
		LocalDateTime to = dateTo != null ? TimeTools.getBeginningOfNextDay(dateTo) : null;
		return archivedBillPaymentsRepository.sumPaymentsByUserAndFilters(username, status, from, to, patientId, guarantorId);
	}
}
