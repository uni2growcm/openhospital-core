package org.isf.maternity;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.maternity.model.VisitType;

public class TestVisitType {

	private final String code = "ANC";
	private final String description = "Antenatal Care";

	public VisitType setup() {
		VisitType vt = new VisitType();
		vt.setCode(code);
		vt.setDescription(description);
		return vt;
	}

	public void check(VisitType vt) {
		assertThat(vt.getCode()).isEqualTo(code);
		assertThat(vt.getDescription()).isEqualTo(description);
	}
}