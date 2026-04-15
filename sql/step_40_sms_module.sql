create table sms (
  sms_id int(11) not null auto_increment ,
  sms_date timeSTAMP not null default current_timestamp ,
  sms_date_sched datetime not null ,
  sms_number varchar(45) not null ,
  sms_text varchar(160) not null ,
  sms_date_sent datetime null ,
  sms_user varchar(50) not null default 'admin' ,
  primary key (sms_id) 
);

insert into menuitem values ('smsmanager', 'angal.menu.btn.smsmanager', 'angal.menu.smsmanager', 'x', 'M', 'generaldata', 'org.isf.sms.gui.SmsBrowser','N', 9);
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','smsmanager','Y');
