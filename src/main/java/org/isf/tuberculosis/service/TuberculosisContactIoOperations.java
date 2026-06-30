package org.isf.tuberculosis.service;

import java.util.List;

import org.isf.tuberculosis.model.TuberculosisContact;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class TuberculosisContactIoOperations {

    @Autowired
    private TuberculosisContactIoOperationRepository repository;

    public TuberculosisContact saveContact(TuberculosisContact contact) throws OHServiceException {
        return repository.save(contact);
    }

    public TuberculosisContact updateContact(TuberculosisContact contact) throws OHServiceException {
        return repository.save(contact);
    }

    public void deleteContact(TuberculosisContact contact) throws OHServiceException {
        repository.delete(contact);
    }

    public TuberculosisContact findContactById(Integer id) throws OHServiceException {
        return repository.findById(id).orElse(null);
    }

    public List<TuberculosisContact> findContactsByTreatmentId(Integer treatmentId) throws OHServiceException {
        return repository.findByTreatment_Id(treatmentId);
    }
}
