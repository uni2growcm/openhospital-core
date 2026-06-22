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
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleFamilyIoOperations {

	private ArticleFamilyIoOperationRepository repository;

	public ArticleFamilyIoOperations(ArticleFamilyIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Returns all active {@link ArticleFamily}s ordered by code ascending.
	 *
	 * @return the list of active {@link ArticleFamily}s.
	 * @throws OHServiceException when fails to fetch the article families.
	 */
	public List<ArticleFamily> getAllArticleFamilies() throws OHServiceException {
		return repository.findAllByActiveOrderByCodeAsc(1);
	}

	/**
	 * Returns the active {@link ArticleFamily} with the given code, if it exists.
	 *
	 * @param code the code of the article family to retrieve.
	 * @return an {@link Optional} containing the {@link ArticleFamily} if found and active, or empty otherwise.
	 * @throws OHServiceException when fails to fetch the article family.
	 */
	public Optional<ArticleFamily> getArticleFamilyByCode(String code) throws OHServiceException {
		return repository.findByCodeAndActive(code, 1);
	}

	/**
	 * Returns the active {@link ArticleFamily} with the given id, if it exists.
	 *
	 * @param id the id of the article family to retrieve.
	 * @return an {@link Optional} containing the {@link ArticleFamily} if found and active, or empty otherwise.
	 * @throws OHServiceException when fails to fetch the article family.
	 */
	public Optional<ArticleFamily> getArticleFamilyById(int id) throws OHServiceException {
		return repository.findByIdAndActive(id, 1);
	}

	/**
	 * Returns the active {@link ArticleFamily} with the given description, if it exists.
	 *
	 * @param description the description of the article family to retrieve.
	 * @return an {@link Optional} containing the {@link ArticleFamily} if found and active, or empty otherwise.
	 * @throws OHServiceException when fails to fetch the article family.
	 */
	public Optional<ArticleFamily> getArticleFamilyByDescription(String description) throws OHServiceException {
		return repository.findByDescriptionAndActive(description, 1);
	}

	/**
	 * Saves the given {@link ArticleFamily}. Creates it if new, updates it if already existing.
	 * Audit fields (createdBy, createdDate, lastModifiedBy, lastModifiedDate) are populated
	 * automatically via {@code Auditable} and Spring Data JPA auditing.
	 *
	 * @param articleFamily the article family to save.
	 * @return the saved {@link ArticleFamily}.
	 * @throws OHServiceException when fails to save the article family.
	 */
	public ArticleFamily saveArticleFamily(ArticleFamily articleFamily) throws OHServiceException {
		return repository.save(articleFamily);
	}

	/**
	 * Updates an existing {@link ArticleFamily}.
	 *
	 * @param articleFamily the article family with updated fields.
	 * @return the updated {@link ArticleFamily}.
	 * @throws OHServiceException when fails to update the article family.
	 */
	@Transactional
	public ArticleFamily updateArticleFamily(ArticleFamily articleFamily) throws OHServiceException {
		return repository.save(articleFamily);
	}

	/**
	 * Soft-deletes the {@link ArticleFamily} with the given id by setting its {@code active} flag to {@code 0}.
	 * The record remains in the database and will no longer appear in active queries.
	 *
	 * @param id the id of the article family to soft-delete.
	 * @throws OHServiceException when fails to soft-delete the article family.
	 */
	@Transactional
	public void deleteArticleFamily(int id) throws OHServiceException {
		repository.softDelete(id);
	}

	/**
	 * Checks whether the given code is unique among active {@link ArticleFamily}s.
	 * When {@code excludeId} is provided, the article family with that id is excluded
	 * from the uniqueness check (useful during an update to avoid self-conflict).
	 *
	 * @param code      the code to check.
	 * @param excludeId the id of the article family to exclude, or {@code null} for a creation check.
	 * @return {@code true} if the code is available, {@code false} if already taken by another article family.
	 * @throws OHServiceException when fails to execute the uniqueness check.
	 */
	public boolean isCodeUnique(String code, Integer excludeId) throws OHServiceException {
		Optional<ArticleFamily> existing = repository.findByCodeAndActive(code, 1);
		if (excludeId == null) {
			return existing.isEmpty();
		}
		return existing.isEmpty() || existing.get().getId() == excludeId;
	}

	/**
	 * Searches for active {@link ArticleFamily}s whose code or description
	 * contains the given keyword (case-insensitive).
	 * If the keyword is {@code null} or blank, returns all active article families.
	 *
	 * @param keyword the search keyword; may be {@code null} or empty.
	 * @return the list of matching active {@link ArticleFamily}s.
	 * @throws OHServiceException when fails to execute the search.
	 */
	public List<ArticleFamily> searchArticleFamilies(String keyword) throws OHServiceException {
		if (keyword == null || keyword.trim().isEmpty()) {
			return getAllArticleFamilies();
		}
		return repository.searchArticleFamilies(keyword.trim());
	}

	/**
	 * Returns the total count of active article families.
	 *
	 * @return the count of active article families.
	 * @throws OHServiceException when fails to count the article families.
	 */
	public long getActiveCount() throws OHServiceException {
		return repository.countByActive(1);
	}
}