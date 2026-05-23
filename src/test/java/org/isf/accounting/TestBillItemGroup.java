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
package org.isf.accounting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import org.isf.accounting.model.BillItemGroup;
import org.isf.utils.exception.OHException;

public class TestBillItemGroup {

	private static final String title = "Pregnancy Kit";
	private static final String description = "Complete pregnancy care kit";
	private static final Double total = 100.0;

	public BillItemGroup setup(boolean usingSet) throws OHException {
		BillItemGroup group;
		String uniqueTitle = title + "_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);

		if (usingSet) {
			group = new BillItemGroup();
			setParameters(group, uniqueTitle);
		} else {
			group = new BillItemGroup(uniqueTitle, description, total);
		}

		return group;
	}

	public void setParameters(BillItemGroup group, String uniqueTitle) {
		group.setTitle(uniqueTitle);
		group.setDescription(description);
		group.setTotal(total);
	}

	public void setParameters(BillItemGroup group) {
		String uniqueTitle = title + "_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
		setParameters(group, uniqueTitle);
	}

	public void check(BillItemGroup group) {
		assertThat(group.getTitle()).isNotNull().isNotEmpty().startsWith(title);
		assertThat(group.getDescription()).isEqualTo(description);
		assertThat(group.getTotal()).isCloseTo(total, offset(0.1));
	}
}
