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
package org.isf.command;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.command.manager.CommandBrowserManager;
import org.isf.command.model.Command;
import org.isf.command.model.CommandRow;
import org.isf.command.service.CommandIoOperationRepository;
import org.isf.command.service.CommandIoOperations;
import org.isf.command.service.CommandRowIoOperationRepository;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicalstock.TestLot;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.supplier.TestSupplier;
import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.SupplierIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestCommand testCommand;
	private static TestCommandRow testCommandRow;
	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestLot testLot;
	private static TestSupplier testSupplier;

	@Autowired
	CommandIoOperations commandIoOperations;
	@Autowired
	CommandIoOperationRepository commandIoOperationRepository;
	@Autowired
	CommandRowIoOperationRepository commandRowIoOperationRepository;
	@Autowired
	CommandBrowserManager commandBrowserManager;
	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;
	@Autowired
	LotIoOperationRepository lotIoOperationRepository;
	@Autowired
	SupplierIoOperationRepository supplierIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testCommand = new TestCommand();
		testCommandRow = new TestCommandRow();
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
		testLot = new TestLot();
		testSupplier = new TestSupplier();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testCommandGets() throws Exception {
		int code = setupTestCommand(false);
		checkCommandIntoDb(code);
	}

	@Test
	void testCommandSets() throws Exception {
		int code = setupTestCommand(true);
		checkCommandIntoDb(code);
	}

	@Test
	void testIoCommandSaveOrUpdate() throws Exception {
		Command command = testCommand.setup(true);
		Command newCommand = commandIoOperations.saveOrUpdate(command);
		checkCommandIntoDb(newCommand.getId());
	}

	@Test
	void testIoCommandGetByID() throws Exception {
		int code = setupTestCommand(false);
		Command foundCommand = commandIoOperations.getByID(code);
		assertThat(foundCommand).isNotNull();
		checkCommandIntoDb(foundCommand.getId());
	}

	@Test
	void testIoCommandGetAll() throws Exception {
		setupTestCommand(false);
		List<Command> commands = commandIoOperations.getAll();
		assertThat(commands).hasSize(1);
	}

	@Test
	void testIoCommandGetList() throws Exception {
		setupTestCommand(false);
		List<Command> commands = commandIoOperations.getList();
		assertThat(commands).hasSize(1);
	}

	@Test
	void testIoCommandGetAllOrderByDateDesc() throws Exception {
		setupTestCommand(false);
		List<Command> commands = commandIoOperations.getAllOrderByDateDesc();
		assertThat(commands).hasSize(1);
	}

	@Test
	void testMgrCommandSaveOrUpdate() throws Exception {
		Command command = testCommand.setup(true);
		Command newCommand = commandBrowserManager.saveOrUpdate(command);
		checkCommandIntoDb(newCommand.getId());
	}

	@Test
	void testMgrCommandGetByID() throws Exception {
		int code = setupTestCommand(false);
		Command foundCommand = commandBrowserManager.getByID(code);
		assertThat(foundCommand).isNotNull();
		checkCommandIntoDb(foundCommand.getId());
	}

	@Test
	void testMgrCommandGetAll() throws Exception {
		setupTestCommand(false);
		List<Command> commands = commandBrowserManager.getAll();
		assertThat(commands).hasSize(1);
	}

	@Test
	void testMgrCommandGetList() throws Exception {
		setupTestCommand(false);
		List<Command> commands = commandBrowserManager.getList();
		assertThat(commands).hasSize(1);
	}

	@Test
	void testMgrCommandGetAllOrderByDateDesc() throws Exception {
		setupTestCommand(false);
		List<Command> commands = commandBrowserManager.getAllOrderByDateDesc();
		assertThat(commands).hasSize(1);
	}

	@Test
	void testCommandToString() throws Exception {
		Command command = testCommand.setup(false);
		assertThat(command).hasToString("TestRefNo");
	}

	@Test
	void testCommandEquals() throws Exception {
		Command command = new Command(1, "TestRefNo", LocalDateTime.of(2024, 1, 1, 0, 0, 0));
		assertThat(command)
				.isEqualTo(command)
				.isNotNull()
				.isNotEqualTo("someString");
		Command command2 = new Command(2, "TestRefNo2", LocalDateTime.of(2024, 1, 1, 0, 0, 0));
		assertThat(command).isNotEqualTo(command2);
		command2.setId(command.getId());
		assertThat(command).isEqualTo(command2);
	}

	@Test
	void testCommandHashCode() throws Exception {
		int code = setupTestCommand(false);
		Command command = commandBrowserManager.getByID(code);
		assertThat(command).isNotNull();
		int hashCode = command.hashCode();
		assertThat(command.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void testCommandRowGets() throws Exception {
		int id = setupTestCommandRow(false);
		checkCommandRowIntoDb(id);
	}

	@Test
	void testCommandRowSets() throws Exception {
		int id = setupTestCommandRow(true);
		checkCommandRowIntoDb(id);
	}

	@Test
	void testIoCommandRowSaveOrUpdate() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Lot lot = testLot.setup(medical, false);
		Supplier supplier = testSupplier.setup(false);
		Command command = testCommand.setup(true);
		CommandRow commandRow = testCommandRow.setup(command, medical, lot, supplier, true);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		lotIoOperationRepository.saveAndFlush(lot);
		supplierIoOperationRepository.saveAndFlush(supplier);
		commandIoOperationRepository.saveAndFlush(command);
		CommandRow newCommandRow = commandIoOperations.saveOrUpdateRow(commandRow);
		checkCommandRowIntoDb(newCommandRow.getId());
	}

	@Test
	void testIoCommandRowGetByCommand() throws Exception {
		int id = setupTestCommandRow(false);
		CommandRow commandRow = commandRowIoOperationRepository.findById(id).orElse(null);
		assertThat(commandRow).isNotNull();
		List<CommandRow> rows = commandIoOperations.getRowsByCommand(commandRow.getCommand());
		assertThat(rows).hasSize(1);
	}

	@Test
	void testIoCommandRowGetActiveByCommand() throws Exception {
		int id = setupTestCommandRow(false);
		CommandRow commandRow = commandRowIoOperationRepository.findById(id).orElse(null);
		assertThat(commandRow).isNotNull();
		List<CommandRow> rows = commandIoOperations.getActiveRowsByCommand(commandRow.getCommand());
		assertThat(rows).hasSize(1);
	}

	@Test
	void testMgrCommandRowSaveOrUpdate() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Lot lot = testLot.setup(medical, false);
		Supplier supplier = testSupplier.setup(false);
		Command command = testCommand.setup(true);
		CommandRow commandRow = testCommandRow.setup(command, medical, lot, supplier, true);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		lotIoOperationRepository.saveAndFlush(lot);
		supplierIoOperationRepository.saveAndFlush(supplier);
		commandIoOperationRepository.saveAndFlush(command);
		CommandRow newCommandRow = commandBrowserManager.saveOrUpdateRow(commandRow);
		checkCommandRowIntoDb(newCommandRow.getId());
	}

	@Test
	void testMgrCommandRowGetByCommand() throws Exception {
		int id = setupTestCommandRow(false);
		CommandRow commandRow = commandRowIoOperationRepository.findById(id).orElse(null);
		assertThat(commandRow).isNotNull();
		List<CommandRow> rows = commandBrowserManager.getRowsByCommand(commandRow.getCommand());
		assertThat(rows).hasSize(1);
	}

	@Test
	void testMgrCommandRowGetActiveByCommand() throws Exception {
		int id = setupTestCommandRow(false);
		CommandRow commandRow = commandRowIoOperationRepository.findById(id).orElse(null);
		assertThat(commandRow).isNotNull();
		List<CommandRow> rows = commandBrowserManager.getActiveRowsByCommand(commandRow.getCommand());
		assertThat(rows).hasSize(1);
	}

	@Test
	void testCommandRowToString() throws Exception {
		Command command = testCommand.setup(false);
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		CommandRow commandRow = testCommandRow.setup(command, medical, null, null, false);
		assertThat(commandRow).hasToString("TestMedicalDesc");
	}

	@Test
	void testCommandRowEquals() throws Exception {
		CommandRow commandRow = new CommandRow();
		commandRow.setId(1);
		assertThat(commandRow)
				.isEqualTo(commandRow)
				.isNotNull()
				.isNotEqualTo("someString");
		CommandRow commandRow2 = new CommandRow();
		commandRow2.setId(2);
		assertThat(commandRow).isNotEqualTo(commandRow2);
		commandRow2.setId(commandRow.getId());
		assertThat(commandRow).isEqualTo(commandRow2);
	}

	@Test
	void testCommandRowHashCode() throws Exception {
		int id = setupTestCommandRow(false);
		CommandRow commandRow = commandRowIoOperationRepository.findById(id).orElse(null);
		assertThat(commandRow).isNotNull();
		int hashCode = commandRow.hashCode();
		assertThat(commandRow.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void testCommandDeletion() throws Exception {
		int code = setupTestCommand(false);
		Command command = commandBrowserManager.getByID(code);
		assertThat(command).isNotNull();
		commandBrowserManager.delete(command);
		assertThat(commandBrowserManager.getByID(code)).isNull();
	}

	@Test
	void testCommandRowDeletion() throws Exception {
		int id = setupTestCommandRow(false);
		CommandRow commandRow = commandRowIoOperationRepository.findById(id).orElse(null);
		assertThat(commandRow).isNotNull();
		commandIoOperations.deleteRow(commandRow);
		assertThat(commandRowIoOperationRepository.findById(id)).isEmpty();
	}

	@Test
	void testCommandRowWithNullLotAndSupplier() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Command command = testCommand.setup(false);
		CommandRow commandRow = testCommandRow.setup(command, medical, null, null, false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		commandIoOperationRepository.saveAndFlush(command);
		commandRowIoOperationRepository.saveAndFlush(commandRow);

		CommandRow foundRow = commandRowIoOperationRepository.findById(commandRow.getId()).orElse(null);
		assertThat(foundRow).isNotNull();
		assertThat(foundRow.getLot()).isNull();
		assertThat(foundRow.getSupplier()).isNull();
	}

	private int setupTestCommand(boolean usingSet) throws OHException {
		Command command = testCommand.setup(usingSet);
		commandIoOperationRepository.saveAndFlush(command);
		return command.getId();
	}

	private void checkCommandIntoDb(int code) throws OHServiceException {
		Command foundCommand = commandIoOperations.getByID(code);
		assertThat(foundCommand).isNotNull();
		testCommand.check(foundCommand);
	}

	private int setupTestCommandRow(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Lot lot = testLot.setup(medical, false);
		Supplier supplier = testSupplier.setup(false);
		Command command = testCommand.setup(false);
		CommandRow commandRow = testCommandRow.setup(command, medical, lot, supplier, usingSet);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		lotIoOperationRepository.saveAndFlush(lot);
		supplierIoOperationRepository.saveAndFlush(supplier);
		commandIoOperationRepository.saveAndFlush(command);
		commandRowIoOperationRepository.saveAndFlush(commandRow);
		return commandRow.getId();
	}

	private void checkCommandRowIntoDb(int id) {
		CommandRow foundCommandRow = commandRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundCommandRow).isNotNull();
		testCommandRow.check(foundCommandRow);
	}
}
