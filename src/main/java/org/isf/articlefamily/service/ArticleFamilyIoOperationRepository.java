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

package org.isf.articlefamily.service;

import org.isf.articlefamily.model.ArticleFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticleFamilyIoOperationRepository extends JpaRepository<ArticleFamily, Integer> {

	List<ArticleFamily> findAllByActiveOrderByCodeAsc(int active);


	Optional<ArticleFamily> findByCodeAndActive(String code, int active);
	Optional<ArticleFamily> findByIdAndActive(int id, int active);
	Optional<ArticleFamily> findByDescriptionAndActive(String description, int active);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE ArticleFamily af SET af.active = 0 WHERE af.id = :id")
	void softDelete(@Param("id") int id);

	@Query("""
		SELECT af FROM ArticleFamily af
			WHERE af.active = 1 AND (
				LOWER(af.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
				LOWER(af.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
			)
		ORDER BY af.code ASC
	""")
	List<ArticleFamily> searchArticleFamilies(@Param("keyword") String keyword);

	boolean existsByCodeAndActive(String code, int active);

	boolean existsByDescriptionAndActive(String description, int active);

	long countByActive(int active);
}