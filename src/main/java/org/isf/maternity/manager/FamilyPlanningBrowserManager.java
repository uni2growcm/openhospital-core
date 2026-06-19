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
import org.isf.maternity.model.FPStatus;
import org.isf.maternity.model.FamilyPlanning;
import org.isf.maternity.service.FamilyPlanningIoOperation;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class FamilyPlanningBrowserManager {

    private final FamilyPlanningIoOperation ioOperation;

    public FamilyPlanningBrowserManager(FamilyPlanningIoOperation ioOperation) {
        this.ioOperation = ioOperation;
    }

    public FamilyPlanning newFamilyPlanning(FamilyPlanning familyPlanning) throws OHServiceException {
        validateFamilyPlanning(familyPlanning);
        return ioOperation.newFamilyPlanning(familyPlanning);
    }

    public FamilyPlanning updateFamilyPlanning(FamilyPlanning familyPlanning) throws OHServiceException {
        validateFamilyPlanning(familyPlanning);
        return ioOperation.updateFamilyPlanning(familyPlanning);
    }

    public void deleteFamilyPlanning(FamilyPlanning familyPlanning) throws OHServiceException {
        ioOperation.deleteFamilyPlanning(familyPlanning);
    }

    public List<FamilyPlanning> getFamilyPlanningsByPatient(Integer patientCode) throws OHServiceException {
        return ioOperation.getFamilyPlanningsByPatient(patientCode);
    }

    public Page<FamilyPlanning> searchFamilyPlannings(
        Integer patientCode,
        String methodCode,
        FPStatus status,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        int page,
        int size
    ) throws OHServiceException {
        Pageable pageable = PageRequest.of(page, size);
        return ioOperation.searchFamilyPlannings(patientCode, methodCode, status, fromDate, toDate, pageable);
    }

    public Page<FamilyPlanning> getFamilyPlanningsByDateRange(
        LocalDateTime fromDate,
        LocalDateTime toDate,
        int page,
        int size
    ) throws OHServiceException {
        Pageable pageable = PageRequest.of(page, size);
        return ioOperation.getFamilyPlanningsByDateRange(fromDate, toDate, pageable);
    }

    public boolean hasActiveFamilyPlanning(Integer patientCode) throws OHServiceException {
        return ioOperation.hasActiveFamilyPlanning(patientCode);
    }

    public long countFamilyPlanningsByPatientAndStatus(Integer patientCode, FPStatus status) throws OHServiceException {
        return ioOperation.countFamilyPlanningsByPatientAndStatus(patientCode, status);
    }

    public List<FamilyPlanning> getActiveFamilyPlanningsByPatient(Integer patientCode) throws OHServiceException {
        return ioOperation.getActiveFamilyPlanningsByPatient(patientCode);
    }

    public FamilyPlanning stopFamilyPlanning(Integer familyPlanningId, LocalDateTime endDate, String stopReason)
        throws OHServiceException {
        return ioOperation.stopFamilyPlanning(familyPlanningId, endDate, stopReason);
    }

    private void validateFamilyPlanning(FamilyPlanning fp) throws OHServiceException {
        if (fp == null) {
            throw new OHServiceException(
                new OHExceptionMessage(
                    MessageBundle.getMessage("angal.maternity.fpcannotbenull.msg")
                )
            );
        }

        if (fp.getPatient() == null || fp.getPatient().getCode() == null) {
            throw new OHServiceException(
                new OHExceptionMessage(
                    MessageBundle.getMessage("angal.maternity.fpmusthavepatient.msg")
                )
            );
        }

        if (fp.getRegistrationDate() == null) {
            throw new OHServiceException(
                new OHExceptionMessage(
                    MessageBundle.getMessage("angal.maternity.fpregistrationdaterequired.msg")
                )
            );
        }

        if (fp.getRegistrationDate().isAfter(LocalDateTime.now())) {
            throw new OHServiceException(
                new OHExceptionMessage(
                    MessageBundle.getMessage("angal.maternity.fpregistrationdatecannotbeinfuture.msg")
                )
            );
        }

        if (fp.getStatus() == null) {
            throw new OHServiceException(
                new OHExceptionMessage(
                    MessageBundle.getMessage("angal.maternity.fpstatusrequired.msg")
                )
            );
        }

        if (fp.getId() == null && hasActiveFamilyPlanning(fp.getPatient().getCode())) {
            throw new OHServiceException(
                new OHExceptionMessage(
                    MessageBundle.getMessage("angal.maternity.fpalreadyactive.msg")
                )
            );
        }
    }
}
