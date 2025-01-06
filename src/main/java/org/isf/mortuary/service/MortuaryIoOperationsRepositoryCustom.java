package org.isf.mortuary.service;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.mortuary.model.Mortuary;
import org.springframework.stereotype.Repository;

@Repository
public interface MortuaryIoOperationsRepositoryCustom {

	List<Mortuary> findAllWhereData(String patientName, String provenance, LocalDateTime dateFrom, LocalDateTime dateTo,String deathReason);
}
