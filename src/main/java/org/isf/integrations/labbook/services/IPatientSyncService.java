/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import org.isf.patient.model.PatientCreatedOrUpdatedEvent;

/**
 * Contract for synchronising Open Hospital patient data with the LabBook system.
 *
 * <p>Implementations listen for {@link PatientCreatedOrUpdatedEvent} published by the
 * persistence layer and push the corresponding patient record to the LabBook API.
 *
 * <h3>Failure contract</h3>
 * <p>Implementations <strong>must not</strong> propagate {@code RestClientException} or
 * any other unchecked exception to the caller. A LabBook connectivity failure must never
 * roll back the Open Hospital patient transaction. Errors should be logged at {@code WARN}
 * level and swallowed.
 *
 * <h3>Null-safety</h3>
 * <p>Implementations must silently skip sync when the event's patient or its code is
 * {@code null}, logging a warning for observability.
 */
public interface IPatientSyncService {

	/**
	 * Handles a patient create-or-update event by pushing the patient data to LabBook.
	 *
	 * <p>Field mapping summary:
	 * <ul>
	 *   <li>OH {@code patient.getCode()} → LabBook {@code idPat} path variable and {@code pat_code}</li>
	 *   <li>OH {@code secondName} → LabBook {@code pat_name} (last name)</li>
	 *   <li>OH {@code firstName}  → LabBook {@code pat_firstname}</li>
	 *   <li>OH sex char ({@code 'M'}/{@code 'F'}) → LabBook integer (1=Male / 2=Female / 3=Unknown)</li>
	 * </ul>
	 *
	 * @param event the published event carrying the persisted patient and a flag indicating
	 *              whether it was newly inserted ({@code isNew=true}) or updated ({@code isNew=false})
	 */
	void onPatientCreatedOrUpdated(PatientCreatedOrUpdatedEvent event);
}
