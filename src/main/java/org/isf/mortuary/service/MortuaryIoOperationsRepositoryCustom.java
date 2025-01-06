package org.isf.mortuary.service;

import java.util.List;

import org.isf.mortuary.model.Mortuary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MortuaryIoOperationsRepositoryCustom {

	List<Mortuary> findAllWithData();
}
