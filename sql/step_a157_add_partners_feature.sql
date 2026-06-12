-- ============================================================
--  Table: OH_PARTNERS
-- Description: Stores external partners information
-- ============================================================
CREATE TABLE IF NOT EXISTS OH_PARTNERS (
    PRT_ID                  INT AUTO_INCREMENT PRIMARY KEY,
    PRT_NAME                VARCHAR(100) NOT NULL,
    PRT_TYPE                VARCHAR(20)  NOT NULL,
    PRT_CONTACT_PERSON      VARCHAR(100),
    PRT_PHONE               VARCHAR(50),
    PRT_EMAIL               VARCHAR(100),
    PRT_ADDRESS             VARCHAR(255),
    PRT_NOTES               TEXT,
    PRT_CREATED_BY          VARCHAR(50)  DEFAULT NULL,
    PRT_CREATED_DATE        DATETIME     DEFAULT NULL,
    PRT_LAST_MODIFIED_BY    VARCHAR(50)  DEFAULT NULL,
    PRT_LAST_MODIFIED_DATE  DATETIME     DEFAULT NULL,
    PRT_ACTIVE              TINYINT(1)   NOT NULL DEFAULT 1,
    PRT_LOCK                INT          DEFAULT 0,
    KEY IDX_PARTNER_NAME    (PRT_NAME),
    FOREIGN KEY (PRT_TYPE) REFERENCES OH_TYPOLOGIES(TYPO_CODE)
)   ENGINE=InnoDB;

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
)   ENGINE=InnoDB;

-- ============================================================
-- ADD PARTNERS MENU ITEM
-- Description: Create the Partners menu entry if it does not exist
-- ============================================================
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT
    'partners',
    'angal.menu.btn.partners',
    'angal.menu.partners',
    'angal.menu.tooltip.partners',
    'P',
    'generaldata',
    'org.isf.partner.gui.PartnerBrowser',
    'N',
    13
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'partners'
);

-- =========================
-- GROUP MENU
-- Description: Associate the Partners menu with the admin group
-- =========================
INSERT INTO oh_groupmenu
(GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT
    (SELECT COALESCE(MAX(GM_ID), 0) + 1 FROM oh_groupmenu),
    'admin',
    'partners',
    1,
    NULL,
    NULL,
    NULL,
    NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu WHERE GM_MNI_ID_A = 'partners' AND GM_UG_ID_A = 'admin'
);
