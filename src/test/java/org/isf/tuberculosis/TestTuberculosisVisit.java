package org.isf.tuberculosis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.isf.tuberculosis.model.DotStatus;
import org.isf.tuberculosis.model.DstResult;
import org.isf.tuberculosis.model.LabResult;
import org.isf.tuberculosis.model.ResistanceResult;
import org.isf.tuberculosis.model.TuberculosisTreatment;
import org.isf.tuberculosis.model.TuberculosisVisit;

public class TestTuberculosisVisit {

    private final LocalDateTime visitDate = LocalDateTime.of(2024, 2, 10, 14, 30);
    private final Boolean symptomsImproved = true;
    private final Integer adherence = 95;
    private final DotStatus dotStatus = DotStatus.SUPERVISED;
    private final String sideEffects = "Mild nausea";
    private final LabResult smearResult = LabResult.NEGATIVE;
    private final LabResult geneXpertResult = LabResult.NEGATIVE;
    private final ResistanceResult geneXpertRifResistance = ResistanceResult.NOT_DETECTED;
    private final LabResult cultureResult = LabResult.POSITIVE;
    private final DstResult dstResult = DstResult.SUSCEPTIBLE;
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
        visit.setSymptomsImproved(symptomsImproved);
        visit.setAdherence(adherence);
        visit.setDotStatus(dotStatus);
        visit.setSideEffects(sideEffects);
        visit.setSmearResult(smearResult);
        visit.setGeneXpertResult(geneXpertResult);
        visit.setGeneXpertRifResistance(geneXpertRifResistance);
        visit.setCultureResult(cultureResult);
        visit.setDstResult(dstResult);
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
        assertThat(visit.getSymptomsImproved()).isEqualTo(symptomsImproved);
        assertThat(visit.getAdherence()).isEqualTo(adherence);
        assertThat(visit.getDotStatus()).isEqualTo(dotStatus);
        assertThat(visit.getSideEffects()).isEqualTo(sideEffects);
        assertThat(visit.getSmearResult()).isEqualTo(smearResult);
        assertThat(visit.getGeneXpertResult()).isEqualTo(geneXpertResult);
        assertThat(visit.getGeneXpertRifResistance()).isEqualTo(geneXpertRifResistance);
        assertThat(visit.getCultureResult()).isEqualTo(cultureResult);
        assertThat(visit.getDstResult()).isEqualTo(dstResult);
        assertThat(visit.getAlt()).isEqualTo(alt);
        assertThat(visit.getAst()).isEqualTo(ast);
        assertThat(visit.getCreatinine()).isEqualTo(creatinine);
        assertThat(visit.getHemoglobin()).isEqualTo(hemoglobin);
        assertThat(visit.getPlatelets()).isEqualTo(platelets);
        assertThat(visit.getNextAppointmentDate()).isEqualTo(nextAppointmentDate);
        assertThat(visit.getNotes()).isEqualTo(notes);
    }
}
