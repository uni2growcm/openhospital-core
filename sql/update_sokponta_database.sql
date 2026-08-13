source step_a130_update_patient_table.sql;
source step_a131_update_admission_table.sql;
source step_a132_update_patient_age_units.sql;
source step_a133_add_town_ethnic_municipality.sql;
source step_a134_update_medical_history_table.sql;
source step_a135_update_conditioning_table.sql;
source step_a136_add_referral_fields.sql;
source step_a137_add_cares_table_and_add_care_permission.sql;
source step_a138_create_table_oh_hospitalizationconsultation.sql;
source step_a139_create_table_oh_diagnosisout.sql;
source step_a140_create_table_oh_diagnosisin.sql;
source step_a141_update_table_patientexamation.sql;

INSERT INTO OH_DISCHARGETYPE (DIST_ID_A, DIST_DESC, DIST_CREATED_BY, DIST_CREATED_DATE, DIST_LAST_MODIFIED_BY, DIST_LAST_MODIFIED_DATE, DIST_ACTIVE) VALUES ('SCAM','SORTIE CONTRE AVIS MEDICAL',NULL,NULL,NULL,NULL,1);

INSERT INTO OH_INDIAGNOSIS (ID_ADM_ID, ID_DIS_ID_A)
SELECT ADM_ID, ADM_IN_DIS_ID_A
FROM OH_ADMISSION
WHERE ADM_IN_DIS_ID_A IS NOT NULL;

INSERT INTO OH_OUTDIAGNOSIS (OD_ADM_ID, OD_DIS_ID_A)
SELECT ADM_ID, ADM_OUT_DIS_ID_A
FROM OH_ADMISSION
WHERE ADM_OUT_DIS_ID_A IS NOT NULL;

INSERT INTO OH_OUTDIAGNOSIS (OD_ADM_ID, OD_DIS_ID_A)
SELECT ADM_ID, ADM_OUT_DIS_ID_A_2
FROM OH_ADMISSION
WHERE ADM_OUT_DIS_ID_A_2 IS NOT NULL;

INSERT INTO OH_OUTDIAGNOSIS (OD_ADM_ID, OD_DIS_ID_A)
SELECT ADM_ID, ADM_OUT_DIS_ID_A_3
FROM OH_ADMISSION
WHERE ADM_OUT_DIS_ID_A_3 IS NOT NULL;