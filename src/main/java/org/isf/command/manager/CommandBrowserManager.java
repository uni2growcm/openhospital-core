/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.command.manager;

import java.util.List;

import org.isf.command.model.Command;
import org.isf.command.model.CommandRow;
import org.isf.command.service.CommandIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

@Component
public class CommandBrowserManager {

	private final CommandIoOperations ioOperations;

	public CommandBrowserManager(CommandIoOperations commandIoOperations) {
		this.ioOperations = commandIoOperations;
	}

	public Command saveOrUpdate(Command command) throws OHServiceException {
		return ioOperations.saveOrUpdate(command);
	}

	public void delete(Command command) throws OHServiceException {
		ioOperations.delete(command);
	}

	public Command getByID(int id) throws OHServiceException {
		return ioOperations.getByID(id);
	}

	public List<Command> getAll() throws OHServiceException {
		return ioOperations.getAll();
	}

	public List<Command> getList() throws OHServiceException {
		return ioOperations.getList();
	}

	public List<Command> getAllOrderByDateDesc() throws OHServiceException {
		return ioOperations.getAllOrderByDateDesc();
	}

	public CommandRow saveOrUpdateRow(CommandRow commandRow) throws OHServiceException {
		return ioOperations.saveOrUpdateRow(commandRow);
	}

	public void deleteRow(CommandRow commandRow) throws OHServiceException {
		ioOperations.deleteRow(commandRow);
	}

	public List<CommandRow> getRowsByCommand(Command command) throws OHServiceException {
		return ioOperations.getRowsByCommand(command);
	}

	public List<CommandRow> getActiveRowsByCommand(Command command) throws OHServiceException {
		return ioOperations.getActiveRowsByCommand(command);
	}
}
