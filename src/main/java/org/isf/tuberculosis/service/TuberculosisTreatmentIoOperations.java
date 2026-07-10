package org.isf.tuberculosis.service;

import java.time.LocalDate;
import java.util.List;

import org.isf.tuberculosis.model.Classification;
import org.isf.tuberculosis.model.DiseaseLocation;
import org.isf.tuberculosis.model.TuberculosisTreatment;
import org.isf.tuberculosis.model.TreatmentStatus;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class TuberculosisTreatmentIoOperations {

    @Autowired
    private TuberculosisTreatmentIoOperationRepository repository;

    public TuberculosisTreatment saveTreatment(TuberculosisTreatment treatment) throws OHServiceException {
        return repository.save(treatment);
    }

    public TuberculosisTreatment updateTreatment(TuberculosisTreatment treatment) throws OHServiceException {
        return repository.save(treatment);
    }

    public void deleteTreatment(TuberculosisTreatment treatment) throws OHServiceException {
        repository.delete(treatment);
    }

    public TuberculosisTreatment findTreatmentById(Integer id) throws OHServiceException {
        return repository.findById(id).orElse(null);
    }

    public List<TuberculosisTreatment> findTreatmentsByPatientCode(Integer patientCode) throws OHServiceException {
        return repository.findByPatient_Code(patientCode);
    }

    public Page<TuberculosisTreatment> findAllTreatments(Pageable pageable) throws OHServiceException {
        return repository.findAll(pageable);
    }

    public Page<TuberculosisTreatment> findTreatmentsByStatus(TreatmentStatus status, Pageable pageable) throws OHServiceException {
        return repository.findByStatus(status, pageable);
    }

    public Page<TuberculosisTreatment> findTreatmentsByFilters(
        Integer patientCode,
        TreatmentStatus status,
        Classification classification,
        DiseaseLocation diseaseLocation,
        LocalDate dateFrom,
        LocalDate dateTo,
        LocalDate startDateFrom,
        LocalDate startDateTo,
        Pageable pageable) throws OHServiceException {
        return repository.findByFilters(
            patientCode, status, classification, diseaseLocation,
            dateFrom, dateTo, startDateFrom, startDateTo, pageable);
    }
}
