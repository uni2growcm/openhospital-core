package org.isf.mortuary.manager;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.mortuary.model.Mortuary;
import org.isf.mortuary.service.MortuaryIoOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.springframework.stereotype.Component;

@Component
public class MortuaryBrowserManager {

	private final MortuaryIoOperations mortuaryIoOperations;

	public MortuaryBrowserManager(MortuaryIoOperations mortuaryIoOperations) {
		this.mortuaryIoOperations = mortuaryIoOperations;
	}
	public Mortuary add(Mortuary Mortuary) throws OHException {
		return mortuaryIoOperations.add(Mortuary);
	}

	public List<Mortuary> getAll() throws OHException{
		return mortuaryIoOperations.getAll();
	}

	public Mortuary update(Mortuary mortuary) throws OHException {
		return mortuaryIoOperations.update(mortuary);
	}

	public void delete(Mortuary mortuary) throws OHException {
		mortuaryIoOperations.delete(mortuary);
	}

	public List<Mortuary> getMortuariesWhereData(String patientName) {
		return mortuaryIoOperations.getMortuariesWhereData(patientName);
	}

	public List<Mortuary> getMortuariesWhereData(
		String patientName,
		String provenance,
		LocalDateTime dateFrom,
		LocalDateTime dateTo,
		String deathReason
	) {
		return mortuaryIoOperations.getMortuariesWhereData(patientName, provenance, TimeTools.truncateToSeconds(dateFrom),TimeTools.truncateToSeconds(dateFrom), deathReason);
	}
}
