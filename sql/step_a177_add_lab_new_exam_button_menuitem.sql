-- Add menu item for the "new exam" button in the ExamPicker (lab exam prescription)
INSERT INTO oh_menuitem (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'btnlabnewexam', 'angal.exa.newexam.btn', 'angal.exa.newexam.btn', 'angal.exa.newexam.btn', 'x', 'laboratory', 'none', 'N', 3
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_menuitem WHERE MNI_ID_A = 'btnlabnewexam');

-- Fix the label on installs where this script already ran with the old, ambiguous label
UPDATE oh_menuitem
SET MNI_BTN_LABEL = 'angal.exa.newexam.btn', MNI_LABEL = 'angal.exa.newexam.btn', MNI_TOOLTIP = 'angal.exa.newexam.btn'
WHERE MNI_ID_A = 'btnlabnewexam' AND MNI_BTN_LABEL = 'angal.common.new.btn';

-- Grant permission to admin group
INSERT INTO oh_groupmenu (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE, GM_CREATED_BY, GM_CREATED_DATE, GM_LAST_MODIFIED_BY, GM_LAST_MODIFIED_DATE)
SELECT 'admin', 'btnlabnewexam', 1, NULL, NULL, NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM oh_groupmenu WHERE GM_UG_ID_A = 'admin' AND GM_MNI_ID_A = 'btnlabnewexam');
