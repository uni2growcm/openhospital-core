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
package org.isf.maternity.model;

import jakarta.persistence.*;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.isf.patient.model.Patient;
import org.isf.typology.model.Typology;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "OH_FAMILYPLANNING")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "FP_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "FP_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "FP_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "FP_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "FP_LAST_MODIFIED_DATE"))
public class FamilyPlanning extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FP_ID")
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "FP_PAT_ID")
    private Patient patient;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "FP_STATUS")
    private FPStatus status;

    @Nullable
    @ManyToOne
    @JoinColumn(name = "FP_CURRENT_METHOD_CODE")
    private Typology currentMethod;

    @NotNull
    @Column(name = "FP_REGISTRATION_DATE")
    private LocalDateTime registrationDate;

    @Nullable
    @Column(name = "FP_NOTES", columnDefinition = "LONGTEXT")
    private String notes;

    @OneToMany(mappedBy = "familyPlanning", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FamilyPlanningMethodHistory> methodHistory;

    @OneToMany(mappedBy = "familyPlanning")
    private List<FamilyPlanningVisit> visits;

    @Version
    @Column(name = "FP_LOCK")
    private Integer lock;

    @Transient
    private volatile int hashCode;

    public FamilyPlanning() {
    }

    public FamilyPlanning(Patient patient, LocalDateTime registrationDate) {
        this.patient = patient;
        this.registrationDate = registrationDate;
        this.status = FPStatus.ACTIVE;
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

    public FPStatus getStatus() {
        return status;
    }

    public void setStatus(FPStatus status) {
        this.status = status;
    }

    @Nullable
    public Typology getCurrentMethod() {
        return currentMethod;
    }

    public void setCurrentMethod(@Nullable Typology currentMethod) {
        this.currentMethod = currentMethod;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Nullable
    public String getNotes() {
        return notes;
    }

    public void setNotes(@Nullable String notes) {
        this.notes = notes;
    }

    public List<FamilyPlanningMethodHistory> getMethodHistory() {
        return methodHistory;
    }

    public void setMethodHistory(List<FamilyPlanningMethodHistory> methodHistory) {
        this.methodHistory = methodHistory;
    }

    public List<FamilyPlanningVisit> getVisits() {
        return visits;
    }

    public void setVisits(List<FamilyPlanningVisit> visits) {
        this.visits = visits;
    }

    public Integer getLock() {
        return lock;
    }

    public void setLock(Integer lock) {
        this.lock = lock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FamilyPlanning other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            final int m = 23;
            int c = 133;
            c = m * c + id.hashCode();
            this.hashCode = c;
        }
        return this.hashCode;
    }

    @Override
    public String toString() {
        return "FamilyPlanning{" +
                "ID=" + id +
                ", patient=" + patient +
                ", currentMethod='" + (currentMethod != null ? currentMethod.getCode() : null) + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
