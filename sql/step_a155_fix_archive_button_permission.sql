INSERT INTO OH_MENUITEM (
    MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT,
    MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION
)
SELECT
    'btnbillarchive', 'angal.billbrowser.archive.btn',
    'angal.billbrowser.archive.btn', 'x', 'A',
    'billsmanager', 'none', 'N', 5
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM OH_MENUITEM WHERE MNI_ID_A = 'btnbillarchive'
);

UPDATE OH_MENUITEM
SET MNI_IS_SUBMENU = 'N',
    MNI_SUBMENU = 'billsmanager',
    MNI_CLASS = 'none'
WHERE MNI_ID_A = 'btnbillarchive';

INSERT INTO oh_groupmenu (
    GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE,
    GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE
)
SELECT
    'admin', 'btnbillarchive', 1, NULL, NULL, NULL, NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin'
      AND GM_MNI_ID_A = 'btnbillarchive'
);

