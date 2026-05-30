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
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
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

	public ArchiveIoOperations(ArchiveRepository archiveRepository) {
		this.archiveRepository = archiveRepository;
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
}
