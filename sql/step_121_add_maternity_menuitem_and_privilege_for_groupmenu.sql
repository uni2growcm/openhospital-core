-- Add maternity menuitem and privilege (if not exists)

INSERT INTO `oh_menuitem` (`MNI_ID_A`, `MNI_BTN_LABEL`, `MNI_LABEL`, `MNI_TOOLTIP`, `MNI_SHORTCUT`, `MNI_SUBMENU`, `MNI_CLASS`, `MNI_IS_SUBMENU`, `MNI_POSITION`)
SELECT 'maternity','angal.menu.btn.maternity','angal.menu.maternity','x','M','main','org.isf.maternity.gui.MaternityBrowser','N',5
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `oh_menuitem` WHERE `MNI_ID_A` = 'maternity');

INSERT INTO `oh_groupmenu` (`GM_ID`, `GM_UG_ID_A`, `GM_MNI_ID_A`, `GM_ACTIVE`, `GM_CREATED_BY`, `GM_CREATED_DATE`, `GM_LAST_MODIFIED_BY`, `GM_LAST_MODIFIED_DATE`)
SELECT 351, 'admin', 'maternity', 1, NULL, NULL, NULL, NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `oh_groupmenu` WHERE `GM_ID` = 351);

-- Update the order of positioning of menu items
UPDATE oh_menuitem SET MNI_POSITION=7 WHERE MNI_ID_A="accounting";
UPDATE oh_menuitem SET MNI_POSITION=12 WHERE MNI_ID_A="agetype";
UPDATE oh_menuitem SET MNI_POSITION=6 WHERE MNI_ID_A="btnadmopd";
UPDATE oh_menuitem SET MNI_POSITION=7 WHERE MNI_ID_A="btnadmpatientfolder";
UPDATE oh_menuitem SET MNI_POSITION=7 WHERE MNI_ID_A="btnadmtherapy";
UPDATE oh_menuitem SET MNI_POSITION=7 WHERE MNI_ID_A="btnbillreceipt";
UPDATE oh_menuitem SET MNI_POSITION=6 WHERE MNI_ID_A="btnbillreport";
UPDATE oh_menuitem SET MNI_POSITION=11 WHERE MNI_ID_A="communication";
UPDATE oh_menuitem SET MNI_POSITION=14 WHERE MNI_ID_A="dicomtype";
UPDATE oh_menuitem SET MNI_POSITION=6 WHERE MNI_ID_A="examtype";
UPDATE oh_menuitem SET MNI_POSITION=12 WHERE MNI_ID_A="generaldata";
UPDATE oh_menuitem SET MNI_POSITION=13 WHERE MNI_ID_A="help";
UPDATE oh_menuitem SET MNI_POSITION=6 WHERE MNI_ID_A="inventoryward";
UPDATE oh_menuitem SET MNI_POSITION=8 WHERE MNI_ID_A="medicalstype";
UPDATE oh_menuitem SET MNI_POSITION=7 WHERE MNI_ID_A="medstockmovtype";
UPDATE oh_menuitem SET MNI_POSITION=6 WHERE MNI_ID_A="operation";
UPDATE oh_menuitem SET MNI_POSITION=9 WHERE MNI_ID_A="operationtype";
UPDATE oh_menuitem SET MNI_POSITION=11 WHERE MNI_ID_A="otherprices";
UPDATE oh_menuitem SET MNI_POSITION=6 WHERE MNI_ID_A="patientvaccine";
UPDATE oh_menuitem SET MNI_POSITION=10 WHERE MNI_ID_A="pretreatmenttype";
UPDATE oh_menuitem SET MNI_POSITION=8 WHERE MNI_ID_A="priceslists";
UPDATE oh_menuitem SET MNI_POSITION=10 WHERE MNI_ID_A="printing";
UPDATE oh_menuitem SET MNI_POSITION=10 WHERE MNI_ID_A="smsmanager";
UPDATE oh_menuitem SET MNI_POSITION=9 WHERE MNI_ID_A="statistics";
UPDATE oh_menuitem SET MNI_POSITION=9 WHERE MNI_ID_A="supplier";
UPDATE oh_menuitem SET MNI_POSITION=10 WHERE MNI_ID_A="telemetry";
UPDATE oh_menuitem SET MNI_POSITION=11 WHERE MNI_ID_A="users";
UPDATE oh_menuitem SET MNI_POSITION=7 WHERE MNI_ID_A="vaccine";
UPDATE oh_menuitem SET MNI_POSITION=13 WHERE MNI_ID_A="vaccinetype";
UPDATE oh_menuitem SET MNI_POSITION=8 WHERE MNI_ID_A="worksheet";
UPDATE oh_menuitem SET MNI_POSITION=7 WHERE MNI_ID_A="inventory";