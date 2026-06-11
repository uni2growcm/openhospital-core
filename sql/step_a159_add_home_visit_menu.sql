-- =============================================
-- Menu: Home Visit Management
-- =============================================
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT
    'homevisit',
    'angal.menu.btn.homevisit',
    'angal.menu.homevisit',
    'angal.menu.tooltip.homevisit',
    'H',
    'opd',
    'org.isf.homevisit.gui.HomeVisitBrowser',
    'N',
    5
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'homevisit'
);

-- =========================
-- STAFF MENU ITEM
-- =========================
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT
    'staff',
    'angal.menu.btn.staff',
    'angal.menu.staff',
    'angal.menu.tooltip.staff',
    'S',
    'generaldata',
    'org.isf.homevisit.gui.StaffBrowser',
    'N',
    16
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'staff'
);

-- =========================
-- GROUP MENU
-- =========================
INSERT INTO oh_groupmenu
(GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT
    (SELECT COALESCE(MAX(GM_ID), 0) + 1 FROM oh_groupmenu),
    'admin',
    'homevisit',
    1,
    NULL,
    NULL,
    NULL,
    NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu WHERE GM_MNI_ID_A = 'homevisit' AND GM_UG_ID_A = 'admin'
);

INSERT INTO oh_groupmenu
(GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT
    (SELECT COALESCE(MAX(GM_ID), 0) + 1 FROM oh_groupmenu),
    'admin',
    'staff',
    1,
    NULL,
    NULL,
    NULL,
    NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu WHERE GM_MNI_ID_A = 'staff' AND GM_UG_ID_A = 'admin'
);

INSERT INTO oh_groupmenu (GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT (SELECT COALESCE(MAX(GM_ID), 0) + 1 FROM oh_groupmenu), 'admin', 'homevisit', 1
    WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'homevisit');