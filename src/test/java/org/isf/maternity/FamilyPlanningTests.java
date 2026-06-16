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

import org.isf.OHCoreTestCase;
import org.isf.maternity.manager.FamilyPlanningBrowserManager;
import org.isf.maternity.manager.FamilyPlanningVisitBrowserManager;
import org.isf.maternity.model.*;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FamilyPlanningTests extends OHCoreTestCase {

    private static TestPatient testPatient;
    private static TestFamilyPlanning testFamilyPlanning;
    private static TestFamilyPlanningVisit testFamilyPlanningVisit;

    @Autowired
    PatientIoOperationRepository patientIoOperationRepository;
    @Autowired
    FamilyPlanningBrowserManager familyPlanningBrowserManager;
    @Autowired
    FamilyPlanningVisitBrowserManager familyPlanningVisitBrowserManager;

    @BeforeAll
    static void setUpClass() {
        testPatient = new TestPatient();
        testFamilyPlanning = new TestFamilyPlanning();
        testFamilyPlanningVisit = new TestFamilyPlanningVisit();
    }

    @BeforeEach
    void setUp() {
        cleanH2InMemoryDb();
    }

    @Test
    void testFamilyPlanningCRUD() throws Exception {
        Patient patient = testPatient.setup(false);
        patientIoOperationRepository.save(patient);

        FamilyPlanning fp = testFamilyPlanning.setup(patient, false);
        fp = familyPlanningBrowserManager.newFamilyPlanning(fp);
        assertThat(fp).isNotNull();
        assertThat(fp.getId()).isNotNull();

        List<FamilyPlanning> list = familyPlanningBrowserManager.getFamilyPlanningsByPatient(patient.getCode());
        assertThat(list).isNotEmpty();
        assertThat(list).hasSize(1);

        FamilyPlanning updated = list.get(0);
        updated.setNotes("Updated notes");
        updated = familyPlanningBrowserManager.updateFamilyPlanning(updated);
        assertThat(updated.getNotes()).isEqualTo("Updated notes");

        assertThat(familyPlanningBrowserManager.hasActiveFamilyPlanning(patient.getCode())).isTrue();

        long count = familyPlanningBrowserManager.countFamilyPlanningsByPatientAndStatus(patient.getCode(), FPStatus.ACTIVE);
        assertThat(count).isEqualTo(1);

        List<FamilyPlanning> activeList = familyPlanningBrowserManager.getActiveFamilyPlanningsByPatient(patient.getCode());
        assertThat(activeList).hasSize(1);

        familyPlanningBrowserManager.deleteFamilyPlanning(updated);

        List<FamilyPlanning> afterDelete = familyPlanningBrowserManager.getFamilyPlanningsByPatient(patient.getCode());
        assertThat(afterDelete).isEmpty();
    }

    @Test
    void testStopFamilyPlanning() throws Exception {
        Patient patient = testPatient.setup(false);
        patientIoOperationRepository.save(patient);

        FamilyPlanning fp = testFamilyPlanning.setup(patient, false);
        fp = familyPlanningBrowserManager.newFamilyPlanning(fp);

        FamilyPlanning stopped = familyPlanningBrowserManager.stopFamilyPlanning(
            fp.getId(), LocalDate.now(), "Patient decided to stop"
        );

        assertThat(stopped.getStatus()).isEqualTo(FPStatus.STOPPED);
        assertThat(stopped.getStopReason()).isEqualTo("Patient decided to stop");
        assertThat(stopped.getEndDate()).isNotNull();
        assertThat(familyPlanningBrowserManager.hasActiveFamilyPlanning(patient.getCode())).isFalse();
    }

    @Test
    void testMultipleActiveFamilyPlanningNotAllowed() throws Exception {
        Patient patient = testPatient.setup(false);
        patientIoOperationRepository.save(patient);

        FamilyPlanning fp1 = testFamilyPlanning.setup(patient, false);
        familyPlanningBrowserManager.newFamilyPlanning(fp1);

        FamilyPlanning fp2 = testFamilyPlanning.setup(patient, true);
        fp2.setStartDate(LocalDate.of(2025, 7, 1));

        assertThrows(Exception.class, () -> {
            familyPlanningBrowserManager.newFamilyPlanning(fp2);
        });
    }

    @Test
    void testStopReasonRequiredWhenStopped() throws Exception {
        Patient patient = testPatient.setup(false);
        patientIoOperationRepository.save(patient);

        FamilyPlanning fp = testFamilyPlanning.setup(patient, true);
        fp.setStatus(FPStatus.STOPPED);
        fp.setStartDate(LocalDate.of(2025, 6, 1));

        assertThrows(Exception.class, () -> {
            familyPlanningBrowserManager.newFamilyPlanning(fp);
        });
    }

    @Test
    void testSearchFamilyPlannings() throws Exception {
        Patient patient1 = testPatient.setup(false);
        patientIoOperationRepository.save(patient1);

        Patient patient2 = testPatient.setup(false);
        patientIoOperationRepository.save(patient2);

        LocalDate now = LocalDate.now();

        FamilyPlanning fp1 = testFamilyPlanning.setup(patient1, true);
        fp1.setStartDate(now.minusDays(10));
        fp1.setMethod(FPMethod.IMPLANT);
        fp1.setStatus(FPStatus.ACTIVE);
        fp1 = familyPlanningBrowserManager.newFamilyPlanning(fp1);

        familyPlanningBrowserManager.stopFamilyPlanning(fp1.getId(), now.minusDays(7), "Changed method");

        FamilyPlanning fp2 = testFamilyPlanning.setup(patient2, true);
        fp2.setStartDate(now.minusDays(5));
        fp2.setMethod(FPMethod.PILL);
        fp2.setStatus(FPStatus.ACTIVE);
        fp2 = familyPlanningBrowserManager.newFamilyPlanning(fp2);

        Page<FamilyPlanning> all = familyPlanningBrowserManager.searchFamilyPlannings(null, null, null, null, null, 0, 10);
        assertThat(all).isNotNull();
        assertThat(all.getContent().size()).isGreaterThanOrEqualTo(2);

        Page<FamilyPlanning> byPatient = familyPlanningBrowserManager.searchFamilyPlannings(
            patient1.getCode(), null, null, null, null, 0, 10
        );
        assertThat(byPatient.getContent()).allMatch(p -> p.getPatient().getCode().equals(patient1.getCode()));

        Page<FamilyPlanning> byMethod = familyPlanningBrowserManager.searchFamilyPlannings(
            null, FPMethod.IMPLANT, null, null, null, 0, 10
        );
        assertThat(byMethod.getContent()).allMatch(p -> p.getMethod() == FPMethod.IMPLANT);

        Page<FamilyPlanning> byStatus = familyPlanningBrowserManager.searchFamilyPlannings(
            null, null, FPStatus.ACTIVE, null, null, 0, 10
        );
        assertThat(byStatus.getContent()).allMatch(p -> p.getStatus() == FPStatus.ACTIVE);

        Page<FamilyPlanning> byDate = familyPlanningBrowserManager.searchFamilyPlannings(
            null, null, null, now.minusDays(7), now, 0, 10
        );
        assertThat(byDate.getContent()).allMatch(p -> !p.getStartDate().isBefore(now.minusDays(7)));
    }

    @Test
    void testFamilyPlanningVisitFullFlow() throws Exception {
        Patient patient = testPatient.setup(false);
        patientIoOperationRepository.save(patient);

        FamilyPlanning fp = testFamilyPlanning.setup(patient, false);
        fp = familyPlanningBrowserManager.newFamilyPlanning(fp);

        FamilyPlanningVisit visit = testFamilyPlanningVisit.setup(fp, false);
        FamilyPlanningVisit saved = familyPlanningVisitBrowserManager.newVisit(visit);
        assertThat(saved.getId()).isNotNull();

        List<FamilyPlanningVisit> visits = familyPlanningVisitBrowserManager.getVisitsByFamilyPlanning(fp.getId());
        assertThat(visits).hasSize(1);

        long count = familyPlanningVisitBrowserManager.countVisits(fp.getId());
        assertThat(count).isEqualTo(1);

        assertThat(familyPlanningVisitBrowserManager.hasVisits(fp.getId())).isTrue();

        FamilyPlanningVisit last = familyPlanningVisitBrowserManager.getLastVisit(fp.getId());
        assertThat(last).isNotNull();
        assertThat(last.getId()).isEqualTo(saved.getId());

        last.setComplaints("Headache");
        FamilyPlanningVisit updated = familyPlanningVisitBrowserManager.updateVisit(last);
        assertThat(updated.getComplaints()).isEqualTo("Headache");

        List<FamilyPlanningVisit> range = familyPlanningVisitBrowserManager.getVisitsByDateRange(
            fp.getId(),
            updated.getVisitDate().minusDays(1),
            updated.getVisitDate().plusDays(1)
        );
        assertThat(range).isNotEmpty();

        List<FamilyPlanningVisit> filtered = familyPlanningVisitBrowserManager.getVisitsByFilters(
            fp.getId(),
            updated.getVisitDate().minusDays(1),
            updated.getVisitDate().plusDays(1),
            FPVisitType.FOLLOWUP
        );
        assertThat(filtered).isNotEmpty();

        familyPlanningVisitBrowserManager.deleteVisit(updated);
        assertThat(familyPlanningVisitBrowserManager.hasVisits(fp.getId())).isFalse();
        assertThat(familyPlanningVisitBrowserManager.countVisits(fp.getId())).isEqualTo(0);
    }

    @Test
    void testVisitValidation() throws Exception {
        Patient patient = testPatient.setup(false);
        patientIoOperationRepository.save(patient);

        FamilyPlanning fp = testFamilyPlanning.setup(patient, false);
        fp = familyPlanningBrowserManager.newFamilyPlanning(fp);

        FamilyPlanningVisit visitNoFp = testFamilyPlanningVisit.setup(fp, true);
        visitNoFp.setFamilyPlanning(null);

        assertThrows(Exception.class, () -> {
            familyPlanningVisitBrowserManager.newVisit(visitNoFp);
        });

        FamilyPlanningVisit visitFutureDate = testFamilyPlanningVisit.setup(fp, true);
        visitFutureDate.setFamilyPlanning(fp);
        visitFutureDate.setVisitType(FPVisitType.FOLLOWUP);
        visitFutureDate.setVisitDate(LocalDateTime.now().plusDays(10));

        assertThrows(Exception.class, () -> {
            familyPlanningVisitBrowserManager.newVisit(visitFutureDate);
        });

        FamilyPlanningVisit visitNoType = testFamilyPlanningVisit.setup(fp, true);
        visitNoType.setFamilyPlanning(fp);
        visitNoType.setVisitDate(LocalDateTime.now().minusDays(1));
        visitNoType.setVisitType(null);

        assertThrows(Exception.class, () -> {
            familyPlanningVisitBrowserManager.newVisit(visitNoType);
        });
    }

    @Test
    void testFamilyPlanningValidation() throws Exception {
        assertThrows(Exception.class, () -> {
            familyPlanningBrowserManager.newFamilyPlanning(null);
        });

        Patient patient = testPatient.setup(false);
        patientIoOperationRepository.save(patient);

        FamilyPlanning fpNoPatient = testFamilyPlanning.setup(patient, true);
        fpNoPatient.setPatient(null);
        assertThrows(Exception.class, () -> {
            familyPlanningBrowserManager.newFamilyPlanning(fpNoPatient);
        });

        FamilyPlanning fpFutureDate = testFamilyPlanning.setup(patient, true);
        fpFutureDate.setPatient(patient);
        fpFutureDate.setMethod(FPMethod.PILL);
        fpFutureDate.setStartDate(LocalDate.now().plusDays(10));
        fpFutureDate.setStatus(FPStatus.ACTIVE);
        assertThrows(Exception.class, () -> {
            familyPlanningBrowserManager.newFamilyPlanning(fpFutureDate);
        });
    }
}
