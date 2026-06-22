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

@Entity
@Table(name = "OH_FPMETHODHISTORY")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "FPMH_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "FPMH_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "FPMH_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "FPMH_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "FPMH_LAST_MODIFIED_DATE"))
public class FamilyPlanningMethodHistory extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FPMH_ID")
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "FPMH_FP_ID")
    @JsonIgnore
    private FamilyPlanning familyPlanning;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "FPMH_METHOD_CODE")
    private Typology method;

    @NotNull
    @Column(name = "FPMH_START_DATE")
    private LocalDate startDate;

    @Nullable
    @Column(name = "FPMH_END_DATE")
    private LocalDate endDate;

    @Nullable
    @Column(name = "FPMH_STOP_REASON", length = 255)
    private String stopReason;

    @Version
    @Column(name = "FPMH_LOCK")
    private Integer lock;

    @Transient
    private volatile int hashCode;

    public FamilyPlanningMethodHistory() {
    }

    public FamilyPlanningMethodHistory(FamilyPlanning familyPlanning, Typology method, LocalDate startDate) {
        this.familyPlanning = familyPlanning;
        this.method = method;
        this.startDate = startDate;
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

    public Typology getMethod() {
        return method;
    }

    public void setMethod(Typology method) {
        this.method = method;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Nullable
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(@Nullable LocalDate endDate) {
        this.endDate = endDate;
    }

    @Nullable
    public String getStopReason() {
        return stopReason;
    }

    public void setStopReason(@Nullable String stopReason) {
        this.stopReason = stopReason;
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
        if (!(o instanceof FamilyPlanningMethodHistory other)) return false;
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
        return "FamilyPlanningMethodHistory{" +
                "ID=" + id +
                ", familyPlanningId=" + (familyPlanning != null ? familyPlanning.getId() : null) +
                ", method=" + (method != null ? method.getCode() : null) +
                ", startDate=" + startDate +
                '}';
    }
}
