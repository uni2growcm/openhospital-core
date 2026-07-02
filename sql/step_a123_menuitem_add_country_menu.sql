-- =========================
-- COUNTRY MENU ITEM
-- =========================
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT
    'country',
    'angal.menu.btn.country',
    'angal.menu.country',
    'x',
    'C',
    'generaldata',
    'org.isf.country.gui.CountryBrowser',
    'N',
    12
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'country'
);

-- =========================
-- GROUP MENU
-- =========================
INSERT INTO oh_groupmenu
(GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT
    361, 'admin', 'country', 1, NULL, NULL, NULL, NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu WHERE GM_ID = 361
);
-- =========================
-- HOMEVISIT MENU ITEM
-- =========================
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT
    'homevisit',
    'angal.menu.btn.homevisit',
    'angal.menu.homevisit',
    'x',
    'H',
    'main',
    'org.isf.homevisit.gui.HomeVisitBrowser',
    'N',
    1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'homevisit'
);

-- =========================
-- GROUP MENU
-- =========================
INSERT INTO oh_groupmenu
(GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT
    860, 'admin', 'homevisit', 1, NULL, NULL, NULL, NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu WHERE GM_MNI_ID_A = 'homevisit' AND GM_UG_ID_A = 'admin'
);