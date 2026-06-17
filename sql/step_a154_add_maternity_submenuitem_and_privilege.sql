-- ========================================================================
-- FULL SCRIPT - Maternity Menu with Submenus
-- ========================================================================

UPDATE oh_menuitem
SET MNI_SUBMENU = 'maternity_internal'
WHERE MNI_ID_A IN ('maternity.updatevisit', 'maternity.report');

UPDATE oh_menuitem
SET
    MNI_IS_SUBMENU = 'Y',
    MNI_CLASS      = ''
WHERE MNI_ID_A = 'maternity';

UPDATE oh_menuitem
SET MNI_SUBMENU = 'maternity_internal'
WHERE MNI_ID_A IN (
                   'maternity.new',
                   'maternity.update',
                   'maternity.delete',
                   'maternity.newvisit',
                   'maternity.updatevisit',
                   'maternity.deletevisit',
                   'maternity.delivery',
                   'maternity.admission',
                   'maternity.exams',
                   'maternity.report',
                   'maternity.vaccin',
                   'maternity.therapy'
    );
DELETE FROM oh_menuitem WHERE MNI_ID_A IN ('cpn', 'hiv', 'familyplanning');

-- CPN
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP,
 MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
VALUES (
           'cpn',
           'angal.menu.btn.anc',
           'angal.menu.btn.anc',
           'x',
           '',
           'maternity',
           'org.isf.maternity.gui.MaternityBrowser',
           'N',
           1
       );

-- VIH
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP,
 MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
VALUES (
           'hiv',
           'angal.menu.btn.hivfollowup',
           'angal.menu.btn.hivfollowup',
           'x',
           '',
           'maternity',
           'org.isf.maternity.gui.HIVFollowUpBrowser',
           'N',
           2
       );

-- familyplanning
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP,
 MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
VALUES (
           'familyplanning',
           'angal.menu.familyplanning',
           'angal.menu.familyplanning',
           'x',
           '',
           'maternity',
           'org.isf.maternity.gui.FamilyPlanningBrowser',
           'N',
           3
       );

-- CORRECTION ICI : Remplacement de 'maternity' par 'maternity_internal'
INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'maternity.updatevisit', 'angal.maternity.updatevisit.btn', 'angal.maternity.updatevisit.btn', 'x', '', 'maternity_internal', '', 'N', 5
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'maternity.updatevisit');

DELETE FROM oh_groupmenu
WHERE GM_MNI_ID_A IN ('cpn', 'hiv', 'familyplanning');

INSERT INTO oh_groupmenu
(GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE,
 GM_CREATED_BY, GM_CREATED_DATE,
 GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
VALUES ('admin', 'cpn', 1, NULL, NULL, NULL, NULL);

INSERT INTO oh_groupmenu
(GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE,
 GM_CREATED_BY, GM_CREATED_DATE,
 GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
VALUES ('admin', 'hiv', 1, NULL, NULL, NULL, NULL);

INSERT INTO oh_groupmenu
(GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE,
 GM_CREATED_BY, GM_CREATED_DATE,
 GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
VALUES ('admin', 'familyplanning', 1, NULL, NULL, NULL, NULL);

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'maternity.report', 'angal.common.report.btn', 'angal.common.report.btn', 'x', '', 'maternity_internal', '', 'N', 10
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'maternity.report');

INSERT INTO oh_groupmenu
(GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE,
 GM_CREATED_BY, GM_CREATED_DATE,
 GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'maternity.new', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'maternity.new'
);

INSERT INTO oh_groupmenu
(GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE,
 GM_CREATED_BY, GM_CREATED_DATE,
 GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'maternity.update', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'maternity.update'
);

INSERT INTO oh_groupmenu
(GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE,
 GM_CREATED_BY, GM_CREATED_DATE,
 GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'maternity.delete', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'maternity.delete'
);

INSERT INTO oh_groupmenu
(GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE,
 GM_CREATED_BY, GM_CREATED_DATE,
 GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'maternity.newvisit', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'maternity.newvisit'
);

INSERT INTO oh_groupmenu
(GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE,
 GM_CREATED_BY, GM_CREATED_DATE,
 GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'maternity.updatevisit', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'maternity.updatevisit'
);

INSERT INTO oh_groupmenu
(GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE,
 GM_CREATED_BY, GM_CREATED_DATE,
 GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'maternity.deletevisit', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'maternity.deletevisit'
);

INSERT INTO oh_groupmenu
(GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE,
 GM_CREATED_BY, GM_CREATED_DATE,
 GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'maternity.delivery', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'maternity.delivery'
);

INSERT INTO oh_groupmenu
(GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE,
 GM_CREATED_BY, GM_CREATED_DATE,
 GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'maternity.admission', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'maternity.admission'
);

INSERT INTO oh_groupmenu
(GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE,
 GM_CREATED_BY, GM_CREATED_DATE,
 GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'maternity.exams', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'maternity.exams'
);

INSERT INTO oh_groupmenu
(GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE,
 GM_CREATED_BY, GM_CREATED_DATE,
 GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'maternity.report', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'maternity.report'
);

INSERT INTO oh_groupmenu
(GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE,
 GM_CREATED_BY, GM_CREATED_DATE,
 GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'maternity.vaccin', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'maternity.vaccin'
);

INSERT INTO oh_groupmenu
(GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE,
 GM_CREATED_BY, GM_CREATED_DATE,
 GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'maternity.therapy', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (
    SELECT 1 FROM oh_groupmenu
    WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'maternity.therapy'
);

UPDATE oh_groupmenu
SET GM_ACTIVE = 1
WHERE GM_UG_ID_A = 'admin'
  AND GM_MNI_ID_A IN (
                      'maternity.new', 'maternity.update', 'maternity.delete',
                      'maternity.newvisit', 'maternity.updatevisit', 'maternity.deletevisit',
                      'maternity.delivery', 'maternity.admission', 'maternity.exams',
                      'maternity.report', 'maternity.vaccin', 'maternity.therapy'
    );

INSERT INTO OH_SETTINGS (
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
    WHERE NOT EXISTS (
    SELECT 1 FROM OH_SETTINGS WHERE SETT_CODE = 'HIV_INFANT_MAX_AGE_MONTHS'
);

-- ========================================================================
-- ADD SUB-MENUS VIH
-- ========================================================================

DELETE FROM oh_menuitem WHERE MNI_SUBMENU = 'hiv_internal';

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'hiv.new', 'angal.hiv.button.new', 'angal.hiv.button.new', 'Nouveau nourrisson VIH', 'N', 'hiv_internal', '', 'N', 1
    WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'hiv.new');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'hiv.update', 'angal.hiv.button.edit', 'angal.hiv.button.edit', 'Modifier nourrisson VIH', 'M', 'hiv_internal', '', 'N', 2
    WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'hiv.update');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'hiv.delete', 'angal.hiv.button.delete', 'angal.hiv.button.delete', 'Supprimer nourrisson VIH', 'D', 'hiv_internal', '', 'N', 3
    WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'hiv.delete');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'hiv.newvisit', 'angal.hiv.button.newvisit', 'angal.hiv.button.newvisit', 'Nouvelle visite VIH', 'V', 'hiv_internal', '', 'N', 4
    WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'hiv.newvisit');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'hiv.updatevisit', 'angal.hiv.button.editvisit', 'angal.hiv.button.editvisit', 'Modifier visite VIH', 'U', 'hiv_internal', '', 'N', 5
    WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'hiv.updatevisit');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'hiv.deletevisit', 'angal.hiv.button.deletevisit', 'angal.hiv.button.deletevisit', 'Supprimer visite VIH', 'S', 'hiv_internal', '', 'N', 6
    WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'hiv.deletevisit');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'hiv.therapy', 'angal.hiv.button.therapy', 'angal.hiv.button.therapy', 'Gérer les traitements VIH', 'T', 'hiv_internal', '', 'N', 7
    WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'hiv.therapy');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'hiv.report', 'angal.hiv.button.report', 'angal.hiv.button.report', 'Rapport VIH', 'R', 'hiv_internal', '', 'N', 8
    WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'hiv.report');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'hiv.details', 'angal.common.details.btn', 'angal.common.details.btn', 'Détails nourrisson VIH', 'I', 'hiv_internal', '', 'N', 9
    WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'hiv.details');

-- ========================================================================
-- Assign rights to the Admin group for HIV submenus
-- ========================================================================

DELETE FROM oh_groupmenu WHERE GM_MNI_ID_A LIKE 'hiv.%' AND GM_UG_ID_A = 'admin';

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'hiv.new', 1, NULL, NOW(), NULL, NULL
    WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'hiv.new');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'hiv.update', 1, NULL, NOW(), NULL, NULL
    WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'hiv.update');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'hiv.delete', 1, NULL, NOW(), NULL, NULL
    WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'hiv.delete');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'hiv.newvisit', 1, NULL, NOW(), NULL, NULL
    WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'hiv.newvisit');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'hiv.updatevisit', 1, NULL, NOW(), NULL, NULL
    WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'hiv.updatevisit');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'hiv.deletevisit', 1, NULL, NOW(), NULL, NULL
    WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'hiv.deletevisit');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'hiv.therapy', 1, NULL, NOW(), NULL, NULL
    WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'hiv.therapy');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'hiv.report', 1, NULL, NOW(), NULL, NULL
    WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'hiv.report');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'hiv.details', 1, NULL, NOW(), NULL, NULL
    WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'hiv.details');