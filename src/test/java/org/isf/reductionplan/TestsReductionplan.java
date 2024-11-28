package org.isf.reductionplan;

import static org.assertj.core.api.Assertions.assertThat;
import org.isf.reductionplan.model.ReductionPlan;
import org.junit.jupiter.api.Test;

class TestsReductionplan{


		// Valeurs de test
		private final String description = "Plan de réduction test";
		private final double operationRate = 20.5f;
		private final double medicalRate = 15.0f;
		private final double examRate = 10.0f;
		private final double otherRate = 5.5f;

		// Méthode pour initialiser un Reductionplan
		public ReductionPlan setup(boolean usingConstructor) {
			if (usingConstructor) {
				// Création avec le constructeur complet
				return new ReductionPlan(description, operationRate, medicalRate, examRate, otherRate);
			} else {
				// Création vide + utilisation des setters
				ReductionPlan reductionplan = new ReductionPlan();
				reductionplan.setDescription(description);
				reductionplan.setOperationRate(operationRate);
				reductionplan.setExamRate(examRate);
				reductionplan.setMedicalRate(medicalRate);
				reductionplan.setOtherRate(otherRate);

				return reductionplan;
			}
		}

		// Méthode de vérification des valeurs
		public void check(ReductionPlan reductionplan) {
			assertThat(reductionplan.getDescription()).isEqualTo(description);
			assertThat(reductionplan.getOperationRate()).isEqualTo(operationRate);
			assertThat(reductionplan.getMedicalRate()).isEqualTo(medicalRate);
			assertThat(reductionplan.getExamRate()).isEqualTo(examRate);
			assertThat(reductionplan.getOtherRate()).isEqualTo(otherRate);

		}

		
	}





