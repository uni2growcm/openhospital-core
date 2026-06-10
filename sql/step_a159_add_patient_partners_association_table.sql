-- =============================================
-- Table: OH_PATIENT_PARTNERS
-- Description: Association between patients and partners
-- =============================================
CREATE TABLE IF NOT EXISTS OH_PATIENT_PARTNERS (
    PP_PAT_ID              INT NOT NULL,
    PP_PRT_ID              INT NOT NULL,
    PRIMARY KEY (PP_PAT_ID, PP_PRT_ID),
    FOREIGN KEY (PP_PAT_ID) REFERENCES OH_PATIENT(PAT_ID),
    FOREIGN KEY (PP_PRT_ID) REFERENCES OH_PARTNERS(PRT_ID)
    ) ENGINE=InnoDB;