
/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

package org.isf.reductionplan;

import static org.assertj.core.api.Assertions.assertThat;
import org.isf.reductionplan.model.ReductionPlan;

class TestsReductionplan {

	private final String description = "Plan de réduction test";
	private final double operationRate = 20.5f;
	private final double medicalRate = 15.0f;
	private final double examRate = 10.0f;
	private final double otherRate = 5.5f;

	public ReductionPlan setup(boolean usingConstructor) {
		if (usingConstructor) {
			return new ReductionPlan(description, operationRate, medicalRate, examRate, otherRate);
		} else {
			ReductionPlan reductionplan = new ReductionPlan();
			reductionplan.setDescription(description);
			reductionplan.setOperationRate(operationRate);
			reductionplan.setExamRate(examRate);
			reductionplan.setMedicalRate(medicalRate);
			reductionplan.setOtherRate(otherRate);

			return reductionplan;
		}
	}

	public void check(ReductionPlan reductionplan) {
		assertThat(reductionplan.getDescription()).isEqualTo(description);
		assertThat(reductionplan.getOperationRate()).isEqualTo(operationRate);
		assertThat(reductionplan.getMedicalRate()).isEqualTo(medicalRate);
		assertThat(reductionplan.getExamRate()).isEqualTo(examRate);
		assertThat(reductionplan.getOtherRate()).isEqualTo(otherRate);
	}
}
