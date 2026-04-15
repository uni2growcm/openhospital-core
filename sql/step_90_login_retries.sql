alter table oh_user add us_failed_attempts int default 0;
alter table oh_user add us_account_locked tinyint(1) not null default '0';
alter table oh_user add us_lock_time datetime null default null;