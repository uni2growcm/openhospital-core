-- ============================================================
--  Table: OH_ARTICLE_FAMILIES
--  Description: Stores article (medical) families
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
) ENGINE = InnoDB;

-- ============================================================
--  Link the medicals table to the article families table
-- ============================================================
ALTER TABLE OH_MEDICALDSR
    ADD COLUMN IF NOT EXISTS MDSR_AFM_ID INT DEFAULT NULL,
    ADD CONSTRAINT FK_MEDICAL_ARTICLE_FAMILY FOREIGN KEY (MDSR_AFM_ID) REFERENCES OH_ARTICLE_FAMILIES(AFM_ID);

CREATE INDEX IF NOT EXISTS IDX_MEDICALDSR_AFM_ID ON OH_MEDICALDSR(MDSR_AFM_ID);

-- ============================================================
--  ADD ARTICLE FAMILIES MENU ITEM (under the "types" submenu, alongside
--  the other article/lookup type browsers such as Medical Types)
-- ============================================================
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT
    'articlefamilies',
    'angal.menu.btn.articlefamilies',
    'angal.menu.articlefamilies',
    'x',
    'F',
    'types',
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
