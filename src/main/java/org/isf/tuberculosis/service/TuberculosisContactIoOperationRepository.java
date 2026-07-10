package org.isf.tuberculosis.service;

import java.util.List;

import org.isf.tuberculosis.model.TuberculosisContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TuberculosisContactIoOperationRepository extends JpaRepository<TuberculosisContact, Integer> {

    List<TuberculosisContact> findByTreatment_Id(Integer treatmentId);
}
