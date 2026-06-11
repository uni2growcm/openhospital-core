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
package org.isf.command.service;

import java.util.List;

import org.isf.command.model.Command;
import org.isf.command.model.CommandRow;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class CommandIoOperations {

	private final CommandIoOperationRepository repository;
	private final CommandRowIoOperationRepository rowRepository;

	public CommandIoOperations(CommandIoOperationRepository commandIoOperationRepository,
			CommandRowIoOperationRepository commandRowIoOperationRepository) {
		this.repository = commandIoOperationRepository;
		this.rowRepository = commandRowIoOperationRepository;
	}

	public Command saveOrUpdate(Command command) throws OHServiceException {
		return repository.save(command);
	}

	public void delete(Command command) throws OHServiceException {
		repository.delete(command);
	}

	public Command getByID(int id) throws OHServiceException {
		return repository.findById(id).orElse(null);
	}

	public List<Command> getAll() throws OHServiceException {
		return repository.findAll();
	}

	public List<Command> getList() throws OHServiceException {
		return repository.findAllActiveOrderByDateDesc();
	}

	public List<Command> getAllOrderByDateDesc() throws OHServiceException {
		return repository.findAllOrderByDateDesc();
	}

	public CommandRow saveOrUpdateRow(CommandRow commandRow) throws OHServiceException {
		return rowRepository.save(commandRow);
	}

	public void deleteRow(CommandRow commandRow) throws OHServiceException {
		rowRepository.delete(commandRow);
	}

	public List<CommandRow> getRowsByCommand(Command command) throws OHServiceException {
		return rowRepository.findByCommandOrderById(command);
	}

	public List<CommandRow> getActiveRowsByCommand(Command command) throws OHServiceException {
		return rowRepository.findByCommandAndActiveOrderById(command, 1);
	}

	public List<Integer> getMedicalIdsAlreadyInCommandRows() throws OHServiceException {
		return rowRepository.findDistinctMedicalIdsFromActiveRows();
	}
}
