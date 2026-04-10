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
package org.isf.integrations.labbook.ports;

import org.isf.integrations.labbook.models.ReportGroupedRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Declarative HTTP client for the LabBook report API.
 *
 * <p>Provides access to the report generation endpoint:
 * <ul>
 *   <li>{@code POST /services/pdf/report/grouped/download} — generate and download a grouped PDF report</li>
 * </ul>
 *
 * <p>The proxy is registered in {@code LabBookConfig} via {@code HttpServiceProxyFactory}
 * backed by the {@code labbookRestClient} bean, which transparently injects an OAuth2
 * Bearer token on every request.
 *
 * @author Duval Donfack
 */
@HttpExchange("/services/pdf/report")
public interface IReportService {

	/**
	 * Generates and downloads a grouped PDF report for the given record identifiers.
	 *
	 * @param request DTO containing the record IDs array and desired filename
	 * @return the PDF file content as a byte array wrapped in a {@link ResponseEntity}
	 */
	@PostExchange("/grouped/download")
	ResponseEntity<byte[]> generatePdfReportGrouped(@RequestBody ReportGroupedRequest request);
}