package org.isf.mortuary;

import org.isf.mortuary.model.Mortuary;
import org.isf.utils.exception.OHException;

import static org.assertj.core.api.Assertions.assertThat;

public class TestMortuary {
	private String code = "2";
	private String description = "Paul Bernard";
	private int daysMax = 4;
	private int daysMin = 1;

	public Mortuary setup(boolean usingSet) throws OHException {
		Mortuary mortuary;

		if (usingSet) {
			mortuary = new Mortuary();
			setParameters(mortuary);
		} else{
			mortuary = new Mortuary(code, description, daysMax, daysMin);
		}
		return mortuary;
	}

	public void setParameters(Mortuary mortuary) {
		mortuary.setCode(code);
		mortuary.setDescription(description);
		mortuary.setDaysMax(daysMax);
		mortuary.setDaysMin(daysMin);
	}

	public void check(Mortuary mortuary) {
		assertThat(mortuary.getCode()).isEqualTo(code);
		assertThat(mortuary.getDescription()).isEqualTo(description);
		assertThat(mortuary.getDaysMax()).isEqualTo(daysMax);
		assertThat(mortuary.getDaysMin()).isEqualTo(daysMin);
	}
}
