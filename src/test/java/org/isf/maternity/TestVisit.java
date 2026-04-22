package org.isf.maternity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.isf.maternity.model.Pregnancy;
import org.isf.maternity.model.Visit;
import org.isf.maternity.model.VisitType;

public class TestVisit {

	private final LocalDateTime visitDate = LocalDateTime.of(2025, 3, 15, 10, 30);

	private final Integer weeks = 8;
	private final Integer days = 3;
	private final Double weight = 65.5;

	public Visit setup(Pregnancy p, VisitType vt, boolean usingSet) {
		Visit v;

		if (usingSet) {
			v = new Visit();
			set(v, p, vt);
		} else {
			v = new Visit(p, vt, visitDate);
			set(v, p, vt);
		}

		return v;
	}

	private void set(Visit v, Pregnancy p, VisitType vt) {
		v.setPregnancy(p);
		v.setVisitDate(visitDate);
		v.setGestationalWeeks(weeks);
		v.setGestationalDays(days);
		v.setMaternalWeight(weight);
		v.setVisitType(vt);
	}

	public void check(Visit v) {
		assertThat(v.getGestationalWeeks()).isEqualTo(weeks);
		assertThat(v.getGestationalDays()).isEqualTo(days);
		assertThat(v.getMaternalWeight()).isEqualTo(weight);
		assertThat(v.getVisitType()).isNotNull();
	}
}