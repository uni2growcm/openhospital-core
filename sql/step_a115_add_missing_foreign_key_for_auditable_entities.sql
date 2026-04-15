--- medicaldsrinventory --

alter table oh_medicaldsrinventory 
drop constraint fk_medicaldsrinventory_user;

alter table oh_medicaldsrinventory 
add constraint fk_medicaldsrinventory_user_1
foreign key (minvt_us_id_a) references oh_user (us_id_a) on delete no action on update no action;

alter table oh_medicaldsrinventory 
add constraint fk_medicaldsrinventory_user_2
foreign key (minvt_created_by) references oh_user (us_id_a) on delete no action on update no action;

alter table oh_medicaldsrinventory 
add constraint fk_medicaldsrinventory_user_3
foreign key (minvt_last_modified_by) references oh_user (us_id_a) on delete no action on update no action;

-- oh_medicaldsrinventoryrow --

alter table oh_medicaldsrinventoryrow 
add constraint fk_medicaldsrinventoryrow_user_1
foreign key (minvtr_created_by) references oh_user (us_id_a) on delete no action on update no action;

alter table oh_medicaldsrinventoryrow 
add constraint fk_medicaldsrinventoryrow_user_2
foreign key (minvtr_last_modified_by) references oh_user (us_id_a) on delete no action on update no action;

alter table oh_medicaldsrinventoryrow 
drop constraint oh_medicaldsrinventoryrow_ibfk_1;

alter table oh_medicaldsrinventoryrow 
add constraint fk_medicaldsrinventoryrow_medicaldsrinventory
foreign key (minvtr_invt_id) references oh_medicaldsrinventory (minvt_id) on delete no action on update no action;

alter table oh_medicaldsrinventoryrow 
drop constraint oh_medicaldsrinventoryrow_ibfk_2;

alter table oh_medicaldsrinventoryrow 
add constraint fk_medicaldsrinventoryrow_medicaldsr
foreign key (minvtr_mdsr_id) references oh_medicaldsr (mdsr_id) on delete no action on update no action;

alter table oh_medicaldsrinventoryrow 
drop constraint oh_medicaldsrinventoryrow_ibfk_3;

alter table oh_medicaldsrinventoryrow 
add constraint fk_medicaldsrinventoryrow_medicaldsrlot
foreign key (minvtr_lt_id_a) references oh_medicaldsrlot (lt_id_a) on delete no action on update no action;

-- oh_medicaldsrstock --

alter table oh_medicaldsrstock
modify column ms_created_by varchar(50) character set utf8 collate utf8_general_ci;

alter table oh_medicaldsrstock
modify column ms_last_modified_by varchar(50) character set utf8 collate utf8_general_ci;

alter table oh_medicaldsrstock
add constraint fk_medicaldsrstock_user_1
foreign key (ms_created_by) references oh_user (us_id_a) on delete no action on update no action;

alter table oh_medicaldsrstock 
add constraint fk_medicaldsrstock_user_2
foreign key (ms_last_modified_by) references oh_user (us_id_a) on delete no action on update no action;

-- oh_user_settings --

alter table oh_user_settings
drop constraint uss_us_id_a_fk;

alter table oh_user_settings
add constraint fk_user_settings_user_1
foreign key (uss_us_id_a) references oh_user (us_id_a) on delete no action on update no action;

alter table oh_user_settings
add constraint fk_user_settings_user_2
foreign key (uss_created_by) references oh_user (us_id_a) on delete no action on update no action;

alter table oh_user_settings
add constraint fk_user_settings_user_3
foreign key (uss_last_modified_by) references oh_user (us_id_a) on delete no action on update no action;

-- oh_patient_consensus --

alter table oh_patient_consensus
add constraint fk_patient_consensus_user_1
foreign key (ptc_created_by) references oh_user (us_id_a) on delete no action on update no action;

alter table oh_patient_consensus
add constraint fk_patient_consensus_user_2
foreign key (ptc_last_modified_by) references oh_user (us_id_a) on delete no action on update no action;

alter table oh_patient_consensus 
drop constraint oh_patient_consensus_ibfk_1;

alter table oh_patient_consensus 
add constraint fk_patient_consensus_patient
foreign key (ptc_pat_id) references oh_patient (pat_id) on delete no action on update no action;
