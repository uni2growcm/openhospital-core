-- ========================================================================
-- SCRIPT : step_a159_familyplanning.sql
-- OBJECTIVE : Create tables for the Family Planning module
--             and add corresponding access privileges
-- ========================================================================

-- ========================================================================
-- Table OH_FAMILYPLANNING
-- Description : Main family planning record
-- ========================================================================
CREATE TABLE IF NOT EXISTS OH_FAMILYPLANNING (
                                                 FP_ID               INT(11)       NOT NULL AUTO_INCREMENT,
    FP_PAT_ID           INT(11)       NOT NULL,
    FP_METHOD           VARCHAR(30)   NOT NULL,
    FP_START_DATE       DATE          NOT NULL,
    FP_END_DATE         DATE          DEFAULT NULL,
    FP_STATUS           VARCHAR(30)   NOT NULL DEFAULT 'ACTIVE',
    FP_STOP_REASON      LONGTEXT      DEFAULT NULL,
    FP_NEXT_APP_DATE    DATE          DEFAULT NULL,
    FP_NOTES            LONGTEXT      DEFAULT NULL,
    FP_CREATED_BY       VARCHAR(50)   NOT NULL,
    FP_CREATED_DATE     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FP_LAST_MODIFIED_BY VARCHAR(50)   DEFAULT NULL,
    FP_LAST_MODIFIED_DATE DATETIME    DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    FP_ACTIVE           BOOLEAN       DEFAULT TRUE,
    FP_LOCK             INT(11)       DEFAULT 0,
    PRIMARY KEY (FP_ID),
    CONSTRAINT FK_FAMILYPLANNING_PATIENT FOREIGN KEY (FP_PAT_ID) REFERENCES OH_PATIENT(PAT_ID) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_familyplanning_patient (FP_PAT_ID),
    INDEX idx_familyplanning_status (FP_STATUS),
    INDEX idx_familyplanning_method (FP_METHOD),
    INDEX idx_familyplanning_startdate (FP_START_DATE)
    ) ENGINE = INNODB;

-- ========================================================================
-- Table OH_FAMILYPLANNINGVISIT
-- Description : Family planning follow-up visits
-- ========================================================================
CREATE TABLE IF NOT EXISTS OH_FAMILYPLANNINGVISIT (
                                                      FPV_ID              INT(11)       NOT NULL AUTO_INCREMENT,
    FPV_FP_ID           INT(11)       NOT NULL,
    FPV_VISIT_DATE      DATETIME      NOT NULL,
    FPV_VISIT_TYPE      VARCHAR(30)   NOT NULL,
    FPV_COMPLAINTS      LONGTEXT      DEFAULT NULL,
    FPV_NOTES           LONGTEXT      DEFAULT NULL,
    FPV_NEXT_APP_DATE   DATE          DEFAULT NULL,
    FPV_CREATED_BY      VARCHAR(50)   NOT NULL,
    FPV_CREATED_DATE    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FPV_LAST_MODIFIED_BY VARCHAR(50)  DEFAULT NULL,
    FPV_LAST_MODIFIED_DATE DATETIME   DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    FPV_ACTIVE          BOOLEAN       DEFAULT TRUE,
    FPV_LOCK            INT(11)       DEFAULT 0,
    PRIMARY KEY (FPV_ID),
    CONSTRAINT FK_FP_VISIT_FAMILYPLANNING FOREIGN KEY (FPV_FP_ID) REFERENCES OH_FAMILYPLANNING(FP_ID) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_fpvisit_familyplanning (FPV_FP_ID),
    INDEX idx_fpvisit_date (FPV_VISIT_DATE),
    INDEX idx_fpvisit_type (FPV_VISIT_TYPE)
    ) ENGINE = INNODB;

-- ========================================================================
-- CUSTOM MENU : Internal actions for the Family Planning module
-- These entries are hidden (MNI_CLASS = '') and used for permission
-- management via OH_GROUPMENU.
-- ========================================================================
INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'familyplanning.new', 'angal.maternity.familyplanning.new.btn', 'angal.maternity.familyplanning.new.btn', 'x', '', 'familyplanning_internal', '', 'N', 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'familyplanning.new');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'familyplanning.update', 'angal.maternity.familyplanning.update.btn', 'angal.maternity.familyplanning.update.btn', 'x', '', 'familyplanning_internal', '', 'N', 2
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'familyplanning.update');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'familyplanning.delete', 'angal.maternity.familyplanning.delete.btn', 'angal.maternity.familyplanning.delete.btn', 'x', '', 'familyplanning_internal', '', 'N', 3
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'familyplanning.delete');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'familyplanning.newvisit', 'angal.maternity.familyplanning.newvisit.btn', 'angal.maternity.familyplanning.newvisit.btn', 'x', '', 'familyplanning_internal', '', 'N', 4
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'familyplanning.newvisit');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'familyplanning.updatevisit', 'angal.maternity.familyplanning.updatevisit.btn', 'angal.maternity.familyplanning.updatevisit.btn', 'x', '', 'familyplanning_internal', '', 'N', 5
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'familyplanning.updatevisit');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'familyplanning.deletevisit', 'angal.maternity.familyplanning.deletevisit.btn', 'angal.maternity.familyplanning.deletevisit.btn', 'x', '', 'familyplanning_internal', '', 'N', 6
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'familyplanning.deletevisit');

-- ========================================================================
-- PRIVILEGES : Grant permissions to admin group
-- ========================================================================
INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'familyplanning.new', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'familyplanning.new');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'familyplanning.update', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'familyplanning.update');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'familyplanning.delete', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'familyplanning.delete');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'familyplanning.newvisit', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'familyplanning.newvisit');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'familyplanning.updatevisit', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'familyplanning.updatevisit');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'familyplanning.deletevisit', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'familyplanning.deletevisit');