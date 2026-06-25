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

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.isf.typology.model.Typology;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "OH_FAMILYPLANNINGVISIT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "FPV_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "FPV_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "FPV_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "FPV_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "FPV_LAST_MODIFIED_DATE"))
public class FamilyPlanningVisit extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FPV_ID")
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "FPV_FP_ID")
    @JsonIgnore
    private FamilyPlanning familyPlanning;

    @NotNull
    @Column(name = "FPV_VISIT_DATE")
    private LocalDateTime visitDate;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "FPV_VISIT_TYPE_CODE")
    private Typology visitType;

    @Nullable
    @Column(name = "FPV_NEXT_APP_DATE")
    private LocalDate nextAppointmentDate;

    @Nullable
    @Column(name = "FPV_NOTES", columnDefinition = "LONGTEXT")
    private String notes;

    @Version
    @Column(name = "FPV_LOCK")
    private Integer lock;

    @Transient
    private volatile int hashCode;

    public FamilyPlanningVisit() {
    }

    public FamilyPlanningVisit(FamilyPlanning familyPlanning, LocalDateTime visitDate, Typology visitType) {
        this.familyPlanning = familyPlanning;
        this.visitDate = visitDate;
        this.visitType = visitType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FamilyPlanning getFamilyPlanning() {
        return familyPlanning;
    }

    public void setFamilyPlanning(FamilyPlanning familyPlanning) {
        this.familyPlanning = familyPlanning;
    }

    public LocalDateTime getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDateTime visitDate) {
        this.visitDate = visitDate;
    }

    public Typology getVisitType() {
        return visitType;
    }

    public void setVisitType(Typology visitType) {
        this.visitType = visitType;
    }

    @Nullable
    public LocalDate getNextAppointmentDate() {
        return nextAppointmentDate;
    }

    public void setNextAppointmentDate(@Nullable LocalDate nextAppointmentDate) {
        this.nextAppointmentDate = nextAppointmentDate;
    }

    @Nullable
    public String getNotes() {
        return notes;
    }

    public void setNotes(@Nullable String notes) {
        this.notes = notes;
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
        if (!(o instanceof FamilyPlanningVisit other)) return false;
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
        return "FamilyPlanningVisit{" +
                "ID=" + id +
                ", familyPlanningId=" + (familyPlanning != null ? familyPlanning.getId() : null) +
                ", visitDate=" + visitDate +
                ", visitType=" + (visitType != null ? visitType.getCode() : null) +
                '}';
    }
}
