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
package org.isf.tuberculosis.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_TB_VISIT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "TBV_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "TBV_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "TBV_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "TBV_LAST_MODIFIED_DATE"))
@AttributeOverride(name = "active", column = @Column(name = "TBV_ACTIVE"))
public class TuberculosisVisit extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TBV_ID")
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "TBV_TBT_ID", nullable = false)
    private TuberculosisTreatment treatment;

    @NotNull
    @Column(name = "TBV_VISIT_DATE", nullable = false)
    private LocalDateTime visitDate;

    @Column(name = "TBV_SYMPTOMS_IMPROVED")
    private Boolean symptomsImproved;

    @Column(name = "TBV_ADHERENCE")
    private Integer adherence;

    @Enumerated(EnumType.STRING)
    @Column(name = "TBV_DOT_STATUS")
    private DotStatus dotStatus;

    @Column(name = "TBV_SIDE_EFFECTS", columnDefinition = "TEXT")
    private String sideEffects;

    @Enumerated(EnumType.STRING)
    @Column(name = "TBV_SMEAR_RESULT")
    private LabResult smearResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "TBV_GENEXPERT_RESULT")
    private LabResult geneXpertResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "TBV_GENEXPERT_RIF_RESISTANCE")
    private ResistanceResult geneXpertRifResistance;

    @Enumerated(EnumType.STRING)
    @Column(name = "TBV_CULTURE_RESULT")
    private LabResult cultureResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "TBV_DST_RESULT")
    private DstResult dstResult;

    @Column(name = "TBV_CONVERSION_DATE")
    private LocalDate conversionDate;

    @Column(name = "TBV_CHEST_XRAY_FINDINGS")
    private String chestXrayFindings;

    @Column(name = "TBV_ALT")
    private Double alt;

    @Column(name = "TBV_AST")
    private Double ast;

    @Column(name = "TBV_CREATININE")
    private Double creatinine;

    @Column(name = "TBV_HEMOGLOBIN")
    private Double hemoglobin;

    @Column(name = "TBV_PLATELETS")
    private Integer platelets;

    @Column(name = "TBV_NEXT_APPOINTMENT_DATE")
    private LocalDate nextAppointmentDate;

    @Column(name = "TBV_NOTES", columnDefinition = "TEXT")
    private String notes;

    @Version
    @Column(name = "TBV_LOCK")
    private Integer lock;

    public TuberculosisVisit() {
        super();
    }

    public TuberculosisVisit(TuberculosisTreatment treatment, LocalDateTime visitDate) {
        this.treatment = treatment;
        this.visitDate = visitDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TuberculosisTreatment getTreatment() {
        return treatment;
    }

    public void setTreatment(TuberculosisTreatment treatment) {
        this.treatment = treatment;
    }

    public LocalDateTime getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDateTime visitDate) {
        this.visitDate = visitDate;
    }

    public Boolean getSymptomsImproved() {
        return symptomsImproved;
    }

    public void setSymptomsImproved(Boolean symptomsImproved) {
        this.symptomsImproved = symptomsImproved;
    }

    public Integer getAdherence() {
        return adherence;
    }

    public void setAdherence(Integer adherence) {
        this.adherence = adherence;
    }

    public DotStatus getDotStatus() {
        return dotStatus;
    }

    public void setDotStatus(DotStatus dotStatus) {
        this.dotStatus = dotStatus;
    }

    public String getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(String sideEffects) {
        this.sideEffects = sideEffects;
    }

    public LabResult getSmearResult() {
        return smearResult;
    }

    public void setSmearResult(LabResult smearResult) {
        this.smearResult = smearResult;
    }

    public LabResult getGeneXpertResult() {
        return geneXpertResult;
    }

    public void setGeneXpertResult(LabResult geneXpertResult) {
        this.geneXpertResult = geneXpertResult;
    }

    public ResistanceResult getGeneXpertRifResistance() {
        return geneXpertRifResistance;
    }

    public void setGeneXpertRifResistance(ResistanceResult geneXpertRifResistance) {
        this.geneXpertRifResistance = geneXpertRifResistance;
    }

    public LabResult getCultureResult() {
        return cultureResult;
    }

    public void setCultureResult(LabResult cultureResult) {
        this.cultureResult = cultureResult;
    }

    public DstResult getDstResult() {
        return dstResult;
    }

    public void setDstResult(DstResult dstResult) {
        this.dstResult = dstResult;
    }

    public LocalDate getConversionDate() {
        return conversionDate;
    }

    public void setConversionDate(LocalDate conversionDate) {
        this.conversionDate = conversionDate;
    }

    public String getChestXrayFindings() {
        return chestXrayFindings;
    }

    public void setChestXrayFindings(String chestXrayFindings) {
        this.chestXrayFindings = chestXrayFindings;
    }

    public Double getAlt() {
        return alt;
    }

    public void setAlt(Double alt) {
        this.alt = alt;
    }

    public Double getAst() {
        return ast;
    }

    public void setAst(Double ast) {
        this.ast = ast;
    }

    public Double getCreatinine() {
        return creatinine;
    }

    public void setCreatinine(Double creatinine) {
        this.creatinine = creatinine;
    }

    public Double getHemoglobin() {
        return hemoglobin;
    }

    public void setHemoglobin(Double hemoglobin) {
        this.hemoglobin = hemoglobin;
    }

    public Integer getPlatelets() {
        return platelets;
    }

    public void setPlatelets(Integer platelets) {
        this.platelets = platelets;
    }

    public LocalDate getNextAppointmentDate() {
        return nextAppointmentDate;
    }

    public void setNextAppointmentDate(LocalDate nextAppointmentDate) {
        this.nextAppointmentDate = nextAppointmentDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getLock() {
        return lock;
    }

    public void setLock(Integer lock) {
        this.lock = lock;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TuberculosisVisit)) return false;
        TuberculosisVisit other = (TuberculosisVisit) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public String toString() {
        return "TuberculosisVisit{" +
            "id=" + id +
            ", visitDate=" + visitDate +
            '}';
    }
}
