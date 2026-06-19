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
package org.isf.maternity.service;

import org.isf.maternity.model.FPStatus;
import org.isf.maternity.model.FamilyPlanning;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class FamilyPlanningIoOperation {

    private final FamilyPlanningIoOperationRepository repository;

    public FamilyPlanningIoOperation(FamilyPlanningIoOperationRepository repository) {
        this.repository = repository;
    }

    public FamilyPlanning newFamilyPlanning(FamilyPlanning familyPlanning) throws OHServiceException {
        return repository.save(familyPlanning);
    }

    public FamilyPlanning updateFamilyPlanning(FamilyPlanning familyPlanning) throws OHServiceException {
        return repository.save(familyPlanning);
    }

    public void deleteFamilyPlanning(FamilyPlanning familyPlanning) throws OHServiceException {
        repository.delete(familyPlanning);
    }

    public List<FamilyPlanning> getFamilyPlanningsByPatient(Integer patientCode) throws OHServiceException {
        return repository.findByPatient_CodeOrderByRegistrationDateDesc(patientCode);
    }

    public Page<FamilyPlanning> searchFamilyPlannings(
        Integer patientCode,
        String methodCode,
        FPStatus status,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        Pageable pageable
    ) throws OHServiceException {
        return repository.searchFamilyPlannings(patientCode, methodCode, status, fromDate, toDate, pageable);
    }

    public Page<FamilyPlanning> getFamilyPlanningsByDateRange(
        LocalDateTime fromDate,
        LocalDateTime toDate,
        Pageable pageable
    ) throws OHServiceException {
        return repository.findByRegistrationDateBetween(fromDate, toDate, pageable);
    }

    public boolean hasActiveFamilyPlanning(Integer patientCode) throws OHServiceException {
        return repository.existsByPatient_CodeAndStatus(patientCode, FPStatus.ACTIVE);
    }

    public long countFamilyPlanningsByPatientAndStatus(Integer patientCode, FPStatus status) throws OHServiceException {
        return repository.countByPatient_CodeAndStatus(patientCode, status);
    }

    public FamilyPlanning getLatestFamilyPlanningByPatientAndStatus(Integer patientCode, FPStatus status)
        throws OHServiceException {
        return repository.findTopByPatient_CodeAndStatusOrderByRegistrationDateDesc(patientCode, status).orElse(null);
    }

    public List<FamilyPlanning> getActiveFamilyPlanningsByPatient(Integer patientCode) throws OHServiceException {
        return repository.findByPatient_CodeAndStatus(patientCode, FPStatus.ACTIVE);
    }

    public FamilyPlanning stopFamilyPlanning(Integer familyPlanningId, LocalDateTime endDate, String stopReason)
        throws OHServiceException {
        FamilyPlanning fp = repository.findById(familyPlanningId)
            .orElseThrow(() ->
                new OHServiceException(
                    new OHExceptionMessage("Family Planning not found: " + familyPlanningId)
                )
            );
        fp.setStatus(FPStatus.STOPPED);
        return repository.save(fp);
    }
}
