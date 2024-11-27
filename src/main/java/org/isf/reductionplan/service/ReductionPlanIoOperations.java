package org.isf.reductionplan.service;

import java.util.List;
import java.util.Optional;

import org.isf.reductionplan.model.ReductionPlan;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mwithi
 */
@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class ReductionPlanIoOperations {

	private final ReductionplanIoOperationRepository repository;

	public ReductionPlanIoOperations(ReductionplanIoOperationRepository reductionplanIoOperationRepository
	) {
		this.repository = reductionplanIoOperationRepository;
	}

	/**
	 * Return the list of {@link ReductionPlan}s in the DB
	 * @return the list of {@link ReductionPlan}s
	 * @throws OHServiceException
	 */
	public List<ReductionPlan> getReductionplan() throws OHServiceException {
		return repository.findAll();
	}

	/**
	 * Return the list of {@link ReductionPlan}s in the DB
	 * @return the list of {@link ReductionPlan}s
	 * @throws OHServiceException
	 */

	public List<ReductionPlan> findByIdIn(List<Integer> ids) {
		return repository.findByIdIn(ids);
	}

	public List<ReductionPlan> getReductionPlan(String description) throws OHServiceException {

		List<ReductionPlan> reductionPlans = repository.findByDescription(description);
		return reductionPlans;
	}

	public ReductionPlan newReductionPlan(ReductionPlan reductionPlan) throws OHServiceException {
		return repository.save(reductionPlan);
	}

	public ReductionPlan updateReductionPlan(int rpId, ReductionPlan updatedReductionPlan) throws OHServiceException {

		Optional<ReductionPlan> existingPlanOpt = repository.findById(rpId);
		if (existingPlanOpt.isPresent()) {
			ReductionPlan existingPlan = existingPlanOpt.get();
			existingPlan.setDescription(updatedReductionPlan.getDescription());
			existingPlan.setOperationRate(updatedReductionPlan.getOperationRate());
			existingPlan.setMedicalRate(updatedReductionPlan.getMedicalRate());
			existingPlan.setExamRate(updatedReductionPlan.getExamRate());
			existingPlan.setOtherRate(updatedReductionPlan.getOtherRate());
			return repository.save(existingPlan);
		}
		throw new OHServiceException(new OHExceptionMessage(null));
	}

	public void deleteReductionplan(ReductionPlan reductionplan) throws OHServiceException {
		repository.delete(reductionplan);
	}

}
