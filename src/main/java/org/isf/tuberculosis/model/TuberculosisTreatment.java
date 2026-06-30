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
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "OH_TB_TREATMENT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "TBT_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "TBT_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "TBT_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "TBT_LAST_MODIFIED_DATE"))
@AttributeOverride(name = "active", column = @Column(name = "TBT_ACTIVE"))
public class TuberculosisTreatment extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TBT_ID")
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "TBT_PAT_ID", nullable = false)
    private Patient patient;

    @NotNull
    @Column(name = "TBT_REG_DATE", nullable = false)
    private LocalDateTime registrationDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TBT_CLASSIFICATION", nullable = false)
    private Classification classification;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TBT_DISEASE_LOCATION", nullable = false)
    private DiseaseLocation diseaseLocation;

    @Column(name = "TBT_DISEASE_LOCATION_DETAILS")
    private String diseaseLocationDetails;

    @Column(name = "TBT_DIAGNOSIS_DATE")
    private LocalDate diagnosisDate;

    @Column(name = "TBT_REGIMEN_CODE")
    private String regimenCode;

    @NotNull
    @Column(name = "TBT_TREATMENT_START_DATE", nullable = false)
    private LocalDate treatmentStartDate;

    @Column(name = "TBT_TREATMENT_END_DATE")
    private LocalDate treatmentEndDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "TBT_HIV_STATUS")
    private HivStatus hivStatus;

    @Column(name = "TBT_HIV_TEST_DATE")
    private LocalDate hivTestDate;

    @Column(name = "TBT_DIABETES")
    private Boolean diabetes;

    @Column(name = "TBT_KNOWN_CONTACT")
    private Boolean knownContact;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TBT_STATUS", nullable = false)
    private TreatmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "TBT_OUTCOME")
    private TreatmentOutcome outcome;

    @Column(name = "TBT_OUTCOME_DATE")
    private LocalDate outcomeDate;

    @Column(name = "TBT_NOTES", columnDefinition = "TEXT")
    private String notes;

    @Version
    @Column(name = "TBT_LOCK")
    private Integer lock;

    @JsonIgnore
    @OneToMany(mappedBy = "treatment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TuberculosisVisit> visits = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "treatment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TuberculosisContact> contacts = new ArrayList<>();

    public TuberculosisTreatment() {
        super();
    }

    public TuberculosisTreatment(Patient patient, LocalDateTime registrationDate, Classification classification,
                                  DiseaseLocation diseaseLocation, LocalDate treatmentStartDate, TreatmentStatus status) {
        this.patient = patient;
        this.registrationDate = registrationDate;
        this.classification = classification;
        this.diseaseLocation = diseaseLocation;
        this.treatmentStartDate = treatmentStartDate;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public DiseaseLocation getDiseaseLocation() {
        return diseaseLocation;
    }

    public void setDiseaseLocation(DiseaseLocation diseaseLocation) {
        this.diseaseLocation = diseaseLocation;
    }

    public String getDiseaseLocationDetails() {
        return diseaseLocationDetails;
    }

    public void setDiseaseLocationDetails(String diseaseLocationDetails) {
        this.diseaseLocationDetails = diseaseLocationDetails;
    }

    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    public String getRegimenCode() {
        return regimenCode;
    }

    public void setRegimenCode(String regimenCode) {
        this.regimenCode = regimenCode;
    }

    public LocalDate getTreatmentStartDate() {
        return treatmentStartDate;
    }

    public void setTreatmentStartDate(LocalDate treatmentStartDate) {
        this.treatmentStartDate = treatmentStartDate;
    }

    public LocalDate getTreatmentEndDate() {
        return treatmentEndDate;
    }

    public void setTreatmentEndDate(LocalDate treatmentEndDate) {
        this.treatmentEndDate = treatmentEndDate;
    }

    public HivStatus getHivStatus() {
        return hivStatus;
    }

    public void setHivStatus(HivStatus hivStatus) {
        this.hivStatus = hivStatus;
    }

    public LocalDate getHivTestDate() {
        return hivTestDate;
    }

    public void setHivTestDate(LocalDate hivTestDate) {
        this.hivTestDate = hivTestDate;
    }

    public Boolean getDiabetes() {
        return diabetes;
    }

    public void setDiabetes(Boolean diabetes) {
        this.diabetes = diabetes;
    }

    public Boolean getKnownContact() {
        return knownContact;
    }

    public void setKnownContact(Boolean knownContact) {
        this.knownContact = knownContact;
    }

    public TreatmentStatus getStatus() {
        return status;
    }

    public void setStatus(TreatmentStatus status) {
        this.status = status;
    }

    public TreatmentOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(TreatmentOutcome outcome) {
        this.outcome = outcome;
    }

    public LocalDate getOutcomeDate() {
        return outcomeDate;
    }

    public void setOutcomeDate(LocalDate outcomeDate) {
        this.outcomeDate = outcomeDate;
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

    public List<TuberculosisVisit> getVisits() {
        return visits;
    }

    public void setVisits(List<TuberculosisVisit> visits) {
        this.visits = visits;
    }

    public List<TuberculosisContact> getContacts() {
        return contacts;
    }

    public void setContacts(List<TuberculosisContact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TuberculosisTreatment)) return false;
        TuberculosisTreatment other = (TuberculosisTreatment) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public String toString() {
        return "TuberculosisTreatment{" +
            "id=" + id +
            ", patient=" + (patient != null ? patient.getCode() : null) +
            ", classification=" + classification +
            ", status=" + status +
            '}';
    }
}
