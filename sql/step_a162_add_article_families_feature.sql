-- ============================================================
--  Table: OH_ARTICLE_FAMILIES
--  Description: Stores article families
-- ============================================================
CREATE TABLE IF NOT EXISTS OH_ARTICLE_FAMILIES (
    AFM_ID                  INT AUTO_INCREMENT PRIMARY KEY,
    AFM_CODE                VARCHAR(50)  NOT NULL UNIQUE,
    AFM_DESC                VARCHAR(255) NOT NULL,
    AFM_CREATED_BY          VARCHAR(50)  DEFAULT NULL,
    AFM_CREATED_DATE        DATETIME     DEFAULT CURRENT_TIMESTAMP,
    AFM_LAST_MODIFIED_BY    VARCHAR(50)  DEFAULT NULL,
    AFM_LAST_MODIFIED_DATE  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    AFM_ACTIVE              TINYINT(1)   NOT NULL DEFAULT 1,
    AFM_LOCK                INT          DEFAULT 0,

    INDEX IDX_AFM_CODE (AFM_CODE),
    INDEX IDX_AFM_DESC (AFM_DESC),
    INDEX IDX_AFM_ACTIVE (AFM_ACTIVE)
    ) ENGINE=InnoDB;

-- ============================================================
-- ADD ARTICLE FAMILIES MENU ITEM
-- Description: Create the Article Families menu entry if it does not exist
-- ============================================================
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT
    'articlefamilies',
    'angal.menu.btn.articlefamilies',
    'angal.menu.articlefamilies',
    'angal.menu.tooltip.articlefamilies',
    'F',
    'generaldata',
    'org.isf.articlefamily.gui.ArticleFamilyBrowser',
    'N',
    14
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'articlefamilies'
);

-- =========================
-- GROUP MENU
-- =========================
INSERT INTO oh_groupmenu
(GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT
    (SELECT COALESCE(MAX(GM_ID), 0) + 1 FROM oh_groupmenu),
    'admin',
    'articlefamilies',
    1,
    NULL,
    NULL,
    NULL,
    NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu WHERE GM_MNI_ID_A = 'articlefamilies' AND GM_UG_ID_A = 'admin'
);