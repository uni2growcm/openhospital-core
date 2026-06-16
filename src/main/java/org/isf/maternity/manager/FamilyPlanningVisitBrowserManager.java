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
package org.isf.maternity.manager;

import org.isf.generaldata.MessageBundle;
import org.isf.maternity.model.FPVisitType;
import org.isf.maternity.model.FamilyPlanningVisit;
import org.isf.maternity.service.FamilyPlanningVisitIoOperation;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class FamilyPlanningVisitBrowserManager {

    private final FamilyPlanningVisitIoOperation ioOperation;

    public FamilyPlanningVisitBrowserManager(FamilyPlanningVisitIoOperation ioOperation) {
        this.ioOperation = ioOperation;
    }

    public List<FamilyPlanningVisit> getVisitsByFamilyPlanning(Integer familyPlanningId) throws OHServiceException {
        return ioOperation.getVisitsByFamilyPlanning(familyPlanningId);
    }

    public FamilyPlanningVisit getLastVisit(Integer familyPlanningId) throws OHServiceException {
        return ioOperation.getLastVisit(familyPlanningId).orElse(null);
    }

    public long countVisits(Integer familyPlanningId) throws OHServiceException {
        return ioOperation.countVisits(familyPlanningId);
    }

    public boolean hasVisits(Integer familyPlanningId) throws OHServiceException {
        return ioOperation.hasVisits(familyPlanningId);
    }

    public List<FamilyPlanningVisit> getVisitsByDateRange(
        Integer familyPlanningId,
        LocalDateTime from,
        LocalDateTime to
    ) throws OHServiceException {
        return ioOperation.getVisitsByDateRange(familyPlanningId, from, to);
    }

    public List<FamilyPlanningVisit> getVisitsByFilters(
        Integer familyPlanningId,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        FPVisitType visitType
    ) throws OHServiceException {
        return ioOperation.getVisitsByFilters(familyPlanningId, fromDate, toDate, visitType);
    }

    public FamilyPlanningVisit newVisit(FamilyPlanningVisit visit) throws OHServiceException {
        validateVisit(visit);
        return ioOperation.newVisit(visit);
    }

    public FamilyPlanningVisit updateVisit(FamilyPlanningVisit visit) throws OHServiceException {
        validateVisit(visit);
        return ioOperation.updateVisit(visit);
    }

    public void deleteVisit(FamilyPlanningVisit visit) throws OHServiceException {
        ioOperation.deleteVisit(visit);
    }

    private void validateVisit(FamilyPlanningVisit visit) throws OHServiceException {
        if (visit == null) {
            throw new OHServiceException(
                new OHExceptionMessage(
                    MessageBundle.getMessage("angal.maternity.fpvisitcannotbenull.msg")
                )
            );
        }

        if (visit.getFamilyPlanning() == null || visit.getFamilyPlanning().getId() == null) {
            throw new OHServiceException(
                new OHExceptionMessage(
                    MessageBundle.getMessage("angal.maternity.fpvisitmusthavefamilyplanning.msg")
                )
            );
        }

        if (visit.getVisitDate() == null) {
            throw new OHServiceException(
                new OHExceptionMessage(
                    MessageBundle.getMessage("angal.maternity.fpvisitdaterequired.msg")
                )
            );
        }

        if (visit.getVisitDate().isAfter(LocalDateTime.now())) {
            throw new OHServiceException(
                new OHExceptionMessage(
                    MessageBundle.getMessage("angal.maternity.fpvisitdatecannotbeinfuture.msg")
                )
            );
        }

        if (visit.getVisitType() == null) {
            throw new OHServiceException(
                new OHExceptionMessage(
                    MessageBundle.getMessage("angal.maternity.fpvisittyperequired.msg")
                )
            );
        }
    }
}
