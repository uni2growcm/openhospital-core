package org.isf.tuberculosis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.tuberculosis.manager.TuberculosisContactManager;
import org.isf.tuberculosis.manager.TuberculosisTreatmentManager;
import org.isf.tuberculosis.manager.TuberculosisVisitManager;
import org.isf.tuberculosis.model.TuberculosisContact;
import org.isf.tuberculosis.model.TuberculosisTreatment;
import org.isf.tuberculosis.model.TuberculosisVisit;
import org.isf.tuberculosis.model.Classification;
import org.isf.tuberculosis.model.DiseaseLocation;
import org.isf.tuberculosis.model.DotStatus;
import org.isf.tuberculosis.model.TreatmentStatus;
import org.isf.tuberculosis.service.TuberculosisContactIoOperationRepository;
import org.isf.tuberculosis.service.TuberculosisTreatmentIoOperationRepository;
import org.isf.tuberculosis.service.TuberculosisTreatmentIoOperations;
import org.isf.tuberculosis.service.TuberculosisVisitIoOperationRepository;
import org.isf.tuberculosis.service.TuberculosisVisitIoOperations;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class TuberculosisTests extends OHCoreTestCase {

    private static TestTuberculosisTreatment testTuberculosisTreatment;
    private static TestTuberculosisVisit testTuberculosisVisit;
    private static TestTuberculosisContact testTuberculosisContact;
    private static TestPatient testPatient;

    @Autowired
    TuberculosisTreatmentIoOperations treatmentIoOperations;
    @Autowired
    TuberculosisTreatmentIoOperationRepository treatmentIoOperationRepository;
    @Autowired
    TuberculosisVisitIoOperations visitIoOperations;
    @Autowired
    TuberculosisVisitIoOperationRepository visitIoOperationRepository;
    @Autowired
    TuberculosisContactIoOperationRepository contactIoOperationRepository;
    @Autowired
    TuberculosisTreatmentManager treatmentManager;
    @Autowired
    TuberculosisVisitManager visitManager;
    @Autowired
    TuberculosisContactManager contactManager;
    @Autowired
    PatientIoOperationRepository patientIoOperationRepository;

    @BeforeAll
    static void setUpClass() {
        testTuberculosisTreatment = new TestTuberculosisTreatment();
        testTuberculosisVisit = new TestTuberculosisVisit();
        testTuberculosisContact = new TestTuberculosisContact();
        testPatient = new TestPatient();
    }

    @BeforeEach
    void setUp() {
        cleanH2InMemoryDb();
    }

    @Test
    void testTreatmentCRUD() throws Exception {
        Patient patient = testPatient.setup(false);
        patient = patientIoOperationRepository.save(patient);

        TuberculosisTreatment treatment = testTuberculosisTreatment.setup(patient, false);
        treatment = treatmentManager.newTreatment(treatment);

        assertThat(treatment).isNotNull();
        assertThat(treatment.getId()).isNotNull();

        List<TuberculosisTreatment> list = treatmentManager.getTreatmentsByPatientCode(patient.getCode());
        assertThat(list).isNotEmpty();
        assertThat(list).hasSize(1);

        TuberculosisTreatment updated = list.get(0);
        updated.setClassification(Classification.RELAPSE);
        updated = treatmentManager.updateTreatment(updated);
        assertThat(updated.getClassification()).isEqualTo(Classification.RELAPSE);

        TuberculosisTreatment found = treatmentManager.getTreatmentById(updated.getId());
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(updated.getId());

        treatmentManager.deleteTreatment(updated);

        List<TuberculosisTreatment> afterDelete = treatmentManager.getTreatmentsByPatientCode(patient.getCode());
        assertThat(afterDelete).isEmpty();
    }

    @Test
    void testTreatmentPagination() throws Exception {
        Patient patient = testPatient.setup(false);
        patient = patientIoOperationRepository.save(patient);

        for (int i = 0; i < 5; i++) {
            TuberculosisTreatment treatment = testTuberculosisTreatment.setup(patient, false);
            treatmentIoOperationRepository.save(treatment);
        }

        Pageable pageable = PageRequest.of(0, 2);
        Page<TuberculosisTreatment> page = treatmentManager.getAllTreatments(pageable);

        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void testTreatmentFilters() throws Exception {
        Patient patient = testPatient.setup(false);
        patient = patientIoOperationRepository.save(patient);

        TuberculosisTreatment treatment = testTuberculosisTreatment.setup(patient, false);
        treatment.setStatus(TreatmentStatus.ONGOING);
        treatment = treatmentIoOperationRepository.save(treatment);

        Pageable pageable = PageRequest.of(0, 10);
        LocalDate dateFrom = LocalDate.of(2000, 1, 1);
        LocalDate dateTo = LocalDate.of(2100, 12, 31);

        Page<TuberculosisTreatment> page = treatmentManager.getTreatmentsByFilters(
            patient.getCode(), TreatmentStatus.ONGOING,
            dateFrom, dateTo, dateFrom, dateTo, pageable);

        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testTreatmentValidationPatientNull() throws Exception {
        TuberculosisTreatment treatment = testTuberculosisTreatment.setup(null, false);
        treatment.setPatient(null);

        assertThatThrownBy(() -> treatmentManager.newTreatment(treatment))
            .isInstanceOf(OHDataIntegrityViolationException.class);
    }

    @Test
    void testTreatmentValidationRegistrationDateNull() throws Exception {
        Patient patient = testPatient.setup(false);
        patient = patientIoOperationRepository.save(patient);

        TuberculosisTreatment treatment = testTuberculosisTreatment.setup(patient, false);
        treatment.setRegistrationDate(null);

        assertThatThrownBy(() -> treatmentManager.newTreatment(treatment))
            .isInstanceOf(OHDataIntegrityViolationException.class);
    }

    @Test
    void testVisitCRUD() throws Exception {
        Patient patient = testPatient.setup(false);
        patient = patientIoOperationRepository.save(patient);

        TuberculosisTreatment treatment = testTuberculosisTreatment.setup(patient, false);
        treatment = treatmentManager.newTreatment(treatment);

        TuberculosisVisit visit = testTuberculosisVisit.setup(treatment, false);
        visit = visitManager.newVisit(visit);

        assertThat(visit).isNotNull();
        assertThat(visit.getId()).isNotNull();

        List<TuberculosisVisit> list = visitManager.getVisitsByTreatmentId(treatment.getId());
        assertThat(list).isNotEmpty();
        assertThat(list).hasSize(1);

        TuberculosisVisit updated = list.get(0);
        updated.setSymptomsImproved(false);
        updated = visitManager.updateVisit(updated);
        assertThat(updated.getSymptomsImproved()).isFalse();

        TuberculosisVisit found = visitManager.getVisitById(updated.getId());
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(updated.getId());

        visitManager.deleteVisit(updated);

        List<TuberculosisVisit> afterDelete = visitManager.getVisitsByTreatmentId(treatment.getId());
        assertThat(afterDelete).isEmpty();
    }

    @Test
    void testVisitPagination() throws Exception {
        Patient patient = testPatient.setup(false);
        patient = patientIoOperationRepository.save(patient);

        TuberculosisTreatment treatment = testTuberculosisTreatment.setup(patient, false);
        treatment = treatmentManager.newTreatment(treatment);

        for (int i = 0; i < 5; i++) {
            TuberculosisVisit visit = testTuberculosisVisit.setup(treatment, false);
            visitIoOperationRepository.save(visit);
        }

        Pageable pageable = PageRequest.of(0, 2);
        Page<TuberculosisVisit> page = visitManager.getVisitsByTreatmentId(treatment.getId(), pageable);

        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void testVisitDateRange() throws Exception {
        Patient patient = testPatient.setup(false);
        patient = patientIoOperationRepository.save(patient);

        TuberculosisTreatment treatment = testTuberculosisTreatment.setup(patient, false);
        treatment = treatmentManager.newTreatment(treatment);

        LocalDateTime now = LocalDateTime.now();

        TuberculosisVisit visit1 = testTuberculosisVisit.setup(treatment, false);
        visit1.setVisitDate(now.minusDays(5));
        visitIoOperationRepository.save(visit1);

        TuberculosisVisit visit2 = testTuberculosisVisit.setup(treatment, false);
        visit2.setVisitDate(now.plusDays(5));
        visitIoOperationRepository.save(visit2);

        List<TuberculosisVisit> visits = visitManager.getVisitsByTreatmentIdAndDateRange(
            treatment.getId(), now.minusDays(10), now.plusDays(1));

        assertThat(visits).hasSize(1);
        assertThat(visits.get(0).getVisitDate()).isBefore(now);
    }

    @Test
    void testVisitValidationDateNull() throws Exception {
        Patient patient = testPatient.setup(false);
        patient = patientIoOperationRepository.save(patient);

        TuberculosisTreatment treatment = testTuberculosisTreatment.setup(patient, false);
        treatment = treatmentManager.newTreatment(treatment);

        TuberculosisVisit visit = testTuberculosisVisit.setup(treatment, false);
        visit.setVisitDate(null);

        assertThatThrownBy(() -> visitManager.newVisit(visit))
            .isInstanceOf(OHDataIntegrityViolationException.class);
    }

    @Test
    void testContactCRUD() throws Exception {
        Patient patient = testPatient.setup(false);
        patient = patientIoOperationRepository.save(patient);

        TuberculosisTreatment treatment = testTuberculosisTreatment.setup(patient, false);
        treatment = treatmentManager.newTreatment(treatment);

        TuberculosisContact contact = testTuberculosisContact.setup(treatment, false);
        contact = contactManager.newContact(contact);

        assertThat(contact).isNotNull();
        assertThat(contact.getId()).isNotNull();

        List<TuberculosisContact> list = contactManager.getContactsByTreatmentId(treatment.getId());
        assertThat(list).isNotEmpty();
        assertThat(list).hasSize(1);

        TuberculosisContact updated = list.get(0);
        updated.setAge(40);
        updated = contactManager.updateContact(updated);
        assertThat(updated.getAge()).isEqualTo(40);

        TuberculosisContact found = contactManager.getContactById(updated.getId());
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(updated.getId());

        contactManager.deleteContact(updated);

        List<TuberculosisContact> afterDelete = contactManager.getContactsByTreatmentId(treatment.getId());
        assertThat(afterDelete).isEmpty();
    }

    @Test
    void testContactCascadeDelete() throws Exception {
        Patient patient = testPatient.setup(false);
        patient = patientIoOperationRepository.save(patient);

        TuberculosisTreatment treatment = testTuberculosisTreatment.setup(patient, false);
        TuberculosisContact contact = testTuberculosisContact.setup(treatment, false);
        treatment.getContacts().add(contact);
        treatment = treatmentManager.newTreatment(treatment);

        Integer treatmentId = treatment.getId();
        assertThat(contactManager.getContactsByTreatmentId(treatmentId)).isNotEmpty();

        treatmentManager.deleteTreatment(treatment);

        List<TuberculosisContact> afterDelete = contactManager.getContactsByTreatmentId(treatmentId);
        assertThat(afterDelete).isEmpty();
    }
}
