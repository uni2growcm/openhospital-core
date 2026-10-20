-- ============================================================
-- ADD COUNTRIES PERMISSIONS
-- Description: Create permissions for countries and assign to admin group
-- ============================================================

-- countries.create
INSERT INTO `oh_permissions` (`P_ID_A`, `P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`)
SELECT (SELECT COALESCE(MAX(P_ID_A), 0) + 1 FROM `oh_permissions`), 'countries.create', '', 1, NULL, NULL, NULL, NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `oh_permissions` WHERE `P_NAME` = 'countries.create'
);

-- countries.read
INSERT INTO `oh_permissions` (`P_ID_A`, `P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`)
SELECT (SELECT COALESCE(MAX(P_ID_A), 0) + 1 FROM `oh_permissions`), 'countries.read', '', 1, NULL, NULL, NULL, NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `oh_permissions` WHERE `P_NAME` = 'countries.read'
);

-- countries.update
INSERT INTO `oh_permissions` (`P_ID_A`, `P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`)
SELECT (SELECT COALESCE(MAX(P_ID_A), 0) + 1 FROM `oh_permissions`), 'countries.update', '', 1, NULL, NULL, NULL, NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `oh_permissions` WHERE `P_NAME` = 'countries.update'
);

-- countries.delete
INSERT INTO `oh_permissions` (`P_ID_A`, `P_NAME`, `P_DESCRIPTION`, `P_ACTIVE`, `P_CREATED_BY`, `P_CREATED_DATE`, `P_LAST_MODIFIED_BY`, `P_LAST_MODIFIED_DATE`)
SELECT (SELECT COALESCE(MAX(P_ID_A), 0) + 1 FROM `oh_permissions`), 'countries.delete', '', 1, NULL, NULL, NULL, NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `oh_permissions` WHERE `P_NAME` = 'countries.delete'
);

-- ============================================================
-- ASSIGN PERMISSIONS TO ADMIN GROUP
-- ============================================================

INSERT INTO `oh_grouppermission` (`GP_ID`, `GP_UG_ID_A`, `GP_P_ID_A`, `GP_ACTIVE`, `GP_CREATED_BY`, `GP_CREATED_DATE`, `GP_LAST_MODIFIED_BY`, `GP_LAST_MODIFIED_DATE`)
SELECT (SELECT COALESCE(MAX(GP_ID), 0) + 1 FROM `oh_grouppermission`), 'admin', p.P_ID_A, 1, NULL, NULL, NULL, NULL
FROM `oh_permissions` p
WHERE p.P_NAME = 'countries.create'
  AND NOT EXISTS (
    SELECT 1 FROM `oh_grouppermission` gp WHERE gp.GP_UG_ID_A = 'admin' AND gp.GP_P_ID_A = p.P_ID_A
);

INSERT INTO `oh_grouppermission` (`GP_ID`, `GP_UG_ID_A`, `GP_P_ID_A`, `GP_ACTIVE`, `GP_CREATED_BY`, `GP_CREATED_DATE`, `GP_LAST_MODIFIED_BY`, `GP_LAST_MODIFIED_DATE`)
SELECT (SELECT COALESCE(MAX(GP_ID), 0) + 1 FROM `oh_grouppermission`), 'admin', p.P_ID_A, 1, NULL, NULL, NULL, NULL
FROM `oh_permissions` p
WHERE p.P_NAME = 'countries.read'
  AND NOT EXISTS (
    SELECT 1 FROM `oh_grouppermission` gp WHERE gp.GP_UG_ID_A = 'admin' AND gp.GP_P_ID_A = p.P_ID_A
);

INSERT INTO `oh_grouppermission` (`GP_ID`, `GP_UG_ID_A`, `GP_P_ID_A`, `GP_ACTIVE`, `GP_CREATED_BY`, `GP_CREATED_DATE`, `GP_LAST_MODIFIED_BY`, `GP_LAST_MODIFIED_DATE`)
SELECT (SELECT COALESCE(MAX(GP_ID), 0) + 1 FROM `oh_grouppermission`), 'admin', p.P_ID_A, 1, NULL, NULL, NULL, NULL
FROM `oh_permissions` p
WHERE p.P_NAME = 'countries.update'
  AND NOT EXISTS (
    SELECT 1 FROM `oh_grouppermission` gp WHERE gp.GP_UG_ID_A = 'admin' AND gp.GP_P_ID_A = p.P_ID_A
);

INSERT INTO `oh_grouppermission` (`GP_ID`, `GP_UG_ID_A`, `GP_P_ID_A`, `GP_ACTIVE`, `GP_CREATED_BY`, `GP_CREATED_DATE`, `GP_LAST_MODIFIED_BY`, `GP_LAST_MODIFIED_DATE`)
SELECT (SELECT COALESCE(MAX(GP_ID), 0) + 1 FROM `oh_grouppermission`), 'admin', p.P_ID_A, 1, NULL, NULL, NULL, NULL
FROM `oh_permissions` p
WHERE p.P_NAME = 'countries.delete'
  AND NOT EXISTS (
    SELECT 1 FROM `oh_grouppermission` gp WHERE gp.GP_UG_ID_A = 'admin' AND gp.GP_P_ID_A = p.P_ID_A
);
