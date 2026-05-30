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
		archiveIoOperations = new ArchiveIoOperations(archiveRepository);
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
}
