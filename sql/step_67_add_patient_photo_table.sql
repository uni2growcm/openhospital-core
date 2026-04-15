-- first, add foreign key from patient to patient_profile_photo
-- this means that the patient table will be responsible in maintaining the relationship between patient and the profile photo table
alter table patient add column profile_photo_id int(11);

-- now add the new table
-- note that:
-- 1. we are adding primary key with auto increment. We will not be setting this auto increment manually
-- 2. we are adding temp_pat_id for migration purpose, so that we can relate existing photo from patient table to this new table while doing migration
create table patient_profile_photo (
                                       pat_profile_photo_id int not null auto_increment,
                                       temp_pat_id int not null,
                                       pat_photo blob,
                                       primary key (pat_profile_photo_id)
) engine=MyISAM;

-- now, we migrate photo from patient table to the new table, using the temp_pat_id
insert into patient_profile_photo(temp_pat_id, pat_photo)
    (select p.pat_id, p.pat_photo
     from patient p
     where p.pat_photo IS not null);

-- now we can update references from patient table back to patient_profile_photo
update patient
    inner join patient_profile_photo
    on patient.pat_id = patient_profile_photo.temp_pat_id
set profile_photo_id=patient_profile_photo.pat_profile_photo_id;

-- and finally, we can drop the temporary column
alter table patient_profile_photo drop temp_pat_id;
alter table patient drop pat_photo;