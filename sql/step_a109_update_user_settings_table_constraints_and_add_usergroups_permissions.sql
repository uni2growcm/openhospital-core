alter table oh_user_settings drop foreign key oh_user_settings_ibfk_1;
alter table oh_user_settings drop index uss_us_id_a;
alter table oh_user_settings
add constraint uss_us_id_a_fk foreign key (uss_us_id_a) references oh_user(us_id_a) on delete cascade;
alter table oh_user_settings add constraint uss_us_id_a_config_name_idx unique (uss_us_id_a, uss_config_name);

insert into `oh_permissions` (`p_id_a`, `p_name`, `p_description`, `p_active`, `p_created_by`, `p_created_date`, `p_last_modified_by`, `p_last_modified_date`) values (168,'usergroups.create','','1',null,null,null,null);
insert into `oh_permissions` (`p_id_a`, `p_name`, `p_description`, `p_active`, `p_created_by`, `p_created_date`, `p_last_modified_by`, `p_last_modified_date`) values (169,'usergroups.read','','1',null,null,null,null);
insert into `oh_permissions` (`p_id_a`, `p_name`, `p_description`, `p_active`, `p_created_by`, `p_created_date`, `p_last_modified_by`, `p_last_modified_date`) values (170,'usergroups.update','','1',null,null,null,null);
insert into `oh_permissions` (`p_id_a`, `p_name`, `p_description`, `p_active`, `p_created_by`, `p_created_date`, `p_last_modified_by`, `p_last_modified_date`) values (171,'usergroups.delete','','1',null,null,null,null);

insert into `oh_grouppermission` (`gp_id`, `gp_ug_id_a`, `gp_p_id_a`, `gp_active`, `gp_created_by`, `gp_created_date`, `gp_last_modified_by`, `gp_last_modified_date`) values (313,'admin',168,'1',null,null,null,null);
insert into `oh_grouppermission` (`gp_id`, `gp_ug_id_a`, `gp_p_id_a`, `gp_active`, `gp_created_by`, `gp_created_date`, `gp_last_modified_by`, `gp_last_modified_date`) values (314,'admin',169,'1',null,null,null,null);
insert into `oh_grouppermission` (`gp_id`, `gp_ug_id_a`, `gp_p_id_a`, `gp_active`, `gp_created_by`, `gp_created_date`, `gp_last_modified_by`, `gp_last_modified_date`) values (315,'admin',170,'1',null,null,null,null);
insert into `oh_grouppermission` (`gp_id`, `gp_ug_id_a`, `gp_p_id_a`, `gp_active`, `gp_created_by`, `gp_created_date`, `gp_last_modified_by`, `gp_last_modified_date`) values (316,'admin',171,'1',null,null,null,null);