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
package org.isf.stockorder.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstock.model.Movement;
import org.isf.stockorder.model.StockOrder;
import org.isf.stockorder.model.StockOrderStatus;
import org.isf.stockorder.service.StockOrderIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.generaldata.MessageBundle;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StockOrderBrowserManager {

	private StockOrderIoOperations ioOperations;
	private MovStockInsertingManager movStockInsertingManager;

	public StockOrderBrowserManager(StockOrderIoOperations ioOperations, MovStockInsertingManager movStockInsertingManager) {
		this.ioOperations = ioOperations;
		this.movStockInsertingManager = movStockInsertingManager;
	}

	public List<StockOrder> getOrders() throws OHServiceException {
		return ioOperations.getAllOrders();
	}

	public StockOrder getOrder(int id) throws OHServiceException {
		return ioOperations.getOrder(id);
	}

	public StockOrder newOrder(StockOrder order) throws OHServiceException {
		return ioOperations.saveOrder(order);
	}

	public StockOrder updateOrder(StockOrder order) throws OHServiceException {
		return ioOperations.saveOrder(order);
	}

	public void deleteOrder(StockOrder order) throws OHServiceException {
		ioOperations.deleteOrder(order);
	}

	/**
	 * Returns the codes of the medicals that already appear in a row of some "open" (not yet confirmed)
	 * order, and should therefore be left out of the low-stock list offered when starting a new order.
	 *
	 * @return the list of medical codes.
	 * @throws OHServiceException
	 */
	public List<Integer> getMedicalCodesInOpenOrders() throws OHServiceException {
		return ioOperations.getMedicalCodesInOpenOrders();
	}

	/**
	 * Returns a page of {@link StockOrder}s matching the given optional filters, most recent first.
	 *
	 * @param search free-text search on reference, medical description or medical code; {@code null}/blank for no filter.
	 * @param status the order status to filter by; {@code null} for any status.
	 * @param supplierId the supplier id to filter by; {@code null} for any supplier.
	 * @param dateFrom the earliest order date (inclusive); {@code null} for no lower bound.
	 * @param dateTo the latest order date (inclusive); {@code null} for no upper bound.
	 * @param page the page number (0-based).
	 * @param size the page size.
	 * @return the matching page of {@link StockOrder}s.
	 * @throws OHServiceException
	 */
	public Page<StockOrder> getOrdersFiltered(String search, StockOrderStatus status, Integer supplierId, LocalDateTime dateFrom, LocalDateTime dateTo,
					int page, int size) throws OHServiceException {
		return ioOperations.getOrdersFiltered(search, status, supplierId, dateFrom, dateTo, page, size);
	}

	/**
	 * Generate the next available reference number for a new {@link StockOrder}, dated the given date.
	 *
	 * @param date the {@link StockOrder} date the reference is generated for.
	 * @return the generated reference number.
	 * @throws OHServiceException
	 */
	public String generateReferenceNumber(LocalDateTime date) throws OHServiceException {
		return ioOperations.generateReferenceNumber(date);
	}

	/**
	 * Convert an "open" {@link StockOrder} into a stock-in (charging) movement: one {@link Movement} per
	 * order row, using the lot supplied for each row by the caller. On success, the order is marked
	 * {@link StockOrderStatus#closed}.
	 *
	 * @param order the order to confirm; must currently be {@link StockOrderStatus#open}.
	 * @param movements the movements to create, one per order row, built by the caller (which is
	 *        responsible for collecting the lot information interactively).
	 * @return the list of inserted {@link Movement}s.
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	public List<Movement> confirmOrder(StockOrder order, List<Movement> movements) throws OHServiceException {
		if (order.getStatus() != StockOrderStatus.open) {
			throw new OHDataValidationException(
							new OHExceptionMessage(MessageBundle.getMessage("angal.stockorder.ordernoteditable.msg")));
		}
		List<Movement> insertedMovements = new ArrayList<>(
						movStockInsertingManager.newMultipleChargingMovements(movements, order.getRefNo()));
		order.setStatus(StockOrderStatus.closed);
		ioOperations.saveOrder(order);
		return insertedMovements;
	}
}
