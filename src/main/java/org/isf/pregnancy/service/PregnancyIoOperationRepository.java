package org.isf.pregnancy.service;

import org.isf.admission.model.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PregnancyIoOperationRepository extends JpaRepository<Admission, Integer>, PregnancyIoOperationRepositoryCustom {

}
