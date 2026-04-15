create table oh_permissions (
  p_id_a int not null auto_increment,
  p_name varchar(50) not null default '',
  p_description varchar(255) not null default '',
  p_active char(1) not null default '',
  p_created_by varchar(50) default null,
  p_created_date datetime default null,
  p_last_modified_by varchar(50) default null,
  p_last_modified_date datetime default null,
  primary key ( p_id_a )
) engine innodb default character set utf8;



insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('opd.read','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('opd.create','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('opd.update','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('opd.delete','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('summary.read','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('summary.create','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('summary.update','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('summary.delete','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('examination.read','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('examination.create','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('examination.update','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('examination.delete','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('admission.read','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('admission.create','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('admission.update','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('admission.delete','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('therapy.read','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('therapy.create','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('therapy.update','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('therapy.delete','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('vaccine.read','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('vaccine.create','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('vaccine.update','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('vaccine.delete','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('exam.read','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('exam.create','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('exam.update','',1,null,null,null,null);
insert into oh_permissions (p_name, p_description, p_active, p_created_by, p_created_date, p_last_modified_by, p_last_modified_date) values ('exam.delete','',1,null,null,null,null);

create table oh_grouppermission (
  gp_id int not null auto_increment,
  gp_ug_id_a varchar(50) not null default '',
  gp_p_id_a int not null,
  gp_active char(1) not null default '',
  gp_created_by varchar(50) default null,
  gp_created_date datetime default null,
  gp_last_modified_by varchar(50) default null,
  gp_last_modified_date datetime default null,
  primary key (gp_id)
) engine innodb default character set utf8;

insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',1,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',2,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',3,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',4,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',5,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',6,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',7,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',8,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',9,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',10,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',11,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',12,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',13,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',14,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',15,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',16,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',17,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',18,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',19,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',20,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',21,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',22,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',23,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',24,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',25,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',26,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',27,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('admin',28,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('guest',1,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('guest',5,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('guest',9,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('guest',13,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('guest',17,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('guest',21,1,null,null,null,null);
insert into oh_grouppermission (gp_ug_id_a, gp_p_id_a, gp_active, gp_created_by,  gp_created_date, gp_last_modified_by, gp_last_modified_date) values ('guest',25,1,null,null,null,null);
