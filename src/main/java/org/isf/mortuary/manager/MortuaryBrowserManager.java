package org.isf.mortuary.manager;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.mortuary.model.Mortuary;
import org.isf.mortuary.service.MortuaryIoOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.springframework.beans.factory.annotation.Autowired;
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

//	public List<Mortuary> getMortuariesWhereData(
//		String patientName,
//		String deathReason,
//		String ward,
//		LocalDateTime movFrom,
//		LocalDateTime movTo
//	) throws OHServiceException {
//		return mortuaryIoOperations.getMortuariesWhereData(patientName,
//			deathReason,
//			ward,
//			TimeTools.truncateToSeconds(movFrom),
//			TimeTools.truncateToSeconds(movTo)
//		);
//	}
}
