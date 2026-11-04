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
package org.isf.reductionplan.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.isf.OHCoreTestCase;
import org.isf.partner.TestPartner;
import org.isf.partner.manager.PartnerBrowserManager;
import org.isf.partner.model.Partner;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.priceslist.model.Price;
import org.isf.reductionplan.model.AppliedReduction;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.reductionplan.model.ValidityUnit;
import org.isf.reductionplan.service.ReductionPlanRepository;
import org.isf.typology.model.Family;
import org.isf.typology.model.Typology;
import org.isf.typology.service.TypologyIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PatientReductionPlanManagerTest extends OHCoreTestCase {

	private static final ReductionPlanSelector.Policy DEFAULT_POLICY = new ReductionPlanSelector.Policy(true, true, true);

	@Autowired
	PatientReductionPlanManager manager;

	@Autowired
	PartnerBrowserManager partnerBrowserManager;

	@Autowired
	ReductionPlanRepository reductionPlanRepository;

	@Autowired
	PatientIoOperationRepository patientRepository;

	@Autowired
	TypologyIoOperationRepository typologyRepository;

	private Typology partnerType;

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
		partnerType = new Typology();
		partnerType.setCode("PRT_TEST");
		partnerType.setDescription("Test Partner Type");
		partnerType.setFamily(Family.PARTNERTYPE);
		partnerType.setActive(1);
		typologyRepository.saveAndFlush(partnerType);
	}

	/* ---------------------------- selection ---------------------------- */

	@Test
	void selectsHighestOrLowestRate() {
		AppliedReduction low = reduction(plan(1, null), 10);
		AppliedReduction high = reduction(plan(2, null), 30);

		assertThat(new ReductionPlanSelector(DEFAULT_POLICY).select(List.of(low, high))).isSameAs(high);
		assertThat(new ReductionPlanSelector(new ReductionPlanSelector.Policy(false, true, true)).select(List.of(low, high))).isSameAs(low);
		assertThat(new ReductionPlanSelector(DEFAULT_POLICY).select(List.of())).isNull();
	}

	@Test
	void onEqualRatePrefersPartnerOrHospitalPlans() {
		AppliedReduction hospital = reduction(plan(1, null), 20);
		AppliedReduction partner = reduction(plan(2, partner(5, 1)), 20);

		assertThat(new ReductionPlanSelector(DEFAULT_POLICY).select(List.of(hospital, partner))).isSameAs(partner);
		assertThat(new ReductionPlanSelector(new ReductionPlanSelector.Policy(true, false, true)).select(List.of(hospital, partner)))
			.isSameAs(hospital);
	}

	@Test
	void preferredGroupMissingFallsBackOnTheOther() {
		AppliedReduction hospital1 = reduction(plan(1, null), 20);
		AppliedReduction partner = reduction(plan(2, partner(5, 1)), 10);
		// the partner plan has a lower rate, the highest rate wins before the partner/hospital criterion
		assertThat(new ReductionPlanSelector(DEFAULT_POLICY).select(List.of(hospital1, partner))).isSameAs(hospital1);
	}

	@Test
	void onEqualRateBetweenPartnersUsesPartnerPriority() {
		AppliedReduction first = reduction(plan(1, partner(5, 1)), 20);
		AppliedReduction second = reduction(plan(2, partner(6, 2)), 20);

		assertThat(new ReductionPlanSelector(DEFAULT_POLICY).select(List.of(second, first))).isSameAs(first);
		assertThat(new ReductionPlanSelector(new ReductionPlanSelector.Policy(true, true, false)).select(List.of(first, second)))
			.isSameAs(second);
	}

	@Test
	void onEqualRateBetweenHospitalPlansPicksRandomly() {
		AppliedReduction a = reduction(plan(1, null), 20);
		AppliedReduction b = reduction(plan(2, null), 20);
		ReductionPlanSelector selector = new ReductionPlanSelector(DEFAULT_POLICY, new Random(42));
		for (int i = 0; i < 10; i++) {
			assertThat(selector.select(List.of(a, b))).isIn(a, b);
		}
	}

	@Test
	void resolvesSpecificRateBeforeGlobalRate() {
		ReductionPlan global = plan(1, null);
		global.setExamRate(BigDecimal.valueOf(10));
		ReductionPlan none = plan(2, null); // all rates at 0: not a candidate

		AppliedReduction reduction = manager.resolveReduction(List.of(global, none), PatientReductionPlanManager.GROUP_EXAM, "EX1",
			new ReductionPlanSelector(DEFAULT_POLICY));
		assertThat(reduction.reductionPlan()).isSameAs(global);
		assertThat(reduction.apply(200.0)).isEqualTo(180.0);
		assertThat(manager.resolveReduction(List.of(none), PatientReductionPlanManager.GROUP_EXAM, "EX1",
			new ReductionPlanSelector(DEFAULT_POLICY))).isNull();
	}

	@Test
	void reducedPriceDoesNotModifyOriginal() throws Exception {
		Patient patient = savePatient();
		ReductionPlan plan = savePlan("hospital", null, 25, null);
		manager.setPatientHospitalPlans(patient, List.of(plan));

		Price price = new Price(null, PatientReductionPlanManager.GROUP_EXAM, "EX1", "exam", 100.0);
		Price reduced = manager.getReducedPrice(price, manager.getApplicableReductionPlans(patient.getCode(), null));
		assertThat(reduced.getPrice()).isEqualTo(75.0);
		assertThat(price.getPrice()).isEqualTo(100.0);
	}

	/* ---------------------------- assignments -------------------------- */

	@Test
	void assignAndReplaceHospitalPlans() throws Exception {
		Patient patient = savePatient();
		ReductionPlan p1 = savePlan("p1", null, 10, null);
		ReductionPlan p2 = savePlan("p2", null, 20, null);

		manager.setPatientHospitalPlans(patient, List.of(p1, p2));
		assertThat(manager.getPatientHospitalPlans(patient.getCode())).containsExactlyInAnyOrder(p1, p2);

		manager.setPatientHospitalPlans(patient, List.of(p2));
		assertThat(manager.getPatientHospitalPlans(patient.getCode())).containsExactly(p2);
	}

	@Test
	void maxPatientsIsCumulative() throws Exception {
		Patient patient1 = savePatient();
		Patient patient2 = savePatient();
		ReductionPlan plan = savePlan("limited", null, 10, 1);

		manager.setPatientHospitalPlans(patient1, List.of(plan));
		manager.setPatientHospitalPlans(patient1, List.of());

		// the place is not released when the plan is removed from the patient
		assertThatThrownBy(() -> manager.setPatientHospitalPlans(patient2, List.of(plan))).isInstanceOf(OHDataValidationException.class);
		assertThat(manager.getAssignableHospitalPlans(patient2.getCode())).isEmpty();

		// but the patient that already used it can get it back
		assertThat(manager.getAssignableHospitalPlans(patient1.getCode())).containsExactly(plan);
		manager.setPatientHospitalPlans(patient1, List.of(plan));
		assertThat(manager.getPatientHospitalPlans(patient1.getCode())).containsExactly(plan);
	}

	@Test
	void expiredPlanDoesNotApply() throws Exception {
		Patient patient = savePatient();
		ReductionPlan plan = new ReductionPlan("10 days", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(10), BigDecimal.ZERO);
		plan.setValidityValue(10);
		plan.setValidityUnit(ValidityUnit.DAYS);
		reductionPlanRepository.saveAndFlush(plan);
		manager.setPatientHospitalPlans(patient, List.of(plan));

		assertThat(plan.getExpirationDate()).isEqualTo(plan.getCreatedDate().plusDays(10));
		assertThat(manager.getApplicableReductionPlans(patient.getCode(), LocalDateTime.now())).containsExactly(plan);
		assertThat(manager.getApplicableReductionPlans(patient.getCode(), LocalDateTime.now().plusDays(11))).isEmpty();
	}

	@Test
	void partnerPlansRequirePartnerAssociation() throws Exception {
		Patient patient = savePatient();
		Partner partner = partnerBrowserManager.savePartner(new TestPartner().setup(false, partnerType));
		ReductionPlan partnerPlan = savePlan("partner plan", partner, 15, null);
		ReductionPlan hospitalPlan = savePlan("hospital plan", null, 15, null);

		assertThatThrownBy(() -> manager.setPatientPartnerPlans(patient, partner, List.of(partnerPlan)))
			.isInstanceOf(OHDataValidationException.class);

		patient.addPartner(partner);
		patientRepository.saveAndFlush(patient);

		// a hospital plan cannot be assigned as a partner plan
		assertThatThrownBy(() -> manager.setPatientPartnerPlans(patient, partner, List.of(hospitalPlan)))
			.isInstanceOf(OHDataValidationException.class);

		assertThat(manager.getAssignablePartnerPlans(partner.getCode(), patient.getCode())).containsExactly(partnerPlan);
		manager.setPatientPartnerPlans(patient, partner, List.of(partnerPlan));
		assertThat(manager.getPatientPartnerPlans(patient.getCode(), partner.getCode())).containsExactly(partnerPlan);
		assertThat(manager.getApplicableReductionPlans(patient.getCode(), null)).containsExactly(partnerPlan);

		// the partner plans do not apply anymore once the partner is removed from the patient
		patient.removePartner(partner);
		patientRepository.saveAndFlush(patient);
		assertThat(manager.getApplicableReductionPlans(patient.getCode(), null)).isEmpty();

		manager.removePatientPartnerPlans(patient, partner);
		assertThat(manager.getPatientPartnerPlans(patient.getCode(), partner.getCode())).isEmpty();
	}

	@Test
	void partnerPriorityDefaultsToInsertionOrderAndCanBeReordered() throws Exception {
		Partner first = partnerBrowserManager.savePartner(new TestPartner().setup(false, partnerType));
		Partner second = partnerBrowserManager.savePartner(new TestPartner().setup(false, partnerType));
		assertThat(first.getPriority()).isEqualTo(1);
		assertThat(second.getPriority()).isEqualTo(2);
		assertThat(partnerBrowserManager.getPartnersByPriority()).containsExactly(first, second);

		partnerBrowserManager.updatePriorities(List.of(second, first));
		assertThat(partnerBrowserManager.getPartnersByPriority()).containsExactly(second, first);
		assertThat(partnerBrowserManager.getPartner(second.getCode()).getPriority()).isEqualTo(1);
	}

	/* ------------------------------ helpers ----------------------------- */

	private Patient savePatient() throws Exception {
		return patientRepository.saveAndFlush(new TestPatient().setup(false));
	}

	private ReductionPlan savePlan(String description, Partner partner, int examRate, Integer maxPatients) {
		ReductionPlan plan = new ReductionPlan(description, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(examRate), BigDecimal.ZERO);
		plan.setPartner(partner);
		plan.setMaxPatients(maxPatients);
		return reductionPlanRepository.saveAndFlush(plan);
	}

	private static ReductionPlan plan(int id, Partner partner) {
		ReductionPlan plan = new ReductionPlan(id, "plan " + id, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		plan.setPartner(partner);
		return plan;
	}

	private static Partner partner(int code, int priority) {
		Partner partner = new Partner();
		partner.setCode(code);
		partner.setPriority(priority);
		return partner;
	}

	private static AppliedReduction reduction(ReductionPlan plan, int rate) {
		return new AppliedReduction(plan, BigDecimal.valueOf(rate));
	}
}
