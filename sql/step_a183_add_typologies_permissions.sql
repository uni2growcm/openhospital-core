-- ========================================================================
-- Typologies permissions
-- ========================================================================

-- Insert permissions if they do not exist
INSERT INTO `oh_permissions` (`P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`)
SELECT 'typologies.read', 'Read typologies', 1, NULL, NULL, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `oh_permissions` WHERE `P_NAME` = 'typologies.read');

INSERT INTO `oh_permissions` (`P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`)
SELECT 'typologies.create', 'Create typologies', 1, NULL, NULL, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `oh_permissions` WHERE `P_NAME` = 'typologies.create');

INSERT INTO `oh_permissions` (`P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`)
SELECT 'typologies.update', 'Update typologies', 1, NULL, NULL, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `oh_permissions` WHERE `P_NAME` = 'typologies.update');

INSERT INTO `oh_permissions` (`P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`)
SELECT 'typologies.delete', 'Delete typologies', 1, NULL, NULL, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `oh_permissions` WHERE `P_NAME` = 'typologies.delete');

-- Assign permissions to admin group
INSERT INTO `oh_grouppermission` (`GP_UG_ID_A`, `GP_P_ID_A`, `GP_ACTIVE`, `GP_CREATED_BY`, `GP_CREATED_DATE`, `GP_LAST_MODIFIED_BY`, `GP_LAST_MODIFIED_DATE`)
SELECT 'admin', p.P_ID_A, 1, NULL, NULL, NULL, NULL
FROM `oh_permissions` p
WHERE p.P_NAME IN ('typologies.read', 'typologies.create', 'typologies.update', 'typologies.delete')
  AND NOT EXISTS (
      SELECT 1 FROM `oh_grouppermission` gp
      WHERE gp.GP_UG_ID_A = 'admin' AND gp.GP_P_ID_A = p.P_ID_A
  );
