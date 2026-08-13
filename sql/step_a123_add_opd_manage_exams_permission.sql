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

--
-- Add the "Therapy" button permission to OpdBrowser, granted by default to admin and doctor,
-- matching the existing btnadmtherapy grant on AdmittedPatientBrowser.
--

INSERT INTO OH_MENUITEM (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
VALUES ('btnopdnewtherapy', 'angal.admission.therapy.btn', 'angal.admission.therapy.btn', 'x', 'T', 'opd', 'none', 'N', 4);

INSERT INTO OH_GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
VALUES ('admin', 'btnopdnewtherapy', 1),
       ('doctor', 'btnopdnewtherapy', 1);


--
-- Add the "Operation" button permission to OpdBrowser, granted by default to admin and doctor,
-- matching the existing btnopdnewoperation/btnopdeditoperation grants.
--

INSERT INTO OH_MENUITEM (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
VALUES ('opdeope', 'angal.opd.operation', 'angal.opd.operation', 'x', 'O', 'opd', 'none', 'N', 5);

INSERT INTO OH_GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
VALUES ('admin', 'opdeope', 1),
       ('doctor', 'opdeope', 1);
