package org.isf.reductionplan.service;

import java.util.List;

import org.isf.reductionplan.model.ReductionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReductionplanIoOperationRepository  extends JpaRepository<ReductionPlan, Integer> {
	List<ReductionPlan> findByDescription(String description);
//	List<ReductionPlan> getAll();
	List<ReductionPlan> findByIdIn(List<Integer> ids);


}



