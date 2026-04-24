-- =========================
-- DELIVERY TYPES MENU
-- =========================

INSERT INTO `oh_menuitem`
(`MNI_ID_A`,
 `MNI_BTN_LABEL`,
 `MNI_LABEL`,
 `MNI_TOOLTIP`,
 `MNI_SHORTCUT`,
 `MNI_SUBMENU`,
 `MNI_CLASS`,
 `MNI_IS_SUBMENU`,
 `MNI_POSITION`)
VALUES
('pregdeliverytypes',
 'angal.menu.btn.pregdeliverytypes',
 'angal.menu.pregdeliverytypes',
 'x',
 'D',
 'types',
 'org.isf.maternity.gui.delivery.DeliveryTypeBrowser',
 'N',
 15);

INSERT INTO `oh_groupmenu`
(`GM_ID`,
 `GM_UG_ID_A`,
 `GM_MNI_ID_A`,
 `GM_ACTIVE`,
 `GM_CREATED_BY`,
 `GM_CREATED_DATE`,
 `GM_LAST_MODIFIED_BY`,
 `GM_LAST_MODIFIED_DATE`)
VALUES
(357, 'admin', 'pregdeliverytypes', 1, NULL, NULL, NULL, NULL);


-- =========================
-- VISIT TYPES MENU
-- =========================

INSERT INTO `oh_menuitem`
(`MNI_ID_A`,
 `MNI_BTN_LABEL`,
 `MNI_LABEL`,
 `MNI_TOOLTIP`,
 `MNI_SHORTCUT`,
 `MNI_SUBMENU`,
 `MNI_CLASS`,
 `MNI_IS_SUBMENU`,
 `MNI_POSITION`)
VALUES
('pregvisittypes',
 'angal.menu.btn.pregvisittypes',
 'angal.menu.pregvisittypes',
 'x',
 'V',
 'types',
 'org.isf.maternity.gui.visits.VisitTypeBrowser',
 'N',
 14);

INSERT INTO `oh_groupmenu`
(`GM_ID`,
 `GM_UG_ID_A`,
 `GM_MNI_ID_A`,
 `GM_ACTIVE`,
 `GM_CREATED_BY`,
 `GM_CREATED_DATE`,
 `GM_LAST_MODIFIED_BY`,
 `GM_LAST_MODIFIED_DATE`)
VALUES
(356, 'admin', 'pregvisittypes', 1, NULL, NULL, NULL, NULL);