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
package org.isf.patient.model;

/**
 * Spring application event published after a {@link Patient} is successfully persisted.
 *
 * <p>Published by {@code PatientIoOperations} immediately after the database save completes,
 * still within the same transaction. Listeners must not throw unchecked exceptions if the
 * associated side-effect (e.g. external system sync) is non-critical — they should log and
 * swallow to avoid rolling back the patient transaction.
 *
 * @param patient the patient that was just created or updated
 * @param isNew   {@code true} when the patient was newly inserted; {@code false} for updates
 */
public record PatientCreatedOrUpdatedEvent(Patient patient, boolean isNew) {

}
