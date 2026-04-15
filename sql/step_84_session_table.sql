create table oh_session_audit (
  sea_id int(11) not null auto_increment,
  sea_us_id_a varchar(50) not null,
  sea_login datetime not null,
  sea_logout datetime null,
  sea_created_by varchar(50) null default null,
  sea_created_date datetime null default null,
  sea_last_modified_by varchar(50) null default null,
  sea_last_modified_date datetime null default null,
  sea_active tinyint(1) not null default 1,
  primary key (sea_id),
  foreign key (sea_us_id_a) references oh_user (us_id_a)
) engine = innodb default character set utf8;
