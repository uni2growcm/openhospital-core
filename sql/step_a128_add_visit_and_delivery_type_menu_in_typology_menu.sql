-- =========================
-- DELIVERY TYPES MENU
-- =========================
-- Insert menuitem if not exists

INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
    SELECT
        'pregdeliverytypes',
        'angal.menu.btn.pregdeliverytypes',
        'angal.menu.pregdeliverytypes',
        'Delivery Types',
        'D',
        'types',
        'org.isf.maternity.gui.delivery.DeliveryTypeBrowser',
        'N',
        15
    FROM DUAL
    WHERE NOT EXISTS (
        SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'pregdeliverytypes'
);

-- Insert privilege
INSERT INTO oh_groupmenu
(GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
    SELECT
        358, 'admin', 'pregdeliverytypes', 1, NULL, NULL, NULL, NULL
    FROM DUAL
    WHERE NOT EXISTS (
        SELECT 1 FROM oh_groupmenu WHERE GM_MNI_ID_A = 'pregdeliverytypes' AND GM_UG_ID_A = 'admin'
);

-- =========================
-- VISIT TYPES MENU
-- =========================

-- Insert menuitem if not exists
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT
    'pregvisittypes',
    'angal.menu.btn.pregvisittypes',
    'angal.menu.pregvisittypes',
    'Visit Types',
    'V',
    'types',
    'org.isf.maternity.gui.visits.VisitTypeBrowser',
    'N',
    14
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'pregvisittypes'
);

-- Insert privilege
INSERT INTO oh_groupmenu
(GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT
    359, 'admin', 'pregvisittypes', 1, NULL, NULL, NULL, NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu WHERE GM_MNI_ID_A = 'pregvisittypes' AND GM_UG_ID_A = 'admin'
);