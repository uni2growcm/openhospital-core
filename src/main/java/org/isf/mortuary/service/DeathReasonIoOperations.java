package org.isf.mortuary.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.model.Mortuary;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DeathReasonIoOperations {

	private static DeathReasonRepository deathReasonRepository;

	public DeathReasonIoOperations() {
	}

	@Autowired
	public DeathReasonIoOperations(DeathReasonRepository deathReasonRepository) {
		this.deathReasonRepository = deathReasonRepository;
	}

	public List<DeathReason> getAll() throws OHServiceException {
		return deathReasonRepository.findAll();
	}

	public Optional<DeathReason> getById(int id) throws OHServiceException{
		return deathReasonRepository.findById(id);
	}
}

