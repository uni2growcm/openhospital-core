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
package org.isf.exa.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.exa.model.Block;
import org.isf.exa.model.Exam;
import org.isf.exa.service.BlockIoOperations;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

/**
 * Class that provides gui separation from database operations and gives some useful logic
 * manipulations of the dynamic data (memory).
 */
@Component
public class BlockBrowsingManager {

	private final BlockIoOperations ioOperations;

	public BlockBrowsingManager(BlockIoOperations blockIoOperations) {
		this.ioOperations = blockIoOperations;
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any.
	 * @param block - the {@link Block} to validate
	 * @param insert {@code true} or updated {@code false}
	 * @throws OHServiceException
	 */
	protected void validateBlock(Block block, boolean insert) throws OHServiceException {
		String code = block.getCode();
		String description = block.getDescription();
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (code == null || code.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		}
		if (description == null || description.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}
		if (insert && isCodePresent(code)) {
			throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Returns the list of {@link Block}s.
	 * @return the list of {@link Block}s
	 * @throws OHServiceException
	 */
	public List<Block> getBlocks() throws OHServiceException {
		return ioOperations.getBlocks();
	}

	/**
	 * Returns the list of {@link Block}s that matches the passed description.
	 * @param description - the block description
	 * @return the filtered list of {@link Block}s
	 * @throws OHServiceException
	 */
	public List<Block> getBlocks(String description) throws OHServiceException {
		return ioOperations.getBlocks(description);
	}

	/**
	 * Finds a {@link Block} by its code.
	 * @param code - the block code
	 * @return the {@link Block} if found, {@code null} otherwise
	 * @throws OHServiceException
	 */
	public Block getBlock(String code) throws OHServiceException {
		return ioOperations.getBlock(code);
	}

	/**
	 * Insert a new {@link Block}.
	 * @param block - the {@link Block} to insert
	 * @return the newly persisted {@link Block}
	 * @throws OHServiceException
	 */
	public Block newBlock(Block block) throws OHServiceException {
		validateBlock(block, true);
		return ioOperations.newBlock(block);
	}

	/**
	 * Update an already existing {@link Block}.
	 * @param block - the {@link Block} to update
	 * @return the updated {@link Block}
	 * @throws OHServiceException
	 */
	public Block updateBlock(Block block) throws OHServiceException {
		validateBlock(block, false);
		return ioOperations.updateBlock(block);
	}

	/**
	 * Delete a {@link Block} together with all its exam associations.
	 * @param code - the code of the {@link Block} to delete
	 * @throws OHServiceException
	 */
	public void deleteBlock(String code) throws OHServiceException {
		ioOperations.deleteBlock(code);
	}

	/**
	 * Returns the {@link Exam}s associated to the {@link Block} with the given code.
	 * @param code - the block code
	 * @return the list of {@link Exam}s of the block
	 * @throws OHServiceException
	 */
	public List<Exam> getExamWithBlock(String code) throws OHServiceException {
		return ioOperations.getExamWithBlock(code);
	}

	/**
	 * Replaces the exams of the given {@link Block} with the passed list.
	 * @param block - the {@link Block} to associate the exams with
	 * @param exams - the {@link List<Exam>} to associate
	 * @throws OHServiceException
	 */
	public void saveExamBlocks(Block block, List<Exam> exams) throws OHServiceException {
		ioOperations.saveExamBlocks(block, exams);
	}

	/**
	 * Finds an {@link Exam} by its description.
	 * @param description - the exam description
	 * @return the {@link Exam} if found, {@code null} otherwise
	 * @throws OHServiceException
	 */
	public Exam getExamByDescription(String description) throws OHServiceException {
		return ioOperations.getExamByDescription(description);
	}

	/**
	 * Checks if a {@link Block} with the given code already exists.
	 * @param code - the block code
	 * @return {@code true} if the code is already in use, {@code false} otherwise
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}
}
