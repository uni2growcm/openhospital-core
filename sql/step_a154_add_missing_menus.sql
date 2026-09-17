-- ============================================================================
-- step_a154_add_missing_menus.sql
-- ============================================================================
-- Several modules shipped with screens (and bundle keys) but never received
-- their menu wiring:
--   - Reduction plans      (org.isf.reductionplan.gui.ReductionPlanBrowser)
--   - Partners             (org.isf.partner.gui.PartnerBrowser)
--   - Partner types        (org.isf.partnertype.gui.PartnerTypeBrowser)
--   - Prescribers          (org.isf.prescriber.gui.PrescriberBrowser)
--
-- This script adds them to the right submenus (types / accounting) with the
-- permissions for every user group, mirroring how existing "types" entries
-- are granted. Idempotent.
-- ============================================================================

-- ---------------------------------------------------------------- 8< --------
-- 1. Types submenu entries (manage the module type lists)
-- ---------------------------------------------------------------- 8< --------
INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'partnertype', 'angal.menu.btn.partnertype', 'angal.menu.partnertype', 'x', 'T', 'types',
       'org.isf.partnertype.gui.PartnerTypeBrowser', 'N', 15
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'partnertype');

INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'prescriber', 'angal.menu.btn.prescriber', 'angal.menu.prescriber', 'x', 'P', 'types',
       'org.isf.prescriber.gui.PrescriberBrowser', 'N', 16
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'prescriber');

-- ---------------------------------------------------------------- 8< --------
-- 2. Accounting (billing) submenu entries (business screens)
-- ---------------------------------------------------------------- 8< --------
-- Repair pass: an earlier revision used a 'billing' submenu that does not
-- exist in the menu dump and granted rows to a 'patient' user group that is
-- not in oh_usergroup. Both are repaired here (PrivilegeTree loops forever
-- on menu items whose parent is missing).
UPDATE oh_menuitem SET MNI_SUBMENU = 'accounting' WHERE MNI_ID_A IN ('reductionplan', 'partners');
DELETE FROM oh_groupmenu WHERE GM_UG_ID_A = 'patient'
  AND GM_MNI_ID_A IN ('partnertype', 'prescriber', 'reductionplan', 'partners');

INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'reductionplan', 'angal.menu.btn.reductionplans', 'angal.menu.reductionplans', 'x', 'R', 'accounting',
       'org.isf.reductionplan.gui.ReductionPlanBrowser', 'N', 5
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'reductionplan');

INSERT INTO oh_menuitem
(MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'partners', 'angal.menu.btn.partners', 'angal.menu.partners', 'x', 'P', 'accounting',
       'org.isf.partner.gui.PartnerBrowser', 'N', 6
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'partners');

-- ---------------------------------------------------------------- 8< --------
-- 3. Permissions - grant every entry to all user groups (types entries and
--    business screens follow the same pattern as admtype/agetype/... and
--    billsmanager: visible to everyone, screens filter internally).
--    GM_ID is AUTO_INCREMENT: it must NOT be supplied explicitly, otherwise
--    a multi-row insert would assign the same id to every row.
-- ---------------------------------------------------------------- 8< --------
INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT g.ug, m.id, 1
FROM (SELECT 'admin' AS ug UNION SELECT 'doctor' UNION SELECT 'laboratorist' UNION SELECT 'guest') g,
     (SELECT 'partnertype' AS id UNION SELECT 'prescriber' UNION SELECT 'reductionplan' UNION SELECT 'partners') m
WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = g.ug AND GM_MNI_ID_A = m.id);
