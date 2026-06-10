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
package org.isf.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import org.isf.command.model.Command;
import org.isf.command.model.CommandRow;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.isf.supplier.model.Supplier;
import org.isf.utils.exception.OHException;

public class TestCommandRow {

	private String medicalCode = "M123";
	private String medicalDescription = "TestMedicalDesc";
	private double qtyInStore = 50.0;
	private double criticalLevel = 10.0;
	private Double stillQty = 40.0;
	private Double orderQty = 20.0;
	private double userAddedQty = 5.0;

	public CommandRow setup(Command command, Medical medical, Lot lot, Supplier supplier, boolean usingSet) throws OHException {
		CommandRow commandRow;

		if (usingSet) {
			commandRow = new CommandRow();
			setParameters(commandRow, command, medical, lot, supplier);
		} else {
			commandRow = new CommandRow();
			commandRow.setCommand(command);
			commandRow.setMedical(medical);
			commandRow.setMedicalCode(medicalCode);
			commandRow.setMedicalDescription(medicalDescription);
			commandRow.setLot(lot);
			commandRow.setSupplier(supplier);
			commandRow.setQtyInStore(qtyInStore);
			commandRow.setCriticalLevel(criticalLevel);
			commandRow.setStillQty(stillQty);
			commandRow.setOrderQty(orderQty);
			commandRow.setUserAddedQty(userAddedQty);
		}

		return commandRow;
	}

	public void setParameters(CommandRow commandRow, Command command, Medical medical, Lot lot, Supplier supplier) {
		commandRow.setCommand(command);
		commandRow.setMedical(medical);
		commandRow.setMedicalCode(medicalCode);
		commandRow.setMedicalDescription(medicalDescription);
		commandRow.setLot(lot);
		commandRow.setSupplier(supplier);
		commandRow.setQtyInStore(qtyInStore);
		commandRow.setCriticalLevel(criticalLevel);
		commandRow.setStillQty(stillQty);
		commandRow.setOrderQty(orderQty);
		commandRow.setUserAddedQty(userAddedQty);
	}

	public void check(CommandRow commandRow) {
		assertThat(commandRow.getMedicalCode()).isEqualTo(medicalCode);
		assertThat(commandRow.getMedicalDescription()).isEqualTo(medicalDescription);
		assertThat(commandRow.getQtyInStore()).isCloseTo(qtyInStore, offset(0.1));
		assertThat(commandRow.getCriticalLevel()).isCloseTo(criticalLevel, offset(0.1));
		assertThat(commandRow.getStillQty()).isCloseTo(stillQty, offset(0.1));
		assertThat(commandRow.getOrderQty()).isCloseTo(orderQty, offset(0.1));
		assertThat(commandRow.getUserAddedQty()).isCloseTo(userAddedQty, offset(0.1));
	}
}
