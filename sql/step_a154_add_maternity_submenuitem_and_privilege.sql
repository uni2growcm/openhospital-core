-- =========================
-- SOUS-ITEMS MENU MATERNITÉ
-- =========================

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'maternity.new', 'angal.maternity.new.btn', 'angal.maternity.new.btn', 'x', '', 'maternity', '', 'N', 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'maternity.new');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'maternity.update', 'angal.maternity.update.btn', 'angal.maternity.update.btn', 'x', '', 'maternity', '', 'N', 2
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'maternity.update');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'maternity.delete', 'angal.maternity.delete.btn', 'angal.maternity.delete.btn', 'x', '', 'maternity', '', 'N', 3
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'maternity.delete');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'maternity.newvisit', 'angal.maternity.newvisit.btn', 'angal.maternity.newvisit.btn', 'x', '', 'maternity', '', 'N', 4
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'maternity.newvisit');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'maternity.updatevisit', 'angal.maternity.updatevisit.btn', 'angal.maternity.updatevisit.btn', 'x', '', 'maternity', '', 'N', 5
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'maternity.updatevisit');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'maternity.deletevisit', 'angal.maternity.deletevisit.btn', 'angal.maternity.deletevisit.btn', 'x', '', 'maternity', '', 'N', 6
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'maternity.deletevisit');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'maternity.delivery', 'angal.maternity.delivery.btn', 'angal.maternity.delivery.btn', 'x', '', 'maternity', '', 'N', 7
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'maternity.delivery');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'maternity.admission', 'angal.maternity.admission.btn', 'angal.maternity.admission.btn', 'x', '', 'maternity', '', 'N', 8
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'maternity.admission');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'maternity.exams', 'angal.opd.exams.btn', 'angal.opd.exams.btn', 'x', '', 'maternity', '', 'N', 9
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'maternity.exams');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'maternity.report', 'angal.common.report.btn', 'angal.common.report.btn', 'x', '', 'maternity', '', 'N', 10
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'maternity.report');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'maternity.vaccin', 'angal.cpn.vaccin.btn', 'angal.cpn.vaccin.btn', 'x', '', 'maternity', '', 'N', 11
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'maternity.vaccin');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'maternity.therapy', 'angal.maternity.therapy.btn', 'angal.maternity.therapy.btn', 'x', '', 'maternity', '', 'N', 12
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'maternity.therapy');


-- =========================
-- Lier au groupe admin dans oh_groupmenu
-- =========================

INSERT INTO oh_groupmenu (GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 821, 'admin', 'maternity.new', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_ID = 821);

INSERT INTO oh_groupmenu (GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 822, 'admin', 'maternity.update', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_ID = 822);

INSERT INTO oh_groupmenu (GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 823, 'admin', 'maternity.delete', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_ID = 823);

INSERT INTO oh_groupmenu (GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 824, 'admin', 'maternity.newvisit', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_ID = 824);

INSERT INTO oh_groupmenu (GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 825, 'admin', 'maternity.updatevisit', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_ID = 825);

INSERT INTO oh_groupmenu (GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 826, 'admin', 'maternity.deletevisit', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_ID = 826);

INSERT INTO oh_groupmenu (GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 827, 'admin', 'maternity.delivery', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_ID = 827);

INSERT INTO oh_groupmenu (GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 828, 'admin', 'maternity.admission', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_ID = 828);

INSERT INTO oh_groupmenu (GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 829, 'admin', 'maternity.exams', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_ID = 829);

INSERT INTO oh_groupmenu (GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 830, 'admin', 'maternity.report', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_ID = 830);

INSERT INTO oh_groupmenu (GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 831, 'admin', 'maternity.vaccin', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_ID = 831);

INSERT INTO oh_groupmenu (GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 832, 'admin', 'maternity.therapy', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_ID = 832);

-- CPN / FAMILY PLANNING / HIV

UPDATE oh_menuitem
SET MNI_CLASS = 'org.isf.maternity.gui.FamilyPlanningBrowser'
WHERE MNI_ID_A = 'familyplanning';

UPDATE oh_menuitem
SET MNI_CLASS = 'org.isf.maternity.gui.HIVFollowUpBrowser'
WHERE MNI_ID_A = 'hiv';

UPDATE oh_menuitem
SET MNI_CLASS = 'org.isf.maternity.gui.MaternityBrowser'
WHERE MNI_ID_A = 'anc';

INSERT INTO oh_groupmenu
(GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE,
 GM_CREATED_BY, GM_CREATED_DATE,
 GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT
    (SELECT MAX(GM_ID)+1 FROM oh_groupmenu),
    'admin',
    'cpn',
    1,
    NULL,NULL,NULL,NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM oh_groupmenu
    WHERE GM_UG_ID_A='admin'
      AND GM_MNI_ID_A='cpn'
);

INSERT INTO oh_groupmenu
(GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE,
 GM_CREATED_BY, GM_CREATED_DATE,
 GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT
    (SELECT MAX(GM_ID)+1 FROM oh_groupmenu),
    'admin',
    'familyplanning',
    1,
    NULL,NULL,NULL,NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM oh_groupmenu
    WHERE GM_UG_ID_A='admin'
      AND GM_MNI_ID_A='familyplanning'
);

INSERT INTO oh_groupmenu
(GM_ID, GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE,
 GM_CREATED_BY, GM_CREATED_DATE,
 GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT
    (SELECT MAX(GM_ID)+1 FROM oh_groupmenu),
    'admin',
    'hiv',
    1,
    NULL,NULL,NULL,NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM oh_groupmenu
    WHERE GM_UG_ID_A='admin'
      AND GM_MNI_ID_A='hiv'
);