-- add permission
insert into `oh_permissions` (`p_id_a`, `p_name`, `p_description`, `p_active`, `p_created_by`, `p_created_date`, `p_last_modified_by`, `p_last_modified_date`) values (167,'admin.access','','1',null,null,null,null);
-- add group permisson
insert into `oh_grouppermission` (`gp_id`, `gp_ug_id_a`, `gp_p_id_a`, `gp_active`, `gp_created_by`, `gp_created_date`, `gp_last_modified_by`, `gp_last_modified_date`) values (312,'admin',167,'1',null,null,null,null);