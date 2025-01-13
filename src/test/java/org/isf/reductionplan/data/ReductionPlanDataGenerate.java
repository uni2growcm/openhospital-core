package org.isf.reductionplan.data;

import java.util.List;
import java.util.stream.IntStream;

import org.isf.exa.TestExam;
import org.isf.exa.model.Exam;
import org.isf.exatype.TestExamType;
import org.isf.exatype.model.ExamType;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medtype.TestMedicalType;
import org.isf.operation.TestOperation;
import org.isf.operation.model.Operation;
import org.isf.opetype.TestOperationType;
import org.isf.opetype.model.OperationType;
import org.isf.pricesothers.TestPricesOthers;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.reductionplan.model.ExamReduction;
import org.isf.reductionplan.model.MedicalReduction;
import org.isf.reductionplan.model.OperationReduction;
import org.isf.reductionplan.model.PriceOtherReduction;
import org.isf.reductionplan.model.ReductionPlan;
import org.isf.utils.exception.OHException;

public class ReductionPlanDataGenerate {

	public static List<ReductionPlan> generateReductionPlanFixtures(int number, String fixedDescription) {
		return IntStream.range(0, number).mapToObj(i -> new ReductionPlan(
			fixedDescription != null ? fixedDescription : "Description " + i,
			1.0 + i,
			2.0 + i,
			3.0 + i,
			3.0 + i

		)).toList();
	}

	public static ExamReduction generateExamReductionFixture(Exam exam, ReductionPlan reductionPlan) throws OHException {
		if (exam == null) {
			TestExam testExam = new TestExam();
			TestExamType testExamType = new TestExamType();
			exam = testExam.setup(testExamType.setup(false), 1, false);
		}
		return new ExamReduction(reductionPlan != null ? reductionPlan : generateReductionPlanFixtures(1, null).get(0)
			, exam, 1.0);
	}

	public static OperationReduction generateOperationReductionFixture(Operation operation, ReductionPlan reductionPlan) throws OHException {
		if (operation == null) {
			TestOperation testOperation = new TestOperation();
			TestOperationType testOperationType = new TestOperationType();
			operation = testOperation.setup(testOperationType.setup(false), false);
		}
		return new OperationReduction(reductionPlan != null ? reductionPlan : generateReductionPlanFixtures(1, null).get(0)
			, operation, 1.0);
	}

	public static MedicalReduction generateMedicalReductionFixture(Medical medical, ReductionPlan reductionPlan) throws OHException {
		if (medical == null) {
			TestMedical testMedical = new TestMedical();
			TestMedicalType testMedicalType = new TestMedicalType();
			medical = testMedical.setup(testMedicalType.setup(false), false);
		}
		return new MedicalReduction(reductionPlan != null ? reductionPlan : generateReductionPlanFixtures(1, null).get(0)
		, medical, 1.0);
	}

	public static PriceOtherReduction generateOperationReductionFixture(PricesOthers pricesOthers, ReductionPlan reductionPlan) throws OHException {
		if (pricesOthers == null) {
			TestPricesOthers testPricesOthers = new TestPricesOthers();
			pricesOthers = testPricesOthers.setup(false);
		}
		return new PriceOtherReduction(reductionPlan != null ? reductionPlan : generateReductionPlanFixtures(1, null).get(0)
		, pricesOthers, 1.0);
	}
}
