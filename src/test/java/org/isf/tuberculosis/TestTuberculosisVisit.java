package org.isf.tuberculosis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.isf.tuberculosis.model.TuberculosisTreatment;
import org.isf.tuberculosis.model.TuberculosisTreatment.LabResult;
import org.isf.tuberculosis.model.TuberculosisVisit;
import org.isf.tuberculosis.model.TuberculosisVisit.DotStatus;

public class TestTuberculosisVisit {

    private final LocalDateTime visitDate = LocalDateTime.of(2024, 2, 10, 14, 30);
    private final Double weight = 58.5;
    private final Double temperature = 36.8;
    private final Boolean symptomsImproved = true;
    private final Integer adherence = 95;
    private final DotStatus dotStatus = DotStatus.SUPERVISED;
    private final String sideEffects = "Mild nausea";
    private final LabResult smearResult = LabResult.NEGATIVE;
    private final Double alt = 35.0;
    private final Double ast = 28.0;
    private final Double creatinine = 0.9;
    private final Double hemoglobin = 13.5;
    private final Integer platelets = 250;
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
        visit.setWeight(weight);
        visit.setTemperature(temperature);
        visit.setSymptomsImproved(symptomsImproved);
        visit.setAdherence(adherence);
        visit.setDotStatus(dotStatus);
        visit.setSideEffects(sideEffects);
        visit.setSmearResult(smearResult);
        visit.setAlt(alt);
        visit.setAst(ast);
        visit.setCreatinine(creatinine);
        visit.setHemoglobin(hemoglobin);
        visit.setPlatelets(platelets);
        visit.setNextAppointmentDate(nextAppointmentDate);
        visit.setNotes(notes);
    }

    public void check(TuberculosisVisit visit) {
        assertThat(visit.getVisitDate()).isCloseTo(visitDate, within(1, ChronoUnit.SECONDS));
        assertThat(visit.getWeight()).isEqualTo(weight);
        assertThat(visit.getTemperature()).isEqualTo(temperature);
        assertThat(visit.getSymptomsImproved()).isEqualTo(symptomsImproved);
        assertThat(visit.getAdherence()).isEqualTo(adherence);
        assertThat(visit.getDotStatus()).isEqualTo(dotStatus);
        assertThat(visit.getSideEffects()).isEqualTo(sideEffects);
        assertThat(visit.getSmearResult()).isEqualTo(smearResult);
        assertThat(visit.getAlt()).isEqualTo(alt);
        assertThat(visit.getAst()).isEqualTo(ast);
        assertThat(visit.getCreatinine()).isEqualTo(creatinine);
        assertThat(visit.getHemoglobin()).isEqualTo(hemoglobin);
        assertThat(visit.getPlatelets()).isEqualTo(platelets);
        assertThat(visit.getNextAppointmentDate()).isEqualTo(nextAppointmentDate);
        assertThat(visit.getNotes()).isEqualTo(notes);
    }
}
