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
import java.util.List;

import org.isf.stockorder.model.StockOrder;
import org.isf.stockorder.model.StockOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockOrderIoOperationRepository extends JpaRepository<StockOrder, Integer> {

	List<StockOrder> findAllByOrderByOrderDateDesc();

	@Query("select so.refNo from StockOrder so where so.refNo like :pattern")
	List<String> findAllRefNoWhereRefNoLike(@Param("pattern") String pattern);

	@Query("select distinct r.medical.code from StockOrder so join so.rows r where so.status = org.isf.stockorder.model.StockOrderStatus.open")
	List<Integer> findMedicalCodesInOpenOrders();

	@Query("""
		select distinct so from StockOrder so
			left join so.rows r
			where (:search is null or :search = ''
				or lower(so.refNo) like lower(concat('%', :search, '%'))
				or lower(r.medical.description) like lower(concat('%', :search, '%'))
				or lower(r.medical.prod_code) like lower(concat('%', :search, '%')))
			and (:status is null or so.status = :status)
			and (:supplierId is null or so.supplier.supId = :supplierId)
			and (:dateFrom is null or so.orderDate >= :dateFrom)
			and (:dateTo is null or so.orderDate <= :dateTo)
			order by so.orderDate desc
		""")
	Page<StockOrder> findAllFiltered(@Param("search") String search, @Param("status") StockOrderStatus status,
					@Param("supplierId") Integer supplierId, @Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo,
					Pageable pageable);
}
