INSERT INTO OH_MENUITEM (
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
    'btnbillarchive',
    'angal.billbrowser.archive.btn',
    'angal.billbrowser.archive.btn',
    'Archive old bills',
    'A',
    'billsmanager',
    'none',
    'N',
    6
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM OH_MENUITEM WHERE MNI_ID_A = 'btnbillarchive'
);

INSERT INTO oh_groupmenu (
    GM_UG_ID_A,
    GM_MNI_ID_A,
    GM_ACTIVE,
    GM_CREATED_BY,
    GM_CREATED_DATE,
    GM_LAST_MODIFIED_BY,
    GM_LAST_MODIFIED_DATE
)
SELECT
    'admin',
    'btnbillarchive',
    1,
    'system',
    NOW(),
    'system',
    NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin'
      AND GM_MNI_ID_A = 'btnbillarchive'
);

SELECT
    CASE
        WHEN EXISTS (SELECT 1 FROM OH_MENUITEM WHERE MNI_ID_A = 'btnbillarchive')
            THEN 'OH_MENUITEM: btnbillarchive present'
        ELSE 'OH_MENUITEM: btnbillarchive absent'
        END AS menu_item_status,
    CASE
        WHEN EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'btnbillarchive')
            THEN 'oh_groupmenu: btnbillarchive present for admin'
        ELSE 'oh_groupmenu: btnbillarchive absent for admin'
        END AS group_permission_status;