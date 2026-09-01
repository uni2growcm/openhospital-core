-- ============================================================
--  Table: OH_STOCKORDER
--  Description: Product order forms ("fiches de commande") that can later be
--  converted into a stock-in (charging) movement.
-- ============================================================
CREATE TABLE IF NOT EXISTS OH_STOCKORDER (
    SO_ID                    INT AUTO_INCREMENT PRIMARY KEY,
    SO_REFNO                 VARCHAR(50)  NOT NULL UNIQUE,
    SO_DATE                  DATETIME     NOT NULL,
    SO_SUP_ID                INT          DEFAULT NULL,
    SO_MMVT_ID_A             VARCHAR(10)  DEFAULT NULL,
    SO_STATUS                VARCHAR(10)  NOT NULL DEFAULT 'open',
    SO_CREATED_BY             VARCHAR(50)  DEFAULT NULL,
    SO_CREATED_DATE           DATETIME     DEFAULT CURRENT_TIMESTAMP,
    SO_LAST_MODIFIED_BY       VARCHAR(50)  DEFAULT NULL,
    SO_LAST_MODIFIED_DATE     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    SO_ACTIVE                TINYINT(1)   NOT NULL DEFAULT 1,
    SO_LOCK                  INT          DEFAULT 0,

    CONSTRAINT FK_STOCKORDER_SUPPLIER FOREIGN KEY (SO_SUP_ID) REFERENCES OH_SUPPLIER(SUP_ID),
    CONSTRAINT FK_STOCKORDER_MOVTYPE FOREIGN KEY (SO_MMVT_ID_A) REFERENCES OH_MEDICALDSRSTOCKMOVTYPE(MMVT_ID_A),
    INDEX IDX_SO_REFNO (SO_REFNO),
    INDEX IDX_SO_STATUS (SO_STATUS)
) ENGINE = INNODB DEFAULT CHARACTER SET utf8;

-- ============================================================
--  Table: OH_STOCKORDER_ROW
--  Description: Line items (medical + quantity) of a stock order.
-- ============================================================
CREATE TABLE IF NOT EXISTS OH_STOCKORDER_ROW (
    SOR_ID                   INT AUTO_INCREMENT PRIMARY KEY,
    SOR_SO_ID                INT          NOT NULL,
    SOR_MDSR_ID              INT          NOT NULL,
    SOR_QTY                  INT          NOT NULL,
    SOR_LOCK                 INT          DEFAULT 0,

    CONSTRAINT FK_STOCKORDERROW_ORDER FOREIGN KEY (SOR_SO_ID) REFERENCES OH_STOCKORDER(SO_ID),
    CONSTRAINT FK_STOCKORDERROW_MEDICAL FOREIGN KEY (SOR_MDSR_ID) REFERENCES OH_MEDICALDSR(MDSR_ID),
    INDEX IDX_SOR_SO_ID (SOR_SO_ID)
) ENGINE = INNODB DEFAULT CHARACTER SET utf8;

-- ============================================================
-- ADD STOCK ORDER MENU ITEM
-- ============================================================
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT
    'stockorder',
    'angal.menu.btn.stockorder',
    'angal.menu.stockorder',
    'x',
    'O',
    'pharmacy',
    'org.isf.stockorder.gui.StockOrderBrowser',
    'N',
    7
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'stockorder'
);

-- =========================
-- GROUP MENU
-- =========================
INSERT INTO oh_groupmenu
(GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT
    (SELECT COALESCE(MAX(GM_ID), 0) + 1 FROM oh_groupmenu),
    'admin',
    'stockorder',
    1,
    NULL,
    NULL,
    NULL,
    NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu WHERE GM_MNI_ID_A = 'stockorder' AND GM_UG_ID_A = 'admin'
);
