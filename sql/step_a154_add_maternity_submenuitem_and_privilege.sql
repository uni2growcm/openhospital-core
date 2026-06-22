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

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'maternity.updatevisit', 'angal.maternity.updatevisit.btn', 'angal.maternity.updatevisit.btn', 'x', '', 'maternity', '', 'N', 5
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
SELECT 'maternity.report', 'angal.common.report.btn', 'angal.common.report.btn', 'x', '', 'maternity', '', 'N', 10
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