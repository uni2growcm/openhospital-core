package org.isf.tuberculosis.service;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.tuberculosis.model.TuberculosisVisit;
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
public class TuberculosisVisitIoOperations {

    @Autowired
    private TuberculosisVisitIoOperationRepository repository;

    public TuberculosisVisit saveVisit(TuberculosisVisit visit) throws OHServiceException {
        return repository.save(visit);
    }

    public TuberculosisVisit updateVisit(TuberculosisVisit visit) throws OHServiceException {
        return repository.save(visit);
    }

    public void deleteVisit(TuberculosisVisit visit) throws OHServiceException {
        repository.delete(visit);
    }

    public TuberculosisVisit findVisitById(Integer id) throws OHServiceException {
        return repository.findById(id).orElse(null);
    }

    public List<TuberculosisVisit> findVisitsByTreatmentId(Integer treatmentId) throws OHServiceException {
        return repository.findByTreatment_IdOrderByVisitDateDesc(treatmentId);
    }

    public Page<TuberculosisVisit> findVisitsByTreatmentId(Integer treatmentId, Pageable pageable) throws OHServiceException {
        return repository.findByTreatment_Id(treatmentId, pageable);
    }

    public List<TuberculosisVisit> findVisitsByTreatmentIdAndDateRange(
        Integer treatmentId,
        LocalDateTime dateFrom,
        LocalDateTime dateTo) throws OHServiceException {
        return repository.findByTreatmentIdAndDateRange(treatmentId, dateFrom, dateTo);
    }
}
