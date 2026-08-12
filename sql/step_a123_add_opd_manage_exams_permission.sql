--
-- Add the "Manage exams" button permission to OpdBrowser, granted by default to
-- admin, doctor, and laboratorist.
--

INSERT INTO OH_MENUITEM (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
VALUES ('opdexam', 'angal.opd.manageexams.btn', 'angal.opd.manageexams.btn', 'x', 'X', 'opd', 'none', 'N', 3);

INSERT INTO OH_GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
VALUES ('admin', 'opdexam', 1),
       ('doctor', 'opdexam', 1),
       ('laboratorist', 'opdexam', 1);
