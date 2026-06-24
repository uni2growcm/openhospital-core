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

import java.time.LocalDateTime;
import java.util.List;

import org.isf.opd.model.TuberculosisVisit;
import org.isf.opd.service.TuberculosisVisitIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class TuberculosisVisitManager {

    @Autowired
    private TuberculosisVisitIoOperations ioOperations;

    public TuberculosisVisit newVisit(TuberculosisVisit visit) throws OHServiceException {
        return ioOperations.saveVisit(visit);
    }

    public TuberculosisVisit updateVisit(TuberculosisVisit visit) throws OHServiceException {
        return ioOperations.updateVisit(visit);
    }

    public void deleteVisit(TuberculosisVisit visit) throws OHServiceException {
        ioOperations.deleteVisit(visit);
    }

    public TuberculosisVisit getVisitById(Integer id) throws OHServiceException {
        return ioOperations.findVisitById(id);
    }

    public List<TuberculosisVisit> getVisitsByTreatmentId(Integer treatmentId) throws OHServiceException {
        return ioOperations.findVisitsByTreatmentId(treatmentId);
    }

    public Page<TuberculosisVisit> getVisitsByTreatmentId(Integer treatmentId, Pageable pageable) throws OHServiceException {
        return ioOperations.findVisitsByTreatmentId(treatmentId, pageable);
    }

    public List<TuberculosisVisit> getVisitsByTreatmentIdAndDateRange(
        Integer treatmentId,
        LocalDateTime dateFrom,
        LocalDateTime dateTo) throws OHServiceException {
        return ioOperations.findVisitsByTreatmentIdAndDateRange(treatmentId, dateFrom, dateTo);
    }
}
