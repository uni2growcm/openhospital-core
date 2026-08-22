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
package org.isf.maternity;

import org.isf.maternity.model.FPStatus;
import org.isf.maternity.model.FamilyPlanning;
import org.isf.patient.model.Patient;
import org.isf.typology.model.Typology;

import java.time.LocalDateTime;

public class TestFamilyPlanning {

    private final LocalDateTime registrationDate = LocalDateTime.of(2025, 6, 1, 10, 0);

    public FamilyPlanning setup(Patient patient, Typology method, boolean usingSet) {
        FamilyPlanning fp;
        if (usingSet) {
            fp = new FamilyPlanning();
        } else {
            fp = new FamilyPlanning(patient, registrationDate);
        }
        setParameters(fp, patient, method);
        return fp;
    }

    private void setParameters(FamilyPlanning fp, Patient patient, Typology method) {
        fp.setPatient(patient);
        fp.setCurrentMethod(method);
        fp.setRegistrationDate(registrationDate);
        fp.setStatus(FPStatus.ACTIVE);
        fp.setNotes("Test family planning record");
    }

    public void check(FamilyPlanning fp) {
        assert fp.getStatus() == FPStatus.ACTIVE;
        assert fp.getRegistrationDate().equals(registrationDate);
        assert fp.getNotes().equals("Test family planning record");
    }
}
