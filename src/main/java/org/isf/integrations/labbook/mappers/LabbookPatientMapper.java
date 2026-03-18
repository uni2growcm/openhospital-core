package org.isf.integrations.labbook.mappers;

import org.isf.integrations.labbook.models.CreatePatientRequest;
import org.isf.patient.model.Patient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class LabbookPatientMapper {
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

	public CreatePatientRequest toCreatePatientRequest(Patient ohPatient) {
		if (ohPatient == null) {
			return null;
		}

		CreatePatientRequest labbookPatient = new CreatePatientRequest();

		labbookPatient.setPat_code(ohPatient.getCode());
		labbookPatient.setPat_name(ohPatient.getSecondName());
		labbookPatient.setPat_firstname(ohPatient.getFirstName());

		if (ohPatient.getBirthDate() != null) {
			labbookPatient.setPat_birth(ohPatient.getBirthDate().format(DATE_FORMATTER));
		}

		labbookPatient.setPat_sex(mapSexToLabbook(ohPatient.getSex()));
		labbookPatient.setPat_address(ohPatient.getAddress());
		labbookPatient.setPat_city(ohPatient.getCity());
		labbookPatient.setPat_phone1(ohPatient.getTelephone());

		mapBloodType(ohPatient.getBloodType(), labbookPatient);

		return labbookPatient;
	}

	public Patient toPatient(CreatePatientRequest labbookPatient) {
		if (labbookPatient == null) {
			return null;
		}

		Patient ohpatient = new Patient();

		ohpatient.setCode(labbookPatient.getPat_code());
		ohpatient.setSecondName(labbookPatient.getPat_name());
		ohpatient.setFirstName(labbookPatient.getPat_firstname());
		ohpatient.setSex(mapSexToPatient(labbookPatient.getPat_sex()));

		if (labbookPatient.getPat_birth() != null && !labbookPatient.getPat_birth().isEmpty()) {
			ohpatient.setBirthDate(LocalDate.parse(labbookPatient.getPat_birth(), DATE_FORMATTER));
		}

		ohpatient.setAddress(labbookPatient.getPat_address());
		ohpatient.setCity(labbookPatient.getPat_city());
		ohpatient.setTelephone(labbookPatient.getPat_phone1());
		ohpatient.setBloodType(labbookPatient.getPat_blood_group() + labbookPatient.getPat_rhesus());

		return ohpatient;
	};

	private Integer mapSexToLabbook(char ohSex) {
		return switch (ohSex) {
			case 'M' -> 1;
			case 'F' -> 2;
			default  -> 3;
		};
	}

	private char mapSexToPatient(int labbookSex) {
		return switch (labbookSex) {
			case 1 -> 'M';
			case 2 -> 'F';
			default  -> 'O';
		};
	}

	private void mapBloodType(String ohBloodType, CreatePatientRequest req) {
		if (ohBloodType == null || ohBloodType.trim().isEmpty()) {
			return;
		}

		String cleaned = ohBloodType.trim().toUpperCase();
		String patRhesus = cleaned.contains("+") ? "+" : (cleaned.contains("-") ? "-" : null);

		if (cleaned.startsWith("AB")) {
			req.setPat_blood_group("AB");
		} else if (cleaned.startsWith("B")) {
			req.setPat_blood_group("B");
		} else if (cleaned.startsWith("A")) {
			req.setPat_blood_group("A");
		} else if (cleaned.startsWith("O")) {
			req.setPat_blood_group("O");
		}
		req.setPat_rhesus(patRhesus);
	}
}
