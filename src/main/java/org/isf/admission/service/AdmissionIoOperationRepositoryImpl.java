/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.admission.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AdmissionIoOperationRepositoryImpl implements AdmissionIoOperationRepositoryCustom {

	private static String nativeQueryTerms = "SELECT * from OH_PATIENT as p  "
					+ " left join (select * from OH_ADMISSION where ADM_IN = 1 and ( (ADM_DELETED='N') or (ADM_DELETED is null ) ) ) as a on p.PAT_ID = a.ADM_PAT_ID "
					+ " where ( ( p.PAT_DELETED='N' ) or ( p.PAT_DELETED is null ) )"
					+ " and ( lower(concat_ws(' ', p.PAT_ID, p.PAT_SNAME, p.PAT_FNAME, p.PAT_NAME, p.PAT_NOTE, p.PAT_TAXCODE, p.PAT_CITY, p.PAT_ADDR, p.PAT_TELE)) like :param0 ) "
					+ " order by p.PAT_ID desc";

	private static String nativeQueryRanges = "SELECT * from OH_PATIENT as p  "
					+ " left join (select * from OH_ADMISSION where ADM_IN = 1 and ( (ADM_DELETED='N') or (ADM_DELETED is null ) ) ) as a on p.PAT_ID = a.ADM_PAT_ID "
					+ " where (p.PAT_ID IN (SELECT ADM_PAT_ID from OH_ADMISSION where param1))"
					+ " and ( lower(concat_ws(' ', p.PAT_ID, p.PAT_SNAME, p.PAT_FNAME, p.PAT_NAME, p.PAT_NOTE, p.PAT_TAXCODE, p.PAT_CITY, p.PAT_ADDR, p.PAT_TELE)) like :param0 ) "
					+ " order by p.PAT_ID desc";

	private static String nativeQueryCode = "SELECT * from OH_PATIENT as p  "
					+ " left join (select * from OH_ADMISSION where ADM_IN = 1 and ( (ADM_DELETED='N') or (ADM_DELETED is null ) ) order by ADM_ID desc) as a on p.PAT_ID = a.ADM_PAT_ID "
					+ " where p.PAT_ID = :param0 "
					+ " and ( ( p.PAT_DELETED='N' ) or ( p.PAT_DELETED is null ) )";

	private static final String YYYY_MM_DD = "yyyy-MM-dd";

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<AdmittedPatient> findPatientAdmissionsBySearchAndDateRanges(String searchTerms, LocalDateTime[] admissionRange,
					LocalDateTime[] dischargeRange) throws OHServiceException {
		String[] terms = getTermsToSearch(searchTerms);
		List<AdmittedPatient> admittedPatients = new ArrayList<>();
		if (terms.length == 1) {
			try {
				int code = Integer.parseInt(terms[0]);
				Query nativeQuery = this.entityManager.createNativeQuery(nativeQueryCode, "AdmittedPatient");
				nativeQuery.setParameter("param0", code);

				return parseResultSet(admittedPatients, nativeQuery);

			} catch (NumberFormatException nfe) {
				// used to see if the search parameter is a patient code (number)
			}
		}

		if ((admissionRange != null && (admissionRange[0] != null || admissionRange[1] != null)) ||
						(dischargeRange != null && (dischargeRange[0] != null || dischargeRange[1] != null))) {
			StringBuilder rangePredicate = new StringBuilder("( (ADM_DELETED='N') or (ADM_DELETED is null ) )");
			if (admissionRange != null) {

				if (admissionRange[0] != null) {
					rangePredicate.append(" and ").append("DATE(ADM_DATE_ADM) >= '").append(TimeTools.formatDateTime(admissionRange[0], YYYY_MM_DD))
									.append('\'');
				}
				if (admissionRange[1] != null) {
					rangePredicate.append(" and ").append("DATE(ADM_DATE_ADM) <= '").append(TimeTools.formatDateTime(admissionRange[1], YYYY_MM_DD))
									.append('\'');
				}
			}
			if (dischargeRange != null) {

				if (dischargeRange[0] != null) {
					rangePredicate.append(" and ").append("DATE(ADM_DATE_DIS) >= '").append(TimeTools.formatDateTime(dischargeRange[0], YYYY_MM_DD))
									.append('\'');
				}
				if (dischargeRange[1] != null) {
					rangePredicate.append(" and ").append("DATE(ADM_DATE_DIS) <= '").append(TimeTools.formatDateTime(dischargeRange[1], YYYY_MM_DD))
									.append('\'');
				}
			}
			Query nativeQuery = this.entityManager.createNativeQuery(nativeQueryRanges.replace("param1", rangePredicate.toString()), "AdmittedPatient");
			String paramTerms = like(terms);
			nativeQuery.setParameter("param0", paramTerms);

			return parseResultSet(admittedPatients, nativeQuery);

		} else {

			Query nativeQuery = this.entityManager.createNativeQuery(nativeQueryTerms, "AdmittedPatient");
			String paramTerms = like(terms);
			nativeQuery.setParameter("param0", paramTerms);

			return parseResultSet(admittedPatients, nativeQuery);
		}
	}

	private List<AdmittedPatient> parseResultSet(List<AdmittedPatient> admittedPatients, Query nativeQuery) throws OHServiceException {
		List<Object[]> results = nativeQuery.getResultList();
		results.stream().forEach(resultRecord -> {
			Patient patientRecord = (Patient) resultRecord[0];
			Admission admissionRecord = (Admission) resultRecord[1];
			admittedPatients.add(new AdmittedPatient(patientRecord, admissionRecord));
		});
		return admittedPatients;
	}

	private String like(String[] terms) {
		StringBuilder sb = new StringBuilder("%");

		// result of type "%term0%term1%...%termN%"
		for (String term : terms) {
			sb.append(term).append('%');
		}
		return sb.toString();
	}

	private String[] getTermsToSearch(String searchTerms) {
		String[] terms = {};

		if (searchTerms != null && !searchTerms.isEmpty()) {
			searchTerms = searchTerms.trim().toLowerCase();
			terms = searchTerms.split(" ");
		}
		return terms;
	}

	@Override
	public Page<AdmittedPatient> findPatientAdmissionsByFilters(
		String searchTerms,
		String admissionStatus,
		List<String> wardCodes,
		LocalDateTime admissionDateFrom,
		LocalDateTime admissionDateTo,
		LocalDateTime dischargeDateFrom,
		LocalDateTime dischargeDateTo,
		Integer ageFrom,
		Integer ageTo,
		Character sex,
		Integer country,
		Integer partner,
		Pageable pageable) throws OHServiceException {

		boolean admitted = "admitted".equals(admissionStatus);
		boolean notAdmitted = "notAdmitted".equals(admissionStatus);

		StringBuilder where = new StringBuilder();
		where.append(" WHERE ((p.PAT_DELETED='N') OR (p.PAT_DELETED IS NULL))");

		String[] terms = getTermsToSearch(searchTerms);
		String paramTerms = like(terms);
		where.append(" AND (lower(concat_ws(' ', p.PAT_ID, p.PAT_SNAME, p.PAT_FNAME,")
			.append(" p.PAT_NAME, p.PAT_NOTE, p.PAT_TAXCODE, p.PAT_CITY,")
			.append(" p.PAT_ADDR, p.PAT_TELE)) LIKE :search)");

		if (sex != null) {
			where.append(" AND p.PAT_SEX = :sex");
		}

		if (country != null) {
			where.append(" AND c.CNT_ID = :country");
		}

		if (partner != null) {
			where.append(" AND p.PAT_ID IN (SELECT pp.PP_PAT_ID FROM OH_PATIENT_PARTNERS pp WHERE pp.PP_PRT_ID = :partner)");
		}

		if (ageFrom != null) {
			where.append(" AND p.PAT_BDATE IS NOT NULL")
				.append(" AND TIMESTAMPDIFF(YEAR, p.PAT_BDATE, CURDATE()) >= :ageFrom");
		}

		if (ageTo != null) {
			where.append(" AND p.PAT_BDATE IS NOT NULL")
				.append(" AND TIMESTAMPDIFF(YEAR, p.PAT_BDATE, CURDATE()) <= :ageTo");
		}

		if (admitted) {
			where.append(" AND a.ADM_ID IS NOT NULL");
		} else if (notAdmitted) {
			where.append(" AND a.ADM_ID IS NULL");
		}

		if (wardCodes != null && !wardCodes.isEmpty() && !notAdmitted) {
			where.append(" AND a.ADM_WRD_ID_A IN (:wardCodes)");
		}

		if (admissionDateFrom != null) {
			where.append(" AND DATE(a.ADM_DATE_ADM) >= '")
				.append(TimeTools.formatDateTime(admissionDateFrom, "yyyy-MM-dd")).append("'");
		}

		if (admissionDateTo != null) {
			where.append(" AND DATE(a.ADM_DATE_ADM) <= '")
				.append(TimeTools.formatDateTime(admissionDateTo, "yyyy-MM-dd")).append("'");
		}

		if (dischargeDateFrom != null) {
			where.append(" AND DATE(a.ADM_DATE_DIS) >= '")
				.append(TimeTools.formatDateTime(dischargeDateFrom, "yyyy-MM-dd")).append("'");
		}
		if (dischargeDateTo != null) {
			where.append(" AND DATE(a.ADM_DATE_DIS) <= '")
				.append(TimeTools.formatDateTime(dischargeDateTo, "yyyy-MM-dd")).append("'");
		}

		String from = " FROM OH_PATIENT as p"
			+ " LEFT JOIN (SELECT * FROM OH_ADMISSION WHERE ADM_IN = 1"
			+ "   AND ((ADM_DELETED='N') OR (ADM_DELETED IS NULL))) as a"
			+ "   ON p.PAT_ID = a.ADM_PAT_ID"
			+ " LEFT JOIN OH_COUNTRY as c"
			+ "   ON p.PAT_COUNTRY_ID = c.CNT_ID";

		String dataSql = "SELECT *" + from + where + " ORDER BY p.PAT_ID DESC";
		Query dataQuery = entityManager.createNativeQuery(dataSql, "AdmittedPatient");
		dataQuery.setParameter("search", paramTerms);
		if (sex != null)    dataQuery.setParameter("sex", String.valueOf(sex));
		if (country != null)  dataQuery.setParameter("country", country);
		if (partner != null)   dataQuery.setParameter("partner", partner);
		if (ageFrom != null) dataQuery.setParameter("ageFrom", ageFrom);
		if (ageTo != null)   dataQuery.setParameter("ageTo", ageTo);
		if (wardCodes != null && !wardCodes.isEmpty() && !notAdmitted) {
			dataQuery.setParameter("wardCodes", new ArrayList<>(wardCodes));
		}
		dataQuery.setFirstResult((int) pageable.getOffset());
		dataQuery.setMaxResults(pageable.getPageSize());

		String countSql = "SELECT COUNT(*)" + from + where;
		Query countQuery = entityManager.createNativeQuery(countSql);
		countQuery.setParameter("search", paramTerms);
		if (sex != null)    countQuery.setParameter("sex", String.valueOf(sex));
		if (country != null)  countQuery.setParameter("country", country);
		if (partner != null) countQuery.setParameter("partner", partner);
		if (ageFrom != null) countQuery.setParameter("ageFrom", ageFrom);
		if (ageTo != null)   countQuery.setParameter("ageTo", ageTo);
		if (wardCodes != null && !wardCodes.isEmpty() && !notAdmitted) {
			countQuery.setParameter("wardCodes", new ArrayList<>(wardCodes));
		}
		long total = ((Number) countQuery.getSingleResult()).longValue();

		List<AdmittedPatient> admittedPatients = new ArrayList<>();
		dataQuery.getResultList().forEach(record -> {
			Object[] row = (Object[]) record;
			Patient p = (Patient) row[0];
			if (p.getCountry() != null){
				p.getCountry().getName();
			}
			admittedPatients.add(new AdmittedPatient(p, (Admission) row[1]));
		});

		return new PageImpl<>(admittedPatients, pageable, total);
	}
}
