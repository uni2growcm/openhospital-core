UPDATE oh_menuitem
SET MNI_IS_SUBMENU = 'Y'
WHERE MNI_ID_A = 'statistics';

-- Rapports
INSERT INTO oh_menuitem
(
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
    'stat.reports',
    'angal.stat.menu.reports',
    'angal.stat.menu.reports',
    'x',
    'R',
    'statistics',
    'org.isf.stat.reportlauncher.gui.ReportLauncher',
    'N',
    1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM oh_menuitem
    WHERE MNI_ID_A = 'stat.reports'
);

-- Statistics (parent submenu)
INSERT INTO oh_menuitem
(
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
    'stat.substats',
    'angal.stat.menu.statistics',
    'angal.stat.menu.statistics',
    'x',
    'S',
    'statistics',
    'org.isf.stat.gui.EmptyStatisticsBrowser',
    'Y',
    2
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM oh_menuitem
    WHERE MNI_ID_A = 'stat.substats'
);

-- If already exists, convert it into parent menu
UPDATE oh_menuitem
SET MNI_IS_SUBMENU = 'Y'
WHERE MNI_ID_A = 'stat.substats';

-- General Statistics
INSERT INTO oh_menuitem
(
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
    'stat.general',
    'angal.stat.menu.general',
    'angal.stat.menu.general',
    'x',
    'G',
    'stat.substats',
    'org.isf.stat2.StatsBrowsing',
    'N',
    1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM oh_menuitem
    WHERE MNI_ID_A = 'stat.general'
);

-- CPN Statistics
INSERT INTO oh_menuitem
(
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
    'stat.cpn',
    'angal.stat.menu.cpn',
    'angal.stat.menu.cpn',
    'x',
    'C',
    'stat.substats',
    'org.isf.stat.gui.EmptyStatisticsBrowser',
    'N',
    2
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM oh_menuitem
    WHERE MNI_ID_A = 'stat.cpn'
);

-- Delivery Statistics
INSERT INTO oh_menuitem
(
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
    'stat.delivery',
    'angal.stat.menu.delivery',
    'angal.stat.menu.delivery',
    'x',
    'D',
    'stat.substats',
    'org.isf.stat.gui.EmptyStatisticsBrowser',
    'N',
    3
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM oh_menuitem
    WHERE MNI_ID_A = 'stat.delivery'
);

-- Permissions for admin group

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'stat.reports', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin'
      AND GM_MNI_ID_A = 'stat.reports'
);

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'stat.substats', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin'
      AND GM_MNI_ID_A = 'stat.substats'
);

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'stat.general', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin'
      AND GM_MNI_ID_A = 'stat.general'
);

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'stat.cpn', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin'
      AND GM_MNI_ID_A = 'stat.cpn'
);

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'stat.delivery', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin'
      AND GM_MNI_ID_A = 'stat.delivery'
);