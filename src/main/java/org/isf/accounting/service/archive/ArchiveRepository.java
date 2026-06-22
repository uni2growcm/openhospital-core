/*
 * Open Hospital (www.open-hospital.org)
 * Copyright  2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.accounting.service.archive;

import java.time.LocalDateTime;
import org.isf.accounting.model.BillItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchiveRepository extends JpaRepository<BillItems, Integer> {

	@Query(value = "SELECT COUNT(*) FROM OH_BILLS WHERE DATEDIFF(:currentTime, DATE(BLL_UPDATE)) >= :nbDays AND BLL_STATUS = :status", nativeQuery = true)
	int countBillsToArchive(@Param("currentTime") LocalDateTime currentTime, @Param("nbDays") int nbDays, @Param("status") String status);

	@Modifying
	@Query(value = "INSERT INTO OH_ARCHIVED_BILLS SELECT * FROM OH_BILLS b WHERE DATEDIFF(:currentTime, DATE(b.BLL_UPDATE)) >= :nbDays AND b.BLL_STATUS = :status AND b.BLL_PARENT_ID IS NULL", nativeQuery = true)
	int archiveMainBills(@Param("currentTime") LocalDateTime currentTime, @Param("nbDays") int nbDays, @Param("status") String status);

	@Query(value = "SELECT COUNT(*) FROM OH_BILLS b USE INDEX(PRIMARY) INNER JOIN OH_BILLS b2 ON b2.BLL_ID = b.BLL_PARENT_ID WHERE DATEDIFF(:currentTime, DATE(b2.BLL_UPDATE)) >= :nbDays AND b2.BLL_STATUS = :status", nativeQuery = true)
	int countRefundBillsToArchive(@Param("currentTime") LocalDateTime currentTime, @Param("nbDays") int nbDays, @Param("status") String status);

	@Modifying
	@Query(value = "INSERT INTO OH_ARCHIVED_BILLS SELECT b.* FROM OH_BILLS b INNER JOIN OH_BILLS b2 ON b2.BLL_ID = b.BLL_PARENT_ID WHERE DATEDIFF(:currentTime, DATE(b2.BLL_UPDATE)) >= :nbDays AND b2.BLL_STATUS = :status", nativeQuery = true)
	int archiveRefundBills(@Param("currentTime") LocalDateTime currentTime, @Param("nbDays") int nbDays, @Param("status") String status);

	@Modifying
	@Query(value = "DELETE b FROM OH_BILLS b INNER JOIN OH_BILLS b2 ON b2.BLL_ID = b.BLL_PARENT_ID WHERE DATEDIFF(:currentTime, DATE(b2.BLL_UPDATE)) >= :nbDays AND b2.BLL_STATUS = :status", nativeQuery = true)
	int deleteRefundBills(@Param("currentTime") LocalDateTime currentTime, @Param("nbDays") int nbDays, @Param("status") String status);

	@Modifying
	@Query(value = "DELETE FROM OH_BILLS WHERE DATEDIFF(:currentTime, DATE(BLL_UPDATE)) >= :nbDays AND BLL_STATUS = :status", nativeQuery = true)
	int deleteMainBills(@Param("currentTime") LocalDateTime currentTime, @Param("nbDays") int nbDays, @Param("status") String status);

	@Query(value = "SELECT COUNT(*) FROM OH_BILLITEMS bli USE INDEX(PRIMARY) "
		+ "INNER JOIN OH_BILLS b ON b.BLL_ID = bli.BLI_ID_BILL "
		+ "LEFT JOIN OH_BILLS b2 ON b2.BLL_ID = b.BLL_PARENT_ID "
		+ "WHERE (b.BLL_PARENT_ID IS NULL AND DATEDIFF(:currentTime, DATE(b.BLL_UPDATE)) >= :nbDays AND b.BLL_STATUS = :status) "
		+ "OR (b.BLL_PARENT_ID IS NOT NULL AND DATEDIFF(:currentTime, DATE(b2.BLL_UPDATE)) >= :nbDays AND b2.BLL_STATUS = :status)", nativeQuery = true)
	int countItemsToArchive(@Param("currentTime") LocalDateTime currentTime, @Param("nbDays") int nbDays, @Param("status") String status);

	@Modifying
	@Query(value = "INSERT INTO OH_ARCHIVED_BILLITEMS SELECT bli.* FROM OH_BILLITEMS bli "
		+ "INNER JOIN OH_BILLS b ON b.BLL_ID = bli.BLI_ID_BILL "
		+ "LEFT JOIN OH_BILLS b2 ON b2.BLL_ID = b.BLL_PARENT_ID "
		+ "WHERE (b.BLL_PARENT_ID IS NULL AND DATEDIFF(:currentTime, DATE(b.BLL_UPDATE)) >= :nbDays AND b.BLL_STATUS = :status) "
		+ "OR (b.BLL_PARENT_ID IS NOT NULL AND DATEDIFF(:currentTime, DATE(b2.BLL_UPDATE)) >= :nbDays AND b2.BLL_STATUS = :status)", nativeQuery = true)
	int archiveItems(@Param("currentTime") LocalDateTime currentTime, @Param("nbDays") int nbDays, @Param("status") String status);

	@Modifying
	@Query(value = "DELETE bli FROM OH_BILLITEMS bli "
		+ "INNER JOIN OH_BILLS b ON b.BLL_ID = bli.BLI_ID_BILL "
		+ "LEFT JOIN OH_BILLS b2 ON b2.BLL_ID = b.BLL_PARENT_ID "
		+ "WHERE (b.BLL_PARENT_ID IS NULL AND DATEDIFF(:currentTime, DATE(b.BLL_UPDATE)) >= :nbDays AND b.BLL_STATUS = :status) "
		+ "OR (b.BLL_PARENT_ID IS NOT NULL AND DATEDIFF(:currentTime, DATE(b2.BLL_UPDATE)) >= :nbDays AND b2.BLL_STATUS = :status)", nativeQuery = true)
	int deleteItems(@Param("currentTime") LocalDateTime currentTime, @Param("nbDays") int nbDays, @Param("status") String status);

	@Query(value = "SELECT COUNT(*) FROM OH_BILLPAYMENTS bp USE INDEX(PRIMARY) "
		+ "INNER JOIN OH_BILLS b ON b.BLL_ID = bp.BLP_ID_BILL "
		+ "LEFT JOIN OH_BILLS b2 ON b2.BLL_ID = b.BLL_PARENT_ID "
		+ "WHERE (b.BLL_PARENT_ID IS NULL AND DATEDIFF(:currentTime, DATE(b.BLL_UPDATE)) >= :nbDays AND b.BLL_STATUS = :status) "
		+ "OR (b.BLL_PARENT_ID IS NOT NULL AND DATEDIFF(:currentTime, DATE(b2.BLL_UPDATE)) >= :nbDays AND b2.BLL_STATUS = :status)", nativeQuery = true)
	int countPaymentsToArchive(@Param("currentTime") LocalDateTime currentTime, @Param("nbDays") int nbDays, @Param("status") String status);

	@Modifying
	@Query(value = "INSERT INTO OH_ARCHIVED_BILLPAYMENTS SELECT bp.* FROM OH_BILLPAYMENTS bp "
		+ "INNER JOIN OH_BILLS b ON b.BLL_ID = bp.BLP_ID_BILL "
		+ "LEFT JOIN OH_BILLS b2 ON b2.BLL_ID = b.BLL_PARENT_ID "
		+ "WHERE (b.BLL_PARENT_ID IS NULL AND DATEDIFF(:currentTime, DATE(b.BLL_UPDATE)) >= :nbDays AND b.BLL_STATUS = :status) "
		+ "OR (b.BLL_PARENT_ID IS NOT NULL AND DATEDIFF(:currentTime, DATE(b2.BLL_UPDATE)) >= :nbDays AND b2.BLL_STATUS = :status)", nativeQuery = true)
	int archivePayments(@Param("currentTime") LocalDateTime currentTime, @Param("nbDays") int nbDays, @Param("status") String status);

	@Modifying
	@Query(value = "DELETE bp FROM OH_BILLPAYMENTS bp "
		+ "INNER JOIN OH_BILLS b ON b.BLL_ID = bp.BLP_ID_BILL "
		+ "LEFT JOIN OH_BILLS b2 ON b2.BLL_ID = b.BLL_PARENT_ID "
		+ "WHERE (b.BLL_PARENT_ID IS NULL AND DATEDIFF(:currentTime, DATE(b.BLL_UPDATE)) >= :nbDays AND b.BLL_STATUS = :status) "
		+ "OR (b.BLL_PARENT_ID IS NOT NULL AND DATEDIFF(:currentTime, DATE(b2.BLL_UPDATE)) >= :nbDays AND b2.BLL_STATUS = :status)", nativeQuery = true)
	int deletePayments(@Param("currentTime") LocalDateTime currentTime, @Param("nbDays") int nbDays, @Param("status") String status);

	@Query(value = "SELECT PRMS_VALUE FROM OH_PARAMETERS WHERE PRMS_CODE = :code", nativeQuery = true)
	String findParameterValueByCode(@Param("code") String code);
}
