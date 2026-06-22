-- =========================
-- TYPOLOGY MENU (UNIFIED)
-- =========================

INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT
    'typologies',
    'angal.menu.btn.typologies',
    'angal.menu.typologies',
    'angal.menu.typologies.tooltip',
    'T',
    'types',
    'org.isf.typology.TypologyBrowser',
    'N',
    14
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'typologies'
);

-- =========================
-- GROUP MENU
-- =========================

INSERT INTO oh_groupmenu
(GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT
    360, 'admin', 'typologies', 1, NULL, NULL, NULL, NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu WHERE GM_ID = 360
);
