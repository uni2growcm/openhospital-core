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

import org.isf.maternity.model.FamilyPlanningVisit;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class FamilyPlanningVisitIoOperation {

    private final FamilyPlanningVisitIoOperationRepository repository;

    public FamilyPlanningVisitIoOperation(FamilyPlanningVisitIoOperationRepository repository) {
        this.repository = repository;
    }

    public List<FamilyPlanningVisit> getVisitsByFamilyPlanning(Integer familyPlanningId) throws OHServiceException {
        return repository.findByFamilyPlanningIdOrderByVisitDateAsc(familyPlanningId);
    }

    public Optional<FamilyPlanningVisit> getLastVisit(Integer familyPlanningId) throws OHServiceException {
        return repository.findTopByFamilyPlanningIdOrderByVisitDateDesc(familyPlanningId);
    }

    public long countVisits(Integer familyPlanningId) throws OHServiceException {
        return repository.countByFamilyPlanningId(familyPlanningId);
    }

    public boolean hasVisits(Integer familyPlanningId) throws OHServiceException {
        return repository.existsByFamilyPlanningId(familyPlanningId);
    }

    public List<FamilyPlanningVisit> getVisitsByDateRange(
        Integer familyPlanningId,
        LocalDateTime from,
        LocalDateTime to
    ) throws OHServiceException {
        return repository.findByFamilyPlanningIdAndVisitDateBetween(familyPlanningId, from, to);
    }

    public List<FamilyPlanningVisit> getVisitsByFilters(
        Integer familyPlanningId,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        String visitTypeCode
    ) throws OHServiceException {
        return repository.findVisitsByFilters(familyPlanningId, fromDate, toDate, visitTypeCode);
    }

    public FamilyPlanningVisit newVisit(FamilyPlanningVisit visit) throws OHServiceException {
        return repository.save(visit);
    }

    public FamilyPlanningVisit updateVisit(FamilyPlanningVisit visit) throws OHServiceException {
        return repository.save(visit);
    }

    public void deleteVisit(FamilyPlanningVisit visit) throws OHServiceException {
        repository.delete(visit);
    }

    public Optional<FamilyPlanningVisit> getVisitById(Integer id) throws OHServiceException {
        return repository.findById(id);
    }

    public FamilyPlanningVisit validateVisitBelongsToFamilyPlanning(
        Integer visitId,
        Integer familyPlanningId
    ) throws OHServiceException {
        FamilyPlanningVisit visit = repository.findById(visitId)
            .orElseThrow(() ->
                new OHServiceException(
                    new OHExceptionMessage("Family Planning Visit not found: " + visitId)
                )
            );
        if (!visit.getFamilyPlanning().getId().equals(familyPlanningId)) {
            throw new OHServiceException(
                new OHExceptionMessage("Visit does not belong to Family Planning: " + familyPlanningId)
            );
        }
        return visit;
    }
}
