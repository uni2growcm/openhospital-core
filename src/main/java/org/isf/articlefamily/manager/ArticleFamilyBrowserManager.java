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

package org.isf.articlefamily.manager;

import jakarta.persistence.EntityNotFoundException;
import org.isf.articlefamily.model.ArticleFamily;
import org.isf.articlefamily.service.ArticleFamilyIoOperations;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ArticleFamilyBrowserManager {

	private ArticleFamilyIoOperations ioOperations;

	public ArticleFamilyBrowserManager(ArticleFamilyIoOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	/**
	 * Returns all the list of active {@link ArticleFamily}s.
	 *
	 * @return the list of active {@link ArticleFamily}s.
	 * @throws OHServiceException when fails to fetch the article families.
	 */
	public List<ArticleFamily> getArticleFamilies() throws OHServiceException {
		return ioOperations.getAllArticleFamilies();
	}

	/**
	 * Returns the {@link ArticleFamily} with the given id.
	 *
	 * @param id the id of the article family to retrieve.
	 * @return the {@link ArticleFamily} with the given id.
	 * @throws EntityNotFoundException if no active article family is found with the given id.
	 * @throws OHServiceException when fails to fetch the article family.
	 */
	public ArticleFamily getArticleFamily(int id) throws OHServiceException {
		return ioOperations.getArticleFamilyById(id)
			.orElseThrow(() -> new EntityNotFoundException(
				MessageBundle.formatMessage("angal.articlefamily.notfound.msg", String.valueOf(id))
			));
	}

	/**
	 * Returns the active {@link ArticleFamily} with the given code, if it exists.
	 *
	 * @param code the code of the article family to retrieve.
	 * @return an {@link Optional} containing the {@link ArticleFamily} if found and active, or empty otherwise.
	 * @throws OHServiceException when fails to fetch the article family.
	 */
	public Optional<ArticleFamily> getArticleFamilyByCode(String code) throws OHServiceException {
		return ioOperations.getArticleFamilyByCode(code);
	}

	/**
	 * Returns the active {@link ArticleFamily} with the given description, if it exists.
	 *
	 * @param description the description of the article family to retrieve.
	 * @return an {@link Optional} containing the {@link ArticleFamily} if found and active, or empty otherwise.
	 * @throws OHServiceException when fails to fetch the article family.
	 */
	public Optional<ArticleFamily> getArticleFamilyByDescription(String description) throws OHServiceException {
		return ioOperations.getArticleFamilyByDescription(description);
	}

	/**
	 * Saves a {@link ArticleFamily}. Creates it if new, updates it if already existing.
	 *
	 * @param articleFamily the article family to save.
	 * @return the saved {@link ArticleFamily}.
	 * @throws OHServiceException when fails to save the article family.
	 */
	public ArticleFamily saveArticleFamily(ArticleFamily articleFamily) throws OHServiceException {
		return ioOperations.saveArticleFamily(articleFamily);
	}

	/**
	 * Updates an existing {@link ArticleFamily}.
	 *
	 * @param articleFamily the article family with updated fields.
	 * @return the updated {@link ArticleFamily}.
	 * @throws OHServiceException when fails to update the article family.
	 */
	public ArticleFamily updateArticleFamily(ArticleFamily articleFamily) throws OHServiceException {
		return ioOperations.updateArticleFamily(articleFamily);
	}

	/**
	 * Soft-deletes the {@link ArticleFamily} with the given id by setting its active flag to {@code 0}.
	 * The record is not physically removed from the database.
	 *
	 * @param id the id of the article family to delete.
	 * @throws OHServiceException when fails to delete the article family.
	 */
	public void deleteArticleFamily(int id) throws OHServiceException {
		ioOperations.deleteArticleFamily(id);
	}

	/**
	 * Searches for active {@link ArticleFamily}s whose code or description
	 * contains the given keyword (case-insensitive).
	 * Returns all active article families if the keyword is {@code null} or blank.
	 *
	 * @param keyword the search keyword; may be {@code null} or empty.
	 * @return the list of matching {@link ArticleFamily}s.
	 * @throws OHServiceException when fails to execute the search.
	 */
	public List<ArticleFamily> searchArticleFamilies(String keyword) throws OHServiceException {
		return ioOperations.searchArticleFamilies(keyword);
	}

	/**
	 * Checks whether the given code is unique among active {@link ArticleFamily}s.
	 * When {@code excludeId} is provided, the article family with that id is excluded
	 * from the uniqueness check (useful when updating an existing article family).
	 *
	 * @param code      the code to check.
	 * @param excludeId the id of the article family to exclude from the check, or {@code null} for a creation check.
	 * @return {@code true} if the code is not used by any other active article family, {@code false} otherwise.
	 * @throws OHServiceException when fails to execute the uniqueness check.
	 */
	public boolean isCodeUnique(String code, Integer excludeId) throws OHServiceException {
		return ioOperations.isCodeUnique(code, excludeId);
	}

	/**
	 * Returns the total count of active article families.
	 *
	 * @return the count of active article families.
	 * @throws OHServiceException when fails to count the article families.
	 */
	public long getActiveCount() throws OHServiceException {
		return ioOperations.getActiveCount();
	}
}