-- =========================
-- PARTNERS MENU ITEM
-- =========================
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