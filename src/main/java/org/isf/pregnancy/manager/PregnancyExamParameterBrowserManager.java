/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.pregnancy.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.isf.generaldata.MessageBundle;
import org.isf.pregnancy.model.PregnancyExamDataType;
import org.isf.pregnancy.model.PregnancyExamParameter;
import org.isf.pregnancy.service.PregnancyExamParameterIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

/**
 * Manages the "paramètres CPN" catalog ({@link PregnancyExamParameter}) used both by the CPN visit screen
 * (which renders one input control per active parameter) and by the CPN parameters administration screen.
 */
@Component
public class PregnancyExamParameterBrowserManager {

	private final PregnancyExamParameterIoOperations ioOperations;

	public PregnancyExamParameterBrowserManager(PregnancyExamParameterIoOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	public List<PregnancyExamParameter> getPregnancyExamParameters() throws OHServiceException {
		return ioOperations.getAll();
	}

	public List<PregnancyExamParameter> getPregnancyExamParametersForVisitType(int visitType) throws OHServiceException {
		return ioOperations.getForVisitType(visitType);
	}

	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	public PregnancyExamParameter newPregnancyExamParameter(PregnancyExamParameter parameter) throws OHServiceException {
		validate(parameter, true);
		return ioOperations.saveOrUpdate(parameter);
	}

	public PregnancyExamParameter updatePregnancyExamParameter(PregnancyExamParameter parameter) throws OHServiceException {
		validate(parameter, false);
		return ioOperations.saveOrUpdate(parameter);
	}

	public void deletePregnancyExamParameter(PregnancyExamParameter parameter) throws OHServiceException {
		ioOperations.delete(parameter);
	}

	protected void validate(PregnancyExamParameter parameter, boolean insert) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (StringUtils.isEmpty(parameter.getCode())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		} else if (insert && isCodePresent(parameter.getCode())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}
		if (StringUtils.isEmpty(parameter.getDescription())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}
		if (parameter.getDataType() == PregnancyExamDataType.ENUM && parameter.getAllowedValuesList().length == 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.cpn.pleaseinsertthelistofallowedvalues.msg")));
		}
		if (parameter.getDataType() == PregnancyExamDataType.NUMERIC && parameter.getMaxValue() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.cpn.pleaseinsertthemaximumvalue.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Validates a raw value entered by a user against the {@link PregnancyExamParameter} definition
	 * (bounds for {@link PregnancyExamDataType#NUMERIC}, closed list for {@link PregnancyExamDataType#ENUM},
	 * true/false for {@link PregnancyExamDataType#BOOLEAN}). {@link PregnancyExamDataType#TEXT} accepts
	 * anything.
	 *
	 * @throws OHServiceException if the value does not respect the parameter's constraints
	 */
	public void validateOutcome(PregnancyExamParameter parameter, String outcome) throws OHServiceException {
		if (StringUtils.isEmpty(outcome)) {
			return;
		}
		List<OHExceptionMessage> errors = new ArrayList<>();
		switch (parameter.getDataType()) {
			case NUMERIC:
				try {
					double value = Double.parseDouble(outcome);
					if (value < 0 || (parameter.getMaxValue() != null && value > parameter.getMaxValue())) {
						errors.add(new OHExceptionMessage(
								MessageBundle.formatMessage("angal.cpn.thevaluemustbebetween0andmax.fmt.msg",
										parameter.getDescription(), parameter.getMaxValue())));
					}
				} catch (NumberFormatException e) {
					errors.add(new OHExceptionMessage(
							MessageBundle.formatMessage("angal.cpn.pleaseinsertavalidnumericvalue.fmt.msg", parameter.getDescription())));
				}
				break;
			case ENUM:
				boolean allowed = false;
				for (String value : parameter.getAllowedValuesList()) {
					if (value.equalsIgnoreCase(outcome)) {
						allowed = true;
						break;
					}
				}
				if (!allowed) {
					errors.add(new OHExceptionMessage(
							MessageBundle.formatMessage("angal.cpn.pleaseselectavalidvalue.fmt.msg", parameter.getDescription())));
				}
				break;
			case BOOLEAN:
				if (!"true".equalsIgnoreCase(outcome) && !"false".equalsIgnoreCase(outcome)) {
					errors.add(new OHExceptionMessage(
							MessageBundle.formatMessage("angal.cpn.pleaseselectavalidvalue.fmt.msg", parameter.getDescription())));
				}
				break;
			case TEXT:
			default:
				break;
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
