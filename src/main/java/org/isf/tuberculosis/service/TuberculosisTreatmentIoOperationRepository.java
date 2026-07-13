package org.isf.tuberculosis.service;

import java.time.LocalDate;
import java.util.List;

import org.isf.tuberculosis.model.Classification;
import org.isf.tuberculosis.model.DiseaseLocation;
import org.isf.tuberculosis.model.TuberculosisTreatment;
import org.isf.tuberculosis.model.TreatmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TuberculosisTreatmentIoOperationRepository extends JpaRepository<TuberculosisTreatment, Integer> {

    List<TuberculosisTreatment> findByPatient_Code(Integer patientCode);

    Page<TuberculosisTreatment> findByStatus(TreatmentStatus status, Pageable pageable);

    @Query("SELECT t FROM TuberculosisTreatment t WHERE "
        + "(:patientCode IS NULL OR t.patient.code = :patientCode) "
        + "AND (:status IS NULL OR t.status = :status) "
        + "AND (:classification IS NULL OR t.classification = :classification) "
        + "AND (:diseaseLocation IS NULL OR t.diseaseLocation = :diseaseLocation) "
        + "AND (:dateFrom IS NULL OR DATE(t.registrationDate) >= :dateFrom) "
        + "AND (:dateTo IS NULL OR DATE(t.registrationDate) <= :dateTo) "
        + "AND (:startDateFrom IS NULL OR t.treatmentStartDate >= :startDateFrom) "
        + "AND (:startDateTo IS NULL OR t.treatmentStartDate <= :startDateTo)")
    Page<TuberculosisTreatment> findByFilters(
        @Param("patientCode") Integer patientCode,
        @Param("status") TreatmentStatus status,
        @Param("classification") Classification classification,
        @Param("diseaseLocation") DiseaseLocation diseaseLocation,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo,
        @Param("startDateFrom") LocalDate startDateFrom,
        @Param("startDateTo") LocalDate startDateTo,
        Pageable pageable);
}
