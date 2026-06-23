

UPDATE oh_menuitem
SET    MNI_SUBMENU = 'maternity_internal'
WHERE  MNI_SUBMENU = 'maternity'
  AND  MNI_ID_A NOT IN ('maternity.cpn', 'maternity.familyplanning', 'maternity.hiv');

UPDATE oh_menuitem
SET    MNI_IS_SUBMENU = 'Y',
       MNI_CLASS      = ''
WHERE  MNI_ID_A = 'maternity';

-- CPN → MaternityBrowser
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
    'maternity.cpn',
    'angal.menu.btn.anc',
    'angal.menu.btn.anc',
    'x',
    'C',
    'maternity',
    'org.isf.maternity.gui.MaternityBrowser',
    'N',
    1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_menuitem
    WHERE  MNI_ID_A = 'maternity.cpn'
);

-- Planning Familial → FamilyPlanningBrowser
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
    'maternity.familyplanning',
    'angal.menu.familyplanning',
    'angal.menu.familyplanning',
    'x',
    'P',
    'maternity',
    'org.isf.maternity.gui.FamilyPlanningBrowser',
    'N',
    2
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_menuitem
    WHERE  MNI_ID_A = 'maternity.familyplanning'
);

-- FllowUp VIH → HIVFollowUpBrowser
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
    'maternity.hiv',
    'angal.menu.btn.hivfollowup',
    'angal.menu.btn.hivfollowup',
    'x',
    'H',
    'maternity',
    'org.isf.maternity.gui.HIVFollowUpBrowser',
    'N',
    3
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_menuitem
    WHERE  MNI_ID_A = 'maternity.hiv'
);

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
    'hiv.new',
    'angal.hiv.button.new',
    'angal.hiv.button.new',
    'x',
    '',
    'hiv_internal',
    '',
    'N',
    1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_menuitem
    WHERE  MNI_ID_A = 'hiv.new'
);

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
    'hiv.update',
    'angal.hiv.button.edit',
    'angal.hiv.button.edit',
    'x',
    '',
    'hiv_internal',
    '',
    'N',
    2
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_menuitem
    WHERE  MNI_ID_A = 'hiv.update'
);

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
    'hiv.delete',
    'angal.hiv.button.delete',
    'angal.hiv.button.delete',
    'x',
    '',
    'hiv_internal',
    '',
    'N',
    3
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_menuitem
    WHERE  MNI_ID_A = 'hiv.delete'
);

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
    'hiv.newvisit',
    'angal.hiv.button.newvisit',
    'angal.hiv.button.newvisit',
    'x',
    '',
    'hiv_internal',
    '',
    'N',
    4
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_menuitem
    WHERE  MNI_ID_A = 'hiv.newvisit'
);

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
    'hiv.updatevisit',
    'angal.hiv.button.editvisit',
    'angal.hiv.button.editvisit',
    'x',
    '',
    'hiv_internal',
    '',
    'N',
    5
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_menuitem
    WHERE  MNI_ID_A = 'hiv.updatevisit'
);

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
    'hiv.deletevisit',
    'angal.hiv.button.deletevisit',
    'angal.hiv.button.deletevisit',
    'x',
    '',
    'hiv_internal',
    '',
    'N',
    6
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_menuitem
    WHERE  MNI_ID_A = 'hiv.deletevisit'
);

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
    'hiv.therapy',
    'angal.hiv.button.therapy',
    'angal.hiv.button.therapy',
    'x',
    '',
    'hiv_internal',
    '',
    'N',
    7
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_menuitem
    WHERE  MNI_ID_A = 'hiv.therapy'
);

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
    'hiv.report',
    'angal.common.report.btn',
    'angal.common.report.btn',
    'x',
    '',
    'hiv_internal',
    '',
    'N',
    8
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_menuitem
    WHERE  MNI_ID_A = 'hiv.report'
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
    'admin',
    'maternity.cpn',
    1,
    'admin',
    NOW(),
    'admin',
    NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_groupmenu
    WHERE  GM_UG_ID_A  = 'admin'
      AND  GM_MNI_ID_A = 'maternity.cpn'
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
    'admin',
    'maternity.familyplanning',
    1,
    'admin',
    NOW(),
    'admin',
    NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_groupmenu
    WHERE  GM_UG_ID_A  = 'admin'
      AND  GM_MNI_ID_A = 'maternity.familyplanning'
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
    'admin',
    'maternity.hiv',
    1,
    'admin',
    NOW(),
    'admin',
    NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_groupmenu
    WHERE  GM_UG_ID_A  = 'admin'
      AND  GM_MNI_ID_A = 'maternity.hiv'
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
    'admin',
    'hiv.new',
    1,
    'admin',
    NOW(),
    'admin',
    NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_groupmenu
    WHERE  GM_UG_ID_A  = 'admin'
      AND  GM_MNI_ID_A = 'hiv.new'
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
    'admin',
    'hiv.update',
    1,
    'admin',
    NOW(),
    'admin',
    NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_groupmenu
    WHERE  GM_UG_ID_A  = 'admin'
      AND  GM_MNI_ID_A = 'hiv.update'
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
    'admin',
    'hiv.delete',
    1,
    'admin',
    NOW(),
    'admin',
    NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_groupmenu
    WHERE  GM_UG_ID_A  = 'admin'
      AND  GM_MNI_ID_A = 'hiv.delete'
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
    'admin',
    'hiv.newvisit',
    1,
    'admin',
    NOW(),
    'admin',
    NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_groupmenu
    WHERE  GM_UG_ID_A  = 'admin'
      AND  GM_MNI_ID_A = 'hiv.newvisit'
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
    'admin',
    'hiv.updatevisit',
    1,
    'admin',
    NOW(),
    'admin',
    NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_groupmenu
    WHERE  GM_UG_ID_A  = 'admin'
      AND  GM_MNI_ID_A = 'hiv.updatevisit'
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
    'admin',
    'hiv.deletevisit',
    1,
    'admin',
    NOW(),
    'admin',
    NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_groupmenu
    WHERE  GM_UG_ID_A  = 'admin'
      AND  GM_MNI_ID_A = 'hiv.deletevisit'
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
    'admin',
    'hiv.therapy',
    1,
    'admin',
    NOW(),
    'admin',
    NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_groupmenu
    WHERE  GM_UG_ID_A  = 'admin'
      AND  GM_MNI_ID_A = 'hiv.therapy'
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
    'admin',
    'hiv.report',
    1,
    'admin',
    NOW(),
    'admin',
    NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   oh_groupmenu
    WHERE  GM_UG_ID_A  = 'admin'
      AND  GM_MNI_ID_A = 'hiv.report'
);

INSERT INTO OH_SETTINGS
(
    SETT_CODE,
    SETT_CATEGORY,
    SETT_VALUE_TYPE,
    SETT_DEFAULT_VALUE,
    SETT_VALUE,
    SETT_DESCRIPTION,
    SETT_ACTIVE,
    SETT_NEED_RESTART
)
SELECT
    'HIV_INFANT_MAX_AGE_MONTHS',
    'application',
    'number',
    '24',
    '24',
    'Maximum age in months for HIV infant follow-up',
    1,
    1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   OH_SETTINGS
    WHERE  SETT_CODE = 'HIV_INFANT_MAX_AGE_MONTHS'
);