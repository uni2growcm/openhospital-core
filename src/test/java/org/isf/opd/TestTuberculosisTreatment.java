package org.isf.opd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.isf.opd.model.TuberculosisTreatment;
import org.isf.opd.model.TuberculosisTreatment.Classification;
import org.isf.opd.model.TuberculosisTreatment.DiseaseLocation;
import org.isf.opd.model.TuberculosisTreatment.TreatmentStatus;
import org.isf.patient.model.Patient;

public class TestTuberculosisTreatment {

    private final LocalDateTime registrationDate = LocalDateTime.of(2024, 1, 15, 10, 30);
    private final Classification classification = Classification.NEW;
    private final DiseaseLocation diseaseLocation = DiseaseLocation.PULMONARY;
    private final String diseaseLocationDetails = "Right upper lobe cavity";
    private final LocalDate diagnosisDate = LocalDate.of(2024, 1, 10);
    private final LocalDate treatmentStartDate = LocalDate.of(2024, 1, 15);
    private final TreatmentStatus status = TreatmentStatus.ONGOING;
    private final String notes = "Test TB treatment notes";

    public TuberculosisTreatment setup(Patient patient, boolean usingSet) {
        TuberculosisTreatment treatment;

        if (usingSet) {
            treatment = new TuberculosisTreatment();
            set(treatment, patient);
        } else {
            treatment = new TuberculosisTreatment(patient, registrationDate, classification, diseaseLocation, treatmentStartDate, status);
            set(treatment, patient);
        }

        return treatment;
    }

    private void set(TuberculosisTreatment treatment, Patient patient) {
        treatment.setPatient(patient);
        treatment.setRegistrationDate(registrationDate);
        treatment.setClassification(classification);
        treatment.setDiseaseLocation(diseaseLocation);
        treatment.setDiseaseLocationDetails(diseaseLocationDetails);
        treatment.setDiagnosisDate(diagnosisDate);
        treatment.setTreatmentStartDate(treatmentStartDate);
        treatment.setStatus(status);
        treatment.setNotes(notes);
    }

    public void check(TuberculosisTreatment treatment) {
        assertThat(treatment.getRegistrationDate()).isCloseTo(registrationDate, within(1, ChronoUnit.SECONDS));
        assertThat(treatment.getClassification()).isEqualTo(classification);
        assertThat(treatment.getDiseaseLocation()).isEqualTo(diseaseLocation);
        assertThat(treatment.getDiseaseLocationDetails()).isEqualTo(diseaseLocationDetails);
        assertThat(treatment.getDiagnosisDate()).isEqualTo(diagnosisDate);
        assertThat(treatment.getTreatmentStartDate()).isEqualTo(treatmentStartDate);
        assertThat(treatment.getStatus()).isEqualTo(status);
        assertThat(treatment.getNotes()).isEqualTo(notes);
    }
}
