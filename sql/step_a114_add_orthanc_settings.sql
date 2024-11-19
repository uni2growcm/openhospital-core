-- Load settings into the database
INSERT INTO `OH_SETTINGS` (`SETT_ID`, `SETT_CODE`, `SETT_VALUE_TYPE`, `SETT_VALUE_OPTIONS`, `SETT_DEFAULT_VALUE`, `SETT_VALUE`, `SETT_DESCRIPTION`, `SETT_CREATED_BY`, `SETT_LAST_MODIFIED_BY`, `SETT_CREATED_DATE`, `SETT_LAST_MODIFIED_DATE`, `SETT_ACTIVE`, `SETT_NEED_RESTART`, `SETT_CATEGORY`) VALUES
(NULL, 'ORTHANCENABLED', 'bool', NULL, 'FALSE', 'FALSE', 'Enable ORTHANC integration', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'ORTHANCBASEURL ', 'text', NULL, NULL, NULL, 'Base URL of the ORTHANC server', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'ORTHANCUSERNAME', 'text', NULL, 'admin', 'admin', 'Name of the user to use for ORTHANC APIs calls', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'ORTHANCPASSWORD ', 'text', NULL, 'adminADMIN!123#', 'adminADMIN!123#', 'Password of the user to use for ORTHANC APIs calls', 'admin', NULL, NOW(), NULL, 1, 1, 'application'),
(NULL, 'ORTHANCEXPLORERURL', 'text', NULL, NULL, NULL, 'Full URL of the ORTHANC explorer', 'admin', NULL, NOW(), NULL, 1, 1, 'application');