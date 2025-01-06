package org.isf.mortuary.manager;

import java.util.List;

import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.service.DeathReasonIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

@Component
public class DeathReasonManager {
	private final DeathReasonIoOperations deathReasonIoOperations;

	public DeathReasonManager(DeathReasonIoOperations deathReasonIoOperations) {
		this.deathReasonIoOperations = deathReasonIoOperations;
	}

	public List<DeathReason> getAll() throws OHServiceException {
		return deathReasonIoOperations.getAll();
	}
}
