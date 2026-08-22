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
import org.isf.maternity.model.FamilyPlanningMethodHistory;
import org.isf.maternity.service.FamilyPlanningMethodHistoryIoOperation;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FamilyPlanningMethodHistoryBrowserManager {

    private final FamilyPlanningMethodHistoryIoOperation ioOperation;

    public FamilyPlanningMethodHistoryBrowserManager(FamilyPlanningMethodHistoryIoOperation ioOperation) {
        this.ioOperation = ioOperation;
    }

    public FamilyPlanningMethodHistory newMethodHistory(FamilyPlanningMethodHistory history) throws OHServiceException {
        validateMethodHistory(history);
        return ioOperation.newMethodHistory(history);
    }

    public FamilyPlanningMethodHistory updateMethodHistory(FamilyPlanningMethodHistory history) throws OHServiceException {
        validateMethodHistory(history);
        return ioOperation.updateMethodHistory(history);
    }

    public void deleteMethodHistory(FamilyPlanningMethodHistory history) throws OHServiceException {
        ioOperation.deleteMethodHistory(history);
    }

    public List<FamilyPlanningMethodHistory> getMethodHistoryByFamilyPlanning(Integer familyPlanningId) throws OHServiceException {
        return ioOperation.getMethodHistoryByFamilyPlanning(familyPlanningId);
    }

    private void validateMethodHistory(FamilyPlanningMethodHistory history) throws OHServiceException {
        if (history == null) {
            throw new OHServiceException(
                new OHExceptionMessage(
                    MessageBundle.getMessage("angal.maternity.fpmethodhistorycannotbenull.msg")
                )
            );
        }

        if (history.getFamilyPlanning() == null || history.getFamilyPlanning().getId() == null) {
            throw new OHServiceException(
                new OHExceptionMessage(
                    MessageBundle.getMessage("angal.maternity.fpmethodhistorymusthavefamilyplanning.msg")
                )
            );
        }

        if (history.getMethod() == null) {
            throw new OHServiceException(
                new OHExceptionMessage(
                    MessageBundle.getMessage("angal.maternity.fpmethodhistorymethodrequired.msg")
                )
            );
        }

        if (history.getStartDate() == null) {
            throw new OHServiceException(
                new OHExceptionMessage(
                    MessageBundle.getMessage("angal.maternity.fpmethodhistorystartdaterequired.msg")
                )
            );
        }

        if (history.getStartDate().isAfter(java.time.LocalDate.now())) {
            throw new OHServiceException(
                new OHExceptionMessage(
                    MessageBundle.getMessage("angal.maternity.fpmethodhistorystartdatecannotbeinfuture.msg")
                )
            );
        }
    }
}
