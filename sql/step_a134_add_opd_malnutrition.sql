--
-- Extend malnutrition tracking to support OPD-visit scoping alongside the existing admission scoping,
-- and add the OPD-visit malnutrition flag plus the "Manage malnutrition" button permission on OpdBrowser.
--

ALTER TABLE OH_MALNUTRITIONCONTROL
	MODIFY MLN_ADM_ID int(11) NULL;

ALTER TABLE OH_MALNUTRITIONCONTROL
	ADD COLUMN MLN_OPD_ID int(11) NULL;

ALTER TABLE OH_OPD
	ADD COLUMN OPD_MALNUTRI tinyint(1) NOT NULL DEFAULT 0;

INSERT INTO OH_MENUITEM (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
VALUES ('opdmalnutri', 'angal.admission.malnutritioncontrol', 'angal.admission.malnutritioncontrol', 'x', 'M', 'opd', 'none', 'N', 6);

INSERT INTO OH_GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
VALUES ('admin', 'opdmalnutri', 1),
       ('doctor', 'opdmalnutri', 0),
       ('laboratorist', 'opdmalnutri', 0);
