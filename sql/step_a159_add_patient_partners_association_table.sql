-- =============================================
-- Table: OH_PATIENT_PARTNERS
-- Description: Association between patients and partners
-- =============================================
CREATE TABLE IF NOT EXISTS OH_PATIENT_PARTNERS (
    PP_ID                  INT AUTO_INCREMENT PRIMARY KEY,
    PP_PAT_ID              INT          NOT NULL,
    PP_PRT_ID              INT          NOT NULL,
    PP_START_DATE          DATE         NOT NULL,
    PP_END_DATE            DATE         DEFAULT NULL,
    PP_CREATED_BY          VARCHAR(50)  DEFAULT NULL,
    PP_CREATED_DATE        DATETIME     DEFAULT NULL,
    PP_LAST_MODIFIED_BY    VARCHAR(50)  DEFAULT NULL,
    PP_LAST_MODIFIED_DATE  DATETIME     DEFAULT NULL,
    PP_ACTIVE              TINYINT(1)   NOT NULL DEFAULT 1,
    PP_LOCK                INT          DEFAULT 0,
    FOREIGN KEY (PP_PAT_ID) REFERENCES OH_PATIENT(PAT_ID),
    FOREIGN KEY (PP_PRT_ID) REFERENCES OH_PARTNERS(PRT_ID),
    KEY IDX_PATIENT_PARTNER_PATIENT (PP_PAT_ID),
    KEY IDX_PATIENT_PARTNER_PARTNER (PP_PRT_ID),
    KEY IDX_PATIENT_PARTNER_DATES (PP_START_DATE, PP_END_DATE)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;