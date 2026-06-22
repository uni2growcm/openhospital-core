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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.accounting.model.ArchivedBillPayments;
import org.isf.accounting.service.archive.ArchiveIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ArchiveManagerTest {

	@Mock
	private ArchiveIoOperations archiveIoOperations;

	@Captor
	private ArgumentCaptor<Integer> intCaptor;

	@Captor
	private ArgumentCaptor<LocalDateTime> dateFromCaptor;

	@Captor
	private ArgumentCaptor<LocalDateTime> dateToCaptor;

	@Captor
	private ArgumentCaptor<String> stringCaptor;

	private ArchiveManager archiveManager;
	private AutoCloseable closeable;

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		archiveManager = new ArchiveManager(archiveIoOperations);
	}

	@AfterEach
	void tearDown() throws Exception {
		closeable.close();
	}

	// ==================== archiveClosedBills ====================

	@Test
	void testArchiveClosedBillsDelegatesToIoOperations() throws Exception {
		archiveManager.archiveClosedBills();
		verify(archiveIoOperations).archiveClosedBills();
	}

	@Test
	void testArchiveClosedBillsPropagatesException() throws Exception {
		doThrow(new OHServiceException(new OHExceptionMessage("error")))
			.when(archiveIoOperations).archiveClosedBills();

		assertThatThrownBy(() -> archiveManager.archiveClosedBills())
			.isInstanceOf(OHServiceException.class);
	}

	@Test
	void testArchiveClosedBillsDoesNotThrowWhenIoOperationsSucceeds() throws Exception {
		assertThatCode(() -> archiveManager.archiveClosedBills())
			.doesNotThrowAnyException();
	}

	// ==================== Archived Bills ====================

	@Test
	void testGetArchivedBillsDelegatesToIoOperations() throws Exception {
		archiveManager.getArchivedBills();
		verify(archiveIoOperations).getArchivedBills();
	}

	@Test
	void testGetArchivedBillDelegatesToIoOperations() throws Exception {
		archiveManager.getArchivedBill(123);
		verify(archiveIoOperations).getArchivedBill(123);
	}

	@Test
	void testGetArchivedBillsByDateRangeDelegatesToIoOperations() throws Exception {
		LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);
		archiveManager.getArchivedBills(from, to);
		verify(archiveIoOperations).getArchivedBills(from, to);
	}

	@Test
	void testGetArchivedBillsByDateRangeAndPatientDelegatesToIoOperations() throws Exception {
		LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);
		archiveManager.getArchivedBills(from, to, 456);
		verify(archiveIoOperations).getArchivedBills(from, to, 456);
	}

	@Test
	void testGetArchivedPendingBillsDelegatesToIoOperations() throws Exception {
		archiveManager.getArchivedPendingBills(789);
		verify(archiveIoOperations).getArchivedPendingBills(789);
	}

	@Test
	void testGetArchivedBillsByDatePatientAndGuarantorDelegatesToIoOperations() throws Exception {
		LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);
		archiveManager.getArchivedBillsByDatePatientAndGuarantor(from, to, 456, "guarantor1");
		verify(archiveIoOperations).getArchivedBillsByDatePatientAndGuarantor(from, to, 456, "guarantor1");
	}

	@Test
	void testGetArchivedBillsWithFiltersDelegatesToIoOperations() throws Exception {
		LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);
		archiveManager.getArchivedBillsWithFilters("C", from, to, 456, "guarantor1");
		verify(archiveIoOperations).getArchivedBillsWithFilters("C", from, to, 456, "guarantor1");
	}

	@Test
	void testCountArchivedBillsWithFiltersDelegatesToIoOperations() throws Exception {
		LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);
		archiveManager.countArchivedBillsWithFilters("O", from, to, null, null);
		verify(archiveIoOperations).countArchivedBillsWithFilters("O", from, to, null, null);
	}

	@Test
	void testSumArchivedAmountByFiltersDelegatesToIoOperations() throws Exception {
		LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);
		archiveManager.sumArchivedAmountByFilters(null, from, to, null, null);
		verify(archiveIoOperations).sumArchivedAmountByFilters(null, from, to, null, null);
	}

	@Test
	void testSumArchivedBalanceByFiltersDelegatesToIoOperations() throws Exception {
		LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);
		archiveManager.sumArchivedBalanceByFilters("C", from, to, 456, "g1");
		verify(archiveIoOperations).sumArchivedBalanceByFilters("C", from, to, 456, "g1");
	}

	@Test
	void testCountAllActiveArchivedBillsDelegatesToIoOperations() throws Exception {
		archiveManager.countAllActiveArchivedBills();
		verify(archiveIoOperations).countAllActiveArchivedBills();
	}

	@Test
	void testGetArchivedUsersDelegatesToIoOperations() throws Exception {
		archiveManager.getArchivedUsers();
		verify(archiveIoOperations).getArchivedUsers();
	}

	// ==================== Archived BillItems ====================

	@Test
	void testGetArchivedItemsDelegatesToIoOperations() throws Exception {
		archiveManager.getArchivedItems(123);
		verify(archiveIoOperations).getArchivedItems(123);
	}

	@Test
	void testGetArchivedDistinctItemsDelegatesToIoOperations() throws Exception {
		archiveManager.getArchivedDistinctItems();
		verify(archiveIoOperations).getArchivedDistinctItems();
	}

	@Test
	void testGetAllArchivedBillItemsDelegatesToIoOperations() throws Exception {
		archiveManager.getAllArchivedBillItems(null);
		verify(archiveIoOperations).getAllArchivedBillItems(null);
	}

	// ==================== Archived BillPayments ====================

	@Test
	void testGetArchivedPaymentsByBillIdDelegatesToIoOperations() throws Exception {
		archiveManager.getArchivedPayments(123);
		verify(archiveIoOperations).getArchivedPayments(123);
	}

	@Test
	void testGetArchivedPaymentsByDateRangeDelegatesToIoOperations() throws Exception {
		LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);
		archiveManager.getArchivedPayments(from, to);
		verify(archiveIoOperations).getArchivedPayments(from, to);
	}

	@Test
	void testGetArchivedPaymentsByDateRangeAndPatientDelegatesToIoOperations() throws Exception {
		LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);
		archiveManager.getArchivedPayments(from, to, 456);
		verify(archiveIoOperations).getArchivedPayments(from, to, 456);
	}

	@Test
	void testGetArchivedPaymentsByBillsDelegatesToIoOperations() throws Exception {
		archiveManager.getArchivedPayments(List.of());
		verify(archiveIoOperations).getArchivedPayments(List.of());
	}

	@Test
	void testGetArchivedPaymentsByDatePatientAndGuarantorDelegatesToIoOperations() throws Exception {
		LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);
		archiveManager.getArchivedPaymentsByDatePatientAndGuarantor(from, to, 456, "g1");
		verify(archiveIoOperations).getArchivedPaymentsByDatePatientAndGuarantor(from, to, 456, "g1");
	}

	@Test
	void testGetArchivedBillsFromPaymentsDelegatesToIoOperations() throws Exception {
		List<ArchivedBillPayments> payments = List.of();
		archiveManager.getArchivedBillsFromPayments(payments);
		verify(archiveIoOperations).getArchivedBillsFromPayments(payments);
	}

	@Test
	void testGetAllArchivedBillPaymentsDelegatesToIoOperations() throws Exception {
		archiveManager.getAllArchivedBillPayments(null);
		verify(archiveIoOperations).getAllArchivedBillPayments(null);
	}

	@Test
	void testSumArchivedPaymentsByFiltersDelegatesToIoOperations() throws Exception {
		LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);
		archiveManager.sumArchivedPaymentsByFilters("C", from, to, null, null);
		verify(archiveIoOperations).sumArchivedPaymentsByFilters("C", from, to, null, null);
	}

	@Test
	void testSumArchivedPaymentsByUserAndFiltersDelegatesToIoOperations() throws Exception {
		LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);
		archiveManager.sumArchivedPaymentsByUserAndFilters("user1", "C", from, to, null, null);
		verify(archiveIoOperations).sumArchivedPaymentsByUserAndFilters("user1", "C", from, to, null, null);
	}
}
