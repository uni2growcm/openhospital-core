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
package org.isf.stockorder.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.isf.stockorder.model.StockOrder;
import org.isf.stockorder.model.StockOrderStatus;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class StockOrderIoOperations {

	private static final DateTimeFormatter REFERENCE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final String REFERENCE_PREFIX = "CDE";

	private StockOrderIoOperationRepository repository;

	public StockOrderIoOperations(StockOrderIoOperationRepository repository) {
		this.repository = repository;
	}

	public List<StockOrder> getAllOrders() throws OHServiceException {
		return repository.findAllByOrderByOrderDateDesc();
	}

	public StockOrder getOrder(int id) throws OHServiceException {
		return repository.findById(id).orElse(null);
	}

	public StockOrder saveOrder(StockOrder order) throws OHServiceException {
		return repository.save(order);
	}

	public void deleteOrder(StockOrder order) throws OHServiceException {
		repository.delete(order);
	}

	/**
	 * Returns the codes of the {@link org.isf.medicals.model.Medical}s that already appear in a row of
	 * some "open" (not yet confirmed) {@link StockOrder}.
	 *
	 * @return the list of medical codes.
	 * @throws OHServiceException
	 */
	public List<Integer> getMedicalCodesInOpenOrders() throws OHServiceException {
		return repository.findMedicalCodesInOpenOrders();
	}

	/**
	 * Returns a page of {@link StockOrder}s matching the given optional filters.
	 *
	 * @param search free-text search on reference, medical description or medical code; {@code null}/blank for no filter.
	 * @param status the order status to filter by; {@code null} for any status.
	 * @param supplierId the supplier id to filter by; {@code null} for any supplier.
	 * @param dateFrom the earliest order date (inclusive); {@code null} for no lower bound.
	 * @param dateTo the latest order date (inclusive); {@code null} for no upper bound.
	 * @param page the page number (0-based).
	 * @param size the page size.
	 * @return the matching page of {@link StockOrder}s, most recent first.
	 * @throws OHServiceException
	 */
	public Page<StockOrder> getOrdersFiltered(String search, StockOrderStatus status, Integer supplierId, LocalDateTime dateFrom, LocalDateTime dateTo,
					int page, int size) throws OHServiceException {
		return repository.findAllFiltered(search, status, supplierId, dateFrom, dateTo, PageRequest.of(page, size));
	}

	/**
	 * Generate the next available reference number for the given date, in the form
	 * {@code CDE-yyyyMMdd-NNN}, where {@code NNN} restarts from 1 on each new day.
	 *
	 * @param date - the {@link StockOrder} date the reference is generated for.
	 * @return the generated reference number.
	 * @throws OHServiceException
	 */
	public String generateReferenceNumber(LocalDateTime date) throws OHServiceException {
		String prefix = REFERENCE_PREFIX + "-" + date.format(REFERENCE_DATE_FORMAT);
		List<String> existingRefNos = repository.findAllRefNoWhereRefNoLike(prefix + "-%");
		int nextSeq = 1;
		for (String refNo : existingRefNos) {
			try {
				int seq = Integer.parseInt(refNo.substring(prefix.length() + 1));
				if (seq >= nextSeq) {
					nextSeq = seq + 1;
				}
			} catch (NumberFormatException e) {
				// ignore reference numbers whose suffix isn't a plain sequence number
			}
		}
		return String.format("%s-%03d", prefix, nextSeq);
	}
}
