-- ========================================================================
-- Reduction plans: partners, validity period, patient quota, priorities
-- ========================================================================
--   1. OH_REDUCTIONPLAN : optional owner partner (NULL = hospital plan),
--                         validity period (value + unit) counted from the
--                         plan creation date, maximum number of patients
--   2. OH_PARTNERS      : priority rank (1 = most prioritary), initialised
--                         from the insertion order
--   3. OH_PATIENT_REDUCTIONPLANS : a patient can now have several reduction
--                         plans (hospital plans and partner plans). Rows are
--                         never physically deleted so that the patient quota
--                         of a plan is cumulative.
--   4. Settings used to resolve collisions between plans during billing
-- ========================================================================

ALTER TABLE OH_REDUCTIONPLAN
    ADD COLUMN RP_PRT_ID INT NULL DEFAULT NULL,
    ADD COLUMN RP_VALIDITY_VALUE INT NULL DEFAULT NULL,
    ADD COLUMN RP_VALIDITY_UNIT VARCHAR(10) NULL DEFAULT NULL,
    ADD COLUMN RP_MAX_PATIENTS INT NULL DEFAULT NULL,
    ADD CONSTRAINT FK_REDUCTIONPLAN_PRT_ID FOREIGN KEY (RP_PRT_ID) REFERENCES OH_PARTNERS (PRT_ID);

-- the validity period is counted from the creation date: make sure it is set
UPDATE OH_REDUCTIONPLAN SET RP_CREATED_DATE = NOW() WHERE RP_CREATED_DATE IS NULL;

ALTER TABLE OH_PARTNERS
    ADD COLUMN PRT_PRIORITY INT NOT NULL DEFAULT 0;

-- default priority: the first partner added is the most prioritary
SET @prt_rank = 0;
UPDATE OH_PARTNERS SET PRT_PRIORITY = (@prt_rank := @prt_rank + 1) ORDER BY PRT_ID;

CREATE TABLE IF NOT EXISTS OH_PATIENT_REDUCTIONPLANS (
    PRP_ID                  INT AUTO_INCREMENT PRIMARY KEY,
    PRP_PAT_ID              INT NOT NULL,
    PRP_RP_ID               INT NOT NULL,
    PRP_CREATED_BY          VARCHAR(50) DEFAULT NULL,
    PRP_CREATED_DATE        DATETIME    DEFAULT NULL,
    PRP_LAST_MODIFIED_BY    VARCHAR(50) DEFAULT NULL,
    PRP_LAST_MODIFIED_DATE  DATETIME    DEFAULT NULL,
    PRP_ACTIVE              TINYINT(1)  NOT NULL DEFAULT 1,
    PRP_LOCK                INT         NOT NULL DEFAULT 0,
    UNIQUE KEY UK_PATIENT_REDUCTIONPLAN (PRP_PAT_ID, PRP_RP_ID),
    CONSTRAINT FK_PATIENT_REDUCTIONPLANS_PAT_ID FOREIGN KEY (PRP_PAT_ID) REFERENCES OH_PATIENT (PAT_ID),
    CONSTRAINT FK_PATIENT_REDUCTIONPLANS_RP_ID FOREIGN KEY (PRP_RP_ID) REFERENCES OH_REDUCTIONPLAN (RP_ID)
) ENGINE=InnoDB;

-- migrate the single reduction plan previously stored on the patient
INSERT INTO OH_PATIENT_REDUCTIONPLANS (PRP_PAT_ID, PRP_RP_ID, PRP_CREATED_DATE, PRP_ACTIVE)
SELECT P.PAT_ID, P.PAT_RP_ID, NOW(), 1
FROM OH_PATIENT P
WHERE P.PAT_RP_ID IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM OH_PATIENT_REDUCTIONPLANS X
      WHERE X.PRP_PAT_ID = P.PAT_ID AND X.PRP_RP_ID = P.PAT_RP_ID
  );

-- ------------------------------------------------------------------------
-- Settings
-- ------------------------------------------------------------------------
INSERT INTO OH_SETTINGS
    (SETT_CODE, SETT_CATEGORY, SETT_VALUE_TYPE, SETT_DEFAULT_VALUE, SETT_VALUE, SETT_DESCRIPTION, SETT_ACTIVE, SETT_NEED_RESTART)
SELECT 'REDUCTIONPLANHIGHESTRATEFIRST', 'accounting', 'bool', 'TRUE', 'TRUE',
       'When several reduction plans apply to the same item: TRUE = apply the highest reduction rate, FALSE = apply the lowest one',
       1, 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM OH_SETTINGS WHERE SETT_CODE = 'REDUCTIONPLANHIGHESTRATEFIRST');

INSERT INTO OH_SETTINGS
    (SETT_CODE, SETT_CATEGORY, SETT_VALUE_TYPE, SETT_DEFAULT_VALUE, SETT_VALUE, SETT_DESCRIPTION, SETT_ACTIVE, SETT_NEED_RESTART)
SELECT 'REDUCTIONPLANPARTNERFIRST', 'accounting', 'bool', 'TRUE', 'TRUE',
       'When several reduction plans give the same rate on the same item: TRUE = prefer partner plans, FALSE = prefer hospital plans',
       1, 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM OH_SETTINGS WHERE SETT_CODE = 'REDUCTIONPLANPARTNERFIRST');

INSERT INTO OH_SETTINGS
    (SETT_CODE, SETT_CATEGORY, SETT_VALUE_TYPE, SETT_DEFAULT_VALUE, SETT_VALUE, SETT_DESCRIPTION, SETT_ACTIVE, SETT_NEED_RESTART)
SELECT 'REDUCTIONPLANMOSTPRIORITARYPARTNERFIRST', 'accounting', 'bool', 'TRUE', 'TRUE',
       'When several partner reduction plans give the same rate on the same item: TRUE = prefer the most prioritary partner, FALSE = prefer the least prioritary one',
       1, 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM OH_SETTINGS WHERE SETT_CODE = 'REDUCTIONPLANMOSTPRIORITARYPARTNERFIRST');
