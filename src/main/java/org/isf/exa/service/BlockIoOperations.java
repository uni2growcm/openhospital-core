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
package org.isf.exa.service;

import java.util.List;
import java.util.Objects;

import org.isf.exa.model.Block;
import org.isf.exa.model.BlockExam;
import org.isf.exa.model.Exam;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class BlockIoOperations {

	private final BlockIoOperationRepository blockRepository;

	private final BlockExamIoOperationRepository blockExamRepository;

	private final ExamIoOperationRepository examRepository;

	public BlockIoOperations(BlockIoOperationRepository blockRepository,
			BlockExamIoOperationRepository blockExamRepository,
			ExamIoOperationRepository examRepository) {
		this.blockRepository = blockRepository;
		this.blockExamRepository = blockExamRepository;
		this.examRepository = examRepository;
	}

	/**
	 * Returns the list of {@link Block}s.
	 * @return the list of {@link Block}s
	 * @throws OHServiceException
	 */
	public List<Block> getBlocks() throws OHServiceException {
		return blockRepository.findByOrderByDescriptionAsc();
	}

	/**
	 * Returns the list of {@link Block}s that matches the passed description.
	 * @param description - the block description
	 * @return the filtered list of {@link Block}s
	 * @throws OHServiceException
	 */
	public List<Block> getBlocks(String description) throws OHServiceException {
		return description != null && !description.isBlank()
				? blockRepository.findByDescriptionContainingOrderByDescriptionAsc(description)
				: blockRepository.findByOrderByDescriptionAsc();
	}

	/**
	 * Finds a {@link Block} by its code.
	 * @param code - the block code
	 * @return the {@link Block} if found, {@code null} otherwise
	 * @throws OHServiceException
	 */
	public Block getBlock(String code) throws OHServiceException {
		return blockRepository.findById(code).orElse(null);
	}

	/**
	 * Insert a new {@link Block}.
	 * @param block - the {@link Block} to insert
	 * @return the newly persisted {@link Block}
	 * @throws OHServiceException
	 */
	public Block newBlock(Block block) throws OHServiceException {
		return blockRepository.save(block);
	}

	/**
	 * Update an already existing {@link Block} (description only; the code is the primary key).
	 * @param block - the {@link Block} to update
	 * @return the updated {@link Block}
	 * @throws OHServiceException
	 */
	public Block updateBlock(Block block) throws OHServiceException {
		return blockRepository.save(block);
	}

	/**
	 * Delete a {@link Block} together with all its exam associations.
	 * @param code - the code of the {@link Block} to delete
	 * @throws OHServiceException
	 */
	public void deleteBlock(String code) throws OHServiceException {
		blockExamRepository.deleteByBlock_Code(code);
		blockRepository.deleteById(code);
	}

	/**
	 * Returns the {@link Exam}s associated to the {@link Block} with the given code.
	 * @param code - the block code
	 * @return the list of {@link Exam}s of the block
	 * @throws OHServiceException
	 */
	public List<Exam> getExamWithBlock(String code) throws OHServiceException {
		return blockExamRepository.findByBlock_Code(code).stream()
				.map(BlockExam::getExam)
				.filter(Objects::nonNull)
				.toList();
	}

	/**
	 * Replaces the exams of the given {@link Block} with the passed list:
	 * all existing associations are removed and the new ones are inserted.
	 * @param block - the {@link Block} to associate the exams with
	 * @param exams - the {@link List<Exam>} to associate
	 * @throws OHServiceException
	 */
	@Transactional
	public void saveExamBlocks(Block block, List<Exam> exams) throws OHServiceException {
		blockExamRepository.deleteByBlock_Code(block.getCode());
		if (exams != null && !exams.isEmpty()) {
			blockExamRepository.saveAll(exams.stream()
					.map(exam -> new BlockExam(block, exam))
					.toList());
		}
	}

	/**
	 * Finds an {@link Exam} by its description.
	 * @param description - the exam description
	 * @return the {@link Exam} if found, {@code null} otherwise
	 * @throws OHServiceException
	 */
	public Exam getExamByDescription(String description) throws OHServiceException {
		return examRepository.findAll().stream()
				.filter(exam -> Objects.equals(exam.getDescription(), description))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Checks if a {@link Block} with the given code already exists.
	 * @param code - the block code
	 * @return {@code true} if the code is already in use, {@code false} otherwise
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return blockRepository.existsById(code);
	}
}
