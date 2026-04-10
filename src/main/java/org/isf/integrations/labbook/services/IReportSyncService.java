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

import org.isf.integrations.labbook.models.ReportGroupedRequest;
import org.isf.utils.exception.OHException;

/**
 * Contract for synchronising Open Hospital report requests with the LabBook system.
 *
 * <p>Implementations delegate to the HTTP client {@code IReportService} to trigger
 * report generation on the LabBook side.
 *
 * <h3>Failure contract</h3>
 * <p>Implementations <strong>must not</strong> propagate {@code RestClientException} or
 * any other unchecked exception to the caller. A LabBook connectivity failure must never
 * block the main application flow. Errors should be logged at {@code WARN} level and
 * re-thrown as {@link OHException}.
 *
 * <h3>Null-safety</h3>
 * <p>Implementations must validate that the request and its {@code recordIds}
 * are not {@code null}, throwing {@link IllegalArgumentException} when validation fails.
 *
 * @author Duval Donfack
 */
public interface IReportSyncService {

	/**
	 * Generates and downloads a grouped PDF report for the given record identifiers.
	 *
	 * <p>The LabBook API expects a JSON body containing:
	 * <ul>
	 *   <li>{@code "1_id_rec_vld"} → array of record IDs</li>
	 *   <li>{@code "filename"}       → desired PDF filename</li>
	 * </ul>
	 *
	 * @param request DTO containing the record IDs array and desired filename
	 * @return the PDF file content as a byte array
	 * @throws IllegalArgumentException if the request is invalid
	 * @throws OHException if the report generation fails
	 */
	byte[] generateReportGrouped(ReportGroupedRequest request) throws OHException;
}