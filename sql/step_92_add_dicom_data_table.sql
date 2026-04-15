-- add the new table
-- adding primary key with auto increment. We will not be setting this auto increment manually
create table oh_dicom_data (
    dmd_data_id bigint(20) not null auto_increment,
    dmd_file_id bigint(20),
    dmd_data longblob,
    primary key (dmd_data_id)
) engine=innodb charset=utf8mb3 collate=utf8mb3_general_ci;

-- migrate data from dicom table to the new table, using the dmd_file_id
insert into oh_dicom_data(dmd_file_id, dmd_data)
    (select d.dm_file_id, d.dm_data
     from oh_dicom d
     where d.dm_data IS not null);

-- drop original column
alter table oh_dicom drop dm_data;

-- add dmd_file_id foreign key
alter table oh_dicom_data
    add constraint fk_dicom_data_dicom
        foreign key (dmd_file_id)
        references oh_dicom (dm_file_id)
        on delete cascade
        on update cascade;
