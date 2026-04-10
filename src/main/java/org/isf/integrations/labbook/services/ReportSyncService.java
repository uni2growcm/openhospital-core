/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.integrations.labbook.services;

import org.isf.integrations.labbook.annotations.EnableLabBook;
import org.isf.integrations.labbook.config.LabBookBeanNames;
import org.isf.integrations.labbook.models.ReportGroupedRequest;
import org.isf.integrations.labbook.ports.IReportService;
import org.isf.utils.exception.OHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * {@inheritDoc}
 *
 * <p>Implements the report sync contract by delegating directly to the
 * {@link IReportService} HTTP client. No additional business logic or
 * event handling is applied at this layer.
 *
 * <p>All calls are performed synchronously. Errors from the underlying
 * {@code RestClient} are propagated to the caller (typically caught
 * at controller level or logged by the HTTP client infrastructure).
 *
 * @author Duval Donfack
 */
@Service
@EnableLabBook
public class ReportSyncService implements IReportSyncService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportSyncService.class);

	private final IReportService reportService;

	public ReportSyncService(@Qualifier(LabBookBeanNames.REPORT_SERVICE) IReportService reportService) {
		this.reportService = reportService;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Logs the request at {@code INFO} level before invocation and
	 * the file size after completion.
	 *
	 * @param request DTO containing record IDs and desired filename
	 * @return PDF file content as byte array
	 */
	@Override
	public byte[] generateReportGrouped(ReportGroupedRequest request) throws OHException {
		LOGGER.info("generateReportGrouped() called");
		try {
			ResponseEntity<byte[]> response = reportService.generatePdfReportGrouped(request);
			LOGGER.info("generateReportGrouped() completed, size={} bytes",
				response.getBody() != null ? response.getBody().length : 0);
			return response.getBody();
		} catch (Exception e) {
			LOGGER.error("Error occurred during report generation", e);
			throw new OHException("Error occurred during report generation: " + e.getMessage(), e);
		}
	}
}