/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.mortuary.service;

import java.util.List;

import org.isf.mortuary.model.Mortuary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MortuaryIoOperationRepository extends JpaRepository<Mortuary, String> {

	@Query(value = "SELECT m FROM OH_MORTUARY m WHERE  ORDER BY w.description")
	List<Mortuary> findAllMortuariesByOrderByDescriptionAsc();

	//List<Mortuary> findByCodeNot(String code);

	//List<Mortuary> findByCodeContains(String id);

	//List<Mortuary> findByIsOpdIsTrue();

	//List<Mortuary> findByBedsGreaterThanZero();

	@Query("select count(m) from OH_MORTUARY m")
	long countAllActiveMortuaries();
}





