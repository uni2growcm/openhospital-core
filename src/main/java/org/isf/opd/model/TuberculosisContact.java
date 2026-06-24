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
package org.isf.opd.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_TB_CONTACT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "TBC_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "TBC_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "TBC_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "TBC_LAST_MODIFIED_DATE"))
@AttributeOverride(name = "active", column = @Column(name = "TBC_ACTIVE"))
public class TuberculosisContact extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TBC_ID")
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "TBC_TBT_ID", nullable = false)
    private TuberculosisTreatment treatment;

    @NotNull
    @Column(name = "TBC_NAME", nullable = false)
    private String name;

    @Column(name = "TBC_AGE")
    private Integer age;

    @Column(name = "TBC_GENDER")
    private Character gender;

    @Column(name = "TBC_RELATIONSHIP")
    private String relationship;

    @Column(name = "TBC_SCREENED")
    private Boolean screened;

    @Column(name = "TBC_SCREENING_DATE")
    private LocalDate screeningDate;

    @Column(name = "TBC_TB_INFECTED")
    private Boolean tbInfected;

    @Column(name = "TBC_TB_DISEASE")
    private Boolean tbDisease;

    @Column(name = "TBC_TPT_STARTED")
    private Boolean tptStarted;

    @Column(name = "TBC_NOTES", columnDefinition = "TEXT")
    private String notes;

    @Version
    @Column(name = "TBC_LOCK")
    private Integer lock;

    public TuberculosisContact() {
        super();
    }

    public TuberculosisContact(TuberculosisTreatment treatment, String name) {
        this.treatment = treatment;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public Boolean getScreened() {
        return screened;
    }

    public void setScreened(Boolean screened) {
        this.screened = screened;
    }

    public LocalDate getScreeningDate() {
        return screeningDate;
    }

    public void setScreeningDate(LocalDate screeningDate) {
        this.screeningDate = screeningDate;
    }

    public Boolean getTbInfected() {
        return tbInfected;
    }

    public void setTbInfected(Boolean tbInfected) {
        this.tbInfected = tbInfected;
    }

    public Boolean getTbDisease() {
        return tbDisease;
    }

    public void setTbDisease(Boolean tbDisease) {
        this.tbDisease = tbDisease;
    }

    public Boolean getTptStarted() {
        return tptStarted;
    }

    public void setTptStarted(Boolean tptStarted) {
        this.tptStarted = tptStarted;
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
        if (!(obj instanceof TuberculosisContact)) return false;
        TuberculosisContact other = (TuberculosisContact) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public String toString() {
        return "TuberculosisContact{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
