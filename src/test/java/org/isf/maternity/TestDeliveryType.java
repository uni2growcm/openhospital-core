package org.isf.maternity;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.maternity.model.DeliveryType;

public class TestDeliveryType {

	private final String code = "VD";
	private final String description = "Vaginal Delivery";

	public DeliveryType setup() {
		DeliveryType dt = new DeliveryType();
		dt.setCode(code);
		dt.setDescription(description);
		return dt;
	}

	public void check(DeliveryType dt) {
		assertThat(dt.getCode()).isEqualTo(code);
		assertThat(dt.getDescription()).isEqualTo(description);
	}
}