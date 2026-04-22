package org.isf.maternity;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.maternity.model.Delivery;
import org.isf.maternity.model.Newborn;

public class TestNewBorn {

	private final String name = "Baby Doe";
	private final String gender = "M";
	private final Integer weight = 3200;

	public Newborn setup(Delivery d, boolean usingSet) {
		Newborn n;

		if (usingSet) {
			n = new Newborn();
			set(n, d);
		} else {
			n = new Newborn(d, name, gender);
			set(n, d);
		}

		return n;
	}

	private void set(Newborn n, Delivery d) {
		n.setDelivery(d);
		n.setName(name);
		n.setGender(gender);
		n.setBirthWeight(weight);
	}

	public void check(Newborn n) {
		assertThat(n.getName()).isEqualTo(name);
		assertThat(n.getGender()).isEqualTo(gender);
		assertThat(n.getBirthWeight()).isEqualTo(weight);
	}
}
