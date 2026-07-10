package org.isf.tuberculosis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.isf.tuberculosis.model.DotStatus;
import org.isf.tuberculosis.model.TuberculosisTreatment;
import org.isf.tuberculosis.model.TuberculosisVisit;

public class TestTuberculosisVisit {

    private final LocalDateTime visitDate = LocalDateTime.of(2024, 2, 10, 14, 30);
    private final Boolean symptomsImproved = true;
    private final Integer adherence = 95;
    private final DotStatus dotStatus = DotStatus.SUPERVISED;
    private final String sideEffects = "Mild nausea";
    private final LocalDate nextAppointmentDate = LocalDate.of(2024, 3, 10);
    private final String notes = "Test visit notes";

    public TuberculosisVisit setup(TuberculosisTreatment treatment, boolean usingSet) {
        TuberculosisVisit visit;

        if (usingSet) {
            visit = new TuberculosisVisit();
            set(visit, treatment);
        } else {
            visit = new TuberculosisVisit(treatment, visitDate);
            set(visit, treatment);
        }

        return visit;
    }

    private void set(TuberculosisVisit visit, TuberculosisTreatment treatment) {
        visit.setTreatment(treatment);
        visit.setVisitDate(visitDate);
        visit.setSymptomsImproved(symptomsImproved);
        visit.setAdherence(adherence);
        visit.setDotStatus(dotStatus);
        visit.setSideEffects(sideEffects);
        visit.setNextAppointmentDate(nextAppointmentDate);
        visit.setNotes(notes);
    }

    public void check(TuberculosisVisit visit) {
        assertThat(visit.getVisitDate()).isCloseTo(visitDate, within(1, ChronoUnit.SECONDS));
        assertThat(visit.getSymptomsImproved()).isEqualTo(symptomsImproved);
        assertThat(visit.getAdherence()).isEqualTo(adherence);
        assertThat(visit.getDotStatus()).isEqualTo(dotStatus);
        assertThat(visit.getSideEffects()).isEqualTo(sideEffects);
        assertThat(visit.getNextAppointmentDate()).isEqualTo(nextAppointmentDate);
        assertThat(visit.getNotes()).isEqualTo(notes);
    }
}
