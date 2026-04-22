package org.isf.maternity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.isf.maternity.model.Delivery;
import org.isf.maternity.model.DeliveryType;
import org.isf.maternity.model.Pregnancy;

public class TestDelivery {

	private final LocalDateTime deliveryDate = LocalDateTime.of(2025, 10, 19, 12, 15);
	private final String mode = "SVD";
	private final String father = "John Doe";
	private final DeliveryType dvt = new DeliveryType();

	public Delivery setup(Pregnancy p, boolean usingSet) {
		Delivery d;

		if (usingSet) {
			d = new Delivery();
			set(d, p, dvt);
		} else {
			d = new Delivery(p, dvt,  deliveryDate);
			set(d, p, dvt);
		}

		return d;
	}

	private void set(Delivery d, Pregnancy p, DeliveryType deliveryType) {
		d.setPregnancy(p);
		d.setDeliveryDateTime(deliveryDate);
		d.setModeOfDelivery(mode);
		d.setFatherName(father);
		d.setDeliveryType(deliveryType);
	}

	public void check(Delivery d) {
		assertThat(d.getModeOfDelivery()).isEqualTo(mode);
		assertThat(d.getFatherName()).isEqualTo(father);
	}
}