-- alter table oh_dicomtype --------------

ALTER TABLE `oh_dicomtype` 
ADD COLUMN DCMT_CREATED_BY VARCHAR(50) NULL DEFAULT NULL,
ADD COLUMN DCMT_CREATED_DATE datetime NULL DEFAULT NULL,
ADD COLUMN DCMT_LAST_MODIFIED_BY VARCHAR(50) NULL DEFAULT NULL,
ADD COLUMN DCMT_LAST_MODIFIED_DATE datetime NULL DEFAULT NULL,
ADD COLUMN DCMT_ACTIVE TINYINT(1) NOT NULL DEFAULT 1;

-- alter table oh_dicom_data --------------

ALTER TABLE `oh_dicom_data`
ADD COLUMN DMD_CREATED_BY VARCHAR(50) NULL DEFAULT NULL,
ADD COLUMN DMD_CREATED_DATE datetime NULL DEFAULT NULL,
ADD COLUMN DMD_LAST_MODIFIED_BY VARCHAR(50) NULL DEFAULT NULL,
ADD COLUMN DMD_LAST_MODIFIED_DATE datetime NULL DEFAULT NULL,
ADD COLUMN DMD_ACTIVE TINYINT(1) NOT NULL DEFAULT 1;