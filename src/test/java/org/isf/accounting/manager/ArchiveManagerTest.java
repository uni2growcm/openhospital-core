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

import org.isf.accounting.service.archive.ArchiveIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ArchiveManagerTest {

	@Mock
	private ArchiveIoOperations archiveIoOperations;

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
}
