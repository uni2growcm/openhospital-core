-- ========================================================================
-- Tuberculosis Module - Top-level menu items and privileges
-- ========================================================================
-- This script:
--   1. Cleans up any previous TB-as-OPD-submenu items
--   2. Restores OPD to direct browser (undo containerization)
--   3. Creates 'tuberculosis' as a top-level main menu item
--   4. Creates TB permission items under tuberculosis_internal
--   5. Grants admin privileges
-- Prerequisite: step_a169_tuberculosis_module.sql must have been run first.
-- ========================================================================

-- Step 1: Clean up old TB submenu items under OPD (from previous approach)
DELETE FROM oh_groupmenu WHERE GM_MNI_ID_A LIKE 'opd.tuberculosis%';
DELETE FROM oh_menuitem WHERE MNI_ID_A LIKE 'opd.tuberculosis%';

-- Remove opd.browser if it was added by previous OPD restructuring
DELETE FROM oh_groupmenu WHERE GM_MNI_ID_A = 'opd.browser';
DELETE FROM oh_menuitem WHERE MNI_ID_A = 'opd.browser';

-- Step 2: Restore OPD to direct browser (undo containerization)
UPDATE oh_menuitem
SET MNI_IS_SUBMENU = 'N',
    MNI_CLASS      = 'org.isf.opd.gui.OpdBrowser'
WHERE MNI_ID_A = 'opd'
  AND MNI_IS_SUBMENU = 'Y';

-- Move any items from opd_internal back to opd
UPDATE oh_menuitem
SET MNI_SUBMENU = 'opd'
WHERE MNI_SUBMENU = 'opd_internal';

-- Step 3: Create tuberculosis as top-level main menu item
INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'tuberculosis', 'angal.menu.btn.tuberculosis', 'angal.menu.tuberculosis', 'x', 'T', 'main',
       'org.isf.tuberculosis.gui.TuberculosisBrowser', 'N', 14
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'tuberculosis');

-- Step 4: Create TB permission items under tuberculosis_internal
INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'tuberculosis.new', 'angal.tb.browser.newtreatment.btn', 'angal.tb.browser.newtreatment.btn', 'x', '', 'tuberculosis_internal', '', 'N', 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'tuberculosis.new');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'tuberculosis.update', 'angal.tb.browser.edittreatment.btn', 'angal.tb.browser.edittreatment.btn', 'x', '', 'tuberculosis_internal', '', 'N', 2
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'tuberculosis.update');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'tuberculosis.delete', 'angal.common.delete.btn', 'angal.common.delete.btn', 'x', '', 'tuberculosis_internal', '', 'N', 3
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'tuberculosis.delete');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'tuberculosis.newvisit', 'angal.tb.browser.newvisit.btn', 'angal.tb.browser.newvisit.btn', 'x', '', 'tuberculosis_internal', '', 'N', 4
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'tuberculosis.newvisit');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'tuberculosis.updatevisit', 'angal.tb.browser.editvisit.btn', 'angal.tb.browser.editvisit.btn', 'x', '', 'tuberculosis_internal', '', 'N', 5
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'tuberculosis.updatevisit');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'tuberculosis.deletevisit', 'angal.tb.browser.deletevisit.btn', 'angal.tb.browser.deletevisit.btn', 'x', '', 'tuberculosis_internal', '', 'N', 6
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'tuberculosis.deletevisit');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'tuberculosis.newcontact', 'angal.tb.browser.newcontact.btn', 'angal.tb.browser.newcontact.btn', 'x', '', 'tuberculosis_internal', '', 'N', 7
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'tuberculosis.newcontact');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'tuberculosis.updatecontact', 'angal.tb.browser.editcontact.btn', 'angal.tb.browser.editcontact.btn', 'x', '', 'tuberculosis_internal', '', 'N', 8
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'tuberculosis.updatecontact');

INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'tuberculosis.deletecontact', 'angal.tb.browser.deletecontact.btn', 'angal.tb.browser.deletecontact.btn', 'x', '', 'tuberculosis_internal', '', 'N', 9
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'tuberculosis.deletecontact');

-- Step 5: Grant admin privileges for tuberculosis menu items
INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'tuberculosis', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'tuberculosis');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'tuberculosis.new', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'tuberculosis.new');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'tuberculosis.update', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'tuberculosis.update');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'tuberculosis.delete', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'tuberculosis.delete');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'tuberculosis.newvisit', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'tuberculosis.newvisit');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'tuberculosis.updatevisit', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'tuberculosis.updatevisit');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'tuberculosis.deletevisit', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'tuberculosis.deletevisit');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'tuberculosis.newcontact', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'tuberculosis.newcontact');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'tuberculosis.updatecontact', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'tuberculosis.updatecontact');

INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'tuberculosis.deletecontact', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'tuberculosis.deletecontact');
