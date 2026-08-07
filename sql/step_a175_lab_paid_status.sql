-- ========================================================================
-- Laboratory paid status (Payer) feature
-- ========================================================================
-- Adds the settings that control the "Payer" column, the financial status
-- filter and the paid / not paid / not billed totals in the Laboratory Browser:
--   1. CREATELABORATORYAUTO              - enable the paid status column,
--                                          the "Financial Status" filter and
--                                          the totals panel
--   2. CREATELABORATORYAUTOWITHOPENEDBILL - allow editing of laboratory exams
--                                          that are not yet paid ("C")
-- The LAB_BILL_ID column already exists on OH_LABORATORY (step_a137).
-- ========================================================================

INSERT INTO OH_SETTINGS
(
    SETT_CODE,
    SETT_CATEGORY,
    SETT_VALUE_TYPE,
    SETT_DEFAULT_VALUE,
    SETT_VALUE,
    SETT_DESCRIPTION,
    SETT_ACTIVE,
    SETT_NEED_RESTART
)
SELECT
    'CREATELABORATORYAUTO',
    'application',
    'bool',
    'FALSE',
    'FALSE',
    'Enable the paid status column, the financial status filter and the totals panel in the Laboratory Browser',
    1,
    1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   OH_SETTINGS
    WHERE  SETT_CODE = 'CREATELABORATORYAUTO'
);

INSERT INTO OH_SETTINGS
(
    SETT_CODE,
    SETT_CATEGORY,
    SETT_VALUE_TYPE,
    SETT_DEFAULT_VALUE,
    SETT_VALUE,
    SETT_DESCRIPTION,
    SETT_ACTIVE,
    SETT_NEED_RESTART
)
SELECT
    'CREATELABORATORYAUTOWITHOPENEDBILL',
    'application',
    'bool',
    'FALSE',
    'FALSE',
    'Allow editing laboratory exams that are not yet paid (requires CREATELABORATORYAUTO)',
    1,
    1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM   OH_SETTINGS
    WHERE  SETT_CODE = 'CREATELABORATORYAUTOWITHOPENEDBILL'
);

SELECT SETT_CODE, SETT_VALUE, SETT_DESCRIPTION
FROM OH_SETTINGS
WHERE SETT_CODE IN ('CREATELABORATORYAUTO', 'CREATELABORATORYAUTOWITHOPENEDBILL');
