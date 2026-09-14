-- ============================================================
--  Family planning module — CPN, Phase 4/5
--  Table: OH_FAMILYPLANNING
-- ============================================================

CREATE TABLE IF NOT EXISTS OH_FAMILYPLANNING (
    FPL_ID                      INT AUTO_INCREMENT PRIMARY KEY,
    FPL_PAT_ID                  INT          NOT NULL,
    FPL_DATE                    DATE         NOT NULL,
    FPL_METHOD                  VARCHAR(20)  NOT NULL,
    FPL_REASON                  VARCHAR(20)  NOT NULL,
    FPL_PREVIOUS_METHOD         VARCHAR(20)  DEFAULT NULL,
    FPL_SIDE_EFFECTS            VARCHAR(255) DEFAULT NULL,
    FPL_COUNSELING              TINYINT(1)   NOT NULL DEFAULT 0,
    FPL_PARITY                  INT          DEFAULT NULL,
    FPL_NEXT_APPOINTMENT        DATE         DEFAULT NULL,
    FPL_NOTE                    VARCHAR(255) DEFAULT NULL,
    FPL_CREATED_BY              VARCHAR(50)  DEFAULT NULL,
    FPL_CREATED_DATE            DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FPL_LAST_MODIFIED_BY        VARCHAR(50)  DEFAULT NULL,
    FPL_LAST_MODIFIED_DATE      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FPL_ACTIVE                  TINYINT(1)   NOT NULL DEFAULT 1,

    CONSTRAINT FK_FAMILYPLANNING_PATIENT FOREIGN KEY (FPL_PAT_ID) REFERENCES OH_PATIENT(PAT_ID),
    INDEX IDX_FPL_PAT_ID (FPL_PAT_ID),
    INDEX IDX_FPL_DATE (FPL_DATE)
) ENGINE = INNODB DEFAULT CHARACTER SET utf8;

-- ============================================================
-- ADD FAMILY PLANNING MENU ITEM
-- ============================================================
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT
    'familyplanning',
    'angal.menu.btn.familyplanning',
    'angal.menu.familyplanning',
    'x',
    'F',
    'main',
    'org.isf.familyplanning.gui.FamilyPlanningBrowser',
    'N',
    10
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'familyplanning'
);

-- =========================
-- GROUP MENU
-- =========================
INSERT INTO oh_groupmenu
(GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT
    (SELECT COALESCE(MAX(GM_ID), 0) + 1 FROM oh_groupmenu),
    'admin',
    'familyplanning',
    1,
    NULL,
    NULL,
    NULL,
    NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu WHERE GM_MNI_ID_A = 'familyplanning' AND GM_UG_ID_A = 'admin'
);
