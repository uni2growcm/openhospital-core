--
-- Add the "Manage exams" button permission to OpdBrowser, granted by default to
-- admin, doctor, and laboratorist.
--

INSERT INTO OH_MENUITEM (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'opdexam', 'angal.opd.manageexams.btn', 'angal.opd.manageexams.btn', 'x', 'X', 'opd', 'none', 'N', 3
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM OH_MENUITEM WHERE MNI_ID_A = 'opdexam');

INSERT INTO OH_GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT g.ug, 'opdexam', 1
FROM (SELECT 'admin' AS ug UNION SELECT 'doctor' UNION SELECT 'laboratorist') g
WHERE NOT EXISTS (SELECT 1 FROM OH_GROUPMENU WHERE GM_UG_ID_A = g.ug AND GM_MNI_ID_A = 'opdexam');

--
-- Add the "Therapy" button permission to OpdBrowser, granted by default to admin and doctor,
-- matching the existing btnadmtherapy grant on AdmittedPatientBrowser.
--

INSERT INTO OH_MENUITEM (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'btnopdnewtherapy', 'angal.admission.therapy.btn', 'angal.admission.therapy.btn', 'x', 'T', 'opd', 'none', 'N', 4
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM OH_MENUITEM WHERE MNI_ID_A = 'btnopdnewtherapy');

INSERT INTO OH_GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT g.ug, 'btnopdnewtherapy', 1
FROM (SELECT 'admin' AS ug UNION SELECT 'doctor') g
WHERE NOT EXISTS (SELECT 1 FROM OH_GROUPMENU WHERE GM_UG_ID_A = g.ug AND GM_MNI_ID_A = 'btnopdnewtherapy');

--
-- Add the "Operation" button permission to OpdBrowser, granted by default to admin and doctor,
-- matching the existing btnopdnewoperation/btnopdeditoperation grants.
--

INSERT INTO OH_MENUITEM (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
SELECT 'opdeope', 'angal.opd.operation', 'angal.opd.operation', 'x', 'O', 'opd', 'none', 'N', 5
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM OH_MENUITEM WHERE MNI_ID_A = 'opdeope');

INSERT INTO OH_GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
SELECT g.ug, 'opdeope', 1
FROM (SELECT 'admin' AS ug UNION SELECT 'doctor') g
WHERE NOT EXISTS (SELECT 1 FROM OH_GROUPMENU WHERE GM_UG_ID_A = g.ug AND GM_MNI_ID_A = 'opdeope');
