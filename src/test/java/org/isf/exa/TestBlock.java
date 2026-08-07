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
package org.isf.exa;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.exa.model.Block;
import org.isf.exa.model.BlockExam;
import org.isf.exa.model.Exam;
import org.isf.utils.exception.OHException;

public class TestBlock {

	private String code = "BLK";
	private String description = "TestDescription";

	public Block setup(boolean usingSet) throws OHException {
		Block block;

		if (usingSet) {
			block = new Block();
			setParameters(block);
		} else {
			// Create Block with all parameters
			block = new Block(code, description);
		}

		return block;
	}

	public void setParameters(Block block) {
		block.setCode(code);
		block.setDescription(description);
	}

	public void check(Block block) {
		assertThat(block.getCode()).isEqualTo(code);
		assertThat(block.getDescription()).isEqualTo(description);
	}

	public BlockExam setupBlockExam(Block block, Exam exam) {
		return new BlockExam(block, exam);
	}

	public void checkBlockExam(BlockExam blockExam, Block block, Exam exam) {
		assertThat(blockExam.getBlock()).isEqualTo(block);
		assertThat(blockExam.getExam()).isEqualTo(exam);
	}
}
