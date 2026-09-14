--
-- Add indexes on columns used by the paginated admission/opd/patient filter queries.
-- ADM_WRD_ID_A, ADM_PAT_ID, OPD_WRD_ID_A, OPD_DIS_ID_A and OPD_USR_ID_A are already indexed
-- (added by earlier steps / foreign key constraints); only the genuinely missing ones are added here.
--

ALTER TABLE OH_ADMISSION
	ADD INDEX ADM_IN_idx (ADM_IN ASC);

ALTER TABLE OH_PATIENT
	ADD INDEX PAT_AGE_idx (PAT_AGE ASC),
	ADD INDEX PAT_SEX_idx (PAT_SEX ASC);

ALTER TABLE OH_OPD
	ADD INDEX OPD_DATE_idx (OPD_DATE ASC),
	ADD INDEX OPD_AGE_idx (OPD_AGE ASC),
	ADD INDEX OPD_SEX_idx (OPD_SEX ASC),
	ADD INDEX OPD_NEW_PAT_idx (OPD_NEW_PAT ASC);
