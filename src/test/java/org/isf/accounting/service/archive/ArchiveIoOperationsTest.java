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
package org.isf.accounting.service.archive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.isf.accounting.model.ArchivedBill;
import org.isf.accounting.model.ArchivedBillItems;
import org.isf.accounting.model.ArchivedBillPayments;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ArchiveIoOperationsTest {

	@Mock
	private ArchiveRepository archiveRepository;

	@Mock
	private ArchivedBillRepository archivedBillRepository;

	@Mock
	private ArchivedBillItemsRepository archivedBillItemsRepository;

	@Mock
	private ArchivedBillPaymentsRepository archivedBillPaymentsRepository;

	@Captor
	private ArgumentCaptor<LocalDateTime> currentTimeCaptor;

	@Captor
	private ArgumentCaptor<Integer> nbDaysCaptor;

	@Captor
	private ArgumentCaptor<String> statusCaptor;

	private ArchiveIoOperations archiveIoOperations;
	private AutoCloseable closeable;

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		archiveIoOperations = new ArchiveIoOperations(archiveRepository, archivedBillRepository, archivedBillItemsRepository, archivedBillPaymentsRepository);
	}

	@AfterEach
	void tearDown() throws Exception {
		closeable.close();
	}

	@Test
	void testArchiveClosedBills_NoBillsToArchive_ReturnsMinusTwo() throws Exception {
		when(archiveRepository.findParameterValueByCode(anyString())).thenReturn(null);
		when(archiveRepository.countBillsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(0);

		int result = archiveIoOperations.archiveClosedBills();

		assertThat(result).isEqualTo(-2);
		verify(archiveRepository).countBillsToArchive(any(LocalDateTime.class), anyInt(), anyString());
		verify(archiveRepository, never()).archiveMainBills(any(LocalDateTime.class), anyInt(), anyString());
		verify(archiveRepository, never()).deleteMainBills(any(LocalDateTime.class), anyInt(), anyString());
	}

	@Test
	void testArchiveClosedBills_SuccessWithDefaultParameter_ReturnsCount() throws Exception {
		when(archiveRepository.findParameterValueByCode(anyString())).thenReturn(null);
		when(archiveRepository.countBillsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(5);
		when(archiveRepository.archiveMainBills(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(5);
		when(archiveRepository.countRefundBillsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(0);
		when(archiveRepository.countPaymentsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(0);
		when(archiveRepository.countItemsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(0);
		when(archiveRepository.deleteMainBills(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(5);

		int result = archiveIoOperations.archiveClosedBills();

		assertThat(result).isEqualTo(5);
		verify(archiveRepository).archiveMainBills(any(LocalDateTime.class), anyInt(), anyString());
		verify(archiveRepository).deleteMainBills(any(LocalDateTime.class), anyInt(), anyString());
		verify(archiveRepository, never()).archiveRefundBills(any(LocalDateTime.class), anyInt(), anyString());
		verify(archiveRepository, never()).archivePayments(any(LocalDateTime.class), anyInt(), anyString());
		verify(archiveRepository, never()).archiveItems(any(LocalDateTime.class), anyInt(), anyString());
		verify(archiveRepository, never()).deleteRefundBills(any(LocalDateTime.class), anyInt(), anyString());
		verify(archiveRepository, never()).deletePayments(any(LocalDateTime.class), anyInt(), anyString());
		verify(archiveRepository, never()).deleteItems(any(LocalDateTime.class), anyInt(), anyString());
	}

	@Test
	void testArchiveClosedBills_SuccessWithCustomParameter_ReturnsCount() throws Exception {
		when(archiveRepository.findParameterValueByCode(anyString())).thenReturn("180");
		when(archiveRepository.countBillsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(3);
		when(archiveRepository.archiveMainBills(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(3);
		when(archiveRepository.countRefundBillsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(0);
		when(archiveRepository.countPaymentsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(0);
		when(archiveRepository.countItemsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(0);
		when(archiveRepository.deleteMainBills(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(3);

		int result = archiveIoOperations.archiveClosedBills();

		assertThat(result).isEqualTo(3);

		verify(archiveRepository).archiveMainBills(currentTimeCaptor.capture(), nbDaysCaptor.capture(), statusCaptor.capture());
		assertThat(nbDaysCaptor.getValue()).isEqualTo(180);
		assertThat(statusCaptor.getValue()).isEqualTo("C");
	}

	@Test
	void testArchiveClosedBills_WithRefundsAndPayments_ProcessesAll() throws Exception {
		when(archiveRepository.findParameterValueByCode(anyString())).thenReturn(null);
		when(archiveRepository.countBillsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(10);
		when(archiveRepository.archiveMainBills(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(10);
		when(archiveRepository.countRefundBillsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(2);
		when(archiveRepository.archiveRefundBills(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(2);
		when(archiveRepository.countPaymentsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(15);
		when(archiveRepository.archivePayments(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(15);
		when(archiveRepository.countItemsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(25);
		when(archiveRepository.archiveItems(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(25);
		when(archiveRepository.deletePayments(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(15);
		when(archiveRepository.deleteItems(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(25);
		when(archiveRepository.deleteRefundBills(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(2);
		when(archiveRepository.deleteMainBills(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(10);

		int result = archiveIoOperations.archiveClosedBills();

		assertThat(result).isEqualTo(10);
		verify(archiveRepository).archiveRefundBills(any(LocalDateTime.class), anyInt(), anyString());
		verify(archiveRepository).archivePayments(any(LocalDateTime.class), anyInt(), anyString());
		verify(archiveRepository).archiveItems(any(LocalDateTime.class), anyInt(), anyString());
		verify(archiveRepository).deletePayments(any(LocalDateTime.class), anyInt(), anyString());
		verify(archiveRepository).deleteItems(any(LocalDateTime.class), anyInt(), anyString());
		verify(archiveRepository).deleteRefundBills(any(LocalDateTime.class), anyInt(), anyString());
		verify(archiveRepository).deleteMainBills(any(LocalDateTime.class), anyInt(), anyString());
	}

	@Test
	void testArchiveClosedBills_WhenParameterThrowsRuntimeException_WrapsIt() {
		when(archiveRepository.findParameterValueByCode(anyString()))
			.thenThrow(new RuntimeException("db error"));

		assertThatThrownBy(() -> archiveIoOperations.archiveClosedBills())
			.isInstanceOf(OHServiceException.class);
	}

	@Test
	void testArchiveClosedBills_WhenCopyFails_ThrowsOhServiceException() throws Exception {
		when(archiveRepository.findParameterValueByCode(anyString())).thenReturn(null);
		when(archiveRepository.countBillsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(5);
		when(archiveRepository.archiveMainBills(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(-1);
		when(archiveRepository.countRefundBillsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(0);
		when(archiveRepository.countPaymentsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(0);
		when(archiveRepository.countItemsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(0);

		assertThatThrownBy(() -> archiveIoOperations.archiveClosedBills())
			.isInstanceOf(OHServiceException.class);

		verify(archiveRepository, never()).deleteMainBills(any(LocalDateTime.class), anyInt(), anyString());
	}

	@Test
	void testArchiveClosedBills_WhenPurgeFails_ThrowsOhServiceException() throws Exception {
		when(archiveRepository.findParameterValueByCode(anyString())).thenReturn(null);
		when(archiveRepository.countBillsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(5);
		when(archiveRepository.archiveMainBills(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(5);
		when(archiveRepository.countRefundBillsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(0);
		when(archiveRepository.countPaymentsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(0);
		when(archiveRepository.countItemsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(0);
		when(archiveRepository.deleteMainBills(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(-1);

		assertThatThrownBy(() -> archiveIoOperations.archiveClosedBills())
			.isInstanceOf(OHServiceException.class);
	}

	@Test
	void testArchiveClosedBills_CountReturnsNegative_ReturnsMinusTwo() throws Exception {
		when(archiveRepository.findParameterValueByCode(anyString())).thenReturn(null);
		when(archiveRepository.countBillsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(-1);

		int result = archiveIoOperations.archiveClosedBills();

		assertThat(result).isEqualTo(-2);
		verify(archiveRepository, never()).archiveMainBills(any(LocalDateTime.class), anyInt(), anyString());
	}

	@Test
	void testArchiveClosedBills_WhenInnerOperationThrowsRuntimeException_WrapsIt() throws Exception {
		when(archiveRepository.findParameterValueByCode(anyString())).thenReturn(null);
		when(archiveRepository.countBillsToArchive(any(LocalDateTime.class), anyInt(), anyString())).thenReturn(5);
		when(archiveRepository.archiveMainBills(any(LocalDateTime.class), anyInt(), anyString()))
			.thenThrow(new RuntimeException("archive error"));

		assertThatThrownBy(() -> archiveIoOperations.archiveClosedBills())
			.isInstanceOf(OHServiceException.class);
	}

	// ==================================================================================
	// Archived Bills query tests
	// ==================================================================================

	@Test
	void testGetArchivedBills_ReturnsAllBills() throws Exception {
		List<ArchivedBill> expected = List.of(new ArchivedBill(), new ArchivedBill());
		when(archivedBillRepository.findAllByOrderByDateDesc()).thenReturn(expected);

		List<ArchivedBill> result = archiveIoOperations.getArchivedBills();

		assertThat(result).hasSize(2);
		verify(archivedBillRepository).findAllByOrderByDateDesc();
	}

	@Test
	void testGetArchivedBill_WithValidId_ReturnsBill() throws Exception {
		ArchivedBill expected = new ArchivedBill();
		expected.setId(123);
		when(archivedBillRepository.findById(123)).thenReturn(Optional.of(expected));

		ArchivedBill result = archiveIoOperations.getArchivedBill(123);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(123);
	}

	@Test
	void testGetArchivedBill_WithInvalidId_ReturnsNull() throws Exception {
		when(archivedBillRepository.findById(999)).thenReturn(Optional.empty());

		ArchivedBill result = archiveIoOperations.getArchivedBill(999);

		assertThat(result).isNull();
	}

	@Test
	void testGetArchivedBillsByDateRange_DelegatesToRepository() throws Exception {
		LocalDateTime dateFrom = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(2024, 12, 31, 23, 59);
		when(archivedBillRepository.findByDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
			.thenReturn(List.of(new ArchivedBill()));

		List<ArchivedBill> result = archiveIoOperations.getArchivedBills(dateFrom, dateTo);

		assertThat(result).hasSize(1);
		verify(archivedBillRepository).findByDateBetween(any(LocalDateTime.class), any(LocalDateTime.class));
	}

	@Test
	void testGetArchivedBillsByDateRangeAndPatientId_WithPatientId_DelegatesCorrectly() throws Exception {
		LocalDateTime dateFrom = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(2024, 12, 31, 23, 59);
		when(archivedBillRepository.findByDateAndPatient(any(LocalDateTime.class), any(LocalDateTime.class), anyInt()))
			.thenReturn(List.of(new ArchivedBill()));

		List<ArchivedBill> result = archiveIoOperations.getArchivedBills(dateFrom, dateTo, 456);

		assertThat(result).hasSize(1);
		verify(archivedBillRepository).findByDateAndPatient(any(LocalDateTime.class), any(LocalDateTime.class), anyInt());
	}

	@Test
	void testGetArchivedBillsByDateRangeAndPatientId_WithNullPatientId_DelegatesToDateRange() throws Exception {
		LocalDateTime dateFrom = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(2024, 12, 31, 23, 59);
		when(archivedBillRepository.findByDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
			.thenReturn(List.of());

		List<ArchivedBill> result = archiveIoOperations.getArchivedBills(dateFrom, dateTo, null);

		assertThat(result).isEmpty();
		verify(archivedBillRepository).findByDateBetween(any(LocalDateTime.class), any(LocalDateTime.class));
	}

	@Test
	void testGetArchivedPendingBills_WithPatientId_DelegatesCorrectly() throws Exception {
		when(archivedBillRepository.findByStatusAndBillPatientIdOrderByDateDesc("O", 789))
			.thenReturn(List.of(new ArchivedBill()));

		List<ArchivedBill> result = archiveIoOperations.getArchivedPendingBills(789);

		assertThat(result).hasSize(1);
		verify(archivedBillRepository).findByStatusAndBillPatientIdOrderByDateDesc("O", 789);
	}

	@Test
	void testGetArchivedPendingBills_WithNullPatientId_DelegatesToStatusOnly() throws Exception {
		when(archivedBillRepository.findByStatusOrderByDateDesc("O"))
			.thenReturn(List.of());

		List<ArchivedBill> result = archiveIoOperations.getArchivedPendingBills(null);

		assertThat(result).isEmpty();
		verify(archivedBillRepository).findByStatusOrderByDateDesc("O");
	}

	@Test
	void testGetArchivedBillsByDatePatientAndGuarantor_WithPatientId_DelegatesCorrectly() throws Exception {
		LocalDateTime dateFrom = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(2024, 12, 31, 23, 59);
		when(archivedBillRepository.findByDateBetweenAndBillPatientIdAndGuarantorId(
			any(LocalDateTime.class), any(LocalDateTime.class), anyInt(), anyString()))
			.thenReturn(List.of(new ArchivedBill()));

		List<ArchivedBill> result = archiveIoOperations.getArchivedBillsByDatePatientAndGuarantor(dateFrom, dateTo, 456, "g1");

		assertThat(result).hasSize(1);
		verify(archivedBillRepository).findByDateBetweenAndBillPatientIdAndGuarantorId(
			any(LocalDateTime.class), any(LocalDateTime.class), anyInt(), anyString());
	}

	@Test
	void testGetArchivedBillsByDatePatientAndGuarantor_WithNullPatientId_DelegatesToGuarantorOnly() throws Exception {
		LocalDateTime dateFrom = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(2024, 12, 31, 23, 59);
		when(archivedBillRepository.findByDateBetweenAndGuarantorId(
			any(LocalDateTime.class), any(LocalDateTime.class), anyString()))
			.thenReturn(List.of());

		List<ArchivedBill> result = archiveIoOperations.getArchivedBillsByDatePatientAndGuarantor(dateFrom, dateTo, null, "g1");

		assertThat(result).isEmpty();
		verify(archivedBillRepository).findByDateBetweenAndGuarantorId(
			any(LocalDateTime.class), any(LocalDateTime.class), anyString());
	}

	@Test
	void testGetArchivedBillsWithFilters_DelegatesToRepository() throws Exception {
		LocalDateTime dateFrom = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(2024, 12, 31, 23, 59);
		when(archivedBillRepository.findArchivedBillsWithFilters(any(), any(), any(), any(), any()))
			.thenReturn(List.of(new ArchivedBill()));

		List<ArchivedBill> result = archiveIoOperations.getArchivedBillsWithFilters("C", dateFrom, dateTo, 456, "g1");

		assertThat(result).hasSize(1);
		verify(archivedBillRepository).findArchivedBillsWithFilters(any(), any(), any(), any(), any());
	}

	@Test
	void testGetArchivedBillsWithFilters_WithNullDates_PassesNullToRepository() throws Exception {
		when(archivedBillRepository.findArchivedBillsWithFilters(any(), any(), any(), any(), any()))
			.thenReturn(List.of());

		List<ArchivedBill> result = archiveIoOperations.getArchivedBillsWithFilters("C", null, null, null, null);

		assertThat(result).isEmpty();
		verify(archivedBillRepository).findArchivedBillsWithFilters("C", null, null, null, null);
	}

	@Test
	void testCountArchivedBillsWithFilters_DelegatesToRepository() throws Exception {
		when(archivedBillRepository.countArchivedBillsWithFilters(any(), any(), any(), any(), any()))
			.thenReturn(5L);

		long result = archiveIoOperations.countArchivedBillsWithFilters(null, null, null, null, null);

		assertThat(result).isEqualTo(5);
		verify(archivedBillRepository).countArchivedBillsWithFilters(null, null, null, null, null);
	}

	@Test
	void testSumArchivedAmountByFilters_DelegatesToRepository() throws Exception {
		when(archivedBillRepository.sumAmountByFilters(any(), any(), any(), any(), any()))
			.thenReturn(1000.50);

		double result = archiveIoOperations.sumArchivedAmountByFilters("C", null, null, null, null);

		assertThat(result).isCloseTo(1000.50, org.assertj.core.data.Offset.offset(0.01));
	}

	@Test
	void testSumArchivedBalanceByFilters_DelegatesToRepository() throws Exception {
		when(archivedBillRepository.sumBalanceByFilters(any(), any(), any(), any(), any()))
			.thenReturn(500.25);

		double result = archiveIoOperations.sumArchivedBalanceByFilters("O", null, null, null, null);

		assertThat(result).isCloseTo(500.25, org.assertj.core.data.Offset.offset(0.01));
	}

	@Test
	void testCountAllActiveArchivedBills_DelegatesToRepository() throws Exception {
		when(archivedBillRepository.countAllActiveArchivedBills()).thenReturn(10L);

		long result = archiveIoOperations.countAllActiveArchivedBills();

		assertThat(result).isEqualTo(10);
		verify(archivedBillRepository).countAllActiveArchivedBills();
	}

	@Test
	void testGetArchivedUsers_ReturnsDistinctUsersFromBothRepositories() throws Exception {
		when(archivedBillRepository.findUserDistinctByOrderByUserAsc()).thenReturn(List.of("user1", "user2"));
		when(archivedBillPaymentsRepository.findUserDistinctByOrderByUserAsc()).thenReturn(List.of("user2", "user3"));

		List<String> result = archiveIoOperations.getArchivedUsers();

		assertThat(result).containsExactlyInAnyOrder("user1", "user2", "user3");
		verify(archivedBillRepository).findUserDistinctByOrderByUserAsc();
		verify(archivedBillPaymentsRepository).findUserDistinctByOrderByUserAsc();
	}

	// ==================================================================================
	// Archived BillItems query tests
	// ==================================================================================

	@Test
	void testGetArchivedItems_WithValidBillId_ReturnsItems() throws Exception {
		when(archivedBillItemsRepository.findByBillIdOrderByIdAsc(123))
			.thenReturn(List.of(new ArchivedBillItems()));

		List<ArchivedBillItems> result = archiveIoOperations.getArchivedItems(123);

		assertThat(result).hasSize(1);
		verify(archivedBillItemsRepository).findByBillIdOrderByIdAsc(123);
	}

	@Test
	void testGetArchivedItems_WithBillIdZero_ReturnsEmptyList() throws Exception {
		List<ArchivedBillItems> result = archiveIoOperations.getArchivedItems(0);

		assertThat(result).isEmpty();
		verify(archivedBillItemsRepository, never()).findByBillIdOrderByIdAsc(anyInt());
	}

	@Test
	void testGetArchivedDistinctItems_DelegatesToRepository() throws Exception {
		when(archivedBillItemsRepository.findAllGroupByDescription())
			.thenReturn(List.of(new ArchivedBillItems()));

		List<ArchivedBillItems> result = archiveIoOperations.getArchivedDistinctItems();

		assertThat(result).hasSize(1);
		verify(archivedBillItemsRepository).findAllGroupByDescription();
	}

	@Test
	void testGetAllArchivedBillItems_WithMainAndRefundItems_ReturnsCombinedList() throws Exception {
		ArchivedBill bill = new ArchivedBill();
		bill.setId(1);

		ArchivedBillItems mainItem = new ArchivedBillItems();
		mainItem.setItemQuantity(5);
		mainItem.setItemDate(LocalDateTime.of(2024, 6, 1, 12, 0));

		ArchivedBillItems refundItem = new ArchivedBillItems();
		refundItem.setItemQuantity(2);
		refundItem.setItemDate(LocalDateTime.of(2024, 6, 2, 12, 0));

		when(archivedBillItemsRepository.findByBillIdOrderByItemDateAsc(1)).thenReturn(List.of(mainItem));
		when(archivedBillItemsRepository.findByBillParentIdOrderByItemDateAsc(1)).thenReturn(List.of(refundItem));

		List<ArchivedBillItems> result = archiveIoOperations.getAllArchivedBillItems(bill);

		assertThat(result).hasSize(2);
		assertThat(result.get(0).getItemQuantity()).isEqualTo(5);
		assertThat(result.get(1).getItemQuantity()).isEqualTo(-2);
	}

	@Test
	void testGetAllArchivedBillItems_WithNullBill_ReturnsEmptyList() throws Exception {
		List<ArchivedBillItems> result = archiveIoOperations.getAllArchivedBillItems(null);

		assertThat(result).isEmpty();
	}

	// ==================================================================================
	// Archived BillPayments query tests
	// ==================================================================================

	@Test
	void testGetArchivedPaymentsByBillId_WithValidId_ReturnsPayments() throws Exception {
		when(archivedBillPaymentsRepository.findByBillIdOrderByIdAsc(123))
			.thenReturn(List.of(new ArchivedBillPayments()));

		List<ArchivedBillPayments> result = archiveIoOperations.getArchivedPayments(123);

		assertThat(result).hasSize(1);
		verify(archivedBillPaymentsRepository).findByBillIdOrderByIdAsc(123);
	}

	@Test
	void testGetArchivedPaymentsByBillId_WithZeroId_ReturnsEmptyList() throws Exception {
		List<ArchivedBillPayments> result = archiveIoOperations.getArchivedPayments(0);

		assertThat(result).isEmpty();
		verify(archivedBillPaymentsRepository, never()).findByBillIdOrderByIdAsc(anyInt());
	}

	@Test
	void testGetArchivedPaymentsByDateRange_DelegatesToRepository() throws Exception {
		LocalDateTime dateFrom = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(2024, 12, 31, 23, 59);
		when(archivedBillPaymentsRepository.findByDateBetweenOrderByIdAscDateAsc(any(LocalDateTime.class), any(LocalDateTime.class)))
			.thenReturn(List.of(new ArchivedBillPayments()));

		List<ArchivedBillPayments> result = archiveIoOperations.getArchivedPayments(dateFrom, dateTo);

		assertThat(result).hasSize(1);
		verify(archivedBillPaymentsRepository).findByDateBetweenOrderByIdAscDateAsc(any(LocalDateTime.class), any(LocalDateTime.class));
	}

	@Test
	void testGetArchivedPaymentsByDateRangeAndPatientId_WithPatientId_DelegatesCorrectly() throws Exception {
		LocalDateTime dateFrom = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(2024, 12, 31, 23, 59);
		when(archivedBillPaymentsRepository.findByDateAndPatient(any(LocalDateTime.class), any(LocalDateTime.class), anyInt()))
			.thenReturn(List.of(new ArchivedBillPayments()));

		List<ArchivedBillPayments> result = archiveIoOperations.getArchivedPayments(dateFrom, dateTo, 456);

		assertThat(result).hasSize(1);
		verify(archivedBillPaymentsRepository).findByDateAndPatient(any(LocalDateTime.class), any(LocalDateTime.class), anyInt());
	}

	@Test
	void testGetArchivedPaymentsByDateRangeAndPatientId_WithNullPatientId_DelegatesToDateRange() throws Exception {
		LocalDateTime dateFrom = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(2024, 12, 31, 23, 59);
		when(archivedBillPaymentsRepository.findByDateBetweenOrderByIdAscDateAsc(any(LocalDateTime.class), any(LocalDateTime.class)))
			.thenReturn(List.of());

		List<ArchivedBillPayments> result = archiveIoOperations.getArchivedPayments(dateFrom, dateTo, null);

		assertThat(result).isEmpty();
		verify(archivedBillPaymentsRepository).findByDateBetweenOrderByIdAscDateAsc(any(LocalDateTime.class), any(LocalDateTime.class));
	}

	@Test
	void testGetArchivedPaymentsByBills_ReturnsCombinedPayments() throws Exception {
		ArchivedBill bill1 = new ArchivedBill();
		bill1.setId(1);
		ArchivedBill bill2 = new ArchivedBill();
		bill2.setId(2);

		when(archivedBillPaymentsRepository.findByBillIdOrderByIdAsc(1)).thenReturn(List.of(new ArchivedBillPayments()));
		when(archivedBillPaymentsRepository.findByBillIdOrderByIdAsc(2)).thenReturn(List.of(new ArchivedBillPayments()));

		List<ArchivedBillPayments> result = archiveIoOperations.getArchivedPayments(List.of(bill1, bill2));

		assertThat(result).hasSize(2);
	}

	@Test
	void testGetArchivedPaymentsByDatePatientAndGuarantor_WithNullPatientId_DelegatesToDateRange() throws Exception {
		LocalDateTime dateFrom = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(2024, 12, 31, 23, 59);
		when(archivedBillPaymentsRepository.findByDateBetweenOrderByIdAscDateAsc(any(LocalDateTime.class), any(LocalDateTime.class)))
			.thenReturn(List.of());

		List<ArchivedBillPayments> result = archiveIoOperations.getArchivedPaymentsByDatePatientAndGuarantor(dateFrom, dateTo, null, "g1");

		assertThat(result).isEmpty();
		verify(archivedBillPaymentsRepository).findByDateBetweenOrderByIdAscDateAsc(any(LocalDateTime.class), any(LocalDateTime.class));
	}

	@Test
	void testGetArchivedPaymentsByDatePatientAndGuarantor_WithNullDate_ThrowsException() {
		LocalDateTime dateFrom = LocalDateTime.of(2024, 1, 1, 0, 0);

		assertThatThrownBy(() -> archiveIoOperations.getArchivedPaymentsByDatePatientAndGuarantor(dateFrom, null, null, "g1"))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testGetArchivedBillsFromPayments_ReturnsUniqueBills() throws Exception {
		ArchivedBillPayments payment1 = new ArchivedBillPayments();
		payment1.setBillId(1);
		ArchivedBillPayments payment2 = new ArchivedBillPayments();
		payment2.setBillId(1); // same bill as payment1
		ArchivedBillPayments payment3 = new ArchivedBillPayments();
		payment3.setBillId(2);

		ArchivedBill bill1 = new ArchivedBill();
		bill1.setId(1);
		ArchivedBill bill2 = new ArchivedBill();
		bill2.setId(2);

		when(archivedBillRepository.findById(1)).thenReturn(Optional.of(bill1));
		when(archivedBillRepository.findById(2)).thenReturn(Optional.of(bill2));

		List<ArchivedBill> result = archiveIoOperations.getArchivedBillsFromPayments(List.of(payment1, payment2, payment3));

		assertThat(result).hasSize(2);
		verify(archivedBillRepository).findById(1);
		verify(archivedBillRepository).findById(2);
	}

	@Test
	void testGetAllArchivedBillPayments_WithMainAndRefundPayments_ReturnsCombinedList() throws Exception {
		ArchivedBill bill = new ArchivedBill();
		bill.setId(1);

		ArchivedBillPayments mainPayment = new ArchivedBillPayments();
		mainPayment.setDate(LocalDateTime.of(2024, 6, 1, 12, 0));

		ArchivedBillPayments refundPayment = new ArchivedBillPayments();
		refundPayment.setDate(LocalDateTime.of(2024, 6, 2, 12, 0));

		when(archivedBillPaymentsRepository.findByBillIdOrderByDateAsc(1)).thenReturn(List.of(mainPayment));
		when(archivedBillPaymentsRepository.findByBillParentIdOrderByDateAsc(1)).thenReturn(List.of(refundPayment));

		List<ArchivedBillPayments> result = archiveIoOperations.getAllArchivedBillPayments(bill);

		assertThat(result).hasSize(2);
	}

	@Test
	void testGetAllArchivedBillPayments_WithNullBill_ReturnsEmptyList() throws Exception {
		List<ArchivedBillPayments> result = archiveIoOperations.getAllArchivedBillPayments(null);

		assertThat(result).isEmpty();
	}

	@Test
	void testSumArchivedPaymentsByFilters_DelegatesToRepository() throws Exception {
		when(archivedBillPaymentsRepository.sumPaymentsByFilters(any(), any(), any(), any(), any()))
			.thenReturn(2000.75);

		double result = archiveIoOperations.sumArchivedPaymentsByFilters("C", null, null, null, null);

		assertThat(result).isCloseTo(2000.75, org.assertj.core.data.Offset.offset(0.01));
	}

	@Test
	void testSumArchivedPaymentsByUserAndFilters_DelegatesToRepository() throws Exception {
		when(archivedBillPaymentsRepository.sumPaymentsByUserAndFilters(any(), any(), any(), any(), any(), any()))
			.thenReturn(750.50);

		double result = archiveIoOperations.sumArchivedPaymentsByUserAndFilters("user1", "C", null, null, null, null);

		assertThat(result).isCloseTo(750.50, org.assertj.core.data.Offset.offset(0.01));
	}
}
