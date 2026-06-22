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
import java.util.List;

import org.isf.accounting.model.ArchivedBill;
import org.isf.accounting.model.ArchivedBillItems;
import org.isf.accounting.model.ArchivedBillPayments;
import org.isf.accounting.service.archive.ArchiveIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class ArchiveManager {

	private final ArchiveIoOperations archiveIoOperations;

	public ArchiveManager(ArchiveIoOperations archiveIoOperations) {
		this.archiveIoOperations = archiveIoOperations;
	}

	public int archiveClosedBills() throws OHServiceException {
		return archiveIoOperations.archiveClosedBills();
	}

	// ---------------------------------------------------------------------------
	// Archived Bills
	// ---------------------------------------------------------------------------

	public List<ArchivedBill> getArchivedBills() throws OHServiceException {
		return archiveIoOperations.getArchivedBills();
	}

	public ArchivedBill getArchivedBill(int billId) throws OHServiceException {
		return archiveIoOperations.getArchivedBill(billId);
	}

	public List<ArchivedBill> getArchivedBills(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return archiveIoOperations.getArchivedBills(dateFrom, dateTo);
	}

	public List<ArchivedBill> getArchivedBills(LocalDateTime dateFrom, LocalDateTime dateTo, Integer patientId) throws OHServiceException {
		return archiveIoOperations.getArchivedBills(dateFrom, dateTo, patientId);
	}

	public List<ArchivedBill> getArchivedPendingBills(Integer patientId) throws OHServiceException {
		return archiveIoOperations.getArchivedPendingBills(patientId);
	}

	public List<ArchivedBill> getArchivedBillsByDatePatientAndGuarantor(LocalDateTime dateFrom, LocalDateTime dateTo,
	                                                                     Integer patientId, String guarantorId) throws OHServiceException {
		return archiveIoOperations.getArchivedBillsByDatePatientAndGuarantor(dateFrom, dateTo, patientId, guarantorId);
	}

	public List<ArchivedBill> getArchivedBillsWithFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                                       Integer patientId, String guarantorId) throws OHServiceException {
		return archiveIoOperations.getArchivedBillsWithFilters(status, dateFrom, dateTo, patientId, guarantorId);
	}

	public Page<ArchivedBill> getArchivedBillsWithFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                                       Integer patientId, String guarantorId, int page, int size) throws OHServiceException {
		Pageable pageable = PageRequest.of(page, size);
		return archiveIoOperations.getArchivedBillsWithFilters(status, dateFrom, dateTo, patientId, guarantorId, pageable);
	}

	public long countArchivedBillsWithFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                           Integer patientId, String guarantorId) throws OHServiceException {
		return archiveIoOperations.countArchivedBillsWithFilters(status, dateFrom, dateTo, patientId, guarantorId);
	}

	public double sumArchivedAmountByFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                          Integer patientId, String guarantorId) throws OHServiceException {
		return archiveIoOperations.sumArchivedAmountByFilters(status, dateFrom, dateTo, patientId, guarantorId);
	}

	public double sumArchivedBalanceByFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                           Integer patientId, String guarantorId) throws OHServiceException {
		return archiveIoOperations.sumArchivedBalanceByFilters(status, dateFrom, dateTo, patientId, guarantorId);
	}

	public long countAllActiveArchivedBills() throws OHServiceException {
		return archiveIoOperations.countAllActiveArchivedBills();
	}

	public List<String> getArchivedUsers() throws OHServiceException {
		return archiveIoOperations.getArchivedUsers();
	}

	// ---------------------------------------------------------------------------
	// Archived BillItems
	// ---------------------------------------------------------------------------

	public List<ArchivedBillItems> getArchivedItems(int billId) throws OHServiceException {
		return archiveIoOperations.getArchivedItems(billId);
	}

	public List<ArchivedBillItems> getArchivedDistinctItems() throws OHServiceException {
		return archiveIoOperations.getArchivedDistinctItems();
	}

	public List<ArchivedBillItems> getAllArchivedBillItems(ArchivedBill bill) throws OHServiceException {
		return archiveIoOperations.getAllArchivedBillItems(bill);
	}

	// ---------------------------------------------------------------------------
	// Archived BillPayments
	// ---------------------------------------------------------------------------

	public List<ArchivedBillPayments> getArchivedPayments(int billId) throws OHServiceException {
		return archiveIoOperations.getArchivedPayments(billId);
	}

	public List<ArchivedBillPayments> getArchivedPayments(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return archiveIoOperations.getArchivedPayments(dateFrom, dateTo);
	}

	public List<ArchivedBillPayments> getArchivedPayments(LocalDateTime dateFrom, LocalDateTime dateTo, Integer patientId) throws OHServiceException {
		return archiveIoOperations.getArchivedPayments(dateFrom, dateTo, patientId);
	}

	public List<ArchivedBillPayments> getArchivedPayments(List<ArchivedBill> bills) throws OHServiceException {
		return archiveIoOperations.getArchivedPayments(bills);
	}

	public List<ArchivedBillPayments> getArchivedPaymentsByDatePatientAndGuarantor(LocalDateTime dateFrom, LocalDateTime dateTo,
	                                                                                Integer patientId, String guarantorId) throws OHServiceException {
		return archiveIoOperations.getArchivedPaymentsByDatePatientAndGuarantor(dateFrom, dateTo, patientId, guarantorId);
	}

	public List<ArchivedBill> getArchivedBillsFromPayments(List<ArchivedBillPayments> payments) throws OHServiceException {
		return archiveIoOperations.getArchivedBillsFromPayments(payments);
	}

	public List<ArchivedBillPayments> getAllArchivedBillPayments(ArchivedBill bill) throws OHServiceException {
		return archiveIoOperations.getAllArchivedBillPayments(bill);
	}

	public double sumArchivedPaymentsByFilters(String status, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                            Integer patientId, String guarantorId) throws OHServiceException {
		return archiveIoOperations.sumArchivedPaymentsByFilters(status, dateFrom, dateTo, patientId, guarantorId);
	}

	public double sumArchivedPaymentsByUserAndFilters(String username, String status, LocalDateTime dateFrom, LocalDateTime dateTo,
	                                                   Integer patientId, String guarantorId) throws OHServiceException {
		return archiveIoOperations.sumArchivedPaymentsByUserAndFilters(username, status, dateFrom, dateTo, patientId, guarantorId);
	}
}
