-- Add missing GeneralData settings into OH_SETTINGS

INSERT INTO `OH_SETTINGS` (`SETT_ID`, `SETT_CODE`, `SETT_VALUE_TYPE`, `SETT_VALUE_OPTIONS`, `SETT_DEFAULT_VALUE`, `SETT_VALUE`, `SETT_DESCRIPTION`, `SETT_CREATED_BY`, `SETT_LAST_MODIFIED_BY`, `SETT_CREATED_DATE`, `SETT_LAST_MODIFIED_DATE`, `SETT_ACTIVE`, `SETT_NEED_RESTART`, `SETT_CATEGORY`) VALUES
-- Application module settings
(NULL, 'HOMEVISITMODULEENABLED', 'bool', NULL, 'TRUE', 'TRUE', 'Whether to enable Home Visit module or not', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'MATERNITYMODULEENABLED', 'bool', NULL, 'TRUE', 'TRUE', 'Whether to enable Maternity module or not', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'HIVMODULEENABLED', 'bool', NULL, 'TRUE', 'TRUE', 'Whether to enable HIV module or not', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'HIV_INFANT_MAX_AGE_MONTHS', 'number', NULL, '24', '24', 'Maximum age in months for HIV infant patients', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'TUBERCULOSISMODULEENABLED', 'bool', NULL, 'TRUE', 'TRUE', 'Whether to enable Tuberculosis module or not', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'FAMILYPLANNINGMODULEENABLED', 'bool', NULL, 'TRUE', 'TRUE', 'Whether to enable Family Planning module or not', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'MALNUTRITIONMODULEENABLED', 'bool', NULL, 'TRUE', 'TRUE', 'Whether to enable Malnutrition module or not', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'COMMANDMODULEENABLED', 'bool', NULL, 'TRUE', 'TRUE', 'Whether to enable Stock Order/Command module or not', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'PARTNERSMODULEENABLED', 'bool', NULL, 'TRUE', 'TRUE', 'Whether to enable Partners module or not', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'ENABLEMORTUARYMODULE', 'bool', NULL, 'FALSE', 'FALSE', 'Whether to enable Mortuary module or not', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'CREATELABORATORYAUTO', 'bool', NULL, 'FALSE', 'FALSE', 'Automatically create laboratory exam request', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'CREATELABORATORYAUTOWITHOPENEDBILL', 'bool', NULL, 'FALSE', 'FALSE', 'Automatically create laboratory exam request with opened bill', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),

-- Accounting settings
(NULL, 'ENABLEMEDICALREFUND', 'bool', NULL, 'FALSE', 'FALSE', 'Enable medical refund feature in billing', 'admin', NULL, NOW(), NULL, 1, 1, 'accounting'),
(NULL, 'ALLOWBILLGUARANTOR', 'bool', NULL, 'FALSE', 'FALSE', 'Allow guarantor assignment on patient bills', 'admin', NULL, NOW(), NULL, 1, 1, 'accounting'),
(NULL, 'ALLOWBILLPARTNER', 'bool', NULL, 'FALSE', 'FALSE', 'Allow partner/insurance assignment on patient bills', 'admin', NULL, NOW(), NULL, 1, 1, 'accounting'),
(NULL, 'ENABLEBILLITEMGROUP', 'bool', NULL, 'FALSE', 'FALSE', 'Group items by category in new bill creation', 'admin', NULL, NOW(), NULL, 1, 1, 'accounting'),
(NULL, 'STOCKMVTONBILLSAVE', 'bool', NULL, 'FALSE', 'FALSE', 'Trigger stock movement automatically when bill is saved', 'admin', NULL, NOW(), NULL, 1, 1, 'accounting'),
(NULL, 'DEFAULTWARDINNEWBILL', 'text', NULL, '', '', 'Default ward preselected when creating a new bill', 'admin', NULL, NOW(), NULL, 1, 1, 'accounting'),

-- Pharmacy settings
(NULL, 'HIGHLIGHTEXPIRINGMEDICAL', 'bool', NULL, 'FALSE', 'FALSE', 'Highlight expiring medicals in stock list', 'admin', NULL, NOW(), NULL, 1, 1, 'pharmacy'),
(NULL, 'HIGHLIGHTEXPIRINGMEDICALDAYS', 'number', NULL, '30', '30', 'Number of days before expiration date to highlight medicals', 'admin', NULL, NOW(), NULL, 1, 1, 'pharmacy'),
(NULL, 'REFERENCE_AUTOMATIC', 'bool', NULL, 'FALSE', 'FALSE', 'Enable automatic stock reference generation', 'admin', NULL, NOW(), NULL, 1, 1, 'pharmacy'),
(NULL, 'REFERENCE_PREFIX_CHARGE', 'text', NULL, 'REF+', 'REF+', 'Prefix used for stock charge (inflow) reference codes', 'admin', NULL, NOW(), NULL, 1, 1, 'pharmacy'),
(NULL, 'REFERENCE_PREFIX_DISCHARGE', 'text', NULL, 'REF-', 'REF-', 'Prefix used for stock discharge (outflow) reference codes', 'admin', NULL, NOW(), NULL, 1, 1, 'pharmacy'),
(NULL, 'REFERENCE_PREFIX_INVENTORY', 'text', NULL, 'INV', 'INV', 'Prefix used for stock inventory reference codes', 'admin', NULL, NOW(), NULL, 1, 1, 'pharmacy'),
(NULL, 'REFERENCE_TIMESTAMP_FORMAT', 'text', NULL, 'yyyyMMdd''T''HHmmss', 'yyyyMMdd''T''HHmmss', 'Timestamp format used in automatic stock reference generation', 'admin', NULL, NOW(), NULL, 1, 1, 'pharmacy'),

-- Reports settings
(NULL, 'PHARMACEUTICALSTOCKNOZERO', 'text', NULL, 'PharmaceuticalStock_ver4_1', 'PharmaceuticalStock_ver4_1', 'File name for pharmaceutical stock report excluding zero stock', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'BILLSREFUNDREPORT', 'text', NULL, 'BillsRefundReport', 'BillsRefundReport', 'File name for bills refund report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'PATIENTARCHIVEDBILL', 'text', NULL, 'PatientArchivedBill', 'PatientArchivedBill', 'File name for archived patient bill report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),
(NULL, 'PATIENTARCHIVEBILLSTATEMENT', 'text', NULL, 'PatientArchiveBillStatement', 'PatientArchiveBillStatement', 'File name for patient archive bills statement report', 'admin', NULL, NOW(), NULL, 1, 1, 'reports'),

-- General settings
(NULL, 'PAGINATIONPAGESIZE', 'number', NULL, '100', '100', 'Default page size for paginated tables', 'admin', NULL, NOW(), NULL, 1, 1, 'general')
ON DUPLICATE KEY UPDATE `SETT_CODE` = VALUES(`SETT_CODE`);
