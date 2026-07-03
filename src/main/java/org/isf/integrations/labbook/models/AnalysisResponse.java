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
package org.isf.integrations.labbook.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO representing an analysis from LabBook patient history.
 * Retrieved from {@code GET /services/patient/historic/{idPat}}.
 *
 * @param id        Record identifier
 * @param recordType      Record type (e.g., "E" for exam)
 * @param prescriptionDate   Prescription date (YYYY-MM-DD)
 * @param analysis     Analysis name
 * @param recordNumber       Record number
 * @param variable     Variable name of the analysis
 * @param result       Result value
 *
 * @author Duval Donfack
 */
public record AnalysisResponse(

	@JsonProperty("id_rec") Integer id,

	@JsonProperty("type_rec") String recordType,

	@JsonProperty("date_prescr") String prescriptionDate,

	@JsonProperty("analysis") String analysis,

	@JsonProperty("rec_num") String recordNumber,

	@JsonProperty String variable,

	@JsonProperty String result

) {
}