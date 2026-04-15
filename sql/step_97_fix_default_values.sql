set @old_unique_checks=@@unique_checks, unique_checks=0;
set @old_foreign_key_checks=@@foreign_key_checks, foreign_key_checks=0;
set @old_sql_mode=@@sql_mode, sql_mode='traditional';

alter table oh_medicaldsrstockmov 
drop foreign key fk_medicaldsrstockmov_ward;
alter table oh_medicaldsrstockmov 
change column mmv_wrd_id_a mmv_wrd_id_a char(3) null default null;
alter table oh_medicaldsrstockmov 
add constraint fk_medicaldsrstockmov_ward
  foreign key (mmv_wrd_id_a)
  references oh_ward (wrd_id_a)
  on delete no action
  on update no action;

alter table oh_medicaldsrstockmovward 
drop foreign key fk_medicaldsrstockmovward_ward;
alter table oh_medicaldsrstockmovward 
change column mmvn_wrd_id_a mmvn_wrd_id_a char(3) not null;
alter table oh_medicaldsrstockmovward 
add constraint fk_medicaldsrstockmovward_ward
  foreign key (mmvn_wrd_id_a)
  references oh_ward (wrd_id_a)
  on delete no action
  on update no action;

alter table oh_medicaldsrward 
drop foreign key fk_medicaldsrward_ward;
alter table oh_medicaldsrward 
change column mdsrwrd_wrd_id_a mdsrwrd_wrd_id_a char(3) not null;
alter table oh_medicaldsrward 
add constraint fk_medicaldsrward_ward
  foreign key (mdsrwrd_wrd_id_a)
  references oh_ward (wrd_id_a)
  on delete no action
  on update no action;

alter table oh_visits 
drop foreign key fk_visits_ward;
alter table oh_visits 
change column vst_wrd_id_a vst_wrd_id_a char(3) not null;
alter table oh_visits 
add constraint fk_visits_ward
  foreign key (vst_wrd_id_a)
  references oh_ward (wrd_id_a)
  on delete no action
  on update no action;

set sql_mode=@old_sql_mode;
set foreign_key_checks=@old_foreign_key_checks;
set unique_checks=@old_unique_checks;

alter table oh_ward alter wrd_id_a drop default;