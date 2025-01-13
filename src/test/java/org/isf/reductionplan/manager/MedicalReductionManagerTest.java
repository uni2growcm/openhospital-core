package org.isf.reductionplan.manager;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medicals.TestMedical;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.manager.MedicalTypeBrowserManager;
import org.isf.medtype.model.MedicalType;
import org.isf.reductionplan.data.ReductionPlanDataGenerate;
import org.isf.reductionplan.model.MedicalReduction;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.reductionplan.service.MedicalReductionIoOperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MedicalReductionManagerTest extends OHCoreTestCase {

	@Autowired
	MedicalReductionIoOperationRepository repository;

	@Autowired
	MedicalReductionManager manager;

	@Autowired
	ReductionPlanManager reductionPlanManager;

	@Autowired
	MedicalBrowsingManager medicalBrowsingManager;

	@Autowired
	MedicalTypeBrowserManager medicalTypeBrowserManager;

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	@DisplayName("Should get medical reduction by reduction plan")
	void testGetByReductionPlan() throws Exception {
		TestMedicalType testMedicalType = new TestMedicalType();
		TestMedical testMedical = new TestMedical();
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = reductionPlanManager.save(reductionPlan);

		MedicalReduction medicalReduction = ReductionPlanDataGenerate.generateMedicalReductionFixture(null, reductionPlan);
		MedicalType medicalType = medicalTypeBrowserManager.newMedicalType(medicalReduction.getMedical().getType());
		medicalReduction.getMedical().setType(medicalType);
		Medical medical = medicalBrowsingManager.newMedical(medicalReduction.getMedical());
		medicalReduction.setMedical(medical);
		repository.saveAndFlush(medicalReduction);

		MedicalType medicalType1 = testMedicalType.setup(false);
		medicalType1.setCode("A");
		medicalType1.setDescription("test description 1");
		medicalType1 = medicalTypeBrowserManager.newMedicalType(medicalType1);

		Medical medical1 = testMedical.setup(medicalType1, false);
		medical1.setProdCode("TP2");
		medical1.setDescription("test exam description 1");
		medical1 = medicalBrowsingManager.newMedical(medical1);

		MedicalReduction medicalReduction1 = ReductionPlanDataGenerate.generateMedicalReductionFixture(medical1, reductionPlan);
		repository.saveAndFlush(medicalReduction1);

		List<MedicalReduction> existingMedicalReductionList = manager.getByReductionPlanId(reductionPlan.getId(), false);

		assertThat(existingMedicalReductionList.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Should save exam reduction")
	void testSave() throws Exception {
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = reductionPlanManager.save(reductionPlan);

		MedicalReduction medicalReduction = ReductionPlanDataGenerate.generateMedicalReductionFixture(null, reductionPlan);
		MedicalType medicalType = medicalTypeBrowserManager.newMedicalType(medicalReduction.getMedical().getType());
		medicalReduction.getMedical().setType(medicalType);
		Medical medical = medicalBrowsingManager.newMedical(medicalReduction.getMedical());
		medicalReduction.setMedical(medical);

		MedicalReduction saveMedicalReduction = manager.save(medicalReduction);
		assertThat(saveMedicalReduction).isNotNull();
		assertThat(saveMedicalReduction.getReductionPlan()).isEqualTo(reductionPlan);
		assertThat(saveMedicalReduction.getMedical()).isEqualTo(medical);
		assertThat(saveMedicalReduction.getReductionRate()).isEqualTo(1.0);
	}

	@Test
	@DisplayName("Should delete an exam reduction")
	void testDelete() throws Exception {
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = reductionPlanManager.save(reductionPlan);

		MedicalReduction medicalReduction = ReductionPlanDataGenerate.generateMedicalReductionFixture(null, reductionPlan);
		MedicalType medicalType = medicalTypeBrowserManager.newMedicalType(medicalReduction.getMedical().getType());
		medicalReduction.getMedical().setType(medicalType);
		Medical medical = medicalBrowsingManager.newMedical(medicalReduction.getMedical());
		medicalReduction.setMedical(medical);
		medicalReduction = manager.save(medicalReduction);

		MedicalReduction deletedMedicalReduction = manager.delete(medicalReduction);

		assertThat(deletedMedicalReduction.isDeleted()).isTrue();
		assertThat(manager.getByReductionPlanId(reductionPlan.getId(), false).size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should delete a list of exam reduction")
	void testDeleteBulk() throws Exception {
		TestMedicalType testMedicalType = new TestMedicalType();
		TestMedical testMedical = new TestMedical();
		String testDescription = "Test description";
		ReductionPlan reductionPlan = ReductionPlanDataGenerate.generateReductionPlanFixtures(1, testDescription).get(0);
		reductionPlan = reductionPlanManager.save(reductionPlan);

		MedicalReduction medicalReduction = ReductionPlanDataGenerate.generateMedicalReductionFixture(null, reductionPlan);
		MedicalType medicalType = medicalTypeBrowserManager.newMedicalType(medicalReduction.getMedical().getType());
		medicalReduction.getMedical().setType(medicalType);
		Medical medical = medicalBrowsingManager.newMedical(medicalReduction.getMedical());
		medicalReduction.setMedical(medical);

		MedicalType medicalType1 = testMedicalType.setup(false);
		medicalType1.setCode("A");
		medicalType1.setDescription("test description 1");
		medicalType1 = medicalTypeBrowserManager.newMedicalType(medicalType1);

		Medical medical1 = testMedical.setup(medicalType1, false);
		medical1.setProdCode("TP2");
		medical1.setDescription("test exam description 1");
		medical1 = medicalBrowsingManager.newMedical(medical1);

		MedicalReduction medicalReduction1 = ReductionPlanDataGenerate.generateMedicalReductionFixture(medical1, reductionPlan);

		repository.saveAndFlush(medicalReduction);
		repository.saveAndFlush(medicalReduction1);

		List<MedicalReduction> medicalReductionList = manager.getByReductionPlanId(reductionPlan.getId(), false);

		manager.deleteBulk(medicalReductionList);

		List<MedicalReduction> deletedMedicalReductionList = manager.getByReductionPlanId(reductionPlan.getId(), false);

		assertThat(deletedMedicalReductionList.size()).isEqualTo(0);
	}
}
