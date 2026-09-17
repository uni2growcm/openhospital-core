-- "Vaccin du patient" becomes a submenu folder instead of launching PatVacBrowser directly.
UPDATE OH_MENUITEM SET MNI_CLASS = 'none', MNI_IS_SUBMENU = 'Y' WHERE MNI_ID_A = 'patientvaccine';

-- New leaf: the screen "Vaccin du patient" used to open directly.
INSERT INTO OH_MENUITEM (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'patientvaccinemanage','angal.menu.btn.patientvaccinemanage','angal.menu.patientvaccinemanage','x','V','patientvaccine','org.isf.patvac.gui.PatVacBrowser','N',0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM OH_MENUITEM WHERE MNI_ID_A = 'patientvaccinemanage');

-- Re-parent the button-level permissions to the new leaf screen, not the folder, so SubMenu
-- doesn't try to render them as clickable folder entries.
UPDATE OH_MENUITEM SET MNI_SUBMENU = 'patientvaccinemanage'
WHERE MNI_ID_A IN ('btnpatientvaccinenew','btnpatientvaccineedit','btnpatientvaccinedel')
  AND MNI_SUBMENU <> 'patientvaccinemanage';

-- The vaccine stock screen joins the folder instead of sitting at the top-level main menu.
UPDATE OH_MENUITEM SET MNI_SUBMENU = 'patientvaccine', MNI_POSITION = 1
WHERE MNI_ID_A = 'vaccinestock' AND MNI_SUBMENU <> 'patientvaccine';

-- The new leaf inherits whatever group access "Vaccin du patient" already had.
INSERT INTO OH_GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT gm.GM_UG_ID_A, 'patientvaccinemanage', gm.GM_ACTIVE
FROM OH_GROUPMENU gm
WHERE gm.GM_MNI_ID_A = 'patientvaccine'
  AND NOT EXISTS (SELECT 1 FROM OH_GROUPMENU x
                  WHERE x.GM_UG_ID_A = gm.GM_UG_ID_A AND x.GM_MNI_ID_A = 'patientvaccinemanage');
