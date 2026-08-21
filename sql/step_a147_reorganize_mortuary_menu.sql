-- Change mortuary to submenu
UPDATE OH_MENUITEM SET MNI_CLASS = "none", MNI_IS_SUBMENU='Y' WHERE MNI_ID_A="mortuary";

-- Add body compartments in the mortuary menu
INSERT INTO `oh_menuitem` (`MNI_ID_A`, `MNI_BTN_LABEL`, `MNI_LABEL`, `MNI_TOOLTIP`, `MNI_SHORTCUT`, `MNI_SUBMENU`, `MNI_CLASS`, `MNI_IS_SUBMENU`, `MNI_POSITION`) VALUES ('bodycompartments', 'angal.menu.btn.bodycompartments', 'angal.menu.bodycompartments', 'x', 'B', 'mortuary', 'org.isf.mortuary.gui.BodyCompartmentBrowser','N', 1);
INSERT INTO `oh_groupmenu` (`GM_ID`, `GM_UG_ID_A`, `GM_MNI_ID_A`, `GM_ACTIVE`, `GM_CREATED_BY`, `GM_CREATED_DATE`, `GM_LAST_MODIFIED_BY`, `GM_LAST_MODIFIED_DATE`) VALUES (353,'admin','bodycompartments',1,NULL,NULL,NULL,NULL);

-- Add mortuary stay in the mortuary menu
UPDATE OH_MENUITEM SET MNI_SUBMENU="mortuary", MNI_POSITION=2 WHERE MNI_ID_A="mortuarystays";

-- Add deaths in the mortuary menu
INSERT INTO `oh_menuitem` (`MNI_ID_A`, `MNI_BTN_LABEL`, `MNI_LABEL`, `MNI_TOOLTIP`, `MNI_SHORTCUT`, `MNI_SUBMENU`, `MNI_CLASS`, `MNI_IS_SUBMENU`, `MNI_POSITION`) VALUES ('deaths', 'angal.menu.btn.deaths', 'angal.menu.deaths', 'x', 'D', 'mortuary', 'org.isf.mortuary.gui.MortuaryBrowser','N', 3);
INSERT INTO `oh_groupmenu` (`GM_ID`, `GM_UG_ID_A`, `GM_MNI_ID_A`, `GM_ACTIVE`, `GM_CREATED_BY`, `GM_CREATED_DATE`, `GM_LAST_MODIFIED_BY`, `GM_LAST_MODIFIED_DATE`) VALUES (354,'admin','deaths',1,NULL,NULL,NULL,NULL);