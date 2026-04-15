--
-- Instead of manipulating and resorting system global variables just
-- drop and regenerate the offending foreign key constraint.
-- This makes the script database version independent.
-- See OP-1335  (https://openhospital.atlassian.net/browse/OP-1335)
--
alter table oh_dicom drop foreign key fk_dicom_dicomtype;

alter table oh_dicom engine = innodb, convert to character set utf8;
alter table oh_dicomtype engine = innodb, convert to character set utf8;

alter table oh_dicom
    add constraint fk_dicom_dicomtype
        foreign key (dm_dcmt_id)
            references oh_dicomtype (dcmt_id)
            on delete no action
            on update no action;
