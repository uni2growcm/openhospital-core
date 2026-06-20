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

import java.time.LocalDateTime;

import org.isf.command.model.Command;
import org.isf.utils.exception.OHException;

public class TestCommand {

	private Integer id;
	private String refNo = "TestRefNo";
	private LocalDateTime date = LocalDateTime.of(2024, 1, 1, 0, 0, 0);

	public Command setup(boolean usingSet) throws OHException {
		Command command;

		if (usingSet) {
			command = new Command();
			setParameters(command);
		} else {
			command = new Command(id, refNo, date);
		}

		return command;
	}

	public void setParameters(Command command) {
		command.setRefNo(refNo);
		command.setDate(date);
	}

	public void check(Command command) {
		assertThat(command.getRefNo()).isEqualTo(refNo);
		assertThat(command.getDate()).isEqualTo(date);
	}
}
