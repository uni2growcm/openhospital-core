alter table log change column log_tyme log_time datetime not null;
alter table log add column log_method varchar(64) null after log_class;
alter table log add column log_user varchar(50) null after log_mess;

alter table log change column log_mess log_mess varchar(1024) null default null;