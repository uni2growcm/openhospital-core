package org.isf.reductionplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.reductionplan.manager.ReductionplanBrowserManager;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.reductionplan.service.ReductionPlanIoOperations;
import org.isf.reductionplan.service.ReductionplanIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestsReductionplan testsReductionplan;

	@Autowired
	ReductionPlanIoOperations ioOperations;

	@Autowired
	ReductionplanIoOperationRepository repository;

	@Autowired
	ReductionplanBrowserManager manager;

	@BeforeAll
	static void setUpClass() {
		testsReductionplan = new TestsReductionplan();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testReductionplanGets() throws Exception {
		// given:
		int id = setupTestReductionplan(false);

		// then:
		checkReductionplanIntoDb(id);
	}

	@Test
	void testReductionplanSets() throws Exception {
		// given:
		int id = setupTestReductionplan(true);

		// then:
		checkReductionplanIntoDb(id);
	}

	@Test
	void testIoReductionplanGetAll() throws Exception {
		// given:
		ReductionPlan reductionplan = testsReductionplan.setup(false);
		repository.save(reductionplan);
		List<ReductionPlan> foundReductionPlan = ioOperations.getReductionplan();
		assertThat(foundReductionPlan).isNotNull();
		assertThat(foundReductionPlan.size()).isGreaterThan(0);
		assertThat(foundReductionPlan.size()).isEqualTo(1);
	}

	@Test
	void testFindById() throws Exception {
		// GIVEN: Sauvegarder plusieurs Reductionplan
		ReductionPlan reduction1 = new ReductionPlan("Plan 1", 10, 20, 30, 40);
		ReductionPlan reduction2 = new ReductionPlan("Plan 2", 15, 25, 35, 45);
		repository.save(reduction1);
		repository.save(reduction2);

		// Récupérer les IDs générés
		List<Integer> ids = List.of(reduction1.getId(), reduction2.getId());

		// WHEN: Appeler la méthode findById
		List<ReductionPlan> result = manager.getReductionplanByIds(ids);

		// THEN: Vérifier que les résultats sont corrects
		assertThat(result).isNotNull();
		assertThat(result.size()).isEqualTo(2); // Les deux objets doivent être récupérés
		assertThat(result).extracting(ReductionPlan::getDescription)
						.containsExactlyInAnyOrder("Plan 1", "Plan 2");
	}






	@Test
void testMgrGetReductionplanByDescription() throws Exception {
	// given: Initialiser et sauvegarder un Reductionplan
	ReductionPlan reductionplan = testsReductionplan.setup(false); // Utilise les setters
	repository.save(reductionplan); // Sauvegarder l'entité dans la base de données

	// when: Utiliser la méthode manager pour rechercher par description
	List<ReductionPlan> result = manager.getReductionplan(reductionplan.getDescription());

	// then: Vérifier que le Reductionplan attendu est dans les résultats
	assertThat(result).isNotEmpty();
	assertThat(result.get(0).getDescription()).isEqualTo(reductionplan.getDescription());
}




	@Test
	void testMgrUpdateReductionplan() throws Exception {
		int id = setupTestReductionplan(false);
		ReductionPlan foundReductionPlan = repository.findById(id).orElse(null);
		assertThat(foundReductionPlan).isNotNull();
		foundReductionPlan.setDescription("Updated Manager Description");
		ReductionPlan updatedReductionPlan = manager.updateReductionplan(foundReductionPlan);
		assertThat(updatedReductionPlan.getDescription()).isEqualTo("Updated Manager Description");
	}

	@Test
	void testMgrNewReductionplan() throws Exception {
		// given:
		ReductionPlan reductionplan = testsReductionplan.setup(true);

		// when:
		ReductionPlan newReductionPlan = manager.newReductionplan(reductionplan);

		// then:
		assertThat(newReductionPlan.getId()).isGreaterThan(0); // Vérifier que l'ID est généré
		checkReductionplanIntoDb(newReductionPlan.getId());
	}

	@Test
	void testMgrDeleteReductionplan() throws Exception {
		// given:
		int id = setupTestReductionplan(false);
		ReductionPlan foundReductionPlan = repository.findById(id).orElse(null);
		assertThat(foundReductionPlan).isNotNull();

		// when:
		manager.deleteReductionplan(foundReductionPlan);

		// then:
		assertThat(repository.existsById(id)).isFalse();
	}

	@Test
	void testMgrValidationCodeEmpty() throws Exception {
		assertThatThrownBy(() -> {
			// Test sans définir un rpId, l'ID sera généré automatiquement
			ReductionPlan reductionplan = testsReductionplan.setup(true);
			reductionplan.setDescription("");  // Validation du champ description vide
			manager.newReductionplan(reductionplan);
		}).isInstanceOf(OHDataValidationException.class)
						.has(new Condition<>(e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@Test
	void testMgrValidationDescriptionEmpty() throws Exception {
		assertThatThrownBy(() -> {
			ReductionPlan reductionplan = testsReductionplan.setup(true);
			reductionplan.setDescription("");  // Validation de la description vide
			manager.newReductionplan(reductionplan);
		}).isInstanceOf(OHDataValidationException.class)
						.has(new Condition<>(e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@Test
	void setTestsReductionplanToString() throws Exception {
		ReductionPlan reductionplan = new ReductionPlan(1, "TestDescription", 0.00f, 0.1f, 0.02f, 1.3f);
		assertThat(reductionplan).hasToString("TestDescription");
	}

	@Test
	void testReductionplanEquals() throws Exception {
		ReductionPlan reductionplan = new ReductionPlan(2, "TestDescription", 0.0f, 0.05f, 0.06f, 0.07f);

		assertThat(reductionplan)
						.isEqualTo(reductionplan)
						.isNotNull()
						.isNotEqualTo("someString");

		ReductionPlan reductionPlan1 = new ReductionPlan(3, "TestDescription", 0.0f, 0.05f, 0.06f, 0.07f);
		assertThat(reductionplan).isNotEqualTo(reductionPlan1);

		reductionPlan1.setId(reductionplan.getId());
		assertThat(reductionplan).isEqualTo(reductionPlan1);
	}

	@Test
	void testReductionplanHashCode() throws Exception {
		ReductionPlan reductionplan = testsReductionplan.setup(true);
		reductionplan.setId(1);
		int hashCode = reductionplan.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + 1); // check computed value
		assertThat(reductionplan.hashCode()).isEqualTo(hashCode);
	}

	private int setupTestReductionplan(boolean usingSet) throws Exception {
		ReductionPlan reductionplan = testsReductionplan.setup(usingSet);
		repository.saveAndFlush(reductionplan);
		return reductionplan.getId();
	}

	private void checkReductionplanIntoDb(int id) throws OHServiceException {
		ReductionPlan foundReductionPlan = repository.findById(id).orElse(null);
		assertThat(foundReductionPlan).isNotNull();
		testsReductionplan.check(foundReductionPlan);
	}
}
