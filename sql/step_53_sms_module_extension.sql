alter table sms 
add column sms_mod varchar(45) not null default 'smsmanager' after sms_user,
add column sms_mod_id varchar(45) null default null after sms_mod;
