-- ============================================================================
-- step_a153_reapply_menus.sql
-- ============================================================================
-- The demo-data dump (load_demo_data.sql) reloads OH_MENUITEM, OH_GROUPMENU and
-- OH_GROUPPERMISSION from an old snapshot, and delete_all_data.sql truncates
-- OH_MENUITEM beforehand. Every menu entry added by the migration steps
-- (a121-a152) is therefore lost when provisioning a demo database.
--
-- This script re-applies ALL menu wiring. It is idempotent: safe on a fresh
-- migration chain (entries already exist) and after the demo dump (entries are
-- missing). It must be sourced AFTER load_demo_data.sql.
-- ============================================================================

-- ---------------------------------------------------------------- 8< --------
-- 1. PHARMACY MENU - stock order screen (step_a126)
-- ---------------------------------------------------------------- 8< --------
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'stockorder', 'angal.menu.btn.stockorder', 'angal.menu.stockorder', 'x', 'O', 'pharmacy',
       'org.isf.stockorder.gui.StockOrderBrowser', 'N', 7
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'stockorder');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'stockorder', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'stockorder');

-- ---------------------------------------------------------------- 8< --------
-- 2. PATIENTS MENU - CPN screens (step_a127)
-- ---------------------------------------------------------------- 8< --------
-- Repair pass: an earlier revision attached these entries to a 'patient'
-- submenu that does not exist in the menu dump (PrivilegeTree loops forever
-- on such orphans). The original a127/a129/a130 scripts attach them to 'main'.
UPDATE oh_menuitem SET MNI_SUBMENU = 'main' WHERE MNI_ID_A IN ('cpn', 'cpnexam', 'familyplanning', 'hivchildfollowup');

INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'cpn', 'angal.menu.btn.cpn', 'angal.menu.cpn', 'x', 'P', 'main',
       'org.isf.pregnancy.gui.CpnBrowser', 'N', 8
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'cpn');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'cpn', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'cpn');

INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'cpnexam', 'angal.menu.btn.cpnexam', 'angal.menu.cpnexam', 'x', 'E', 'main',
       'org.isf.pregnancy.gui.CpnExamParameterBrowser', 'N', 9
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'cpnexam');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'cpnexam', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'cpnexam');

-- ---------------------------------------------------------------- 8< --------
-- 3. PATIENTS MENU - family planning (step_a129) and HIV child follow-up
--    (step_a130)
-- ---------------------------------------------------------------- 8< --------
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'familyplanning', 'angal.menu.btn.familyplanning', 'angal.menu.familyplanning', 'x', 'F', 'main',
       'org.isf.familyplanning.gui.FamilyPlanningBrowser', 'N', 10
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'familyplanning');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'familyplanning', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'familyplanning');

INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'hivchildfollowup', 'angal.menu.btn.hivchildfollowup', 'angal.menu.hivchildfollowup', 'x', 'H', 'main',
       'org.isf.hivchildfollowup.gui.HivExposedChildBrowser', 'N', 11
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'hivchildfollowup');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'hivchildfollowup', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'hivchildfollowup');

-- ---------------------------------------------------------------- 8< --------
-- 4. OPD MENU - malnutrition tab is part of OpdBrowser (no menu entry),
--    button-level permissions from step_a133
-- ---------------------------------------------------------------- 8< --------
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'opdexam', 'angal.opd.manageexams.btn', 'angal.opd.manageexams.btn', 'x', 'X', 'opd', 'none', 'N', 3
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'opdexam');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT g.ug, 'opdexam', 1 FROM DUAL, (SELECT 'admin' AS ug UNION SELECT 'doctor' UNION SELECT 'laboratorist') g
WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = g.ug AND GM_MNI_ID_A = 'opdexam');

INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'btnopdnewtherapy', 'angal.admission.therapy.btn', 'angal.admission.therapy.btn', 'x', 'T', 'opd', 'none', 'N', 4
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'btnopdnewtherapy');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT g.ug, 'btnopdnewtherapy', 1 FROM DUAL, (SELECT 'admin' AS ug UNION SELECT 'doctor') g
WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = g.ug AND GM_MNI_ID_A = 'btnopdnewtherapy');

INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'opdeope', 'angal.opd.operation', 'angal.opd.operation', 'x', 'O', 'opd', 'none', 'N', 5
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'opdeope');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT g.ug, 'opdeope', 1 FROM DUAL, (SELECT 'admin' AS ug UNION SELECT 'doctor') g
WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = g.ug AND GM_MNI_ID_A = 'opdeope');

-- ---------------------------------------------------------------- 8< --------
-- 5. VACCINE MENU - patient-vaccine folder + vaccine stock (steps a139, a140)
-- ---------------------------------------------------------------- 8< --------
-- "Vaccin du patient" becomes a submenu folder instead of launching PatVacBrowser directly.
UPDATE oh_menuitem SET MNI_CLASS = 'none', MNI_IS_SUBMENU = 'Y' WHERE MNI_ID_A = 'patientvaccine';

-- New leaf: the screen "Vaccin du patient" used to open directly.
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'patientvaccinemanage', 'angal.menu.btn.patientvaccinemanage', 'angal.menu.patientvaccinemanage', 'x', 'V',
       'patientvaccine', 'org.isf.patvac.gui.PatVacBrowser', 'N', 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'patientvaccinemanage');

-- Re-parent the button-level permissions to the new leaf screen, not the folder.
UPDATE oh_menuitem SET MNI_SUBMENU = 'patientvaccinemanage'
WHERE MNI_ID_A IN ('btnpatientvaccinenew', 'btnpatientvaccineedit', 'btnpatientvaccinedel');

-- The vaccine stock screen joins the folder instead of sitting at the top-level main menu.
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'vaccinestock', 'angal.menu.btn.vaccinestock', 'angal.menu.vaccinestock', 'x', 'K', 'patientvaccine',
       'org.isf.vaccinestock.gui.VaccineStockBrowser', 'N', 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'vaccinestock');

UPDATE oh_menuitem SET MNI_SUBMENU = 'patientvaccine', MNI_POSITION = 1
WHERE MNI_ID_A = 'vaccinestock' AND MNI_SUBMENU <> 'patientvaccine';

INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'btnvaccinestockcharge', 'angal.vaccinestock.charge', 'angal.vaccinestock.charge', 'x', 'C',
       'vaccinestock', 'none', 'N', 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'btnvaccinestockcharge');

INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'btnvaccinestockdischarge', 'angal.vaccinestock.discharge', 'angal.vaccinestock.discharge', 'x', 'D',
       'vaccinestock', 'none', 'N', 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'btnvaccinestockdischarge');

-- The new leaf inherits whatever group access "Vaccin du patient" already had.
INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT gm.GM_UG_ID_A, 'patientvaccinemanage', gm.GM_ACTIVE
FROM oh_groupmenu gm
WHERE gm.GM_MNI_ID_A = 'patientvaccine'
  AND NOT EXISTS (SELECT 1 FROM oh_groupmenu x
                  WHERE x.GM_UG_ID_A = gm.GM_UG_ID_A AND x.GM_MNI_ID_A = 'patientvaccinemanage');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'vaccinestock', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'vaccinestock');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'btnvaccinestockcharge', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'btnvaccinestockcharge');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'btnvaccinestockdischarge', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'btnvaccinestockdischarge');

-- ---------------------------------------------------------------- 8< --------
-- 6. STATISTICS SUBMENUS (step_a149) - Reports + Statistics folder with the
--    General / CPN / Delivery screens
-- ---------------------------------------------------------------- 8< --------
UPDATE oh_menuitem SET MNI_IS_SUBMENU = 'Y' WHERE MNI_ID_A = 'statistics';

INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'stat.reports', 'angal.stat.menu.reports', 'angal.stat.menu.reports', 'x', 'R', 'statistics',
       'org.isf.stat.reportlauncher.gui.ReportLauncher', 'N', 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'stat.reports');

INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'stat.substats', 'angal.stat.menu.statistics', 'angal.stat.menu.statistics', 'x', 'S', 'statistics',
       'org.isf.stat2.StatsBrowsing', 'Y', 2
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'stat.substats');

UPDATE oh_menuitem SET MNI_IS_SUBMENU = 'Y' WHERE MNI_ID_A = 'stat.substats';

INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'stat.general', 'angal.stat.menu.general', 'angal.stat.menu.general', 'x', 'G', 'stat.substats',
       'org.isf.stat2.StatsBrowsing', 'N', 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'stat.general');

INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'stat.cpn', 'angal.stat.menu.cpn', 'angal.stat.menu.cpn', 'x', 'C', 'stat.substats',
       'org.isf.stat.gui.CpnStatisticsBrowser', 'N', 2
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'stat.cpn');

INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'stat.delivery', 'angal.stat.menu.delivery', 'angal.stat.menu.delivery', 'x', 'D', 'stat.substats',
       'org.isf.stat.gui.DeliveryStatisticsBrowser', 'N', 3
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'stat.delivery');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'stat.reports', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'stat.reports');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'stat.substats', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'stat.substats');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'stat.general', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'stat.general');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'stat.cpn', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'stat.cpn');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'stat.delivery', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'stat.delivery');

-- ---------------------------------------------------------------- 8< --------
-- 7. TYPES MENU - article families (step_a122)
-- ---------------------------------------------------------------- 8< --------
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'articlefamilies', 'angal.menu.btn.articlefamilies', 'angal.menu.articlefamilies', 'x', 'F', 'types',
       'org.isf.articlefamily.gui.ArticleFamilyBrowser', 'N', 14
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'articlefamilies');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT 'admin', 'articlefamilies', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'articlefamilies');
