INSERT INTO oh_menuitem (
    MNI_ID_A,
    MNI_BTN_LABEL,
    MNI_LABEL,
    MNI_TOOLTIP,
    MNI_SHORTCUT,
    MNI_SUBMENU,
    MNI_CLASS,
    MNI_IS_SUBMENU,
    MNI_POSITION
)
SELECT
    'archive',
    'angal.menu.btn.archive',
    'angal.menu.archive',
    'x',
    'M',
    'main',
    'org.isf.archive.gui.ArchiveBrowser',
    'N',
    15
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'archive'
);

INSERT INTO oh_groupmenu
(
    GM_UG_ID_A,
    GM_MNI_ID_A,
    GM_ACTIVE,
    GM_CREATED_BY,
    GM_CREATED_DATE,
    GM_LAST_MODIFIED_BY,
    GM_LAST_MODIFIED_DATE
)
SELECT
    'admin', 'archive', 1, NULL, NULL, NULL,NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin'
      AND GM_MNI_ID_A = 'archive'
);