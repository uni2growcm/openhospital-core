-- alter table oh_dicomtype --------------

alter table `oh_dicomtype` 
add column dcmt_created_by varchar(50) null default null,
add column dcmt_created_date datetime null default null,
add column dcmt_last_modified_by varchar(50) null default null,
add column dcmt_last_modified_date datetime null default null,
add column dcmt_active tinyint(1) not null default 1;

-- alter table oh_dicom_data --------------

alter table `oh_dicom_data`
add column dmd_created_by varchar(50) null default null,
add column dmd_created_date datetime null default null,
add column dmd_last_modified_by varchar(50) null default null,
add column dmd_last_modified_date datetime null default null,
add column dmd_active tinyint(1) not null default 1;