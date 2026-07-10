package org.isf.tuberculosis.service;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.tuberculosis.model.TuberculosisVisit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TuberculosisVisitIoOperationRepository extends JpaRepository<TuberculosisVisit, Integer> {

    List<TuberculosisVisit> findByTreatment_IdOrderByVisitDateDesc(Integer treatmentId);

    Page<TuberculosisVisit> findByTreatment_Id(Integer treatmentId, Pageable pageable);

    @Query("SELECT v FROM TuberculosisVisit v WHERE v.treatment.id = :treatmentId "
        + "AND (:dateFrom IS NULL OR v.visitDate >= :dateFrom) "
        + "AND (:dateTo IS NULL OR v.visitDate <= :dateTo)")
    List<TuberculosisVisit> findByTreatmentIdAndDateRange(
        @Param("treatmentId") Integer treatmentId,
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo);
}
