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
import org.isf.maternity.manager.FamilyPlanningMethodHistoryBrowserManager;
import org.isf.maternity.manager.FamilyPlanningVisitBrowserManager;
import org.isf.maternity.model.*;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.typology.model.Family;
import org.isf.typology.model.Typology;
import org.isf.typology.service.TypologyIoOperationRepository;
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
    private static TestFamilyPlanningMethodHistory testFamilyPlanningMethodHistory;

    @Autowired
    PatientIoOperationRepository patientIoOperationRepository;
    @Autowired
    TypologyIoOperationRepository typologyIoOperationRepository;
    @Autowired
    FamilyPlanningBrowserManager familyPlanningBrowserManager;
    @Autowired
    FamilyPlanningVisitBrowserManager familyPlanningVisitBrowserManager;
    @Autowired
    FamilyPlanningMethodHistoryBrowserManager familyPlanningMethodHistoryBrowserManager;

    private Typology pillMethod;
    private Typology implantMethod;
    private Typology followupVisitType;

    @BeforeAll
    static void setUpClass() {
        testPatient = new TestPatient();
        testFamilyPlanning = new TestFamilyPlanning();
        testFamilyPlanningVisit = new TestFamilyPlanningVisit();
        testFamilyPlanningMethodHistory = new TestFamilyPlanningMethodHistory();
    }

    @BeforeEach
    void setUp() {
        cleanH2InMemoryDb();
        pillMethod = createTypology("PILL", "Pill", Family.FAMILYPLANNINGMETHODTYPE);
        implantMethod = createTypology("IMPLANT", "Implant", Family.FAMILYPLANNINGMETHODTYPE);
        followupVisitType = createTypology("FOLLOWUP", "Follow-up", Family.FAMILYPLANNINGVISITTYPE);
    }

    private Typology createTypology(String code, String description, Family family) {
        Typology typology = new Typology(code, description, family);
        return typologyIoOperationRepository.save(typology);
    }

    @Test
    void testFamilyPlanningCRUD() throws Exception {
        Patient patient = testPatient.setup(false);
        patientIoOperationRepository.save(patient);

        FamilyPlanning fp = testFamilyPlanning.setup(patient, pillMethod, false);
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

        FamilyPlanning fp = testFamilyPlanning.setup(patient, pillMethod, false);
        fp = familyPlanningBrowserManager.newFamilyPlanning(fp);

        FamilyPlanning stopped = familyPlanningBrowserManager.stopFamilyPlanning(
            fp.getId(), LocalDateTime.now(), "Patient decided to stop"
        );

        assertThat(stopped.getStatus()).isEqualTo(FPStatus.STOPPED);
        assertThat(familyPlanningBrowserManager.hasActiveFamilyPlanning(patient.getCode())).isFalse();
    }

    @Test
    void testMultipleActiveFamilyPlanningNotAllowed() throws Exception {
        Patient patient = testPatient.setup(false);
        patientIoOperationRepository.save(patient);

        FamilyPlanning fp1 = testFamilyPlanning.setup(patient, pillMethod, false);
        familyPlanningBrowserManager.newFamilyPlanning(fp1);

        FamilyPlanning fp2 = testFamilyPlanning.setup(patient, implantMethod, true);
        fp2.setRegistrationDate(LocalDateTime.of(2025, 7, 1, 10, 0));

        assertThrows(Exception.class, () -> {
            familyPlanningBrowserManager.newFamilyPlanning(fp2);
        });
    }

    @Test
    void testSearchFamilyPlannings() throws Exception {
        Patient patient1 = testPatient.setup(false);
        patientIoOperationRepository.save(patient1);

        Patient patient2 = testPatient.setup(false);
        patientIoOperationRepository.save(patient2);

        LocalDateTime now = LocalDateTime.now();

        FamilyPlanning fp1 = testFamilyPlanning.setup(patient1, implantMethod, true);
        fp1.setRegistrationDate(now.minusDays(10));
        fp1.setStatus(FPStatus.ACTIVE);
        fp1 = familyPlanningBrowserManager.newFamilyPlanning(fp1);

        familyPlanningBrowserManager.stopFamilyPlanning(fp1.getId(), now.minusDays(7), "Changed method");

        FamilyPlanning fp2 = testFamilyPlanning.setup(patient2, pillMethod, true);
        fp2.setRegistrationDate(now.minusDays(5));
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
            null, "IMPLANT", null, null, null, 0, 10
        );
        assertThat(byMethod.getContent()).allMatch(p ->
            p.getCurrentMethod() != null && "IMPLANT".equals(p.getCurrentMethod().getCode())
        );

        Page<FamilyPlanning> byStatus = familyPlanningBrowserManager.searchFamilyPlannings(
            null, null, FPStatus.ACTIVE, null, null, 0, 10
        );
        assertThat(byStatus.getContent()).allMatch(p -> p.getStatus() == FPStatus.ACTIVE);

        Page<FamilyPlanning> byDate = familyPlanningBrowserManager.searchFamilyPlannings(
            null, null, null, now.minusDays(7), now, 0, 10
        );
        assertThat(byDate.getContent()).allMatch(p -> !p.getRegistrationDate().isBefore(now.minusDays(7)));
    }

    @Test
    void testFamilyPlanningVisitFullFlow() throws Exception {
        Patient patient = testPatient.setup(false);
        patientIoOperationRepository.save(patient);

        FamilyPlanning fp = testFamilyPlanning.setup(patient, pillMethod, false);
        fp = familyPlanningBrowserManager.newFamilyPlanning(fp);

        FamilyPlanningVisit visit = testFamilyPlanningVisit.setup(fp, followupVisitType, false);
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

        last.setNotes("Headache");
        FamilyPlanningVisit updated = familyPlanningVisitBrowserManager.updateVisit(last);
        assertThat(updated.getNotes()).isEqualTo("Headache");

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
            "FOLLOWUP"
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

        FamilyPlanning fp = testFamilyPlanning.setup(patient, pillMethod, false);
        fp = familyPlanningBrowserManager.newFamilyPlanning(fp);

        FamilyPlanningVisit visitNoFp = testFamilyPlanningVisit.setup(fp, followupVisitType, true);
        visitNoFp.setFamilyPlanning(null);

        assertThrows(Exception.class, () -> {
            familyPlanningVisitBrowserManager.newVisit(visitNoFp);
        });

        FamilyPlanningVisit visitFutureDate = testFamilyPlanningVisit.setup(fp, followupVisitType, true);
        visitFutureDate.setFamilyPlanning(fp);
        visitFutureDate.setVisitType(followupVisitType);
        visitFutureDate.setVisitDate(LocalDateTime.now().plusDays(10));

        assertThrows(Exception.class, () -> {
            familyPlanningVisitBrowserManager.newVisit(visitFutureDate);
        });

        FamilyPlanningVisit visitNoType = testFamilyPlanningVisit.setup(fp, followupVisitType, true);
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

        FamilyPlanning fpNoPatient = testFamilyPlanning.setup(patient, pillMethod, true);
        fpNoPatient.setPatient(null);
        assertThrows(Exception.class, () -> {
            familyPlanningBrowserManager.newFamilyPlanning(fpNoPatient);
        });

        FamilyPlanning fpFutureDate = testFamilyPlanning.setup(patient, pillMethod, true);
        fpFutureDate.setPatient(patient);
        fpFutureDate.setCurrentMethod(pillMethod);
        fpFutureDate.setRegistrationDate(LocalDateTime.now().plusDays(10));
        fpFutureDate.setStatus(FPStatus.ACTIVE);
        assertThrows(Exception.class, () -> {
            familyPlanningBrowserManager.newFamilyPlanning(fpFutureDate);
        });
    }

    @Test
    void testMethodHistoryFullFlow() throws Exception {
        Patient patient = testPatient.setup(false);
        patientIoOperationRepository.save(patient);

        FamilyPlanning fp = testFamilyPlanning.setup(patient, pillMethod, false);
        fp = familyPlanningBrowserManager.newFamilyPlanning(fp);

        FamilyPlanningMethodHistory history = testFamilyPlanningMethodHistory.setup(fp, pillMethod, false);
        FamilyPlanningMethodHistory saved = familyPlanningMethodHistoryBrowserManager.newMethodHistory(history);
        assertThat(saved.getId()).isNotNull();

        List<FamilyPlanningMethodHistory> historyList =
            familyPlanningMethodHistoryBrowserManager.getMethodHistoryByFamilyPlanning(fp.getId());
        assertThat(historyList).hasSize(1);

        saved.setStopReason("Changed method");
        FamilyPlanningMethodHistory updated = familyPlanningMethodHistoryBrowserManager.updateMethodHistory(saved);
        assertThat(updated.getStopReason()).isEqualTo("Changed method");

        familyPlanningMethodHistoryBrowserManager.deleteMethodHistory(saved);
        List<FamilyPlanningMethodHistory> afterDelete =
            familyPlanningMethodHistoryBrowserManager.getMethodHistoryByFamilyPlanning(fp.getId());
        assertThat(afterDelete).isEmpty();
    }
}
