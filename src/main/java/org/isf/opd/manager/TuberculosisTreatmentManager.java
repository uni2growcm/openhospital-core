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
package org.isf.opd.manager;

import java.time.LocalDate;
import java.util.List;

import org.isf.opd.model.TuberculosisTreatment;
import org.isf.opd.model.TuberculosisTreatment.TreatmentStatus;
import org.isf.opd.service.TuberculosisTreatmentIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class TuberculosisTreatmentManager {

    @Autowired
    private TuberculosisTreatmentIoOperations ioOperations;

    public TuberculosisTreatment newTreatment(TuberculosisTreatment treatment) throws OHServiceException {
        return ioOperations.saveTreatment(treatment);
    }

    public TuberculosisTreatment updateTreatment(TuberculosisTreatment treatment) throws OHServiceException {
        return ioOperations.updateTreatment(treatment);
    }

    public void deleteTreatment(TuberculosisTreatment treatment) throws OHServiceException {
        ioOperations.deleteTreatment(treatment);
    }

    public TuberculosisTreatment getTreatmentById(Integer id) throws OHServiceException {
        return ioOperations.findTreatmentById(id);
    }

    public List<TuberculosisTreatment> getTreatmentsByPatientCode(Integer patientCode) throws OHServiceException {
        return ioOperations.findTreatmentsByPatientCode(patientCode);
    }

    public Page<TuberculosisTreatment> getAllTreatments(Pageable pageable) throws OHServiceException {
        return ioOperations.findAllTreatments(pageable);
    }

    public Page<TuberculosisTreatment> getTreatmentsByStatus(TreatmentStatus status, Pageable pageable) throws OHServiceException {
        return ioOperations.findTreatmentsByStatus(status, pageable);
    }

    public Page<TuberculosisTreatment> getTreatmentsByFilters(
        Integer patientCode,
        TreatmentStatus status,
        LocalDate dateFrom,
        LocalDate dateTo,
        LocalDate startDateFrom,
        LocalDate startDateTo,
        Pageable pageable) throws OHServiceException {
        return ioOperations.findTreatmentsByFilters(
            patientCode, status, dateFrom, dateTo, startDateFrom, startDateTo, pageable);
    }
}
