package org.isf.opd;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.isf.opd.model.TuberculosisContact;
import org.isf.opd.model.TuberculosisTreatment;

public class TestTuberculosisContact {

    private final String name = "John Contact";
    private final Integer age = 35;
    private final Character gender = 'M';
    private final String relationship = "TB_CONTACT_HOUSEHOLD";
    private final Boolean screened = true;
    private final LocalDate screeningDate = LocalDate.of(2024, 1, 20);
    private final Boolean tbInfected = true;
    private final Boolean tptStarted = true;
    private final String notes = "Started on IPT";

    public TuberculosisContact setup(TuberculosisTreatment treatment, boolean usingSet) {
        TuberculosisContact contact;

        if (usingSet) {
            contact = new TuberculosisContact();
            set(contact, treatment);
        } else {
            contact = new TuberculosisContact(treatment, name);
            set(contact, treatment);
        }

        return contact;
    }

    private void set(TuberculosisContact contact, TuberculosisTreatment treatment) {
        contact.setTreatment(treatment);
        contact.setName(name);
        contact.setAge(age);
        contact.setGender(gender);
        contact.setRelationship(relationship);
        contact.setScreened(screened);
        contact.setScreeningDate(screeningDate);
        contact.setTbInfected(tbInfected);
        contact.setTptStarted(tptStarted);
        contact.setNotes(notes);
    }

    public void check(TuberculosisContact contact) {
        assertThat(contact.getName()).isEqualTo(name);
        assertThat(contact.getAge()).isEqualTo(age);
        assertThat(contact.getGender()).isEqualTo(gender);
        assertThat(contact.getRelationship()).isEqualTo(relationship);
        assertThat(contact.getScreened()).isEqualTo(screened);
        assertThat(contact.getScreeningDate()).isEqualTo(screeningDate);
        assertThat(contact.getTbInfected()).isEqualTo(tbInfected);
        assertThat(contact.getTptStarted()).isEqualTo(tptStarted);
        assertThat(contact.getNotes()).isEqualTo(notes);
    }
}
