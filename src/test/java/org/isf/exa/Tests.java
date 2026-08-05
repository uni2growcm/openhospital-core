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
package org.isf.exa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;
import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.exa.manager.BlockBrowsingManager;
import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.manager.ExamRowBrowsingManager;
import org.isf.exa.model.Block;
import org.isf.exa.model.BlockExam;
import org.isf.exa.model.Exam;
import org.isf.exa.model.ExamRow;
import org.isf.exa.service.BlockExamIoOperationRepository;
import org.isf.exa.service.BlockIoOperationRepository;
import org.isf.exa.service.BlockIoOperations;
import org.isf.exa.service.ExamIoOperationRepository;
import org.isf.exa.service.ExamIoOperations;
import org.isf.exa.service.ExamRowIoOperationRepository;
import org.isf.exa.service.ExamRowIoOperations;
import org.isf.exatype.TestExamType;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestExam testExam;
	private static TestExamRow testExamRow;
	private static TestExamType testExamType;
	private static TestBlock testBlock;

	@Autowired
	ExamIoOperations examIoOperation;
	@Autowired
	ExamRowIoOperations examRowIoOperation;
	@Autowired
	BlockIoOperations blockIoOperation;
	@Autowired
	ExamIoOperationRepository examIoOperationRepository;
	@Autowired
	ExamRowIoOperationRepository examRowIoOperationRepository;
	@Autowired
	ExamTypeIoOperationRepository examTypeIoOperationRepository;
	@Autowired
	BlockIoOperationRepository blockIoOperationRepository;
	@Autowired
	BlockExamIoOperationRepository blockExamIoOperationRepository;
	@Autowired
	ExamBrowsingManager examBrowsingManager;
	@Autowired
	ExamRowBrowsingManager examRowBrowsingManager;
	@Autowired
	BlockBrowsingManager blockBrowsingManager;

	@BeforeAll
	static void setUpClass() {
		testExam = new TestExam();
		testExamType = new TestExamType();
		testExamRow = new TestExamRow();
		testBlock = new TestBlock();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testExamGets() throws Exception {
		String code = setupTestExam(false);
		checkExamIntoDb(code);
	}

	@Test
	void testExamSets() throws Exception {
		String code = setupTestExam(true);
		checkExamIntoDb(code);
	}

	@Test
	void testExamRowGets() throws Exception {
		int code = setupTestExamRow(false);
		checkExamRowIntoDb(code);
	}

	@Test
	void testExamRowSets() throws Exception {
		int code = setupTestExamRow(true);
		checkExamRowIntoDb(code);
	}

	@Test
	void testIoGetExamRowZero() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowIoOperation.getExamRow(0, null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void testIoGetExamRowNoDescription() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowIoOperation.getExamRow(foundExamRow.getCode(), null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void testIoGetExamRowWithDescription() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowIoOperation.getExamRow(foundExamRow.getCode(), foundExamRow.getDescription());
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void testIoGetExamRows() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowIoOperation.getExamRows();
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
		// deprecated method
		examRows = examRowIoOperation.getExamRows();
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void testIoGetExamsRowByDesc() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowIoOperation.getExamsRowByDesc(foundExamRow.getDescription());
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void testIoGetExams() throws Exception {
		String code = setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExam).isNotNull();
		List<Exam> exams = examIoOperation.getExams();
		assertThat(exams.get(exams.size() - 1).getDescription()).isEqualTo(foundExam.getDescription());
	}

	@Test
	void testIoGetExamTypeExam() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		List<ExamType> examTypes = examIoOperation.getExamType();
		assertThat(examTypes).contains(foundExamType);
	}

	@Test
	void testIoGetExamTypeExamRow() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		List<ExamType> examTypes = examRowIoOperation.getExamType();
		assertThat(examTypes).contains(foundExamType);
	}

	@Test
	void testIoNewExamRow() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		ExamRow examRow = testExamRow.setup(exam, true);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		ExamRow newExamRow = examIoOperation.newExamRow(examRow);
		checkExamRowIntoDb(newExamRow.getCode());
	}

	@Test
	void testIoNewExam() throws Exception {
		ExamType examType = testExamType.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		Exam exam = testExam.setup(examType, 1, false);
		Exam newExam = examIoOperation.newExam(exam);
		checkExamIntoDb(newExam.getCode());
	}

	@Test
	void testIoUpdateExam() throws Exception {
		String code = setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExam).isNotNull();
		foundExam.setDescription("Update");
		Exam result = examIoOperation.updateExam(foundExam);
		assertThat(result).isNotNull();
		Exam updateExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(updateExam).isNotNull();
		assertThat(updateExam.getDescription()).isEqualTo("Update");
	}

	@Test
	void testIoUpdateExamRow() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow examRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(examRow).isNotNull();
		examRow.setDescription("Update");
		ExamRow result = examRowIoOperation.updateExamRow(examRow);
		assertThat(result).isNotNull();
		ExamRow updateExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(updateExamRow).isNotNull();
		assertThat(updateExamRow.getDescription()).isEqualTo("Update");
	}

	@Test
	void testIoDeleteExam() throws Exception {
		String code = setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExam).isNotNull();
		examIoOperation.deleteExam(foundExam);
		assertThat(examIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void testIoDeleteExamRow() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		examIoOperation.deleteExamRow(foundExamRow);
		assertThat(examIoOperation.isRowPresent(code)).isFalse();
	}

	@Test
	void testIoIsKeyPresent() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		boolean result = examIoOperation.isKeyPresent(exam);
		assertThat(result).isTrue();
	}

	@Test
	void testIoIsKeyPresentExamRow() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow examRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(examRow).isNotNull();
		boolean result = examRowIoOperation.isKeyPresent(examRow);
		assertThat(result).isTrue();
		// fail
		examRow.setCode(-1);
		result = examRowIoOperation.isKeyPresent(examRow);
		assertThat(result).isFalse();
	}

	@Test
	void testIoIsCodePresent() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow examRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(examRow).isNotNull();
		assertThat(examRowIoOperation.isCodePresent(examRow.getCode())).isTrue();
	}

	@Test
	void testIoIsRowPresent() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow examRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(examRow).isNotNull();
		assertThat(examRowIoOperation.isRowPresent(examRow.getCode())).isTrue();
	}

	@Test
	void testIoGetExamRowByExamCode() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		ExamRow examRow = testExamRow.setup(exam, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		examRowIoOperationRepository.saveAndFlush(examRow);
		List<ExamRow> examRows = examRowIoOperation.getExamRowByExamCode(String.valueOf(exam.getCode()));
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(examRow.getDescription());
	}

	@Test
	void testMgrGetExamRowByExamCode() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		ExamRow examRow = testExamRow.setup(exam, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		examRowIoOperationRepository.saveAndFlush(examRow);
		List<ExamRow> examRows = examRowBrowsingManager.getExamRowByExamCode(String.valueOf(exam.getCode()));
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(examRow.getDescription());
	}

	@Test
	void testMgrGetExamRow() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowBrowsingManager.getExamRow(0, null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void testMgrGetExams() throws Exception {
		String code = setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExam).isNotNull();
		List<Exam> exams = examBrowsingManager.getExams();
		assertThat(exams.get(exams.size() - 1).getDescription()).isEqualTo(foundExam.getDescription());

		exams = examBrowsingManager.getExamsByTypeDescription("xxxx");
		assertThat(exams).isEmpty();

		exams = examBrowsingManager.getExamsByTypeDescription("TestDescription");
		assertThat(exams.get(exams.size() - 1).getDescription()).isEqualTo(foundExam.getDescription());

		exams = examBrowsingManager.getExamsByTypeDescription(null);
		assertThat(exams.get(exams.size() - 1).getDescription()).isEqualTo(foundExam.getDescription());

		exams = examBrowsingManager.getExams("TestDescription");
		assertThat(exams.get(exams.size() - 1).getDescription()).isEqualTo(foundExam.getDescription());
	}

	@Test
	void testMgrGetExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		List<ExamType> examTypes = examBrowsingManager.getExamType();
		assertThat(examTypes).contains(foundExamType);
	}

	@Test
	void testMgrNewExamRow() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		ExamRow examRow = testExamRow.setup(exam, true);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		ExamRow result = examRowBrowsingManager.newExamRow(examRow);
		assertThat(result).isNotNull();
		checkExamRowIntoDb(examRow.getCode());
	}

	@Test
	void testMgrNewExam() throws Exception {
		ExamType examType = testExamType.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		Exam exam = testExam.setup(examType, 1, false);
		Exam newExam = examBrowsingManager.newExam(exam);
		checkExamIntoDb(newExam.getCode());
	}

	@Test
	void testMgrUpdateExam() throws Exception {
		String code = setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExam).isNotNull();
		foundExam.setDescription("Update");
		Exam result = examBrowsingManager.updateExam(foundExam);
		assertThat(result).isNotNull();
		Exam updatedExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(updatedExam).isNotNull();
		assertThat(updatedExam.getDescription()).isEqualTo("Update");
	}

	@Test
	void testMgrDeleteExam() throws Exception {
		String code = setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExam).isNotNull();
		examBrowsingManager.deleteExam(foundExam);
		assertThat(examIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void testMgrDeleteExamRow() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		examRowBrowsingManager.deleteExamRow(foundExamRow);
		assertThat(examIoOperation.isRowPresent(code)).isFalse();
	}

	@Test
	void testMgrIsKeyPresent() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		boolean result = examBrowsingManager.isKeyPresent(exam);
		assertThat(result).isTrue();
	}

	@Test
	void testMgrGetExamRowZero() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowBrowsingManager.getExamRow(0, null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());

		examRows = examRowBrowsingManager.getExamRow();
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void testMgrGetExamRowNoDescription() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowBrowsingManager.getExamRow(foundExamRow.getCode(), null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());

		examRows = examRowBrowsingManager.getExamRow(foundExamRow.getCode());
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void testMgrGetExamRowWithDescription() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowBrowsingManager.getExamRow(foundExamRow.getCode(), foundExamRow.getDescription());
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void testMgrExamValidationUpdate() throws Exception {
		ExamType examType = new ExamType("ZZ", "TestDescription");
		Exam exam = testExam.setup(examType, 1, false);
		String code = exam.getCode();
		// code = ""
		exam.setCode("");
		assertThatThrownBy(() -> examBrowsingManager.updateExam(exam))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
                                e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
				);
		// description = ""
		exam.setCode(code);
		exam.setDescription("");
		assertThatThrownBy(() -> examBrowsingManager.updateExam(exam))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
                                e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
				);
	}

	@Test
	void testMgrExamValidationInsert() throws Exception {
		String code = setupTestExam(false);
		// code already exists
		ExamType examType = new ExamType("ZZ", "TestDescription");
		Exam exam2 = testExam.setup(examType, 1, false);
		exam2.setCode(code);
		assertThatThrownBy(() -> examBrowsingManager.newExam(exam2))
				.isInstanceOf(OHDataIntegrityViolationException.class)
				.has(
						new Condition<Throwable>(
                                e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
				);
	}

	@Test
	void testMgrExamRowValidationUpdate() throws Exception {
		ExamType examType = new ExamType("ZZ", "TestDescription");
		Exam exam = testExam.setup(examType, 2, false);
		ExamRow examRow = testExamRow.setup(exam, false);
		// description = ""
		examRow.setDescription("");
		assertThatThrownBy(() -> examRowBrowsingManager.newExamRow(examRow))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
                                e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
				);
	}

	@Test
	void testExamEqualHashToString() throws Exception {
		String code = setupTestExam(false);
		Exam exam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(exam).isNotNull();
		ExamType examType = testExamType.setup(false);
		Exam exam2 = new Exam("XXX", "TestDescription", examType, 1, "TestDefaultResult");
		assertThat(exam)
				.isEqualTo(exam)
				.isNotEqualTo(exam2)
				.isNotEqualTo("xyzzy");
		exam2.setCode(exam.getCode());
		exam2.setDescription(exam.getDescription());
		exam2.setExamtype(exam.getExamtype());
		assertThat(exam).isEqualTo(exam2);

		assertThat(exam.hashCode()).isPositive();

		assertThat(exam2).hasToString(exam.getDescription());
	}

	@Test
	void testExamRowEqualHashToString() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow examRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(examRow).isNotNull();
		ExamType examType = testExamType.setup(false);
		Exam exam2 = new Exam("XXX", "TestDescription", examType, 1, "TestDefaultResult");
		ExamRow examRow2 = new ExamRow(exam2, "NewDescription");
		assertThat(examRow)
				.isEqualTo(examRow)
				.isNotEqualTo(examRow2)
				.isNotEqualTo("xyzzy");
		examRow2.setCode(examRow.getCode());
		examRow2.setExamCode(examRow.getExamCode());
		examRow2.setDescription(examRow.getDescription());
		assertThat(examRow).isEqualTo(examRow2);

		assertThat(examRow.hashCode()).isPositive();

		assertThat(examRow).hasToString(examRow.getDescription());
	}

	@Test
	void testExamGetterSetter() throws Exception {
		String code = setupTestExam(false);
		Exam exam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(exam).isNotNull();
		exam.setLock(-99);
		assertThat(exam.getLock()).isEqualTo(-99);

		assertThat(exam.getSearchString()).isEqualTo("zztestdescription");
	}

	@Test
	void testIoExamSanitize() throws Exception {
		Method method = examIoOperation.getClass().getDeclaredMethod("sanitize", String.class);
		method.setAccessible(true);
		assertThat((String) method.invoke(examIoOperation, "abc'de'f")).isEqualTo("abc''de''f");
		assertThat((String) method.invoke(examIoOperation, (String) null)).isNull();
		assertThat((String) method.invoke(examIoOperation, "abcdef")).isEqualTo("abcdef");
	}

	@Test
	void testIoExamRowSanitize() throws Exception {
		Method method = examRowIoOperation.getClass().getDeclaredMethod("sanitize", String.class);
		method.setAccessible(true);
		assertThat((String) method.invoke(examRowIoOperation, "abc'de'f")).isEqualTo("abc''de''f");
		assertThat((String) method.invoke(examRowIoOperation, (String) null)).isNull();
		assertThat((String) method.invoke(examRowIoOperation, "abcdef")).isEqualTo("abcdef");
	}

	@Test
	void testBlockGets() throws Exception {
		String code = setupTestBlock(false);
		checkBlockIntoDb(code);
	}

	@Test
	void testBlockSets() throws Exception {
		String code = setupTestBlock(true);
		checkBlockIntoDb(code);
	}

	@Test
	void testBlockExamGets() throws Exception {
		String code = setupTestBlockExam(false);
		Block block = blockIoOperationRepository.findById(code).orElse(null);
		assertThat(block).isNotNull();
		List<BlockExam> blockExams = blockExamIoOperationRepository.findByBlock_Code(code);
		assertThat(blockExams).hasSize(1);
		testBlock.checkBlockExam(blockExams.get(0), block, blockExams.get(0).getExam());
	}

	@Test
	void testBlockExamSets() throws Exception {
		String code = setupTestBlockExam(true);
		Block block = blockIoOperationRepository.findById(code).orElse(null);
		assertThat(block).isNotNull();
		List<BlockExam> blockExams = blockExamIoOperationRepository.findByBlock_Code(code);
		assertThat(blockExams).hasSize(1);
		testBlock.checkBlockExam(blockExams.get(0), block, blockExams.get(0).getExam());
	}

	@Test
	void testIoGetBlocks() throws Exception {
		String code = setupTestBlock(false);
		Block foundBlock = blockIoOperationRepository.findById(code).orElse(null);
		assertThat(foundBlock).isNotNull();
		List<Block> blocks = blockIoOperation.getBlocks();
		assertThat(blocks).contains(foundBlock);
	}

	@Test
	void testIoGetBlocksWithDescription() throws Exception {
		String code = setupTestBlock(false);
		Block foundBlock = blockIoOperationRepository.findById(code).orElse(null);
		assertThat(foundBlock).isNotNull();
		// matching description
		List<Block> blocks = blockIoOperation.getBlocks("TestDescription");
		assertThat(blocks).contains(foundBlock);
		// no match
		blocks = blockIoOperation.getBlocks("xxxx");
		assertThat(blocks).isEmpty();
		// blank / null means all
		blocks = blockIoOperation.getBlocks("");
		assertThat(blocks).contains(foundBlock);
		blocks = blockIoOperation.getBlocks(null);
		assertThat(blocks).contains(foundBlock);
	}

	@Test
	void testIoGetBlock() throws Exception {
		String code = setupTestBlock(false);
		Block foundBlock = blockIoOperation.getBlock(code);
		assertThat(foundBlock).isNotNull();
		testBlock.check(foundBlock);
		// unknown code
		assertThat(blockIoOperation.getBlock("UNKNOWN")).isNull();
	}

	@Test
	void testIoNewBlock() throws Exception {
		Block block = testBlock.setup(false);
		Block newBlock = blockIoOperation.newBlock(block);
		checkBlockIntoDb(newBlock.getCode());
	}

	@Test
	void testIoUpdateBlock() throws Exception {
		String code = setupTestBlock(false);
		Block foundBlock = blockIoOperationRepository.findById(code).orElse(null);
		assertThat(foundBlock).isNotNull();
		foundBlock.setDescription("Update");
		Block result = blockIoOperation.updateBlock(foundBlock);
		assertThat(result).isNotNull();
		Block updatedBlock = blockIoOperationRepository.findById(code).orElse(null);
		assertThat(updatedBlock).isNotNull();
		assertThat(updatedBlock.getDescription()).isEqualTo("Update");
	}

	@Test
	void testIoDeleteBlock() throws Exception {
		String code = setupTestBlockExam(false);
		blockIoOperation.deleteBlock(code);
		assertThat(blockIoOperation.isCodePresent(code)).isFalse();
		assertThat(blockExamIoOperationRepository.findByBlock_Code(code)).isEmpty();
	}

	@Test
	void testIoGetExamWithBlock() throws Exception {
		String code = setupTestBlockExam(false);
		List<Exam> exams = blockIoOperation.getExamWithBlock(code);
		assertThat(exams).hasSize(1);
		assertThat(exams.get(0).getDescription()).isEqualTo("TestDescription");
		// unknown block
		assertThat(blockIoOperation.getExamWithBlock("UNKNOWN")).isEmpty();
	}

	@Test
	void testIoSaveExamBlocks() throws Exception {
		String code = setupTestBlock(false);
		Block block = blockIoOperationRepository.findById(code).orElse(null);
		assertThat(block).isNotNull();
		assertThat(blockExamIoOperationRepository.findByBlock_Code(code)).isEmpty();
		// associate two exams
		Exam exam1 = saveExam();
		Exam exam2 = saveExam();
		blockIoOperation.saveExamBlocks(block, List.of(exam1, exam2));
		assertThat(blockIoOperation.getExamWithBlock(code)).hasSize(2);
		// replace with only exam1
		blockIoOperation.saveExamBlocks(block, List.of(exam1));
		assertThat(blockIoOperation.getExamWithBlock(code)).hasSize(1);
		// null and empty clear the associations
		blockIoOperation.saveExamBlocks(block, null);
		assertThat(blockIoOperation.getExamWithBlock(code)).isEmpty();
	}

	@Test
	void testIoGetExamByDescription() throws Exception {
		saveExam();
		Exam foundExam = blockIoOperation.getExamByDescription("TestDescription");
		assertThat(foundExam).isNotNull();
		assertThat(foundExam.getDescription()).isEqualTo("TestDescription");
		// unknown
		assertThat(blockIoOperation.getExamByDescription("xxxx")).isNull();
	}

	@Test
	void testIoIsBlockCodePresent() throws Exception {
		String code = setupTestBlock(false);
		assertThat(blockIoOperation.isCodePresent(code)).isTrue();
		assertThat(blockIoOperation.isCodePresent("UNKNOWN")).isFalse();
	}

	@Test
	void testMgrGetBlocks() throws Exception {
		String code = setupTestBlock(false);
		Block foundBlock = blockIoOperationRepository.findById(code).orElse(null);
		assertThat(foundBlock).isNotNull();
		List<Block> blocks = blockBrowsingManager.getBlocks();
		assertThat(blocks).contains(foundBlock);
	}

	@Test
	void testMgrGetBlocksWithDescription() throws Exception {
		String code = setupTestBlock(false);
		Block foundBlock = blockIoOperationRepository.findById(code).orElse(null);
		assertThat(foundBlock).isNotNull();
		List<Block> blocks = blockBrowsingManager.getBlocks("TestDescription");
		assertThat(blocks).contains(foundBlock);
		blocks = blockBrowsingManager.getBlocks("xxxx");
		assertThat(blocks).isEmpty();
	}

	@Test
	void testMgrGetBlock() throws Exception {
		String code = setupTestBlock(false);
		Block foundBlock = blockBrowsingManager.getBlock(code);
		assertThat(foundBlock).isNotNull();
		testBlock.check(foundBlock);
		assertThat(blockBrowsingManager.getBlock("UNKNOWN")).isNull();
	}

	@Test
	void testMgrNewBlock() throws Exception {
		Block block = testBlock.setup(false);
		Block newBlock = blockBrowsingManager.newBlock(block);
		checkBlockIntoDb(newBlock.getCode());
	}

	@Test
	void testMgrUpdateBlock() throws Exception {
		String code = setupTestBlock(false);
		Block foundBlock = blockIoOperationRepository.findById(code).orElse(null);
		assertThat(foundBlock).isNotNull();
		foundBlock.setDescription("Update");
		Block result = blockBrowsingManager.updateBlock(foundBlock);
		assertThat(result).isNotNull();
		Block updatedBlock = blockIoOperationRepository.findById(code).orElse(null);
		assertThat(updatedBlock).isNotNull();
		assertThat(updatedBlock.getDescription()).isEqualTo("Update");
	}

	@Test
	void testMgrDeleteBlock() throws Exception {
		String code = setupTestBlockExam(false);
		blockBrowsingManager.deleteBlock(code);
		assertThat(blockBrowsingManager.isCodePresent(code)).isFalse();
		assertThat(blockExamIoOperationRepository.findByBlock_Code(code)).isEmpty();
	}

	@Test
	void testMgrGetExamWithBlock() throws Exception {
		String code = setupTestBlockExam(false);
		List<Exam> exams = blockBrowsingManager.getExamWithBlock(code);
		assertThat(exams).hasSize(1);
		assertThat(exams.get(0).getDescription()).isEqualTo("TestDescription");
	}

	@Test
	void testMgrSaveExamBlocks() throws Exception {
		String code = setupTestBlock(false);
		Block block = blockIoOperationRepository.findById(code).orElse(null);
		assertThat(block).isNotNull();
		Exam exam = saveExam();
		blockBrowsingManager.saveExamBlocks(block, List.of(exam));
		assertThat(blockBrowsingManager.getExamWithBlock(code)).hasSize(1);
	}

	@Test
	void testMgrGetExamByDescription() throws Exception {
		saveExam();
		Exam exam = blockBrowsingManager.getExamByDescription("TestDescription");
		assertThat(exam).isNotNull();
		assertThat(exam.getDescription()).isEqualTo("TestDescription");
	}

	@Test
	void testMgrIsBlockCodePresent() throws Exception {
		String code = setupTestBlock(false);
		assertThat(blockBrowsingManager.isCodePresent(code)).isTrue();
		assertThat(blockBrowsingManager.isCodePresent("UNKNOWN")).isFalse();
	}

	@Test
	void testMgrBlockValidation() throws Exception {
		String code = setupTestBlock(false);
		// duplicate on insert
		Block blockDup = testBlock.setup(false);
		blockDup.setCode(code);
		assertThatThrownBy(() -> blockBrowsingManager.newBlock(blockDup))
				.isInstanceOf(OHDataIntegrityViolationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		// empty code
		Block block = new Block("", "TestDescription");
		assertThatThrownBy(() -> blockBrowsingManager.newBlock(block))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		// empty description
		Block blockNoDesc = new Block("NEWCODE", "");
		assertThatThrownBy(() -> blockBrowsingManager.newBlock(blockNoDesc))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	void testBlockEqualHashToString() throws Exception {
		String code = setupTestBlock(false);
		Block block = blockIoOperationRepository.findById(code).orElse(null);
		assertThat(block).isNotNull();
		Block block2 = new Block("XXX", "TestDescription");
		assertThat(block)
				.isEqualTo(block)
				.isNotEqualTo(block2)
				.isNotEqualTo("xyzzy");
		block2.setCode(block.getCode());
		block2.setDescription(block.getDescription());
		assertThat(block).isEqualTo(block2);

		assertThat(block.hashCode()).isPositive();
		assertThat(block).hasToString(block.getDescription());
	}

	@Test
	void testBlockExamEqualHash() throws Exception {
		String code = setupTestBlockExam(false);
		Block block = blockIoOperationRepository.findById(code).orElse(null);
		assertThat(block).isNotNull();
		BlockExam blockExam = blockExamIoOperationRepository.findByBlock_Code(code).get(0);
		Exam exam = blockExam.getExam();
		assertThat(exam).isNotNull();
		BlockExam blockExam2 = new BlockExam(new Block("XXX", "TestDescription"), exam);
		assertThat(blockExam)
				.isEqualTo(blockExam)
				.isNotEqualTo(blockExam2)
				.isNotEqualTo("xyzzy");
		blockExam2.setBlock(block);
		blockExam2.setId(blockExam.getId());
		assertThat(blockExam).isEqualTo(blockExam2);
		assertThat(blockExam.hashCode()).isPositive();
	}

	@Test
	void testBlockGetterSetter() throws Exception {
		Block block = new Block("BLK", "TestDescription");
		block.setCode("NEWCODE");
		assertThat(block.getCode()).isEqualTo("NEWCODE");
		assertThat(block.getActive()).isEqualTo(1);
		assertThat(block.getSearchString()).isEqualTo("newcodetestdescription");
	}

	private int saveExamCounter;

	private String setupTestBlock(boolean usingSet) throws OHException {
		Block block = testBlock.setup(usingSet);
		blockIoOperationRepository.saveAndFlush(block);
		return block.getCode();
	}

	private void checkBlockIntoDb(String code) throws OHException {
		Block foundBlock = blockIoOperationRepository.findById(code).orElse(null);
		assertThat(foundBlock).isNotNull();
		testBlock.check(foundBlock);
	}

	private String setupTestBlockExam(boolean usingSet) throws OHException {
		String code = setupTestBlock(usingSet);
		Block block = blockIoOperationRepository.findById(code).orElse(null);
		assertThat(block).isNotNull();
		Exam exam = saveExam();
		BlockExam blockExam = testBlock.setupBlockExam(block, exam);
		blockExamIoOperationRepository.saveAndFlush(blockExam);
		return code;
	}

	private Exam saveExam() throws OHException {
		String suffix = saveExamCounter < 10 ? "0" + saveExamCounter : String.valueOf(saveExamCounter);
		saveExamCounter++;
		ExamType examType = new ExamType("Z" + suffix, "TestDescription");
		examTypeIoOperationRepository.saveAndFlush(examType);
		Exam exam = testExam.setup(examType, 1, false);
		exam.setCode("ZZ" + suffix);
		examIoOperationRepository.saveAndFlush(exam);
		return exam;
	}

	private String setupTestExam(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, usingSet);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		return exam.getCode();
	}

	private void checkExamIntoDb(String code) throws OHException {
		Exam foundExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExam).isNotNull();
		testExam.check(foundExam);
	}

	private int setupTestExamRow(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(usingSet);
		Exam exam = testExam.setup(examType, 2, usingSet);
		ExamRow examRow = testExamRow.setup(exam, usingSet);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		examRowIoOperationRepository.saveAndFlush(examRow);
		return examRow.getCode();
	}

	private void checkExamRowIntoDb(int code) throws OHException {
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		testExamRow.check(foundExamRow);
	}

	private String setupTestExamType(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		return examType.getCode();
	}
}